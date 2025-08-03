package com.ly.cloud.backup.service.impl;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.cloud.auth.util.StringUtils;
import com.ly.cloud.backup.common.constant.CommonConstant;
import com.ly.cloud.backup.dto.SystemInfoDto;
import com.ly.cloud.backup.vo.SystemInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ly.cloud.backup.dto.SystemSetupDto;
import com.ly.cloud.backup.po.SystemSetupPo;
import com.ly.cloud.backup.mapper.SystemSetupMapper;
import com.ly.cloud.backup.service.SystemSetupService;
import com.ly.cloud.backup.vo.SystemSetupVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import static com.ly.cloud.backup.common.constant.SystemSetupConstant.*;

/**
 * 系统设置信息表
 *
 * @author chenguoqing
 */
@Slf4j
@Service
public class SystemSetupServiceImpl extends ServiceImpl<SystemSetupMapper, SystemSetupPo> implements SystemSetupService {

    private static final Logger logger = LoggerFactory.getLogger(SystemSetupServiceImpl.class);
    /**
     * 截取图片文件名的长度
     */
    private static final int CUT_LENGTH = 8;

    @Value("${fileClientPath:/data/dc}")
    private String fileClientPath;

    @Autowired
    private SystemSetupMapper systemSetupMapper;

    /**
     * 根据指定的key获取对应的value值
     * @param setKey
     * @return
     */
    public String getSystemSetupValue(String setKey){
        SystemSetupPo systemSetupPo = this.getOne(new QueryWrapper<SystemSetupPo>().lambda().eq(SystemSetupPo::getSetKey, setKey));
        return Optional.ofNullable(systemSetupPo).map(f -> f.getSystray()).orElse(null);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(SystemSetupDto systemSetupDto) {
        try {
            SystemSetupPo systemSetupPo = new SystemSetupPo();
            BeanUtils.copyProperties(systemSetupDto, systemSetupPo);

            int result = systemSetupMapper.insert(systemSetupPo);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateSystray(SystemSetupDto systemSetupDto) {
        try {
            SystemSetupPo systemSetupPo = new SystemSetupPo();
            BeanUtils.copyProperties(systemSetupDto, systemSetupPo);
            int result = systemSetupMapper.updateSystray(systemSetupPo);
            if (result >0){
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        return false;
    }

    @Override
    public SystemSetupVo find(String  id) {
        return systemSetupMapper.getDataById(id);
    }

    @Override
    public IPage<SystemSetupVo> query(Integer page,Integer pageSize, String searchParam) {
        try {
            Page<SystemSetupVo> sPage = new Page<>(page > 0 ? page : 1, pageSize > 0 ? pageSize: 15);
            SystemSetupDto systemSetupDto = new SystemSetupDto();
            if (StringUtils.isNotEmpty(searchParam)) {
                systemSetupDto.setSearchParam(searchParam);
            }
            return systemSetupMapper.select(sPage, systemSetupDto);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    //将图片的Base64编码返回
    @Override
    public String uploadLogoFile(MultipartFile uploadlogofile) throws IOException {
        String result=null;
        //将图片的Base64编码返回
        try {
            String  imgEncodeToSystray = Base64Utils.encodeToString(uploadlogofile.getBytes());
            return imgEncodeToSystray;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return  result;
    }

    //回显logo图标
    @Override
    public boolean viewImg(HttpServletResponse response) throws IOException {
         boolean result = false;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            LambdaQueryWrapper<SystemSetupPo> queryWrapper = new QueryWrapper<SystemSetupPo>().lambda()
                    .eq(SystemSetupPo::getSetKey, LOGO);
            SystemSetupPo systemSetupPo = this.getOne(queryWrapper);
            String systray = Optional.ofNullable(systemSetupPo).map(f -> f.getSystray()).orElse(null);
            //判断数据库中是否有logo的base64编码值
            if (!StringUtils.isEmpty(systray)) {
                byte[] jsonFile = Base64Utils.decodeFromString(systray);
                response.setContentType(CommonConstant.CONTENT_TYPE_IMG_PNG);
                response.setContentType(CommonConstant.CONTENT_TYPE_APPLICATION_CHARSET);
                response.setHeader(CommonConstant.CONTENT_DISPOSITION, CommonConstant.CONTENT_DISPOSITION_VALUE + "logo.png");
                bis = new BufferedInputStream(new ByteArrayInputStream(jsonFile));
                bos = new BufferedOutputStream(response.getOutputStream());
                byte[] buff = new byte[jsonFile.length];
                while (-1 != (bis.read(buff, 0, buff.length))) {
                    bos.write(buff, 0, buff.length);
                }
                result=true;
            }else{
                return false;
            }
         }catch(IOException e){
                log.error(e.getMessage(),e);
            }finally {
            if (bis != null) {
                bis.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
        return result;
    }


    //获取系统参数信息中的页脚、平台名称以及项目地名称
    @Override
    public SystemInfoVo getSystemInfo()  {
        //创建返回给前端页面的VO对象
        SystemInfoVo systemInfoVo = new SystemInfoVo();
        //构造SQL：SELECT id,title,whether_text,systray,set_key,operator_id AS operatorIid,operation_time,remark FROM ly_sm_system_setup WHERE set_Key = ? OR set_key = ?
        QueryWrapper<SystemSetupPo> queryWrapper = new QueryWrapper<SystemSetupPo>()
                .eq("set_Key", FOOTER)
                .or()
                .eq("set_key", PLATFORMNAME)
                .or()
                .eq("set_key", LOGO);
//                .or()
//                .eq("set_key",SCHOOLNAME);
        List<SystemSetupPo> systemSetupPos = systemSetupMapper.selectList(queryWrapper);
        //map存储 ly_sm_system_setup表 中<set_key,systray>键值对
        HashMap<String, String> map = new HashMap<>();
        for(SystemSetupPo systemSetupPo:systemSetupPos){
            String value = systemSetupPo.getSystray();
            map.put(systemSetupPo.getSetKey(),value);
        }
        //取出SystemInfoVo类中的各个属性 组合成对象数组Field[]
        Field[] declaredFields = SystemInfoVo.class.getDeclaredFields();
        for(Field declaredField:declaredFields){
            log.debug("当前Field名为：" + map.get(declaredField.getName()));
            //用反射时访问私有变量
            declaredField.setAccessible(true);
           String decValue= map.get(declaredField.getName());
           if(decValue!=null){
               try {
                   declaredField.set(systemInfoVo,decValue);
               } catch (IllegalAccessException e) {
                   e.printStackTrace();
               }
           }
        }
        return systemInfoVo;
    }

    //保存系统设置参数logo、页脚、平台名称等
    @Override
    public void savaSystemInfo(SystemInfoDto systemInfoDto) throws IllegalAccessException {

        //获取数据库表 ly_sm_system_setup中set_key,systray的值，并以键值对形式存储<set_key,systray>
        QueryWrapper<SystemSetupPo> queryWrapper = new QueryWrapper<SystemSetupPo>()
                .eq("set_Key", FOOTER)
                .or()
                .eq("set_key", PLATFORMNAME)
                .or()
                .eq("set_key",LOGO);
        List<SystemSetupPo> systemSetupPos = systemSetupMapper.selectList(queryWrapper);
        //map存储 ly_sm_system_setup表 中<set_key,systray>键值对
        HashMap<String, String> map = new HashMap<>();
        for(SystemSetupPo systemSetupPo:systemSetupPos){
            String value = systemSetupPo.getSystray();
            map.put(systemSetupPo.getSetKey(),value);
        }

        //前端传来的fieldValue字段
        Field[] declaredFields = SystemInfoDto.class.getDeclaredFields();
        for(Field declaredField:declaredFields) {
            declaredField.setAccessible(true);
            String fieldValue = (String)declaredField.get(systemInfoDto);
            if (fieldValue == null || "".equals(fieldValue)) {
                continue;
            }
            String dbData = map.get(declaredField.getName());
            log.info("数据库中的数据dbData:" + dbData + ", 前端传入的数据fieldValue:" + fieldValue);
            if(!dbData.equals(fieldValue)){
                    LambdaUpdateWrapper<SystemSetupPo> lambdaUpdateWrapper = new LambdaUpdateWrapper<SystemSetupPo>().eq(SystemSetupPo::getSetKey,declaredField.getName());
                    lambdaUpdateWrapper.set(SystemSetupPo::getSystray,fieldValue);
                    systemSetupMapper.update(null, lambdaUpdateWrapper);

                }
        }
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long[] ids) {
        try {
            for (Long id : ids) {
                int result = systemSetupMapper.deleteById(id);

            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }



}
