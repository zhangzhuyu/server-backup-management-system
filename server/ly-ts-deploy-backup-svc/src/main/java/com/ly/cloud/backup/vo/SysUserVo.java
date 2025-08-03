package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Class Name: SysUserVo Description:
 *
 * @author: jiangzhongxin
 * @mail: jiangzhongxin@ly-sky.com
 * @date: 2022年03月24日 9:47
 * @copyright: 2018 LIANYI TECHNOLOGY CO.;LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
@Data
public class SysUserVo implements Serializable {

    private static final long serialVersionUID = -7526639000426910937L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户性别
     */
    private String sex;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phonenumber;

    /**
     * 钉钉号
     */
    private String dingTalk;

    /**
     * 所属部门
     */
    private String department;

}
