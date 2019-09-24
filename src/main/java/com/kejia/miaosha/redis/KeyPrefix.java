package com.kejia.miaosha.redis;

/**
 * @AUTHOR :yuankejia
 * @DESCRIPTION:
 * @DATE:CRETED: IN 21:56 2019/9/23
 * @MODIFY:
 */
public interface KeyPrefix {
    public int expireSeconds();

    public String getPrefix();
}
