package com.mindorks.jpost;

import com.mindorks.jpost.core.Channel;
import com.mindorks.jpost.core.ChannelState;
import com.mindorks.jpost.core.Post;
import com.mindorks.jpost.exceptions.*;
import com.mindorks.jpost.exceptions.IllegalStateException;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by janisharali on 22/09/16.
 */
public class BroadcastCenter{

    private static int CHANNEL_INITIAL_CAPACITY = 5;

    private static ConcurrentHashMap<Integer, WeakReference<Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
            ConcurrentHashMap<Integer,WeakReference<Object>>>>> customChannelMap;

    static {
        customChannelMap = new ConcurrentHashMap<>(CHANNEL_INITIAL_CAPACITY);
        customChannelMap.put(Channel.DEFAULT_CHANNEL_ID,
                new WeakReference<Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
                ConcurrentHashMap<Integer,WeakReference<Object>>>>(new DefaultChannel()));
    }

    private static ExecutorService executorService = Executors.newCachedThreadPool();

    public static <T>PrivateChannel createPrivateChannel(T owner, Integer channelId)
            throws NullObjectException, AlreadyExistsException{
        if(channelId == null){
            throw new NullObjectException("channelId is null");
        }

        PrivateChannel privateChannel = new PrivateChannel(owner, channelId, ChannelState.OPEN);
        customChannelMap.put(channelId, new WeakReference<Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
                ConcurrentHashMap<Integer,WeakReference<Object>>>>(privateChannel));
        return privateChannel;
    }

    public static PublicChannel createPublicChannel(Integer channelId)
            throws AlreadyExistsException{
        if(channelId == null){
            System.out.println("channelId is null");
            return null;
        }
        if (customChannelMap.containsKey(channelId)) {
            throw new AlreadyExistsException("Channel with id " + channelId + " already exists");
        }
        PublicChannel publicChannel = new PublicChannel(channelId, ChannelState.OPEN);
        customChannelMap.put(channelId, new WeakReference<Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
                ConcurrentHashMap<Integer, WeakReference<Object>>>>(publicChannel));
        return publicChannel;

    }

    public static void stopChannel(Integer channelId){
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

    public static void reopenChannel(Integer channelId){
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

    public static void removeChannel(Integer channelId){
        try {
            getChannel(channelId).setChannelState(ChannelState.REMOVED);
        }
        catch (NullObjectException e){
            e.printStackTrace();
        }catch (NoSuchChannelException e){
            e.printStackTrace();
        }
    }

    private static Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
            ConcurrentHashMap<Integer, WeakReference<Object>>> getChannel(Integer channelId)
            throws NoSuchChannelException, NullObjectException {
        if(channelId == null){
            throw new NullObjectException("channelId is null");
        }
        if(!customChannelMap.containsKey(channelId)){
            throw new NoSuchChannelException("Channel with id " + channelId + " does not exists");
        }
        if(customChannelMap.get(channelId) == null){
            throw new NoSuchChannelException("Channel with id " + channelId + " does not exists");
        }
        if(customChannelMap.get(channelId).get() == null){
            throw new NoSuchChannelException("Channel with id " + channelId + " has been garbage collected");
        }

        return customChannelMap.get(channelId).get();
    }

    public static <T> void broadcast(Integer channelId, T msg){
        executorService.execute(new MsgTasKRunner<T>(channelId, msg));
    }

    public static <T> void broadcast(T msg){
        executorService.execute(new MsgTasKRunner<>(Channel.DEFAULT_CHANNEL_ID, msg));
    }

    public static <T> void addSubscriber(Integer channelId, T subscriber, Integer subscriberId){
        executorService.execute(new SubscribeTaskRunner<>(channelId, subscriber, subscriberId));
    }

    public static List<Objects> getAllSubscribers(Integer channelId) throws NoSuchChannelException {
        return null;
    }

    private static class MsgTasKRunner<T> implements Runnable{

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

    private static class SubscribeTaskRunner<T> implements Runnable{

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
