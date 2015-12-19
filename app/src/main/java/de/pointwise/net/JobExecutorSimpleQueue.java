package de.pointwise.net;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;

import de.pointwise.model.Job;

/**
 * Created by emanuele on 19.12.15.
 */
public class JobExecutorSimpleQueue {

    private static class JobCommandComparator implements Comparator<JobCommand> {
        @Override
        public int compare(JobCommand jobCommand1, JobCommand jobCommand2) {
            return jobCommand2.getPriority() > jobCommand1.getPriority() ? 1 :
                    jobCommand2.getPriority() == jobCommand1.getPriority() ? 0 : -1;
        }
    }

    public static JobExecutorSimpleQueue sJobExecutor;
    private PriorityBlockingQueue<JobCommand> mJobs;
    private ExecutorService mExecutor;

    private JobExecutorSimpleQueue() {
        mJobs = new PriorityBlockingQueue<>(16, new JobCommandComparator());
    }

    public static synchronized JobExecutorSimpleQueue getInstance() {
        if (sJobExecutor == null) {
            sJobExecutor = new JobExecutorSimpleQueue();
        }
        return sJobExecutor;
    }


    public void addJob(JobCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("JobCommand can't be null");
        }
        mJobs.add(command);
    }

    public void addJob(Job job) {
        if (job == null) {
            throw new IllegalArgumentException("Job can't be null");
        }
        addJob(new JobCommand<>(job));
    }

    public JobCommand consumeJob() {
        return mJobs.poll();
    }

    public int getSize() {
        return mJobs.size();
    }
}
