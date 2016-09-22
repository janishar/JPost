package com.mindorks.jpost.core;

import com.mindorks.jpost.exceptions.AlreadyExistsException;
import com.mindorks.jpost.exceptions.IllegalStateException;
import com.mindorks.jpost.exceptions.NullObjectException;

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

    @Override
    public abstract <T> void broadcast(T msg) throws NullObjectException, IllegalStateException;

    @Override
    public abstract <T> T addSubscriber(T subscriber, Integer subscriberId)
            throws NullObjectException, AlreadyExistsException, IllegalStateException;

}
