package com.ly.cloud.dingtalk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Class Name: singleChatDto Description:
 *
 * @author: jiangzhongxin
 * @mail: jiangzhongxin@ly-sky.com
 * @date: 2022年03月04日 15:48
 * @copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
@Data
public class SingleChatDto implements Serializable {

    private static final long serialVersionUID = -8564061625044742279L;

    /**
     * 用户id，企业内部基本以姓名拼音，每次最多传20个userid值（已处理，放心传）
     */
    @ApiModelProperty(value = "用户id，企业内部基本以姓名拼音，每次最多传20个userid值（已处理，放心传）", required = true)
    private List<String> userIds;

    /**
     * 消息的msgKey，参考url：https://open.dingtalk.com/document/group/message-types-and-data-format
     */
    @ApiModelProperty(value = "消息的msgKey，参考url：https://open.dingtalk.com/document/group/message-types-and-data-format", required = true)
    private String msgKey;

    /**
     * 消息体，json字符传
     */
    @ApiModelProperty(value = "消息体，json字符传", required = true)
    private String msgParam;

}
