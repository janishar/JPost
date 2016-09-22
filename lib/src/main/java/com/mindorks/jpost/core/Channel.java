package com.mindorks.jpost.core;

import com.mindorks.jpost.exceptions.*;
import com.mindorks.jpost.exceptions.IllegalStateException;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by janisharali on 22/09/16.
 */
public interface Channel<Q extends PriorityBlockingQueue<WeakReference<ChannelPost>>,
        M extends ConcurrentHashMap<? extends Integer,? extends WeakReference<?>>> {

    int MSG_QUEUE_INITIAL_CAPACITY = 10;
    int SUBSCRIBER_INITIAL_CAPACITY = 10;
    int DEFAULT_CHANNEL_ID = -99999999;

    Integer getChannelId();
    ChannelType getChannelType();
    Q getPostQueue();
    M getSubscriberMap();
    ChannelState getChannelState();
    void setChannelState(ChannelState state);
    <T>void broadcast(T msg) throws NullObjectException, IllegalStateException;
    <T> T addSubscriber(T subscriber, Integer subscriberId) throws NullObjectException, AlreadyExistsException, IllegalStateException;
    Collection<? extends WeakReference<?>> getAllSubscribersReferenceList();
}
