package com.chiba.controller;

import com.chiba.bean.ResponseBean;
import com.chiba.bean.SelectBean;
import com.chiba.domain.Team;
import com.chiba.service.CustomUserService;
import com.chiba.service.TeamService;
import com.chiba.utils.SysUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/15              
 *  Description: 
 *****************************************/
@Controller
@Slf4j
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @GetMapping("/segment_team_info")
    public String showTeamInfoPage(Long teamId, ModelMap map) {
        Team team = teamService.getTeamById(teamId);
        map.put("team", team);
        return "segment/seg_team_info";
    }

    @GetMapping("/segment_team_create")
    public String showCreateTeamPage(ModelMap map) {
        String msg = teamService.checkCreateTeam();
        if (null != msg) {
            map.addAttribute("flag", true);
            map.addAttribute("info", msg);
        } else {
            map.addAttribute("flag", false);
            map.addAttribute("code", SysUtils.generateShortUuid());
        }
        return "segment/seg_team_create";
    }
}
