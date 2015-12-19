package de.pointwise;

import android.util.Log;

import java.util.Random;

/**
 * Created by emanuele on 19.12.15.
 */
public class Utils {
    public static final int PRODUCER_DELAY = 500;
    public static final int CONSUMER_DELAY = 1500;
    public static final int MAX_PRIORITY = 10;
    public static final int MIN_PRIORITY = 1;

    private static final char[] sChars;

    private static final Random sRandom = new Random();

    static {
        sChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    }

    public synchronized static String generateString(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(sChars[sRandom.nextInt(sChars.length)]);
        }
        return builder.toString();
    }

    public synchronized static int generatePriority() {
        return Utils.MIN_PRIORITY +
                sRandom.nextInt(Utils.MAX_PRIORITY - Utils.MIN_PRIORITY + 1);
    }

    public static synchronized boolean isFailed() {
        return sRandom.nextInt(10) <= 2;
    }
}
