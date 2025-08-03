package com.ly.cloud.backup.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.backup.vo.DatabaseListVo;
import com.ly.cloud.backup.vo.ServerVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ly.cloud.backup.dto.DatabaseDto;
import com.ly.cloud.backup.po.DatabasePo;
import com.ly.cloud.backup.vo.DatabaseVo;

/**
 * 数据库信息DatabaseMapper
 * @author jiangzhongxin
 */
@Mapper
public interface DatabaseMapper extends BaseMapper<DatabasePo> {

    /**
     * 条件查询数据库信息
     * @author: jiangzhongxin
     * @date: 2022/3/8 12:02
     * @param page : 分页辅助
     * @param databaseDto : 查询参数
     * @return: Page<DatabaseListVo> : 数据库信息
     */
    public IPage<DatabaseListVo> selectPageLike(@Param("page") Page<DatabaseListVo> page, @Param("dto") DatabaseDto databaseDto);

    /**
     * 查询所有数据库信息
     * @return List<DatabaseListVo> 所有数据库信息
     * @author jiangzhongxin
     * @date 2022/4/1 14:52
     */
    public List<DatabaseListVo> selectAllList(@Param("dto") DatabaseDto databaseDto);

    /**
     * 查询一条数据
     * @author: jiangzhongxin
     * @date: 2022/3/8 12:05
     * @param id : 主键
     * @return: DatabaseVo : 数据
     */
    public DatabaseVo get(String id);

    /**
     * 添加数据库信息表
     * @author: jiangzhongxin
     * @date: 2022/3/8 11:55
     * @param databasePo 数据库信息表
     * @return: Boolean ：操作是否成功
     */
    public Integer insertPo(DatabasePo databasePo);

    /**
     * 更新数据库信息表
     * @author: jiangzhongxin
     * @date: 2022/3/8 11:55
     * @param databasePo 数据库信息表
     * @return: Boolean ：操作是否成功
     */
    public Integer updatePo(DatabasePo databasePo);

    /**
     * 通过一定参数校验唯一性
     *
     * @param databaseDto : 查询参数
     * @return Integer 查询的个数
     * @author: jiangzhongxin
     * @date 2022/4/1 14:52
     */
    public Integer checkUniqueness(@Param("dto") DatabaseDto databaseDto);

    /**
     * 批量删除数据
     * @author: jiangzhongxin
     * @date: 2022/3/8 11:54
     * @param ids : 主键集合
     * @return: Integer ： 删除数量
     */
    public Integer deleteByMulti(@Param("list")List<String> ids);

    /**
     * 查询所有关联服务器
     *
     * @return List<ServerVo>
     * @author jiangzhongxin
     * @date 2022/3/29 16:41
     */
    public List<ServerVo> servers();

    /**
     *  更新状态 异常
     * @param list
     * @return
     */
    Integer updateStatusErr(@Param("list") List<String> list);

    /**
     *  更新状态 健康
     * @param list
     * @return
     */
    Integer updateStatusSucc(@Param("list") List<String> list);

}