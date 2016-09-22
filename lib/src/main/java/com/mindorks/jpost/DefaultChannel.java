package com.mindorks.jpost;

import com.mindorks.jpost.annotations.SubscribeMsg;
import com.mindorks.jpost.core.*;
import com.mindorks.jpost.exceptions.*;
import com.mindorks.jpost.exceptions.IllegalStateException;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by janisharali on 22/09/16.
 */
public class DefaultChannel extends AbstractChannel<PriorityBlockingQueue<WeakReference<ChannelPost>>,
        ConcurrentHashMap<Integer,WeakReference<Object>>>{

    public DefaultChannel() {
        super(DEFAULT_CHANNEL_ID, ChannelState.OPEN, ChannelType.DEFAULT,  new PriorityBlockingQueue<>(MSG_QUEUE_INITIAL_CAPACITY,
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
                }),  new ConcurrentHashMap<Integer, WeakReference<Object>>(SUBSCRIBER_INITIAL_CAPACITY));
    }

    @Override
    public void setChannelState(ChannelState state) {
        super.setChannelState(ChannelState.OPEN);
    }

    @Override
    public <T> void broadcast(T msg) throws NullObjectException, IllegalStateException {
        if(super.getChannelState() != ChannelState.OPEN){
            throw new IllegalStateException("Channel is closed");
        }
        if(msg == null){
            throw new NullObjectException("subscriber is null");
        }

        ChannelPost<T, Object> post = new ChannelPost<>(msg, getChannelId(), Post.PRIORITY_MEDIUM);
        getPostQueue().put(new WeakReference<ChannelPost>(post));

        while (!getPostQueue().isEmpty()) {
            try {
                WeakReference<ChannelPost> msgRef = getPostQueue().take();
                ChannelPost mspPost = msgRef.get();
                if (mspPost != null && mspPost.getChannelId() != null) {
                    if(mspPost.getChannelId().equals(getChannelId())) {
                        for (WeakReference<Object> subscriberRef : getSubscriberMap().values()) {
                            Object subscriberObj = subscriberRef.get();
                            if (subscriberObj != null) {
                                for (final Method method : subscriberObj.getClass().getDeclaredMethods()) {
                                    Annotation annotation = method.getAnnotation(SubscribeMsg.class);
                                    if (annotation != null) {
                                        SubscribeMsg subscribeMsg = (SubscribeMsg) annotation;
                                        int channelId = subscribeMsg.channelId();
                                        if (getChannelId().equals(channelId)) {
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
                    }else{
                        getPostQueue().put(msgRef);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public <T> T addSubscriber(T subscriber, Integer subscriberId )
            throws NullObjectException, AlreadyExistsException, IllegalStateException {
        if(super.getChannelState() != ChannelState.OPEN){
            throw new IllegalStateException("Channel with id " + super.getChannelId() + " is closed");
        }
        if(subscriber == null){
            throw new NullObjectException("subscriber is null");
        }
        if(subscriberId == null){
            throw new NullObjectException("subscriberId is null");
        }
        if(super.getSubscriberMap().containsKey(subscriberId)){
            throw new AlreadyExistsException("subscriber with subscriberId " + subscriberId + " already registered");
        }
        super.getSubscriberMap().put(subscriberId, new WeakReference<Object>(subscriber));
        return subscriber;
    }
}
