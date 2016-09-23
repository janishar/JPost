package com.mindorks.jpost;

import com.mindorks.jpost.core.*;
import com.mindorks.jpost.core.ChannelPost;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by janisharali on 22/09/16.
 */
public class PublicChannel extends DefaultChannel
        implements CustomChannel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
        ConcurrentHashMap<Integer,WeakReference<Object>>>{

    public PublicChannel(Integer channelId, ChannelType type, ChannelState state) {
        super(channelId, type, state);
    }

    @Override
    public void terminateChannel() {
        super.setChannelState(ChannelState.TERMINATED);
        super.getSubscriberMap().clear();
        super.getPostQueue().clear();
    }

    @Override
    public void startChannel() {
        super.setChannelState(ChannelState.OPEN);
    }

    @Override
    public void stopChannel() {
        super.setChannelState(ChannelState.STOPPED);
    }
}
