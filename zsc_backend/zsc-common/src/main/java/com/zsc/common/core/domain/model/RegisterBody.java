package com.zsc.common.core.domain.model;

/**
 * 用户注册对象
 *
 * @author zsc
 */
public class RegisterBody extends LoginBody
{
    /** 注册角色：user-普通用户, reviewer-票据审核员 */
    private String roleKey;

    public String getRoleKey()
    {
        return roleKey;
    }

    public void setRoleKey(String roleKey)
    {
        this.roleKey = roleKey;
    }
}
