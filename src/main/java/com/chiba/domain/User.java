package com.chiba.domain;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/4              
 *  Description: 
 *****************************************/
@Entity
@Data
@Table(name = "sys_user")
@ToString
public class User extends ExtendEntity {
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "dept")
    private String dept;

    @Column(name = "true_name")
    private String trueName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;
}
