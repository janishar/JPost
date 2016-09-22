package com.mindorks.jpost.core;

import com.mindorks.jpost.exceptions.NoSuchChannelException;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by janisharali on 22/09/16.
 */
public interface CustomChannel<Q extends PriorityBlockingQueue<WeakReference<Post>>, M extends ConcurrentHashMap<? extends Integer,? extends WeakReference<?>>>
        extends Channel<Q, M>{
    boolean removeChannel(Integer channelId) throws NoSuchChannelException;
    boolean startChannel(Integer channelId) throws NoSuchChannelException;
    boolean stopChannel(Integer channelId) throws NoSuchChannelException;
}
