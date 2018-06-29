package com.chiba.controller;

import com.chiba.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/29              
 *  Description: 
 *****************************************/
@Controller
@RequestMapping("/resources")
@Slf4j
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @GetMapping("/ztree_list")
    @ResponseBody
    public String getZtreeList(Long id) {
        try {
            return resourceService.getResourceTreeByRoleId(id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return "error";
        }
    }

    @GetMapping("/ztree_checked_list")
    @ResponseBody
    public String getZtreeCheckedList(Long id) {
        return getZtreeList(id);
    }

}
