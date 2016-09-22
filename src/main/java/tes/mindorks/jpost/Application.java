package tes.mindorks.jpost;


import com.mindorks.jpost.JPost;

/**
 * Created by janisharali on 21/09/16.
 */
public class Application {
    public static void main(String[] args) {
        new A();
        new B();

        JPost.getBroadcastCenter().broadcast("Ali calling");
    }
}
