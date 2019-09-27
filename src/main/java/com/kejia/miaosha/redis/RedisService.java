package com.kejia.miaosha.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @AUTHOR :yuankejia
 * @DESCRIPTION:
 * @DATE:CRETED: IN 20:23 2019/9/22
 * @MODIFY:
 */
@Service
public class RedisService {
    @Autowired
    JedisPool jp;

    /**
    *
    *METHOD_NAME:
    *PARAM:
    *RETURN:
    *DATE:16:04 2019/9/24
    *DESCRIPTION:获取单个对象
    */
    public <T> T get(KeyPrefix prefix,String key,Class<T> clazz){
        Jedis jedis = null;
        try{
            jedis = jp.getResource();
            String realKey = prefix.getPrefix()+key;
            String str = jedis.get(realKey);

            T t = stringToBean(str,clazz);
            return t;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
    *METHOD_NAME:exists
    *PARAM:KeyPrefix prefix,String key
    *RETURN:boolean
    *DATE:16:08 2019/9/24
    *DESCRIPTION:判断某个key是否存在：
    */
    public <T> boolean exists(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis = jp.getResource();
            String realKey = prefix.getPrefix()+key;
            return jedis.exists(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
    *METHOD_NAME:incre
    *PARAM:KeyPrefix prefix,String key
    *RETURN:Long
    *DATE:16:10 2019/9/24
    *DESCRIPTION:使key对应的值自增1，并返回
    */
    public <T> Long incre(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis = jp.getResource();
            String realKey = prefix.getPrefix()+key;
            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }
    /**
    *METHOD_NAME:decr
    *PARAM:KeyPrefix prefix,String key
    *RETURN:Long
    *DATE:16:11 2019/9/24
    *DESCRIPTION:使key对应的值自减1，并返回
    */
    public <T> Long decr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis = jp.getResource();
            String realKey = prefix.getPrefix()+key;
            return jedis.decr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }


    public <T> boolean set(KeyPrefix prefix,String key,T value){
        Jedis jedis = null;
        try{
            jedis = jp.getResource();
            String realKey = prefix.getPrefix()+key;
            String str = beanToString(value);
            if(str==null||str.length()<=0) return false;
            int seconds = prefix.expireSeconds();
            if(seconds==0)
            jedis.set(realKey,str);
            else{//先设置一个值在设置一个国企时间
                jedis.setex(realKey,seconds,str);
            }
            return true;
        }finally {
            returnToPool(jedis);
        }
    }






    private <T> String beanToString(T value) {
        if(value==null){
            return null;
        }
        Class<?> clazz =  value.getClass();
        if(clazz==int.class||clazz==Integer.class){
            return ""+value;
        }
        else if(clazz==String.class)
        {
            return (String) value;
        }else if(clazz == long.class||clazz == Long.class){
            return ""+value;
        }else
            return JSON.toJSONString(value);
    }


    private <T> T stringToBean(String str,Class<?> clazz) {
        if(str==null||str.length()<=0)return null;
        if(clazz==int.class||clazz==Integer.class){
            return (T)Integer.valueOf(str);
        }
        else if(clazz==String.class)
        {
            return (T)str;
        }else if(clazz == long.class||clazz == Long.class){
            return (T)Long.valueOf(str);
        }else
            return (T) JSON.toJavaObject(JSON.parseObject(str),clazz);
    }

    private void returnToPool(Jedis jedis) {
        if(jedis!=null){
            jedis.close();
        }
    }


    /**
     * 删除
     * */
    public boolean delete(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis =  jp.getResource();
            //生成真正的key
            String realKey  = prefix.getPrefix() + key;
            long ret =  jedis.del(key);
            return ret > 0;
        }finally {
            returnToPool(jedis);
        }
    }   public void delete(MiaoshaUserKey getById, String s) {
    }
}
