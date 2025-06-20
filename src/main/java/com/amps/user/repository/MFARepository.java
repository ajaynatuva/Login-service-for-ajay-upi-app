package com.amps.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.amps.user.model.UserMfaDetails;

import java.util.List;

@Repository
public interface MFARepository extends JpaRepository<UserMfaDetails, Integer> {
    @Query(value = "Select * from users.user_mfa_details where user_id =:userId", nativeQuery = true)
    List<UserMfaDetails> findByUserId(Integer userId);
}
