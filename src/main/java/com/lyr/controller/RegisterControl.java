package com.lyr.controller;

import com.lyr.aspect.PrincipalAspect;
import com.lyr.constant.CodeType;
import com.lyr.model.User;
import com.lyr.redis.StringRedisServiceImpl;
import com.lyr.service.UserService;
import com.lyr.utils.DataMap;
import com.lyr.utils.JsonResult;
import com.lyr.utils.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: zhangocean
 * @Date: 2018/6/4 11:48
 * Describe:
 */
@RestController
@Slf4j
public class RegisterControl {

    @Autowired
    UserService userService;

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String register(User user,
                            HttpServletRequest request){
        try {

            String pattern = "[1][358]\\d{9}";

            //判断手机号是否正确
            if(!user.getPhone().matches(pattern)){
                return JsonResult.fail(CodeType.PHONE_ERROR).toJSON();
            }
            //判断用户名是否存在
            if(userService.usernameIsExist(user.getUsername()) || user.getUsername().equals(PrincipalAspect.ANONYMOUS_USER)){
                return JsonResult.fail(CodeType.USERNAME_EXIST).toJSON();
            }
            //注册时对密码进行MD5加密
            MD5Util md5Util = new MD5Util();
            user.setPassword(md5Util.encode(user.getPassword()));

            //注册结果
            DataMap data = userService.insert(user);
            return JsonResult.build(data).toJSON();
        } catch (Exception e){
            log.error("User [{}] register exception", user, e);
        }
        return JsonResult.fail(CodeType.SERVER_EXCEPTION).toJSON();
    }

}
