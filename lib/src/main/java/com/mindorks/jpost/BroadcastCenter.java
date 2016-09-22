package com.mindorks.jpost;

import com.mindorks.jpost.core.Broadcast;
import com.mindorks.jpost.core.Channel;
import com.mindorks.jpost.core.Post;
import com.mindorks.jpost.exceptions.NoSuchChannelException;
import com.mindorks.jpost.exceptions.NullObjectException;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by janisharali on 22/09/16.
 */
public class BroadcastCenter {

    private static int CHANNEL_INITIAL_CAPACITY = 5;

    private static ConcurrentHashMap<Integer, WeakReference<Object>> channelMap = new ConcurrentHashMap<>(CHANNEL_INITIAL_CAPACITY);

    public static Channel<PriorityBlockingQueue<WeakReference<Post>>, ConcurrentHashMap<Integer, WeakReference<Object>>> getChannel(Integer channelId)
            throws NoSuchChannelException {
        if(!channelMap.containsKey(channelId)){
            throw new NoSuchChannelException("Channel with id " + channelId + " does not exists");
        }
        if(channelMap.get(channelId) == null){
            throw new NoSuchChannelException("Channel with id " + channelId + " does not exists");
        }
        if(channelMap.get(channelId).get() == null){
            throw new NoSuchChannelException("Channel with id " + channelId + " has been garbage collected");
        }

        return (Channel<PriorityBlockingQueue<WeakReference<Post>>, ConcurrentHashMap<Integer, WeakReference<Object>>>)channelMap.get(channelId).get();
    }

    public static <T> boolean broadcast(Integer channelId, T msg) throws NoSuchChannelException, NullObjectException {
        return false;
    }

    public static <T> T addSubscriber(Integer channelId, T subscriber, Integer subscriberId)
            throws NoSuchChannelException, NullObjectException {
        return null;
    }

    public static List<Objects> getAllSubscribers(Integer channelId) throws NoSuchChannelException {
        return null;
    }
}
