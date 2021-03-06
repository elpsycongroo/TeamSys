package com.chiba.config;

import com.chiba.bean.EmailBean;
import com.sun.mail.util.MailSSLSocketFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/22              
 *  Description: 
 *****************************************/
@Configuration
public class MailConfig {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.username}")
    private String account;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean auth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private boolean startTlsEnable;

    @Value("${spring.mail.properties.mail.smtp.starttls.required}")
    private boolean startTlsRequired;

    @Value("${spring.mail.properties.mail.smtp.port}")
    private int port;

    @Bean(name = "JavaMailSenderImpl")
    public JavaMailSenderImpl getMailSender() throws Exception{
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(host);
        javaMailSender.setUsername(account);
        javaMailSender.setPassword(password);
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", auth);
        properties.put("mail.smtp.port", port);
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.socketFactory", sf);
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.port", port);
        javaMailSender.setJavaMailProperties(properties);
        return javaMailSender;
    }

    public void sendHtmlMail(EmailBean emailBean) throws Exception {
        JavaMailSender mailSender = getMailSender();
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(new InternetAddress(account,"组队系统","UTF-8"));
        helper.setTo(emailBean.getReceiver().split(";"));
        helper.setSubject(emailBean.getSubject());
        message.setContent(emailBean.getContent(), "text/html;charset=gb2312");
        mailSender.send(message);
    }

}
