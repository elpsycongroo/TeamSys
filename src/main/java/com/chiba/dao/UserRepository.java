package com.chiba.dao;

import com.chiba.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
}
