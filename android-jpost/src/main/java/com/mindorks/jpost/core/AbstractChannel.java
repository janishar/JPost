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

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by janisharali on 22/09/16.
 */
public abstract class AbstractChannel<Q extends PriorityBlockingQueue<? extends WeakReference<? extends ChannelPost>>,
        M extends ConcurrentHashMap<? extends Integer,? extends WeakReference<?>>> implements Channel<Q, M>{

    private Integer channelId;
    private ChannelType channelType;
    private ChannelState channelState;
    private Q postQueue;
    private M subscriberMap;

    public AbstractChannel(Integer channelId, ChannelState state, ChannelType type, Q postQueue, M subscriberMap) {
        this.channelId = channelId;
        this.channelState = state;
        this.channelType = type;
        this.postQueue = postQueue;
        this.subscriberMap = subscriberMap;
    }

    @Override
    public Integer getChannelId() {
        return channelId;
    }

    @Override
    public ChannelType getChannelType() {
        return channelType;
    }

    @Override
    public ChannelState getChannelState() {
        return channelState;
    }

    @Override
    public void setChannelState(ChannelState state) {
        channelState = state;
    }

    @Override
    public Q getPostQueue() {
        return postQueue;
    }

    @Override
    public M getSubscriberMap() {
        return subscriberMap;
    }

    @Override
    public Collection<? extends WeakReference<?>> getAllSubscribersReferenceList() {
        return subscriberMap.values();
    }
}
