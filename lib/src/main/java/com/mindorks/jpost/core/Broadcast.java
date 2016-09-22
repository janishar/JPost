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

    /**
     *
     * @param owner
     * @param channelId
     * @param <T>
     * @return
     * @throws NullObjectException
     * @throws AlreadyExistsException
     */
    <T>PrivateChannel createPrivateChannel(T owner, Integer channelId) throws NullObjectException, AlreadyExistsException;

    /**
     *
     * @param channelId
     * @return
     * @throws AlreadyExistsException
     */
    PublicChannel createPublicChannel(Integer channelId) throws AlreadyExistsException;

    /**
     *
     * @param channelId
     */
    void stopChannel(Integer channelId);

    /**
     *
     * @param channelId
     */
    void reopenChannel(Integer channelId);

    /**
     *
     * @param channelId
     */
    void removeChannel(Integer channelId);

    /**
     *
     * @param channelId
     * @return
     * @throws NoSuchChannelException
     * @throws NullObjectException
     */
    C getChannel(Integer channelId) throws NoSuchChannelException, NullObjectException;

    /**
     *
     * @param channelId
     * @param msg
     * @param <T>
     */
    public <T> void broadcast(Integer channelId, T msg);

    /**
     *
     * @param owner
     * @param channelId
     * @param msg
     * @param <V>
     * @param <T>
     */
    public <V, T> void broadcast(V owner, Integer channelId, T msg);

    /**
     *
     * @param msg
     * @param <T>
     */
    public <T> void broadcast(T msg);

    /**
     *
     * @param channelId
     * @param subscriber
     * @param subscriberId
     * @param <T>
     */
    public <T> void addSubscriber(Integer channelId, T subscriber, Integer subscriberId);

    /**
     *
     * @param subscriber
     * @param <T>
     */
    public <T> void addSubscriber(T subscriber);

    /**
     *
     * @param channelId
     * @return
     * @throws NoSuchChannelException
     */
    public List<Objects> getAllSubscribers(Integer channelId) throws NoSuchChannelException;
}
