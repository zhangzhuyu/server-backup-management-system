package com.ly.cloud.backup.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.cloud.backup.po.ServerPo;
import com.ly.cloud.backup.po.SystemSettingPo;

import java.util.Map;

public interface SystemSettingService extends IService<SystemSettingPo> {
    /**
     * 将所有配置项作为 Map<String, String> 返回
     * @return key-value 形式的配置 Map
     */
    Map<String, String> getAllSettingsAsMap();

    /**
     * 从 Map 中保存所有配置项
     * @param settingsMap 包含配置的 Map
     */
    void saveAllSettingsFromMap(Map<String, String> settingsMap);

    /**
     * 根据 key 获取单个配置值
     * @param key 配置键
     * @return 配置值，如果不存在则返回 null
     */
    String getSettingValue(String key);
}
