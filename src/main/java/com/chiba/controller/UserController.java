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
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/users")
public class UserController {

    @Autowired
    private CustomUserService userService;

    @GetMapping("/team")
    public String showPrivateTeamPage(ModelMap map) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        map.addAttribute("team", userService.getTeamByUserName(user.getUsername()));
        return "private_team";
    }

    @GetMapping("/info")
    public String showUserInfo(ModelMap map) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        map.addAttribute("user", userService.findUserByUsername(user.getUsername()));
        return "private_info";
    }

    @GetMapping("/segment_user_pwdinfo")
    public String showPwdInfoPage() {
        return "segment/seg_user_pwd_info";
    }

    @GetMapping("/segment_info_edit")
    public String showPrivateInfoPage(ModelMap map) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        map.addAttribute("user", userService.findUserByUsername(user.getUsername()));
        return "segment/seg_private_info_edit";
    }

    @GetMapping("/email/verify_address")
    public String verifyAddress(String username, String link, String forget, ModelMap map) {
        if (null != forget) {
            map.put("data",userService.verifyForgetKey(username, link));
        } else {
            map.put("data", userService.verifyEmailKey(username, link));
        }
        return "segment/seg_verify_address";
    }
}
