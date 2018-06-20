package com.chiba.bean;

import com.chiba.utils.SysUtils;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/15              
 *  Description: 
 *****************************************/
@Data
public class SelectBean {

    private String sidx;

    private String sord;

    private Integer page;

    private Integer rows;

    private Map<String, Object> param;

    public SelectBean(String sidx, String sord, Integer page, Integer rows) {
        this.param = new HashMap<>();
        this.sidx = SysUtils.isEmpty(sidx) ? Constant.SIDX_UPDATE_TIME : sidx;
        this.sord = SysUtils.isEmpty(sord) ? Constant.SORT_DESC : sord;
        this.page = null == page ? 0 : page - 1;
        this.rows = null == rows ? 0 : rows;
    }


}
