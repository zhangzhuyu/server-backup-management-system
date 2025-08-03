package com.ly.cloud.backup.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BackupManagementValueDto implements Serializable {

    private static final long serialVersionUID = 5719445808532765404L;

//    private Long strategyRecordId;

    private String date;

    private Integer num;
}
