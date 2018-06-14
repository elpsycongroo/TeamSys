package com.chiba.service;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/11              
 *  Description: 
 *****************************************/
@Service
public class CustomAccessDecisionManager implements AccessDecisionManager {

    // decide 方法是判定是否拥有权限的决策方法，
    //authentication 是释CustomUserService中循环添加到 GrantedAuthority 对象中的权限信息集合.
    //object 包含客户端发起的请求的requset信息，可转换为 HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
    //configAttributes 为MyInvocationSecurityMetadataSource的getAttributes(Object object)这个方法返回的结果，此方法是为了判定
    // 用户请求的url 是否在权限表中，如果在权限表中，则返回给 decide 方法，用来判定用户是否有此权限。如果不在权限表中则放行。

    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> configAttributes)
            throws AccessDeniedException, InsufficientAuthenticationException {
        if (null == configAttributes || configAttributes.size() <= 0) {
            return;
        }
        HttpServletRequest request = ((FilterInvocation) o).getHttpRequest();
        String requestMethod = request.getMethod();
        String needResource;
        for (ConfigAttribute c : configAttributes) {
            //此处needResource为资源权限的code
            needResource = c.getAttribute();
            for (GrantedAuthority a : authentication.getAuthorities()) {
                //getAuthority()方法的返回值取决于CustomGrantedAuthority的toString
                if (needResource.trim().equals(a.getAuthority().split(";")[0])) {
                    //如果method方法符合 或者为all 则放行
                    if (a.getAuthority().split(";")[1].equals("ALL") ||
                            requestMethod.equalsIgnoreCase(a.getAuthority().split(";")[1])) {
                        return;
                    }
                }
            }
        }
        throw new AccessDeniedException("no permission");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
