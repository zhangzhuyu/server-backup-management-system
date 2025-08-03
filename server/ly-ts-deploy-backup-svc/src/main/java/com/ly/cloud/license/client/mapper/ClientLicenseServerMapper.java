package com.ly.cloud.license.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.cloud.license.client.po.ClientLicensePo;
import com.ly.cloud.license.client.po.ClientLicenseServerPo;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 *  license服务器信息
 * @author SYC
 * @date 20220720
 */
@Mapper
public interface ClientLicenseServerMapper extends BaseMapper<ClientLicenseServerPo> {


}