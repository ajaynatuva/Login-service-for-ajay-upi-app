package com.amps.user.dao;


public interface MFAProviderDao {

    void saveMfaDetailsToDatabase(Integer userId, String mfaSecret, String ipAddress, Boolean trustThisComputer, long tsTokenExpiration, String systemInfo);

    void updateOtpAndIpAddressToDatabase(Integer id, Integer userId, String mfaSecret, String ipAddress, String systemInfo);

    void updateMfaDetailsToDatabase(Integer userId, Boolean trustThisComputer);

	void updateLastLogin(int userId);
}
