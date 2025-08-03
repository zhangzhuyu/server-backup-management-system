package com.ly.cloud.backup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.cloud.auth.util.StringUtils;
import com.ly.cloud.backup.common.annotation.Sm4DecryptMethod;
import com.ly.cloud.backup.dto.ServerInfoDto;
import com.ly.cloud.backup.mapper.WarningRuleMapper;
import com.ly.cloud.backup.po.BackupManagementPo;
import com.ly.cloud.backup.util.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ly.cloud.backup.dto.ServerDto;
import com.ly.cloud.backup.po.ServerPo;
import com.ly.cloud.backup.mapper.ServerMapper;
import com.ly.cloud.backup.service.ServerService;
import com.ly.cloud.backup.vo.ServerVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;

import static com.ly.cloud.backup.common.constant.FullLnkTraceConstant.CPU;
import static com.ly.cloud.backup.common.constant.FullLnkTraceConstant.MEMORY;
import static com.ly.cloud.backup.common.constant.RedisConstants.*;
import static com.ly.cloud.backup.common.constant.RedisConstants.COLON;

/**
 * 服务器信息表
 *
 * @author chenguoqing
 */
@Service
public class ServerServiceImpl extends ServiceImpl<ServerMapper, ServerPo> implements ServerService {

    private static final Logger logger = LoggerFactory.getLogger(ServerServiceImpl.class);

    /**
     * profile配置文件
     */
    private static final String PROFILE = "profile";

    /**
     * nginx默认配置文件名
     */
    private static final String FILENAME = "nginx.conf";

    @Autowired
    private ServerMapper serverMapper;

