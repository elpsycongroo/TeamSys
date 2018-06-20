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
    @Autowired
    private CustomUserService userService;

    @GetMapping("/api/teams")
    @ResponseBody
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

    @GetMapping("teams/segment_team_info")
    public String showTeamInfoPage(Long teamId, ModelMap map) {
        Team team = teamService.getTeamById(teamId);
        map.put("team", team);
        return "segment/seg_team_info";
    }

    @PutMapping("/api/leaveTeam")
    @ResponseBody
    public ResponseBean leaveTeam(Long teamId, Long userId) {
        return teamService.leaveTeam(userId, teamId);
    }
}
