package com.mindorks.jpost.core;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by janisharali on 22/09/16.
 */
public interface CustomChannel<Q extends PriorityBlockingQueue<? extends WeakReference<? extends ChannelPost>>,
        M extends ConcurrentHashMap<? extends Integer,? extends WeakReference<?>>>
        extends Channel<Q, M>{

    /**
     *
     */
    void terminateChannel();

    /**
     *
     */
    void startChannel();

    /**
     *
     */
    void stopChannel();
}