    @Autowired
    private WarningRuleMapper warningRuleMapper;

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(ServerDto serverDto) {

        QueryWrapper<ServerPo> serverPoQueryWrapper = new QueryWrapper<>();
        serverPoQueryWrapper.eq("ipv4", serverDto.getIpv4());
        List<ServerPo> serverPos = serverMapper.selectList(serverPoQueryWrapper);
//        List<ServerPo> serverPos = serverMapper.selectList(new QueryWrapper<ServerPo>()
//                .lambda().eq(ServerPo::getIpv4, serverDto.getIpv4()));
        if (StringUtils.isNotEmpty(serverPos)) {
            throw new RuntimeException("Ipv4为" + serverDto.getIpv4() + "的服务器已存在数据库中，请勿重复添加！");
        }

        try {
            ServerPo serverPo = new ServerPo();
            BeanUtils.copyProperties(serverDto, serverPo);
            // 初始值
            serverPo.setHealthStatus("1");
            serverPo.setFirewallState("0");

            if ("1".equals(serverPo.getSystemType())) {
                if (UsualUtil.strIsNotEmpty(serverDto.getIpv4(), serverDto.getUser(), serverDto.getPassword(), serverDto.getPort())) {
                    serverDto.setPassword(DesUtil.decrypt(serverDto.getPassword()));
                    JSchUtil jSchUtil = new JSchUtil(serverDto.getIpv4(), serverDto.getUser(), serverDto.getPassword(), Integer.parseInt(serverDto.getPort()), 3 * 60 * 1000);
                    try {
                        String[] logs = jSchUtil.execCommand("hostname & firewall-cmd --state 2>&1").split("\\n");
                        // 退出Exec方式登录
                        jSchUtil.logoutExec();
                        serverPo.setHostName(logs[0]);
                        if ("not running".equals(logs[1])) {
                            serverPo.setFirewallState("0");
                        } else {
                            serverPo.setFirewallState("1");
                        }

                        // 测试是否能访问外网
                        String logs2 = "";
                        try {
                            logs2 = jSchUtil.execCommand("curl www.baidu.com 2>&1");
                        } catch (Exception e) {
                            logger.error("测试ping通外网失败");
                        }
                        if (StringUtils.isNotEmpty(logs2) && logs2.contains("百度一下")) {
                            serverPo.setWhetherOuterNet("1");
                        } else {
                            serverPo.setWhetherOuterNet("0");
                        }

                        // 检查liunx系统有没有创建日志脚本
                        String[] logs3 = jSchUtil.execCommand("cat /etc/profile|grep USER_IP 2>&1").split("\\n");
                        if (logs3 == null || (logs3.length == 1 && logs3[0].equals(""))) {
                            // 换行
                            byte[] brByte = "\n\n\n\n".getBytes();

                            // 读取liunx系统中/etc/profile 文件内容
                            jSchUtil.loginSftp();
                            byte[] downloadByte = jSchUtil.downloadFileByte("/etc", PROFILE);

                            // 读取系统本地的日志文件
                            byte[] profileInfo = PublicUtil.readProfileInfo("/scriptFile/linux操作日志脚本.txt");
                            profileInfo = new String(profileInfo).replaceAll("\r\n", "\n").getBytes();

                            // 合并后的文件内容
                            byte[] mergeProfile = new byte[brByte.length + downloadByte.length + profileInfo.length];
                            System.arraycopy(downloadByte, 0, mergeProfile, 0, downloadByte.length);
                            System.arraycopy(brByte, 0, mergeProfile, downloadByte.length, brByte.length);
                            System.arraycopy(profileInfo, 0, mergeProfile, downloadByte.length + brByte.length, profileInfo.length);

                            // 把合并后的内容 重新写入/etc/profile 文件中
                            ByteArrayInputStream infoStream = new ByteArrayInputStream(mergeProfile);
                            jSchUtil.uploadFileByte("/etc", PROFILE, infoStream);
                            jSchUtil.logoutSftp();
                            // 刷新配置文件
                            String[] logs4 = jSchUtil.execCommand("source /etc/profile 2>&1").split("\\n");
                        }
                        serverPo.setTestStatus("1");// 连接测试状态成功
                    } catch (Exception e) {
                        serverPo.setTestStatus("0");// 连接测试状态失败
                    }
                }
            }
            serverPo.setPassword(DesUtil.encrypt(serverPo.getPassword()));
            serverPo.setOperationTime(new Date());
            serverPo.setId(IdWorker.getNextId());
            int result = serverMapper.insert(serverPo);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("新增失败！");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(ServerDto serverDto) {
        try {
            ServerPo serverPo = new ServerPo();
            BeanUtils.copyProperties(serverDto, serverPo);
//            if (StringUtils.isNotEmpty(serverPo.getPassword())) {
//                serverPo.setPassword(DesUtil.encrypt(serverPo.getPassword()));
//            }
            if ("1".equals(serverPo.getSystemType())) {
                if (UsualUtil.strIsNotEmpty(serverDto.getIpv4(), serverDto.getUser(), serverDto.getPassword(), serverDto.getPort())) {
                    serverDto.setPassword(DesUtil.decrypt(serverDto.getPassword()));
                    JSchUtil jSchUtil = new JSchUtil(serverDto.getIpv4(), serverDto.getUser(), serverDto.getPassword(), Integer.parseInt(serverDto.getPort()), 3 * 60 * 1000);
                    try {
                        if (StringUtils.isEmpty(serverDto.getHostName())) {
                            String[] logs = jSchUtil.execCommand("hostname 2>&1").split("\\n");
                            String hostname = logs[0];
                            if (hostname != null) {
                                serverPo.setHostName(hostname);
                            }
                        }

                        // 检查liunx系统有没有创建日志脚本
                        String[] logs2 = jSchUtil.execCommand("cat /etc/profile|grep USER_IP 2>&1").split("\\n");
                        if (logs2 == null || (logs2.length == 1 && logs2[0].equals(""))) {
                            // 换行
                            byte[] brByte = "\n\n\n\n".getBytes();

                            // 读取liunx系统中/etc/profile 文件内容
                            jSchUtil.loginSftp();
                            byte[] downloadByte = jSchUtil.downloadFileByte("/etc", PROFILE);

                            // 读取系统本地的日志文件
                            byte[] profileInfo = PublicUtil.readProfileInfo("/scriptFile/linux操作日志脚本.txt");
                            profileInfo = new String(profileInfo).replaceAll("\r\n", "\n").getBytes();

                            // 合并后的文件内容
                            byte[] mergeProfile = new byte[brByte.length + downloadByte.length + profileInfo.length];
                            System.arraycopy(downloadByte, 0, mergeProfile, 0, downloadByte.length);
                            System.arraycopy(brByte, 0, mergeProfile, downloadByte.length, brByte.length);
                            System.arraycopy(profileInfo, 0, mergeProfile, downloadByte.length + brByte.length, profileInfo.length);

                            // 把合并后的内容 重新写入/etc/profile 文件中
                            ByteArrayInputStream infoStream = new ByteArrayInputStream(mergeProfile);
                            jSchUtil.uploadFileByte("/etc", PROFILE, infoStream);
                            jSchUtil.logoutSftp();
                            // 刷新配置文件
                            String[] logs3 = jSchUtil.execCommand("source /etc/profile 2>&1").split("\\n");
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
            int result = serverMapper.updateById(serverPo);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }

    @Override
    public ServerVo find(Long id) {
        ServerVo serverVo = serverMapper.selectById(id);
        if (serverVo != null) {
            if (UsualUtil.strIsNotEmpty(serverVo.getIpv4(), serverVo.getUser(), serverVo.getPassword(), serverVo.getPort())) {
                serverVo.setPassword(DesUtil.decrypt(serverVo.getPassword()));
                JSchUtil jSchUtil = new JSchUtil(serverVo.getIpv4(), serverVo.getUser(), serverVo.getPassword(), Integer.parseInt(serverVo.getPort()), 3 * 60 * 1000);
                try {
                    // 获取服务器 cpu使用率，内存使用率，硬盘使用率
                    serverVo.setUtilizationRateCPU(jSchUtil.getCPUUtilizationRate());
                    serverVo.setUtilizationRateRAM(jSchUtil.getRAMUtilizationRate().get("utilizationRateRAM"));
                    serverVo.setUtilizationRateHardDisk(jSchUtil.getSysHardDiskUtilizationRate().get("utilizationRateHardDisk"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } finally {
                    // 退出Exec方式登录
                    jSchUtil.logoutExec();
                }
            }
        }
        return serverVo;
    }

    @Override
    public IPage<ServerVo> query(int pageNum, int pageSize, String content, String affiliatedCompany) {

        Page<ServerVo> page = new Page<>(pageNum > 0 ? pageNum : 1, pageSize > 0 ? pageSize : 15);
        ServerDto serverDto = new ServerDto();
        if (StringUtils.isNotEmpty(content)) {
            serverDto.setContent(content);
        }
        if (StringUtils.isNotEmpty(affiliatedCompany)) {
            serverDto.setAffiliatedCompany(Long.valueOf(affiliatedCompany));
        }
        IPage<ServerVo> serverVoIPage = serverMapper.select(page, serverDto);
        List<ServerVo> serverVos = serverVoIPage.getRecords();
        for (ServerVo serverVo : serverVos) {
            if ("1".equals(serverVo.getSystemType())) {
                if (UsualUtil.strIsNotEmpty(serverVo.getIpv4(), serverVo.getUser(), serverVo.getPassword(), serverVo.getPort())) {
//                serverVo.setPassword(DesUtil.decrypt(serverVo.getPassword()));
//                JSchUtil jSchUtil = new JSchUtil(serverVo.getIpv4(), serverVo.getUser(), serverVo.getPassword(), Integer.parseInt(serverVo.getPort()), 3 * 60 * 1000);
//                try {
//                    String[] logs = jSchUtil.execCommand("hostname & firewall-cmd --state 2>&1").split("\\n");
//                    String hostname = logs[0];
//                    String fpd = "not running".equals(logs[1]) ? "0" : "1";
//                    if (hostname != null) {
//                        if (StringUtils.isEmpty(serverVo.getHostName())) {
//                            serverVo.setHostName(hostname);
//                        }
//                        if (!fpd.equals(serverVo.getFirewallState())) {
//                            serverVo.setFirewallState(fpd);
//                        }
//                        serverVo.setHealthStatus("1");
//                        ServerPo serverPo = new ServerPo();
//                        BeanUtils.copyProperties(serverVo, serverPo);
//                        serverMapper.updateById(serverPo);
//                    } else {
//                        serverVo.setHealthStatus("0");
//                    }
//                } catch (Exception e) {
//                    logger.error("连接服务器失败!", e);
//                    ServerPo serverPo = new ServerPo();
//                    serverPo.setId(serverVo.getId());
//                    serverPo.setHealthStatus("0");
//                    serverMapper.updateById(serverPo);
//                }
                    //获取数据库所在服务器的资源配置与使用率信息
//                    ServerPo serverPo = new ServerPo();
//                    BeanUtils.copyProperties(serverVo, serverPo);
//                    Map<String, Object> serverInfoByServerPo = appliedPortraitService.getServerInfoByServerPo(serverPo);
//                    Map<String, Object> file = (Map<String, Object>) serverInfoByServerPo.get("file");
//                    //系统盘
//                    serverVo.setUnitDisk(UsualUtil.getString(file.get("unitDisk")));
//                    serverVo.setUseUnitDisk(UsualUtil.getString(file.get("useUnitDisk")));
//                    serverVo.setTotalHardDisk(UsualUtil.getString(file.get("totalHardDisk")));
//                    serverVo.setUseHardDisk(UsualUtil.getString(file.get("useHardDisk")));
//                    //cpu
//                    Map<String, Object> cpu = (Map<String, Object>) serverInfoByServerPo.get(CPU);
//                    serverVo.setUnitCpu(UsualUtil.getString(cpu.get("unitCpu")));
//                    serverVo.setTotalCpu(UsualUtil.getString(cpu.get("totalCpu")));
//                    serverVo.setUseCpu(UsualUtil.getString(cpu.get("useCpu")));
//                    //内存
//                    Map<String, Object> memory = (Map<String, Object>) serverInfoByServerPo.get(MEMORY);
//                    serverVo.setUnitRam(UsualUtil.getString(memory.get("unitRam")));
//                    serverVo.setUseUnitRam(UsualUtil.getString(memory.get("useUnitRam")));
//                    serverVo.setTotalRam(UsualUtil.getString(memory.get("totalRam")));
//                    serverVo.setUseRam(UsualUtil.getString(memory.get("useRam")));
//                        // 获取服务器 cpu使用率，内存使用率，硬盘使用率
//                        serverVo.setUtilizationRateCPU(jSchUtil.getCPUUtilizationRate());
//                        serverVo.setMapRAM(jSchUtil.getRAMUtilizationRate());
//                        serverVo.setUtilizationRateRAM(serverVo.getMapRAM().get("utilizationRateRAM"));
//                        serverVo.setMapHardDisk(jSchUtil.getSysHardDiskUtilizationRate());
//                        serverVo.setUtilizationRateHardDisk(serverVo.getMapHardDisk().get("utilizationRateHardDisk"));
                }
            }
        }
        serverVoIPage.setRecords(serverVos);
        return serverVoIPage;
    }

    @Override
    public Boolean clearRedisServerCache() {
        String redisKey = REDIS_KEY_PREFIX_TC + "operations" + COLON + "*";
        Set keys = redisTemplate.keys(redisKey);
        try {
            redisTemplate.delete(keys);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void batchPowerOff(Long[] ids) {
        try {
            for (Long id : ids) {
                ServerVo serverVo = serverMapper.selectById(id);
                if (serverVo != null) {
                    if (UsualUtil.strIsNotEmpty(serverVo.getIpv4(), serverVo.getUser(), serverVo.getPassword(), serverVo.getPort())) {
                        serverVo.setPassword(DesUtil.decrypt(serverVo.getPassword()));
                        JSchUtil jSchUtil = new JSchUtil(serverVo.getIpv4(), serverVo.getUser(), serverVo.getPassword(), Integer.parseInt(serverVo.getPort()), 3 * 60 * 1000);
                        String log = jSchUtil.execCommand("sudo shutdown -h now 2>&1");
                        if (log.indexOf("program specified") != -1) {
                            throw new RuntimeException("当前用户没有开启免密码权限，关机命令执行失败！");
                        } else if (log.indexOf("sudoers") != -1) {
                            throw new RuntimeException("当前用户没有sudo权限，关机失败！");
                        }

                        // 退出Exec方式登录
                        jSchUtil.logoutExec();
//                        serverVo.setHealthStatus("0");
//                        ServerPo serverPo = new ServerPo();
//                        BeanUtils.copyProperties(serverVo, serverPo);
//                        int result = serverMapper.updateById(serverPo);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void synchronizationTime(Long[] ids) {
        try {
            List<Long> longs = Arrays.asList(ids);
            if (CollectionUtils.isNotEmpty(longs) && longs.contains(-1L)) {
                List<ServerPo> serverPos = serverMapper.selectList(null);
                List<Long> collect = serverPos.stream().map(s -> s.getId()).collect(Collectors.toList());
                Long[] array = new Long[collect.size()];
                for (int i = 0; i < collect.size(); i++) {
                    array[i] = collect.get(i);
                }
                ids = array;
            }
            for (Long id : ids) {
                ServerVo serverVo = serverMapper.selectById(id);
                if (serverVo != null) {
                    if (UsualUtil.strIsNotEmpty(serverVo.getIpv4(), serverVo.getUser(), serverVo.getPassword(), serverVo.getPort())) {
                        serverVo.setPassword(DesUtil.decrypt(serverVo.getPassword()));
                        JSchUtil jSchUtil = new JSchUtil(serverVo.getIpv4(), serverVo.getUser(), serverVo.getPassword(), Integer.parseInt(serverVo.getPort()), 3 * 60 * 1000);
                        String log = jSchUtil.execCommand("rpm -qa|grep ntp 2>&1");
                        if (log.indexOf("ntp-") == -1) {
                            String installLog = jSchUtil.execCommand("yum install ntp 2>&1");
                        }
                        String startLog1 = jSchUtil.execCommand("systemctl stop ntpd 2>&1");
                        String startLog2 = jSchUtil.execCommand("ntpdate ntp1.aliyun.com 2>&1");
                        String startLog3 = jSchUtil.execCommand("systemctl start ntpd 2>&1");
                        if (startLog2.indexOf("adjust") == -1) {
                            throw new RuntimeException("同步时间出现错误！同步失败！");
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("同步时间出现错误！同步失败！");
        }
    }

    @Override
    public void powerOffDto(ServerDto serverDto) {
        try {
            ServerVo serverVo = serverMapper.selectById(serverDto.getId());
            if (serverVo != null) {
                if (!(StringUtils.equals(serverDto.getUser(), serverVo.getUser()) && StringUtils.equals(serverDto.getPassword(), serverVo.getPassword()))) {
                    throw new RuntimeException("账号或密码错误，关机失败！");
                }

                if (UsualUtil.strIsNotEmpty(serverVo.getIpv4(), serverVo.getUser(), serverVo.getPassword(), serverVo.getPort())) {
                    serverVo.setPassword(DesUtil.decrypt(serverVo.getPassword()));
                    JSchUtil jSchUtil = new JSchUtil(serverVo.getIpv4(), serverVo.getUser(), serverVo.getPassword(), Integer.parseInt(serverVo.getPort()), 3 * 60 * 1000);
                    String log = jSchUtil.execCommand("sudo shutdown -h now 2>&1");
                    if (log.indexOf("program specified") != -1) {
                        throw new RuntimeException("当前用户没有开启免密码权限，关机命令执行失败！");
                    } else if (log.indexOf("sudoers") != -1) {
                        throw new RuntimeException("当前用户没有sudo权限，关机失败！");
                    }

                    // 退出Exec方式登录
                    jSchUtil.logoutExec();
//                        serverVo.setHealthStatus("0");
//                        ServerPo serverPo = new ServerPo();
//                        BeanUtils.copyProperties(serverVo, serverPo);
//                        int result = serverMapper.updateById(serverPo);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void restart(Long[] ids) {
        try {
            for (Long id : ids) {
                ServerVo serverVo = serverMapper.selectById(id);
                if (serverVo != null) {
                    if (UsualUtil.strIsNotEmpty(serverVo.getIpv4(), serverVo.getUser(), serverVo.getPassword(), serverVo.getPort())) {
                        serverVo.setPassword(DesUtil.decrypt(serverVo.getPassword()));
                        JSchUtil jSchUtil = new JSchUtil(serverVo.getIpv4(), serverVo.getUser(), serverVo.getPassword(), Integer.parseInt(serverVo.getPort()), 3 * 60 * 1000);
                        String log = jSchUtil.execCommand("sudo reboot 2>&1");
                        if (log.indexOf("program specified") != -1) {
                            throw new RuntimeException("当前用户没有开启免密码权限，重启命令执行失败！");
                        } else if (log.indexOf("sudoers") != -1) {
                            throw new RuntimeException("当前用户没有sudo权限，重启失败！");
                        }
                        // 退出Exec方式登录
                        jSchUtil.logoutExec();
//                    serverVo.setHealthStatus("0");
//                    ServerPo serverPo = new ServerPo();
//                    BeanUtils.copyProperties(serverVo, serverPo);
//                    int result = serverMapper.updateById(serverPo);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void restartDto(ServerDto serverDto) {
        try {
            ServerVo serverVo = serverMapper.selectById(serverDto.getId());
            if (serverVo != null) {
                if (!(StringUtils.equals(serverDto.getUser(), serverVo.getUser()) && StringUtils.equals(serverDto.getPassword(), serverVo.getPassword()))) {
                    throw new RuntimeException("账号或密码错误，重启失败！");
                }
                if (UsualUtil.strIsNotEmpty(serverVo.getIpv4(), serverVo.getUser(), serverVo.getPassword(), serverVo.getPort())) {
                    serverVo.setPassword(DesUtil.decrypt(serverVo.getPassword()));
                    JSchUtil jSchUtil = new JSchUtil(serverVo.getIpv4(), serverVo.getUser(), serverVo.getPassword(), Integer.parseInt(serverVo.getPort()), 3 * 60 * 1000);
                    String log = jSchUtil.execCommand("sudo reboot 2>&1");
                    if (log.indexOf("program specified") != -1) {
                        throw new RuntimeException("当前用户没有开启免密码权限，重启命令执行失败！");
                    } else if (log.indexOf("sudoers") != -1) {
                        throw new RuntimeException("当前用户没有sudo权限，重启失败！");
                    }
                    // 退出Exec方式登录
                    jSchUtil.logoutExec();
//                    serverVo.setHealthStatus("0");
//                    ServerPo serverPo = new ServerPo();
//                    BeanUtils.copyProperties(serverVo, serverPo);
//                    int result = serverMapper.updateById(serverPo);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long[] ids) {
        try {
            for (Long id : ids) {
                int result = serverMapper.deleteById(id);
                List<String> longs = warningRuleMapper.selectRuleIdByResourcesObject(id);
                System.out.println("集合"+longs);
                if (CollectionUtils.isNotEmpty(longs)) {
                    List<String> list = warningRuleMapper.selectDisableRuleId(longs);
                    if (CollectionUtils.isNotEmpty(list)) {
                        warningRuleMapper.updateDisableByRuleIds(list);
                    }
                }
                warningRuleMapper.deleteWarningObjectByObjectId(id);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }


    @Sm4DecryptMethod
    @Override
    public void testConnection(Long id) {
        ServerDto serverDto = new ServerDto();
        ServerVo serverVo = serverMapper.selectById(id);
        BeanUtils.copyProperties(serverVo, serverDto);
        if (UsualUtil.strIsNotEmpty(serverDto.getIpv4(), serverDto.getUser(), serverDto.getPassword(), serverDto.getPort())) {

            QueryWrapper<ServerPo> serverPoQueryWrapper = new QueryWrapper<>();
            serverPoQueryWrapper.eq("ipv4", serverDto.getIpv4());
            List<ServerPo> serverPos = serverMapper.selectList(serverPoQueryWrapper);
            ServerPo serverPo = null;
            if (CollectionUtils.isNotEmpty(serverPos)) {
                serverPo = new ServerPo();
                serverPo.setId(serverPos.get(0).getId());
                serverPo.setTestStatus(serverPos.get(0).getTestStatus());
            }
            serverDto.setPassword(DesUtil.decrypt(serverDto.getPassword()));
            JSchUtil jSchUtil = new JSchUtil(serverDto.getIpv4(), serverDto.getUser(), serverDto.getPassword(), Integer.parseInt(serverDto.getPort()), 3 * 60 * 1000);
            try {
                jSchUtil.loginSftp();

                if (Optional.ofNullable(serverPo).isPresent()) {
                    serverPo.setTestStatus("1");
                    serverMapper.updateById(serverPo);
                }
                // 退出Jsch方式登录
                jSchUtil.logoutSftp();
            } catch (Exception e) {
                if (Optional.ofNullable(serverPo).isPresent()) {
                    serverPo.setTestStatus("0");
                    serverMapper.updateById(serverPo);
                }
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("参数缺失，测试连接失败！");
        }
    }

    @Override
    public void testUser(ServerDto serverDto) {
        // 根据id查询服务器信息
        ServerVo serverVo = serverMapper.selectById(serverDto.getId());
        // 修改账号密码，用来判断登录的账号密码是否正确
        serverVo.setUser(serverDto.getUser());
        serverVo.setPassword(serverDto.getPassword());

        if (UsualUtil.strIsNotEmpty(serverVo.getIpv4(), serverVo.getUser(), serverVo.getPassword(), serverVo.getPort())) {
            serverVo.setPassword(DesUtil.decrypt(serverVo.getPassword()));
            JSchUtil jSchUtil = new JSchUtil(serverVo.getIpv4(), serverVo.getUser(), serverVo.getPassword(), Integer.parseInt(serverVo.getPort()), 3 * 60 * 1000);
            try {
                jSchUtil.loginSftp();
                // 退出Jsch方式登录
                jSchUtil.logoutSftp();
            } catch (Exception e) {
                throw new RuntimeException("账号密码错误！");
            }
        } else {
            throw new RuntimeException("参数缺失，测试连接失败！");
        }
    }

    @Override
    public List<String> getLogInformation(Long id, String startTime, String endTime) {
        List<String> reString = new ArrayList<>();
        ServerVo serverVo = serverMapper.selectById(id);
        if (serverVo != null && UsualUtil.strIsNotEmpty(serverVo.getIpv4(), serverVo.getUser(), serverVo.getPassword(), serverVo.getPort())) {
            serverVo.setPassword(DesUtil.decrypt(serverVo.getPassword()));
            JSchUtil jSchUtil = new JSchUtil(serverVo.getIpv4(), serverVo.getUser(), serverVo.getPassword(), Integer.parseInt(serverVo.getPort()), 3 * 60 * 1000);
            try {
                String info = "";
                if (StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)) {
                    info = jSchUtil.execCommand("find /var/log/history/" + serverVo.getUser() + " -type f 2>&1");
                } else {
                    info = jSchUtil.execCommand("find /var/log/history/" + serverVo.getUser() + " -type f -newermt '"+startTime+"' ! -newermt '"+endTime+"' 2>&1");
                }
                if(StringUtils.isEmpty(info)){
                    return reString;
                }
                String[] infos = info.split("\\n");
                // 裁剪用
                String adds="/var/log/history/" + serverVo.getUser()+"/";
                for(String infoString:infos){
                    String split = infoString.split(adds)[1];
                    reString.add(split);
                }
                // 退出Exec方式登录
                jSchUtil.logoutExec();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        Collections.sort(reString, Collections.reverseOrder());
        return reString;
    }
}
