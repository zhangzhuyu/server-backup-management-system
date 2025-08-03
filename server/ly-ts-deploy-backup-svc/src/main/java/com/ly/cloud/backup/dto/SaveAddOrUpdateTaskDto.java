package com.ly.cloud.backup.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;

//taier  设置好所有后，点击保存任务
@Data
public class SaveAddOrUpdateTaskDto implements Serializable {
    private static final long serialVersionUID = -9003609292510835997L;
    private String appType;
    private String componentVersion;
    private Integer computeType;
    private Integer createModel;
    private String createUser;
    private Integer createUserId;
    private String cron;
    private Boolean currentProject;
    private Integer currentStep;
    private Integer datasourceId;
    private String[] dependencyTasks;
    private String empyt;
    private String engineType;
    private String exeArgs;
    private String extraInfo;
    private Integer flowId;
    private String flowName;
    private Boolean forceUpdate;
    private Long gmtCreate;
    private Long gmtModified;
    private Integer id;
    private Integer increColumn;
    private String input;
    private Boolean invalidSubmit;
    private Integer isDeleted;
    private Integer isPublishToProduce;
    private String lockVersion;
    private String mainClass;
    private String modifyUser;
    private Integer modifyUserId;
    private String name;
    private String nodeMap;
    private String nodePName;
    private Integer nodePid;
    private Boolean notSynced;
    private Boolean openDirtyDataManage;
    private Integer operateModel;
    private String options;
    private String output;
    private String ownerUser;
    private Integer ownerUserId;
    private String periodType;
    private Boolean preSave;
    private String projectScheduleStatus;
    private String pythonVersion;
    private String queueName;
    private String[] refResourceList;
    private String relatedTasks;
    private String[] resourceIdList;
    private String scheduleConf;
    private Integer scheduleStatus;
    private JSONObject settingMap;
    private String side;
    private String sink;
    private String source;
    private JSONObject sourceMap;
    private String sqlText;
    private String subNodes;
    private String subTaskVOS;
    private Integer submitStatus;
    private String submitted;
    private Integer syncModel;
    private JSONObject targetMap;
    private String taskDesc;
    private String taskDirtyDataManageVO;
    private Integer taskId;
    private String taskParams;
    private Integer taskPeriodId;
    private String taskPeriodType;
    private Integer taskType;
    private String[] taskVariables;
    private Integer tenantId;
    private String tenantName;
    private Boolean updateSource;
    private Integer userId;
    private String value;
    private Integer version;
    private Integer versionId;
    private Integer parentId;
    private String schema;

    //json里面的变量
    //settingMap
    private Integer speed;
    private String readerChannel;
    private Integer writerChannel;
    //sourceMap
    private Integer type;
    private String[] table;
    private Integer sourceId;
    private Integer collectType;
    private Integer[] cat;
    private Boolean pavingData;
    private Integer rdbmsDaType;
    private Long timestamp;
    private Boolean dataSequence;

    //targetMap
    private Integer sourceIdToTarget;
    private String topic;
    private Integer typeToTarget;


}




































