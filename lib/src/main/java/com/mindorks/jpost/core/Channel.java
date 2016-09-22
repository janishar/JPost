package com.mindorks.jpost.core;

import com.mindorks.jpost.exceptions.AlreadyExistsException;
import com.mindorks.jpost.exceptions.NullObjectException;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by janisharali on 22/09/16.
 */
public interface Channel<Q extends PriorityBlockingQueue<WeakReference<Post>>, M extends ConcurrentHashMap<? extends Integer,? extends WeakReference<?>>> {

    int CHANNEL_TYPE_DEFAULT = 0;
    int CHANNEL_TYPE_PRIVATE = 1;
    int CHANNEL_TYPE_PUBLIC = 2;
    int MSG_QUEUE_INITIAL_CAPACITY = 10;

    Integer getChannelId();
    Integer getChannelType();
    Q getPostQueue();
    M getSubscriberMap();
    <T>boolean broadcast(T msg) throws NullObjectException;
    <T> T addSubscriber(T subscriber, Integer subscriberId) throws NullObjectException, AlreadyExistsException;
    Collection<Object> getAllSubscribers();
}
