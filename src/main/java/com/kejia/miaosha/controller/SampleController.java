package com.kejia.miaosha.controller;

import com.kejia.miaosha.domin.User;
import com.kejia.miaosha.redis.RedisService;
import com.kejia.miaosha.redis.UserKey;
import com.kejia.miaosha.result.Result;
import com.kejia.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @AUTHOR :yuankejia
 * @DESCRIPTION:
 * @DATE:CRETED: IN 17:19 2019/9/22
 * @MODIFY:
 */
@Controller
@RequestMapping("/demo")
public class SampleController {
    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;
    @RequestMapping("/thymeleaf")
    @ResponseBody
    public Result<User> dbGet(){
        User user = userService.getByid(1);
        return Result.success(user);
    }
    @RequestMapping("/tx")
    @ResponseBody
    public Result<Boolean> dbTx(){
        userService.tx();
        return Result.success(true);
    }

    @RequestMapping("/redis")
    @ResponseBody
    public Result<User> dbRedis(){
        User v1 = redisService.get(UserKey.getByid,""+1,User.class);
        return Result.success(v1);
    }
    @RequestMapping("/redis_set")
    @ResponseBody
    public Result<Boolean> redisSet(){
        User user = new User();
        user.setId(1);
        user.setName("1111");
        redisService.set(UserKey.getByid,""+1,user);
        //String str =redisService.get(UserKey.getByid,""+1,String.class);
        return Result.success(true);
    }
}
