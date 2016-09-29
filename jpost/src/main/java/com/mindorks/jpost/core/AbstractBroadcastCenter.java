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

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by janisharali on 22/09/16.
 */
public abstract class AbstractBroadcastCenter
        implements Broadcast<Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
        ConcurrentHashMap<Integer,WeakReference<Object>>>>{

    private ReentrantLock channelStateChangerLock;
    private ConcurrentHashMap<Integer, Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
            ConcurrentHashMap<Integer,WeakReference<Object>>>> channelMap;
    private ExecutorService executorService;

    public AbstractBroadcastCenter(ConcurrentHashMap<Integer, Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
            ConcurrentHashMap<Integer,WeakReference<Object>>>> channelMap, ExecutorService executorService) {
        this.channelMap = channelMap;
        this.executorService = executorService;
        channelStateChangerLock = new ReentrantLock();
    }

    @Override
    public void stopChannel(Integer channelId) {
        executorService.execute(new ChannelStateTasKRunner(channelId, ChannelState.STOPPED));
    }

    @Override
    public void reopenChannel(Integer channelId) {
        executorService.execute(new ChannelStateTasKRunner(channelId, ChannelState.OPEN));
    }

    @Override
    public void terminateChannel(Integer channelId) {
        executorService.execute(new ChannelStateTasKRunner(channelId, ChannelState.TERMINATED));
    }

    @Override
    public Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
            ConcurrentHashMap<Integer,WeakReference<Object>>> getChannel(Integer channelId)
            throws NoSuchChannelException, NullObjectException {
        if(channelId == null){
            throw new NullObjectException("channelId is null");
        }
        if(!channelMap.containsKey(channelId)){
            throw new NoSuchChannelException("Channel with id " + channelId + " does not exists");
        }
        if(channelMap.get(channelId) == null){
            throw new NoSuchChannelException("Channel with id " + channelId + " does not exists");
        }
        return channelMap.get(channelId);
    }

    @Override
    public <T> void addSubscriber(Integer channelId, T subscriber, Integer subscriberId)
            throws NoSuchChannelException, AlreadyExistsException, PermissionException, IllegalChannelStateException, NullObjectException{
        runSubscriptionTask(channelId, subscriber, subscriberId);
    }

    @Override
    public <T> void addSubscriber(T subscriber)
            throws AlreadyExistsException, NullObjectException{
        try {
            runSubscriptionTask(Channel.DEFAULT_CHANNEL_ID, subscriber, subscriber.hashCode());
        }catch (NoSuchChannelException e){
            e.printStackTrace();
        }catch (IllegalChannelStateException e){
            e.printStackTrace();
        }catch (PermissionException e){
            e.printStackTrace();
        }
    }

    @Override
    public <T> void addSubscriber(Integer channelId, T subscriber)
            throws NoSuchChannelException, AlreadyExistsException, PermissionException, IllegalChannelStateException, NullObjectException{
        runSubscriptionTask(channelId, subscriber, subscriber.hashCode());
    }

    @Override
    public <T, V> void addSubscriber(V owner, Integer channelId, T subscriber)
            throws NoSuchChannelException, AlreadyExistsException, PermissionException, IllegalChannelStateException, NullObjectException{
        runPrivateSubscriptionTask(owner, channelId, subscriber, subscriber.hashCode());
    }

    @Override
    public <T, V> void addSubscriber(V owner, Integer channelId, T subscriber, Integer subscriberId)
            throws NoSuchChannelException, AlreadyExistsException, PermissionException, IllegalChannelStateException, NullObjectException{
        runPrivateSubscriptionTask(owner, channelId, subscriber, subscriberId);
    }

    @Override
    public <T, V> void addSubscriberAsync(V owner, Integer channelId, T subscriber) {
        executorService.execute(new SubscribePrivateTaskRunner<>(owner, channelId, subscriber, subscriber.hashCode()));
    }

    @Override
    public <T, V> void addSubscriberAsync(V owner, Integer channelId, T subscriber, Integer subscriberId) {
        executorService.execute(new SubscribePrivateTaskRunner<>(owner, channelId, subscriber, subscriberId));
    }

    @Override
    public <T> void addSubscriberAsync(Integer channelId, T subscriber, Integer subscriberId) {
        executorService.execute(new SubscribeTaskRunner<>(channelId, subscriber, subscriberId));
    }

    @Override
    public <T> void addSubscriberAsync(T subscriber) {
        executorService.execute(new SubscribeTaskRunner<>(Channel.DEFAULT_CHANNEL_ID, subscriber, subscriber.hashCode()));
    }

    @Override
    public <T> void addSubscriberAsync(Integer channelId, T subscriber) {
        executorService.execute(new SubscribeTaskRunner<>(channelId, subscriber, subscriber.hashCode()));
    }

    @Override
    public <T> void removeSubscriber(T subscriber)
            throws InvalidPropertyException, NoSuchChannelException, NullObjectException{

        Channel channel = getChannel(Channel.DEFAULT_CHANNEL_ID);
        channel.removeSubscriber(subscriber);
    }

    @Override
    public <T> void removeSubscriber(Integer channelId, T subscriber)
            throws InvalidPropertyException, NoSuchChannelException, NullObjectException{

        Channel channel = getChannel(channelId);
        channel.removeSubscriber(subscriber);
    }

    @Override
    public <T> void removeSubscriber(T registeredSubscriber, Integer channelId, Integer subscriberId)
            throws InvalidPropertyException, NoSuchChannelException, PermissionException, NullObjectException{

        Channel channel = getChannel(channelId);
        if(channel instanceof PrivateChannel){
            PrivateChannel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
                    ConcurrentHashMap<Integer,WeakReference<Object>>> privateChannel = (PrivateChannel)channel;
            boolean isPermissionGranted = false;
            for(WeakReference<Object> weakReference : privateChannel.getSubscriberMap().values()){
                Object subscriber = weakReference.get();
                if(subscriber != null && subscriber == registeredSubscriber){
                    isPermissionGranted = true;
                    break;
                }
            }
            if(isPermissionGranted) {
                privateChannel.removeSubscriber(subscriberId);
            }else{
                throw new PermissionException("Only the subscriber of the private channel is allowed to broadcast on private channel");
            }
        }else{
            throw new NoSuchChannelException("No private channel with channelId " + channelId + " exists");
        }
    }

    @Override
    public Collection<WeakReference<Object>> getAllSubscribersWeakRef(Integer channelId) throws NoSuchChannelException {
        try {
            Channel channel = getChannel(channelId);
            return channel.getAllSubscribersReferenceList();
        }catch (NullObjectException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> void broadcast(T msg){
        try {
            runBroadcastTask(Channel.DEFAULT_CHANNEL_ID, msg);
        }catch (NoSuchChannelException e){
            e.printStackTrace();
        }catch (NullObjectException e){
            e.printStackTrace();
        }catch (IllegalChannelStateException e){
            e.printStackTrace();
        }
    }

    @Override
    public <T> void broadcastAsync(T msg) throws JPostNotRunningException{
        if(executorService.isShutdown()){
            throw new JPostNotRunningException("JPost is shutdown");
        }
        executorService.execute(new MsgTasKRunner<>(Channel.DEFAULT_CHANNEL_ID, msg));
    }

    @Override
    public <T> void broadcast(Integer channelId, T msg, Integer... subscribers){
        try {
            runBroadcastTask(channelId, msg, subscribers);
        }catch (NoSuchChannelException e){
            e.printStackTrace();
        }catch (NullObjectException e){
            e.printStackTrace();
        }catch (IllegalChannelStateException e){
            e.printStackTrace();
        }
    }

    @Override
    public <T> void broadcastAsync(Integer channelId, T msg, Integer... subscribers) throws JPostNotRunningException{
        if(executorService.isShutdown()){
            throw new JPostNotRunningException("JPost is shutdown");
        }
        executorService.execute(new MsgTasKRunner<T>(channelId, msg, subscribers));
    }

    @Override
    public <V, T> void broadcast(V registeredSubscriber, Integer channelId, T msg, Integer... subscribers){
        try {
            runPrivateBroadcastTask(registeredSubscriber, channelId, msg, subscribers);
        }catch (NoSuchChannelException e){
            e.printStackTrace();
        }catch (IllegalChannelStateException e){
            e.printStackTrace();
        }catch (PermissionException e){
            e.printStackTrace();
        }catch (NullObjectException e){
            e.printStackTrace();
        }
    }

    @Override
    public <V, T> void broadcastAsync(V registeredSubscriber, Integer channelId, T msg, Integer... subscribers) throws JPostNotRunningException{
        if(executorService.isShutdown()){
            throw new JPostNotRunningException("JPost is shutdown");
        }
        executorService.execute(new PrivateMsgTasKRunner<>(registeredSubscriber, channelId, msg, subscribers));
    }

    protected class ChannelStateTasKRunner implements Runnable{

        private Integer channelId;
        private ChannelState state;

        public ChannelStateTasKRunner(Integer channelId, ChannelState state) {
            this.channelId = channelId;
            this.state = state;
            new Thread(this, String.valueOf(channelId));
        }

        @Override
        public void run(){
            try {
                channelStateChangerLock.lock();
                Channel channel = getChannel(channelId);
                if (channel.getChannelState() == ChannelState.TERMINATED) {
                    throw new IllegalChannelStateException("Channel with id " + channelId + " has been terminated");
                }
                if(channel instanceof CustomChannel){
                    switch (state){
                        case OPEN:
                            ((CustomChannel)channel).startChannel();
                            break;
                        case STOPPED:
                            ((CustomChannel)channel).stopChannel();
                            break;
                        case TERMINATED:
                            ((CustomChannel)channel).terminateChannel();
                            channelMap.remove(channelId);
                            break;
                    }
                }
            }catch (NoSuchChannelException e){
                e.printStackTrace();
            }catch (NullObjectException e){
                e.printStackTrace();
            }catch (IllegalChannelStateException e){
                e.printStackTrace();
            }finally {
                channelStateChangerLock.unlock();
            }
        }
    }

    protected class MsgTasKRunner<T> implements Runnable{

        private Integer channelId;
        private T msg;
        private Integer[] subscribers;

        public MsgTasKRunner(Integer channelId, T msg, Integer... subscribers) {
            this.channelId = channelId;
            this.msg = msg;
            this.subscribers = subscribers;
            new Thread(this, String.valueOf(channelId));
        }

        @Override
        public void run(){
            try{
                runBroadcastTask(channelId, msg, subscribers);
            }catch (NoSuchChannelException e){
                e.printStackTrace();
            }catch (NullObjectException e){
                e.printStackTrace();
            }catch (IllegalChannelStateException e){
                e.printStackTrace();
            }
        }
    }

    protected class PrivateMsgTasKRunner<V, T> implements Runnable{

        private Integer channelId;
        private T msg;
        private V registeredSubscriber;
        private Integer[] subscribers;

        public PrivateMsgTasKRunner(V registeredSubscriber, Integer channelId, T msg, Integer... subscribers) {
            this.registeredSubscriber = registeredSubscriber;
            this.channelId = channelId;
            this.msg = msg;
            this.subscribers = subscribers;
            new Thread(this, String.valueOf(channelId));
        }

        @Override
        public void run(){
            try{
                runPrivateBroadcastTask(registeredSubscriber, channelId, msg, subscribers);
            }catch (NoSuchChannelException e){
                e.printStackTrace();
            }catch (NullObjectException e){
                e.printStackTrace();
            }catch (IllegalChannelStateException e){
                e.printStackTrace();
            }catch (PermissionException e){
                e.printStackTrace();
            }
        }
    }

    protected class SubscribeTaskRunner<T> implements Runnable{

        private Integer channelId;
        private Integer subscriberId;
        private T subscriber;

        public SubscribeTaskRunner(Integer channelId, T subscriber, Integer subscriberId) {
            this.channelId = channelId;
            this.subscriberId = subscriberId;
            this.subscriber = subscriber;
            new Thread(this, String.valueOf(channelId));
        }

        @Override
        public void run(){
            try {
                runSubscriptionTask(channelId, subscriber, subscriberId);
            }catch (NoSuchChannelException e){
                e.printStackTrace();
            }catch (NullObjectException e){
                e.printStackTrace();
            }catch (AlreadyExistsException e){
                e.printStackTrace();
            }catch (IllegalChannelStateException e){
                e.printStackTrace();
            }catch (PermissionException e){
                e.printStackTrace();
            }
        }
    }

    protected class SubscribePrivateTaskRunner<V, T> implements Runnable{

        private Integer channelId;
        private Integer subscriberId;
        private V owner;
        private T subscriber;

        public SubscribePrivateTaskRunner(V owner, Integer channelId, T subscriber, Integer subscriberId) {
            this.owner = owner;
            this.channelId = channelId;
            this.subscriberId = subscriberId;
            this.subscriber = subscriber;
            new Thread(this, String.valueOf(channelId));
        }

        @Override
        public void run(){
            try {
                runPrivateSubscriptionTask(owner, channelId, subscriber, subscriberId);
            }catch (NoSuchChannelException e){
                e.printStackTrace();
            }catch (NullObjectException e){
                e.printStackTrace();
            }catch (AlreadyExistsException e){
                e.printStackTrace();
            }catch (IllegalChannelStateException e){
                e.printStackTrace();
            }catch (PermissionException e){
                e.printStackTrace();
            }
        }
    }

    protected  <T>void runBroadcastTask(Integer channelId, T msg, Integer... subscribers)
            throws NoSuchChannelException, IllegalChannelStateException, NullObjectException{

        Channel channel = getChannel(channelId);
        if(channel.getChannelState() == ChannelState.OPEN){
            if(channel instanceof PublicChannel && subscribers.length > 0){
                ((PublicChannel)channel).broadcast(msg, subscribers);
            }else {
                channel.broadcast(msg);
            }
        }else{
            throw new IllegalChannelStateException("Channel with channelId " + channelId + " has been " + channel.getChannelState());
        }
    }

    protected <V, T>void runPrivateBroadcastTask(V registeredSubscriber, Integer channelId, T msg, Integer... subscribers)
            throws NoSuchChannelException, PermissionException, IllegalChannelStateException, NullObjectException{

        Channel channel = getChannel(channelId);
        if(channel.getChannelState() == ChannelState.OPEN){
            if(channel instanceof PrivateChannel){
                PrivateChannel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
                        ConcurrentHashMap<Integer,WeakReference<Object>>> privateChannel = (PrivateChannel)channel;
                boolean isPermissionGranted = false;
                for(WeakReference weakReference : privateChannel.getSubscriberMap().values()){
                    Object subscriber = weakReference.get();
                    if(subscriber != null && subscriber == registeredSubscriber){
                        isPermissionGranted = true;
                        break;
                    }
                }
                if(isPermissionGranted) {
                    if(subscribers.length > 0) {
                        privateChannel.broadcast(msg, subscribers);
                    }else{
                        privateChannel.broadcast(msg);
                    }
                }else{
                    throw new PermissionException("Only the subscriber of the private channel is allowed to broadcast on private channel");
                }
            }else{
                throw new NoSuchChannelException("No private channel with channelId " + channelId + " exists");
            }
        }else{
            throw new IllegalChannelStateException("Channel with channelId " + channelId + " has been " + channel.getChannelState().name());
        }
    }

    protected <T>void runSubscriptionTask(Integer channelId, T subscriber, Integer subscriberId)
            throws NoSuchChannelException, AlreadyExistsException, PermissionException, IllegalChannelStateException, NullObjectException{

        Channel channel = getChannel(channelId);
        if(channel instanceof PrivateChannel){
            throw new PermissionException("Only owner of the private channel can add a subscriber to private channel");
        }
        if(channel.getChannelState() == ChannelState.OPEN){
            channel.addSubscriber(subscriber, subscriberId);;
        }else{
            throw new IllegalChannelStateException("Channel with channelId " + channelId + " has been " + channel.getChannelState().name());
        }
    }

    protected <T, V>void runPrivateSubscriptionTask(V owner, Integer channelId, T subscriber, Integer subscriberId)
            throws NoSuchChannelException, AlreadyExistsException, PermissionException, IllegalChannelStateException, NullObjectException{

        Channel channel = getChannel(channelId);
        if(channel.getChannelState() == ChannelState.OPEN){
            if(channel instanceof PrivateChannel){
                PrivateChannel privateChannel = (PrivateChannel)channel;
                if(privateChannel.getChannelOwnerRef() != null
                        && privateChannel.getChannelOwnerRef().get() != null){
                    if(privateChannel.getChannelOwnerRef().get().equals(owner)) {
                        privateChannel.addSubscriber(subscriber, subscriberId);
                    }else{
                        throw new PermissionException("Only the owner of the private channel is allowed to add subscribers to private channel");
                    }
                }
            }else{
                throw new NoSuchChannelException("No private channel with channelId " + channelId + " exists");
            }
        }else{
            throw new IllegalChannelStateException("Channel with channelId " + channelId + " has been " + channel.getChannelState().name());
        }
    }

    public ConcurrentHashMap<Integer, Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
            ConcurrentHashMap<Integer, WeakReference<Object>>>> getChannelMap() {
        return channelMap;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}
