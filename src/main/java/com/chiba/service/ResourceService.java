package com.chiba.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chiba.dao.ResourceRepository;
import com.chiba.dao.RoleRepository;
import com.chiba.domain.Resource;
import com.chiba.domain.Role;
import com.chiba.domain.User;
import com.chiba.utils.SysUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/12              
 *  Description: 
 *****************************************/
@Service
public class ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private RoleRepository roleRepository;

    public List<Resource> getResourceByUserId(Long userId) {
        return resourceRepository.findByUserId(userId);
    }

    public List<Resource> getResourceListForLeftSlider() {
        return resourceRepository.findByParentResourceIsNullAndApi(false);
    }

    public String getResourceTreeByRoleId(Long roleId) {
        if (null == roleId) {
            List<Resource> resources = resourceRepository.findAll();
            return getTreeJsonResult(resources);
        } else {
            Role role = roleRepository.getOne(roleId);
            List<Resource> resources = role.getResourceList();
            return getTreeJsonResult(resources);
        }
    }

    private String getTreeJsonResult(List<Resource> resources) {
        JSONArray jsonArray = new JSONArray();
        for (Resource resource : resources) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", resource.getId());
            jsonObject.put("pId", resource.getParentResource() == null ? "" : resource.getParentResource().getId());
            jsonObject.put("name", resource.getName());
            jsonObject.put("code", resource.getCode());
            jsonArray.add(jsonObject);
        }
        return jsonArray.toString();
    }

    public void processResourceList(Role role, String map_content) {
        Resource resource = null;
        if (SysUtils.isEmpty(map_content)) {
            role.setResourceList(null);
        } else {
            String[] resIds = map_content.split(",");
            List<Resource> resources = new ArrayList<Resource>();
            for (String resId : resIds) {
                resource = resourceRepository.getOne(Long.parseLong(resId));
                resources.add(resource);
            }
            role.setResourceList(resources);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveRole(Role role, User currentUser) {
        Date now = new Date();
        Role r;
        if (role.getId() != null) {
            r = roleRepository.getOne(role.getId());
        } else {
            r = new Role();
            r.setAddTime(now);
            r.setAddUser(currentUser);
        }
        r.setResourceList(role.getResourceList());
        r.setName(role.getName());
        r.setRemark(role.getRemark());
        r.setCode(role.getCode());
        r.setUpdateTime(now);
        r.setUpdateUser(currentUser);
        roleRepository.save(r);
    }

}
