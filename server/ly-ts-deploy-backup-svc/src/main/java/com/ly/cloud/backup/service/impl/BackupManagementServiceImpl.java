package com.ly.cloud.backup.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.cloud.backup.common.annotation.Sm4DecryptMethod;
import com.ly.cloud.backup.common.enums.DataBaseEnums;
import com.ly.cloud.backup.config.MinioConfig;
import com.ly.cloud.backup.dto.*;
import com.ly.cloud.backup.mapper.BackupManagementMapper;
import com.ly.cloud.backup.mapper.LyDbBackupHistoryRecordMapper;
import com.ly.cloud.backup.mapper.ServerMapper;
import com.ly.cloud.backup.po.BackupManagementPo;
import com.ly.cloud.backup.po.LyDbBackupHistoryRecordPo;
import com.ly.cloud.backup.po.ServerPo;
import com.ly.cloud.backup.service.BackupManagementService;
import com.ly.cloud.backup.util.*;
import com.ly.cloud.backup.vo.BackupManagementVo;
import com.ly.cloud.backup.vo.ServerVo;
import com.ly.cloud.backup.vo.StrategicModeVo;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class BackupManagementServiceImpl extends ServiceImpl<BackupManagementMapper, BackupManagementPo> implements BackupManagementService {

    private static final Logger logger = LoggerFactory.getLogger(BackupManagementServiceImpl.class);

    public static boolean backupClearFlag = false;

    @Autowired
    private ServerMapper serverMapper;

    @Autowired
    private BackupManagementMapper backupManagementMapper;


    @Autowired
    private LyDbBackupHistoryRecordMapper lyDbBackupHistoryRecordMapper;

    @Autowired
    private MinioClientUtils minioClientUtils;

    @Autowired
    private MinioConfig minioConfig;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<StrategicModeVo> findClientList(String totalMethod) {
        List<ServerPo> serverPos = serverMapper.selectList(null);
        List<ServerPo> serverPoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(serverPos)) {
            if ("2".equals(totalMethod)) {
                serverPoList = serverPos.stream().filter(serverPo -> !StringUtils.isEmpty(serverPo.getOracleUser()) && !StringUtils.isEmpty(serverPo.getOraclePassword())).collect(Collectors.toList());
            } else {
                serverPoList.addAll(serverPos);
            }
            if (CollectionUtils.isNotEmpty(serverPoList)) {
                List<StrategicModeVo> list=serverPoList.stream().map(s -> {
                    StrategicModeVo vo=new StrategicModeVo();
                    vo.setValue(s.getId().toString());
                    vo.setLabel(s.getIpv4());
                    return vo;
                }).collect(Collectors.toList());
                return list;
            }
        }
        return null;
    }

    @Override
    public List<BackupManagementPo> getBackupManagementList(String backupWay, String authDeptId) {
//        List<BackupManagementVo> li= backupManagementMapper.selectBackupManagementByBackupWayList(backupWay,authDeptId);
//        li.forEach(u->u.setAuthDeptIds(DeptIdUtil.getAuthDeptIds(u.getAuthDeptId())));
//        return li;
        List<BackupManagementPo> backupManagementPos = backupManagementMapper.selectList(new LambdaQueryWrapper<>());
        return backupManagementPos;
    }

    @Override
    public void updateBackupManagement(BackupManagementDto backupManagementDto) {
        BackupManagementPo po = new BackupManagementPo();
        BeanUtils.copyProperties(backupManagementDto, po);
        backupManagementMapper.updateById(po);
    }

    /**
     * 按备份管理规则进行清理备份文件跟备份记录
     */
    @SneakyThrows
    @Override
    public void clearBackupFile() {
        try {
            Date date1 = new Date();
            String format = simpleDateFormat1.format(date1);
            logger.info(format+" 历史备份管理开始执行调度......");
            BackupManagementServiceImpl.backupClearFlag = true;
            List<Long> deleteBackupHistoryIds = new ArrayList<>();
            List<String> deleteBackupHistoryFileNames = new ArrayList<>();
            //查找出备份规则不是永久的备份规则
            QueryWrapper<BackupManagementPo> wrapper = new QueryWrapper<>();
            wrapper.ne("record_retention_time",0);
            List<BackupManagementPo> backupManagementList = backupManagementMapper.selectList(wrapper);
            if (CollectionUtils.isNotEmpty(backupManagementList)) {
                //查找出所有的备份策略记录
                List<LyDbBackupHistoryRecordPo> backupHistoryRecordList = lyDbBackupHistoryRecordMapper.selectList(null);
                if (CollectionUtils.isNotEmpty(backupHistoryRecordList)) {
                    //整理备份管理信息，计算出每种备份管理对应的时间分割点 和 保留的版本如:
                    Map<BackupManagementKeyDto, BackupManagementValueDto> backupManagementMap = backupManagementList.stream().collect(Collectors.toMap(b -> {
                        BackupManagementKeyDto keyDto=new BackupManagementKeyDto();
                        keyDto.setBackupWay(b.getBackupWay());
                        keyDto.setDataSourceType(b.getDataSourceType());
                        keyDto.setTotalMethod(b.getTotalMethod());
                        return keyDto;
                    }, b -> {
                        BackupManagementValueDto valueDto = new BackupManagementValueDto();
                        valueDto.setNum(b.getVersionRetentionNumber());
                        //计算时间分割点
                        Integer retentionTime = b.getRecordRetentionTime();
                        LocalDate localDate = LocalDate.now().minusDays(retentionTime);
                        valueDto.setDate(localDate.toString());
                        return valueDto;
                    }));
                    //整理出策略信息
                    Map<BackupManagementKeyDto, List<Long>> backupHistoryRecordMap = new HashMap<>();
                    backupHistoryRecordList = backupHistoryRecordList.stream().filter(h->"100".equals(h.getProportion())).collect(Collectors.toList());
                    for (LyDbBackupHistoryRecordPo po : backupHistoryRecordList) {
                        BackupManagementKeyDto keyDto=new BackupManagementKeyDto();
                        keyDto.setBackupWay(po.getBackupWay());
                        keyDto.setDataSourceType(po.getDataSourceType());
                        keyDto.setTotalMethod(po.getTotalMethod());
                        List<Long> list = backupHistoryRecordMap.get(keyDto);
                        if (CollectionUtils.isEmpty(list)) {
                            list = new ArrayList<>();
                        }
                        list.add(Long.parseLong(po.getStrategyId()));
                        backupHistoryRecordMap.put(keyDto,list);
                    }
                    List<Long> strategyIdList = new ArrayList<>();
                    Map<String, BackupManagementValueDto> hashMap = new HashMap<>();
                    Set<BackupManagementKeyDto> set = backupManagementMap.keySet();
                    for (BackupManagementKeyDto backupManagementKeyDto : set) {
                        List<Long> ids = backupHistoryRecordMap.get(backupManagementKeyDto);
                        if (CollectionUtils.isNotEmpty(ids)) {
                            ids = ids.stream().distinct().collect(Collectors.toList());
                            for (Long id : ids) {
                                BackupManagementValueDto valueDto = backupManagementMap.get(backupManagementKeyDto);
                                strategyIdList.add(id);
                                hashMap.put(id.toString(),valueDto);
                            }
                        }
                    }
                    if (CollectionUtils.isNotEmpty(backupHistoryRecordList)) {
                        if (CollectionUtils.isNotEmpty(backupHistoryRecordList)) {
                            Map<String, List<LyDbBackupHistoryRecordPo>> map = backupHistoryRecordList.stream().collect(Collectors.groupingBy(h -> h.getStrategyId()));
                            logger.debug("历史备份管理->对筛选出的备份历史记录按策略id进行分组，分组结果为:" + map);
                            for (String s : map.keySet()) {
                                logger.debug("历史备份管理->对策略id:"+s+"对应的历史记录按备份规则帅选处理开始：start");
                                List<LyDbBackupHistoryRecordPo> valueList = map.get(s);
                                logger.debug("历史备份管理->该策略id对应的备份历史记录为："+valueList);
                                BackupManagementValueDto dto = hashMap.get(s);
                                logger.debug("历史备份管理->该策略id对应的备份规则为："+dto);
                                if (dto != null && CollectionUtils.isNotEmpty(valueList)) {
                                    String date = dto.getDate();
                                    Date parse = simpleDateFormat.parse(date);
                                    List<Long> sortIdList = new ArrayList<>();
                                    Map<String, List<LyDbBackupHistoryRecordPo>> listMap = valueList.stream().filter(p -> p.getOperationTime() != null && p.getOperationTime().getTime() >= parse.getTime()).map(h-> {
                                        //防止url字段为空
                                        if (StringUtils.isEmpty(h.getUrl()) ) {
                                            h.setUrl("null");
                                        }
                                        return h;
                                    }).collect(Collectors.groupingBy(h -> h.getUrl()));
                                    for (String s1 : listMap.keySet()) {
                                        List<LyDbBackupHistoryRecordPo> lyDbBackupHistoryRecordPos = listMap.get(s1);
                                        if (CollectionUtils.isNotEmpty(lyDbBackupHistoryRecordPos)) {
                                            List<Long> collect = lyDbBackupHistoryRecordPos.stream().sorted(new Comparator<LyDbBackupHistoryRecordPo>() {
                                                @Override //这里上面规定了进度为100 时是一定不为空的。所有这里不做判空处理
                                                public int compare(LyDbBackupHistoryRecordPo o1, LyDbBackupHistoryRecordPo o2) {
                                                    if (o2.getOperationTime().getTime()
                                                            - o1.getOperationTime().getTime() > 0) {
                                                        return 1;
                                                    } else {
                                                        return -1;
                                                    }
                                                }
                                            }).limit(dto.getNum()).map(p -> p.getId()).collect(Collectors.toList());
                                            sortIdList.addAll(collect);
                                        }
                                    }
                                    /*List<Long> sortIdList = valueList.stream().filter(p->p.getOperationTime() != null && p.getOperationTime().getTime() >= parse.getTime()).sorted(new Comparator<LyDbBackupHistoryRecordPo>() {
                                        @Override //这里上面规定了进度为100 时是一定不为空的。所有这里不做判空处理
                                        public int compare(LyDbBackupHistoryRecordPo o1, LyDbBackupHistoryRecordPo o2) {
                                            if (o2.getOperationTime().getTime()
                                                    - o1.getOperationTime().getTime() > 0) {
                                                return 1;
                                            } else {
                                                return -1;
                                            }
                                        }
                                    }).limit(dto.getNum()).map(p->p.getId()).collect(Collectors.toList());*/
                                    logger.debug("历史备份管理->经过备份规则筛选后符合在规则内(即要保留的历史记录)的备份历史记录id为:"+sortIdList);
                                    List<LyDbBackupHistoryRecordPo> deleteBackupHistory = new ArrayList<>();
                                    if (CollectionUtils.isNotEmpty(sortIdList)) {
                                        //筛选出要删除的备份历史记录
                                        deleteBackupHistory = valueList.stream().filter(v -> !sortIdList.contains(v.getId())).collect(Collectors.toList());
                                    } else {
                                        deleteBackupHistory = valueList;
                                    }
                                    logger.debug("历史备份管理->经过备份规则筛选后符合在规则外(即要删除的备份历史记录)的备份历史记录为:"+deleteBackupHistory);
                                    if (CollectionUtils.isNotEmpty(deleteBackupHistory)) {
                                        List<Long> idList = deleteBackupHistory.stream().map(p -> p.getId()).collect(Collectors.toList());
                                        List<String> fileNameList = deleteBackupHistory.stream().map(p -> p.getTimeStamp()).collect(Collectors.toList());
                                        deleteBackupHistoryIds.addAll(idList);
                                        deleteBackupHistoryFileNames.addAll(fileNameList);
                                    }
                                }
                                logger.debug("历史备份管理->对策略id:"+s+"对应的历史记录按备份规则帅选处理结束：end");
                            }
                        }
                    }
                }
            }
            logger.debug("历史备份管理->经过备份规则筛选后符合在规则外(即要删除的备份历史记录)的备份历史记录id为:"+deleteBackupHistoryIds);
            logger.debug("历史备份管理->经过备份规则筛选后符合在规则外(即要删除的备份历史记录)的备份历史记录备份文件为:"+deleteBackupHistoryFileNames);
            //删除历史记录
            if (CollectionUtils.isNotEmpty(deleteBackupHistoryIds)) {
                List<List<Long>> listList=ListUtil.createList(deleteBackupHistoryIds, 500);
                for (List<Long> list : listList) {
                    lyDbBackupHistoryRecordMapper.deleteBatchIds(list);
                }
            }
            //删除minio 上的备份文件
            if (CollectionUtils.isNotEmpty(deleteBackupHistoryFileNames)) {
                List<String> collect = deleteBackupHistoryFileNames.stream().filter(f -> StringUtils.isNotEmpty(f)).collect(Collectors.toList());
                minioClientUtils.removeFiles(minioConfig.getBucketName(),collect);
            }
            Date date2 = new Date();
            String format1 = simpleDateFormat1.format(date2);
            logger.info(format1+" 历史备份管理执行调度结束......");
        } catch (BeansException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            BackupManagementServiceImpl.backupClearFlag = false;
        }
    }

    @Override
    public String Checkcorrect(Long serverId, String dataSourceType) {
        if (!Optional.ofNullable(serverId).isPresent() || StringUtils.isEmpty(dataSourceType)) {
            return null;
        }
        ServerVo serverVo = serverMapper.selectById(serverId);
        if (Optional.ofNullable(serverVo).isPresent()) {
            if (DataBaseEnums.ORACLE.getCode().toString().equals(dataSourceType)) {
                String oracleUser = serverVo.getOracleUser();
                if (StringUtils.isEmpty(oracleUser)) {
                    return "oracle用户不能为空! 请到服务器管理,设置对应的服务器oracle用户值";
                }
                String oraclePassword = serverVo.getOraclePassword();
                if (StringUtils.isEmpty(oraclePassword)) {
                    return "oracle密码不能为空! 请到服务器管理,设置对应的服务器oracle密码值";
                }
                String oracleBackupPath = serverVo.getOracleBackupPath();
                if (StringUtils.isEmpty(oracleBackupPath)) {
                    return "oracle备份路径不能为空! 请到服务器管理,设置对应的服务器oracle备份路径值";
                }
            }
            if (DataBaseEnums.MYSQL.getCode().toString().equals(dataSourceType)) {
                String mysqlBackupPath = serverVo.getMysqlBackupPath();
                if (StringUtils.isEmpty(mysqlBackupPath)) {
                    return "mysql备份路径不能为空! 请到服务器管理,设置对应的服务器mysql备份路径值";
                }
            }
            if (DataBaseEnums.MONGODB.getCode().toString().equals(dataSourceType)) {
                String mongodbBackupPath = serverVo.getMongodbBackupPath();
                if (StringUtils.isEmpty(mongodbBackupPath)) {
                    return "mongodb备份路径不能为空! 请到服务器管理,设置对应的服务器mongodb备份路径值";
                }
            }
        }
        return null;
    }

    @Override
    public void insertBackupManagement(BackupManagementDto backupManagementDto) {
        BackupManagementPo po = new BackupManagementPo();
        BeanUtils.copyProperties(backupManagementDto, po);
        po.setId(IdWorker.getNextId());
        backupManagementMapper.insert(po);
    }

    @Sm4DecryptMethod
    @Override
    public void testConnection(BackupManagementDto backupManagementDto) {
        BackupManagementPo backupManagementPo1 = backupManagementMapper.selectById(backupManagementDto.getId());
        BeanUtils.copyProperties(backupManagementPo1, backupManagementDto);
        if (UsualUtil.strIsNotEmpty(backupManagementDto.getIpv4(), backupManagementDto.getUser(), backupManagementDto.getPassword(), backupManagementDto.getPort())) {

            QueryWrapper<BackupManagementPo> backupManagementPoQueryWrapper = new QueryWrapper<>();
            backupManagementPoQueryWrapper.eq("ipv4", backupManagementDto.getIpv4());
            List<BackupManagementPo> backupManagementPos = backupManagementMapper.selectList(backupManagementPoQueryWrapper);
            BackupManagementPo backupManagementPo = null;
            if (CollectionUtils.isNotEmpty(backupManagementPos)) {
                backupManagementPo = new BackupManagementPo();
                backupManagementPo.setId(backupManagementPos.get(0).getId());
                backupManagementPo.setTestStatus(backupManagementPos.get(0).getTestStatus());
            }
            backupManagementDto.setPassword(DesUtil.decrypt(backupManagementDto.getPassword()));
            JSchUtil jSchUtil = new JSchUtil(backupManagementDto.getIpv4(), backupManagementDto.getUser(), backupManagementDto.getPassword(), Integer.parseInt(backupManagementDto.getPort()), 3 * 60 * 1000);
            try {
                jSchUtil.loginSftp();

                if (Optional.ofNullable(backupManagementPo).isPresent()) {
                    backupManagementPo.setTestStatus("1");
                    backupManagementMapper.updateById(backupManagementPo);
                }
                // 退出Jsch方式登录
                jSchUtil.logoutSftp();
            } catch (Exception e) {
                if (Optional.ofNullable(backupManagementPo).isPresent()) {
                    backupManagementPo.setTestStatus("0");
                    backupManagementMapper.updateById(backupManagementPo);
                }
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("参数缺失，测试连接失败！");
        }
    }

    @Override
    public void delete(Long[] ids) {
        try {
            for (Long id : ids) {
                int result = backupManagementMapper.deleteById(id);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<ServerInfoDto> getServerInfoList() {
        // 这里我们只查询有IPv4地址的服务器，并转换为DTO
        return this.list(new LambdaQueryWrapper<BackupManagementPo>()
                .isNotNull(BackupManagementPo::getIpv4)
                .ne(BackupManagementPo::getIpv4, ""))
                .stream()
                .map(server -> new ServerInfoDto(
                        server.getId(),
                        server.getName(),
                        server.getIpv4()))
                .collect(Collectors.toList());
    }

    //定时将备份历史表的状态改为停止备份，每隔一小时执行一次
    @SneakyThrows
    @Override
    public void reSetBackupStatus() {
        List<LyDbBackupHistoryRecordPo> historyRecordPoList = lyDbBackupHistoryRecordMapper.selectList(new LambdaQueryWrapper<LyDbBackupHistoryRecordPo>().eq(LyDbBackupHistoryRecordPo::getBackupStatus, "0").or().eq(LyDbBackupHistoryRecordPo::getBackupStatus, "5"));
        for (int i = 0; i < historyRecordPoList.size(); i++) {
            Date operationTime = historyRecordPoList.get(i).getOperationTime();
            Date nowTime = new Date();
            long duration = nowTime.getTime() - operationTime.getTime();
            long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
            if (minutes > 120) {
                historyRecordPoList.get(i).setBackupStatus("3");
                historyRecordPoList.get(i).setProportion("100");
                lyDbBackupHistoryRecordMapper.updateById(historyRecordPoList.get(i));
            }
        }
    }

}

