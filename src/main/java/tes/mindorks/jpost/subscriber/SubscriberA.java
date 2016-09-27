package tes.mindorks.jpost.subscriber;

import com.mindorks.jpost.annotations.OnMessage;
import com.mindorks.jpost.center.JPost;
import tes.mindorks.jpost.ChannelIds;
import tes.mindorks.jpost.message.Message1;
import tes.mindorks.jpost.message.Message2;
import tes.mindorks.jpost.message.Message3;
import tes.mindorks.jpost.message.Message4;

/**
 * Created by janisharali on 24/09/16.
 */
public class SubscriberA {

    public SubscriberA() {
        try {
            JPost.getBroadcastCenter().addSubscriber(this);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            JPost.getBroadcastCenter().addSubscriber(ChannelIds.publicChannel1, this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @OnMessage
    private void onMessage1(Message1 msg){
        System.out.println("SubscriberA: "+ msg.getMsg());
    }

    @OnMessage(channelId = ChannelIds.publicChannel1)
    private void onMessage2(Message2 msg){
        System.out.println("SubscriberA: " + msg.getMsg());
    }

    @OnMessage(channelId = ChannelIds.privateChannel1)
    private void onMessage3(Message3 msg){
        System.out.println("SubscriberA: " + msg.getMsg());
    }

    @OnMessage(isCommonReceiver = true)
    private void onMessage4(Message4 msg){
        System.out.println("SubscriberA: " + msg.getMsg());
    }
}
