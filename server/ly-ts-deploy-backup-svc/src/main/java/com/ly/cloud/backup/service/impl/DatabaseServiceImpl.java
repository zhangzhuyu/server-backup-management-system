package com.ly.cloud.backup.service.impl;

import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.ly.cloud.auth.util.StringUtils;
import com.ly.cloud.backup.mapper.DatabaseServerMapper;
import com.ly.cloud.backup.mapper.ServerMapper;
import com.ly.cloud.backup.mapper.WarningRuleMapper;
import com.ly.cloud.backup.po.DatabaseServerPo;
import com.ly.cloud.backup.util.DesUtil;
import com.ly.cloud.backup.common.constant.CommonConstant;
import com.ly.cloud.backup.util.DateUtil;
import com.ly.cloud.backup.vo.*;
import com.ly.cloud.backup.common.enums.CommonEnums;
import com.ly.cloud.backup.util.ClassLoaderUtil;
import com.ly.cloud.backup.util.UsualUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ly.cloud.backup.dto.DatabaseDto;
import com.ly.cloud.backup.po.DatabasePo;
import com.ly.cloud.backup.mapper.DatabaseMapper;
import com.ly.cloud.backup.service.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 数据库信息表
 *
 * @author chenguoqing
 */
@Service
public class DatabaseServiceImpl extends ServiceImpl<DatabaseMapper, DatabasePo> implements DatabaseService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseServiceImpl.class);

    private final Map<String, Class<?>> providers = Maps.newConcurrentMap();

    @Resource
    private DatabaseMapper databaseMapper;

    @Autowired
    private DatabaseServerMapper databaseServerMapper;

    @Autowired
    private WarningRuleMapper warningRuleMapper;

    @Autowired
    private ServerMapper serverMapper;

    /**
     * 条件查询数据库信息
     *
     * @param pageNum     : 页码
     * @param pageSize    : 页码大小
     * @param databaseDto : 查询参数
     * @return Page<DatabaseListVo> : 数据库信息
     * @author: jiangzhongxin
     * @date: 2022/3/8 12:02
     */
    @Override
    public IPage<DatabaseListVo> selectPageLike(Integer pageNum, Integer pageSize, DatabaseDto databaseDto) {
        try {
            String content = databaseDto.getContent();
            List<DatabaseListVo> res = databaseMapper.selectAllList(databaseDto);
            Map<String, List<String>> prefixAndFieldsMap = new HashMap<>(2);
            prefixAndFieldsMap.put("SOURCE_TYPE", Collections.singletonList("sourceType"));
            UsualUtil.assignmentByEnums(res, prefixAndFieldsMap);
            // 手动进行分页
            pageNum = pageNum > 0 ? pageNum : 1;
            pageSize = pageSize > 0 ? pageSize : 15;

            List<DatabaseListVo> subList = res.stream()
                    .filter(UsualUtil::objNotNull)
                    .filter(r -> UsualUtil.strIsEmpty(content) || r.getDataSourceName().contains(content) || r.getSourceType().contains(content) || r.getDataBaseUser().contains(content))
                    .skip((pageNum - 1) * pageSize)
                    .limit(pageSize)
                    .collect(Collectors.toList());
            long total = subList.size();
            IPage<DatabaseListVo> pageResult = new Page<>(pageNum, pageSize, total);

            if (CollectionUtils.isNotEmpty(subList)) {
                subList.forEach(databaseListVo -> {
                    Long id = databaseListVo.getId();
                    //获取关联的服务器信息
                    List<DatabaseServerPo> databaseServerPos = databaseServerMapper.selectList(new QueryWrapper<DatabaseServerPo>().lambda()
                            .eq(DatabaseServerPo::getDatabaseId, id));
                    if (CollectionUtils.isNotEmpty(databaseServerPos)) {
                        List<String> serverIds = databaseServerPos.stream().map(m -> String.valueOf(m.getServerId())).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(serverIds)) {
                            String[] stringSize = new String[serverIds.size()];
                            final String[] stringIds = serverIds.toArray(stringSize);
                            databaseListVo.setServerId(stringIds);
                        }
                    }
                });
            }


            pageResult.setRecords(subList);
            return pageResult;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 查询一条数据
     *
     * @param id : 主键
     * @return DatabaseVo : 数据
     * @author: jiangzhongxin
     * @date: 2022/3/8 12:05
     */
    @Override
    public DatabaseVo get(String id, boolean isGet) {
        try {
            if (UsualUtil.strIsNotEmpty(id)) {
                DatabaseVo databaseVo = databaseMapper.get(id);
                if (UsualUtil.objNotNull(databaseVo)) {
                    List<SelectVo> servers = servers();
                    Map<String, List<String>> prefixAndFieldsMap = new HashMap<>(2);
                    if (isGet) {
                        prefixAndFieldsMap.put("SOURCE_TYPE", Collections.singletonList("sourceType"));
                        prefixAndFieldsMap.put("WHETHER_MONITORING", Collections.singletonList("whetherMonitoring"));
                        UsualUtil.assignmentByEnums(databaseVo, prefixAndFieldsMap);
                    }

                    //获取关联的服务器信息
                    List<DatabaseServerPo> databaseServerPos = databaseServerMapper.selectList(new QueryWrapper<DatabaseServerPo>().lambda()
                            .eq(DatabaseServerPo::getDatabaseId, id));
                    if (CollectionUtils.isNotEmpty(databaseServerPos)) {
                        List<String> serverIds = databaseServerPos.stream().map(m -> m.getServerId().toString()).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(serverIds)) {
                            List<String> serverNames = new ArrayList<>();
                            for (String serverId : serverIds) {
                                ServerVo serverVo = serverMapper.selectById(Long.valueOf(serverId));
                                if (serverVo != null) {
                                    serverNames.add(serverVo.getName());
                                }
                            }
                            databaseVo.setServerId(serverIds);
                            databaseVo.setServerNames(serverNames);
                        }
                    }
                    if (isGet) {
                        databaseVo.setPassword("");
                    }
                    return databaseVo;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        return null;
    }

    /**
     * 添加数据库信息表
     *
     * @param databaseDto 数据库信息表
     * @return Boolean ：操作是否成功
     * @author: jiangzhongxin
     * @date: 2022/3/8 11:55
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean insert(DatabaseDto databaseDto) {
        try {
            if (checkUniqueness(databaseDto)) {
                DatabasePo databasePo = new DatabasePo();
                BeanUtils.copyProperties(databaseDto, databasePo);
                if (databasePo.getSourceType().equals("Oracle")) {
                    databasePo.setSourceType("1");
                    databasePo.setDriver("oracle.jdbc.driver.OracleDriver");
                }else if(databasePo.getSourceType().equals("mysql")){
                    databasePo.setSourceType("2");
                    databasePo.setDriver("com.mysql.jdbc.Driver");
                }else if(databasePo.getSourceType().equals("Mongo")){
                    databasePo.setSourceType("6");
                    databasePo.setDriver("com.mysql.jdbc.Driver");
                }
                // 编码
                databasePo.setPassword(DesUtil.encrypt(databasePo.getPassword()));
                handleTestStatus(databasePo);
                databasePo.setHealthStatus("1");
                int insert = databaseMapper.insert(databasePo);
                //保存数据库和服务器的关联关系到映射表
                if (databaseDto.getServerId() != null && databaseDto.getServerId().size() > 0) {
                    //先清空旧的映射关系
                    databaseServerMapper.delete(new QueryWrapper<DatabaseServerPo>().lambda().eq(DatabaseServerPo::getDatabaseId, databasePo.getId()));
                    for (String serverId : databaseDto.getServerId()) {
                        DatabaseServerPo databaseServerPo = new DatabaseServerPo();
                        databaseServerPo.setDatabaseId(databasePo.getId());
                        databaseServerPo.setServerId(UsualUtil.getLong(serverId));
                        databaseServerMapper.insert(databaseServerPo);
                    }
                }
                if (insert > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return false;
    }

    /**
     * 更新数据库信息表
     *
     * @param databaseDto 数据库信息表
     * @return Boolean ：操作是否成功
     * @author: jiangzhongxin
     * @date: 2022/3/8 11:55
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(DatabaseDto databaseDto) {
        try {
            DatabasePo databasePo = new DatabasePo();
            BeanUtils.copyProperties(databaseDto, databasePo);
            String password = databasePo.getPassword();
            DatabasePo po = databaseMapper.selectById(databasePo.getId());
            if (UsualUtil.objNotNull(po)) {
                // 若密码被修改，则重新进行加密
                if (UsualUtil.objUnequals(password, po.getPassword())) {
                    databasePo.setPassword(DesUtil.encrypt(password));
                }
                handleTestStatus(databasePo);
                databasePo.setOperationTime(DateUtil.getCurrentDate());
                int i = databaseMapper.updateById(databasePo);
                //保存数据库和服务器的关联关系到映射表
                if (databaseDto.getServerId() != null && databaseDto.getServerId().size() > 0) {
                    //先清空旧的映射关系
                    databaseServerMapper.delete(new QueryWrapper<DatabaseServerPo>().lambda().eq(DatabaseServerPo::getDatabaseId, databaseDto.getId()));
                    for (String serverId : databaseDto.getServerId()) {
                        DatabaseServerPo databaseServerPo = new DatabaseServerPo();
                        databaseServerPo.setDatabaseId(databaseDto.getId());
                        databaseServerPo.setServerId(Long.valueOf(serverId));
                        databaseServerMapper.insert(databaseServerPo);
                    }
                }
                if (i > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return false;
    }

    /**
     * 当新增或编辑数据库信息-测试状态为【testStatusSuccess】时，需要重新校验下测试状态时，需要根据结果对测试状态进行重新赋值
     * （排除在新增或修改过程中测试功能成功后又继续修改参数但未进行测试直接进行提交，但测试状态却是成功的bug）
     *
     * @param databasePo : 数据库信息相关参数
     * @author jiangzhongxin
     * @date 2022/4/1 15:43
     */
    private void handleTestStatus(DatabasePo databasePo) {
        if (UsualUtil.objNotNull(databasePo)) {
            final String testStatusSuccess = CommonEnums.TEST_STATUS_SUCCESS.getCode().toString();
            final String testStatusFail = CommonEnums.TEST_STATUS_FAIL.getCode().toString();
            String user = new String(databasePo.getUser());
            String password = databasePo.getPassword();
            String decrypt = DesUtil.decrypt(password);
            String driver = databasePo.getDriver();
            String url = databasePo.getUrl();
            String sourceType = databasePo.getSourceType();
            // 进行测试是否能够连接成功

            if (UsualUtil.strIsNotEmpty(driver, url, user, password)) {
                if (UsualUtil.objNotNull(providers.get(driver))) {
                    String feedback = classForName(driver);
                    if (UsualUtil.objNotNull(feedback)) {
                        return;
                    } else {
                        logger.info("driver loading feedback information is null");
                    }
                }
                Boolean result = false;
                String expiryDate = null;
//                if (driver.toLowerCase().contains("oracle")) {
                if (sourceType.equals("1")) {
                    user = StringUtils.equals(user.toLowerCase(), "sys") ? "sys as sysdba" : user;
                    Map<String, Object> infoMap = getConnectionOracle(url, user, UsualUtil.strIsNotEmpty(decrypt) ? decrypt : password);
                    if (infoMap != null) {
                        result = (Boolean) infoMap.get("testStatus");
                        expiryDate = (String) infoMap.get("expiryDate");
                    }
                } else {
                    result = getConnection(url, user, UsualUtil.strIsNotEmpty(decrypt) ? decrypt : password);
                }
                // 新增或编辑时需要重新测试下测试功能时，需要根据结果对测试状态进行重新赋值;
                databasePo.setTestStatus(result ? testStatusSuccess : testStatusFail);
                databasePo.setPasswordExpireTime(expiryDate);
            }
        }
    }

    /**
     * 通过一定参数校验唯一性，若不存在则返回true
     *
     * @param databaseDto : 查询参数
     * @return Integer 查询的个数
     * @author: jiangzhongxin
     * @date 2022/4/1 14:52
     */
    @Override
    public Boolean checkUniqueness(DatabaseDto databaseDto) {
        try {
            return databaseMapper.checkUniqueness(databaseDto) == 0;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 批量删除数据库信息
     *
     * @param ids : 主键集合
     * @return Boolean ：操作是否成功
     * @author: jiangzhongxin
     * @date: 2022/3/8 11:58
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteByMulti(List<String> ids) {
        try {
            if (UsualUtil.collIsNotEmpty(ids)) {
                Integer integer = databaseMapper.deleteByMulti(ids);
                //保存数据库和服务器的关联关系到映射表
                if (ids.size() > 0) {
                    //先清空旧的映射关系
                    ids.forEach(id -> {
                        databaseServerMapper.delete(new QueryWrapper<DatabaseServerPo>().lambda().eq(DatabaseServerPo::getDatabaseId, id));
                        List<String> longs = warningRuleMapper.selectRuleIdByResourcesObject(Long.parseLong(id));
                        if (CollectionUtils.isNotEmpty(longs)) {
                            List<String> list = warningRuleMapper.selectDisableRuleId(longs);
                            if (CollectionUtils.isNotEmpty(list)) {
                                warningRuleMapper.updateDisableByRuleIds(list);
                            }
                        }
                        warningRuleMapper.deleteWarningObjectByObjectId(Long.parseLong(id));
                    });
                }
                if (integer > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        return null;
    }

    /**
     * 初始化目前有的类包
     *
     * @author: jiangzhongxin
     * @date: 2022/3/8 15:36
     */
    @PostConstruct
    public void initClassForName() {
        try {
            URL url = this.getClass().getClassLoader().getResource("db/drivers");
            Optional.ofNullable(url)
                    .map(URL::getPath)
                    .map(r -> {
                        ClassLoaderUtil.getInstance().loadClasspath(r, null);
                        return null;
                    });
            classForName(CommonConstant.DRIVER_LIST);
            logger.info("drivers package loading completed!");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 类加载
     *
     * @param drivers : 驱动字符集
     * @return String ：反馈信息
     * @author: jiangzhongxin
     * @date: 2022/3/8 15:34
     */
    public String classForName(String... drivers) {
        try {
            for (String string : drivers) {
                Class<?> driver = Class.forName(string);
                providers.put(driver.getName(), driver);
                logger.info("Driver package loaded: {}", driver);
            }
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
            return "未找到对应驱动包";
        }
        return null;
    }

    /**
     * 根据数据源相关测试数据源是否可连接成功
     *
     * @param databaseDto : 数据源数据
     * @return Boolean ：测试是否成功
     * @author: jiangzhongxin
     * @date: 2022/3/8 12:16
     */
    @Override
    public Boolean testDataSource(DatabaseDto databaseDto) {
        try {
            if (StringUtils.isNotEmpty(databaseDto.getContent()) && "insert".equals(databaseDto.getContent())) {
                databaseDto.setPassword(DesUtil.encrypt(databaseDto.getPassword()));
            }
            return handleTestDataSource(databaseDto.getId(), databaseDto.getDriver(), databaseDto.getUrl(), databaseDto.getUser(), databaseDto.getPassword());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 根据数据源主键测试数据源是否可连接成功
     *
     * @param databaseId : 主键
     * @return Boolean : 测试是否成功
     * @author jiangzhongxin
     * @date 2022/4/1 9:36
     */
    @Override
    public Boolean testDataSourceById(String databaseId) {
        try {
            DatabasePo databasePo = databaseMapper.selectById(databaseId);
            if (UsualUtil.objNotNull(databasePo)) {
                return handleTestDataSource(databasePo.getId(), databasePo.getDriver(), databasePo.getUrl(), databasePo.getUser(), databasePo.getPassword());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 根据数据源相关测试数据源是否可连接成功
     *
     * @param id       : 主键
     * @param driver   : 驱动
     * @param url      : url
     * @param user     : 用户名
     * @param password : 密码
     * @return Boolean : 测试是否成功
     * @author jiangzhongxin
     * @date 2022/4/1 9:44
     */
    private Boolean handleTestDataSource(Long id, String driver, String url, String user, String password) {
        try {
            if (UsualUtil.strIsNotEmpty(driver, url, user, password)) {
                if (UsualUtil.objNotNull(providers.get(driver))) {
                    String feedback = classForName(driver);
                    if (UsualUtil.objNotNull(feedback)) {
                        return false;
                    } else {
                        logger.info("driver loading feedback information is null");
                    }
                }
                String decrypt = "";
                Boolean result = false;
                String expiryDate = null;
                if (driver.toLowerCase().contains("oracle")) {
                    user = StringUtils.equals(user.toLowerCase(), "sys") ? "sys as sysdba" : user;
                    decrypt = DesUtil.decrypt(password);
                    Map<String, Object> infoMap = getConnectionOracle(url, user, UsualUtil.strIsNotEmpty(decrypt) ? decrypt : password);
                    if (infoMap != null) {
                        result = (Boolean) infoMap.get("testStatus");
                        expiryDate = (String) infoMap.get("expiryDate");
                    }
                } else {
                    decrypt = DesUtil.decrypt(password);
                    result = getConnection(url, user, UsualUtil.strIsNotEmpty(decrypt) ? decrypt : password);
                }
                // 处于新增时测试功能时，没有生成id;
                // 处于编辑及列表时测试功能时，需要根据结果对测试状态的修改;
                updateStatus(id, result, expiryDate);
                return result;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 测试连接
     *
     * @param url      : 地址
     * @param user     : 用户名
     * @param password : 密码
     * @return Boolean : 连接是否成功
     * @author: jiangzhongxin
     * @date: 2022/3/8 15:34
     */
    public Boolean getConnection(String url, String user, String password) {
        Connection con = null;
        boolean present = false;
        try {
            if (UsualUtil.strIsNotEmpty(url, user, password)) {
                con = DriverManager.getConnection(url, user, password);
                present = UsualUtil.objNotNull(con);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if (UsualUtil.objNotNull(con)) {
                    // 获取链接状态后，关闭链接
                    assert con != null;
                    con.close();
                }
            } catch (SQLException throwables) {
                logger.error(throwables.getMessage(), throwables);
            }
        }
        return present;
    }

    /**
     * Oracle测试连接
     *
     * @param url      : 地址
     * @param user     : 用户名
     * @param password : 密码
     * @return Boolean : 连接是否成功
     * @author: jiangzhongxin
     * @date: 2022/3/8 15:34
     */
    public Map<String, Object> getConnectionOracle(String url, String user, String password) {
        Connection con = null;
        boolean present = false;
        Map<String, Object> returnMap = new HashMap<>();
        try {
            if (UsualUtil.strIsNotEmpty(url, user, password)) {
                con = DriverManager.getConnection(url, user, password);
                con.setAutoCommit(false);
                present = UsualUtil.objNotNull(con);
                returnMap.put("testStatus", present);

                // 查询密码过期时间
                if (present) {
                    Statement conn = con.createStatement();
                    if (user.equals("sys as sysdba")) {
                        user = "sys";
                    }
                    //4、执行sql语句
                    String sql = "select username,account_status,expiry_date,profile from dba_users where username='" + user.toUpperCase() + "'";
//                    String sql="select username,account_status,expiry_date,profile from dba_users where username='SYSDG'";
                    PreparedStatement pt = con.prepareStatement(sql);
                    ResultSet rs = pt.executeQuery();
                    while (rs.next()) {
                        String rsString = rs.getString(3);
                        String rsName = rs.getString(1);
                        if (rsName != null && (rsString == null || rsString.length() == 0)) {
                            returnMap.put("expiryDate", "-1");
                        } else {
                            String expiryDate = rsString.substring(0, 19);
                            if (expiryDate == null || expiryDate.length() == 0) {
                                returnMap.put("expiryDate", "-1");
                            } else {
                                returnMap.put("expiryDate", expiryDate);
                            }
                        }
                    }
                }
                return returnMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            return null;
        } finally {
            try {
                if (UsualUtil.objNotNull(con)) {
                    // 获取链接状态后，关闭链接
                    assert con != null;
                    con.close();
                }
            } catch (SQLException throwables) {
                logger.error(throwables.getMessage(), throwables);
            }
        }
        return null;
    }


    /**
     * 根据id更新测试状态
     *
     * @param id     : 主键
     * @param status : 测试状态 0 失败 1 成功
     * @author: jiangzhongxin
     * @date: 2022/3/8 11:16
     */
    public void updateStatus(Long id, Boolean status, String expiryDate) {
        try {
            if (UsualUtil.objNotNull(id, status)) {
                DatabasePo databasePo = new DatabasePo();
                databasePo.setId(id);
                databasePo.setTestStatus(status ? CommonEnums.TEST_STATUS_SUCCESS.getCode().toString() : CommonEnums.TEST_STATUS_FAIL.getCode().toString());
                if (expiryDate != null && expiryDate.length() > 0) {
                    databasePo.setPasswordExpireTime(expiryDate);
                }
                databaseMapper.updateById(databasePo);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 查询所有关联服务器
     *
     * @return List<SelectVo>
     * @author jiangzhongxin
     * @date 2022/3/29 16:41
     */
    @Override
    public List<SelectVo> servers() {
        try {
            List<ServerVo> servers = databaseMapper.servers();
            if (UsualUtil.collIsNotEmpty()) {
                return servers.stream().map(serverVo -> {
                    SelectVo selectVo = new SelectVo();
                    selectVo.setLabel(serverVo.getName() + "（" + serverVo.getIpv4() + "）");
                    selectVo.setValue(String.valueOf(serverVo.getId()));
                    selectVo.setParentValue("");
                    return selectVo;
                }).collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        return null;
    }

}
