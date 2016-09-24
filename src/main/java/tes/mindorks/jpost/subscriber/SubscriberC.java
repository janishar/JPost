package tes.mindorks.jpost.subscriber;

import com.mindorks.jpost.JPost;
import com.mindorks.jpost.annotations.OnMessage;
import tes.mindorks.jpost.ChannelIds;
import tes.mindorks.jpost.message.Message1;
import tes.mindorks.jpost.message.Message2;
import tes.mindorks.jpost.message.Message3;
import tes.mindorks.jpost.message.Message4;

/**
 * Created by janisharali on 24/09/16.
 */
public class SubscriberC {

    public SubscriberC() {
        try {
            JPost.getBroadcastCenter().addSubscriber(ChannelIds.publicChannel1, this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @OnMessage(channelId = ChannelIds.publicChannel1)
    private void onMessage2(Message2 msg){
        System.out.println("SubscriberC: " + msg.getMsg());
    }

    @OnMessage(channelId = ChannelIds.privateChannel1)
    private void onMessage3(Message3 msg){
        System.out.println("SubscriberC: " + msg.getMsg());
    }

    @OnMessage(isCommonReceiver = true)
    private void onMessage4(Message4 msg){
        System.out.println("SubscriberC: " + msg.getMsg());
    }
}
