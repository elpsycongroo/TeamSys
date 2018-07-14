package com.chiba.dao;

import com.chiba.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/15              
 *  Description: 
 *****************************************/
@Repository
public interface TeamRepository extends JpaRepository<Team, Long>, JpaSpecificationExecutor<Team> {
    Team findByCode(String code);

    List<Team> findByDeleteStatus(boolean deleteStatus);
}
