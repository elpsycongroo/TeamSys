package com.chiba.controller;

import com.chiba.bean.ValidatorBean;
import com.chiba.domain.User;
import com.chiba.service.CustomUserService;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.util.Map;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/6              
 *  Description: 
 *****************************************/
@Controller
@RequestMapping("/")
@Slf4j
public class CommonPageController {

    @Autowired
    private DefaultKaptcha defaultKaptcha;
    @Autowired
    private CustomUserService userService;

    @GetMapping("/register")
    public String regPage() {
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashBoardPage() {
        return "dashboard";
    }

    @GetMapping("/kaptcha-image")
    public String refreshKaptcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ServletOutputStream out = null;
        try {
            Producer captchaProducer = defaultKaptcha.getConfig().getProducerImpl();

            HttpSession session = request.getSession();
            String code = (String) session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("image/jpeg");
            String capText = captchaProducer.createText();
            session.setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);
            BufferedImage bi = captchaProducer.createImage(capText);
            out = response.getOutputStream();
            ImageIO.write(bi, "jpg", out);

            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            log.error("kaptcha-image======" + e.getMessage());
        } finally {
            if (null != out) {
                out.close();
            }
        }
        return null;
    }

    @GetMapping("/kaptcha-image/verify")
    @ResponseBody
    public ValidatorBean verifyKaptcha(String verifyCode, HttpServletRequest request) {
        String expect = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        if ((expect != null && !expect.equalsIgnoreCase(verifyCode))) {
            return new ValidatorBean(false);
        }
        return new ValidatorBean(true);
    }
}
