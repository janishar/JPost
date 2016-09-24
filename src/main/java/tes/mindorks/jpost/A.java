package tes.mindorks.jpost;

import com.mindorks.jpost.JPost;
import com.mindorks.jpost.annotations.SubscribeMsg;

/**
 * Created by janisharali on 23/09/16.
 */
public class A {

    public A() {
        Thread.currentThread().setName("Thread Main");
        try {
            JPost.getBroadcastCenter().addSubscriber(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @SubscribeMsg(channelId = 2)
    private void onMsg(String name){
        System.out.println(Thread.currentThread().getName());
        System.out.println("A onMsg " + name);
    }
}
