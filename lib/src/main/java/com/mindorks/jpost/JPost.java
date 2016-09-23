package com.mindorks.jpost;

import com.mindorks.jpost.core.*;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by janisharali on 23/09/16.
 */
public class JPost {

    protected static ConcurrentHashMap<Integer, WeakReference<Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
            ConcurrentHashMap<Integer,WeakReference<Object>>>>> channelMap;

    private static BroadcastCenter broadcastCenter;

    static {
        channelMap = new ConcurrentHashMap<>(Broadcast.CHANNEL_INITIAL_CAPACITY);
        channelMap.put(Channel.DEFAULT_CHANNEL_ID,
                new WeakReference<Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
                        ConcurrentHashMap<Integer,WeakReference<Object>>>>(
                                new DefaultChannel(Channel.DEFAULT_CHANNEL_ID, ChannelType.DEFAULT, ChannelState.OPEN)));
        broadcastCenter = new BroadcastCenter();
    }

    protected static ExecutorService executorService = Executors.newCachedThreadPool();

    public static BroadcastCenter getBroadcastCenter(){
        return broadcastCenter;
    }

    public static void shutdown(){
        executorService.shutdown();
    }
}
