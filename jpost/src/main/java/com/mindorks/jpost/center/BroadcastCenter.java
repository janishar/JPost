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

package com.mindorks.jpost.center;

import com.mindorks.jpost.channels.PrivateChannel;
import com.mindorks.jpost.channels.PublicChannel;
import com.mindorks.jpost.core.*;
import com.mindorks.jpost.exceptions.*;
import com.mindorks.jpost.exceptions.IllegalChannelStateException;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by janisharali on 22/09/16.
 */
public class BroadcastCenter extends AbstractBroadcastCenter{

    @Override
    public <T> Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
            ConcurrentHashMap<Integer,WeakReference<Object>>> createPrivateChannel(T owner, Integer channelId) throws AlreadyExistsException {

        if(channelId == null){
            System.out.println("channelId is null");
            return null;
        }
        if (JPost.channelMap.containsKey(channelId)) {
            throw new AlreadyExistsException("Channel with id " + channelId + " already exists");
        }

        try {
            PrivateChannel privateChannel = new PrivateChannel(channelId, ChannelType.PRIVATE, ChannelState.OPEN, new WeakReference<Object>(owner));
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
            ConcurrentHashMap<Integer,WeakReference<Object>>> createPrivateChannel(T owner, Integer channelId, Integer subscriberId) throws AlreadyExistsException{
        if(channelId == null){
            System.out.println("channelId is null");
            return null;
        }
        if (JPost.channelMap.containsKey(channelId)) {
            throw new AlreadyExistsException("Channel with id " + channelId + " already exists");
        }

        try {
            PrivateChannel privateChannel = new PrivateChannel(channelId, ChannelType.PRIVATE, ChannelState.OPEN, new WeakReference<Object>(owner));
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
            ConcurrentHashMap<Integer,WeakReference<Object>>> createPublicChannel(Integer channelId) throws AlreadyExistsException {
        if(channelId == null){
            System.out.println("channelId is null");
            return null;
        }
        if (JPost.channelMap.containsKey(channelId)) {
            throw new AlreadyExistsException("Channel with id " + channelId + " already exists");
        }
        PublicChannel publicChannel = new PublicChannel(channelId, ChannelType.PUBLIC, ChannelState.OPEN);
        JPost.channelMap.put(channelId, publicChannel);
        return publicChannel;
    }
}
