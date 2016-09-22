package com.mindorks.jpost.core;

import com.mindorks.jpost.exceptions.InvalidPropertyException;
import com.mindorks.jpost.exceptions.NoSuchChannelException;
import com.mindorks.jpost.exceptions.NullObjectException;

import java.util.List;

/**
 * Created by janisharali on 22/09/16.
 */
public interface Post<T, K> {
    <T>T setMessage() throws NullObjectException;
    <T>T getMessage();
    void setPriority(Integer integer) throws NullObjectException, InvalidPropertyException;
    Integer getPriority();
    <K>K getSender() throws NullObjectException;
    <K>K setSender();
    boolean setReceivers(Object... receivers) throws NullObjectException;
    List<Object> getReceiversList();
    void setIsSerialised(boolean isSerialised);
    boolean isSerialised();
    String setSerialisedClassName() throws NullObjectException;
    String getSerialisedClassName();
    Integer getChannelId();
    void setChannelId(Integer channelId) throws NullObjectException, NoSuchChannelException;
}
