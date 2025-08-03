package com.ly.cloud.auth.common;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
/**
 * Entity基类
 * 
 *
 */
public class BaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;


    /** 备注 */
    private String remark;

    /** 请求参数 */
    @TableField(exist = false)
    private Map<String, Object> params;


    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Map<String, Object> getParams()
    {
        if (params == null)
        {
            params = new HashMap<>();
        }
        return params;
    }

    public void setParams(Map<String, Object> params)
    {
        this.params = params;
    }
}
