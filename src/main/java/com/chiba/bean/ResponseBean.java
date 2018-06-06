package com.chiba.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/5              
 *  Description: 
 *****************************************/
@Data
public class ResponseBean {
    private String status;
    private String msg;
    @JsonIgnore
    private Object obj;

    public ResponseBean() {
        this.status = Constant.OK;
        this.msg = Constant.SUCCESS;
        this.obj = null;
    }

    public ResponseBean(String status, String msg) {
        this.status = status;
        this.msg = msg;
        this.obj = null;
    }

    public ResponseBean(String status, String msg, Object obj) {
        this.status = status;
        this.msg = msg;
        this.obj = obj;
    }
}
