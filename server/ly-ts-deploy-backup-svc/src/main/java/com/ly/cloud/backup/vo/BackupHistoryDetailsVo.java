package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BackupHistoryDetailsVo implements Serializable {

    private static final long serialVersionUID = 3635474491407617602L;

    private String value;

    private String label;

    private List<BackupHistoryDetailsVo> children;

}
