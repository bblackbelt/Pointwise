package de.pointwise.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.lang.ref.WeakReference;

import de.pointwise.Utils;
import de.pointwise.model.Job;
import de.pointwise.net.FailedExecutionException;
import de.pointwise.net.JobCommand;
import de.pointwise.net.JobExecutorSimpleQueue;

/**
 * Created by emanuele on 19.12.15.
 */
public class ConsumerService extends Service {

    private static class ConsumerThread extends Thread {

        private boolean mExit = false;
        private long mConsumerDelay = Utils.CONSUMER_DELAY;
        private WeakReference<Context> mWeakContext;

        public ConsumerThread(final Context context) {
            setName("ConsumerThread");
            mWeakContext = new WeakReference<>(context);
        }

        @Override
        public void run() {
            while (!mExit) {
                JobCommand command = null;
                Job job = null;
                final JobExecutorSimpleQueue executorSimpleQueue = JobExecutorSimpleQueue.getInstance();
                try {
                    command = executorSimpleQueue.consumeJob();
                    if (command == null) {
                        continue;
                    }
                    job = command.call();
                } catch (FailedExecutionException e) {
                    Log.d(getClass().getSimpleName(), " command " + command + " failed ");
                    JobExecutorSimpleQueue.getInstance().addJob(command);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                broadcastJob(job, executorSimpleQueue.getSize());

                try {
                    Thread.sleep(mConsumerDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcastJob(Job job, int size) {
            final Context context;
            if (job == null) {
                return;
            }
            if ((context = mWeakContext.get()) == null) {
                return;
            }

            Log.i(getClass().getSimpleName(), "Broadcasting " + job);

            Intent intent = new Intent(CONSUMED_ACTION);
            intent.putExtra(JOB_EXTRA, job);
            intent.putExtra(SIZE_EXTRA, size);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

        public void setConsumerDelay(long delay) {
            mConsumerDelay = delay;
        }

        public void exit() {
            mExit = true;
        }
    }

    public static final String CONSUMED_ACTION = "CONSUMED_ACTION";
    public static final String JOB_EXTRA = "JOB_EXTRA";
    public static final String SIZE_EXTRA = "SIZE_EXTRA";
    private ConsumerThread mConsumer;

    @Override
    public void onCreate() {
        super.onCreate();
        mConsumer = new ConsumerThread(this);
        mConsumer.start();
    }

    public  class ConsumerServiceBinder extends Binder {
        public ConsumerService getService() {
            return ConsumerService.this;
        }
    }

    private final IBinder mBinder = new ConsumerServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mConsumer != null) {
            mConsumer.exit();
        }
    }

    /**
     * Delay in seconds
     *
     * @param delay
     */
    public void setConsumerDelay(long delay) {
        if (mConsumer != null) {
            mConsumer.setConsumerDelay(delay);
        }
    }
}
