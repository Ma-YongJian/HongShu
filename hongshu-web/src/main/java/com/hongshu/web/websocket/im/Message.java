package com.hongshu.web.websocket.im;

import lombok.Data;

import java.io.Serializable;

@Data
public class Message implements Serializable {

    private static final long serialVersionUID = 2405172041950251807L;

    private String sendUid;

    private String acceptUid;

    private Object content;

    private Integer msgType;

    private Integer chatType;
}
