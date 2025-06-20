package com.amps.user.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles", schema = "users")
public class Roles implements Comparable<Roles> {
    @Id
    @SequenceGenerator(schema = "users", name = "roles_role_id_seq", sequenceName = "roles_role_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roles_role_id_seq")
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "role_desc")
    private String roleDesc;


    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    @Override
    public int compareTo(Roles o) {
        return this.getRoleId() - o.getRoleId();
    }

}
