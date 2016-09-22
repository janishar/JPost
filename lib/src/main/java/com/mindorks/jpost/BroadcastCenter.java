package com.mindorks.jpost;

import com.mindorks.jpost.core.Channel;
import com.mindorks.jpost.core.ChannelState;
import com.mindorks.jpost.core.CustomChannel;
import com.mindorks.jpost.core.Post;
import com.mindorks.jpost.exceptions.AlreadyExistsException;
import com.mindorks.jpost.exceptions.IllegalStateException;
import com.mindorks.jpost.exceptions.NoSuchChannelException;
import com.mindorks.jpost.exceptions.NullObjectException;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by janisharali on 22/09/16.
 */
public class BroadcastCenter {

    private static int CHANNEL_INITIAL_CAPACITY = 5;

    private static ConcurrentHashMap<Integer, WeakReference<CustomChannel<PriorityBlockingQueue<WeakReference<Post>>,
            ConcurrentHashMap<Integer,WeakReference<Object>>>>> customChannelMap =
            new ConcurrentHashMap<>(CHANNEL_INITIAL_CAPACITY);

    private static DefaultChannel defaultChannel = new DefaultChannel();

    public static <T>PrivateChannel createPrivateChannel(T owner, Integer channelId)
            throws NullObjectException, AlreadyExistsException{
        if(channelId == null){
            throw new NullObjectException("channelId is null");
        }

        PrivateChannel privateChannel = new PrivateChannel(owner, channelId, ChannelState.OPEN);
        customChannelMap.put(channelId, new WeakReference<CustomChannel<PriorityBlockingQueue<WeakReference<Post>>,
                ConcurrentHashMap<Integer,WeakReference<Object>>>>(privateChannel));
        return privateChannel;
    }

    public static PublicChannel createPublicChannel(Integer channelId)
            throws NullObjectException, AlreadyExistsException{
        if(channelId == null){
            throw new NullObjectException("channelId is null");
        }

        if(customChannelMap.containsKey(channelId)){
            throw new AlreadyExistsException("Channel with id " + channelId + " already exists");
        }

        PublicChannel publicChannel = new PublicChannel(channelId, ChannelState.OPEN);
        customChannelMap.put(channelId, new WeakReference<CustomChannel<PriorityBlockingQueue<WeakReference<Post>>,
                ConcurrentHashMap<Integer,WeakReference<Object>>>>(publicChannel));
        return publicChannel;
    }

    public static void stopChannel(Integer channelId) throws NoSuchChannelException, IllegalStateException{
        Channel channel = getChannel(channelId);
        if(channel.getChannelState() == ChannelState.REMOVED){
            throw new IllegalStateException("Channel with id " + channelId + " has been removed");
        }
        channel.setChannelState(ChannelState.STOPPED);
    }

    public static void reopenChannel(Integer channelId)throws NoSuchChannelException, IllegalStateException{
        Channel channel = getChannel(channelId);
        if(channel.getChannelState() == ChannelState.REMOVED){
            throw new IllegalStateException("Channel with id " + channelId + " has been removed");
        }
        channel.setChannelState(ChannelState.OPEN);
    }

    public static void removeChannel(Integer channelId)throws NoSuchChannelException{
        getChannel(channelId).setChannelState(ChannelState.REMOVED);
    }

    private static CustomChannel<PriorityBlockingQueue<WeakReference<Post>>,
            ConcurrentHashMap<Integer, WeakReference<Object>>> getChannel(Integer channelId)
            throws NoSuchChannelException {
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

    public static <T> void broadcast(Integer channelId, T msg)
            throws NoSuchChannelException, NullObjectException, IllegalStateException {
        if(channelId == null){
            throw new NullObjectException("channelId is null");
        }
        if(msg == null){
            throw new NullObjectException("message send is null");
        }

        CustomChannel channel = getChannel(channelId);
        channel.broadcast(msg);
    }

    public static <T> void broadcast(T msg)
            throws NullObjectException{
        if(msg == null){
            throw new NullObjectException("message send is null");
        }
        try {
            defaultChannel.broadcast(msg);
        }catch (IllegalStateException e){
            e.printStackTrace();
        }

    }

    public static <T> T addSubscriber(Integer channelId, T subscriber, Integer subscriberId)
            throws NoSuchChannelException, NullObjectException {
        return null;
    }

    public static List<Objects> getAllSubscribers(Integer channelId) throws NoSuchChannelException {
        return null;
    }
}
