package com.kejia.miaosha.utils;

import java.util.UUID;

/**
 * @AUTHOR :yuankejia
 * @DESCRIPTION:
 * @DATE:CRETED: IN 18:51 2019/9/24
 * @MODIFY:
 */
public class UUIDUtil {
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
