package com.amps.user.repository;

import com.amps.user.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RolesRepository extends JpaRepository<Roles, Integer> {

    Roles findByRoleId(Integer roleId);

}
