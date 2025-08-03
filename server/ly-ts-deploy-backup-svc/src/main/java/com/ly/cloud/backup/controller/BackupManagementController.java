package com.ly.cloud.backup.controller;

import com.ly.cloud.backup.common.WebResponse;
import com.ly.cloud.backup.dto.BackupManagementDto;
import com.ly.cloud.backup.dto.ServerDto;
import com.ly.cloud.backup.po.BackupManagementPo;
import com.ly.cloud.backup.service.BackupManagementService;
import com.ly.cloud.backup.util.DeptIdUtil;
import com.ly.cloud.backup.vo.BackupManagementVo;
import com.ly.cloud.backup.vo.StrategicModeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Api(tags = "系统设置_备份管理相关接口API")
@RequestMapping("/backup-management")
public class BackupManagementController {

    private static final Logger logger = LoggerFactory.getLogger(BackupManagementController.class);

    @Resource
    private BackupManagementService backupManagementService;

    @Autowired
    private HttpServletRequest request;

    @ApiOperation(value = "备份管理_获取备份管理列表" , notes = "备份管理_获取备份管理列表" , httpMethod = "GET")
    @RequestMapping(value = "/list", method= RequestMethod.GET)
    public WebResponse<List<BackupManagementPo>> list(@RequestParam(value = "backupWay",required = false) String backupWay)  {
        try {
            String authDeptId = DeptIdUtil.deptId(request);
            return new WebResponse<List<BackupManagementPo>>().success(backupManagementService.getBackupManagementList(backupWay,authDeptId));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<List<BackupManagementPo>>().failure("获取备份管理列表失败");
        }
    }


    @ApiOperation(value = "备份管理_修改备份管理" , notes = "备份管理_修改备份管理" , httpMethod = "POST")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public WebResponse<?> update(@RequestBody BackupManagementDto backupManagementDto) {
        try {
            if(backupManagementDto.getAuthDeptIds()!=null && !backupManagementDto.getAuthDeptIds().isEmpty()){
                backupManagementDto.setAuthDeptId(DeptIdUtil.join(",",backupManagementDto.getAuthDeptIds()));
            }else {
                backupManagementDto.setAuthDeptId("-1");
            }
            backupManagementService.updateBackupManagement(backupManagementDto);
            return new WebResponse<Object>().success("");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Object>().failure("修改备份管理信息失败");
        }
    }

    @ApiOperation(value = "备份管理_获取客户端服务器下拉框" , notes = "备份管理_获取客户端服务器下拉框" , httpMethod = "GET")
    @RequestMapping(value = "/find-client-list", method= RequestMethod.GET)
    public WebResponse<List<StrategicModeVo>> findClientList(@RequestParam("totalMethod") String totalMethod)  {
        try {
            return new WebResponse<List<StrategicModeVo>>().success(backupManagementService.findClientList(totalMethod));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<List<StrategicModeVo>>().failure("获取客户端服务器下拉框失败");
        }
    }

    @ApiOperation(value = "备份管理_新增备份管理" , notes = "备份管理_新增备份管理" , httpMethod = "POST")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public WebResponse<?> insert(@RequestBody BackupManagementDto backupManagementDto) {
        try {
            backupManagementService.insertBackupManagement(backupManagementDto);
            return new WebResponse<Object>().success("");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Object>().failure("新增备份管理信息失败");
        }
    }

    @ApiOperation(value = "服务器信息表_测试连接" , notes = "服务器信息表_测试连接" , httpMethod = "POST")
    @RequestMapping(value = "/testConnection", method=RequestMethod.POST)
    public WebResponse<?> testConnection(@RequestBody BackupManagementDto backupManagementDto) {
        try {
            backupManagementService.testConnection(backupManagementDto);
            return new WebResponse<Object>().success("");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Object>().failure(e.getMessage());
        }
    }

    @ApiOperation(value = "服务器信息表_删除" , notes = "服务器信息表_删除" , httpMethod = "POST")
    @RequestMapping(value = "/delete", method=RequestMethod.POST)
    public WebResponse<?> delete(@RequestBody Long[] ids) {
        try {
            backupManagementService.delete(ids);
            return new WebResponse<Object>().success("");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Object>().failure(e.getMessage());
        }
    }

  /*  @ApiOperation(value = "备份管理_校验服务器参数" , notes = "备份管理_校验服务器参数" , httpMethod = "GET")
    @RequestMapping(value = "/check-correct", method= RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serverId", required = true, paramType = "query", dataType = "long", value = "服务id", example = "1"),
            @ApiImplicitParam(name = "dataSourceType", required = true, paramType = "query", dataType = "string", value = "数据源类型", example = "1"),
    })
    public WebResponse<String> Checkcorrect(@RequestParam("serverId") Long serverId, @RequestParam("dataSourceType") String dataSourceType)  {
        try {
            return new WebResponse<String>().success(backupManagementService.Checkcorrect(serverId, dataSourceType));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<String>().failure("获取客户端服务器下拉框失败");
        }
    }*/

}

