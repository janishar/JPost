package com.mindorks.jpost.exceptions;

import com.mindorks.jpost.core.Post;

import java.util.List;

/**
 * Created by janisharali on 23/09/16.
 */
public class ChannelPost<T, V> implements Post<T, V> {

    private T message;
    private V sender;
    private Integer channelId;
    private Integer priority;
    private Object[] receivers;
    private boolean isSerialised;
    private String className;

    public ChannelPost(T message, Integer channelId, Integer priority) {
        this.message = message;
        this.channelId = channelId;
        this.priority = priority;
    }

    public ChannelPost(T message, Integer channelId, Integer priority, boolean isSerialised, String className) {
        this.message = message;
        this.channelId = channelId;
        this.priority = priority;
        this.isSerialised = isSerialised;
        this.className = className;
    }

    @Override
    public void setMessage(T message){
        this.message = message;
    }

    @Override
    public T getMessage() {
        return message;
    }

    @Override
    public void setPriority(Integer priority){
        this.priority = priority;
    }

    @Override
    public Integer getPriority() {
        return priority;
    }

    @Override
    public V getSender() throws NullObjectException {
        return sender;
    }

    @Override
    public void setSender(V sender) {
        this.sender = sender;
    }

    @Override
    public void setReceivers(Object... receivers){
        this.receivers = receivers;
    }

    @Override
    public Object[] getReceiversList() {
        return receivers;
    }

    @Override
    public void setIsSerialised(boolean isSerialised) {
        this.isSerialised = isSerialised;
    }

    @Override
    public boolean isSerialised() {
        return isSerialised;
    }

    @Override
    public void setSerialisedClassName(String className){
        this.className = className;
    }

    @Override
    public String getSerialisedClassName() {
        return className;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId){
        this.channelId = channelId;
    }
}
