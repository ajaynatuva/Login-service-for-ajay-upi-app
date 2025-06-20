package com.amps.user.service.impl;

import com.amps.user.dto.UserLoginDTO;
import com.amps.user.model.User;
import com.amps.user.model.UserRoles;
import com.amps.user.repository.RolesRepository;
import com.amps.user.repository.UserLoginRepository;
import com.amps.user.repository.UserRolesRepository;
import com.amps.user.service.UserLoginService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.amps.user.constants.ParameterConstants.Id;

@Service
public class UserLoginServiceImpl implements UserLoginService {

    Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    UserLoginRepository userLoginRepository;

    @Autowired
    UserRolesRepository userRolesRepository;

    @Autowired
    RolesRepository rolesRepository;
    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User createUser(UserLoginDTO userloginDto) {
        try {
            User user = new User();
            user.setUserName(userloginDto.getUserName());
            user.setEmailId(userloginDto.getEmailId());
            user.setPassword(passwordEncoder.encode(userloginDto.getPassword()));
            user.setDeletedb(userloginDto.getDeletedb());
            user.setMfaRequired(true);
            user.getCreatedOn();
            user.getLastLogin();
            userLoginRepository.save(user);
            return user;
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
    }

    @Override
    public void saveUserRoles(User user, List<Integer> rolesId) {
        userRolesRepository.deleteRoleById(user.getUserId());

        rolesId.stream()
                .map(roleId -> {
                    var userRole = new UserRoles();
                    userRole.setRoleId(rolesRepository.findByRoleId(roleId));
                    userRole.setUserId(user);
                    return userRole;
                })
                .forEach(userRolesRepository::save);
    }

    @Override
    public List<User> searchUser(String userName, String emailId) {
        return userLoginRepository.findUser(userName, emailId);
    }

    @Override
    public User updateUser(UserLoginDTO userloginDto) {
        try {
            User user = new User();
            user.setUserId(userloginDto.getUserId());
            user.setUserName(userloginDto.getUserName());
            user.setEmailId(userloginDto.getEmailId());
            user.setPassword(passwordEncoder.encode(userloginDto.getPassword()));
            Date createdOn = new Date();
            Date lastLogin = new Date();
            user.setCreatedOn(createdOn);
            user.setLastLogin(lastLogin);
            userLoginRepository.saveUser(user.getUserName(), user.getEmailId(), user.getUserId());
            return user;
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
    }

    @Override
    public User login(String email, String password) {
        User user = userLoginRepository.findByEmailId(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    @Override
    public void deleteUser(int id) {
        String updateQuery = "update users.user_details set deleted_b = 1 where user_id =:id";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue(Id, id);
        namedParameterJdbcTemplate.update(updateQuery, namedParameters);
    }

    @Override
    public List<User> getExistingUsers() {
        return userLoginRepository.getExistingUsers();
    }


    @Override
    public Boolean validateEmail(String emailId) {
        User user = userLoginRepository.findByEmailId(emailId);
        logger.info("user: {}", user);
        return user != null;
    }
}
