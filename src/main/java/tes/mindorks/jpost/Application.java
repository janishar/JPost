package tes.mindorks.jpost;


import com.mindorks.androidjpost.JPost;
import com.mindorks.jpost.exceptions.AlreadyExistsException;
import com.mindorks.jpost.exceptions.JPostNotRunningException;
import com.mindorks.jpost.exceptions.NoSuchChannelException;
import tes.mindorks.jpost.message.Message1;
import tes.mindorks.jpost.message.Message2;
import tes.mindorks.jpost.message.Message4;
import tes.mindorks.jpost.subscriber.SubscriberB;
import tes.mindorks.jpost.subscriber.SubscriberD;

import java.lang.ref.WeakReference;
import java.util.Collection;

/**
 * Created by janisharali on 21/09/16.
 */
public class Application {
    public static void main(String[] args) {
        try {
            JPost.getBroadcastCenter().createPublicChannel(ChannelIds.publicChannel1);
        }catch (AlreadyExistsException e){
            e.printStackTrace();
        }
        SubscriberB subscriberB = new SubscriberB();
        SubscriberD subscriberD = new SubscriberD();

        JPost.getBroadcastCenter().broadcast(ChannelIds.publicChannel1, new Message2("Application sending default message"), subscriberB.hashCode());

        subscriberD.sendPrivateMsg();

        JPost.getBroadcastCenter().broadcast(new Message1("Application sending default message"));

        try {
            JPost.getBroadcastCenter().broadcastAsync(new Message1("Application sending default message"));
        }
        catch (JPostNotRunningException e){
            e.printStackTrace();
        }
        JPost.getBroadcastCenter().broadcast(new Message4("Application sending default message"));

        try {
            JPost.getBroadcastCenter().broadcastAsync(ChannelIds.publicChannel1, new Message2("Application sending public message"));
        }catch (JPostNotRunningException e){
            e.printStackTrace();
        }
        JPost.getBroadcastCenter().broadcast(ChannelIds.publicChannel1, new Message4("Application sending public message"));

        try {
            Collection<WeakReference<Object>> collection = JPost.getBroadcastCenter().getAllSubscribersWeakRef(ChannelIds.publicChannel1);
            System.out.println("publicChannel1 subscribers count " + collection.size());
        }catch (NoSuchChannelException e){
            e.printStackTrace();
        }
        JPost.shutdown();
    }
}
