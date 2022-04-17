package com.lyr.controller;

import com.lyr.constant.CodeType;
import com.lyr.model.User;
import com.lyr.redis.StringRedisServiceImpl;
import com.lyr.service.UserService;
import com.lyr.utils.JsonResult;
import com.lyr.utils.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: zhangocean
 * @Date: 2018/6/8 9:24
 * Describe: 登录控制
 */
@RestController
@Slf4j
public class LoginControl {

    @Autowired
    UserService userService;

    @PostMapping(value = "/changePassword", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String changePassword(@RequestParam("phone") String phone,
                                 @RequestParam("newPassword") String newPassword){

        try {

            User user = userService.findUserByPhone(phone);
            if(user == null){
                return JsonResult.fail(CodeType.USERNAME_NOT_EXIST).toJSON();
            }
            MD5Util md5Util = new MD5Util();
            String mD5Password = md5Util.encode(newPassword);
            userService.updatePasswordByPhone(phone, mD5Password);

            return JsonResult.success().toJSON();
        } catch (Exception e){
            log.error("[{}] change password [{}] exception", phone, newPassword, e);
        }
        return JsonResult.fail(CodeType.SERVER_EXCEPTION).toJSON();
    }

}
