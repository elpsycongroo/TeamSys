package com.chiba.domain;

import com.chiba.bean.CustomGrantedAuthority;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
public class User extends ExtendEntity implements UserDetails, Serializable {

    private static final long serialVersionUID = 7021091094681733360L;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "true_name")
    private String trueName;

    @Column(name = "clan")
    private String clan;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "game_id", unique = true)
    private String gameId;

    /**
     * 邮箱是否验证过
     */
    @Column(name = "email_validation", nullable = false)
    private boolean emailValidation;

    /**
     * 数据库中存储本次校验key
     */
    @Column(name = "email_validation_key")
    private String emailValidationKey;

    /**
     * 校验key生成时间
     */
    @Column(name = "key_gen_time")
    private Date keyGenTime;

    /**
     * 忘记密码校验key
     */
    @Column(name = "forget_key")
    private String forgetKey;

    /**
     * 忘记密码校验key是否被使用过
     */
    @Column(name = "forget_key_valid", nullable = false)
    private boolean forgetKeyValid;

    /**
     * 忘记密码校验key生成时间
     */
    @Column(name = "forget_key_gen_time")
    private Date forgetKeyGenTime;

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

    @Override
    public boolean equals(Object rhs) {
        return rhs instanceof User && username.equals(((User) rhs).username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

}
