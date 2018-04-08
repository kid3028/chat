package com.chat.chatServer;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserInfoManager {
    private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    private static ConcurrentMap<Channel, UserInfo> userInfos = new ConcurrentHashMap<Channel, UserInfo>();


    public static void removeChannel(Channel channel) {
    }

    public static void addChannel(Channel channel, String userId) {
        String remoteAddr = channel.localAddress().toString();
        UserInfo userInfo = new UserInfo(userId, remoteAddr, channel);
        userInfos.put(channel, userInfo);

    }

    public static void broadCastMsg(String friend, String message, String send) {

        try {
            if (message != null) {
                lock.readLock().lock();
                Set<Channel> channels = userInfos.keySet();
                for (Channel channel : channels) {
                    UserInfo userInfo = userInfos.get(channel);
                    if (!userInfo.getUserId().equals(friend)) {
                        continue;
                    }
                    String responseMsg = send + "," + message;
                    channel.writeAndFlush(new TextWebSocketFrame(responseMsg));
                }
            }
        } catch (Exception e) {
            System.out.println("exception:======>" + e.getMessage());
        } finally {
            lock.readLock().unlock();
        }
    }

    public static UserInfo getUserInfo(Channel channel) {
        return userInfos.get(channel);
    }
} 