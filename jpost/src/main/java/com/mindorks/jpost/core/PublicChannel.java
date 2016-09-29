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

import com.mindorks.jpost.core.OnMessage;
import com.mindorks.jpost.core.*;
import com.mindorks.jpost.core.ChannelPost;
import com.mindorks.jpost.exceptions.IllegalChannelStateException;
import com.mindorks.jpost.exceptions.NullObjectException;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by janisharali on 22/09/16.
 */
public class PublicChannel<
        Q extends PriorityBlockingQueue<WeakReference<ChannelPost>>,
        M extends ConcurrentHashMap<Integer,WeakReference<Object>>>
        extends DefaultChannel<Q,M>
        implements CustomChannel<Q,M>{

    public PublicChannel(Integer channelId, ChannelState state, ChannelType type, Q postQueue, M subscriberMap) {
        super(channelId, state, type, postQueue, subscriberMap);
    }

    @Override
    public void terminateChannel() {
        super.setChannelState(ChannelState.TERMINATED);
        super.getSubscriberMap().clear();
        super.getPostQueue().clear();
    }

    @Override
    public void startChannel() {
        super.setChannelState(ChannelState.OPEN);
    }

    @Override
    public void stopChannel() {
        super.setChannelState(ChannelState.STOPPED);
    }

    @Override
    public <T> void broadcast(T msg, Integer... subscriberIds) throws NullObjectException, IllegalChannelStateException {
        if(super.getChannelState() != ChannelState.OPEN){
            throw new IllegalChannelStateException("Channel with id " + super.getChannelId() + " is closed");
        }
        if(msg == null){
            throw new NullObjectException("message is null");
        }
        ChannelPost post = new ChannelPost<>(msg, getChannelId(), Post.PRIORITY_MEDIUM);
        getPostQueue().put(new WeakReference<>(post));

        while (!getPostQueue().isEmpty()) {
            try {
                WeakReference<ChannelPost> msgRef = getPostQueue().take();
                if(msgRef != null) {
                    ChannelPost mspPost = msgRef.get();
                    if (mspPost != null && mspPost.getChannelId() != null) {
                        if (mspPost.getChannelId().equals(getChannelId())) {
                            for (Integer subscriberId : subscriberIds) {
                                if (getSubscriberMap().containsKey(subscriberId)) {
                                    WeakReference<Object> subscriberRef = getSubscriberMap().get(subscriberId);
                                    if(subscriberRef != null) {
                                        Object subscriberObj = subscriberRef.get();
                                        if (subscriberObj != null) {
                                            for (final Method method : subscriberObj.getClass().getDeclaredMethods()) {
                                                Annotation annotation = method.getAnnotation(OnMessage.class);
                                                if (annotation != null) {
                                                    deliverMessage(subscriberObj, (OnMessage) annotation, method, mspPost);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            getPostQueue().put(msgRef);
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
