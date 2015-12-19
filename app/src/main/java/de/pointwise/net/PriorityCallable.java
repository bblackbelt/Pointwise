package de.pointwise.net;

import java.util.concurrent.Callable;

/**
 * Created by emanuele on 19.12.15.
 */
public interface PriorityCallable<T> extends Callable<T> {
    int getPriority();
}
