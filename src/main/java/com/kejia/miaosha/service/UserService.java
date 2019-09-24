package com.kejia.miaosha.service;

import com.kejia.miaosha.dao.UserDao;
import com.kejia.miaosha.domin.User;
import com.kejia.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

/**
 * @AUTHOR :yuankejia
 * @DESCRIPTION:
 * @DATE:CRETED: IN 17:57 2019/9/22
 * @MODIFY:
 */
@Service
public class UserService {

    @Autowired
    UserDao userDao;
    public User getByid(int id){
        return userDao.getByid(id);
    }
    @Transactional
    public boolean tx() {
        User u1 = new User();
        u1.setId(2);
        u1.setName("aaa");
        userDao.insert(u1);
        User u2 = new User();
        u2.setId(1);
        u2.setName("bbb");
        userDao.insert(u2);
        return true;
    }

    public void login(HttpServletResponse response, LoginVo loginVo) {
    }
}
