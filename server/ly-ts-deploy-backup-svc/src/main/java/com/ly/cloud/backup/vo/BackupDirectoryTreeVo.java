package com.ly.cloud.backup.vo;

import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;

import java.io.Serializable;
import java.util.List;

@Data
public class BackupDirectoryTreeVo implements Serializable {

    private static final long serialVersionUID = 3635474491407617602L;

    private String value;

    private String label;

    private String parentId;

    private String directoryId;

    private List<BackupDirectoryTreeVo> children;

}
