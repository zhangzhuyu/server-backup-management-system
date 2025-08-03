package com.ly.cloud.backup.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * 应用统计信息返回对象
 * @author SYC
 *
 */
@Data
public class ApplicationCountVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer code;

    /**
     * 分类名称
     */
    private String value;

    /**
     * 统计数量
     */
    private Integer count;

}
