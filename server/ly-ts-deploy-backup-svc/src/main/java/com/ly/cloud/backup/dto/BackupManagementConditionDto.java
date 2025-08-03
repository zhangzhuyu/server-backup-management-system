package com.ly.cloud.backup.dto;

import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;

import java.io.Serializable;
import java.util.List;

@Data
public class BackupManagementConditionDto implements Serializable {

    private static final long serialVersionUID = 5719445808532765404L;

    private String strategyRecordId;

    private List<Long> ids;
}
