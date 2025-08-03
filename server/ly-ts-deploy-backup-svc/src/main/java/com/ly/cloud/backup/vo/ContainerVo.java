package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 容器信息
 * @author SYC
 *
 */
@Data
public class ContainerVo  extends BaseVo implements Serializable {

    /**
     * 容器名称
     */
    private String ContainerName;

    /**
     * 容器ID
     */
    private String ContainerId;

    /**
     *容器运行状态
     */
    private String dockerContainerStatus;

    /**
     *容器类型
     */
    private String containerRuntime;

    /**
     *容器创建时间
     */
    private String dockerContainerCreated;


}
