package com.mindorks.jpost.core;

/**
 * Created by janisharali on 29/09/16.
 */

public class Utils {
    public static boolean isEqual(Object o1, Object o2) {
        return o1 == o2 || (o1 != null && o1.equals(o2));
    }
}
