package com.muvlin.ucbradio.client.pojo;

public class InfoResponse {
    public String consultoradas;
    public String email;
    public String muvlin;
    public String ucb;

    public InfoResponse() {
    }

    public InfoResponse(String consultoradas, String email, String muvlin, String ucb) {
        this.consultoradas = consultoradas;
        this.email = email;
        this.muvlin = muvlin;
        this.ucb = ucb;
    }
}
