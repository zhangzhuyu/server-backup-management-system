package com.ly.cloud.backup.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ly.cloud.backup.common.WebResponse;
import com.ly.cloud.backup.dto.ServerDto;
import com.ly.cloud.backup.vo.ServerVo;
import com.ly.cloud.backup.service.ServerService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 服务器信息表
 * @author chenguoqing
 *
 */
@RestController
@Api(tags="资源管理_服务器管理_服务器信息表")
@RequestMapping("/server")
public class ServerController {
	
	private static final Logger logger = LoggerFactory.getLogger(ServerController.class);

    @Autowired
    private ServerService serverService;

    @Autowired
    private HttpServletRequest request;

	@ApiOperation(value = "服务器信息表_添加" , notes = "服务器信息表_添加" , httpMethod = "POST")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public WebResponse<?> create(@RequestBody ServerDto serverDto) {
		try {
			serverService.create(serverDto);
			return new WebResponse<Object>().success("");
		} catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Object>().failure(e.getMessage());
        }
    }

    @ApiOperation(value = "服务器信息表_分页查询" , notes = "服务器信息表_分页查询" , httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, paramType = "query", dataType = "Integer", value = "当前页", example = "1"),
            @ApiImplicitParam(name = "pageSize", required = true, paramType = "query", dataType = "Integer", value = "页面大小", example = "15"),
            @ApiImplicitParam(name = "content", paramType = "query", dataType = "String", value = "搜索框搜索内容", example = "测试"),
            @ApiImplicitParam(name = "affiliatedCompany", paramType = "query", dataType = "String", value = "分类", example = "0"),
    })
    @RequestMapping(value = "/query", method=RequestMethod.GET)
    public WebResponse<?> query(@RequestParam("page") Integer page, @RequestParam("pageSize") Integer pageSize,@RequestParam(value = "content", required = false) String content,@RequestParam(value = "affiliatedCompany", required = false) String affiliatedCompany) {
        try {
            IPage<ServerVo> Page = serverService.query(page,pageSize,content,affiliatedCompany);
			return new WebResponse<IPage<ServerVo>>().success(Page);
		} catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Object>().failure(e.getMessage());
        }
    }

    /**
     * 清空redis缓存
     */
    @ApiOperation(value = "服务器信息表_清空redis缓存",notes = "服务器信息表_清空redis缓存", httpMethod = "GET")
    @GetMapping(value = "/clear-redis-serverCache")
    public WebResponse<?> clearRedisServerCache(){
        try {
            Boolean serverMonitorResource = serverService.clearRedisServerCache();
            return new WebResponse<Boolean>().success(serverMonitorResource);
        } catch (Exception e) {
            return new WebResponse<>().failure(e.getMessage());
        }
    }

    @ApiOperation(value = "服务器信息表_更新" , notes = "服务器信息表_更新" , httpMethod = "POST")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public WebResponse<?> update(@RequestBody ServerDto serverDto) {
        try {
			serverService.update(serverDto);
			return new WebResponse<Object>().success("");
		} catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Object>().failure(e.getMessage());
        }
    }

    @ApiOperation(value = "服务器信息表_删除" , notes = "服务器信息表_删除" , httpMethod = "POST")
    @RequestMapping(value="/delete", method = RequestMethod.POST)
    public WebResponse<?> delete(@RequestBody Long[] ids) {
		try {
			serverService.delete(ids);
			return new WebResponse<Object>().success("");
		} catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Object>().failure(e.getMessage());
        }
    }

    @ApiOperation(value = "服务器信息表_关机" , notes = "服务器信息表_关机" , httpMethod = "POST")
    @RequestMapping(value = "/batchPowerOff", method=RequestMethod.POST)
    public WebResponse<?> powerOff(@RequestBody Long[] ids) {
		try {
            serverService.batchPowerOff(ids);
			return new WebResponse<Object>().success("");
		} catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Object>().failure(e.getMessage());
        }
    }

    @ApiOperation(value = "服务器信息表_同步系统时间" , notes = "服务器信息表_同步系统时间" , httpMethod = "POST")
    @RequestMapping(value = "/synchronization-time", method=RequestMethod.POST)
    public WebResponse<?> synchronizationTime(@RequestBody Long[] ids) {
        try {
            serverService.synchronizationTime(ids);
            return new WebResponse<Object>().success("");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Object>().failure(e.getMessage());
        }
    }

    @ApiOperation(value = "服务器信息表_关机" , notes = "服务器信息表_关机" , httpMethod = "POST")
    @RequestMapping(value = "/powerOff-dto", method=RequestMethod.POST)
    public WebResponse<?> powerOffDto(@RequestBody ServerDto serverDto) {
        try {
            serverService.powerOffDto(serverDto);
            return new WebResponse<Object>().success("");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Object>().failure(e.getMessage());
        }
    }

    @ApiOperation(value = "服务器信息表_重启" , notes = "服务器信息表_重启" , httpMethod = "POST")
    @RequestMapping(value = "/restart", method=RequestMethod.POST)
    public WebResponse<?> restart(@RequestBody Long[] ids) {
        try {
            serverService.restart(ids);
            return new WebResponse<Object>().success("");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Object>().failure(e.getMessage());
        }
    }

    @ApiOperation(value = "服务器信息表_重启" , notes = "服务器信息表_重启" , httpMethod = "POST")
    @RequestMapping(value = "/restart-dto", method=RequestMethod.POST)
    public WebResponse<?> restartDto(@RequestBody ServerDto serverDto) {
        try {
            serverService.restartDto(serverDto);
            return new WebResponse<Object>().success("");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Object>().failure(e.getMessage());
        }
    }

    @ApiOperation(value = "服务器信息表_测试连接" , notes = "服务器信息表_测试连接" , httpMethod = "POST")
    @RequestMapping(value = "/testConnection", method=RequestMethod.POST)
    public WebResponse<?> testConnection(@RequestBody(required = false) ServerDto serverDto) {
        try {
            Long id = serverDto.getId();
            serverService.testConnection(id);
            return new WebResponse<Object>().success("");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Object>().failure(e.getMessage());
        }
    }

    @ApiOperation(value = "服务器信息表_验证服务器账号密码" , notes = "服务器信息表_验证服务器账号密码" , httpMethod = "POST")
    @RequestMapping(value = "/test-user", method=RequestMethod.POST)
    public WebResponse<?> testUser(@RequestBody ServerDto serverDto) {
        try {
            serverService.testUser(serverDto);
            return new WebResponse<Object>().success("");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Object>().failure(e.getMessage());
        }
    }

    @ApiOperation(value = "服务器信息表_服务器日志下拉框" , notes = "服务器信息表_服务器日志下拉框" , httpMethod = "GET")
    @RequestMapping(value = "/getLogInformation", method=RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", required = true, paramType = "query", dataType = "Long", value = "服务器id", example = "1"),
            @ApiImplicitParam(name = "startTime", paramType = "query", dataType = "String", value = "开始时间", example = "2023-02-01 00:00:00"),
            @ApiImplicitParam(name = "endTime", paramType = "query", dataType = "String", value = "结束时间", example = "2023-02-30 00:00:00"),
    })
    public WebResponse<?> getLogInformation(@RequestParam("id") Long id, @RequestParam(value = "startTime", required = false) String startTime, @RequestParam(value = "endTime", required = false) String endTime) {
        try {
            List<String> logs=serverService.getLogInformation(id,startTime,endTime);
            return new WebResponse<Object>().success(logs);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Object>().failure(e.getMessage());
        }
    }

}
