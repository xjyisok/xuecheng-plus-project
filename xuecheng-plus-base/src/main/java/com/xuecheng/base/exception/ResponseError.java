package com.xuecheng.base.exception;

import java.io.Serializable;

public class ResponseError implements Serializable {
    private static final long serialVersionUID = 1L;
    private String errMessage;
    public ResponseError(String errMessage){
        this.errMessage= errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
