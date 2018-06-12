package com.chiba.controller;

import com.chiba.bean.ResponseBean;
import com.chiba.bean.ValidatorBean;
import com.chiba.domain.User;
import com.chiba.service.CustomUserService;
import com.chiba.service.RoleService;
import com.chiba.utils.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/7              
 *  Description: 
 *****************************************/
@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {

    @Autowired
    private CustomUserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("check_username")
    public ValidatorBean checkName(String username) {
        if (null != username) {
            User user = userService.findUserByUsername(username);
            if (null == user) {
                return new ValidatorBean(false);
            }
        }
        return new ValidatorBean(true);
    }

    @GetMapping("check_username_reg")
    public ValidatorBean checkNameReg(String username) {
        if (null != username) {
            User user = userService.findUserByUsername(username);
            if (null == user) {
                return new ValidatorBean(true);
            }
        }
        return new ValidatorBean(false);
    }

    @PostMapping("register")
    public ResponseBean register(User user) {
        user.setPassword(MD5Util.encode(user.getPassword()));
        user.setRole(roleService.findByCode("user"));
        userService.saveUser(user);
        return new ResponseBean();
    }
}
