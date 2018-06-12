package com.chiba.bean;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/12              
 *  Description: 
 *****************************************/
@Data
public class CustomGrantedAuthority implements GrantedAuthority {

    private String url;
    private String method;


    public CustomGrantedAuthority(String url, String method) {
        this.url = url;
        this.method = method;
    }

    @Override
    public String getAuthority() {
        return this.url + ";" + this.method;
    }
}
