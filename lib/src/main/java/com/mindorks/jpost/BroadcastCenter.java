package com.mindorks.jpost;

import com.mindorks.jpost.core.*;
import com.mindorks.jpost.exceptions.*;
import com.mindorks.jpost.exceptions.IllegalStateException;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import static com.mindorks.jpost.JPost.channelMap;
import static com.mindorks.jpost.JPost.executorService;

/**
 * Created by janisharali on 22/09/16.
 */
public class BroadcastCenter implements Broadcast<Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
        ConcurrentHashMap<Integer,WeakReference<Object>>>>{

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
        try {
            Channel channel = getChannel(channelId);
            if (channel.getChannelState() == ChannelState.REMOVED) {
                throw new IllegalStateException("Channel with id " + channelId + " has been removed");
            }
            channel.setChannelState(ChannelState.STOPPED);
        }catch (NullObjectException e){
            e.printStackTrace();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }catch (NoSuchChannelException e){
            e.printStackTrace();
        }
    }

    @Override
    public void reopenChannel(Integer channelId) {
        try {
            Channel channel = getChannel(channelId);
            if (channel.getChannelState() == ChannelState.REMOVED) {
                throw new IllegalStateException("Channel with id " + channelId + " has been removed");
            }
            channel.setChannelState(ChannelState.OPEN);
        }catch (NullObjectException e){
            e.printStackTrace();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }catch (NoSuchChannelException e){
            e.printStackTrace();
        }
    }

    @Override
    public void removeChannel(Integer channelId) {
        try {
            getChannel(channelId).setChannelState(ChannelState.REMOVED);
        }
        catch (NullObjectException e){
            e.printStackTrace();
        }catch (NoSuchChannelException e){
            e.printStackTrace();
        }
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
    public <T> void broadcast(Integer channelId, T msg) {
        executorService.execute(new MsgTasKRunner<T>(channelId, msg));
    }

    @Override
    public <V, T> void broadcast(V owner, Integer channelId, T msg) {
        executorService.execute(new PrivateMsgTasKRunner<>(owner, channelId, msg));
    }

    @Override
    public <T> void broadcast(T msg) {
        executorService.execute(new MsgTasKRunner<>(Channel.DEFAULT_CHANNEL_ID, msg));
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
    public List<Objects> getAllSubscribers(Integer channelId) throws NoSuchChannelException {
        return null;
    }

    private class MsgTasKRunner<T> implements Runnable{

        private Integer channelId;
        private T msg;

        public MsgTasKRunner(Integer channelId, T msg) {
            this.channelId = channelId;
            this.msg = msg;
            new Thread(this, String.valueOf(channelId));
        }

        @Override
        public void run(){
            try {
                Channel channel = getChannel(channelId);
                channel.broadcast(msg);
            }catch (NoSuchChannelException e){
                e.printStackTrace();
            }catch (IllegalStateException e){
                e.printStackTrace();
            }catch (NullObjectException e){
                e.printStackTrace();
            }
        }
    }

    private class PrivateMsgTasKRunner<V, T> implements Runnable{

        private Integer channelId;
        private T msg;
        private V owner;

        public PrivateMsgTasKRunner(V owner, Integer channelId, T msg) {
            this.owner = owner;
            this.channelId = channelId;
            this.msg = msg;
            new Thread(this, String.valueOf(channelId));
        }

        @Override
        public void run(){
            try {
                Channel channel = getChannel(channelId);
                if(channel instanceof PrivateChannel){
                    PrivateChannel privateChannel = (PrivateChannel)channel;
                    if(privateChannel.getChannelOwnerRef() != null && privateChannel.getChannelOwnerRef().get() != null){
                        if(privateChannel.getChannelOwnerRef().get().equals(owner)) {
                            privateChannel.broadcast(msg);
                        }else{
                            throw new PermissionException("Only the owner of the private channel is allowed to broadcast on private channel");
                        }
                    }
                }else{
                    throw new NoSuchChannelException("No private channel with channelId " + channelId + " exists");
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
            try {
                Channel channel = getChannel(channelId);
                channel.addSubscriber(subscriber, subscriberId);
                int a = 1;
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
}
