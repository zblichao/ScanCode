package com.lichao.scancode.entity;

/**
 * Created by zblichao on 2016-03-10.
 */
public class User {
    /*
     * 用户id
	 */
    private String id;

    /*
     * 登录名
     */
    private String userName;
    /*
     * 姓名
     */
    private String name;
    /*
     * 密码
     */
    private String password;

    /*
     *权限信息
     */
    private String permission;

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }


}
