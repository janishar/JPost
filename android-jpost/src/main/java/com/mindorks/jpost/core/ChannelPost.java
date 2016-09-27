/*
 * Copyright (C) 2016 Janishar Ali Anwar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.mindorks.jpost.core;

import com.mindorks.jpost.exceptions.NullObjectException;

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
