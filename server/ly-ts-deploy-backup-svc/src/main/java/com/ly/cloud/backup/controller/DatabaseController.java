package com.ly.cloud.backup.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ly.cloud.backup.util.UsualUtil;
import com.ly.cloud.backup.vo.DatabaseListVo;
import com.ly.cloud.backup.vo.DatabaseVo;
import com.ly.cloud.backup.vo.SelectVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ly.cloud.backup.common.WebResponse;
import com.ly.cloud.backup.dto.DatabaseDto;
import com.ly.cloud.backup.service.DatabaseService;
import java.util.List;
import java.util.Map;

/**
 * Class Name: NetrafficController Description: 运维平台客户端-资源管理-数据库信息相关接口API
 *
 * @author: jiangzhongxin
 * @mail: jiangzhongxin@ly-sky.com
 * @date: 2022年02月23日 15:09
 * @copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
@RestController
@Api(tags = "资源管理_数据库管理_数据库信息相关接口API")
@RequestMapping("/database")
public class DatabaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(DatabaseController.class);

    @Autowired
    private DatabaseService databaseService;

    /**
     * 条件查询数据库信息
     *
     * @return 数据库信息
     * @author: jiangzhongxin
     */
    @ApiOperation(value = "条件查询数据库信息" , notes = "条件查询数据库信息" , httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "pageNum", value = "页码", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "query", name = "pageSize", value = "页码大小", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType = "body", name = "databaseDto", value = "数据源", required = true, dataType = "DatabaseDto"),
    })
    @RequestMapping(value = "/list", method=RequestMethod.POST)
    public WebResponse<?> query(@RequestParam("pageNum") Integer pageNum,
                                                       @RequestParam("pageSize") Integer pageSize,
                                                       @RequestBody DatabaseDto databaseDto) {
        try {
            IPage<DatabaseListVo> page = databaseService.selectPageLike(pageNum, pageSize, databaseDto);
            return new WebResponse<IPage<DatabaseListVo>>().success(page);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<>().failure(e.getMessage());
        }
    }

    /**
     * 添加数据库信息
     *
     * @return 操作是否成功
     * @author: jiangzhongxin
     */
	@ApiOperation(value = "添加数据库信息" , notes = "添加数据库信息" , httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", name = "databaseDto", value = "数据源", required = true, dataType = "DatabaseDto")
    })
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public WebResponse<Boolean> insert(@Validated @RequestBody DatabaseDto databaseDto) {
		try {
			return new WebResponse<Boolean>().success(databaseService.insert(databaseDto));
		} catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Boolean>().failure(e.getMessage());
        }
    }

    /**
     * 更新数据库信息
     *
     * @return 操作是否成功
     * @author: jiangzhongxin
     */
    @ApiOperation(value = "更新数据库信息" , notes = "更新数据库信息" , httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", name = "databaseDto", value = "数据源", required = true, dataType = "DatabaseDto")
    })
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public WebResponse<Boolean> update(@RequestBody DatabaseDto databaseDto) {
        try {
			return new WebResponse<Boolean>().success(databaseService.update(databaseDto));
		} catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Boolean>().failure(e.getMessage());
        }
    }

    /**
     * 批量删除数据库信息
     *
     * @return 操作是否成功
     * @author: jiangzhongxin
     */
    @ApiOperation(value = "批量删除数据库信息" , notes = "批量删除数据库信息" , httpMethod = "POST")
    @RequestMapping(value="/deleteByMulti", method = RequestMethod.POST)
    public WebResponse<Boolean> deleteByMulti(@RequestBody Map<String, Object> params) {
		try {
            List<String> ids = UsualUtil.castList(params.get("ids"), String.class);
			return new WebResponse<Boolean>().success(databaseService.deleteByMulti(ids));
		} catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Boolean>().failure(e.getMessage());
        }
    }

    /**
     * 根据主键查询一条数据
     *
     * @return 数据库信息
     * @author: jiangzhongxin
     */
    @ApiOperation(value = "根据主键查询一条数据" , notes = "根据主键查询一条数据" , httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "id", value = "主键", required = true, dataType = "String")
    })
    @RequestMapping(value = "/get/{id}", method=RequestMethod.GET)
    public WebResponse<DatabaseVo> get(@PathVariable("id") String id) {
		try {
			return new WebResponse<DatabaseVo>().success(databaseService.get(id, false));
		} catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<DatabaseVo>().failure(e.getMessage());
        }
    }

    /**
     * 查询详细信息
     *
     * @return 数据库信息
     * @author: jiangzhongxin
     */
    @ApiOperation(value = "查询详细信息" , notes = "查询详细信息" , httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "id", value = "主键", required = true, dataType = "String")
    })
    @RequestMapping(value = "/detail/{id}", method=RequestMethod.GET)
    public WebResponse<DatabaseVo> detail(@PathVariable("id") String id) {
		try {
			return new WebResponse<DatabaseVo>().success(databaseService.get(id, true));
		} catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<DatabaseVo>().failure(e.getMessage());
        }
    }

    /**
     * 根据数据源相关测试数据源是否可连接成功
     *
     * @return 是否可连接成功
     * @author: jiangzhongxin
     */
    @ApiOperation(value = "根据数据源相关测试数据源是否可连接成功" , notes = "根据数据源相关测试数据源是否可连接成功" , httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", name = "databaseDto", value = "数据源", required = true, dataType = "DatabaseDto")
    })
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public WebResponse<Boolean> testDataSource(@RequestBody DatabaseDto databaseDto){
        try {
            return new WebResponse<Boolean>().success(databaseService.testDataSource(databaseDto));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Boolean>().failure(e.getMessage());
        }
    }

    /**
     * 根据数据源主键测试数据源是否可连接成功
     *
     * @return 是否可连接成功
     * @author: jiangzhongxin
     */
    @ApiOperation(value = "根据数据源主键测试数据源是否可连接成功" , notes = "根据数据源主键测试数据源是否可连接成功" , httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "id", value = "主键", required = true, dataType = "String")
    })
    @RequestMapping(value = "/test/{id}", method = RequestMethod.GET)
    public WebResponse<Boolean> testDataSourceById(@PathVariable("id") String id){
        try {
            return new WebResponse<Boolean>().success(databaseService.testDataSourceById(id));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<Boolean>().failure(e.getMessage());
        }
    }

    /**
     * 查询所有关联服务器
     *
     * @return 所有关联服务器
     * @author: jiangzhongxin
     */
    @ApiOperation(value = "查询所有关联服务器" , notes = "查询所有关联服务器" , httpMethod = "GET")
    @RequestMapping(value = "/servers", method = RequestMethod.GET)
    public WebResponse<List<SelectVo>> servers() {
        try {
            return new WebResponse<List<SelectVo>>().success(databaseService.servers());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new WebResponse<List<SelectVo>>().failure(e.getMessage());
        }
    }

}
