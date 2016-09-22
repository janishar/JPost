package com.mindorks.jpost;

import com.mindorks.jpost.core.Channel;
import com.mindorks.jpost.core.Post;
import com.mindorks.jpost.exceptions.AlreadyExistsException;
import com.mindorks.jpost.exceptions.NullObjectException;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by janisharali on 22/09/16.
 */
public class DefaultChannel implements Channel<PriorityBlockingQueue<WeakReference<Post>>, ConcurrentHashMap<Integer, WeakReference<Object>>>{

    private Integer channelId;
    private Integer channelType;
    private PriorityBlockingQueue<Post> postQueue;
    private ConcurrentHashMap<Integer, Object> subscriberMap;

    public DefaultChannel(Integer channelId) {
        this.channelId = channelId;
        this.channelType = Channel.CHANNEL_TYPE_DEFAULT;
        postQueue = new PriorityBlockingQueue<>(Channel.MSG_QUEUE_INITIAL_CAPACITY, new PriorityComparator());
    }

    private class PriorityComparator implements Comparator<Post>{
        @Override
        public int compare(Post o1, Post o2) {
            return o1.getPriority().compareTo(o2.getPriority());
        }
    }

    @Override
    public Integer getChannelId() {
        return channelId;
    }

    @Override
    public Integer getChannelType() {
        return null;
    }

    @Override
    public PriorityBlockingQueue<WeakReference<Post>> getPostQueue() {
        return null;
    }

    @Override
    public ConcurrentHashMap<Integer, WeakReference<Object>> getSubscriberMap() {
        return null;
    }

    @Override
    public <T> boolean broadcast(T msg) throws NullObjectException {
        if(msg == null){throw new NullObjectException("message send is null");}
        return true;
    }

    @Override
    public <T> T addSubscriber(T subscriber, Integer subscriberId) throws NullObjectException, AlreadyExistsException {
        if(subscriber == null){
            throw new NullObjectException("subscriber is null");
        }
        if(subscriberId == null){
            throw new NullObjectException("subscriberId is null");
        }
        if(!subscriberMap.containsKey(subscriberId)){
            throw new AlreadyExistsException("subscriber with subscriberId " + subscriberId + " already registered");
        }
        subscriberMap.put(subscriberId, subscriber);
        return subscriber;
    }

    @Override
    public Collection<Object> getAllSubscribers() {
        return subscriberMap.values();
    }
}
