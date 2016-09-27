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

package com.mindorks.jpost.core;

import com.mindorks.jpost.exceptions.IllegalChannelStateException;
import com.mindorks.jpost.exceptions.NullObjectException;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by janisharali on 22/09/16.
 */
public interface CustomChannel<Q extends PriorityBlockingQueue<? extends WeakReference<? extends ChannelPost>>,
        M extends ConcurrentHashMap<? extends Integer,? extends WeakReference<?>>>
        extends Channel<Q, M>{


    void terminateChannel();

    void startChannel();

    void stopChannel();

    /**
     *
     * @param msg
     * @param subscriberIds
     * @param <T>
     * @throws NullObjectException
     * @throws IllegalChannelStateException
     */
    <T>void broadcast(T msg, Integer... subscriberIds) throws NullObjectException, IllegalChannelStateException;
}
