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

import com.mindorks.jpost.exceptions.NullObjectException;


/**
 * Created by janisharali on 22/09/16.
 */
public interface Post<T, K> {

    int PRIORITY_LOW = 1;
    int PRIORITY_MEDIUM = 2;
    int PRIORITY_HIGH = 3;

    /**
     *
     * @param message
     */
    void setMessage(T message);

    /**
     *
     * @return
     */
    T getMessage();

    /**
     *
     * @param priority
     */
    void setPriority(Integer priority);

    /**
     *
     * @return
     */
    Integer getPriority();

    /**
     *
     * @return
     * @throws NullObjectException
     */
    K getSender() throws NullObjectException;

    /**
     *
     * @param sender
     */
    void setSender(K sender);

    /**
     *
     * @param receivers
     */
    void setReceivers(Object... receivers);

    /**
     *
     * @return
     */
    Object[] getReceiversList();

    /**
     *
     * @param isSerialised
     */
    void setIsSerialised(boolean isSerialised);

    /**
     *
     * @return
     */
    boolean isSerialised();

    /**
     *
     * @param className
     */
    void setSerialisedClassName(String className);

    /**
     *
     * @return
     */
    String getSerialisedClassName();
}
