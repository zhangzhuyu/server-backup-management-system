package com.ly.cloud.backup.util;

import com.ly.cloud.backup.vo.OssFileVo;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author: chenguoqing
 * @mail: chenguoqing@ly-sky.com
 * @date: 2023-02-10
 * @version: 1.0
 */
@Component
public class MinioClientUtils {

    private static final Logger logger = LoggerFactory.getLogger(MinioClientUtils.class);

    /**
     * 内网访问
     */
    @Autowired
    private  MinioClient minioClient;


    /******************************  操作 Bucket 开始  ******************************/

    /**
     * 启动SpringBoot容器的时候初始化Bucket
     * 如果没有Bucket则创建
     * @throws Exception
     */
    public void createBucket(String bucketName) throws Exception {
        if (!bucketExists(bucketName)) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     *  判断Bucket是否存在，true：存在，false：不存在
     * @return
     * @throws Exception
     */
    public boolean bucketExists(String bucketName) throws Exception {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }


    /**
     * 获得Bucket的策略
     * @param bucketName
     * @return
     * @throws Exception
     */
    public String getBucketPolicy(String bucketName) throws Exception {
        String bucketPolicy = minioClient
                .getBucketPolicy(
                        GetBucketPolicyArgs
                                .builder()
                                .bucket(bucketName)
                                .build()
                );
        return bucketPolicy;
    }


    /**
     * 获得所有Bucket列表
     * @return
     * @throws Exception
     */
    public List<Bucket> getAllBuckets() throws Exception {
        return minioClient.listBuckets();
    }

    /**
     * 根据bucketName获取其相关信息
     * @param bucketName
     * @return
     * @throws Exception
     */
    public Optional<Bucket> getBucket(String bucketName) throws Exception {
        return getAllBuckets().stream().filter(b -> b.name().equals(bucketName)).findFirst();
    }

    /**
     * 根据bucketName删除Bucket，true：删除成功； false：删除失败，文件或已不存在
     * @param bucketName
     * @throws Exception
     */
    public void removeBucket(String bucketName) throws Exception {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
    }

    /**
     * 删除一个桶 根据桶是否存在数据进行不同的删除
     * 桶为空时直接删除
     * 桶不为空时先删除桶中的数据，然后再删除桶
     *
     * @param bucketName 桶名
     */
    public void removeBucket(String bucketName, boolean bucketNotNull) throws Exception {
        if (bucketNotNull) {
            deleteBucketAllObject(bucketName);
        }
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
    }
    /**
     * 删除桶中所有的对象
     *
     * @param bucketName 桶对象
     */

    public void deleteBucketAllObject(String bucketName) throws Exception {
        List<String> list = listObjectNames(bucketName);
        if (!list.isEmpty()) {
            for (String objectName : list) {
                removeFile(bucketName, objectName);
            }
        }
    }


    /******************************  操作 Bucket 结束  ******************************/



    /******************************  操作 Files 开始  ******************************/

    /**
     * 判断文件是否存在
     * @param bucketName 存储桶
     * @param objectName 文件名
     * @return
     */
    public boolean isObjectExist(String bucketName, String objectName) {
        boolean exist = true;
        try {
            minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            exist = false;
        }
        return exist;
    }

    /**
     * 判断文件夹是否存在
     * @param bucketName 存储桶
     * @param objectName 文件夹名称
     * @return
     */
    public boolean isFolderExist(String bucketName, String objectName) {
        boolean exist = false;
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).prefix(objectName).recursive(false).build());
            for (Result<Item> result : results) {
                Item item = result.get();
                if (item.isDir() && objectName.equals(item.objectName())) {
                    exist = true;
                }
            }
        } catch (Exception e) {
            exist = false;
        }
        return exist;
    }

    /**
     * 根据文件前缀查询文件
     * @param bucketName 存储桶
     * @param prefix 前缀
     * @param recursive 是否使用递归查询
     * @return MinioItem 列表
     * @throws Exception
     */
    public List<Item> getAllObjectsByPrefix(String bucketName,
                                                   String prefix,
                                                   boolean recursive) throws Exception {
        List<Item> list = new ArrayList<>();
        Iterable<Result<Item>> objectsIterator = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(recursive).build());
        if (objectsIterator != null) {
            for (Result<Item> o : objectsIterator) {
                Item item = o.get();
                list.add(item);
            }
        }
        return list;
    }

    /**
     * 获取文件流
     * @param bucketName 存储桶
     * @param objectName 文件名
     * @return 二进制流
     */
    public InputStream getObject(String bucketName, String objectName) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    /**
     * 断点下载
     * @param bucketName 存储桶
     * @param objectName 文件名称
     * @param offset 起始字节的位置
     * @param length 要读取的长度
     * @return 二进制流
     */
    public InputStream getObject(String bucketName, String objectName, long offset, long length)throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .offset(offset)
                        .length(length)
                        .build());
    }

    /**
     * 获取路径下文件列表
     * @param bucketName 存储桶
     * @param prefix 文件名称
     * @param recursive 是否递归查找，false：模拟文件夹结构查找
     * @return 二进制流
     */
    public Iterable<Result<Item>> listObjects(String bucketName, String prefix,
                                                     boolean recursive) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(prefix)
                        .recursive(recursive)
                        .build());
    }

    /**
     * 查询桶的对象信息
     *
     * @param bucketName 桶名
     * @param recursive  是否递归查询
     * @return 桶的对象信息
     */
    public Iterable<Result<Item>> listObjects(String bucketName, boolean recursive) {
        return minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).recursive(recursive).build());
    }

    /**
     * 查询桶中所有的对象名
     *
     * @param bucketName 桶名
     * @return objectNames
     */
    public List<String> listObjectNames(String bucketName) throws Exception {
        List<String> objectNameList = new ArrayList<>();
        if (bucketExists(bucketName)) {
            Iterable<Result<Item>> results = listObjects(bucketName, true);
            for (Result<Item> result : results) {
                String objectName = result.get().objectName();
                objectNameList.add(objectName);
            }
        }
        return objectNameList;
    }

    /**
     * 文件合并，将分块文件组成一个新的文件
     *
     * @param bucketName       合并文件生成文件所在的桶
     * @param objectName       原始文件名
     * @param sourceObjectList 分块文件集合
     * @return OssFile
     */
    public OssFileVo composeObject(List<ComposeSource> sourceObjectList, String bucketName, String objectName) throws Exception{
        minioClient.composeObject(ComposeObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .sources(sourceObjectList)
                .build());
        String presignedObjectUrl = getPresignedObjectUrl(bucketName, objectName);
        return new OssFileVo(presignedObjectUrl, objectName);
    }

    /**
     * 文件合并，将分块文件组成一个新的文件
     *
     * @param originBucketName 分块文件所在的桶
     * @param targetBucketName 合并文件生成文件所在的桶
     * @param objectName       存储于桶中的对象名
     * @return OssFile
     */
    public OssFileVo composeObject(String originBucketName, String targetBucketName, String objectName) throws Exception{

        Iterable<Result<Item>> results = listObjects(originBucketName, true);
        List<String> objectNameList = new ArrayList<>();
        for (Result<Item> result : results) {
            Item item = result.get();
            objectNameList.add(item.objectName());
        }

        if (ObjectUtils.isEmpty(objectNameList)) {
            throw new IllegalArgumentException(originBucketName + "桶中没有文件，请检查");
        }

        List<ComposeSource> composeSourceList = new ArrayList<>(objectNameList.size());
        // 对文件名集合进行升序排序
        objectNameList.sort((o1, o2) -> Integer.parseInt(o2) > Integer.parseInt(o1) ? -1 : 1);
        for (String object : objectNameList) {
            composeSourceList.add(ComposeSource.builder()
                    .bucket(originBucketName)
                    .object(object)
                    .build());
        }

        return composeObject(composeSourceList, targetBucketName, objectName);
    }

    /**
     * 使用MultipartFile进行文件上传
     * @param bucketName 存储桶
     * @param file 文件名
     * @param objectName 对象名
     * @param contentType 类型
     * @return
     * @throws Exception
     */
    public ObjectWriteResponse uploadFile(String bucketName, MultipartFile file,
                                                 String objectName, String contentType) throws Exception {
        InputStream inputStream = file.getInputStream();
        return minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .contentType(contentType)
                        .stream(inputStream, inputStream.available(), -1)
                        .build());
    }

    /**
     * 上传本地文件
     * @param bucketName 存储桶
     * @param objectName 对象名称
     * @param fileName 本地文件路径
     */
    public ObjectWriteResponse uploadFile(String bucketName, String objectName,
                                                 String fileName) throws Exception {
        return minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .filename(fileName)
                        .build());
    }

    /**
     * 通过流上传文件
     *
     * @param bucketName 存储桶
     * @param objectName 文件对象
     * @param inputStream 文件流
     */
    public ObjectWriteResponse uploadFile(String bucketName, String objectName, InputStream inputStream) throws Exception {
        return minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, inputStream.available(), -1)
                        .build());
    }

    /**
     * 创建文件夹或目录
     * @param bucketName 存储桶
     * @param objectName 目录路径
     */
    public ObjectWriteResponse createDir(String bucketName, String objectName) throws Exception {
        return minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                        .build());
    }

    /**
     * 获取文件信息, 如果抛出异常则说明文件不存在
     *
     * @param bucketName 存储桶
     * @param objectName 文件名称
     */
    public String getFileStatusInfo(String bucketName, String objectName) throws Exception {
        return minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()).toString();
    }

    /**
     * 拷贝文件
     *
     * @param bucketName 存储桶
     * @param objectName 文件名
     * @param srcBucketName 目标存储桶
     * @param srcObjectName 目标文件名
     */
    public ObjectWriteResponse copyFile(String bucketName, String objectName,
                                               String srcBucketName, String srcObjectName) throws Exception {
        return minioClient.copyObject(
                CopyObjectArgs.builder()
                        .source(CopySource.builder().bucket(bucketName).object(objectName).build())
                        .bucket(srcBucketName)
                        .object(srcObjectName)
                        .build());
    }

    /**
     * 删除文件
     * @param bucketName 存储桶
     * @param objectName 文件名称
     */
    public void removeFile(String bucketName, String objectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
    }

    /**
     * 批量删除文件
     * @param bucketName 存储桶
     * @param keys 需要删除的文件列表
     * @return
     */
    public void removeFiles(String bucketName, List<String> keys) {
        List<DeleteObject> objects = new LinkedList<>();
        keys.forEach(s -> {
            objects.add(new DeleteObject(s));
            try {
                removeFile(bucketName, s);
            } catch (Exception e) {
                logger.error("批量删除失败！error:{}",e);
            }
        });
    }

    /**
     * 获取文件外链
     * @param bucketName 存储桶
     * @param objectName 文件名
     * @param expires 过期时间 <=7 秒 （外链有效时间（单位：秒））
     * @return url
     * @throws Exception
     */
    public String getPresignedObjectUrl(String bucketName, String objectName, Integer expires) throws Exception {
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder().expiry(expires).bucket(bucketName).object(objectName).build();
        return minioClient.getPresignedObjectUrl(args);
    }

    /**
     * 获得文件外链
     * @param bucketName
     * @param objectName
     * @return url
     * @throws Exception
     */
    public String getPresignedObjectUrl(String bucketName, String objectName) throws Exception {
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .method(Method.GET).build();
        return minioClient.getPresignedObjectUrl(args);
    }

    /**
     * 将URLDecoder编码转成UTF8
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    public String getUtf8ByURLDecoder(String str) throws UnsupportedEncodingException {
        String url = str.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
        return URLDecoder.decode(url, "UTF-8");
    }

    /******************************  操作 Files 结束  ******************************/


}
