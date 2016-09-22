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

    void setMessage(T message);
    T getMessage();
    void setPriority(Integer priority);
    Integer getPriority();
    K getSender() throws NullObjectException;
    void setSender(K sender);
    void setReceivers(Object... receivers);
    Object[] getReceiversList();
    void setIsSerialised(boolean isSerialised);
    boolean isSerialised();
    void setSerialisedClassName(String className);
    String getSerialisedClassName();
}
