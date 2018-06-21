package com.chiba.config;

import com.chiba.service.CustomSecurityInterceptor;
import com.chiba.service.CustomUserService;
import com.chiba.service.LoginAuthenticationFilter;
import com.chiba.service.ResourceService;
import com.chiba.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/5              
 *  Description: 
 *****************************************/
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    UserDetailsService userService() {
        return new CustomUserService();
    }

    @Bean
    ResourceService resourceService() {
        return new ResourceService();
    }

    @Bean
    CustomUserService customUserService() {
        return new CustomUserService();
    }

    @Autowired
    private CustomSecurityInterceptor customSecurityInterceptor;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService()).passwordEncoder(new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return MD5Util.encode((String) rawPassword);
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return encodedPassword.equals(MD5Util.encode((String) rawPassword));
            }
        });
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/*/assets/**", "/assets/**", "/**/favicon.ico", "/register");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().ignoringAntMatchers("/api/**", "/login", "/register", "/kaptcha-image/**");
        http.authorizeRequests()
                .antMatchers(
                        "/kaptcha-image/**", "/register", "/api/**").permitAll()
                .anyRequest().authenticated()
                .and().logout().logoutUrl("/logout").permitAll().logoutSuccessUrl("/login")//定义logout不需要验证
                .and().addFilterBefore(new LoginAuthenticationFilter("/login", "/login?error", customUserService(), resourceService()), UsernamePasswordAuthenticationFilter.class)
                .formLogin().loginPage("/login").defaultSuccessUrl("/dashboard", true).failureUrl("/login?error").permitAll()
                .and().rememberMe()
                .and().logout().permitAll().logoutSuccessUrl("/login");
        http.addFilterBefore(customSecurityInterceptor, FilterSecurityInterceptor.class);

        http.sessionManagement().maximumSessions(1).expiredUrl("/login");
    }
}
