package com.mindorks.jpost;

import com.mindorks.jpost.core.*;
import com.mindorks.jpost.exceptions.InvalidPropertyException;
import com.mindorks.jpost.exceptions.NullObjectException;

import java.lang.ref.WeakReference;

/**
 * Created by janisharali on 22/09/16.
 */
public class PrivateChannel extends PublicChannel{

    private WeakReference<Object> channelOwnerRef;

    public PrivateChannel(WeakReference<Object> channelOwnerRef, Integer channelId, ChannelType type, ChannelState state) {
        super(channelId, type, state);
        this.channelOwnerRef = channelOwnerRef;
    }

    public WeakReference<Object> getChannelOwnerRef() {
        return channelOwnerRef;
    }

    public synchronized void removeSubscriber(Integer subscriberId) throws NullObjectException,InvalidPropertyException {
        if(subscriberId == null){
            throw new NullObjectException("subscriberId is null");
        }
        if(getSubscriberMap().containsKey(subscriberId)){
            getSubscriberMap().remove(subscriberId);
        }else{
            throw new InvalidPropertyException("Subscriber with subscriberId " + subscriberId + " do not exists");
        }
    }
}
