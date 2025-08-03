package com.ly.cloud.backup.controller;

import com.ly.cloud.backup.common.WebResponse;
import com.ly.cloud.backup.service.SystemSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/system-settings")
public class SystemSettingController {

    @Autowired
    private SystemSettingService systemSettingService;

    // 获取所有系统设置
    @GetMapping
    public WebResponse<Map<String, String>> getAllSettings() {
        return new WebResponse<Map<String, String>>().success(systemSettingService.getAllSettingsAsMap());
    }

    // 保存（或更新）所有系统设置
    @PostMapping
    public WebResponse<Object> saveAllSettings(@RequestBody Map<String, String> settings) {
        systemSettingService.saveAllSettingsFromMap(settings);
        return new WebResponse<>().success("配置已保存");
    }
}
