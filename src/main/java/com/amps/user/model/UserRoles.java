package com.amps.user.model;


import jakarta.persistence.*;

@Entity
@Table(name = "user_roles", schema = "users")
public class UserRoles {

    @Id
    @SequenceGenerator(schema = "users", name = "user_roles_user_role_key_seq", sequenceName = "user_roles_user_role_key_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_roles_user_role_key_seq")
    @Column(name = "user_role_key")
    private Integer userRoleKey;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id_fk")
    private User userId;

    @OneToOne(targetEntity = Roles.class)
    @JoinColumn(name = "role_id_fk")
    private Roles roleId;

    public Integer getUserRoleKey() {
        return userRoleKey;
    }

    public void setUserRoleKey(Integer userRoleKey) {
        this.userRoleKey = userRoleKey;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public Roles getRoleId() {
        return roleId;
    }

    public void setRoleId(Roles roleId) {
        this.roleId = roleId;
    }

}