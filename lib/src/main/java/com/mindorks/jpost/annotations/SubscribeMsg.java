package com.mindorks.jpost.annotations;

import com.mindorks.jpost.core.Channel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by janisharali on 23/09/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubscribeMsg {
    int channelId() default Channel.DEFAULT_CHANNEL_ID;
}
