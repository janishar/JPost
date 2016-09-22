package com.mindorks.jpost.core;

import com.mindorks.jpost.exceptions.InvalidPropertyException;
import com.mindorks.jpost.exceptions.NoSuchChannelException;
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
