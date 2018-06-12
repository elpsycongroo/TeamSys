package com.chiba.dao;

import com.chiba.domain.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/11              
 *  Description: 
 *****************************************/
@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long>{
}
