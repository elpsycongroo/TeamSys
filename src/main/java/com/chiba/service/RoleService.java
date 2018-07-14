package com.chiba.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chiba.bean.Constant;
import com.chiba.bean.SelectBean;
import com.chiba.dao.RoleRepository;
import com.chiba.domain.Role;
import com.chiba.utils.SysUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/8              
 *  Description: 
 *****************************************/
@Service
@Slf4j
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role findRoleById(Long id) {
        return roleRepository.getOne(id);
    }

    public Role findByCode(String code) {
        return roleRepository.findByCode(code);
    }

    public void save(Role role) {
        roleRepository.save(role);
    }

    public String getJsonResult(Page<Role> page, int type) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", page.getTotalPages());
        jsonObject.put("page", page.getNumber() + 1);
        jsonObject.put("records", page.getTotalElements());
        JSONArray jsonArray = new JSONArray();
        if (type == Constant.JSON_ROLE_TYPE_SELECT_NAME) {
            for (Role r : page.getContent()) {
                JSONObject json = new JSONObject();
                json.put("id", r.getId());
                json.put("text", r.getName());
                jsonArray.add(json);
            }
        } else if (type == Constant.JSON_ROLE_TYPE_SELECT_LIST) {
            for (Role r : page.getContent()) {
                JSONObject json = new JSONObject();
                json.put("id", r.getId());
                json.put("name", r.getName());
                json.put("updateTime", r.getUpdateTime() != null ? SysUtils.dateFormat("yyyy-MM-dd HH:mm:ss", r.getUpdateTime()): "");
                json.put("updateUser", r.getUpdateUser() != null ? r.getUpdateUser().getUsername() : "");
                json.put("code", r.getCode());
                json.put("remark", r.getRemark());
                jsonArray.add(json);
            }
        }
        jsonObject.put("rows", jsonArray);
        return jsonObject.toJSONString();
    }

    public Page<Role> getRoleList(final SelectBean selectBean, int type) {
        String sord = selectBean.getSord();
        String sidx = selectBean.getSidx();
        Integer page = selectBean.getPage();
        Integer rows = selectBean.getRows();
        final Map<String, Object> param = selectBean.getParam();

        Pageable pageable = PageRequest.of(page, rows, new Sort("asc".equals(sord) ? Sort.Direction.ASC : Sort.Direction.DESC, sidx));
        return roleRepository.findAll((Specification<Role>) (root, criteriaQuery, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (type == Constant.JSON_ROLE_TYPE_SELECT_NAME) {
                if (!SysUtils.isEmpty((String) param.get("name"))) {
                    list.add(cb.like(root.get("name").as(String.class), "%" + param.get("name") + "%"));
                }
            } else if (type == Constant.JSON_ROLE_TYPE_SELECT_LIST) {

            }
            Predicate[] p = new Predicate[list.size()];
            return cb.and(list.toArray(p));
        }, pageable);
    }

}
