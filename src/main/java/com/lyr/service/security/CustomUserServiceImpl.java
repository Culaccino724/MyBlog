package com.lyr.service.security;

import com.lyr.mapper.UserMapper;
import com.lyr.model.Role;
import com.lyr.model.User;
import com.lyr.service.UserService;
import com.lyr.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhangocean
 * @Date: 2018/6/5 19:11
 * Describe: 用户登录处理
 */
@Service
public class CustomUserServiceImpl implements UserDetailsService{

    @Autowired
    UserService userService;
    @Autowired
    UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {

        User user = userMapper.getUsernameAndRolesByPhone(phone);

        if(user == null){
            throw  new UsernameNotFoundException("用户不存在");
        }

        TimeUtil timeUtil = new TimeUtil();
        String recentlyLanded = timeUtil.getFormatDateForSix();
        userService.updateRecentlyLanded(user.getUsername(), recentlyLanded);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for(Role role : user.getRoles()){
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}
