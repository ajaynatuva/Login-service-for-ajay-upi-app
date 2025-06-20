package com.amps.user.dao.impl;

import com.amps.user.dao.MFAProviderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MFAProviderDaoImpl implements MFAProviderDao {

    @Autowired
    NamedParameterJdbcTemplate ipuNamedParameterJdbcTemplate;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${trust.validity.days}")
    private int trustValidityDays;

    @Override
    @Transactional
    public void saveMfaDetailsToDatabase(Integer userId, String mfaSecret, String ipAddress, Boolean trustThisComputer, long tsTokenExpiration, String systemInfo) {
        // TODO Auto-generated method stub
        String query = """
                			INSERT INTO users.user_mfa_details(user_id, mfa_secret, ip_address, is_trusted, ts_token_expiration, finger_print_info,created_date,register_date)
                VALUES ( :userId,:mfaSecret,:ipAddress,:isTrusted,now(),:fingerPrintInfo,now(),now())
                			""";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("userId", userId);
        namedParameters.addValue("mfaSecret", mfaSecret);
        namedParameters.addValue("ipAddress", ipAddress);
        namedParameters.addValue("isTrusted", trustThisComputer);
        namedParameters.addValue("tsTokenExpiration", tsTokenExpiration);
        namedParameters.addValue("fingerPrintInfo", systemInfo);
        ipuNamedParameterJdbcTemplate.update(query, namedParameters);
    }

    @Override
    @Transactional
    public void updateMfaDetailsToDatabase(Integer id, Boolean trustThisComputer) {
        // TODO Auto-generated method stub
        String query = """ 
                UPDATE users.user_mfa_details SET is_trusted=:isTrusted,register_date=now() WHERE id =:id;
                """;
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("id", id);
        namedParameters.addValue("isTrusted", trustThisComputer);
        ipuNamedParameterJdbcTemplate.update(query, namedParameters);
    }

    @Override
    @Transactional
    public void updateLastLogin(int userId) {
        String query = """ 
                UPDATE users.user_details SET last_login=now() WHERE user_id=:userId;
                """;
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("userId", userId);
        ipuNamedParameterJdbcTemplate.update(query, namedParameters);
    }

    @Override
    @Transactional
    public void updateOtpAndIpAddressToDatabase(Integer id, Integer userId,String mfaSecret, String ipAddress, String systemInfo) {
        // TODO Auto-generated method stub
        String query = """
                UPDATE users.user_mfa_details
                     SET mfa_secret = :mfaSecret,
                         ip_address = :ipAddress,
                         ts_token_expiration = now(),
                         finger_print_info = :fingerPrintInfo,
                         is_trusted = CASE
                                 WHEN register_date < (now() - INTERVAL '1 day' * :trustValidityDays) THEN false
                                 ELSE is_trusted
                               END
                     WHERE id=:id
                """;
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("id", id);
        namedParameters.addValue("trustValidityDays", trustValidityDays);
        namedParameters.addValue("mfaSecret", mfaSecret);
        namedParameters.addValue("ipAddress", ipAddress);
        namedParameters.addValue("fingerPrintInfo", systemInfo);
        ipuNamedParameterJdbcTemplate.update(query, namedParameters);
    }
}
