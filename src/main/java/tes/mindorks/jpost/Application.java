package tes.mindorks.jpost;

import com.mindorks.javajpost.JPost;

/**
 * Created by janisharali on 30/09/16.
 */

public class Application {

    public static void main(String[] args){
        System.out.println(".......................DEFAULT CHANNEL TEST START..............................");
        TestDefaultChannel.test();
        try {
            Thread.sleep(3000);
            System.out.println(".......................DEFAULT CHANNEL TEST END..............................");
            JPost.shutdown();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        System.out.println(".......................PUBLIC CHANNEL TEST START..............................");
        TestPublicChannel.test();
        try {
            Thread.sleep(3000);
            System.out.println(".......................PUBLIC CHANNEL TEST END..............................");
            JPost.shutdown();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        System.out.println(".......................PRIVATE CHANNEL TEST START..............................");
        TestPrivateChannel.test();
        try {
            Thread.sleep(3000);
            System.out.println(".......................PRIVATE CHANNEL TEST END..............................");
            JPost.shutdown();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
