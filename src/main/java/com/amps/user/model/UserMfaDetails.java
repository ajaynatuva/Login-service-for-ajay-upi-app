package com.amps.user.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_mfa_details", schema = "users")
public class UserMfaDetails {

    @Id
    @SequenceGenerator(schema = "users", name = "user_mfa_provider_id_seq", sequenceName = "user_mfa_provider_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_mfa_provider_id_seq")
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "mfa_secret")
    private String mfaSecret;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "is_trusted")
    private boolean isTrusted;

    @Column(name = "ts_token_expiration")
    private LocalDateTime tsTokenExpiration;

    @Column(name = "finger_print_info")
    private String fingerPrintInfo;


    @Column(name = "register_date")
    private LocalDateTime registerDate;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMfaSecret() {
        return mfaSecret;
    }

    public void setMfaSecret(String mfaSecret) {
        this.mfaSecret = mfaSecret;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isTrusted() {
        return isTrusted;
    }

    public void setTrusted(boolean isTrusted) {
        this.isTrusted = isTrusted;
    }


    public String getFingerPrintInfo() {
        return fingerPrintInfo;
    }

    public LocalDateTime getTsTokenExpiration() {
        return tsTokenExpiration;
    }

    public void setTsTokenExpiration(LocalDateTime tsTokenExpiration) {
        this.tsTokenExpiration = tsTokenExpiration;
    }

    public LocalDateTime getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDateTime registerDate) {
        this.registerDate = registerDate;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
