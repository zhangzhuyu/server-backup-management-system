package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * dump日志上传记录：ly_log_upload_record
 *
 * @author wzz
 */
@Data
@TableName("ly_log_upload_record")
public class LogUploadRecordPo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 4188310108610523009L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * md5
     */
    @TableField(value = "md5")
    private String md5;

    /**
     * 分片文件md5
     */
    @TableField(value = "chunk_md5")
    private String chunkMd5;

    /**
     * 文件名称
     */
    @TableField(value = "file_name")
    private String fileName;

    /**
     * 分片顺序
     */
    @TableField(value = "chunk_num")
    private Integer chunkNum;

    /**
     * 分片总数
     */
    @TableField(value = "chunk_sum")
    private Integer chunkSum;

    /**
     * 关联服务ID
     */
    @TableField(value = "service_id")
    private Long serviceId;


    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 类型(1:完整文件；2:分片文件)
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 父类ID(分片文件来源的父类文件ID)
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time")
    private java.util.Date operationTime;

    /**
     * 文件大小
     */
    @TableField(value = "file_size")
    private Integer fileSize;

    /**
     * 上传状态
     */
    @TableField(value = "upload_status")
    private Integer uploadStatus;

    /**
     * 解析状态
     */
    @TableField(value = "analyse_status")
    private Integer analyseStatus;

    /**
     * 合并状态
     */
    @TableField(value = ",merge_status")
    private Integer mergeStatus;

    /**
     * 状态
     */
    /*@TableField(value = "status")
    private Integer status;*/
}

