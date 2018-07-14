package com.chiba.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chiba.bean.Constant;
import com.chiba.bean.EmailBean;
import com.chiba.bean.ResponseBean;
import com.chiba.bean.SelectBean;
import com.chiba.config.MailConfig;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.text.SimpleDateFormat;
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
    @Autowired
    private MailConfig mailConfig;

    public Team getTeamById(Long id) {
        return teamRepository.getOne(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateTeam(Team team) {
        teamRepository.save(team);
    }

    public List<Team> getTeamByDeleteStatus(boolean deleteStatus) {
        return teamRepository.findByDeleteStatus(deleteStatus);
    }

    public String getJsonResult(Page<Team> teamPage) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", teamPage.getTotalPages());
        jsonObject.put("page", teamPage.getNumber() + 1);
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
            if (!SysUtils.isEmpty((String) param.get("code"))) {
                list.add(criteriaBuilder.like(root.get("code").as(String.class), "%" + param.get("code") + "%"));
            }
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseBean joinTeam(Long userId, Long teamId) throws Exception {
        ResponseBean responseBean = new ResponseBean();
        User user = userRepository.getOne(userId);
        Team team = teamRepository.getOne(teamId);
        Date now = new Date();
        if (SysUtils.isEmpty(user.getGameId())) {
            //判断该user是否填写游戏ID
            responseBean.setStatus(Constant.FAILED);
            responseBean.setMsg("您没有填写游戏ID，无法加入队伍");
        }
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
            responseBean.setMsg("该队伍已过截止时间或已被解散，请选择其它队伍");
        } else if (user.getCreateOperLeft() < 1) {
            //判断是否达到创建和加入次数上限
            responseBean.setStatus(Constant.FAILED);
            responseBean.setMsg("您本日创建和加入队伍次数达到上限，请明日再试");
        } else {
            //处理加入逻辑 避免踩同步坑
            joinTeamSync(user, team);
            sendJoinTeamEmail(team, user);
        }
        return responseBean;
    }

    private synchronized void joinTeamSync(User user, Team team) {
        user.setTeam(team);
        team.setPosLeft(team.getPosLeft() - 1);
        if (user.getCreateOperLeft() < Constant.CREATE_TEAM_LIMIT) {
            user.setCreateOperLeft(user.getCreateOperLeft() - 1);
        }
        team.setUpdateTime(new Date());
        userRepository.save(user);
        teamRepository.save(team);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseBean leaveTeam(Long userId, Long teamId) throws Exception {
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
            sendLeaveTeamEmail(team, user);
            responseBean.setMsg(String.valueOf(user.getCreateOperLeft()));
        }
        return responseBean;
    }

    private synchronized void leaveTeamSync(User user, Team team) {
        Date now = new Date();
        if ((team.isDeleteStatus() || now.after(team.getLimitTime())) && user.getCreateOperLeft() < 3) {
            user.setCreateOperLeft(user.getCreateOperLeft() + 1);
        }
        user.setTeam(null);
        team.setPosLeft(team.getPosLeft() + 1);
        team.setUpdateTime(now);
        userRepository.save(user);
        teamRepository.save(team);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseBean kickoutTeam(Long teamId, Long userId) throws Exception {
        Team team = teamRepository.getOne(teamId);
        User kickUser = userRepository.getOne(userId);
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User leader = userRepository.findByUsername(userDetails.getUsername());
        ResponseBean responseBean = new ResponseBean();
        if (userId.equals(leader.getId())) {
            responseBean.setStatus(Constant.FAILED);
            responseBean.setMsg("你不能踢出自己，停止你的骚操作");
        } else if (!team.getAddUser().getId().equals(leader.getId())) {
            responseBean.setStatus(Constant.FAILED);
            responseBean.setMsg("你不是该小队的队长");
        } else if (team.isDeleteStatus()) {
            responseBean.setStatus(Constant.FAILED);
            responseBean.setMsg("该队伍已经截止或解散");
        } else {
            kickUser.setTeam(null);
            if (kickUser.getCreateOperLeft() < Constant.CREATE_TEAM_LIMIT) {
                kickUser.setCreateOperLeft(kickUser.getCreateOperLeft() + 1);
            }
            team.setPosLeft(team.getPosLeft() + 1);
            team.setUpdateTime(new Date());
            sendKickOutTeamEmail(team, kickUser);
            userRepository.save(kickUser);
            teamRepository.save(team);
        }
        return responseBean;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseBean disTeam(Long teamId) throws Exception {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        Team team = teamRepository.getOne(teamId);
        ResponseBean responseBean = new ResponseBean();
        if (!user.getId().equals(team.getAddUser().getId())) {
            responseBean.setStatus(Constant.FAILED);
            responseBean.setMsg("你不是这个队伍的队长，无权解散该队（你到底是谁？）");
        } else {
            for (User u : team.getPlayerList()) {
                //这里是一个异步方法 可能有坑
                sendDisTeamEmail(team, u);
                if (u.getId().equals(user.getId())) {
                    if (team.isDeleteStatus() && u.getCreateOperLeft() < Constant.CREATE_TEAM_LIMIT) {
                        u.setCreateOperLeft(u.getCreateOperLeft() + 1);
                    }
                } else if (u.getCreateOperLeft() < Constant.CREATE_TEAM_LIMIT) {
                    u.setCreateOperLeft(u.getCreateOperLeft() + 1);
                }
                u.setTeam(null);
                userRepository.save(u);
            }
            team.setDeleteStatus(true);
            team.setUpdateTime(new Date());
            teamRepository.save(team);
            responseBean.setMsg(String.valueOf(user.getCreateOperLeft()));
        }
        return responseBean;
    }

    @Async
    private void sendJoinTeamEmail(Team team, User joinUser) throws Exception {
        User leader = team.getAddUser();
        if (null != leader && null != leader.getEmail() && leader.isEmailValidation()) {
            EmailBean emailBean = new EmailBean();
            emailBean.setContent("<h3>新队员 " + (SysUtils.isEmpty(joinUser.getClan()) ? "" : "[" + joinUser.getClan() + "]") +
                    joinUser.getGameId() + " 加入了您的队伍【" + team.getTeamName() + "】！" + "<br>目前您的队伍尚余空位：" + team.getPosLeft() +
                    "</h3><br>(该邮件为系统自动发送，请勿回复)<br>窝窝屎组队系统");
            emailBean.setReceiver(leader.getEmail());
            emailBean.setSubject("有队员加入了你的队伍！——感谢您使用窝窝屎组队系统");
            mailConfig.sendHtmlMail(emailBean);
        }
    }

    @Async
    private void sendLeaveTeamEmail(Team team, User leftUser) throws Exception {
        User leader = team.getAddUser();
        if (null != leader && null != leader.getEmail() && leader.isEmailValidation()) {
            EmailBean emailBean = new EmailBean();
            emailBean.setContent("<h3>队员 " + (SysUtils.isEmpty(leftUser.getClan()) ? "" : "[" + leftUser.getClan() + "]") +
                    leftUser.getGameId() + " 退出了您的队伍【" + team.getTeamName() + "】！" + "<br>目前您的队伍尚余空位：" + team.getPosLeft() +
                    "</h3><br>(该邮件为系统自动发送，请勿回复)<br>窝窝屎组队系统");
            emailBean.setReceiver(leader.getEmail());
            emailBean.setSubject("有队员退出了你的队伍！——感谢您使用窝窝屎组队系统");
            mailConfig.sendHtmlMail(emailBean);
        }
    }

    @Async
    private void sendKickOutTeamEmail(Team team, User kickedUser) throws Exception {
        if (kickedUser.isEmailValidation()) {
            EmailBean emailBean = new EmailBean();
            emailBean.setContent("<h3>您被【" + team.getTeamName() + "】小队的队长" +
                    (SysUtils.isEmpty(team.getAddUser().getClan()) ? "" : "[" + team.getAddUser().getClan() + "]") +
                    team.getAddUser().getGameId() + "移出了该小队，您可以加入其它队伍。" +
                    "</h3><br>(该邮件为系统自动发送，请勿回复)<br>窝窝屎组队系统");
            emailBean.setReceiver(kickedUser.getEmail());
            emailBean.setSubject("您被队长移出了车队！——感谢您使用窝窝屎组队系统");
            mailConfig.sendHtmlMail(emailBean);
        }
    }

    @Async
    private void sendDisTeamEmail(Team team, User user) throws Exception {
        if (user.isEmailValidation()) {
            EmailBean emailBean = new EmailBean();
            emailBean.setContent("<h3>您加入的【" + team.getTeamName() + "】小队被队长" +
                    (SysUtils.isEmpty(team.getAddUser().getClan()) ? "" : "[" + team.getAddUser().getClan() + "]") +
                    team.getAddUser().getGameId() + "解散，您可以加入其它队伍。" +
                    "</h3><br>(该邮件为系统自动发送，请勿回复)<br>窝窝屎组队系统");
            emailBean.setSubject("您加入的车队解散了！——感谢您使用窝窝屎组队系统");
            emailBean.setReceiver(user.getEmail());
            mailConfig.sendHtmlMail(emailBean);
        }
    }

    @Async
    private void sendChangeLeaderEmail(Team team, User originUser, User changedUser) throws Exception {
        if (changedUser.isEmailValidation()) {
            EmailBean emailBean = new EmailBean();
            emailBean.setContent("<h3>您加入的【" + team.getTeamName() + "】小队的创建者" +
                    (SysUtils.isEmpty(originUser.getClan()) ? "" : "[" + originUser.getClan() + "]") +
                    originUser.getGameId() + "将队长转移给您，您现在拥有该队伍的管理权限。" +
                    "</h3><br>(该邮件为系统自动发送，请勿回复)<br>窝窝屎组队系统");
            emailBean.setSubject("您加入的车队更换了队长！——感谢您使用窝窝屎组队系统");
            emailBean.setReceiver(changedUser.getEmail());
            mailConfig.sendHtmlMail(emailBean);
        }
    }

    @Async
    private void sendChangeLimitDateEmail(Team team) throws Exception {
        List<User> users = team.getPlayerList();
        StringBuffer stringBuffer = new StringBuffer("");
        for (User u : users) {
            if (u.isEmailValidation()) {
                stringBuffer.append(u.getEmail()).append(";");
            }
        }
        if (!SysUtils.isEmpty(stringBuffer.toString())) {
            EmailBean emailBean = new EmailBean();
            emailBean.setContent("<h3>您加入的【" + team.getTeamName() + "】小队，开车时间已被更改，" +
                    "现在的开车时间是：" +  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(team.getLimitTime()) +
                    "</h3><br>(该邮件为系统自动发送，请勿回复)<br>窝窝屎组队系统");
            emailBean.setSubject("您加入的车队开车时间被更改！——感谢您使用窝窝屎组队系统");
            emailBean.setReceiver(stringBuffer.toString());
            mailConfig.sendHtmlMail(emailBean);
        }
    }

    public String checkCreateTeam() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        String msg = null;
        if (null != user.getTeam()) {
            msg = "您已加入队伍，不能再创建队伍";
        } else if (user.getCreateOperLeft() < 1) {
            msg = "您本日剩余可创建或加入队伍次数已用完，请明日再试";
        } else if (SysUtils.isEmpty(user.getGameId())) {
            msg = "您未设置游戏ID，不能创建队伍";
        }
        return msg;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseBean createTeam(Team team) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());

        ResponseBean responseBean = new ResponseBean();
        if (null != user.getTeam()) {
            responseBean.setStatus(Constant.FAILED);
            responseBean.setMsg("您已加入队伍，不能再创建队伍");
        } else if (user.getCreateOperLeft() < 1) {
            responseBean.setStatus(Constant.FAILED);
            responseBean.setMsg("您本日剩余可创建或加入队伍次数已用完，请明日再试");
        } else if (SysUtils.isEmpty(user.getGameId())) {
            responseBean.setStatus(Constant.FAILED);
            responseBean.setMsg("您未设置游戏ID，不能创建队伍");
        } else if (team.getLimitTime().before(new Date())) {
            responseBean.setStatus(Constant.FAILED);
            responseBean.setMsg("截止日期不能早于当前日期");
        } else {
            team.setAddUser(user);
            team.setAddTime(new Date());
            teamRepository.save(team);
            user.setCreateOperLeft(user.getCreateOperLeft() - 1);
            user.setTeam(teamRepository.findByCode(team.getCode()));
            userRepository.save(user);
        }
        return responseBean;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseBean editTeamInfo(Team team) throws Exception {
        Date now = new Date();
        Team originTeam = teamRepository.getOne(team.getId());
        Calendar newLimitTime = Calendar.getInstance();
        newLimitTime.setTime(originTeam.getLimitTime());
        newLimitTime.add(Calendar.MINUTE, 60);
        ResponseBean responseBean = new ResponseBean();
        if (team.getLimitTime().before(now)) {
            responseBean.setStatus(Constant.FAILED);
            responseBean.setMsg("截止时间不得早于当前时间");
        } else if (team.getLimitTime().before(newLimitTime.getTime())) {
            responseBean.setStatus(Constant.FAILED);
            responseBean.setMsg("为了方便队员调整决策，截止日期至少要推迟1小时以上");
        } else {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User originUser = userRepository.findByUsername(userDetails.getUsername());
            User changedUser = userRepository.getOne(team.getAddUser().getId());

            originTeam.setTeamName(team.getTeamName());
            originTeam.setType(team.getType());
            originTeam.setAddUser(changedUser);
            originTeam.setLimitTime(team.getLimitTime());
            if (!changedUser.getId().equals(originUser.getId())) {
                sendChangeLeaderEmail(originTeam, originUser, changedUser);
            }
            if (team.getLimitTime().after(originTeam.getLimitTime())) {
                sendChangeLimitDateEmail(originTeam);
            }
            originTeam.setUpdateTime(now);
            originTeam.setUpdateUser(originUser);
            teamRepository.save(originTeam);
        }
        return responseBean;
    }
}
