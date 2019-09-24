package com.kejia.miaosha.dao;

import com.kejia.miaosha.domin.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @AUTHOR :yuankejia
 * @DESCRIPTION:
 * @DATE:CRETED: IN 17:55 2019/9/22
 * @MODIFY:
 */
@Mapper
public interface UserDao {
    @Select("select * from user where id =#{id}")
    public User getByid(@Param("id") int id);
    @Insert("insert into user(id,name) values(#{id},#{name})")
    public int insert(User user);
}
