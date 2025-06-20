package com.amps.user.controller;

import com.amps.user.dao.MFAProviderDao;
import com.amps.user.dto.UserLoginDTO;
import com.amps.user.model.Roles;
import com.amps.user.model.User;
import com.amps.user.model.UserRoles;
import com.amps.user.repository.RolesRepository;
import com.amps.user.repository.UserLoginRepository;
import com.amps.user.repository.UserRolesRepository;
import com.amps.user.service.OTPService;
import com.amps.user.service.UserLoginService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequestMapping("/users")
@RestController
public class UserLoginController {

    @Autowired
    public MFAProviderDao mfaProviderDao;

    @Autowired
    UserLoginRepository userloginRepository;

    @Autowired
    RolesRepository rolesRepository;

    @Autowired
    UserRolesRepository userRolesRepository;

    @Autowired
    UserLoginService userLoginService;

    @Autowired
    OTPService otpService;

    @Value("${pom.version}")
    String version;

    Logger logger = LogManager.getLogger(UserLoginController.class);
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/getAllUsers")
    public List<User> getUser() {
        return userLoginService.getExistingUsers();
    }

    @PostMapping("/saveUser")
    public boolean saveUser(@RequestBody UserLoginDTO userloginDto) {
        try {
            User user = userLoginService.createUser(userloginDto);
            if (user != null) {
                userLoginService.saveUserRoles(user, userloginDto.getRoleId());
                return true;
            }
            logger.info("save", user);
            return true;
        } catch (Exception e) {
            logger.error(e);
            throw new RuntimeException("Failed to save user: " + e.getMessage());
        }
    }

    @GetMapping("/getUserPOMVersion")
    public String getConfigPOMVersion() {
        return version;
    }

    @GetMapping("/getRoles")
    public List<Roles> getRoles() {
        return rolesRepository.findAll().stream().sorted()
                .toList();
    }

    @PostMapping("/validateUser")
    public User validateUsers(@RequestBody Map<String, String> paramsMap, HttpServletRequest httpRequest) throws Exception {
        User user = userLoginService.login(paramsMap.get("emailId"), paramsMap.get("password"));
        String fingerPrintInfo = paramsMap.get("browserDetails");
        String uniqueId = paramsMap.get("uniqueIdGenerated");
        if (user == null || user.getDeletedb() == 1) {
            throw new Exception("User Does Not Exist");
        }
        if (!user.isMfaRequired()) {
            return user;
        }
        return otpService.userAuthentication(user, httpRequest, fingerPrintInfo, uniqueId, false);
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<User> checkForValidOTP(@RequestBody Map<String, String> request, HttpServletRequest httpServletRequest) {
        boolean isForgotPasswordOtp = Boolean.parseBoolean(request.get("isForgotPasswordOtp"));
        User user = isForgotPasswordOtp ? userloginRepository.findByEmailId(request.get("emailId")) :
                userLoginService.login(request.get("emailId"), request.get("password"));
        String otp = request.get("otpValue");
        Integer userId = user.getUserId();
        boolean trustThisComputer = Boolean.parseBoolean(request.get("trustThisComputer"));
        boolean isValidOtp = otpService.validateOTP(otp, userId, trustThisComputer, isForgotPasswordOtp);
        if (isValidOtp) {
            mfaProviderDao.updateLastLogin(user.getUserId());
            return ResponseEntity.ok(user); // Return user with 200 OK
        } else {
            logger.info("Invalid-otp");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }


    @PostMapping("/Resend-otp")
    public User ResendOtp(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        User user = userloginRepository.findByEmailId(request.get("emailId"));
        String fingerPrintInfo = request.get("browserDetails");
        String uniqueId = request.get("uniqueIdGenerated");
        boolean isForgotPassword = Boolean.parseBoolean(request.get("isForgotPassword"));
        return otpService.userAuthentication(user, httpRequest, fingerPrintInfo, uniqueId, isForgotPassword);
    }


    @PostMapping("/searchUser")
    public List<User> getUser(@RequestBody Map<String, String> paramsMap) {
        List<User> users = userloginRepository.findUser(paramsMap.get("userName"), paramsMap.get("email"));
        return users != null ? users : Collections.emptyList();
    }

    @PostMapping("/updateUser")
    public boolean updateUser(@RequestBody UserLoginDTO userloginDto) {
        try {
            User user = userLoginService.updateUser(userloginDto);
            if (user != null) {
                userLoginService.saveUserRoles(user, userloginDto.getRoleId());
                logger.info("update={}", user);
                return true;
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return false;
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<Void> updatePassword(@RequestBody Map<String, String> paramsMap) {
        String encryptedPassword = passwordEncoder.encode(paramsMap.get("password"));
        userloginRepository.updatePassword(encryptedPassword, Integer.parseInt(paramsMap.get("userId")));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/DeleteUser/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Integer userId) {
        userloginRepository.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getUserById/{id}")
    public User getUserById(@PathVariable int id) {
        return userloginRepository.findById(id).orElse(null);
    }

    @PostMapping("/getRolesData")
    public List<Integer> getRolesById(@RequestBody Map<String, Integer> paramsMap) {
        return userRolesRepository.findRolesById(paramsMap.get("userId"));
    }

    @GetMapping("/getUserByRole/{id}")
    public List<UserRoles> getUserRoleById(@PathVariable Integer id) {
        return userRolesRepository.findRoleById(id);
    }

    @PostMapping(value = "/updateNonExistingUser/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public List<User> updateData(@PathVariable Integer id) {
        userLoginService.deleteUser(id);
        return userLoginService.getExistingUsers();
    }

    @PostMapping("/validateEmail")
    public ResponseEntity<Boolean> validateEmail(@RequestBody String emailId) {
        Boolean isValid = userLoginService.
                validateEmail(emailId.replaceAll("\"", ""));
        return ResponseEntity.ok(isValid);
    }
}
