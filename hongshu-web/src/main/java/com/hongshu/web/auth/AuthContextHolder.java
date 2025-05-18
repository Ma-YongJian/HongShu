package com.hongshu.web.auth;

/**
 * @Author hongshu
 */
public class AuthContextHolder {

    private AuthContextHolder() {

    }

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
