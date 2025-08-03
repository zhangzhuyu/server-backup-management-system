package com.ly.cloud.license.client.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.cloud.license.client.dto.ClientLicenseDto;
import com.ly.cloud.license.client.po.ClientLicensePo;
import com.ly.cloud.license.client.po.ClientLicenseServerPo;
import com.ly.cloud.license.client.vo.ClientLicenseVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 *
 * license
 * @author SYC
 * @date 20220720
 */
public interface ClientLicenseService extends IService<ClientLicensePo> {

    /**
     * license表单查询
     */
    public ClientLicenseVo queryLicense();

    /**
     * license申请form表单提交
     */
    public ClientLicenseVo formSubmit(ClientLicenseDto clientLicenseDto) throws Exception;

    /**
     * 导出Json格式的文件
     * @return
     */
    public boolean exportFormJson(HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 导入license证书
     */
    public boolean importLicense(MultipartFile file);

    /**
     * 删除主机
     */
    public List<ClientLicenseServerPo> deleteServer(String id);


    /**
     * 测试主机链接
     */
    public boolean testServer(ClientLicenseServerPo clientLicenseServerPo);

    /**
     * 发送短信推送license即将过期（钉钉、邮件）
     */
    public boolean send();

    /**
     * 校验license是否过期
     */
    public boolean verify() throws ParseException;


}
