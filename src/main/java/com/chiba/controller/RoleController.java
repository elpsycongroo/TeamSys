package com.chiba.controller;

import com.chiba.domain.Role;
import com.chiba.service.RoleService;
import com.chiba.utils.MD5Util;
import com.chiba.utils.SysUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/27              
 *  Description: 
 *****************************************/
@Controller
@RequestMapping("/roles")
@Slf4j
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/segment_role_info")
    public String getRoleInfoPage(Long roleId, ModelMap map) {
        if (roleId != null) {
            Role role = roleService.findRoleById(roleId);
            map.put("role", role);
        } else {
            map.put("code", "ROLE_" + SysUtils.generateShortUuid());
            map.put("role", new Role());
        }
        return "segment/seg_role_info";
    }
}
