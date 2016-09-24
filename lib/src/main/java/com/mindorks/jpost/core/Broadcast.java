package com.mindorks.jpost.core;

import com.mindorks.jpost.PrivateChannel;
import com.mindorks.jpost.PublicChannel;
import com.mindorks.jpost.exceptions.*;
import com.mindorks.jpost.exceptions.IllegalChannelStateException;

import java.lang.ref.WeakReference;
import java.util.Collection;
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
     * @throws AlreadyExistsException
     */
    <T>PrivateChannel createPrivateChannel(T owner, Integer channelId) throws AlreadyExistsException;

    /**
     *
     * @param owner
     * @param channelId
     * @param subscriberId
     * @param <T>
     * @return
     * @throws AlreadyExistsException
     */
    <T>PrivateChannel createPrivateChannel(T owner, Integer channelId, Integer subscriberId) throws AlreadyExistsException;
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
    void terminateChannel(Integer channelId);

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
     <T> void broadcast(Integer channelId, T msg, Integer... subscribers);

    /**
     *
     * @param registeredSubscriber
     * @param channelId
     * @param msg
     * @param subscribers
     * @param <V>
     * @param <T>
     */
     <V, T> void broadcast(V registeredSubscriber, Integer channelId, T msg, Integer... subscribers);

    /**
     *
     * @param msg
     * @param <T>
     */
    public <T> void broadcast(T msg);

    /**
     *
     * @param channelId
     * @param msg
     * @param subscribers
     * @param <T>
     */
     <T> void broadcastAsync(Integer channelId, T msg, Integer... subscribers)throws JPostNotRunningException;

    /**
     *
     * @param registeredSubscriber
     * @param channelId
     * @param msg
     * @param subscribers
     * @param <V>
     * @param <T>
     */
     <V, T> void broadcastAsync(V registeredSubscriber, Integer channelId, T msg, Integer... subscribers)throws JPostNotRunningException;

    /**
     *
     * @param msg
     * @param <T>
     */
     <T> void broadcastAsync(T msg)throws JPostNotRunningException;

    /**
     *
     * @param channelId
     * @param subscriber
     * @param <T>
     * @throws NoSuchChannelException
     * @throws AlreadyExistsException
     * @throws PermissionException
     * @throws IllegalChannelStateException
     * @throws NullObjectException
     */
     <T> void addSubscriber(Integer channelId, T subscriber)
             throws NoSuchChannelException, AlreadyExistsException, PermissionException, IllegalChannelStateException, NullObjectException;

    /**
     *
     * @param owner
     * @param channelId
     * @param subscriber
     * @param <T>
     * @param <V>
     * @throws NoSuchChannelException
     * @throws AlreadyExistsException
     * @throws PermissionException
     * @throws IllegalChannelStateException
     * @throws NullObjectException
     */
     <T, V> void addSubscriber(V owner, Integer channelId, T subscriber)
             throws NoSuchChannelException, AlreadyExistsException, PermissionException, IllegalChannelStateException, NullObjectException;

    /**
     *
     * @param channelId
     * @param subscriber
     * @param subscriberId
     * @param <T>
     * @throws NoSuchChannelException
     * @throws AlreadyExistsException
     * @throws PermissionException
     * @throws IllegalChannelStateException
     * @throws NullObjectException
     */
    <T> void addSubscriber(Integer channelId, T subscriber, Integer subscriberId)
             throws NoSuchChannelException, AlreadyExistsException, PermissionException, IllegalChannelStateException, NullObjectException;

    <T, V> void addSubscriber(V owner, Integer channelId, T subscriber, Integer subscriberId)
             throws NoSuchChannelException, AlreadyExistsException, PermissionException, IllegalChannelStateException, NullObjectException;

    /**
     *
     * @param subscriber
     * @param <T>
     * @throws AlreadyExistsException
     * @throws NullObjectException
     */
    <T> void addSubscriber(T subscriber)
             throws AlreadyExistsException, NullObjectException;

    /**
     *
     * @param channelId
     * @param subscriber
     * @param <T>
     */
     <T> void addSubscriberAsync(Integer channelId, T subscriber);

    /**
     *
     * @param owner
     * @param channelId
     * @param subscriber
     * @param <T>
     * @param <V>
     */
    <T, V> void addSubscriberAsync(V owner, Integer channelId, T subscriber);

    /**
     *
     * @param channelId
     * @param subscriber
     * @param subscriberId
     * @param <T>
     */
     <T> void addSubscriberAsync(Integer channelId, T subscriber, Integer subscriberId);

    /**
     *
     * @param owner
     * @param channelId
     * @param subscriber
     * @param subscriberId
     * @param <T>
     * @param <V>
     */
     <T, V> void addSubscriberAsync(V owner, Integer channelId, T subscriber, Integer subscriberId);

    /**
     *
     * @param subscriber
     * @param <T>
     */
     <T> void addSubscriberAsync(T subscriber);

    /**
     *
     * @param subscriber
     * @param <T>
     */
     <T> void removeSubscriber(T subscriber)
             throws InvalidPropertyException, NoSuchChannelException, NullObjectException;

    /**
     *
     * @param channelId
     * @param subscriber
     * @param <T>
     */
     <T> void removeSubscriber(Integer channelId, T subscriber)
             throws InvalidPropertyException, NoSuchChannelException, NullObjectException;

    /**
     *
     * @param registeredSubscriber
     * @param channelId
     * @param subscriberId
     * @param <T>
     */
     <T> void removeSubscriber(T registeredSubscriber, Integer channelId, Integer subscriberId)
             throws InvalidPropertyException, NoSuchChannelException, PermissionException, NullObjectException;

    /**
     *
     * @param channelId
     * @return
     * @throws NoSuchChannelException
     */
     Collection<? extends WeakReference<?>> getAllSubscribersWeakRef(Integer channelId) throws NoSuchChannelException;
}
