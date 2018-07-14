package com.chiba.config;

import com.chiba.bean.EmailBean;
import com.chiba.domain.Team;
import com.chiba.domain.User;
import com.chiba.service.TeamService;
import com.chiba.utils.SysUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/7/14              
 *  Description: 
 *****************************************/
@Configuration
@EnableScheduling
@Slf4j
public class ScheduledConfig {

    @Autowired
    private TeamService teamService;
    @Autowired
    private MailConfig mailConfig;

    /**
     * 15分钟处理一次即将到达车队的通知
     */
    @Scheduled(cron = "0 */15 *  * * * ")
    public void broadCastLimitTimeTeam() {
        log.info ("Scheduling Tasks Examples By Cron: The time is now " + new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 45);
        Date limitDate = calendar.getTime();
        List<Team> teamList = teamService.getTeamByDeleteStatus(false);
        teamList.stream().filter(t -> t.getLimitTime().before(limitDate)).forEach(this::sendLimitDateEmail);
    }

    @Async
    private void sendLimitDateEmail(Team team) {
        try {
            List<User> users = team.getPlayerList();
            StringBuffer stringBuffer = new StringBuffer("");
            for (User u : users) {
                if (u.isEmailValidation()) {
                    stringBuffer.append(u.getEmail()).append(";");
                }
            }
            if (!SysUtils.isEmpty(stringBuffer.toString())) {
                EmailBean emailBean = new EmailBean();
                emailBean.setContent("<h3>您加入的【" + team.getTeamName() + "】小队，开车时间即将到达，" +
                        "开车时间是：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(team.getLimitTime()) +
                        "</h3><br>(该邮件为系统自动发送，请勿回复)<br>窝窝屎组队系统");
                emailBean.setSubject("注意：您加入的车队开车时间即将到达！——感谢您使用窝窝屎组队系统");
                emailBean.setReceiver(stringBuffer.toString());
                mailConfig.sendHtmlMail(emailBean);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

}
