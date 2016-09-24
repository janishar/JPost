package com.mindorks.jpost;

import com.mindorks.jpost.annotations.SubscribeMsg;
import com.mindorks.jpost.core.*;
import com.mindorks.jpost.core.ChannelPost;
import com.mindorks.jpost.exceptions.IllegalChannelStateException;
import com.mindorks.jpost.exceptions.NullObjectException;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    @Override
    public <T> void broadcast(T msg, Integer... subscriberIds) throws NullObjectException, IllegalChannelStateException {
        if(super.getChannelState() != ChannelState.OPEN){
            throw new IllegalChannelStateException("Channel with id " + super.getChannelId() + " is closed");
        }
        if(msg == null){
            throw new NullObjectException("message is null");
        }
        ChannelPost<T, Object> post = new ChannelPost<>(msg, getChannelId(), Post.PRIORITY_MEDIUM);
        getPostQueue().put(new WeakReference<ChannelPost>(post));

        while (!getPostQueue().isEmpty()) {
            try {
                WeakReference<ChannelPost> msgRef = getPostQueue().take();
                ChannelPost mspPost = msgRef.get();
                if (mspPost != null && mspPost.getChannelId() != null) {
                    if(mspPost.getChannelId().equals(getChannelId())) {
                        for (Integer subscriberId : subscriberIds) {
                            if(getSubscriberMap().containsKey(subscriberId)) {
                                WeakReference<Object> subscriberRef = getSubscriberMap().get(subscriberId);
                                Object subscriberObj = subscriberRef.get();
                                if (subscriberObj != null) {
                                    for (final Method method : subscriberObj.getClass().getDeclaredMethods()) {
                                        Annotation annotation = method.getAnnotation(SubscribeMsg.class);
                                        if (annotation != null) {
                                            SubscribeMsg subscribeMsg = (SubscribeMsg) annotation;
                                            int channelId = subscribeMsg.channelId();
                                            boolean isCommonReceiver = subscribeMsg.isCommonReceiver();
                                            if (isCommonReceiver || getChannelId().equals(channelId)) {
                                                try {
                                                    boolean methodFound = false;
                                                    for (final Class paramClass : method.getParameterTypes()) {
                                                        if (paramClass.equals(mspPost.getMessage().getClass())) {
                                                            methodFound = true;
                                                            break;
                                                        }
                                                    }
                                                    if (methodFound) {
                                                        method.setAccessible(true);
                                                        method.invoke(subscriberObj, mspPost.getMessage());
                                                    }
                                                } catch (IllegalAccessException e) {
                                                    e.printStackTrace();
                                                } catch (InvocationTargetException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }else{
                        getPostQueue().put(msgRef);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
