package tes.mindorks.jpost;

import com.mindorks.jpost.JPost;
import com.mindorks.jpost.annotations.SubscribeMsg;
import com.mindorks.jpost.exceptions.AlreadyExistsException;

/**
 * Created by janisharali on 23/09/16.
 */
public class C {

    public C() {
        A a = new A();
        B b = new B();
        try {
//            JPost.getBroadcastCenter().createPrivateChannel(this,2);
            JPost.getBroadcastCenter().createPublicChannel(2);
        }catch (AlreadyExistsException e){
            e.printStackTrace();
        }
        JPost.getBroadcastCenter().addSubscriber(2, a, 1);
        JPost.getBroadcastCenter().addSubscriber(2, b, 2);
        JPost.getBroadcastCenter().broadcast(2, "Ali calling");
        JPost.shutdown();
    }

    @SubscribeMsg(channelId = 1)
    private void onMsg(String name){
        System.out.println("A onMsg " + name);
    }
}
