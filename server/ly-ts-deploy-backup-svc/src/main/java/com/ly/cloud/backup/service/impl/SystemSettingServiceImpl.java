package com.ly.cloud.backup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.cloud.backup.mapper.SystemSettingMapper;
import com.ly.cloud.backup.po.SystemSettingPo;
import com.ly.cloud.backup.service.SystemSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SystemSettingServiceImpl extends ServiceImpl<SystemSettingMapper, SystemSettingPo> implements SystemSettingService {
    @Autowired
    private SystemSettingMapper systemSettingMapper;

    /**
     * 将所有配置项作为 Map<String, String> 返回
     */
    @Override
    public Map<String, String> getAllSettingsAsMap() {
        List<SystemSettingPo> settings = systemSettingMapper.selectList(null);
        // 使用 Java 8 Stream API 将 List<SystemSetting> 转换为 Map<String, String>
        return settings.stream()
                .collect(Collectors.toMap(SystemSettingPo::getSettingKey, SystemSettingPo::getSettingValue, (v1, v2) -> v2)); // 如果有重复的key，保留后者
    }

    /**
     * 从 Map 中保存所有配置项
     */
    @Override
    @Transactional // 确保操作的原子性
    public void saveAllSettingsFromMap(Map<String, String> settingsMap) {
        settingsMap.forEach((key, value) -> {
            // 尝试根据 key 查找现有配置
            LambdaQueryWrapper<SystemSettingPo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SystemSettingPo::getSettingKey, key);
            SystemSettingPo existingSetting = systemSettingMapper.selectOne(wrapper);

            if (existingSetting != null) {
                // 如果存在，则更新值
                existingSetting.setSettingValue(value);
                systemSettingMapper.updateById(existingSetting);
            } else {
                // 如果不存在，则插入新记录
                SystemSettingPo newSetting = new SystemSettingPo();
                newSetting.setSettingKey(key);
                newSetting.setSettingValue(value);
                // 你可以根据需要设置 description
                newSetting.setDescription("由系统自动添加");
                systemSettingMapper.insert(newSetting);
            }
        });
    }

    /**
     * 根据 key 获取单个配置值
     */
    @Override
    public String getSettingValue(String key) {
        LambdaQueryWrapper<SystemSettingPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemSettingPo::getSettingKey, key);
        SystemSettingPo setting = systemSettingMapper.selectOne(wrapper);
        return setting != null ? setting.getSettingValue() : null;
    }
}
