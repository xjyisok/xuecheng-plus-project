package com.xuecheng.base.exception;

public class XueChengError extends RuntimeException {
    private String message;
    public XueChengError() {
        super();
    }
    public XueChengError(String message) {
        super(message);
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public static void cast(String message){
        throw new XueChengError(message);
    }
}
