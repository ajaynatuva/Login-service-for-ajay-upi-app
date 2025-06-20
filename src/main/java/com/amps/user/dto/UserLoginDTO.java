package com.amps.user.dto;


import java.util.List;

public class UserLoginDTO {
    private Integer userId;

    private String userName;

    private String Password;

    private String emailId;

    private List<Integer> roleId;

    private Integer deletedb;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public List<Integer> getRoleId() {
        return roleId;
    }

    public void setRoleId(List<Integer> roleId) {
        this.roleId = roleId;
    }

    public Integer getDeletedb() {
        return deletedb;
    }

    public void setDeletedb(Integer deletedb) {
        this.deletedb = deletedb;
    }

    @Override
    public String toString() {
        return "UserLoginDTO [userId=" + userId + ", userName=" + userName + ", Password=" + Password + ", emailId="
                + emailId + ", roleId=" + roleId + ", deletedb=" + deletedb + "]";
    }
}