package com.kejia.miaosha.controller;

import com.kejia.miaosha.domin.User;
import com.kejia.miaosha.redis.RedisService;
import com.kejia.miaosha.redis.UserKey;
import com.kejia.miaosha.result.CodeMsg;
import com.kejia.miaosha.result.Result;
import com.kejia.miaosha.service.MiaoshaUserService;
import com.kejia.miaosha.service.UserService;
import com.kejia.miaosha.utils.ValidatorUtil;
import com.kejia.miaosha.vo.LoginVo;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


/**
 * @AUTHOR :yuankejia
 * @DESCRIPTION:
 * @DATE:CRETED: IN 17:19 2019/9/22
 * @MODIFY:
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    private static Logger log = (Logger) LoggerFactory.getLogger(LoginController.class);
    @Autowired
    MiaoshaUserService userService;


    @Autowired
    RedisService redisService;


    @RequestMapping("/to_login")
    //@ResponseBody
    public String toLogin(){
        return "login";
    }
    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
        log.info(loginVo.toString());
        //登录
        userService.login(response, loginVo);
        return Result.success(true);
    }
}
