package com.ly.cloud.backup.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ly.cloud.auth.util.StringUtils;
import com.ly.cloud.backup.common.WebResponse;
import com.ly.cloud.backup.dto.IdDto;
import com.ly.cloud.backup.dto.LyDbBackupStrategyRecordDto;
import com.ly.cloud.backup.dto.UrlTestDto;
import com.ly.cloud.backup.po.BackupManagementPo;
import com.ly.cloud.backup.util.DeptIdUtil;
import com.ly.cloud.backup.vo.BackupManagementVo;
import com.ly.cloud.backup.vo.LyDbBackupJournalVo;
import com.ly.cloud.backup.vo.LyDbBackupStrategyRecordVo;
import com.ly.cloud.backup.service.LyDbBackupStrategyRecordService;
import com.ly.cloud.backup.vo.SelectVo;
import com.ly.cloud.quartz.util.JSchConnectionPoolUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@Api(tags = "数据备份_数据库管理_数据库信息相关接口API")
@RequestMapping("/backup")
public class BackupDisplayController {

    private static final Logger logger = LoggerFactory.getLogger(BackupDisplayController.class);

    @Autowired
    private LyDbBackupStrategyRecordService lyDbBackupStrategyRecordService;

    @Autowired
    private JSchConnectionPoolUtil poolUtil;

    @Autowired
    private HttpServletRequest request;

    /**
     * 条件查询数据库备份策略信息
     *
     * @return 数据库备份策略信息
     * @author: zhangzhuyu
     */

