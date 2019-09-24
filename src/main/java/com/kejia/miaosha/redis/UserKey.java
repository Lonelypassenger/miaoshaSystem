package com.kejia.miaosha.redis;

/**
 * @AUTHOR :yuankejia
 * @DESCRIPTION:
 * @DATE:CRETED: IN 22:03 2019/9/23
 * @MODIFY:
 */
public class UserKey extends BasePrefix {
    private UserKey( String prefix) {
        super( prefix);
    }
    public static UserKey getByid = new UserKey("id");
    public static UserKey getByName = new UserKey("name");
}
