package com.chiba.dao;

import com.chiba.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/5              
 *  Description: 
 *****************************************/
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findByUsername(String userName);

    User findByEmail(String email);

    User findByUsernameAndEmailValidationKey(String username, String validationKey);

    User findByUsernameAndForgetKey(String username, String forgetKey);

    @Query(value = "select distinct u.clan from User u where u.clan is not null and u.clan like :clanName")
    Page<String> findUserClanList(@Param("clanName") String clanName, Pageable pageable);
}
