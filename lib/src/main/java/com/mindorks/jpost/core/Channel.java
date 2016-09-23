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
public interface Channel<Q extends PriorityBlockingQueue<? extends WeakReference<? extends ChannelPost>>,
        M extends ConcurrentHashMap<? extends Integer,? extends WeakReference<?>>> {

    int MSG_QUEUE_INITIAL_CAPACITY = 10;
    int SUBSCRIBER_INITIAL_CAPACITY = 10;
    int DEFAULT_CHANNEL_ID = -99999999;

    /**
     *
     * @return
     */
    Integer getChannelId();

    /**
     *
     * @return
     */
    ChannelType getChannelType();

    /**
     *
     * @return
     */
    Q getPostQueue();

    /**
     *
     * @return
     */
    M getSubscriberMap();

    /**
     *
     * @return
     */
    ChannelState getChannelState();

    /**
     *
     * @param state
     */
    void setChannelState(ChannelState state);

    /**
     *
     * @param msg
     * @param <T>
     * @throws NullObjectException
     * @throws IllegalStateException
     */
    <T>void broadcast(T msg) throws NullObjectException, IllegalStateException;

    /**
     *
     * @param subscriber
     * @param subscriberId
     * @param <T>
     * @return
     * @throws NullObjectException
     * @throws AlreadyExistsException
     * @throws IllegalStateException
     */
    <T> T addSubscriber(T subscriber, Integer subscriberId) throws NullObjectException, AlreadyExistsException, IllegalStateException;


    <T> void removeSubscriber(T subscriber) throws NullObjectException;


    /**
     *
     * @return
     */
    Collection<? extends WeakReference<?>> getAllSubscribersReferenceList();
}
