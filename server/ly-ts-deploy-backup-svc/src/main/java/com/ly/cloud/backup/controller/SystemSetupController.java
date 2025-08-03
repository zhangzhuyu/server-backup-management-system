package com.ly.cloud.backup.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ly.cloud.backup.dto.SystemInfoDto;
import com.ly.cloud.backup.vo.SystemInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ly.cloud.backup.common.WebResponse;
import com.ly.cloud.backup.dto.SystemSetupDto;
import com.ly.cloud.backup.vo.SystemSetupVo;
import com.ly.cloud.backup.service.SystemSetupService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 系统设置信息表
 * @author chenguoqing
 *
 */

@RestController
@Api(tags="系统管理_系统设置")
@RequestMapping("/systemSetup")
public class SystemSetupController {

	private static final Logger logger = LoggerFactory.getLogger(SystemSetupController.class);

    @Autowired
    private SystemSetupService systemSetupService;


    @Autowired
    private HttpServletRequest request;

//	@ApiOperation(value = "参数设置" , notes = "添加系统参数" , httpMethod = "POST")
//    @RequestMapping(value = "/create", method = RequestMethod.POST)
//    public WebResponse<?> create(@RequestBody SystemSetupDto systemSetupDto) {
//		try {
//			systemSetupService.create(systemSetupDto);
//			return new WebResponse<Object>().success("");
//		} catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            return new WebResponse<Object>().failure(e.getMessage());
//        }
//    }

    @ApiOperation(value = "参数设置" , notes = "删除系统参数" , httpMethod = "POST")
    @RequestMapping(value="/delete", method = RequestMethod.POST)
    public WebResponse<?> delete(@RequestBody Long[] ids) {
		try {
			systemSetupService.delete(ids);
			return new WebResponse<Object>().success("");
		} catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Object>().failure(e.getMessage());
        }
    }

    //参数设置---->更新系统参数的值
    @ApiOperation(value = "参数设置" , notes = "更新系统参数的值" , httpMethod = "POST")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public WebResponse<?> updateSystray(@RequestBody SystemSetupDto systemSetupDto) {
        if (!Optional.ofNullable(systemSetupDto).isPresent()){
            return new WebResponse<Object>().failure("输入参数不能为空");
        }
        try {
            systemSetupService.updateSystray(systemSetupDto);
            return new WebResponse<Object>().success(true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Object>().failure(e.getMessage());
        }
    }

    //参数设置---->查询并分页显示各个参数
    @ApiOperation(value = "参数设置" , notes = "按参数键或参数描述模糊查询并分页显示各个参数" , httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, paramType = "query", dataType = "Integer", value = "当前页", example = "1"),
            @ApiImplicitParam(name = "pageSize", required = true, paramType = "query", dataType = "Integer", value = "页面大小", example = "15"),
            @ApiImplicitParam(name = "searchParam", paramType = "query", dataType = "String", value = "搜索框输入的内容", example = "super")
    })
    @RequestMapping(value = "/query", method=RequestMethod.GET)
    public WebResponse<?> query(@RequestParam("page") Integer page, @RequestParam("pageSize") Integer pageSize, @RequestParam(value = "searchParam", required = false) String searchParam) {
        try {
            IPage<SystemSetupVo> Page = systemSetupService.query(page,pageSize,searchParam);
            return new WebResponse<IPage>().success(Page);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Object>().failure(e.getMessage());
        }
    }

    //参数设置---->查看系统参数的详情
    @ApiOperation(value = "参数设置" , notes = "查看系统参数的详情" , httpMethod = "GET")
    @RequestMapping(value = "/detail/{id}", method=RequestMethod.GET)
    public WebResponse<?> detail(@PathVariable("id") String  id) {
		try {
			return new WebResponse<SystemSetupVo>().success(systemSetupService.find(id));
		} catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<SystemSetupVo>().failure(e.getMessage());
        }
    }


    //系统设置---->上传logo图标
    @PostMapping(value="/upload")
    @ApiOperation(value="系统设置",notes="上传系统logo",httpMethod = "POST")
    public WebResponse<?> uploadLogoFile(@RequestParam (value="file") MultipartFile uploadlogofile){
        if(uploadlogofile==null){
            return new WebResponse<>().failure("上传文件不能为空");
        }
        try {
            String result=systemSetupService.uploadLogoFile(uploadlogofile);
            return new WebResponse(). success(result);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<SystemSetupVo>().failure(e.getMessage());
        }
    }

    //系统设置---->回显logo图标
    @RequestMapping(value = "/view/img", method = RequestMethod.GET)
    @ApiOperation(value = "系统设置", notes = "回显logo图标", httpMethod = "GET")
    public WebResponse<?> viewImg(HttpServletResponse response) {
        try {
            boolean result=systemSetupService.viewImg(response);
            if(result){
                return new WebResponse(). success(result);
            }else {
                return new WebResponse<>().failure("请先上传logo文件");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<SystemSetupVo>().failure(e.getMessage());
        }
    }

    //系统设置---->获取系统信息
    @GetMapping(value="/getSystemInfo")
    @ApiOperation(value="系统设置",notes="获取页脚和平台名称等系统信息参数",httpMethod = "GET")
    public WebResponse getSystemInfo(){
        try {
            SystemInfoVo systemInfoVo=systemSetupService.getSystemInfo();
            return  new WebResponse<SystemInfoVo>().success(systemInfoVo);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<SystemSetupVo>().failure(e.getMessage());
        }
    }

    //系统设置---->保存系统信息
    @PostMapping(value="/saveSystemInfo")
    @ApiOperation(value="系统设置",notes="保存页脚和平台名称等系统信息参数",httpMethod = "POST")
    public WebResponse savaSystemInfo(@RequestBody  SystemInfoDto systemInfoDto){

        try {
            //保存页脚、项目地名称和平台名称等参数
            systemSetupService.savaSystemInfo(systemInfoDto);
            return new WebResponse().success("保存成功");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse().failure("保存失败");
        }
    }

    //参数设置---->新增参数信息
    @ApiOperation(value = "参数设置_添加", notes = "参数设置_添加", httpMethod = "POST")
    @RequestMapping(value = "/create-setup", method = RequestMethod.POST)
    public WebResponse<?> createSetup(@RequestBody SystemSetupDto systemSetupDto) {
        try {
            systemSetupService.create(systemSetupDto);
            return new WebResponse<Object>().success("");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Object>().failure(e.getMessage());
        }
    }
}