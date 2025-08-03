package com.ly.cloud.backup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jcraft.jsch.*;
import com.ly.cloud.auth.mapper.SysUserMapper;
import com.ly.cloud.auth.mapper.SystemUserMapper;
import com.ly.cloud.auth.po.SysUser;
import com.ly.cloud.backup.config.MinioConfig;
import com.ly.cloud.backup.dto.LyDbBackupStrategyRecordDto;
import com.ly.cloud.backup.exception.ServiceException;
import com.ly.cloud.backup.mapper.*;
import com.ly.cloud.backup.po.*;
import com.ly.cloud.backup.util.DeptIdUtil;
import com.ly.cloud.backup.util.ElasticUtil;
import com.ly.cloud.backup.util.ListUtil;
import com.ly.cloud.backup.util.MinioClientUtils;
import com.ly.cloud.backup.vo.*;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.cloud.backup.service.LyDbBackupHistoryRecordService;
import org.springframework.util.StreamUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
public class LyDbBackupHistoryRecordServiceImpl extends ServiceImpl<LyDbBackupHistoryRecordMapper, LyDbBackupHistoryRecordPo> implements LyDbBackupHistoryRecordService{
    private static final Logger logger = LoggerFactory.getLogger(LyDbBackupHistoryRecordServiceImpl.class);

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private MinioClientUtils minioClientUtils;

    @Autowired
    private LyDbBackupHistoryRecordMapper lyDbBackupHistoryRecordMapper;

    @Autowired
    private LyDbBackupStrategyRecordMapper lyDbBackupStrategyRecordMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Resource
    private DatabaseMapper databaseMapper;

    @Resource
    private SystemUserMapper systemUserMapper;

    @Resource
    private MiddlewareMapper middlewareMapper;

    @Resource
    private ServerMapper serverMapper;

    @Resource
    private BackupManagementMapper backupManagementMapper;

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm:ss");


