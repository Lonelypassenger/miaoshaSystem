package com.kejia.miaosha.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @AUTHOR :yuankejia
 * @DESCRIPTION:
 * @DATE:CRETED: IN 16:25 2019/9/24
 * @MODIFY: 两次md5
 */
public class MD5Util {
    public static String md5(String str){
        return DigestUtils.md5Hex(str);
    }

    private static final String salt = "1a2b3c4d";

    public static String inputPassFromPass(String inputPass){
       String str = ""+ salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }


    public static String formPassToDBPass(String formPass,String salt){

        String str = ""+ salt.charAt(0)+salt.charAt(2)+formPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDBpass(String input,String saltDB){
        String formpass = inputPassFromPass(input);
        String dbPass = formPassToDBPass(formpass,saltDB);
        return dbPass;
    }

    public static void main(String[] args) {
        System.out.println(inputPassToDBpass("123456","1a2b3c4d"));
    }
}
