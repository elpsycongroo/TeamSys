package com.chiba.domain;

import com.chiba.bean.CustomGrantedAuthority;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/4              
 *  Description: 
 *****************************************/
@Entity
@Data
@Table(name = "sys_user")
@ToString
public class User extends ExtendEntity implements UserDetails {
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "dept")
    private String dept;

    @Column(name = "true_name")
    private String trueName;

    @Column(name = "clan")
    private String clan;

    @Column(name = "email")
    private String email;

    @Column(name = "game_id")
    private String gameId;

    /**
     * 本日可创建车队数量
     */
    @Column(name = "create_oper_left")
    private int createOperLeft;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role role = this.getRole();
        List<Resource> resources = role.getResourceList();
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        resources.forEach(r -> grantedAuthorities.add(new CustomGrantedAuthority(r.getCode(), r.getMethod())));
        return grantedAuthorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isDeleteStatus();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
