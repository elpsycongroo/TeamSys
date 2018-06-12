package com.chiba.service;

import com.chiba.dao.RoleRepository;
import com.chiba.domain.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
