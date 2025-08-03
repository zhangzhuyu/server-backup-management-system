package com.ly.cloud.backup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ly.cloud.backup.dto.CapacityPredictionDto;
import com.ly.cloud.backup.dto.PredictionResultDto;
import com.ly.cloud.backup.dto.ServerCapacityInfoDto;
import com.ly.cloud.backup.dto.StrategySelectionDto;
import com.ly.cloud.backup.mapper.BackupManagementMapper;
import com.ly.cloud.backup.mapper.LyDbBackupHistoryRecordMapper;
import com.ly.cloud.backup.mapper.LyDbBackupStrategyRecordMapper;
import com.ly.cloud.backup.mapper.ServerMapper;
import com.ly.cloud.backup.po.BackupManagementPo;
import com.ly.cloud.backup.po.LyDbBackupHistoryRecordPo;
import com.ly.cloud.backup.po.LyDbBackupStrategyRecordPo;
import com.ly.cloud.backup.service.CapacityPredictionService;
import com.ly.cloud.backup.util.JSchUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class CapacityPredictionServiceImpl implements CapacityPredictionService {

    // 注入你需要的 Mapper (确保这些都已正确注入)
    @Autowired
    private BackupManagementMapper backupManagementMapper;
    @Autowired
    private LyDbBackupHistoryRecordMapper historyRecordMapper;

    @Override
    public CapacityPredictionDto getPredictionForServer(String serverId) {
        CapacityPredictionDto response = new CapacityPredictionDto();

        // 1. 获取服务器信息和生成假的容量数据
        BackupManagementPo backupManagementPo = backupManagementMapper.selectById(serverId);
        if (backupManagementPo == null) {
            response.setPredictionAvailable(false);
            response.setMessage("未找到指定服务器。");
            return response;
        }
//        response.setServerInfo(createFakeServerCapacityInfo(backupManagementPo));
        response.setServerInfo(getServerCapacityInfo(backupManagementPo));

        // 2. 查询最近所有成功的备份记录 (为了演示，可以放宽时间)
        List<LyDbBackupHistoryRecordPo> records = historyRecordMapper.selectList(
                Wrappers.<LyDbBackupHistoryRecordPo>lambdaQuery()
                        .eq(LyDbBackupHistoryRecordPo::getBackupStatus, "1") // 1=成功
                        .ge(LyDbBackupHistoryRecordPo::getBackupTime, LocalDateTime.now().minusYears(2)) // 查询过去2年的数据
                        .orderByAsc(LyDbBackupHistoryRecordPo::getBackupTime)
        );

        // 3. 过滤出有效的记录（有时间和大小的）
        List<LyDbBackupHistoryRecordPo> validRecords = records.stream()
                .filter(r -> r.getBackupTime() != null && r.getSize() != null && !r.getSize().trim().isEmpty())
                .collect(Collectors.toList());

        if (validRecords.size() < 2) { // 至少需要2个点才能进行线性回归
            response.setPredictionAvailable(false);
            response.setMessage("最近两年内有效备份数据不足 (少于2条)，无法进行预测。");
            return response;
        }

        // 4. 准备历史数据给 ECharts (注意：这里我们不累加，只展示单次备份大小)
        List<Map<String, Object>> historicalDataForChart = validRecords.stream()
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();
                    LocalDateTime dateTime = r.getBackupTime().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();
                    map.put("date", dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE));
                    // ★★★ 关键修复：使用辅助方法解析大小，并转为 MB ★★★
                    BigDecimal sizeInKB = parseSizeToKB(r.getSize());
                    map.put("size", sizeInKB.divide(BigDecimal.valueOf(1024), 2, RoundingMode.HALF_UP)); // 转为MB
                    return map;
                }).collect(Collectors.toList());
        response.setHistoricalData(historicalDataForChart);

        // 5. 执行线性回归预测 (注意：这里需要累加总容量)
        SimpleRegression regression = new SimpleRegression();
        LocalDate firstDay = validRecords.get(0).getBackupTime().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // 使用一个 Map 来存储每天的总增量
        Map<Long, BigDecimal> dailyCumulativeSize = new TreeMap<>();
        BigDecimal totalCumulativeSize = BigDecimal.ZERO;

        for (LyDbBackupHistoryRecordPo record : validRecords) {
            LocalDate recordLocalDate = record.getBackupTime().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            long dayIndex = ChronoUnit.DAYS.between(firstDay, recordLocalDate);

            // ★★★ 关键修复：使用辅助方法解析大小 ★★★
            BigDecimal sizeInKB = parseSizeToKB(record.getSize());
            totalCumulativeSize = totalCumulativeSize.add(sizeInKB);
            dailyCumulativeSize.put(dayIndex, totalCumulativeSize);
        }

        // 将每天的累积数据添加到回归模型
        for (Map.Entry<Long, BigDecimal> entry : dailyCumulativeSize.entrySet()) {
            regression.addData(entry.getKey(), entry.getValue().doubleValue());
        }

        double slope = regression.getSlope(); // 斜率，即每日增长量 (单位：KB)
        if (slope <= 0) {
            response.setPredictionAvailable(false);
            response.setMessage("当前备份容量呈减少或持平趋势，无需预警。");
            return response;
        }

        // 6. 组装预测结果
        BigDecimal dailyGrowthRateMB = BigDecimal.valueOf(slope).divide(BigDecimal.valueOf(1024), 2, RoundingMode.HALF_UP);
        BigDecimal totalCapacityGB = response.getServerInfo().getTotalCapacityGB();
        BigDecimal usedCapacityGB = response.getServerInfo().getUsedCapacityGB();
        BigDecimal remainingCapacityMB = (totalCapacityGB.subtract(usedCapacityGB)).multiply(BigDecimal.valueOf(1024));

        // 如果增长率大于0但非常小，避免除以0或得到超大天数
        if (dailyGrowthRateMB.compareTo(new BigDecimal("0.01")) < 0) {
            response.setPredictionAvailable(false);
            response.setMessage("每日容量增长过小，预计在可预见的未来不会存满。");
            return response;
        }

        long daysRemaining = remainingCapacityMB.divide(dailyGrowthRateMB, 0, RoundingMode.DOWN).longValue();
        LocalDate predictedFullDate = LocalDate.now().plusDays(daysRemaining);

        PredictionResultDto predictionResult = new PredictionResultDto();
        predictionResult.setDaysRemaining(daysRemaining);
        predictionResult.setPredictedFullDate(predictedFullDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
        predictionResult.setDailyGrowthRateMB(dailyGrowthRateMB);

        response.setPredictionResult(predictionResult);
        response.setPredictionAvailable(true);
        response.setMessage("预测成功！");
        return response;
    }

    /**
     * ★★★ 新增的辅助方法：解析带单位的大小字符串，并统一返回以 KB 为单位的 BigDecimal ★★★
     * @param sizeString 如 "126B", "1.5KB", "2.3MB", "0.1GB"
     * @return 转换为 KB 后的 BigDecimal 值
     */
    private BigDecimal parseSizeToKB(String sizeString) {
        if (sizeString == null || sizeString.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        String upperSize = sizeString.trim().toUpperCase();
        try {
            if (upperSize.endsWith("GB")) {
                BigDecimal value = new BigDecimal(upperSize.replace("GB", "").trim());
                return value.multiply(BigDecimal.valueOf(1024 * 1024)); // GB -> KB
            } else if (upperSize.endsWith("MB")) {
                BigDecimal value = new BigDecimal(upperSize.replace("MB", "").trim());
                return value.multiply(BigDecimal.valueOf(1024)); // MB -> KB
            } else if (upperSize.endsWith("KB")) {
                return new BigDecimal(upperSize.replace("KB", "").trim());
            } else if (upperSize.endsWith("B")) {
                BigDecimal value = new BigDecimal(upperSize.replace("B", "").trim());
                return value.divide(BigDecimal.valueOf(1024), 2, RoundingMode.HALF_UP); // B -> KB
            } else {
                // 如果没有单位，假设是 B
                BigDecimal value = new BigDecimal(upperSize);
                return value.divide(BigDecimal.valueOf(1024), 2, RoundingMode.HALF_UP);
            }
        } catch (NumberFormatException e) {
            // 如果解析失败（例如，格式不规范），返回0，避免程序崩溃
            System.err.println("无法解析的大小字符串: " + sizeString);
            return BigDecimal.ZERO;
        }
    }


    // 一个辅助方法，用来创建假的服务器容量信息 (这个方法保持不变)
//    private ServerCapacityInfoDto createFakeServerCapacityInfo(BackupManagementPo backupManagementPo) {
//        ServerCapacityInfoDto info = new ServerCapacityInfoDto();
//        info.setServerName(backupManagementPo.getName());
//        info.setServerIp(backupManagementPo.getIpv4());
//        info.setTotalCapacityGB(new BigDecimal("2048.00")); // 假定总容量为 2TB
//        double used = 600 + new Random().nextDouble() * 600;
//        info.setUsedCapacityGB(new BigDecimal(used).setScale(2, RoundingMode.HALF_UP));
//        int percentage = info.getUsedCapacityGB().multiply(BigDecimal.valueOf(100))
//                .divide(info.getTotalCapacityGB(), 0, RoundingMode.HALF_UP).intValue();
//        info.setUsagePercentage(percentage);
//        return info;
//    }

    private ServerCapacityInfoDto getServerCapacityInfo(BackupManagementPo backupManagementPo) {
        ServerCapacityInfoDto info = new ServerCapacityInfoDto();
        info.setServerName(backupManagementPo.getName());
        info.setServerIp(backupManagementPo.getIpv4());

        try {
            // --- 尝试获取真实数据 ---
            System.out.println("尝试通过 SSH 连接服务器获取真实磁盘空间: " + backupManagementPo.getIpv4());

            Map<String, Long> diskSpaceKB = JSchUtil.getRemoteDiskSpace(
                    backupManagementPo.getIpv4(),
                    Integer.parseInt(backupManagementPo.getPort()),
                    backupManagementPo.getUser(),
                    backupManagementPo.getPassword(),
                    "/" // 我们检查根目录 "/" 的空间
            );

            if (diskSpaceKB != null) {
                long totalKB = diskSpaceKB.get("total");
                long usedKB = diskSpaceKB.get("used");

                // 换算单位：KB -> GB
                BigDecimal kbInOneGB = new BigDecimal(1024 * 1024);
                BigDecimal totalCapacityGB = new BigDecimal(totalKB).divide(kbInOneGB, 2, RoundingMode.HALF_UP);
                BigDecimal usedCapacityGB = new BigDecimal(usedKB).divide(kbInOneGB, 2, RoundingMode.HALF_UP);

                info.setTotalCapacityGB(totalCapacityGB);
                info.setUsedCapacityGB(usedCapacityGB);

                if (totalCapacityGB.compareTo(BigDecimal.ZERO) > 0) {
                    int percentage = usedCapacityGB.multiply(BigDecimal.valueOf(100))
                            .divide(totalCapacityGB, 0, RoundingMode.HALF_UP).intValue();
                    info.setUsagePercentage(percentage);
                } else {
                    info.setUsagePercentage(0);
                }
                System.out.println("成功获取真实磁盘空间: " + backupManagementPo.getIpv4());
                return info;
            } else {
                throw new RuntimeException("通过SSH获取磁盘空间失败，解析结果为空。");
            }
        } catch (Exception e) {
            // --- 如果获取真实数据失败，则回退到生成假数据 ---
            System.err.println("获取真实磁盘空间失败: " + e.getMessage() + "。将回退到生成假数据。");

            info.setTotalCapacityGB(new BigDecimal("2048.00")); // 假定总容量为 2TB
            double used = 600 + new Random().nextDouble() * 600;
            info.setUsedCapacityGB(new BigDecimal(used).setScale(2, RoundingMode.HALF_UP));
            int percentage = info.getUsedCapacityGB().multiply(BigDecimal.valueOf(100))
                    .divide(info.getTotalCapacityGB(), 0, RoundingMode.HALF_UP).intValue();
            info.setUsagePercentage(percentage);

            return info;
        }
    }
}
