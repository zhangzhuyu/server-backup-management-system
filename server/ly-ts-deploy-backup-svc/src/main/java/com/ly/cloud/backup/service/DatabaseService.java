package com.ly.cloud.backup.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.cloud.backup.po.DatabasePo;
import com.ly.cloud.backup.dto.DatabaseDto;
import com.ly.cloud.backup.vo.DatabaseListVo;
import com.ly.cloud.backup.vo.DatabaseVo;
import com.ly.cloud.backup.vo.SelectVo;

import java.util.List;

/**
 * 数据库信息表
 *
 * @author chenguoqing
 */
public interface DatabaseService extends IService<DatabasePo> {

    /**
     * 条件查询数据库信息
     *
     * @param pageNum     : 页码
     * @param pageSize    : 页码大小
     * @param databaseDto : 查询参数
     * @author: jiangzhongxin
     * @date: 2022/3/8 12:02
     * @return: IPage<DatabaseListVo> : 数据库信息
     */
    public IPage<DatabaseListVo> selectPageLike(Integer pageNum, Integer pageSize, DatabaseDto databaseDto);

    /**
     * 查询一条数据
     *
     * @param id : 主键
     * @param isGet : 是否只查询，不用显示详细信息
     * @author: jiangzhongxin
     * @date: 2022/3/8 12:05
     * @return: DatabaseVo : 数据
     */
    public DatabaseVo get(String id, boolean isGet);

    /**
     * 添加数据库信息表
     *
     * @param databaseDto 数据库信息表
     * @author: jiangzhongxin
     * @date: 2022/3/8 11:55
     * @return: Boolean ：操作是否成功
     */
    public Boolean insert(DatabaseDto databaseDto);

    /**
     * 更新数据库信息表
     *
     * @param databaseDto 数据库信息表
     * @author: jiangzhongxin
     * @date: 2022/3/8 11:55
     * @return: Boolean ：操作是否成功
     */
    public Boolean update(DatabaseDto databaseDto);

    /**
     * 通过一定参数校验唯一性，若不存在则返回true
     *
     * @param databaseDto : 查询参数
     * @return Integer 查询的个数
     * @author: jiangzhongxin
     * @date 2022/4/1 14:52
     */
    public Boolean checkUniqueness(DatabaseDto databaseDto);

    /**
     * 批量删除数据库信息
     *
     * @param ids : 主键集合
     * @author: jiangzhongxin
     * @date: 2022/3/8 11:58
     * @return: Boolean ：操作是否成功
     */
    public Boolean deleteByMulti(List<String> ids);

    /**
     * 根据数据源相关测试数据源是否可连接成功
     *
     * @param databaseDto : 数据源数据
     * @author: jiangzhongxin
     * @date: 2022/3/8 12:16
     * @return: Boolean ：测试是否成功
     */
    public Boolean testDataSource(DatabaseDto databaseDto);

    /**
     * 根据数据源主键测试数据源是否可连接成功
     *
     * @param id : 主键
     * @return Boolean : Boolean
     * @author jiangzhongxin
     * @date 2022/4/1 9:36
     */
    public Boolean testDataSourceById(String id);

    /**
     * 查询所有关联服务器
     *
     * @return List<SelectVo>
     * @author jiangzhongxin
     * @date 2022/3/29 16:41
     */
    public List<SelectVo> servers();

}
