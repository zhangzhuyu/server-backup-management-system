package com.ly.cloud.backup.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ly.cloud.backup.common.WebResponse;
import com.ly.cloud.backup.po.LyDbBackupHistoryRecordPo;
import com.ly.cloud.backup.service.LyDbBackupHistoryRecordService;
import com.ly.cloud.backup.util.DeptIdUtil;
import com.ly.cloud.backup.vo.BackupDirectoryTreeVo;
import com.ly.cloud.backup.vo.LyDbBackupHistoryDetailsVo;
import com.ly.cloud.backup.vo.LyDbBackupLevitatedSphereVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.codec.binary.Base64;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "数据备份_历史备份相关接口API")
@RequestMapping("/backup-history")
public class BackupHistoryController {

    private static final Logger logger = LoggerFactory.getLogger(BackupHistoryController.class);

    @Autowired
    private LyDbBackupHistoryRecordService lyDbBackupHistoryRecordService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private HttpServletRequest request;

    @ApiOperation(value = "备份历史_获取数据备份树" , notes = "备份历史_获取数据备份树" , httpMethod = "GET")
    @RequestMapping(value = "/find-backup-tree", method= RequestMethod.GET)
    public WebResponse<List<BackupDirectoryTreeVo>> findBackupTree()  {
        try {
            return new WebResponse<List<BackupDirectoryTreeVo>>().success(lyDbBackupHistoryRecordService.getTypeSourceWayList());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<List<BackupDirectoryTreeVo>>().failure("数据历史备份数获取失败");
        }
    }

    @ApiOperation(value = "备份历史_列表" , notes = "备份历史_列表" , httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, paramType = "query", dataType = "Integer", value = "当前页", example = "1"),
            @ApiImplicitParam(name = "pageSize", required = true, paramType = "query", dataType = "Integer", value = "页面大小", example = "15"),
            @ApiImplicitParam(name = "title", paramType = "query", dataType = "String", value = "搜索框搜索内容", example = "备份"),
            @ApiImplicitParam(name = "value", paramType = "query", dataType = "String", value = "左边目录树id", example = "1001"),

    })
    @RequestMapping(value = "/backup-history/list", method= RequestMethod.GET)
    public WebResponse<IPage<LyDbBackupHistoryRecordPo>> queryBackupHistoryList(@RequestParam("page") Integer page,
                                                                                @RequestParam("pageSize") Integer pageSize,
                                                                                @RequestParam(value = "title", required = false) String title,
                                                                                @RequestParam(value = "totalMethod", required = false) String totalMethod,
                                                                                @RequestParam(value = "value", required = false) String value,
                                                                                @RequestParam(value = "strategyId", required = false) String strategyId
    ) {
        try {
            String authDeptId = DeptIdUtil.deptId(request);
            IPage<LyDbBackupHistoryRecordPo> returnPage = lyDbBackupHistoryRecordService.queryBackupHistoryList(page,pageSize,title,totalMethod,value,strategyId,authDeptId);
            return new WebResponse<IPage<LyDbBackupHistoryRecordPo>>().success(returnPage);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<IPage<LyDbBackupHistoryRecordPo>>().failure("查询备份历史列表失败");
        }
    }


    @ApiOperation(value = "备份历史_备份文件下载", notes = "下载数据备份文件", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "strategyId", paramType = "query", dataType = "String", value = "策略id，传入策略id会下载最新的备份文件(策略与备份历史记录id同时传入时，会下载记录对应的备份文件)"),
            @ApiImplicitParam(name = "id", paramType = "query", dataType = "String", value = "备份历史记录id，备份历史记录id，会准确下载对应的备份文件"),
    })
    @RequestMapping(value = "/download-backup-file", method = RequestMethod.GET)
    public void downloadBackupFile(
            @RequestParam(value = "strategyId", required = false) String strategyId,
            @RequestParam(value = "id", required = false) String id,HttpServletResponse response) {
        try {
            logger.info("Received download request for server ID: {}", id);
            lyDbBackupHistoryRecordService.downloadBackupFile(id, response);
            logger.info("Download request for server ID {} completed successfully.", id);
        } catch (Exception e) {
            logger.error("Error during download from server ID {}: {}", id, e.getMessage(), e);
            // 尝试设置错误状态码，但如果 Header 已发送可能无效
            if (!response.isCommitted()) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                try {
                    // 尝试写入错误信息，但这可能被浏览器忽略
                    response.getWriter().write("Error during file download: " + e.getMessage());
                } catch (IOException ioException) {
                    logger.error("Failed to write error message to response", ioException);
                }
            }
            // 这里不能再抛出异常，因为 response 可能已经在使用
        }
    }

    /**
     * 根据备份策略id查询备份历史记录
     *
     * @return
     */
    @ApiOperation(value = "根据备份策略id查询备份历史记录（用在备份策略详情模块中）" , notes = "根据备份策略id查询备份历史记录（用在备份策略详情模块中）" , httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, paramType = "query", dataType = "Integer", value = "当前页", example = "1"),
            @ApiImplicitParam(name = "pageSize", required = true, paramType = "query", dataType = "Integer", value = "页面大小", example = "15"),
            @ApiImplicitParam(name = "title", paramType = "query", dataType = "String", value = "搜索框搜索内容", example = "备份"),
    })
    @RequestMapping(value = "/backup-history/get/{id}", method=RequestMethod.GET)
    public WebResponse<IPage<LyDbBackupHistoryRecordPo>> get(
            @PathVariable("id") String id,
            @RequestParam("page") Integer page,
            @RequestParam("pageSize") Integer pageSize,
            @RequestParam(value = "title", required = false) String title) {
        try {
            String authDeptId=DeptIdUtil.deptId(request);
            return new WebResponse<IPage<LyDbBackupHistoryRecordPo>>().success(lyDbBackupHistoryRecordService.get(page, pageSize, id, title,authDeptId));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<IPage<LyDbBackupHistoryRecordPo>>().failure(e.getMessage());
        }
    }


    /**
     * 根据备份策略id查询备份历史记录
     *
     * @return
     */
    @ApiOperation(value = "根据历史备份记录id查询备份详情" , notes = "根据历史备份记录id查询备份详情" , httpMethod = "GET")
    @RequestMapping(value = "/backup-history/details/{id}", method=RequestMethod.GET)
    public WebResponse<LyDbBackupHistoryDetailsVo> details(@PathVariable("id") String id) {
        try {
            return new WebResponse<LyDbBackupHistoryDetailsVo>().success(lyDbBackupHistoryRecordService.details(id));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<LyDbBackupHistoryDetailsVo>().failure(e.getMessage());
        }
    }


    /**
     * 查询当天备份详情，用于悬浮球统计
     * @return
     */
    @ApiOperation(value = "查询当天备份详情，用于悬浮球统计" , notes = "查询当天备份详情，用于悬浮球统计" , httpMethod = "GET")
    @RequestMapping(value = "/backup-history/levitated-sphere/details", method=RequestMethod.GET)
    public WebResponse<LyDbBackupLevitatedSphereVo> levitatedSphereDetails() {
        try {
            return new WebResponse<LyDbBackupLevitatedSphereVo>().success(lyDbBackupHistoryRecordService.levitatedSphereDetails());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<LyDbBackupLevitatedSphereVo>().failure(e.getMessage());
        }
    }


}
