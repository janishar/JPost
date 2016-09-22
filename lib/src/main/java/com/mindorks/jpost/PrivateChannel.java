package com.mindorks.jpost;

import com.mindorks.jpost.core.*;
import com.mindorks.jpost.exceptions.AlreadyExistsException;
import com.mindorks.jpost.exceptions.IllegalStateException;
import com.mindorks.jpost.exceptions.NullObjectException;

import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by janisharali on 22/09/16.
 */
public class PrivateChannel extends AbstractChannel<PriorityBlockingQueue<WeakReference<Post>>,
        ConcurrentHashMap<Integer,WeakReference<Object>>>
        implements CustomChannel<PriorityBlockingQueue<WeakReference<Post>>,
        ConcurrentHashMap<Integer,WeakReference<Object>>>{

    private WeakReference<Object> channelOwner;

    public PrivateChannel(Object owner, Integer channelId, ChannelState state) {
        super(channelId, state, ChannelType.PRIVATE,  new PriorityBlockingQueue<>(Channel.MSG_QUEUE_INITIAL_CAPACITY,
                new Comparator<WeakReference<Post>>() {
                    @Override
                    public int compare(WeakReference<Post> o1, WeakReference<Post> o2) {
                        Post post1 = o1.get();
                        Post post2 = o2.get();
                        if(post1 != null || post2 != null){
                            return post1.getPriority().compareTo(post2.getPriority());
                        }else{
                            return 0;
                        }
                    }
                }),  new ConcurrentHashMap<Integer, WeakReference<Object>>(Channel.SUBSCRIBER_INITIAL_CAPACITY));
        channelOwner = new WeakReference<>(owner);
    }

    @Override
    public <T> void broadcast(T msg) throws IllegalStateException {
        if(super.getChannelState() != ChannelState.OPEN){
            throw new IllegalStateException("Channel with id " + super.getChannelId() + " is closed");
        }
    }

    @Override
    public <T> T addSubscriber(T subscriber, Integer subscriberId )
            throws NullObjectException, AlreadyExistsException, IllegalStateException {
        if(super.getChannelState() != ChannelState.OPEN){
            throw new IllegalStateException("Channel with id " + super.getChannelId() + " is closed");
        }

        if(subscriber == null){
            throw new NullObjectException("subscriber is null");
        }
        if(subscriberId == null){
            throw new NullObjectException("subscriberId is null");
        }
        if(!super.getSubscriberMap().containsKey(subscriberId)){
            throw new AlreadyExistsException("subscriber with subscriberId " + subscriberId + " already registered");
        }
        super.getSubscriberMap().put(subscriberId, new WeakReference<Object>(subscriber));
        return subscriber;
    }

    public WeakReference<Object> getChannelOwner() {
        return channelOwner;
    }

    @Override
    public void removeChannel() {
        super.setChannelState(ChannelState.REMOVED);
    }

    @Override
    public void startChannel() {
        super.setChannelState(ChannelState.OPEN);
    }

    @Override
    public void stopChannel() {
        super.setChannelState(ChannelState.STOPPED);
    }
}
