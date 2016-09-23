package com.mindorks.jpost;

import com.mindorks.jpost.core.*;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by janisharali on 23/09/16.
 */
public class JPost {

    private static ReentrantLock JPostBootLock = new ReentrantLock();

    protected static ConcurrentHashMap<Integer, WeakReference<Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
            ConcurrentHashMap<Integer,WeakReference<Object>>>>> channelMap;

    private static BroadcastCenter broadcastCenter;
    private static DefaultChannel channel;
    private static int threadCount = Runtime.getRuntime().availableProcessors() + 1;

    static {
        channelMap = new ConcurrentHashMap<>(Broadcast.CHANNEL_INITIAL_CAPACITY);
        channel = new DefaultChannel(Channel.DEFAULT_CHANNEL_ID, ChannelType.DEFAULT, ChannelState.OPEN);
        channelMap.put(Channel.DEFAULT_CHANNEL_ID,
                new WeakReference<Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
                        ConcurrentHashMap<Integer,WeakReference<Object>>>>(channel));
        broadcastCenter = new BroadcastCenter();
    }

    protected static ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

    public static BroadcastCenter getBroadcastCenter(){
        return broadcastCenter;
    }

    public static void reboot(){
        JPostBootLock.lock();
        executorService = Executors.newFixedThreadPool(threadCount);
        JPostBootLock.unlock();
    }

    public static void shutdown(){
        JPostBootLock.lock();
        executorService.shutdown();
        JPostBootLock.unlock();
    }
}
