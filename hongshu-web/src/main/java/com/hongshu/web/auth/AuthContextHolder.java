package com.hongshu.web.auth;


/**
 * @author: hongshu
 */
public class AuthContextHolder {

    private AuthContextHolder() {

    }

    //用户id
    private static final ThreadLocal<String> userId = new ThreadLocal<>();

    public static void setUserId(String _userId) {
        userId.set(_userId);
    }

    public static String getUserId() {
        return userId.get();
    }

    public static void removeUserId() {
        userId.remove();
    }
}
