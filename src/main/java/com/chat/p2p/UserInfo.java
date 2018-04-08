package com.chat.p2p;

import io.netty.channel.Channel;

public class UserInfo {
    private String userId;

    private String address;

    private Channel channel;

    public UserInfo() {}

    public UserInfo(String userId, String address, Channel channel) {
        this.userId = userId;
        this.address = address;
        this.channel = channel;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}