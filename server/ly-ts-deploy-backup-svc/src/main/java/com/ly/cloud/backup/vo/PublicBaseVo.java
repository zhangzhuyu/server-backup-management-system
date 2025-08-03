package com.ly.cloud.backup.vo;


import lombok.Data;

import java.io.Serializable;

/**
 * @author: chenguoqing
 * @mail: chenguoqing@ly-sky.com
 * @date: 2022-04-27
 * @version: 1.0
 */
@Data
public class PublicBaseVo  implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * id
     */
//    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String id;

    /**
     * 代码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 其它业务代码
     */
    private String businessCode;

    /**
     * 其它代码名称
     */
    private String businessName;

    private String parentValue;

}
