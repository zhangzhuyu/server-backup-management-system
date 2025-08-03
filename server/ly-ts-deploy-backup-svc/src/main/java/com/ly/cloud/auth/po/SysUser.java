package com.ly.cloud.auth.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.ly.cloud.auth.common.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * 用户对象 sys_user
 *
 *
 */
@Data
public class SysUser extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户账号
     */

    private String userName;

    /**
     * 用户昵称
     */

    private String nickName;

    /**
     * 密码
     */
    private String password;

    /**
     * 这个值对于密码加密来说并非必须的，如果想要启用盐加密需要参考
     * {@code org.springframework.security.authentication.dao.DaoAuthenticationProvider}的additionalAuthenticationChecks
     * 方法实现
     * <p>
     * 盐加密
     */
    private String salt;

    /**
     * 帐号状态（1正常 0停用）
     */
    private String status;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 所属部门
     */
    private String department;


    /**
     * 角色对象
     */
    private List<SysRole> roles;

    /**
     * 角色组
     */
    private Long[] role_ids;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 角色ID
     */
    private Long role_id;

    public SysUser() {

    }

    public SysUser(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isAdmin() {
        return isAdmin(this.userId);
    }

    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }


    //    @Xss(message = "用户昵称不能包含脚本字符")
    @Size(min = 0, max = 30, message = "用户昵称长度不能超过30个字符")
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    //    @Xss(message = "用户账号不能包含脚本字符")
    @Size(min = 0, max = 30, message = "用户账号长度不能超过30个字符")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    @JsonIgnore
    @JsonProperty
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public List<SysRole> getRoles() {
        return roles;
    }

    public void setRoles(List<SysRole> roles) {
        this.roles = roles;
    }

    public Long[] getrole_ids() {
        return role_ids;
    }

    public void setrole_ids(Long[] role_ids) {
        this.role_ids = role_ids;
    }


    public Long getrole_id() {
        return role_id;
    }

    public void setrole_id(Long role_id) {
        this.role_id = role_id;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("userId", getUserId())
                .append("userName", getUserName())
                .append("nickName", getNickName())
                .append("password", getPassword())
                .append("salt", getSalt())
                .append("status", getStatus())
                .append("remark", getRemark())
                .toString();
    }
}
