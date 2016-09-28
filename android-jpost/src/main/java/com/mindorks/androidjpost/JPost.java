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

package com.mindorks.androidjpost;

import com.mindorks.androidjpost.channels.AndroidDefaultChannel;
import com.mindorks.jpost.BroadcastCenter;
import com.mindorks.jpost.core.Broadcast;
import com.mindorks.jpost.core.Channel;
import com.mindorks.jpost.core.ChannelPost;
import com.mindorks.jpost.core.ChannelState;
import com.mindorks.jpost.core.ChannelType;
import com.mindorks.jpost.core.DefaultChannel;

import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by janisharali on 23/09/16.
 */
public class JPost extends com.mindorks.jpost.JPost{

    private static ReentrantLock JPostBootLock = new ReentrantLock();

    protected static ConcurrentHashMap<Integer, Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
            ConcurrentHashMap<Integer,WeakReference<Object>>>> channelMap;

    protected static Broadcast broadcastCenter;
    protected static DefaultChannel channel;
    protected static int threadCount;
    protected static ExecutorService executorService;

    static {
        init();
    }

    protected static void init(){
        threadCount = Runtime.getRuntime().availableProcessors() + 1;
        executorService = Executors.newFixedThreadPool(threadCount);
        channelMap = new ConcurrentHashMap<>(Broadcast.CHANNEL_INITIAL_CAPACITY);
        channel = new AndroidDefaultChannel(
                Channel.DEFAULT_CHANNEL_ID,
                ChannelState.OPEN,
                ChannelType.DEFAULT,
                new PriorityBlockingQueue<>(Channel.MSG_QUEUE_INITIAL_CAPACITY,
                        new Comparator<WeakReference<ChannelPost>>() {
                            @Override
                            public int compare(WeakReference<ChannelPost> o1, WeakReference<ChannelPost> o2) {
                                ChannelPost post1 = o1.get();
                                ChannelPost post2 = o2.get();
                                if(post1 != null || post2 != null){
                                    return post1.getPriority().compareTo(post2.getPriority());
                                }else{
                                    return 0;
                                }
                            }
                        }),
                new ConcurrentHashMap<>(Channel.SUBSCRIBER_INITIAL_CAPACITY));

        channelMap.put(Channel.DEFAULT_CHANNEL_ID, channel);
        broadcastCenter = new AndroidBroadcastCenter(channelMap, executorService);
    }

    public static Broadcast getBroadcastCenter(){
        return broadcastCenter;
    }

    public static void reboot(){
        JPostBootLock.lock();
        executorService = Executors.newFixedThreadPool(threadCount);
        JPostBootLock.unlock();
    }

    public static void shutdown(){
        JPostBootLock.lock();
        executorService.shutdown();
        JPostBootLock.unlock();
    }

    public static void haltAndShutdown(){
        JPostBootLock.lock();
        executorService.shutdownNow();
        JPostBootLock.unlock();
    }
}
