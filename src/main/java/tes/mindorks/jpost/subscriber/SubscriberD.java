package tes.mindorks.jpost.subscriber;

import com.mindorks.jpost.annotations.OnMessage;
import com.mindorks.jpost.center.JPost;
import com.mindorks.jpost.exceptions.AlreadyExistsException;
import com.mindorks.jpost.exceptions.JPostNotRunningException;
import tes.mindorks.jpost.ChannelIds;
import tes.mindorks.jpost.message.Message1;
import tes.mindorks.jpost.message.Message2;
import tes.mindorks.jpost.message.Message3;
import tes.mindorks.jpost.message.Message4;

/**
 * Created by janisharali on 24/09/16.
 */
public class SubscriberD {

    public SubscriberD() {

        SubscriberA subscriberA = new SubscriberA();
        SubscriberC subscriberC = new SubscriberC();

        try {
            JPost.getBroadcastCenter().addSubscriber(this);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            JPost.getBroadcastCenter().createPrivateChannel(this, ChannelIds.privateChannel1);
        }catch (AlreadyExistsException e){
            e.printStackTrace();
        }

        try {
            JPost.getBroadcastCenter().addSubscriber(this, ChannelIds.privateChannel1, subscriberA);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            JPost.getBroadcastCenter().addSubscriber(this, ChannelIds.privateChannel1, subscriberC);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendPrivateMsg(){
        try {
            JPost.getBroadcastCenter().broadcastAsync(this, ChannelIds.privateChannel1, new Message3("SubscriberD sending private message"));
        }catch (JPostNotRunningException e){
            e.printStackTrace();
        }
//        JPost.getBroadcastCenter().broadcast(this, ChannelIds.privateChannel1, new Message4("SubscriberD sending private message for general receiver"));
    }

    @OnMessage
    private void onMessage1(Message1 msg){
        System.out.println("SubscriberD: " + msg.getMsg());
    }

    @OnMessage(channelId = ChannelIds.publicChannel1)
    private void onMessage2(Message2 msg){
        System.out.println("SubscriberD: " + msg.getMsg());
    }

    @OnMessage(channelId = ChannelIds.privateChannel1)
    private void onMessage3(Message3 msg){
        System.out.println("SubscriberD: " + msg.getMsg());
    }

    @OnMessage(isCommonReceiver = true)
    private void onMessage4(Message4 msg){
        System.out.println("SubscriberD: " + msg.getMsg());
    }
}
