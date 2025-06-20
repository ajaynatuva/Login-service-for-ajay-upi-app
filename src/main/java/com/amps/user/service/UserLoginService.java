package com.amps.user.service;

import com.amps.user.dto.UserLoginDTO;
import com.amps.user.model.User;

import java.util.List;

public interface UserLoginService {

    User createUser(UserLoginDTO userloginDto);

    void saveUserRoles(User user, List<Integer> rolesId);

    List<User> searchUser(String userName, String emailId);

    User updateUser(UserLoginDTO userloginDto);

    User login(String email, String password);

    void deleteUser(int id);

    List<User> getExistingUsers();

    Boolean validateEmail(String emailId);

}
