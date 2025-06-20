package com.amps.user.repository;

import com.amps.user.model.UserRoles;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRolesRepository extends JpaRepository<UserRoles,Integer> {
    @Query(value="select role_id_fk from users.user_roles where user_id_fk=:id",nativeQuery=true)
    public List<Integer> findRolesById(Integer id);


    @Query(value="select * from users.user_roles where user_id_fk=:id",nativeQuery=true)
    public List<UserRoles> findRoleById(Integer id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value="delete from users.user_roles where user_id_fk=:userId",nativeQuery=true)
    public void deleteRoleById(Integer userId);
}
