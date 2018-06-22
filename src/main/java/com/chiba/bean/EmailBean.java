package com.chiba.bean;

import lombok.Data;
import lombok.ToString;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/22              
 *  Description: 
 *****************************************/
@Data
@ToString
public class EmailBean {
    private String receiver;
    private String subject;
    private String text;
    private String content;
}
