package com.mindorks.jpost;

import com.mindorks.jpost.core.Post;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by janisharali on 26/09/16.
 */
public class Utils {

    protected static <T>boolean runOnUiThreadAndroid(final Method method, final T subscriber, final Post post){
        try {
            Method runOnUiMethod = subscriber.getClass().getMethod("runOnUiThread", Runnable.class);
            if(runOnUiMethod != null) {
                runOnUiMethod.setAccessible(true);
                runOnUiMethod.invoke(subscriber, new Runnable() {
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
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
