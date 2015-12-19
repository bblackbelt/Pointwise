package de.pointwise.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import de.pointwise.Utils;
import de.pointwise.model.Job;
import de.pointwise.net.JobExecutorSimpleQueue;

/**
 * Created by emanuele on 19.12.15.
 */
public class ProducerService extends Service {

    private static class ProducerThread extends Thread {

        private boolean mExit = false;
        private long mProducerDelay = Utils.PRODUCER_DELAY;

        public ProducerThread() {
            setName("ConsumerThread");
        }

        @Override
        public void run() {
            while (!mExit) {
                final int priority = Utils.generatePriority();
                final String token = Utils.generateString(4);
                JobExecutorSimpleQueue.getInstance().addJob(new Job(token, priority));
                try {
                    Thread.sleep(mProducerDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setProducerDelay(long delay) {
            mProducerDelay = delay;
        }

        public void exit() {
            mExit = true;
        }
    }

    private ProducerThread mProducer;

    @Override
    public void onCreate() {
        super.onCreate();
        mProducer = new ProducerThread();
        mProducer.start();
    }

    public class ProducerServiceBinder extends Binder {
        public ProducerService getService() {
            return ProducerService.this;
        }
    }

    private final IBinder mBinder = new ProducerServiceBinder();

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
        if (mProducer != null) {
            mProducer.exit();
        }
    }

    /**
     * Delay in seconds
     *
     * @param delay
     */
    public void setProducerDelay(long delay) {
        if (mProducer != null) {
            mProducer.setProducerDelay(delay);
        }
    }
}
