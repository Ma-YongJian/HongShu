package com.hongshu.common.enums;

/**
 * 七牛云存储空间枚举类
 *
 * @Author hongshu
 */
public enum EQiNiuArea {

    /**
     * 华东
     */
    z0("z0", "华东"),

    /**
     * 华北
     */
    z1("z1", "华北"),

    /**
     * 华南
     */
    z2("z2", "华南"),

    /**
     * 北美
     */
    na0("na0", "北美"),

    /**
     * 东南亚
     */
    as0("as0", "东南亚");


    private final String code;
    private final String name;

    EQiNiuArea(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
