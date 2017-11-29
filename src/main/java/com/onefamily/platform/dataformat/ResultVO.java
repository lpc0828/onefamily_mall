package com.onefamily.platform.dataformat;

import java.io.Serializable;

public class ResultVO<T> implements Serializable {

    private boolean retBool;
    private String message;
    private T t;

    public ResultVO() {
    }

    public ResultVO(boolean retBool, String message) {
        this.retBool = retBool;
        this.message = message;
    }

    public ResultVO(boolean retBool, String message, T t) {
        this.retBool = retBool;
        this.message = message;
        this.t = t;
    }

    public ResultVO<T> format(boolean retBool, String message) {
        this.retBool = retBool;
        this.message = message;
        return this;
    }

    public static <T> ResultVO<T> newResult() {
        return new ResultVO<T>();
    }

    public ResultVO<T> format(boolean retBool, String message, T t) {
        this.retBool = retBool;
        this.message = message;
        this.t = t;
        return this;
    }

    public boolean isRetBool() {
        return retBool;
    }

    public String getMessage() {
        return message;
    }

    public T getT() {
        return t;
    }

    @Override
    public String toString() {
        return "ResultVO{" +
                "retBool=" + retBool +
                ", message='" + message + '\'' +
                ", t=" + t +
                '}';
    }
}
