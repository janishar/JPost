package tes.mindorks.jpost.subscriber;


import com.mindorks.javajpost.JPost;
import com.mindorks.jpost.core.OnMessage;

import tes.mindorks.jpost.ChannelIds;
import tes.mindorks.jpost.message.Message1;
import tes.mindorks.jpost.message.Message2;

/**
 * Created by janisharali on 24/09/16.
 */
public class Subscriber {
    private char classifier;
    public Subscriber(char classifier) {
        this.classifier = classifier;
        try {
            JPost.getBroadcastCenter().addSubscriber(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Subscriber(char classifier, int channelId) {
        this.classifier = classifier;
        try {
            JPost.getBroadcastCenter().addSubscriber(channelId, this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Subscriber(char classifier, int channelId, int subscriberId) {
        this.classifier = classifier;
        try {
            JPost.getBroadcastCenter().addSubscriber(channelId, this, subscriberId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public <T>Subscriber(T owner, char classifier, int channelId) {
        this.classifier = classifier;
        try {
            JPost.getBroadcastCenter().addSubscriber(owner, channelId, this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public <T>Subscriber(T owner, char classifier, int channelId, int subscriberId) {
        this.classifier = classifier;
        try {
            JPost.getBroadcastCenter().addSubscriber(owner, channelId, this, subscriberId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @OnMessage
    private void onMessage1(Message1 msg){
        System.out.println("Subscriber" + classifier + ": "+ msg.getMsg());
    }

    @OnMessage(channelId = ChannelIds.publicChannel1)
    private void onMessage1Pub(Message1 msg){
        System.out.println("Subscriber" + classifier + ": "+ msg.getMsg());
    }

    @OnMessage(channelId = ChannelIds.privateChannel1)
    private void onMessage1Pri(Message1 msg){
        System.out.println("Subscriber" + classifier + ": "+ msg.getMsg());
    }

    @OnMessage(isCommonReceiver = true)
    private void onMessage2(Message2 msg){
        System.out.println("Subscriber" + classifier + ": "+ msg.getMsg());
    }
}
