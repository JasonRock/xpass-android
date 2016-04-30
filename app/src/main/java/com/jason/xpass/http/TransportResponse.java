package com.jason.xpass.http;

/**
 * Description:
 * <p/>
 * Created by js.lee on 4/30/16.
 */
public class TransportResponse {

    private Status status;

    private String info;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

}
