package com.chiba.domain;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/14              
 *  Description: 
 *****************************************/
@Data
@Entity
@Table(name = "ts_team")
@ToString
public class Team extends ExtendEntity {

    @Column(name = "team_name")
    private String teamName;

    /**
     * 开车时间 过期自动不可加入
     */
    @Column(name = "limit_time")
    private Date limitTime;

    /**
     * 队伍类型 ： 0效率 1娱乐 2爬线 3涂油
     */
    @Column(name = "type")
    private int type;

    /**
     * 剩余位置（0.1.2）
     */
    @Column(name = "pos_left")
    private int posLeft;

    @Column(name = "code")
    private String code;

    /**
     * 当前队员名单
     */
    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<User> playerList;

    //create_by = leader;
    //delete_status = isValid
    //only createUser can update

}
