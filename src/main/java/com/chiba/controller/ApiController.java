package com.chiba.controller;

import com.chiba.bean.Constant;
import com.chiba.bean.ResponseBean;
import com.chiba.bean.SelectBean;
import com.chiba.bean.ValidatorBean;
import com.chiba.domain.Team;
import com.chiba.domain.User;
import com.chiba.service.CustomUserService;
import com.chiba.service.RoleService;
import com.chiba.service.TeamService;
import com.chiba.utils.MD5Util;
import com.chiba.utils.SysUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;

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

    @Autowired
    private TeamService teamService;

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

    @GetMapping("check_email_reg")
    public ValidatorBean checkEmailReg(String email) {
        if (null != userService.getUserByEmail(email)) {
            return new ValidatorBean(false);
        }
        return new ValidatorBean(true);
    }

    @GetMapping("check_email_forget")
    public ValidatorBean checkEmailForget(String email) {
        if (null != userService.getUserByEmail(email)) {
            return new ValidatorBean(true);
        }
        return new ValidatorBean(false);
    }

    @PostMapping("register")
    public ResponseBean register(User user) {
        user.setPassword(MD5Util.encode(user.getPassword()));
        user.setRole(roleService.findByCode("ROLE_user"));
        user.setTrueName(user.getUsername());
        user.setCreateOperLeft(3);
        user.setEmailValidation(false);
        user.setForgetKeyValid(false);
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        now.add(Calendar.MINUTE, -5);
        user.setKeyGenTime(now.getTime());
        userService.saveUser(user);
        return new ResponseBean();
    }

    //========================================USER========================================
    @GetMapping("/users/name")
    public String getUserList (String username, Integer rows, Integer page, String sidx, String sord) {
        try {
            SelectBean selectBean = new SelectBean(sidx, sord, page, rows);
            selectBean.getParam().put("username", URLDecoder.decode(username, "UTF-8"));
            Page<User> userPage = userService.getUserList(selectBean);
            return userService.getJsonResult(userPage, Constant.JSON_USER_TYPE_SELECT_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return "error";
        }
    }

    @PutMapping("/users/pwd")
    public ResponseBean changePwd (User user) {
        try {
            return userService.changePwd(user);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseBean(Constant.FAILED, "出现异常");
        }
    }

    @PutMapping("/users/info")
    public ResponseBean changeUsersInfo(User user) {
        try {
            return userService.editUserInfo(user);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseBean(Constant.FAILED, "出现异常");
        }
    }

    @PostMapping("/users/email_address")
    public ResponseBean sendValidateEmailAddress() {
        try {
            return userService.sendEmailAddressValidateEmail();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseBean(Constant.FAILED, "出现异常");
        }
    }

    @PostMapping("/users/pwd_forget")
    public ResponseBean sendValidatePwdForget(String email) {
        try {
            return userService.sendForgetEmail(email);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseBean(Constant.FAILED, "出现异常");
        }
    }

    @PutMapping("/users")
    public ResponseBean editUser() {
        return new ResponseBean();
    }

    //========================================TEAM========================================
    @GetMapping("/teams")
    public String getTeamList(String teamName, Integer deleteStatus, Long leader, Integer type, Integer rows, Integer page, String sidx, String sord) {
        try {
            SelectBean selectBean = new SelectBean(sidx, sord, page, rows);
            if (!SysUtils.isEmpty(teamName)) {
                selectBean.getParam().put("teamName", URLDecoder.decode(teamName, "UTF-8"));
            }
            if (leader != null) {
                selectBean.getParam().put("leader", userService.getUserById(leader).isPresent() ? userService.getUserById(leader).get() : null);
            }
            if (null != deleteStatus) {
                selectBean.getParam().put("deleteStatus", deleteStatus);
            }
            if (null != type) {
                selectBean.getParam().put("type", type);
            }
            Page<Team> teamPage = teamService.getTeamList(selectBean);
            return teamService.getJsonResult(teamPage);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return "error";
        }
    }

    @PostMapping("/teams/join")
    public ResponseBean joinTeam(Long teamId, Long userId) {
        try {
            return teamService.joinTeam(userId, teamId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseBean(Constant.FAILED, "出现异常");
        }
    }

    @PostMapping("/teams/leave")
    public ResponseBean leaveTeam(Long teamId, Long userId) {
        return teamService.leaveTeam(userId, teamId);
    }

}
