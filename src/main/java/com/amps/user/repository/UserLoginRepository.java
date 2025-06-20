package com.amps.user.repository;

import com.amps.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface UserLoginRepository extends JpaRepository<User, Integer> {

    @Query(value = "select * from users.user_details where deleted_b != 1", nativeQuery = true)
    List<User> getExistingUsers();

    @Query(value = "select * from users.user_details where username =:userName or email_id =:emailId", nativeQuery = true)
    List<User> findUser(String userName, String emailId);

    @Query(value = "select * from users.user_details where email_Id =:email and password =:password", nativeQuery = true)
    User validateUser(@Param("email") String email, @Param("password") String password);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE users.user_details SET password=:password where user_id=:userId", nativeQuery = true)
    void updatePassword(String password, Integer userId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "delete from users.user_details where user_id =:userId", nativeQuery = true)
    void deleteUser(Integer userId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE users.user_details SET username=:userName,email_id=:emailId,created_on='now()',last_login='now()' WHERE user_id=:userId", nativeQuery = true)
    void saveUser(String userName, String emailId, Integer userId);

    User findByEmailId(String email);

    @Query(value = "select user_id from users.user_details where email_id =:emailId", nativeQuery = true)
    int getUserId(String emailId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE users.user_details SET device_id=:deviceId, expiry_at=:expiresAt WHERE user_id=:userId", nativeQuery = true)
    void saveDeviceId(String deviceId, LocalDateTime expiresAt, Integer userId);

    @Query(value = "select expiry_at from users.user_details where device_id=:deviceId and user_id =:userId", nativeQuery = true)
    LocalDateTime findByDeviceIdAndUserId(String deviceId, int userId);

}