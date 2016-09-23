package com.mindorks.jpost;

import com.mindorks.jpost.core.*;
import com.mindorks.jpost.exceptions.*;
import com.mindorks.jpost.exceptions.IllegalStateException;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import static com.mindorks.jpost.JPost.channelMap;
import static com.mindorks.jpost.JPost.executorService;

/**
 * Created by janisharali on 22/09/16.
 */
public class BroadcastCenter implements Broadcast<Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
        ConcurrentHashMap<Integer,WeakReference<Object>>>>{

    private ReentrantLock channelStateChangerLock;

    public BroadcastCenter() {
        channelStateChangerLock = new ReentrantLock();
    }

    @Override
    public <T> PrivateChannel createPrivateChannel(T owner, Integer channelId) throws AlreadyExistsException {
        if(channelId != null){
            PrivateChannel privateChannel = new PrivateChannel(new WeakReference<Object>(owner), channelId, ChannelType.PRIVATE, ChannelState.OPEN);
            channelMap.put(channelId, new WeakReference<Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
                    ConcurrentHashMap<Integer,WeakReference<Object>>>>(privateChannel));
            runPrivateSubscriptionTask(owner, channelId, owner, owner.hashCode());
            return privateChannel;
        }
        return null;
    }

    @Override
    public <T> PrivateChannel createPrivateChannel(T owner, Integer channelId, Integer subscriberId) throws AlreadyExistsException {
        if(channelId != null){
            PrivateChannel privateChannel = new PrivateChannel(new WeakReference<Object>(owner), channelId, ChannelType.PRIVATE, ChannelState.OPEN);
            channelMap.put(channelId, new WeakReference<Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
                    ConcurrentHashMap<Integer,WeakReference<Object>>>>(privateChannel));
            runPrivateSubscriptionTask(owner, channelId, owner, subscriberId);
            return privateChannel;
        }
        return null;
    }

    @Override
    public <T> PrivateChannel createPrivateChannelAsync(T owner, Integer channelId) throws AlreadyExistsException {
        if(channelId != null){
            PrivateChannel privateChannel = new PrivateChannel(new WeakReference<Object>(owner), channelId, ChannelType.PRIVATE, ChannelState.OPEN);
            channelMap.put(channelId, new WeakReference<Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
                    ConcurrentHashMap<Integer,WeakReference<Object>>>>(privateChannel));
            executorService.execute(new PrivateMsgTasKRunner<T, T>(owner, channelId, owner, owner.hashCode()));
            return privateChannel;
        }
        return null;
    }

    @Override
    public <T> PrivateChannel createPrivateChannelAsync(T owner, Integer channelId, Integer subscriberId) throws AlreadyExistsException {
        if(channelId != null){
            PrivateChannel privateChannel = new PrivateChannel(new WeakReference<Object>(owner), channelId, ChannelType.PRIVATE, ChannelState.OPEN);
            channelMap.put(channelId, new WeakReference<Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
                    ConcurrentHashMap<Integer,WeakReference<Object>>>>(privateChannel));
            executorService.execute(new PrivateMsgTasKRunner<T, T>(owner, channelId, owner, subscriberId));
            return privateChannel;
        }
        return null;
    }

    @Override
    public PublicChannel createPublicChannel(Integer channelId) throws AlreadyExistsException {
        if(channelId == null){
            System.out.println("channelId is null");
            return null;
        }
        if (channelMap.containsKey(channelId)) {
            throw new AlreadyExistsException("Channel with id " + channelId + " already exists");
        }
        PublicChannel publicChannel = new PublicChannel(channelId, ChannelType.PUBLIC, ChannelState.OPEN);
        channelMap.put(channelId, new WeakReference<Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
                ConcurrentHashMap<Integer, WeakReference<Object>>>>(publicChannel));
        return publicChannel;
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
    public Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>, ConcurrentHashMap<Integer,
            WeakReference<Object>>> getChannel(Integer channelId)
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
        if(channelMap.get(channelId).get() == null){
            throw new NoSuchChannelException("Channel with id " + channelId + " has been garbage collected");
        }

        return channelMap.get(channelId).get();
    }

    @Override
    public <T> void broadcast(T msg) {
        runBroadcastTask(Channel.DEFAULT_CHANNEL_ID, msg);
    }

    @Override
    public <T> void broadcastAsync(T msg) {
        executorService.execute(new MsgTasKRunner<>(Channel.DEFAULT_CHANNEL_ID, msg));
    }

    @Override
    public <T> void broadcast(Integer channelId, T msg, Integer... subscribers) {
        runBroadcastTask(channelId, msg, subscribers);
    }

    @Override
    public <T> void broadcastAsync(Integer channelId, T msg, Integer... subscribers) {
        executorService.execute(new MsgTasKRunner<T>(channelId, msg, subscribers));
    }

    @Override
    public <V, T> void broadcast(V registeredSubscriber, Integer channelId, T msg, Integer... subscribers) {
        runPrivateBroadcastTask(registeredSubscriber, channelId, msg, subscribers);
    }

    @Override
    public <V, T> void broadcastAsync(V registeredSubscriber, Integer channelId, T msg, Integer... subscribers) {
        executorService.execute(new PrivateMsgTasKRunner<>(registeredSubscriber, channelId, msg, subscribers));
    }


    @Override
    public <T> void addSubscriber(Integer channelId, T subscriber, Integer subscriberId) {
        runSubscriptionTask(channelId, subscriber, subscriberId);
    }

    @Override
    public <T> void addSubscriber(T subscriber) {
        runSubscriptionTask(Channel.DEFAULT_CHANNEL_ID, subscriber, subscriber.hashCode());
    }

    @Override
    public <T> void addSubscriber(Integer channelId, T subscriber) {
        runSubscriptionTask(channelId, subscriber, subscriber.hashCode());
    }

    @Override
    public <T, V> void addSubscriber(V owner, Integer channelId, T subscriber) {
        runPrivateSubscriptionTask(owner, channelId, subscriber, subscriber.hashCode());
    }

    @Override
    public <T, V> void addSubscriber(V owner, Integer channelId, T subscriber, Integer subscriberId) {
        runPrivateSubscriptionTask(owner, channelId, subscriber, subscriberId);
    }

    @Override
    public <T, V> void addSubscriberAsync(V owner, Integer channelId, T subscriber) {
        runPrivateSubscriptionTask(owner, channelId, subscriber, subscriber.hashCode());
    }

    @Override
    public <T, V> void addSubscriberAsync(V owner, Integer channelId, T subscriber, Integer subscriberId) {
        runPrivateSubscriptionTask(owner, channelId, subscriber, subscriberId);
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
    public List<Objects> getAllSubscribers(Integer channelId) throws NoSuchChannelException {
        return null;
    }

    private class ChannelStateTasKRunner implements Runnable{

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
                    throw new IllegalStateException("Channel with id " + channelId + " has been terminated");
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
            }catch (IllegalStateException e){
                e.printStackTrace();
            }finally {
                channelStateChangerLock.unlock();
            }
        }
    }

    private class MsgTasKRunner<T> implements Runnable{

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
            runBroadcastTask(channelId, msg, subscribers);
        }
    }

    private class PrivateMsgTasKRunner<V, T> implements Runnable{

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
            runPrivateBroadcastTask(registeredSubscriber, channelId, msg, subscribers);
        }
    }

    private class SubscribeTaskRunner<T> implements Runnable{

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
            runSubscriptionTask(channelId, subscriber, subscriberId);
        }
    }

    private <T>void runBroadcastTask(Integer channelId, T msg, Integer... subscribers){
        try {
            Channel channel = getChannel(channelId);
            if(channel.getChannelState() == ChannelState.OPEN){
                if(channel instanceof PublicChannel && subscribers.length > 0){
                    ((PublicChannel)channel).broadcast(msg, subscribers);
                }else {
                    channel.broadcast(msg);
                }
            }else{
                throw new IllegalStateException("Channel with channelId " + channelId + " has been " + channel.getChannelState());
            }
        }catch (NoSuchChannelException e){
            e.printStackTrace();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }catch (NullObjectException e){
            e.printStackTrace();
        }
    }

    private <V, T>void runPrivateBroadcastTask(V registeredSubscriber, Integer channelId, T msg, Integer... subscribers) {
        try {
            Channel channel = getChannel(channelId);
            if(channel.getChannelState() == ChannelState.OPEN){
                if(channel instanceof PrivateChannel){
                    PrivateChannel privateChannel = (PrivateChannel)channel;
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
                throw new IllegalStateException("Channel with channelId " + channelId + " has been " + channel.getChannelState().name());
            }
        }catch (NoSuchChannelException e){
            e.printStackTrace();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }catch (NullObjectException e){
            e.printStackTrace();
        }catch (PermissionException e){
            e.printStackTrace();
        }
    }

    private <T>void runSubscriptionTask(Integer channelId, T subscriber, Integer subscriberId){
        try {
            Channel channel = getChannel(channelId);
            if(channel instanceof PrivateChannel){
                throw new PermissionException("Only owner of the private channel can add a subscriber to private channel");
            }
            if(channel.getChannelState() == ChannelState.OPEN){
                channel.addSubscriber(subscriber, subscriberId);;
            }else{
                throw new IllegalStateException("Channel with channelId " + channelId + " has been " + channel.getChannelState().name());
            }
        }catch (NoSuchChannelException e){
            e.printStackTrace();
        }catch (NullObjectException e){
            e.printStackTrace();
        }catch (AlreadyExistsException e){
            e.printStackTrace();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }catch (PermissionException e){
            e.printStackTrace();
        }
    }

    private <T, V>void runPrivateSubscriptionTask(V owner, Integer channelId, T subscriber, Integer subscriberId){
        try {
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
                throw new IllegalStateException("Channel with channelId " + channelId + " has been " + channel.getChannelState().name());
            }
        }catch (NoSuchChannelException e){
            e.printStackTrace();
        }catch (NullObjectException e){
            e.printStackTrace();
        }catch (AlreadyExistsException e){
            e.printStackTrace();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }catch (PermissionException e){
            e.printStackTrace();
        }
    }
}