    @Override
    public void downloadBackupFile(String id, HttpServletResponse response) {
        LyDbBackupHistoryRecordPo lyDbBackupHistoryRecordPo = lyDbBackupHistoryRecordMapper.selectById(id);
        BackupManagementPo backupManagementPo = null;
        if (lyDbBackupHistoryRecordPo.getBackupWay().equals("1")) {
            //数据库
            backupManagementPo = backupManagementMapper.selectById(lyDbBackupHistoryRecordPo.getBackupTarget());
        }else {
            //服务器
            LyDbBackupStrategyRecordPo strategyRecordPo = lyDbBackupStrategyRecordMapper.selectById(lyDbBackupHistoryRecordPo.getStrategyId());
            backupManagementPo = backupManagementMapper.selectById(strategyRecordPo.getLoginUrl());
        }

        Session session = null;
        ChannelSftp channelSftp = null;

        try {
            session = connectSession(backupManagementPo);
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            String remotePath = null;
//            mysql
            if (lyDbBackupHistoryRecordPo.getTotalMethod().equals("1")) {
                remotePath = "/data/backup/mysql/"+lyDbBackupHistoryRecordPo.getTimeStamp();
            }
            if (lyDbBackupHistoryRecordPo.getTotalMethod().equals("6")) {
                remotePath = "/data/backup/ssh/"+lyDbBackupHistoryRecordPo.getTimeStamp();
            }
            SftpATTRS attrs = channelSftp.lstat(remotePath); // 获取文件/目录属性

            if (attrs.isDir()) {
                // --- 处理目录: 压缩后下载 ---
                String zipFileName = Paths.get(remotePath).getFileName().toString() + ".zip";
                response.setContentType("application/zip");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + zipFileName + "\"");

                try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
                    addDirectoryToZip(channelSftp, remotePath, zos, "");
                } catch (IOException e) {
                    logger.error("Error creating zip stream for directory {}", remotePath, e);
                    // 尝试在 header 设置后发送错误可能导致问题，最好是能提前判断或记录错误
                    throw new RuntimeException("Error creating zip stream", e);
                }
            } else {
                // --- 处理文件: 直接下载 ---
                String fileName = Paths.get(remotePath).getFileName().toString();
                response.setContentType("application/octet-stream"); // 通用二进制类型
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                response.setContentLengthLong(attrs.getSize()); // 设置文件大小

                try (InputStream inputStream = channelSftp.get(remotePath)) {
                    StreamUtils.copy(inputStream, response.getOutputStream()); // Spring 的工具类简化流复制
                } catch (IOException e) {
                    logger.error("Error streaming file {} to response", remotePath, e);
                    throw new RuntimeException("Error streaming file", e);
                }
            }
            response.flushBuffer(); // 确保所有数据都被发送

        } catch (SftpException e) {
            throw new RuntimeException("SFTP error: " + e.getMessage(), e);
        } catch (JSchException e) {
            throw new RuntimeException("Connection error: " + e.getMessage(), e);
        } catch (IOException e) {
            // 主要捕获 response.getOutputStream() 的 IO 错误
            logger.error("IO error during download process", e);
            throw new RuntimeException();
        } finally {
            disconnectSftp(channelSftp, session);
        }
    }

    @Override
    public List<BackupDirectoryTreeVo> getTypeSourceWayList() {
        List<BackupDirectoryTreeVo> list = lyDbBackupHistoryRecordMapper.getTypeSourceWayList();
        return list;
    }

    @Override
    public IPage<LyDbBackupHistoryRecordPo> queryBackupHistoryList(Integer page, Integer pageSize, String title, String totalMethod, String value, String strategyId,String authDeptId) {
        Page<LyDbBackupHistoryRecordPo> page1 = new Page<>(page, pageSize);
        List<String> totalMethodList = new ArrayList<>();
        //对totalMethod 判断是否要切割 "2,3,1,4,,SQLServer,5,6,7,8,9";
        if (StringUtils.isNotEmpty(totalMethod)) {
            if (totalMethod.indexOf(",") > -1) {
                String[] split = totalMethod.split(",");
                totalMethodList = Arrays.asList(split);
            } else {
                totalMethodList.add(totalMethod);
            }
        }
        if (StringUtils.isNotEmpty(value) && !"-1".equals(value)) { //判断是否根据目录id查询
            LyDbBackupStrategyRecordDto lyDbBackupStrategyRecordDto=handleCondition(value);
            lyDbBackupStrategyRecordDto.setAuthDeptId(authDeptId);
            //根据目录id筛选出备份策略id
            List<String> list=lyDbBackupHistoryRecordMapper.selectBackupStrategyRecordListByCondition(lyDbBackupStrategyRecordDto);
            if (CollectionUtils.isNotEmpty(list)) {
                List<List<String>> list1=ListUtil.createList(list, 500);
                List<LyDbBackupHistoryRecordPo> recordPos=new ArrayList<>();
                Long total=0L;
                for (List<String> strings : list1) {
                    List<LyDbBackupHistoryRecordPo> list2=lyDbBackupHistoryRecordMapper.queryBackupHistoryList(title, totalMethodList, strings,authDeptId,strategyId);
                    recordPos.addAll(list2);
                    total+=list2.size();
                }
                if (CollectionUtils.isNotEmpty(recordPos)) {
                    recordPos=recordPos.stream().sorted(new Comparator<LyDbBackupHistoryRecordPo>() {
                        @Override
                        public int compare(LyDbBackupHistoryRecordPo o1, LyDbBackupHistoryRecordPo o2) {
                            if (o2.getBackupTime().getTime()
                                    - o1.getBackupTime().getTime() > 0) {
                                return 1;
                            } else {
                                return -1;
                            }
                        }
                    }).collect(Collectors.toList());
                    List<LyDbBackupHistoryRecordPo> collect=recordPos.stream().skip((page - 1L) * pageSize).limit(pageSize).collect(Collectors.toList());
                    for (int i = 0; i < collect.size(); i++) {
                        if (collect.get(i).getBackupStatus().equals("1") && collect.get(i).getSize()==null) {
                            LyDbBackupHistoryDetailsVo details = details(String.valueOf(collect.get(i).getId()));
                            collect.get(i).setSize(details.getSize());
                            lyDbBackupHistoryRecordMapper.updateById(collect.get(i));
                        }
                    }
                    page1.setRecords(collect);
                    page1.setTotal(total);
                }
            }
        } else {
            List<LyDbBackupHistoryRecordPo> list2=lyDbBackupHistoryRecordMapper.queryBackupHistoryList(title, totalMethodList, null,authDeptId, strategyId);
            List<LyDbBackupHistoryRecordPo> collect=list2.stream().skip((page - 1L) * pageSize).limit(pageSize).collect(Collectors.toList());
            //请求detail接口获取备份文件大小，只有备份成功状态并且数据库size为空才去请求
//            for (int i = 0; i < collect.size(); i++) {
//                if (collect.get(i).getBackupStatus().equals("1") && collect.get(i).getSize()==null) {
//                    LyDbBackupHistoryDetailsVo details = details(String.valueOf(collect.get(i).getId()));
//                    collect.get(i).setSize(details.getSize());
//                    lyDbBackupHistoryRecordMapper.updateById(collect.get(i));
//                }
//            }
            page1.setRecords(collect);
            page1.setTotal(list2.size());
        }
        return page1;
    }

    @Override
    public IPage<LyDbBackupHistoryRecordPo> get(Integer page, Integer pageSize, String id, String title, String authDeptId) {
        Page<LyDbBackupHistoryRecordPo> page1 = new Page<>(page, pageSize);
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        List<String> list = new ArrayList<>();
        list.add(id);
        IPage<LyDbBackupHistoryRecordPo> iPage1 = lyDbBackupHistoryRecordMapper.queryBackupHistoryListPage(page1, title, list,authDeptId);
        if (iPage1 != null && CollectionUtils.isNotEmpty(iPage1.getRecords())) {
            for (LyDbBackupHistoryRecordPo record : iPage1.getRecords()) {
                LyDbBackupStrategyRecordPo lyDbBackupStrategyRecordPo = lyDbBackupStrategyRecordMapper.selectById(record.getStrategyId());
                record.setTimeStamp(handleFileName(record,null,lyDbBackupStrategyRecordPo.getTitle()));
            }
        }
        iPage1.getRecords().forEach(u->u.setAuthDeptIds(DeptIdUtil.getAuthDeptIds(u.getAuthDeptId())));
        return iPage1;
    }

    /*根据历史备份记录id查询备份详情*/
    @Override
    public LyDbBackupHistoryDetailsVo details(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        LyDbBackupHistoryDetailsVo vo = new LyDbBackupHistoryDetailsVo();
        //LyDbBackupHistoryRecordPo historyRecordPo = lyDbBackupHistoryRecordMapper.selectHistoryRecordById(id);
        LyDbBackupHistoryRecordPo historyRecordPo = lyDbBackupHistoryRecordMapper.selectById(id);
        if (historyRecordPo != null) {
            //LyDbBackupStrategyRecordPo lyDbBackupStrategyRecordPo = lyDbBackupStrategyRecordMapper.selectById(historyRecordPo.getStrategyId());
            String filename = historyRecordPo.getTimeStamp();
            BeanUtils.copyProperties(historyRecordPo,vo);
            //设置备份状态
            vo.setBackupStatus(historyRecordPo.getBackupStatus());
            //设置执行时间
            vo.setRunTime(historyRecordPo.getRunTime());

            vo.setTimeStamp(handleFileName(historyRecordPo,vo,historyRecordPo.getTitle()));

            vo.setBackupStrategyType(historyRecordPo.getBackupStrategyType());
            vo.setDataSourceType(historyRecordPo.getDataSourceType());
            vo.setBackupWay(historyRecordPo.getBackupWay());
            vo.setBackupMethod(historyRecordPo.getBackupMethod());
            vo.setTotalMethod(historyRecordPo.getTotalMethod());

            //Map<String, Object> map = new HashMap<>();
            //map.put("id",historyRecordPo.getStrategyId());
            //List<LyDbBackupStrategyRecordPo> lyDbBackupStrategyRecordPos = lyDbBackupStrategyRecordMapper.selectByMap(map);

            //设置数据连接
            String url=historyRecordPo.getUrl();
            if (StringUtils.isNotEmpty(url)) {
                List<String> databaseIdList = new ArrayList<>();
                if (url.indexOf(";") > -1) {
                    databaseIdList=Arrays.stream(url.split(";")).collect(Collectors.toList());
                } else {
                    databaseIdList.add(url);
                }
                vo.setUrl(databaseIdList);
                    /*if (CollectionUtils.isNotEmpty(databaseIdList)) {
                        //TODO 此次设置数据库连接id为参考备份策略。后续新增备份策略数据库连接下拉框修改时，这里也要相应的修改
                        if ("1".equals(strategyRecordPo.getBackupWay())) {
                            if (!"6".equals(strategyRecordPo.getDataSourceType())) {
                                List<DatabasePo> databasePos = databaseMapper.selectBatchIds(databaseIdList);
                                if (CollectionUtils.isNotEmpty(databasePos)) {
                                    List<String> list = new ArrayList<>();
                                    for (DatabasePo databasePo : databasePos) {
                                        list.add(databasePo.getUrl());
                                    }
                                    vo.setUrl(list);
                                }
                            } else {
                                List<MiddlewarePo> middlewarePos = middlewareMapper.selectBatchIds(databaseIdList);
                                if (CollectionUtils.isNotEmpty(middlewarePos)) {
                                    String mongo = "mongodb://";
                                    List<String> list = new ArrayList<>();
                                    for (MiddlewarePo middlewarePo : middlewarePos) {
                                        list.add(mongo + middlewarePo.getUser() + ":" + middlewarePo.getPassword() + "@" + middlewarePo.getIp() + ":" + middlewarePo.getPort());
                                    }
                                    vo.setUrl(list);
                                }
                            }
                        } else if ("2".equals(strategyRecordPo.getBackupWay())) {
                            List<ServerPo> serverPos = serverMapper.selectBatchIds(databaseIdList);
                            if (CollectionUtils.isNotEmpty(serverPos)) {
                                List<String> list = new ArrayList<>();
                                for (ServerPo serverPo : serverPos) {
                                    list.add(serverPo.getIpv4());
                                }
                                vo.setUrl(list);
                            }
                        }
                    }*/
            }
            vo.setBackupWay(historyRecordPo.getBackupWay());
            vo.setDataSourceType(historyRecordPo.getDataSourceType());
            vo.setTotalMethod(historyRecordPo.getTotalMethod());
            vo.setTaskMode(historyRecordPo.getTaskMode());
            vo.setOperatingCycle(historyRecordPo.getOperatingCycle());
            vo.setBackupMethod(historyRecordPo.getBackupMethod());
            if (StringUtils.isNotEmpty(historyRecordPo.getBackupTarget())) {
                List<String> collect = Arrays.stream(historyRecordPo.getBackupTarget().split(";")).collect(Collectors.toList());
                vo.setBackupTarget(collect);
            }


            if (StringUtils.isNotEmpty(filename)) {
                Iterable<Result<Item>> results=minioClientUtils.listObjects(minioConfig.getBucketName(), filename, false);
                Iterator<Result<Item>> iterator=results.iterator();
                //由于上传的文件名是唯一的，所以只获取第一个文件信息
                if(iterator.hasNext()) {
                    Result<Item> next=iterator.next();
                    try {
                        if (!historyRecordPo.getBackupStatus().equals("3") && !historyRecordPo.getBackupStatus().equals("2")) {
                            Item item=next.get();
                            vo.setSize(ElasticUtil.getSize(item.size()));
                        }
                    } catch (ErrorResponseException e) {
                        e.printStackTrace();
                    } catch (InsufficientDataException e) {
                        e.printStackTrace();
                    } catch (InternalException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (InvalidResponseException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (ServerException e) {
                        e.printStackTrace();
                    } catch (XmlParserException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return vo;
    }

    @Override
    public LyDbBackupLevitatedSphereVo levitatedSphereDetails() {
        LyDbBackupLevitatedSphereVo vo = new LyDbBackupLevitatedSphereVo();
        //备份状态（枚举）（0正在备份，1备份成功，2备份失败，3已停止备份）
        Date date = new Date();
        String format = simpleDateFormat1.format(date);
        QueryWrapper<LyDbBackupHistoryRecordPo> wrapper = new QueryWrapper<>();
        wrapper.like("backup_time",format);
        wrapper.orderByDesc("backup_time");
        List<LyDbBackupHistoryRecordPo> pos = lyDbBackupHistoryRecordMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(pos)) {
            Map<String, LyDbBackupStrategyRecordPo> strategyRecordMap = new HashMap<>();
            List<String> strategyIdList = pos.stream().map(p -> p.getStrategyId()).distinct().collect(Collectors.toList());
            List<List<String>> listList = ListUtil.createList(strategyIdList, 500);
            List<LyDbBackupStrategyRecordPo> poList = new ArrayList<>();
            for (List<String> list : listList) {
                List<LyDbBackupStrategyRecordPo> lyDbBackupStrategyRecordPos = lyDbBackupStrategyRecordMapper.selectBatchIds(list);
                poList.addAll(lyDbBackupStrategyRecordPos);
            }
            if (CollectionUtils.isNotEmpty(poList)) { //p.getTotalMethod()
                strategyRecordMap = poList.stream().collect(Collectors.toMap(p -> p.getId().toString(), p -> p));
            }
            Map<String, LyDbBackupStrategyRecordPo> finalMap = strategyRecordMap;
            List<LyDbBackupListVo> listVos = pos.stream().map(d -> {
                LyDbBackupListVo lyDbBackupListVo=new LyDbBackupListVo();
                BeanUtils.copyProperties(d, lyDbBackupListVo);
                if (finalMap.size()>0) {
                    LyDbBackupStrategyRecordPo po = finalMap.get(d.getStrategyId());
                    if (Optional.ofNullable(po).isPresent()) {
                        lyDbBackupListVo.setTotalMethod(po.getTotalMethod());
//                        lyDbBackupListVo.setBackupMethod(po.getBackupMethod());
//                        lyDbBackupListVo.setBackupTarget(po.getBackupTarget());
                    }
                }
                //没有找到策略，将策略id设置为空，给前端用于判断对应的策略是否被删掉
                if (CollectionUtils.isEmpty(finalMap) || !Optional.ofNullable(finalMap.get(d.getStrategyId())).isPresent() || StringUtils.isEmpty(finalMap.get(d.getStrategyId()).getTotalMethod())) {
                    lyDbBackupListVo.setStrategyId(null);
                }
                return lyDbBackupListVo;
            }).collect(Collectors.toList());
            vo.setBackupList(listVos);
            vo.setTotal(pos.size());
            Map<String, List<LyDbBackupHistoryRecordPo>> map = pos.stream().collect(Collectors.groupingBy(d -> d.getBackupStatus()));
            List<LyDbBackupHistoryRecordPo> list = map.get("0");
            int size = CollectionUtils.isEmpty(list)? 0 : list.size();
            vo.setEndTotal(pos.size()-size);

            Double proportion =getRate(pos.size(), vo.getEndTotal());
            vo.setProportion(proportion);
            vo.setBackupStatus("0");
            if ("100.0".equals(proportion)) {
                vo.setBackupStatus("1");
            }
            vo.setSuccessTotal(CollectionUtils.isEmpty(map.get("1"))? 0 : map.get("1").size());
            vo.setFailTotal(CollectionUtils.isEmpty(map.get("2"))? 0 : map.get("2").size());
            vo.setProcessTotal(CollectionUtils.isEmpty(map.get("0"))? 0 : map.get("0").size());
            vo.setStopTotal(CollectionUtils.isEmpty(map.get("3"))? 0 : map.get("3").size());
            //TODO 没有cdc备份，先设置为0
            vo.setNumberOfTables(0);
            vo.setNumberOfFields(0);
            vo.setDataCapacity(0);
        }
        return vo;
    }

    /**
     * 计算百分比，保留两位小数，只考虑分子，分母都大于0的情况
     * @param total
     * @param endTotal
     * @return
     */
    public Double getRate(long total, long endTotal) {

        double value = endTotal * 1.0 / total;
        //四舍五入，保留小数点后4位
        String errorRate4f = ElasticUtil.getRound(value, 4);
        BigDecimal bigdecimal = new BigDecimal("100.0").multiply(new BigDecimal(errorRate4f));
        return bigdecimal.doubleValue();
    }

    public String handleFileName (LyDbBackupHistoryRecordPo historyRecordPo, LyDbBackupHistoryDetailsVo vo, String strategyName) {
        String userName = "";
        String format = "";
        String serialNumber = "";
        String suffix = "";
        if (historyRecordPo == null) {
            return null;
        }
        String operatorId = historyRecordPo.getOperatorId();
        if (StringUtils.isNotEmpty(operatorId)) {
            SysUser sysUser = sysUserMapper.selectUserById(Long.parseLong(operatorId));
            if (sysUser != null) {
                userName = sysUser.getUserName();
                if (vo != null) {
                    vo.setOperatorName(userName);
                }
            }
        }
        String format1=simpleDateFormat1.format(historyRecordPo.getBackupTime());
        format=simpleDateFormat.format(historyRecordPo.getBackupTime());
        QueryWrapper<LyDbBackupHistoryRecordPo> wrapper = new QueryWrapper<>();
        wrapper.eq("strategy_id", historyRecordPo.getStrategyId());
        wrapper.like("backup_time",format1);
        wrapper.orderByAsc("backup_time");
        List<LyDbBackupHistoryRecordPo> pos = lyDbBackupHistoryRecordMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(pos)) {
            for (int i=0; i < pos.size(); i++) {
                LyDbBackupHistoryRecordPo po = pos.get(i);
                if (historyRecordPo.getId().equals(po.getId())) {
                    serialNumber = String.format("%02d", i+1);
                    break;
                }
            }
        }
        String timeStamp = historyRecordPo.getTimeStamp();
        if (StringUtils.isNotEmpty(timeStamp) && timeStamp.indexOf(".") > -1) {
            suffix = timeStamp.substring(timeStamp.indexOf("."));
        }
        return strategyName+"_"+userName + "_" + format + serialNumber + suffix;
    }


    public LyDbBackupStrategyRecordDto handleCondition(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        List<LyDbBackupStrategyDirectoryPo> list = lyDbBackupHistoryRecordMapper.selectBackupHistoryList();
        if (CollectionUtils.isNotEmpty(list)) {
            Map<String, LyDbBackupStrategyDirectoryPo> map = list.stream().collect(Collectors.toMap(po -> po.getId().toString(), p -> p));
            LyDbBackupStrategyDirectoryPo po = map.get(value);
            LyDbBackupStrategyRecordDto dto = new LyDbBackupStrategyRecordDto();
            if (po != null) {
                Long parentId = po.getParentId();
                String type = po.getType();
                if ("1".equals(type)) {
                    dto.setBackupWay(po.getDirectoryId());
                }
                if ("2".equals(type)) {
                    dto.setDataSourceType(po.getDirectoryId());
                }
                if ("3".equals(type)) {
                    dto.setTotalMethod(po.getDirectoryId());
                }
                while (parentId != null && !"-1".equals(parentId.toString())) {
                    LyDbBackupStrategyDirectoryPo lyDbBackupStrategyDirectoryPo = map.get(parentId.toString());
                    if (lyDbBackupStrategyDirectoryPo!=null) {
                        String directoryId = lyDbBackupStrategyDirectoryPo.getDirectoryId();
                        String type1 = lyDbBackupStrategyDirectoryPo.getType();
                        parentId=lyDbBackupStrategyDirectoryPo.getParentId();
                        if ("1".equals(type1)) {
                            dto.setBackupWay(directoryId);
                        }
                        if ("2".equals(type1)) {
                            dto.setDataSourceType(directoryId);
                        }
                        if ("3".equals(type1)) {
                            dto.setTotalMethod(directoryId);
                        }
                    } else {
                        break;
                    }
                }
            }
            return dto;
        }
        return null;
    }




    // --- 辅助方法 ---

    /**
     * 建立 JSch Session 连接
     */
    private Session connectSession(BackupManagementPo backupManagementPo) throws JSchException {
        JSch jsch = new JSch();
        Session session;

        try {
            // 如果提供了私钥路径，优先使用密钥认证
//            if (serverInfo.getPrivateKeyPath() != null && !serverInfo.getPrivateKeyPath().isEmpty()) {
//                String keyPath = serverInfo.getPrivateKeyPath();
//                String passphrase = serverInfo.getPrivateKeyPassphrase();
//                if (passphrase != null && !passphrase.isEmpty()) {
//                    jsch.addIdentity(keyPath, passphrase);
//                } else {
//                    jsch.addIdentity(keyPath);
//                }
//                log.info("Using private key for authentication: {}", keyPath);
//            }

            session = jsch.getSession(backupManagementPo.getUser(), backupManagementPo.getIpv4(), Integer.parseInt(backupManagementPo.getPort()));

            // 如果没有使用密钥，则使用密码认证
            if (backupManagementPo.getPassword() == null || backupManagementPo.getPassword().isEmpty()) {
                throw new JSchException("Authentication method required: either password or private key must be provided.");
            }
            session.setPassword(backupManagementPo.getPassword());

            // 重要：禁用严格的主机密钥检查（生产环境应考虑导入 known_hosts）
            session.setConfig("StrictHostKeyChecking", "no");
            // 可以设置连接超时
            // session.setTimeout(60000); // 60秒

            session.connect();
            logger.info("Session connected to {}", backupManagementPo.getIpv4());
            return session;
        } catch (JSchException e) {
            logger.error("Failed to connect session to {} with user {}", backupManagementPo.getIpv4(), backupManagementPo.getUser(), e);
            throw e;
        }
    }

    /**
     * 断开 SFTP Channel 和 Session
     */
    private void disconnectSftp(ChannelSftp channelSftp, Session session) {
        if (channelSftp != null && channelSftp.isConnected()) {
            channelSftp.disconnect();
            logger.info("SFTP channel disconnected.");
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
            logger.info("Session disconnected.");
        }
    }

    /**
     * 递归地将远程目录添加到 ZipOutputStream
     * @param channelSftp SFTP channel
     * @param remoteDirPath 远程目录的绝对路径
     * @param zos ZipOutputStream
     * @param basePathInZip Zip 文件内的相对路径前缀
     */
    private void addDirectoryToZip(ChannelSftp channelSftp, String remoteDirPath, ZipOutputStream zos, String basePathInZip) throws SftpException, IOException {
        // 确保基础路径以 '/' 结尾，方便拼接
        if (!basePathInZip.isEmpty() && !basePathInZip.endsWith("/")) {
            basePathInZip += "/";
        }

        @SuppressWarnings("unchecked") // JSch ls 返回原始 Vector
        Vector<ChannelSftp.LsEntry> entries = channelSftp.ls(remoteDirPath);

        if (entries.isEmpty() && !basePathInZip.isEmpty()) {
            // 处理空目录，创建一个条目
            ZipEntry zipEntry = new ZipEntry(basePathInZip); // basePathInZip 就是目录名
            zos.putNextEntry(zipEntry);
            zos.closeEntry();
            logger.debug("Added empty directory entry to zip: {}", basePathInZip);
            return; // 空目录处理完毕
        }


        for (ChannelSftp.LsEntry entry : entries) {
            String entryName = entry.getFilename();
            // 跳过 "." 和 ".." 目录
            if (entryName.equals(".") || entryName.equals("..")) {
                continue;
            }

            String fullRemotePath = combineUnixPaths(remoteDirPath, entryName);
            String entryPathInZip = basePathInZip + entryName;

            if (entry.getAttrs().isDir()) {
                // 如果是目录，递归调用
                logger.debug("Adding directory to zip: {}", entryPathInZip);
                // 为目录本身创建一个条目（以 / 结尾）
                ZipEntry dirEntry = new ZipEntry(entryPathInZip + "/");
                zos.putNextEntry(dirEntry);
                zos.closeEntry();
                addDirectoryToZip(channelSftp, fullRemotePath, zos, entryPathInZip);
            } else {
                // 如果是文件，添加到 Zip
                logger.debug("Adding file to zip: {}", entryPathInZip);
                ZipEntry zipEntry = new ZipEntry(entryPathInZip);
                // 可选：设置文件时间等属性
                // zipEntry.setTime(entry.getAttrs().getMTime() * 1000L);
                zos.putNextEntry(zipEntry);
                try (InputStream fis = channelSftp.get(fullRemotePath)) {
                    StreamUtils.copy(fis, zos); // 从 SFTP 流复制到 Zip 流
                }
                zos.closeEntry(); // 完成当前文件的写入
            }
        }
    }

    /**
     * 确保远程目录存在，如果不存在则尝试创建 (类似 mkdir -p)
     */
    private void ensureRemoteDirectoryExists(ChannelSftp channel, String remoteDirPath) throws SftpException {
        String[] folders = remoteDirPath.split("/");
        String currentPath = "";
        if (remoteDirPath.startsWith("/")) {
            currentPath = "/"; // 处理根目录开头的路径
        }

        for (String folder : folders) {
            if (folder.isEmpty()) continue;
            currentPath = combineUnixPaths(currentPath, folder);
            try {
                SftpATTRS attrs = channel.lstat(currentPath);
                if (!attrs.isDir()) {
                    throw new RuntimeException();
                }
            } catch (SftpException e) {
                if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                    // 目录不存在，创建它
                    logger.info("Remote directory {} does not exist, creating...", currentPath);
                    channel.mkdir(currentPath);
                } else {
                    // 其他错误
                    throw e;
                }
            }
        }
    }

    /**
     * 安全地组合两个 Unix 路径部分
     */
    private String combineUnixPaths(String part1, String part2) {
        String path1 = part1.endsWith("/") ? part1.substring(0, part1.length() - 1) : part1;
        String path2 = part2.startsWith("/") ? part2.substring(1) : part2;
        if (path1.isEmpty()) {
            return path2.isEmpty() ? "/" : "/" + path2; // 处理根目录或只有第二部分的情况
        }
        if (path2.isEmpty()) {
            return path1.isEmpty() ? "/" : path1; // 处理只有第一部分的情况
        }

        // 确保最终结果以 / 开头（如果是根目录）或不以 / 开头（如果part1不是根）
        String combined = path1 + "/" + path2;
        if (!part1.equals("/") && combined.startsWith("//")) {
            return combined.substring(1); // 防止双斜杠，除非是根目录
        }
        return combined;

    }


}
