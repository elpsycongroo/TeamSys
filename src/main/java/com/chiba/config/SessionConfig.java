package com.chiba.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/4              
 *  Description: 
 *****************************************/
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 10 * 60)
public class SessionConfig
{

}
