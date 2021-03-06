package com.chiba.controller;

import com.chiba.bean.Constant;
import com.chiba.bean.ResponseBean;
import com.chiba.bean.SelectBean;
import com.chiba.bean.ValidatorBean;
import com.chiba.domain.Role;
import com.chiba.domain.Team;
import com.chiba.domain.User;
import com.chiba.service.CustomUserService;
import com.chiba.service.ResourceService;
import com.chiba.service.RoleService;
import com.chiba.service.TeamService;
import com.chiba.utils.MD5Util;
import com.chiba.utils.SysUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
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

    @Autowired
    private ResourceService resourceService;

    @InitBinder
    protected void init(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

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
        User user = userService.getUserByEmail(email);
        if (null != user) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!user.getUsername().equals(userDetails.getUsername())) {
                return new ValidatorBean(false);
            }
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

    @GetMapping("check_role_code")
    public ValidatorBean checkRoleCode(String code, Long id) {
        Role role = roleService.findByCode(code);
        if (null != role) {
            if (null != id && !role.getId().equals(id)) {
                return new ValidatorBean(false);
            }
        }
        return new ValidatorBean(true);
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
        user.setAddTime(now.getTime());
        now.add(Calendar.MINUTE, -5);
        user.setKeyGenTime(now.getTime());
        userService.saveUser(user);
        return new ResponseBean();
    }

    //========================================USER========================================
    @GetMapping("/users/name")
    public String getUserList(String username, Integer rows, Integer page, String sidx, String sord) {
        try {
            SelectBean selectBean = new SelectBean(sidx, sord, page, rows);
            selectBean.getParam().put("username", URLDecoder.decode(username, "UTF-8"));
            Page<User> userPage = userService.getUserList(selectBean, Constant.JSON_USER_TYPE_SELECT_NAME);
            return userService.getJsonResult(userPage, Constant.JSON_USER_TYPE_SELECT_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return "error";
        }
    }

    @PutMapping("/users/pwd")
    public ResponseBean changePwd(User user) {
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

    @GetMapping("/users")
    public String getUserList(Long username, String clan, Long role, Integer deleteStatus, Integer rows, Integer page, String sidx, String sord) {
        try {
            SelectBean selectBean = new SelectBean(sidx, sord, page, rows);
            if (null != username) {
                selectBean.getParam().put("username", username);
            }
            if (!SysUtils.isEmpty(clan)) {
                selectBean.getParam().put("clan", clan);
            }
            if (null != role) {
                selectBean.getParam().put("role", role);
            }
            if (null != deleteStatus) {
                selectBean.getParam().put("deleteStatus", deleteStatus);
            }
            Page<User> userPage = userService.getUserList(selectBean, Constant.JSON_USER_TYPE_SELECT_LIST);
            return userService.getJsonResult(userPage, Constant.JSON_USER_TYPE_SELECT_LIST);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return "error";
        }
    }

    @PutMapping("/users")
    public ResponseBean editUser(User user) {
        try {
            return userService.manageUserInfo(user);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseBean(Constant.FAILED, "出现异常");
        }
    }

    @PostMapping("/users/deleteStatus")
    public ResponseBean forbidUsers(Long userId, boolean status) {
        try {
            return userService.forbidUser(userId, status);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseBean(Constant.FAILED, "出现异常");
        }
    }

    @GetMapping("/users/clan")
    public String getClanList(String clan, Integer rows, Integer page, String sidx, String sord) {
        try {
            SelectBean selectBean = new SelectBean(sidx, sord, page, rows);
            if (!SysUtils.isEmpty(clan)) {
                selectBean.getParam().put("clan", URLDecoder.decode(clan, "UTF-8"));
            }
            Page<String> clanPage = userService.getUserClanList(selectBean);
            return userService.getJsonStringResult(clanPage);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return "error";
        }
    }

    //========================================TEAM========================================
    @GetMapping("/teams")
    public String getTeamList(String teamName, String code, Integer deleteStatus, Long leader, Integer type, Integer rows, Integer page, String sidx, String sord) {
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
            if (!SysUtils.isEmpty(code)) {
                selectBean.getParam().put("code", code);
            }
            Page<Team> teamPage = teamService.getTeamList(selectBean);
            return teamService.getJsonResult(teamPage);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return "error";
        }
    }

    @PostMapping("/teams")
    public ResponseBean createTeam(Team team) {
        try {
            return teamService.createTeam(team);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseBean(Constant.FAILED, "出现异常");
        }
    }

    @PutMapping("/teams")
    public ResponseBean editTeam(Team team) {
        try {
            return teamService.editTeamInfo(team);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseBean(Constant.FAILED, "出现异常");
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
        try {
            return teamService.leaveTeam(userId, teamId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseBean(Constant.FAILED, "出现异常");
        }
    }

    @PostMapping("/teams/kick")
    public ResponseBean kickOutTeam(Long teamId, Long userId) {
        try {
            return teamService.kickoutTeam(teamId, userId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseBean(Constant.FAILED, "出现异常");
        }
    }

    @PostMapping("/teams/dis")
    public ResponseBean disTeam(Long teamId) {
        try {
            return teamService.disTeam(teamId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseBean(Constant.FAILED, "出现异常");
        }
    }

    //========================================ROLE========================================
    @GetMapping("/roles/name")
    public String getRolesNameList(String name, Integer rows, Integer page, String sidx, String sord) {
        try {
            SelectBean selectBean = new SelectBean(sidx, sord, page, rows);
            if (!SysUtils.isEmpty(name)) {
                selectBean.getParam().put("name", URLDecoder.decode(name, "UTF-8"));
            }
            Page<Role> rolePage = roleService.getRoleList(selectBean, Constant.JSON_ROLE_TYPE_SELECT_NAME);
            return roleService.getJsonResult(rolePage, Constant.JSON_ROLE_TYPE_SELECT_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return "error";
        }
    }

    @GetMapping("/roles")
    public String getRolesList(Integer rows, Integer page, String sidx, String sord) {
        try {
            SelectBean selectBean = new SelectBean(sidx, sord, page, rows);
            Page<Role> rolePage = roleService.getRoleList(selectBean, Constant.JSON_ROLE_TYPE_SELECT_LIST);
            return roleService.getJsonResult(rolePage, Constant.JSON_ROLE_TYPE_SELECT_LIST);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return "error";
        }
    }

    @PostMapping("/roles")
    public ResponseBean addRoles(Role role,  String map_content, HttpServletRequest request) {
        try {
            resourceService.processResourceList(role, map_content);
            User currentUser = (User) request.getSession().getAttribute("currentUser");
            resourceService.saveRole(role, currentUser);
            return new ResponseBean();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseBean(Constant.FAILED, "出现异常");
        }
    }

    @PutMapping("/roles")
    public ResponseBean editRoles(Role role, String map_content, HttpServletRequest request) {
        return addRoles(role, map_content, request);
    }

}
