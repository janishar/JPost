package com.mindorks.androidjpost.channels;


import android.os.Handler;
import android.os.Looper;

import com.mindorks.androidjpost.annotations.OnUiThread;
import com.mindorks.jpost.annotations.OnMessage;
import com.mindorks.jpost.core.ChannelState;
import com.mindorks.jpost.core.ChannelType;
import com.mindorks.jpost.core.DefaultChannel;
import com.mindorks.jpost.core.Post;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by janisharali on 27/09/16.
 */
public class AndroidDefaultChannel extends DefaultChannel {

    public AndroidDefaultChannel(Integer channelId, ChannelType type, ChannelState state) {
        super(channelId, type, state);
    }

    @Override
    protected <T>boolean deliverMessage(T subscriber, OnMessage msgAnnotation, Method method, Post post){
        int channelId = msgAnnotation.channelId();
        boolean isCommonReceiver = msgAnnotation.isCommonReceiver();
        if (isCommonReceiver || getChannelId().equals(channelId)) {
            try {
                boolean methodFound = false;
                for (final Class paramClass : method.getParameterTypes()) {
                    if (paramClass.equals(post.getMessage().getClass())) {
                        methodFound = true;
                        break;
                    }
                }
                if (methodFound) {
                    Annotation annotation = method.getAnnotation(OnUiThread.class);
                    if (annotation != null) {
                        runOnUiThread(subscriber, method, post);
                    }else{
                        method.setAccessible(true);
                        method.invoke(subscriber, post.getMessage());
                    }
                }
                return true;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    protected <T>void runOnUiThread(final T subscriber,final Method method,final Post post){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    method.setAccessible(true);
                    method.invoke(subscriber, post.getMessage());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
