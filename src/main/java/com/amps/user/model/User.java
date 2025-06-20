package com.amps.user.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "user_details", schema = "users")
public class User {
    @Id
    @SequenceGenerator(schema = "users", name = "user_details_user_id_seq", sequenceName = "user_details_user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_details_user_id_seq")
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "username")
    private String userName;

    @Column(name = "email_id")
    private String emailId;

    @Column(name = "created_on")
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "last_login")
    @UpdateTimestamp
    private Date lastLogin;

    @Column(name = "password")
    private String password;

    @Column(name = "deleted_b")
    private Integer deletedb;

    @Column(name = "is_mfa_required")
    private boolean isMfaRequired;

    private User(Builder builder) {
        setUserId(builder.userId);
        setUserName(builder.userName);
        setEmailId(builder.emailId);
        setCreatedOn(builder.createdOn);
        setLastLogin(builder.lastLogin);
        setPassword(builder.password);
        setDeletedb(builder.deletedb);
        setMfaRequired(builder.isMfaRequired);
    }

    public User(Integer userId, String userName, String emailId, Date createdOn, Date lastLogin, String password, Integer deletedb, boolean isMfaRequired) {
        this.userId = userId;
        this.userName = userName;
        this.emailId = emailId;
        this.createdOn = createdOn;
        this.lastLogin = lastLogin;
        this.password = password;
        this.deletedb = deletedb;
        this.isMfaRequired = isMfaRequired;
    }

    public User() {

    }

    public static User createEmptyUser() {
        User user = new User();
        // Initialize default values or leave fields uninitialized
        return user;
    }

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

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getDeletedb() {
        return deletedb;
    }

    public void setDeletedb(Integer deletedb) {
        this.deletedb = deletedb;
    }

    public boolean isMfaRequired() {
        return isMfaRequired;
    }

    public void setMfaRequired(boolean mfaRequired) {
        isMfaRequired = mfaRequired;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", emailId='" + emailId + '\'' +
                ", createdOn=" + createdOn +
                ", lastLogin=" + lastLogin +
                ", password='" + password + '\'' +
                ", deletedb=" + deletedb +
                ", isMfaRequired=" + isMfaRequired +
                '}';
    }

    public static final class Builder {
        private Integer userId;

        private String userName;

        private String emailId;

        private Date createdOn;

        private Date lastLogin;

        private String password;

        private Integer deletedb;

        private boolean isMfaRequired;

        public Builder() {
        }

        public Builder userId(Integer userId) {
            this.userId = userId;
            return this;
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder emailId(String emailId) {
            this.emailId = emailId;
            return this;
        }

        public Builder createdOn(Date createdOn) {
            this.createdOn = createdOn;
            return this;
        }

        public Builder lastLogin(Date lastLogin) {
            this.lastLogin = lastLogin;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder deletedb(Integer deletedb) {
            this.deletedb = deletedb;
            return this;
        }

        public Builder isMfaRequired(boolean isMfaRequired) {
            this.isMfaRequired = isMfaRequired;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
