package com.chiba.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chiba.bean.Constant;
import com.chiba.bean.ResponseBean;
import com.chiba.bean.SelectBean;
import com.chiba.dao.TeamRepository;
import com.chiba.dao.UserRepository;
import com.chiba.domain.Team;
import com.chiba.domain.User;
import com.chiba.utils.SysUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/15              
 *  Description: 
 *****************************************/
@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserRepository userRepository;

    public Team getTeamById(Long id) {
        return teamRepository.getOne(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateTeam(Team team) {
        teamRepository.save(team);
    }

    public String getJsonResult(Page<Team> teamPage) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", teamPage.getTotalPages());
        jsonObject.put("page", teamPage.getNumber());
        jsonObject.put("records", teamPage.getTotalElements());
        JSONArray jsonArray = new JSONArray();
        for (Team t : teamPage.getContent()) {
            Map<String, Object> json = new HashMap<>();
            json.put("id", t.getId());
            json.put("teamName", t.getTeamName());
            json.put("addUser", t.getAddUser().getGameId());
            json.put("type", t.getType());
            json.put("addTime", SysUtils.dateFormat("yyyy-MM-dd HH:mm:ss", t.getAddTime()));
            json.put("limitTime", SysUtils.dateFormat("yyyy-MM-dd HH:mm:ss", t.getLimitTime()));
            json.put("updateTime", SysUtils.dateFormat("yyyy-MM-dd HH:mm:ss", t.getUpdateTime()));
            json.put("posLeft", t.getPosLeft());
            json.put("deleteStatus", t.isDeleteStatus() ? "已截止" : "招募中");
            json.put("code", t.getCode());
            json.put("action", 0);
            jsonArray.add(json);
        }
        jsonObject.put("rows", jsonArray);
        return jsonObject.toJSONString();
    }

    public Page<Team> getTeamList(final SelectBean selectBean) {
        String sord = selectBean.getSord();
        String sidx = selectBean.getSidx();
        Integer page = selectBean.getPage();
        Integer rows = selectBean.getRows();
        final Map<String, Object> param = selectBean.getParam();

        Pageable pageable = PageRequest.of(page, rows, new Sort("asc".equals(sord) ? Sort.Direction.ASC : Sort.Direction.DESC, sidx));
        return teamRepository.findAll((Specification<Team>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (!SysUtils.isEmpty((String) param.get("teamName"))) {
                list.add(criteriaBuilder.like(root.get("teamName").as(String.class), "%" + param.get("teamName") + "%"));
            }
            if (null != param.get("deleteStatus")) {
                list.add(criteriaBuilder.equal(root.get("deleteStatus").as(Integer.class), param.get("deleteStatus")));
            }
            if (null != param.get("leader")) {
                list.add(criteriaBuilder.equal(root.get("addUser").as(User.class), param.get("leader")));
            }
            if (null != param.get("type")) {
                list.add(criteriaBuilder.equal(root.get("type").as(Integer.class), param.get("type")));
            }
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseBean joinTeam(Long userId, Long teamId) {
        ResponseBean responseBean = new ResponseBean();
        User user = userRepository.getOne(userId);
        Team team = teamRepository.getOne(teamId);
        Date now = new Date();
        if (user.getTeam() != null) {
            //判断该user是否已经加入其它队伍
            responseBean.setStatus(Constant.FAILED);
            responseBean.setMsg("您已加入其它队伍，请退出再加入其它队伍");
        } else if (team.getPosLeft() <= 0) {
            //判断team是否有空位
            responseBean.setStatus(Constant.FAILED);
            responseBean.setMsg("该队伍已没有空位，请选择其它队伍");
        } else if (team.isDeleteStatus() || now.after(team.getLimitTime())) {
            //判断是否已过截止时间
            responseBean.setStatus(Constant.FAILED);
            responseBean.setMsg("该队伍已过截止时间或已被关闭，请选择其它队伍");
        } else if (user.getCreateOperLeft() < 1) {
            //判断是否达到创建和加入次数上限
            responseBean.setStatus(Constant.FAILED);
            responseBean.setMsg("您本日创建和加入队伍次数达到上限，请明日再试");
        } else {
            //处理加入逻辑 避免踩同步坑
            joinTeamSync(user, team);
        }
        return responseBean;
    }

    private synchronized void joinTeamSync(User user, Team team) {
        user.setTeam(team);
        team.setPosLeft(team.getPosLeft() - 1);
        user.setCreateOperLeft(user.getCreateOperLeft() - 1);
        userRepository.save(user);
        teamRepository.save(team);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseBean leaveTeam(Long userId, Long teamId) {
        ResponseBean responseBean = new ResponseBean();
        User user = userRepository.getOne(userId);
        Team team = teamRepository.getOne(teamId);
        if (!user.getTeam().getId().equals(teamId)) {
            //判断是否是当前用户加入的队伍
            responseBean.setStatus(Constant.FAILED);
            responseBean.setMsg("您并未加入您要退出的队伍，可能是该队伍已被解散，或您的打开方式不对");
        } else if (team.getAddUser().getId().equals(userId)) {
            //防止别有用心的请求，应该不存在吧
            responseBean.setStatus(Constant.FAILED);
            responseBean.setMsg("您是该队伍的队长，不能通过该方式退出队伍，或您的打开方式不对");
        } else {
            leaveTeamSync(user, team);
            responseBean.setMsg(String.valueOf(user.getCreateOperLeft()));
        }
        return responseBean;
    }

    private synchronized void leaveTeamSync(User user, Team team) {
        Date now = new Date();
        if (team.isDeleteStatus() || now.after(team.getLimitTime())) {
            user.setCreateOperLeft(user.getCreateOperLeft() + 1);
        }
        user.setTeam(null);
        team.setPosLeft(team.getPosLeft() + 1);
        userRepository.save(user);
        teamRepository.save(team);
    }
}
