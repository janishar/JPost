package com.mindorks.androidjpost.center;

import com.mindorks.androidjpost.channels.AndroidPrivateChannel;
import com.mindorks.androidjpost.channels.AndroidPublicChannel;
import com.mindorks.jpost.core.Channel;
import com.mindorks.jpost.core.ChannelPost;
import com.mindorks.jpost.core.ChannelState;
import com.mindorks.jpost.core.ChannelType;
import com.mindorks.jpost.exceptions.*;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;


/**
 * Created by janisharali on 27/09/16.
 */
public class AndroidBroadcastCenter extends AbstractAndroidBroadcastCenter {

    @Override
    public <T> Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
            ConcurrentHashMap<Integer, WeakReference<Object>>> createPrivateChannel(T owner, Integer channelId)
            throws AlreadyExistsException {

        if(channelId == null){
            System.out.println("channelId is null");
            return null;
        }
        if (JPost.channelMap.containsKey(channelId)) {
            throw new AlreadyExistsException("Channel with id " + channelId + " already exists");
        }

        try {
            AndroidPrivateChannel privateChannel = new AndroidPrivateChannel(channelId, ChannelType.PRIVATE, ChannelState.OPEN, new WeakReference<Object>(owner));
            JPost.channelMap.put(channelId, privateChannel);
            runPrivateSubscriptionTask(owner, channelId, owner, owner.hashCode());
            return privateChannel;
        }catch (IllegalChannelStateException e){
            e.printStackTrace();
        }catch (PermissionException e){
            e.printStackTrace();
        }catch (NullObjectException e){
            e.printStackTrace();
        }catch (NoSuchChannelException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
            ConcurrentHashMap<Integer, WeakReference<Object>>> createPrivateChannel(T owner, Integer channelId, Integer subscriberId)
            throws AlreadyExistsException{
        if(channelId == null){
            System.out.println("channelId is null");
            return null;
        }
        if (JPost.channelMap.containsKey(channelId)) {
            throw new AlreadyExistsException("Channel with id " + channelId + " already exists");
        }

        try {
            AndroidPrivateChannel privateChannel = new AndroidPrivateChannel(channelId, ChannelType.PRIVATE, ChannelState.OPEN, new WeakReference<Object>(owner));
            JPost.channelMap.put(channelId, privateChannel);
            runPrivateSubscriptionTask(owner, channelId, owner, subscriberId);
            return privateChannel;
        }catch (IllegalChannelStateException e){
            e.printStackTrace();
        }catch (PermissionException e){
            e.printStackTrace();
        }catch (NullObjectException e){
            e.printStackTrace();
        }catch (NoSuchChannelException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
            ConcurrentHashMap<Integer, WeakReference<Object>>> createPublicChannel(Integer channelId)
            throws AlreadyExistsException {
        if(channelId == null){
            System.out.println("channelId is null");
            return null;
        }
        if (JPost.channelMap.containsKey(channelId)) {
            throw new AlreadyExistsException("Channel with id " + channelId + " already exists");
        }
        AndroidPublicChannel publicChannel = new AndroidPublicChannel(channelId, ChannelType.PUBLIC, ChannelState.OPEN);
        JPost.channelMap.put(channelId, publicChannel);
        return publicChannel;
    }
}
