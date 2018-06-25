package com.chiba.service;

import com.chiba.bean.Constant;
import com.chiba.domain.Resource;
import com.chiba.domain.User;
import com.google.code.kaptcha.Constants;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/12              
 *  Description: 
 *****************************************/
public class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private String servletPath;

    private CustomUserService userService;

    private ResourceService resourceService;

    public LoginAuthenticationFilter(String servletPath, String failureUrl, CustomUserService customUserService, ResourceService resourceService) {
        super(servletPath);
        this.servletPath = servletPath;
        this.userService = customUserService;
        this.resourceService = resourceService;
        setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler(failureUrl));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (Constant.METHOD_POST.equalsIgnoreCase(req.getMethod()) && servletPath.equals(req.getServletPath())) {
            String expect = (String) req.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
            if (expect != null && !expect.equalsIgnoreCase(req.getParameter(Constant.PARAM_VERIFY_CODE))) {
                req.getSession().setAttribute("login_error_message", "输入的验证码不正确");
                unsuccessfulAuthentication(req, res, new InsufficientAuthenticationException("输入的验证码不正确"));
                return;
            }
            String username = req.getParameter("username");
            User user = userService.findUserByUsername(username);
            if (user.isDeleteStatus()) {
                req.getSession().setAttribute("login_error_message", "用户已停用，请联系管理员");
                unsuccessfulAuthentication(req, res, new InsufficientAuthenticationException("用户已停用"));
                return;
            }
            req.getSession().removeAttribute("login_error_message");
            req.getSession().setAttribute("currentUser", user);
            List<Resource> resourceList = resourceService.getResourceByUserId(user.getId());
            List<Resource> resourceListForLeftSlider = resourceService.getResourceListForLeftSlider();
            req.getSession().setAttribute("resourceListForLeftSlider", resourceListForLeftSlider);
            req.getSession().setAttribute("resourceList", resourceList);
        }
        chain.doFilter(request, response);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        return null;
    }
}
