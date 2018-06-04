package com.chiba.domain;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/4              
 *  Description: 
 *****************************************/
@Data
@Entity
@Table(name = "sys_role")
@ToString
public class Role extends ExtendEntity{
    @Column(name = "name")
    private String name;

    @Column(name = "code", unique = true, nullable = false)
    private String code; //角色编码

    @Column(name = "remark")
    private String remark; //角色描述

    @ManyToMany(targetEntity = Resource.class, fetch = FetchType.EAGER)
    @JoinTable(name = "sys_role_resource", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "resource_id"))
    private List<Resource> resourceList;
}
