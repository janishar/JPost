package com.mindorks.jpost.core;

import com.mindorks.jpost.exceptions.NoSuchChannelException;
import com.mindorks.jpost.exceptions.NullObjectException;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by janisharali on 22/09/16.
 */
public interface Broadcast<C extends Channel<? extends PriorityBlockingQueue<Post>, ? extends ConcurrentHashMap<? extends Integer, ?>>> {

    int CHANNEL_INITIAL_CAPACITY = 5;

     C getChannel(Integer channelId)
            throws NoSuchChannelException;

    <T>boolean broadcast(Integer channelId, T msg)
            throws NoSuchChannelException, NullObjectException;

    <T> T addSubscriber(Integer channelId, T subscriber, Integer subscriberId)
            throws NoSuchChannelException, NullObjectException;

    List<Objects> getAllSubscribers(Integer channelId)
            throws NoSuchChannelException;
}
