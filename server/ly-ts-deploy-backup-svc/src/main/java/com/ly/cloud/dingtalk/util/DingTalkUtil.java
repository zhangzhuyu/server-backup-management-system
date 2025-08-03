package com.ly.cloud.dingtalk.util;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.tea.TeaException;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMediaUploadRequest;
import com.dingtalk.api.response.OapiMediaUploadResponse;
import com.ly.cloud.dingtalk.constant.UrlConstant;
import com.taobao.api.FileItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Class Name: DingTalkUtil Description:
 *
 * @author: jiangzhongxin
 * @mail: jiangzhongxin@ly-sky.com
 * @date: 2022年03月04日 17:38
 * @copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
@Slf4j
@Component
public class DingTalkUtil {

    private static StringRedisTemplate stringRedisTemplate;

    @Autowired
    public DingTalkUtil(StringRedisTemplate stringRedisTemplate) {
        DingTalkUtil.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 随机UUID
     * @return UUID
     */
    public static String createUUID() {
        return UUID.randomUUID().toString().replace("-","");
    }

    /**
     * MultipartFile转File
     * @param multiFile
     * @return
     */
    public static File MultipartFileToFile(MultipartFile multiFile) {
        // 获取文件名
        String fileName = multiFile.getOriginalFilename();
        // 获取文件后缀
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        // 若须要防止生成的临时文件重复,能够在文件名后添加随机码
        try {
            File file = File.createTempFile(fileName, prefix);
            multiFile.transferTo(file);
            return file;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 调用该接口上传图片、语音媒体资源文件以及普通文件，接口返回媒体资源标识media_id。
     * 说明 关于 media_id：
     * media_id是可复用的，同一个media_id多次使用。
     * media_id对应的资源文件，仅能在钉钉客户端内使用
     * @param fileType 媒体文件类型：image 图片 voice 语音 file 普通文件 video 视频
     * @param file 媒体文件
     * @param accessToken 调用服务端接口的授权凭证。
     * @return media_id 媒体文件上传后获取的唯一标识。
     */
    public static String uploadMedia(String fileType, File file, String accessToken){
        try {
            DingTalkClient client = new DefaultDingTalkClient(UrlConstant.MEDIA_UPLOAD);
            OapiMediaUploadRequest req = new OapiMediaUploadRequest();
            req.setType(null == fileType ? "file" : fileType);
            FileItem fileItem = new FileItem(file);
            req.setMedia(fileItem);
            OapiMediaUploadResponse rsp = client.execute(req, null == accessToken ? AccessTokenUtil.getAccessToken() : accessToken);
            System.out.println(rsp.getBody());
            return rsp.getMediaId();
        } catch (TeaException err) {
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                log.error(err.getMessage());
            }
        } catch (Exception exception) {
            TeaException err = new TeaException(exception.getMessage(), exception);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                log.error(err.getMessage());
            }
        }
        return null;
    }

    /**
     * 向Redis中存储 key-value键值对数据，value的类型为object
     * @param key
     * @param value
     */
    public static void setStringKeyAndValue(String key, Object value) {
        stringRedisTemplate.opsForValue().set(key, JSONObject.toJSONString(value));
    }

    /**
     * 保存key-value键值对到 redis，存储的value是字符串类型
     * @param key
     * @param value
     */
    public static void setStringKeyAndValue(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 存储的数据，增加设置、有效时间
     * @param key
     * @param value
     * @param minute
     */
    public static void setStringKeyAndValue(String key, Object value, long minute) {
        stringRedisTemplate.opsForValue().set(key, JSONObject.toJSONString(value), minute, TimeUnit.MINUTES);
    }

    /**
     * 存储的数据，增加设置、有效时间
     * @param key
     * @param value
     * @param minute
     */
    public static void setStringKeyAndValue(String key, String value, long minute) {
        stringRedisTemplate.opsForValue().set(key, value, minute, TimeUnit.MINUTES);
    }

    /**
     * 取出对象并转换为对象类型
     * @param key
     * @param clazz
     * @return T
     */
    public static <T> T getKeyObjectValue(String key, Class<T> clazz) {
        T t = null;
        String s = stringRedisTemplate.opsForValue().get(key);
        // 判断是否为空，避免后面的判断处理报空指针
        if (StringUtils.isBlank(s)) {
            return null;
        }
        String string = "java.lang.string";
        // 判断传入类型是否为字符串类型
        if (StringUtils.equalsIgnoreCase(clazz.getName(), string)) {
            // 定义泛型的语法要求，要转
            t = (T) s;
        } else if (StringUtils.isNotBlank(s)) {
            // 如果不是字符串类型，且有值，查出的字符串（前面定义存入的时候一定转换为字符串），转换为对应的类型
            t = JSONObject.parseObject(s, clazz);
        }
        return t;
    }

    /**
     * 删除缓存中对应的key-value
     * @param key
     */
    public static void delete(String key) {
        stringRedisTemplate.delete(key);
    }

}
