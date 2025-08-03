package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ljb
 */
@Data
public class WarningTestTargetVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -2145609292510835903L;



    /**
     * 表头
     */
    private List<ColumnsVo> columns = new ArrayList<>();

    /**
     *  表数据源
     */
    private List<Map<String, Object>>  dataSource = new ArrayList<>();

    /**
     *  错误信息
     */
    private String errorMes;

    /**
     * sql是否执行成功
     */
    private boolean success;


}
