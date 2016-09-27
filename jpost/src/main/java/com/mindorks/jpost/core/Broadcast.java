/*
 * Copyright (C) 2016 Janishar Ali Anwar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.mindorks.jpost.core;

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
     * @param owner it is the owner of the private channel.
     * @param channelId unique int id assigned to the private channel.
     * @param <T> object casting to the owner class.
     * @return it returns an instance of the created PrivateChannel.
     * @throws AlreadyExistsException
     */
    <T>C createPrivateChannel(T owner, Integer channelId) throws AlreadyExistsException;

    /**
     *
     * @param owner it is the owner of the private channel.
     * @param channelId unique int id assigned to the private channel.
     * @param subscriberId unique int id assigned to the subscriber for the  private channel.
     * @param <T> object casting to the owner class.
     * @return it returns an instance of the created PrivateChannel.
     * @throws AlreadyExistsException
     */
    <T>C createPrivateChannel(T owner, Integer channelId, Integer subscriberId) throws AlreadyExistsException;
    /**
     *
     * @param channelId unique int id assigned to the public channel.
     * @return it returns the instance of the created PublicChannel.
     * @throws AlreadyExistsException
     */
    C createPublicChannel(Integer channelId) throws AlreadyExistsException;

    /**
     *
     * @param channelId it stops the public/private channel having channel id equal channelId.
     *                  No message can be send/received through this channel until it is restarted.
     */
    void stopChannel(Integer channelId);

    /**
     *
     * @param channelId it open the channel with channel id. The sending/receiving messages through this channel can resumed.
     */
    void reopenChannel(Integer channelId);

    /**
     *
     * @param channelId it stops the public/private channel having channel id equal channelId.
     *                  No message can be send/received through this channel after termination.
     *                  It removes the channel from the BroadcastCenter.
     */
    void terminateChannel(Integer channelId);

    /**
     *
     * @param channelId unique id identifying a channel.
     * @return returns the channel having id as channel id.
     * @throws NoSuchChannelException
     * @throws NullObjectException
     */
    C getChannel(Integer channelId) throws NoSuchChannelException, NullObjectException;

    /**
     *
     * @param channelId unique id identifying a public channel.
     * @param msg any object that has to be send through the public channel.
     * @param subscribers the list of specific subscribers subscribed to the public channel that has to be send the message.
     *                    If no subscriber is provided then the message is broadcast to all the subscribers of this channel.
     * @param <T>  object cast of the msg.
     */
     <T> void broadcast(Integer channelId, T msg, Integer... subscribers);

    /**
     *
     * @param registeredSubscriber it is the owner or the subscriber added to the private channel by th owner.
     * @param channelId unique id identifying a private channel.
     * @param msg any object that has to be send through the private channel.
     * @param subscribers the list of specific subscribers subscribed to the private channel that has to be send the message.
     *                    If no subscriber is provided then the message is broadcast to all the subscribers of this channel.
     * @param <V> object cast of the registeredSubscriber.
     * @param <T> object cast of the msg.
     */
     <V, T> void broadcast(V registeredSubscriber, Integer channelId, T msg, Integer... subscribers);

    /**
     *
     * @param msg any object that has to be send through the center global channel.
     *            It is send to all the subscriber of this channel.
     * @param <T> object cast of the msg.
     */
    public <T> void broadcast(T msg);

    /**
     *
     * @param channelId unique id identifying a public channel.
     * @param msg any object that has to be send through the public channel asynchronously.
     * @param subscribers the list of specific subscribers subscribed to the public channel that has to be send the message.
     *                    If no subscriber is provided then the message is broadcast to all the subscribers of this channel.
     * @param <T> object cast of the msg.
     */
     <T> void broadcastAsync(Integer channelId, T msg, Integer... subscribers)throws JPostNotRunningException;

    /**
     *
     * @param registeredSubscriber it is the owner or the subscriber added to the private channel by th owner.
     * @param channelId unique id identifying a private channel.
     * @param msg any object that has to be send through the private channel.
     * @param subscribers the list of specific subscribers subscribed to the private channel that has to be send the message asynchronously.
     *                    If no subscriber is provided then the message is broadcast to all the subscribers of this channel.
     * @param <V> object cast of the registeredSubscriber.
     * @param <T> object cast of the msg.
     */
     <V, T> void broadcastAsync(V registeredSubscriber, Integer channelId, T msg, Integer... subscribers)throws JPostNotRunningException;

    /**
     *
     * @param msg any object that has to be send through the center global channel.
     *            It is send to all the subscriber of this channel asynchronously.
     * @param <T> object cast of the msg.
     */
     <T> void broadcastAsync(T msg)throws JPostNotRunningException;

    /**
     *
     * @param channelId unique id identifying a public channel.
     * @param subscriber subscriber that is subscribing to the public channel.
     *                   the subscriber is provided subscriber.hashCode() as the subscriber id.
     * @param <T> object cast of the subscriber.
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
     * @param owner the creator of the private channel.
     * @param channelId unique id identifying a public channel.
     * @param subscriber subscriber that is subscribing to the private channel.
     *                   the subscriber is provided subscriber.hashCode() as the subscriber id.
     * @param <T> object cast of the subscriber.
     * @param <V> object cast of the owner.
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
     * @param channelId unique id identifying a public channel.
     * @param subscriber subscriber that is subscribing to the public channel.
     * @param subscriberId the subscriber is provided subscriberId as the subscriber id.
     * @param <T> object cast of the subscriber.
     * @throws NoSuchChannelException
     * @throws AlreadyExistsException
     * @throws PermissionException
     * @throws IllegalChannelStateException
     * @throws NullObjectException
     */
    <T> void addSubscriber(Integer channelId, T subscriber, Integer subscriberId)
             throws NoSuchChannelException, AlreadyExistsException, PermissionException, IllegalChannelStateException, NullObjectException;

    /**
     *
     * @param owner the creator of the private channel.
     * @param channelId unique id identifying a private channel.
     * @param subscriber subscriber that is subscribing to the private channel.
     * @param subscriberId  the subscriber is provided subscriberId as the subscriber id.
     * @param <T> object cast of the subscriber.
     * @param <V> object cast of the owner.
     * @throws NoSuchChannelException
     * @throws AlreadyExistsException
     * @throws PermissionException
     * @throws IllegalChannelStateException
     * @throws NullObjectException
     */
    <T, V> void addSubscriber(V owner, Integer channelId, T subscriber, Integer subscriberId)
             throws NoSuchChannelException, AlreadyExistsException, PermissionException, IllegalChannelStateException, NullObjectException;

    /**
     *
     * @param subscriber subscriber that is subscribing to the center global channel.
     *                   the subscriber is provided subscriber.hashCode() as the subscriber id.
     * @param <T> object cast of the subscriber.
     * @throws AlreadyExistsException
     * @throws NullObjectException
     */
    <T> void addSubscriber(T subscriber) throws AlreadyExistsException, NullObjectException;

    /**
     *
     * @param channelId unique id identifying a public channel.
     * @param subscriber subscriber that is subscribing to the public channel.
     *                   the subscriber is provided subscriber.hashCode() as the subscriber id.
     * @param <T> object cast of the subscriber.
     */
     <T> void addSubscriberAsync(Integer channelId, T subscriber);

    /**
     *
     * @param owner object cast of the owner.
     * @param channelId unique id identifying a private channel.
     * @param subscriber subscriber that is subscribing to the private channel.
     *                   the subscriber is provided subscriber.hashCode() as the subscriber id.
     * @param <T> object cast of the subscriber.
     * @param <V> object cast of the owner.
     */
    <T, V> void addSubscriberAsync(V owner, Integer channelId, T subscriber);

    /**
     *
     * @param channelId unique id identifying a public channel.
     * @param subscriber subscriber that is subscribing to the public channel.
     *                   the subscriber is provided subscriberId as the subscriber id.
     * @param subscriberId the subscriber is provided subscriberId as the subscriber id.
     * @param <T> object cast of the subscriber.
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
