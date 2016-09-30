package tes.mindorks.jpost.subscriber;


import com.mindorks.javajpost.JPost;
import com.mindorks.jpost.core.OnMessage;

import tes.mindorks.jpost.ChannelIds;
import tes.mindorks.jpost.message.Message1;
import tes.mindorks.jpost.message.Message2;

/**
 * Created by janisharali on 24/09/16.
 */
public class SubscriberAsync {

    private char classifier;
    public SubscriberAsync(char classifier) {
        this.classifier = classifier;
        JPost.getBroadcastCenter().addSubscriberAsync(this);
    }

    public SubscriberAsync(char classifier, int channelId) {
        this.classifier = classifier;
        JPost.getBroadcastCenter().addSubscriberAsync(channelId, this);
    }

    public SubscriberAsync(char classifier, int channelId, int subscriberId) {
        this.classifier = classifier;
        JPost.getBroadcastCenter().addSubscriberAsync(channelId, this, subscriberId);
    }

    public <T>SubscriberAsync(T owner, char classifier, int channelId, int subscriberId) {
        this.classifier = classifier;
        try {
            JPost.getBroadcastCenter().addSubscriberAsync(owner, channelId, this, subscriberId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void subscribeAsync(){
        JPost.getBroadcastCenter().addSubscriberAsync(this);
    }


    @OnMessage
    private void onMessage1(Message1 msg){
        System.out.println("SubscriberAsync" + classifier + ": "+ msg.getMsg());
    }

    @OnMessage(channelId = ChannelIds.publicChannel1)
    private void onMessage1Pub(Message1 msg){
        System.out.println("Subscriber" + classifier + ": "+ msg.getMsg());
    }

    @OnMessage(channelId = ChannelIds.privateChannel1)
    private void onMessage1Pri(Message1 msg){
        System.out.println("Subscriber" + classifier + ": "+ msg.getMsg());
    }
}
