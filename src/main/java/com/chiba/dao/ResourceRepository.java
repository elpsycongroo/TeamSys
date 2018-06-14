package com.chiba.dao;

import com.chiba.domain.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/11              
 *  Description: 
 *****************************************/
@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long>{

    @Query(value = "select res.id, res.code, res.name, res.url, res.custom_id_name, res.icon_class, res.parent_res_id, res.add_time, res.update_time, res.add_user_id, res.update_user_id, res.delete_status, res.api, res.method"
            + " from sys_resource res where res.id in (select role_res.resource_id from sys_role_resource role_res where role_res.role_id = (select userx.role_id from sys_user userx where userx.id = ?1))", nativeQuery = true)
    List<Resource> findByUserId(Long userId);

    List<Resource> findByParentResourceIsNullAndApi(boolean isApi);

}
