package de.pointwise.net;

import de.pointwise.Utils;
import de.pointwise.model.Job;

/**
 * Created by emanuele on 19.12.15.
 */
public class JobCommand<T extends Job> implements PriorityCallable<T> {

    final T mJob;

    public JobCommand(final T job) {
        if (job == null) {
            throw new IllegalArgumentException("job can't be null");
        }
        mJob = job;
    }

    @Override
    public int getPriority() {
        return mJob.mPriority;
    }

    @Override
    public T call() throws Exception {
        if (Utils.isFailed()) {
            throw new FailedExecutionException("Task failed");
        }
        return mJob;
    }

    @Override
    public String toString() {
        if (mJob != null) {
            return mJob.toString();
        }
        return super.toString();
    }
}
