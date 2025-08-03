package com.ly.cloud.backup.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.cloud.backup.dto.LyDbBackupStrategyRecordDto;
import com.ly.cloud.backup.po.BackupManagementPo;
import com.ly.cloud.backup.vo.BackupManagementVo;
import com.ly.cloud.backup.vo.LyDbBackupJournalVo;
import com.ly.cloud.backup.vo.LyDbBackupStrategyRecordVo;
import com.ly.cloud.backup.po.LyDbBackupStrategyRecordPo;
import com.ly.cloud.backup.vo.SelectVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 数据库备份策略信息表
 *
 * @author zhangzhuyu
 */
public interface LyDbBackupStrategyRecordService extends IService<LyDbBackupStrategyRecordPo> {

    /**
     * 条件查询数据库备份策略信息
     *
     * @param page     : 页码
     * @param pageSize    : 页码大小
     * @param title : 查询参数
     * @param dataSourceType : 数据库类型
     * @param backupWay : 备份方式
     * @param authDeptId
     * @author: zhangzhuyu
     * @date: 2023/4/24 3:00
     * @return: IPage<LyDbBackupStrategyRecordVo> : 数据库备份策略信息
     */
    IPage<LyDbBackupStrategyRecordVo> selectPageLike(Integer page, Integer pageSize, String title, List<String> dataSourceType, List<String> backupWay, List<String> totalMethod, String authDeptId);

    /*添加数据库信息表*/
    void insert(HttpServletRequest request, LyDbBackupStrategyRecordDto lyDbBackupStrategyRecordDto);

    /*更新数据库信息表*/
    void updateSource(LyDbBackupStrategyRecordDto lyDbBackupStrategyRecordDto);

    /*批量删除数据库信息*/
    void deleteByMulti(List<Long> ids);

    /*备份类型的下拉框*/
    List<SelectVo> getBackupTypeList();

    /*数据源类型的下拉框*/
    List<SelectVo> getDataBaseList();

    /*备份目标url的下拉框*/
    List<SelectVo> getBackupTargetList(Integer sourceType,Integer backupWay, String backupMethod);

    /*执行模式的下拉框*/
    List<SelectVo> getTaskModeList();

    /*数据库url连接测试*/
    List<SelectVo> testUrlConnect(Integer backupWay, Integer sourceType, Integer backupMethod, List<String> urlList, List<String> backupTarget);

    /*选择备份的数据库下拉框*/
    List<String> selectDatabases(Integer sourceType,String id);

    /*选择备份的表格下拉框*/
    List<String> selectTables(String url);

    /*启用数据备份按钮*/
    JSONObject startBackup(String id);

    /*服务器工作目录 http路径测试连接*/
    Boolean catalogConnectionTest(String url);

    /*立即启用数据备份按钮*/
    JSONObject backup(String id);

    /*停止数据备份按钮*/
    void stopBackup(List<Long> list);

    /*重新数据备份按钮*/
    void restartBackup(List<String> list);

    /*查看日志*/
    LyDbBackupJournalVo checkJournal(Long id);

    /*下载日志*/
    void downloadJournal(HttpServletResponse response, Long id);

    /*http测试备份*/
    void httpBackup(HttpServletResponse response, Long id);

    List<BackupManagementPo> targetServer();

//    /*服务器工作目录备份，启用数据备份按钮*/
//    Integer catalogStartBackup(Long id);
}





























