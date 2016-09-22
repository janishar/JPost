package com.mindorks.jpost.core;

import com.mindorks.jpost.PrivateChannel;
import com.mindorks.jpost.PublicChannel;
import com.mindorks.jpost.exceptions.AlreadyExistsException;
import com.mindorks.jpost.exceptions.NoSuchChannelException;
import com.mindorks.jpost.exceptions.NullObjectException;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by janisharali on 23/09/16.
 */
public interface Broadcast<C extends Channel<? extends PriorityBlockingQueue<? extends WeakReference<? extends ChannelPost>>,
        ? extends ConcurrentHashMap<? extends Integer,? extends WeakReference<?>>>> {

    int CHANNEL_INITIAL_CAPACITY = 5;
    <T>PrivateChannel createPrivateChannel(T owner, Integer channelId)
            throws NullObjectException, AlreadyExistsException;

    PublicChannel createPublicChannel(Integer channelId)
            throws AlreadyExistsException;

    void stopChannel(Integer channelId);
    void reopenChannel(Integer channelId);
    void removeChannel(Integer channelId);
    C getChannel(Integer channelId)
            throws NoSuchChannelException, NullObjectException;
    public <T> void broadcast(Integer channelId, T msg);
    public <V, T> void broadcast(V owner, Integer channelId, T msg);
    public <T> void broadcast(T msg);
    public <T> void addSubscriber(Integer channelId, T subscriber, Integer subscriberId);
    public <T> void addSubscriber(T subscriber);
    public List<Objects> getAllSubscribers(Integer channelId) throws NoSuchChannelException;
}
