package tes.mindorks.jpost;

import com.mindorks.jpost.JPost;
import com.mindorks.jpost.annotations.SubscribeMsg;
import com.mindorks.jpost.exceptions.*;
import com.mindorks.jpost.exceptions.IllegalChannelStateException;

/**
 * Created by janisharali on 23/09/16.
 */
public class C {

    public C() {
        Thread.currentThread().setName("Thread Main");
        A a = new A();
        A a1 = new A();
        A a2 = new A();
        A a3 = new A();
        A a4 = new A();
        B b = new B();
        try {
            JPost.getBroadcastCenter().createPrivateChannel(this, 2);
        }catch (AlreadyExistsException e){
            e.printStackTrace();
        }

        try {
            JPost.getBroadcastCenter().addSubscriber(this, 2, a);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            JPost.getBroadcastCenter().addSubscriber(this, 2, a1);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            JPost.getBroadcastCenter().addSubscriber(this, 2, a2);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            JPost.getBroadcastCenter().addSubscriber(this, 2, a3);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            JPost.getBroadcastCenter().addSubscriber(this, 2, a4);
        }catch (Exception e){
            e.printStackTrace();
        }

//        JPost.getBroadcastCenter().removeSubscriber(2, this);

        for(int i = 0; i < 1000; i++){
            JPost.getBroadcastCenter().broadcastAsync(this, 2, "Ali calling " + i);
        }
        JPost.shutdown();
    }

    @SubscribeMsg(channelId = 2)
    private void onMsg(String name){
        System.out.println(Thread.currentThread().getName());
        System.out.println("C onMsg " + name);
    }
}
