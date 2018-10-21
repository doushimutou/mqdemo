package com.example.springbootcomsumer.service;/**
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: ${todo}
 * @author ayt
 * @date 2018/10/1422:24
 */

import com.alibaba.dubbo.config.annotation.Reference;
import com.example.springbootdubbo.domain.User;
import com.example.springbootdubbo.service.UserService;
import org.springframework.stereotype.Component;

/**
 * @author ayt  on 
 */
@Component
public class UserConsumerService {
    @Reference
    UserService userService;

    public User saveUser(){
        User user = new User();
        user.setUserName("都是木头");
        User  user1 = userService.saveUser(user);
        return  user1;
    }
}
