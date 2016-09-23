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
    public <T> PrivateChannel createPrivateChannel(T owner, Integer channelId)
            throws AlreadyExistsException {
        if(channelId != null){
            PrivateChannel privateChannel = new PrivateChannel(new WeakReference<Object>(owner), channelId, ChannelType.PRIVATE, ChannelState.OPEN);
            channelMap.put(channelId, new WeakReference<Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
                    ConcurrentHashMap<Integer,WeakReference<Object>>>>(privateChannel));
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
        runTask(Channel.DEFAULT_CHANNEL_ID, msg);
    }

    @Override
    public <T> void broadcastAsync(T msg) {
        executorService.execute(new MsgTasKRunner<>(Channel.DEFAULT_CHANNEL_ID, msg));
    }

    @Override
    public <T> void broadcast(Integer channelId, T msg, Integer... subscribers) {
        runTask(channelId, msg, subscribers);
    }

    @Override
    public <T> void broadcastAsync(Integer channelId, T msg, Integer... subscribers) {
        executorService.execute(new MsgTasKRunner<T>(channelId, msg, subscribers));
    }

    @Override
    public <V, T> void broadcast(V owner, Integer channelId, T msg, Integer... subscribers) {
        runTask(owner, channelId, msg, subscribers);
    }

    @Override
    public <V, T> void broadcastAsync(V owner, Integer channelId, T msg, Integer... subscribers) {
        executorService.execute(new PrivateMsgTasKRunner<>(owner, channelId, msg, subscribers));
    }


    @Override
    public <T> void addSubscriber(Integer channelId, T subscriber, Integer subscriberId) {
        executorService.execute(new SubscribeTaskRunner<>(channelId, subscriber, subscriberId));
    }

    @Override
    public <T> void addSubscriber(T subscriber) {
        executorService.execute(new SubscribeTaskRunner<>(Channel.DEFAULT_CHANNEL_ID, subscriber, subscriber.hashCode()));
    }

    @Override
    public <T> void addSubscriber(Integer channelId, T subscriber) {
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
            runTask(channelId, msg, subscribers);
        }
    }

    private class PrivateMsgTasKRunner<V, T> implements Runnable{

        private Integer channelId;
        private T msg;
        private V owner;
        private Integer[] subscribers;

        public PrivateMsgTasKRunner(V owner, Integer channelId, T msg, Integer... subscribers) {
            this.owner = owner;
            this.channelId = channelId;
            this.msg = msg;
            this.subscribers = subscribers;
            new Thread(this, String.valueOf(channelId));
        }

        @Override
        public void run(){
            runTask(owner, channelId, msg, subscribers);
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
            runTask(channelId, subscriber, subscriberId);
        }
    }

    private <T>void runTask(Integer channelId, T msg, Integer... subscribers){
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

    private <V, T>void runTask(V owner, Integer channelId, T msg, Integer... subscribers) {
        try {
            Channel channel = getChannel(channelId);
            if(channel.getChannelState() == ChannelState.OPEN){
                if(channel instanceof PrivateChannel){
                    PrivateChannel privateChannel = (PrivateChannel)channel;
                    if(privateChannel.getChannelOwnerRef() != null && privateChannel.getChannelOwnerRef().get() != null){
                        if(privateChannel.getChannelOwnerRef().get().equals(owner)) {
                            if(subscribers.length > 0) {
                                privateChannel.broadcast(msg, subscribers);
                            }else{
                                privateChannel.broadcast(msg);
                            }
                        }else{
                            throw new PermissionException("Only the owner of the private channel is allowed to broadcast on private channel");
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
        }catch (IllegalStateException e){
            e.printStackTrace();
        }catch (NullObjectException e){
            e.printStackTrace();
        }catch (PermissionException e){
            e.printStackTrace();
        }
    }

    private <T>void runTask(Integer channelId, T subscriber, Integer subscriberId){
        try {
            Channel channel = getChannel(channelId);
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
        }
    }
}
