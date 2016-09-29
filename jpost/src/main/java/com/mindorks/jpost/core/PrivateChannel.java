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

import com.mindorks.jpost.exceptions.InvalidSubscriberException;
import com.mindorks.jpost.exceptions.NullObjectException;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by janisharali on 22/09/16.
 */
public class PrivateChannel<
        Q extends PriorityBlockingQueue<WeakReference<ChannelPost>>,
        M extends ConcurrentHashMap<Integer,WeakReference<Object>>>
        extends PublicChannel<Q,M>{

    private WeakReference<Object> channelOwnerRef;

    public PrivateChannel(Integer channelId, ChannelState state, ChannelType type, Q postQueue, M subscriberMap, WeakReference<Object> channelOwnerRef) {
        super(channelId, state, type, postQueue, subscriberMap);
        this.channelOwnerRef = channelOwnerRef;
    }

    protected WeakReference<Object> getChannelOwnerRef() {
        return channelOwnerRef;
    }

    protected synchronized void removeSubscriber(Integer subscriberId) throws NullObjectException,InvalidSubscriberException {
        if(subscriberId == null){
            throw new NullObjectException("subscriberId is null");
        }
        if(getSubscriberMap().containsKey(subscriberId)){
            getSubscriberMap().remove(subscriberId);
        }else{
            throw new InvalidSubscriberException("Subscriber with subscriberId " + subscriberId + " do not exists");
        }
    }
}
