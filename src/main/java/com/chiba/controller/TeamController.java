package com.chiba.controller;

import com.chiba.bean.ResponseBean;
import com.chiba.bean.SelectBean;
import com.chiba.domain.Team;
import com.chiba.service.CustomUserService;
import com.chiba.service.TeamService;
import com.chiba.utils.SysUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLDecoder;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/15              
 *  Description: 
 *****************************************/
@Controller
@Slf4j
public class TeamController {

    @Autowired
    private TeamService teamService;

    @GetMapping("teams/segment_team_info")
    public String showTeamInfoPage(Long teamId, ModelMap map) {
        Team team = teamService.getTeamById(teamId);
        map.put("team", team);
        return "segment/seg_team_info";
    }
}
