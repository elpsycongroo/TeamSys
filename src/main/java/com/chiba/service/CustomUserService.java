package com.chiba.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chiba.bean.Constant;
import com.chiba.bean.ResponseBean;
import com.chiba.bean.SelectBean;
import com.chiba.dao.UserRepository;
import com.chiba.domain.Team;
import com.chiba.domain.User;
import com.chiba.utils.MD5Util;
import com.chiba.utils.SysUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/5              
 *  Description: 
 *****************************************/
@Service
@Slf4j
public class CustomUserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        log.info("LOGIN:=====" + user.getUsername() + "=====" + user.getPassword());
        return user;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveUser(User user) {
        userRepository.save(user);
    }

    public String getJsonResult(Page<User> userPage, int type) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", userPage.getTotalPages());
        jsonObject.put("page", userPage.getNumber());
        jsonObject.put("records", userPage.getTotalElements());
        JSONArray jsonArray = new JSONArray();
        if (type == Constant.JSON_USER_TYPE_SELECT_NAME) {
            for (User u : userPage.getContent()) {
                Map<String, Object> json = new HashMap<>();
                json.put("id", u.getId());
                json.put("text", u.getGameId());
                jsonArray.add(json);
            }
            jsonObject.put("rows", jsonArray);
        }
        return jsonObject.toJSONString();
    }

    public Page<User> getUserList(final SelectBean selectBean) {
        String sord = selectBean.getSord();
        String sidx = selectBean.getSidx();
        Integer page = selectBean.getPage();
        Integer rows = selectBean.getRows();
        final Map<String, Object> param = selectBean.getParam();

        Pageable pageable = PageRequest.of(page, rows, new Sort("asc".equals(sord) ? Sort.Direction.ASC : Sort.Direction.DESC, sidx));

        return userRepository.findAll((Specification<User>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (!SysUtils.isEmpty((String) param.get("username"))) {
                list.add(criteriaBuilder.like(root.get("gameId").as(String.class), "%" + param.get("username") + "%"));
            }
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);
    }

    public Team getTeamByUserName(String username) {
        User user = userRepository.findByUsername(username);
        return user.getTeam();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseBean changePwd(User user) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ResponseBean responseBean = new ResponseBean();
        if (!userDetails.getUsername().equals(user.getUsername())) {
            responseBean.setStatus(Constant.FAILED);
            responseBean.setMsg("只有用户自己才能修改自己的密码，你究竟是谁？");
        } else if (!userDetails.getPassword().equals(MD5Util.encode(user.getTrueName()))) {
            //用trueName存储原密码。。懒。。
            responseBean.setStatus(Constant.FAILED);
            responseBean.setMsg("记不住自己密码了？尝试使用邮箱找回吧！");
        } else {
            String newPwd = user.getPassword();
            user = userRepository.findByUsername(user.getUsername());
            user.setPassword(MD5Util.encode(newPwd));
        }
        return responseBean;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseBean editUserInfo(User user) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User originUser = userRepository.findByUsername(userDetails.getUsername());
        ResponseBean responseBean = new ResponseBean();
        if (!originUser.getUsername().equals(user.getUsername())) {
            responseBean.setStatus(Constant.FAILED);
            responseBean.setMsg("校验失败，请尝试注销后重新登录");
        } else {
            originUser.setTrueName(user.getTrueName());
            originUser.setClan(user.getClan());
            originUser.setEmail(user.getEmail());
            originUser.setGameId(user.getGameId());
            userRepository.save(originUser);
        }
        return responseBean;
    }
}
