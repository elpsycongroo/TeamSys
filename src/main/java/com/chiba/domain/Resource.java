package com.chiba.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
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
@Table(name = "sys_resource")
@ToString
@EqualsAndHashCode(exclude = {"id", "parentResource", "childResourceList"})
public class Resource extends ExtendEntity {
    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "url")
    private String url;

    @Column(name = "custom_id_name")
    private String customIdName;

    @Column(name = "icon_class")
    private String iconClass;

    /**
     * HTTP请求方法（GET POST PUT DELETE）
     */
    @Column(name = "method")
    private String method;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_res_id")
    private Resource parentResource; //父资源

    @OneToMany(mappedBy = "parentResource", fetch = FetchType.EAGER)
    private List<Resource> childResourceList; //子资源
}