    @ApiOperation(value = "备份策略列表" , notes = "备份策略列表" , httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, paramType = "query", dataType = "Integer", value = "当前页", example = "1"),
            @ApiImplicitParam(name = "pageSize", required = true, paramType = "query", dataType = "Integer", value = "页面大小", example = "15"),
            @ApiImplicitParam(name = "title", paramType = "query", dataType = "String", value = "搜索框搜索内容", example = "备份"),
            @ApiImplicitParam(name = "dataSourceType", paramType = "query", dataType = "String", value = "数据库类型筛选", example = "1"),
            @ApiImplicitParam(name = "backupWay", paramType = "query", dataType = "String", value = "备份方式筛选", example = "1"),
            @ApiImplicitParam(name = "totalMethod", paramType = "query", dataType = "String", value = "备份方法筛选", example = "1"),
    })
    @RequestMapping(value = "/list", method= RequestMethod.GET)
    public WebResponse<IPage<LyDbBackupStrategyRecordVo>> query(@RequestParam("page") Integer page,
                                                               @RequestParam("pageSize") Integer pageSize,
                                                               @RequestParam(value = "title", required = false) String title,
                                                               @RequestParam(value = "dataSourceType", required = false) List<String> dataSourceType,
                                                               @RequestParam(value = "backupWay", required = false) List<String> backupWay,
                                                               @RequestParam(value = "totalMethod", required = false) List<String> totalMethod
    ) {
        try {
            String authDeptId = DeptIdUtil.deptId(request);
            IPage<LyDbBackupStrategyRecordVo> returnPage = lyDbBackupStrategyRecordService.selectPageLike(page, pageSize, title, dataSourceType, backupWay, totalMethod,authDeptId);
            return new WebResponse<IPage<LyDbBackupStrategyRecordVo>>().success(returnPage);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<IPage<LyDbBackupStrategyRecordVo>>().failure("查询信息失败");
        }
    }


    /**
     * 添加数据库备份策略信息
     *
     * @return 操作是否成功
     * @author: zhangzhuyu
     */
    @ApiOperation(value = "添加备份策略" , notes = "添加备份策略" , httpMethod = "POST")
    //post请求不能加@ApiImplicitParams，否则前端显示不了example
    /*@ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", name = "lyDbBackupStrategyRecordDto", value = "数据源", required = true, dataType = "lyDbBackupStrategyRecordDto")
    })*/
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public WebResponse<String> insert(HttpServletRequest request, @RequestBody LyDbBackupStrategyRecordDto lyDbBackupStrategyRecordDto) {
        try {
            if(lyDbBackupStrategyRecordDto.getAuthDeptIds()!=null && !lyDbBackupStrategyRecordDto.getAuthDeptIds().isEmpty()){
                lyDbBackupStrategyRecordDto.setAuthDeptId(DeptIdUtil.join(",",lyDbBackupStrategyRecordDto.getAuthDeptIds()));
            }
            lyDbBackupStrategyRecordService.insert(request, lyDbBackupStrategyRecordDto);
            return new WebResponse<String>().success("");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<String>().failure(e.getMessage());
        }
    }


    /**
     * 更新数据库备份策略信息
     *
     * @return 操作是否成功
     * @author: zhangzhuhyu
     */
    @ApiOperation(value = "更新备份策略" , notes = "更新备份策略" , httpMethod = "POST")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public WebResponse<?> update(@RequestBody LyDbBackupStrategyRecordDto lyDbBackupStrategyRecordDto) {
        try {
            if(lyDbBackupStrategyRecordDto.getAuthDeptIds()!=null && !lyDbBackupStrategyRecordDto.getAuthDeptIds().isEmpty()){
                lyDbBackupStrategyRecordDto.setAuthDeptId(DeptIdUtil.join(",",lyDbBackupStrategyRecordDto.getAuthDeptIds()));
            }
            lyDbBackupStrategyRecordService.updateSource(lyDbBackupStrategyRecordDto);
            return new WebResponse<Object>().success("");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Object>().failure("更新备份策略信息失败");
        }
    }


    /**
     * 批量删除数据库备份策略信息
     *
     * @return 操作是否成功
     * @author: zhangzhuhyu
     */
    @ApiOperation(value = "删除备份策略" , notes = "删除备份策略" , httpMethod = "POST")
    @RequestMapping(value="/delete", method = RequestMethod.POST)
    public WebResponse<Object> deleteByMulti(@RequestBody List<Long> ids) {
        try {
            lyDbBackupStrategyRecordService.deleteByMulti(ids);
            return new WebResponse<Object>().success("");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Object>().failure("删除备份策略失败");
        }
    }


    /*备份类型的下拉框*/
    @ApiOperation(value = "备份类型的下拉框" , notes = "备份类型的下拉框" , httpMethod = "GET")
    @RequestMapping(value="/getBackupTypeList", method = RequestMethod.GET)
    public WebResponse<List<SelectVo>> getBackupTypeList(){
        try {
            return new WebResponse<List<SelectVo>>().success(lyDbBackupStrategyRecordService.getBackupTypeList());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<List<SelectVo>>().failure("下拉框信息获取失败");
        }
    }


    /*数据源类型的下拉框*/
    @ApiOperation(value = "数据源类型的下拉框" , notes = "数据源类型的下拉框" , httpMethod = "GET")
    @RequestMapping(value="/getDataBaseList", method = RequestMethod.GET)
    public WebResponse<List<SelectVo>> getDataBaseList(){
        try {
            return new WebResponse<List<SelectVo>>().success(lyDbBackupStrategyRecordService.getDataBaseList());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<List<SelectVo>>().failure("下拉框信息获取失败");
        }
    }


    /*数据源类型的下拉框*/
    @ApiOperation(value = "执行模式的下拉框" , notes = "执行模式的下拉框" , httpMethod = "GET")
    @RequestMapping(value="/getTaskModeList", method = RequestMethod.GET)
    public WebResponse<List<SelectVo>> getTaskModeList(){
        try {
            return new WebResponse<List<SelectVo>>().success(lyDbBackupStrategyRecordService.getTaskModeList());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<List<SelectVo>>().failure("执行模式的下拉框信息获取失败");
        }
    }


    /*备份目标url的下拉框*/
    @ApiOperation(value = "备份目标url的下拉框" , notes = "备份目标url的下拉框" , httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sourceType", required = false, paramType = "query", dataType = "Integer", value = "数据源类型，选择服务器备份时不需要此参数"),
            @ApiImplicitParam(name = "backupWay", required = true, paramType = "query", dataType = "Integer", value = "备份方式"),
            @ApiImplicitParam(name = "backupMethod", required = false, paramType = "query", dataType = "String", value = "服务器备份方法，选择数据库备份时不需要此参数")
    })
    @RequestMapping(value="/getBackupTargetList", method = RequestMethod.GET)
    public WebResponse<List<SelectVo>> getBackupTargetList(@RequestParam(value = "sourceType", required = false) Integer sourceType,@RequestParam Integer backupWay,@RequestParam(value = "backupMethod", required = false) String backupMethod){
        try {
            return new WebResponse<List<SelectVo>>().success(lyDbBackupStrategyRecordService.getBackupTargetList(sourceType,backupWay,backupMethod));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<List<SelectVo>>().failure(e.getMessage());
        }
    }


    /*数据库url连接测试，返回1为成功*/
    @ApiOperation(value = "数据库url连接测试" , notes = "数据库url连接测试" , httpMethod = "POST")
    @RequestMapping(value="/urlConnectTest", method = RequestMethod.POST)
    public WebResponse<List<SelectVo>> testUrlConnect(@RequestBody UrlTestDto urlTestDto){
        List<String> urlList = urlTestDto.getUrlList();
        Integer sourceType = urlTestDto.getSourceType();
        Integer backupWay = urlTestDto.getBackupWay();
        Integer backupMethod = urlTestDto.getBackupMethod();
        List<String> backupTarget = urlTestDto.getBackupTarget();
        try {
            return new WebResponse<List<SelectVo>>().success(lyDbBackupStrategyRecordService.testUrlConnect(backupWay, sourceType, backupMethod, urlList, backupTarget));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<List<SelectVo>>().failure(e.getMessage());
        }
    }

    /*选择备份的数据库下拉框*/
    //sourceType数据源类型
    @ApiOperation(value = "备份数据库下拉框" , notes = "备份数据库下拉框" , httpMethod = "GET")
    @RequestMapping(value="/selectDatabases", method = RequestMethod.GET)
    public WebResponse<List<String>> selectDatabases(@RequestParam Integer sourceType,@RequestParam String id){
        try {
            return new WebResponse<List<String>>().success(lyDbBackupStrategyRecordService.selectDatabases(sourceType,id));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<List<String>>().failure("url连接不成功");
        }
    }


    /*选择备份的表格下拉框*/
    //sourceType数据源类型
    @ApiImplicitParams({
            @ApiImplicitParam(name = "url", required = true, paramType = "query", dataType = "String", value = "url"),
    })
    @ApiOperation(value = "备份表格下拉框" , notes = "备份表格下拉框" , httpMethod = "GET")
    @RequestMapping(value="/selectTables", method = RequestMethod.GET)
    public WebResponse<List<String>> selectTables(@RequestParam String url){
        try {
            return new WebResponse<List<String>>().success(lyDbBackupStrategyRecordService.selectTables(url));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<List<String>>().failure(e.getMessage());
        }
    }


    /*启用数据备份按钮*/
    @ApiOperation(value = "启用数据库备份按钮" , notes = "启用数据库备份按钮" , httpMethod = "POST")
    @RequestMapping(value="/startBackup", method = RequestMethod.POST)
    public WebResponse<JSONObject> startBackup(@RequestBody IdDto idDto){
        try {
            return new WebResponse<JSONObject>().success(lyDbBackupStrategyRecordService.startBackup(idDto.getId()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<JSONObject>().failure(e.getMessage());
        }
    }


    /*立即启用数据备份按钮*/
    @ApiOperation(value = "立即启动备份" , notes = "立即启动备份" , httpMethod = "POST")
    @RequestMapping(value="/backup", method = RequestMethod.POST)
    public WebResponse<JSONObject> backup(@RequestBody IdDto idDto){
        try {
            return new WebResponse<JSONObject>().success(lyDbBackupStrategyRecordService.backup(idDto.getId()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<JSONObject>().failure(e.getMessage());
        }
    }


    /*停止数据备份按钮*/
    //传的是历史id
    @ApiOperation(value = "立即停止备份" , notes = "立即停止备份" , httpMethod = "POST")
    @RequestMapping(value="/stopBackup", method = RequestMethod.POST)
    public WebResponse<String> stopBackup(@RequestBody(required = false) List<Long> list){
        try {
            lyDbBackupStrategyRecordService.stopBackup(list);
            return new WebResponse<String>().success("");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<String>().failure(e.getMessage());
        }
    }


    /*重新数据备份按钮*/
    //传的是历史id
    @ApiOperation(value = "重新数据备份" , notes = "重新数据备份" , httpMethod = "POST")
    @RequestMapping(value="/restartBackup", method = RequestMethod.POST)
    public WebResponse<String> restartBackup(@RequestBody(required = false) List<String> list){
        try {
            lyDbBackupStrategyRecordService.restartBackup(list);
            return new WebResponse<String>().success("");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<String>().failure(e.getMessage());
        }
    }


    /*查看日志*/
    //传的是历史id
    @ApiOperation(value = "查看日志" , notes = "查看日志" , httpMethod = "GET")
    @RequestMapping(value="/checkJournal", method = RequestMethod.GET)
    public WebResponse<LyDbBackupJournalVo> checkJournal(Long id){
        try {
            return new WebResponse<LyDbBackupJournalVo>().success(lyDbBackupStrategyRecordService.checkJournal(id));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<LyDbBackupJournalVo>().failure("查看日志失败");
        }
    }

    /*下载日志*/
    //传的是历史id
    @ApiOperation(value = "下载日志" , notes = "下载日志" , httpMethod = "POST")
    @RequestMapping(value="/downloadJournal", method = RequestMethod.POST)
    public WebResponse<String> downloadJournal(HttpServletResponse response,@RequestBody IdDto idDto){
        try {
            lyDbBackupStrategyRecordService.downloadJournal(response, Long.valueOf(idDto.getId()));
            return new WebResponse<String>().success("");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<String>().failure("查看日志失败");
        }
    }


    @ApiOperation(value = "http测试备份" , notes = "http测试备份" , httpMethod = "POST")
    @RequestMapping(value="/httpBackup", method = RequestMethod.POST)
    public WebResponse<String> httpBackup(HttpServletResponse response,@RequestBody IdDto idDto){
        try {
            lyDbBackupStrategyRecordService.httpBackup(response, Long.valueOf(idDto.getId()));
            return new WebResponse<String>().success("");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<String>().failure("查看日志失败");
        }
    }


    @ApiOperation(value = "目标服务器选择" , notes = "目标服务器选择" , httpMethod = "GET")
    @RequestMapping(value="/targetServer", method = RequestMethod.GET)
    public WebResponse<List<BackupManagementPo>> targetServer(){
        try {
            List<BackupManagementPo> backupManagementPoList = lyDbBackupStrategyRecordService.targetServer();
            return new WebResponse<List<BackupManagementPo>>().success(backupManagementPoList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<List<BackupManagementPo>>().failure("目标服务器选择查询失败");
        }
    }




////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 服务器工作目录 http路径测试连接
     *
     * @return 操作是否成功
     * @author: zhangzhuyu
     */
    @ApiOperation(value = "服务器工作目录http路径测试连接" , notes = "服务器工作目录http路径测试连接" , httpMethod = "GET")
    //@RequestMapping(value = "/catalogConnectionTest", method = RequestMethod.GET)
    public WebResponse<Boolean> catalogConnectionTest(@RequestParam String url) {
        try {
            Boolean b = false;
            if (!StringUtils.isEmpty(url)) {//判空
                b = lyDbBackupStrategyRecordService.catalogConnectionTest(url);
            }
            return new WebResponse<Boolean>().success(b);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Boolean>().failure("http路径测试连接不成功");
        }
    }


    /**
     * 服务器工作目录 不同备份方式启动备份（http、ssh、ftp、cifs）
     *
     * @return 操作是否成功
     * @author: zhangzhuyu
     */
    //@ApiOperation(value = "启动服务器工作目录备份" , notes = "启动服务器工作目录备份" , httpMethod = "GET")
    //@RequestMapping(value = "/catalogueStartBackup", method = RequestMethod.GET)
//    public WebResponse<Integer> catalogueStartBackup(@RequestParam Long id) {
//        try {
//            return new WebResponse<Integer>().success(lyDbBackupStrategyRecordService.catalogStartBackup(id));
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            return new WebResponse<Integer>().failure("启动备份失败");
//        }
//    }
}










































