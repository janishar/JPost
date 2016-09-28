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

import com.mindorks.jpost.exceptions.AlreadyExistsException;
import com.mindorks.jpost.exceptions.IllegalChannelStateException;
import com.mindorks.jpost.exceptions.InvalidPropertyException;
import com.mindorks.jpost.exceptions.NullObjectException;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by janisharali on 22/09/16.
 */
public class DefaultChannel<
        Q extends PriorityBlockingQueue<WeakReference<ChannelPost>>,
        M extends ConcurrentHashMap<Integer,WeakReference<Object>>>
        extends AbstractChannel<Q,M> {

    public DefaultChannel(Integer channelId, ChannelState state, ChannelType type, Q postQueue, M subscriberMap) {
        super(channelId, state, type, postQueue, subscriberMap);

    }

    @Override
    public <T> void broadcast(T msg) throws NullObjectException, IllegalChannelStateException {
        if(super.getChannelState() != ChannelState.OPEN){
            throw new IllegalChannelStateException("Channel with id " + super.getChannelId() + " is closed");
        }
        if(msg == null){
            throw new NullObjectException("message is null");
        }
        ChannelPost<T, Object> post = new ChannelPost<>(msg, getChannelId(), Post.PRIORITY_MEDIUM);
        getPostQueue().put(new WeakReference<ChannelPost>(post));

        while (!getPostQueue().isEmpty()) {
            WeakReference<ChannelPost> msgRef = getPostQueue().poll();
            if (msgRef == null) {
                return;
            }
            ChannelPost mspPost = msgRef.get();
            if (mspPost != null && mspPost.getChannelId() != null) {
                if (mspPost.getChannelId().equals(getChannelId())) {
                    for (WeakReference<Object> subscriberRef : getSubscriberMap().values()) {
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
                } else {
                    getPostQueue().offer(msgRef);
                }
            }
        }
    }

    @Override
    public <T, P extends Post<?, ?>> boolean deliverMessage(T subscriber, OnMessage msgAnnotation, Method method, P post) {
        int channelId = msgAnnotation.channelId();
        boolean isCommonReceiver = msgAnnotation.isCommonReceiver();
        if (isCommonReceiver || getChannelId().equals(channelId)) {
            try {
                boolean methodFound = false;
                for (final Class paramClass : method.getParameterTypes()) {
                    if (paramClass.equals(post.getMessage().getClass())) {
                        methodFound = true;
                        break;
                    }
                }
                if (methodFound) {
                    method.setAccessible(true);
                    method.invoke(subscriber, post.getMessage());
                }
                return true;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public <T> T addSubscriber(T subscriber, Integer subscriberId )
            throws NullObjectException, AlreadyExistsException, IllegalChannelStateException {
        if(super.getChannelState() != ChannelState.OPEN){
            throw new IllegalChannelStateException("Channel with id " + super.getChannelId() + " is closed");
        }
        if(subscriber == null){
            throw new NullObjectException("subscriber is null");
        }
        if(subscriberId == null){
            throw new NullObjectException("subscriberId is null");
        }
        if(super.getSubscriberMap().containsKey(subscriberId)){
            throw new AlreadyExistsException("subscriber with subscriberId " + subscriberId + " already registered");
        }
        super.getSubscriberMap().put(subscriberId, new WeakReference<Object>(subscriber));
        return subscriber;
    }

    @Override
    public synchronized <T> void removeSubscriber(T subscriber) throws NullObjectException, InvalidPropertyException {
        if(subscriber == null){
            throw new NullObjectException("subscriber is null");
        }
        boolean isRemoved = false;
        Iterator<WeakReference<Object>> iterator = getSubscriberMap().values().iterator();
        while (iterator.hasNext()){
            WeakReference<Object> weakReference = iterator.next();
            Object subscriberObj = weakReference.get();
            if(subscriberObj != null && subscriberObj == subscriber){
                getSubscriberMap().values().remove(weakReference);
                isRemoved = true;
                break;
            }
        }
        if(!isRemoved){
            throw new InvalidPropertyException("Subscriber  do not exists");
        }
    }
}
