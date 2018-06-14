package com.chiba.service;

import com.chiba.dao.ResourceRepository;
import com.chiba.domain.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<Resource> getResourceByUserId(Long userId) {
        return resourceRepository.findByUserId(userId);
    }

    public List<Resource> getResourceListForLeftSlider() {
        return resourceRepository.findByParentResourceIsNullAndApi(false);
    }

}
