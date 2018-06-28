package com.chiba.controller;

import com.chiba.dao.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/27              
 *  Description: 
 *****************************************/
@Controller
@RequestMapping("/users")
@Slf4j
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;
}
