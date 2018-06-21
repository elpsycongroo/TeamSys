package com.chiba.controller;

import com.chiba.bean.Constant;
import com.chiba.bean.SelectBean;
import com.chiba.domain.User;
import com.chiba.service.CustomUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/15              
 *  Description: 
 *****************************************/
@Controller
@Slf4j
public class UserController {

    @Autowired
    private CustomUserService userService;

    @GetMapping("/user/team")
    public String showPrivateTeamPage(ModelMap map) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        map.addAttribute("team", userService.getTeamByUserName(user.getUsername()));
        return "private_team";
    }

}