package com.chiba.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/4              
 *  Description: 
 *****************************************/
@MappedSuperclass
@Data
@ToString
@EqualsAndHashCode
public class ExtendEntity implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    @JsonIgnore
    private Long id;

    @Column(name = "add_time")
    @JsonIgnore
    private Date addTime;

    @Column(name = "update_time")
    @JsonIgnore
    private Date updateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "add_user_id")
    @JsonIgnore
    private User addUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_user_id")
    @JsonIgnore
    private User updateUser;

    @Column(name = "delete_status")
    @JsonIgnore
    private boolean deleteStatus;

}
