package com.ly.cloud.backup.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 操作日志信息表
 * @author chenguoqing
 *
 */
@Data
public class OperationLogDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 对应用户登录日志的id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 主功能模块搜索框的内容
     */
    private String parentSearchParam;
    /**
     * 子功能模块搜索框的内容
     */
    private String childSearchParam;

}
