package com.amps.user.dto;

public class OTPDetails {
    String otp;

    long timestamp;

    public OTPDetails(String otp, long timestamp) {
        this.otp = otp;
        this.timestamp = timestamp;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "OTPDetails{" +
                "otp='" + otp + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
