package com.chiba.bean;

import lombok.Data;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/7              
 *  Description: 
 *****************************************/
@Data
public class ValidatorBean {
    private boolean valid;

    public ValidatorBean(boolean valid) {
        this.valid = valid;
    }
}
