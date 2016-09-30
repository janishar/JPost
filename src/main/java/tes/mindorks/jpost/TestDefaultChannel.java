package tes.mindorks.jpost;

import com.mindorks.javajpost.JPost;
import com.mindorks.jpost.exceptions.JPostNotRunningException;

import tes.mindorks.jpost.message.Message1;
import tes.mindorks.jpost.message.Message2;
import tes.mindorks.jpost.subscriber.Subscriber;
import tes.mindorks.jpost.subscriber.SubscriberAsync;


/**
 * Created by janisharali on 30/09/16.
 */

public class TestDefaultChannel {

    static Subscriber subscriberA;
    static Subscriber subscriberB;
    static SubscriberAsync subscriberAsyncA;
    static SubscriberAsync subscriberAsyncB;

    public static void test(){
        testSubscriberSubscribeSync();
        testSubscriberBroadcastSync();
        testSubscriberBroadcastAsync();
        testShutdownRestartSync();
        testShutdownRestartAsync();
        testSubscriberSubscribeAsync();
        testRemoveSubscriber();
    }

    private static void testSubscriberSubscribeSync(){
        System.out.println("testSubscriberSubscribeSync");
        subscriberA =  new Subscriber('A');
        subscriberB = new Subscriber('B');
    }

    private static void testSubscriberBroadcastSync(){
        System.out.println("testSubscriberBroadcastSync");
        Message1 message1 = new Message1("Default message subscriber async 1");
        Message2 message2 = new Message2("Default message subscriber async 2");
        JPost.getBroadcastCenter().broadcast(message1);
        JPost.getBroadcastCenter().broadcast(message2);
    }

    private static void testSubscriberBroadcastAsync(){

        System.out.println("testSubscriberBroadcastAsync");
        Message1 message1 = new Message1("Default message 1 async");
        Message2 message2 = new Message2("Default message 2 async");
        try {
            JPost.getBroadcastCenter().broadcastAsync(message1);
        }catch (JPostNotRunningException e){
            e.printStackTrace();
        }
        try {
            JPost.getBroadcastCenter().broadcastAsync(message2);
        }catch (JPostNotRunningException e){
            e.printStackTrace();
        }
    }

    private static void testShutdownRestartSync(){
        System.out.println("testShutdownRestartSync");
        JPost.shutdown();
        JPost.getBroadcastCenter().broadcast(new Message1("Default message sync 1 after shutdown"));
        JPost.getBroadcastCenter().broadcast(new Message2("Default message sync 2 after shutdown"));
        JPost.reboot();
        JPost.getBroadcastCenter().broadcast(new Message1("Default message sync 1 after reboot"));
        JPost.getBroadcastCenter().broadcast(new Message2("Default message sync 2 after reboot"));
        JPost.reboot();
    }

    private static void testShutdownRestartAsync(){
        System.out.println("testShutdownRestartAsync");
        JPost.shutdown();
        try {
            JPost.getBroadcastCenter().broadcastAsync(new Message1("Default message async 1 after shutdown"));
        }catch (JPostNotRunningException e){
            e.printStackTrace();
        }

        try {
            JPost.getBroadcastCenter().broadcastAsync(new Message2("Default message async 2 after shutdown"));
        }catch (JPostNotRunningException e){
            e.printStackTrace();
        }

        JPost.reboot();

        try {
            JPost.getBroadcastCenter().broadcastAsync(new Message1("Default message async 1 after reboot"));
        }catch (JPostNotRunningException e){
            e.printStackTrace();
        }

        try {
            JPost.getBroadcastCenter().broadcastAsync(new Message2("Default message async 2 after reboot"));
        }catch (JPostNotRunningException e){
            e.printStackTrace();
        }
    }

    private static void testSubscriberSubscribeAsync(){
        System.out.println("testSubscriberSubscribeAsync");
        subscriberAsyncA =  new SubscriberAsync('A');
        subscriberAsyncB = new SubscriberAsync('B');
    }

    private static void testRemoveSubscriber(){
        System.out.println("removeSubscriberTest");
        try {
            JPost.getBroadcastCenter().removeSubscriber(subscriberA);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            JPost.getBroadcastCenter().removeSubscriber(subscriberB);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            JPost.getBroadcastCenter().removeSubscriber(subscriberAsyncA);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            JPost.getBroadcastCenter().broadcastAsync(new Message1("Default message async 1 after remove subscriber"));
        }catch (JPostNotRunningException e){
            e.printStackTrace();
        }

        try {
            JPost.getBroadcastCenter().broadcastAsync(new Message2("Default message async 2 after remove subscriber"));
        }catch (JPostNotRunningException e){
            e.printStackTrace();
        }
    }
}
