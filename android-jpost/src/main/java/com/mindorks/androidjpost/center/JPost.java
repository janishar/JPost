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

package com.mindorks.androidjpost.center;

import com.mindorks.androidjpost.channels.AndroidDefaultChannel;
import com.mindorks.jpost.core.Broadcast;
import com.mindorks.jpost.core.DefaultChannel;
import com.mindorks.jpost.core.*;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by janisharali on 23/09/16.
 */
public class JPost {

    private static ReentrantLock JPostBootLock = new ReentrantLock();

    protected static ConcurrentHashMap<Integer, Channel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
            ConcurrentHashMap<Integer,WeakReference<Object>>>> channelMap;

    private static Broadcast broadcastCenter;
    private static DefaultChannel channel;
    private static int threadCount = Runtime.getRuntime().availableProcessors() + 1;

    static {
        channelMap = new ConcurrentHashMap<>(Broadcast.CHANNEL_INITIAL_CAPACITY);
        channel = new AndroidDefaultChannel(Channel.DEFAULT_CHANNEL_ID, ChannelType.DEFAULT, ChannelState.OPEN);
        channelMap.put(Channel.DEFAULT_CHANNEL_ID, channel);
        broadcastCenter = new AndroidBroadcastCenter();
    }

    protected static ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

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
