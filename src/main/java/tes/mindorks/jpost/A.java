package tes.mindorks.jpost;

import com.mindorks.jpost.JPost;
import com.mindorks.jpost.annotations.SubscribeMsg;

/**
 * Created by janisharali on 23/09/16.
 */
public class A {

    public A() {
        JPost.getBroadcastCenter().addSubscriber(this);
    }

    @SubscribeMsg(channelId = 2)
    private void onMsg(String name){
        System.out.println("A onMsg " + name);
    }
}
