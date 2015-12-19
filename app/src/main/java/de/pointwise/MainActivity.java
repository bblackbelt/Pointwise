package de.pointwise;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import de.pointwise.model.Job;
import de.pointwise.service.ConsumerService;
import de.pointwise.service.ProducerService;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver mConsumerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Job job = intent.getParcelableExtra(ConsumerService.JOB_EXTRA);
            ((TextView) findViewById(R.id.job_name)).setText(getString(R.string.last, job.mMessage));
            ((TextView) findViewById(R.id.queue_size)).setText(getString(R.string.size, intent.getIntExtra(ConsumerService.SIZE_EXTRA, 0)));
        }
    };

    private ServiceConnection mConsumerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ConsumerService.ConsumerServiceBinder binder = (ConsumerService.ConsumerServiceBinder) iBinder;
            mConsumer = binder.getService();
            mConsumerBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mConsumerBound = false;
            mConsumer = null;
        }
    };

    private ConsumerService mConsumer;
    private ProducerService mProducer;
    private ServiceConnection mProducerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ProducerService.ProducerServiceBinder binder = (ProducerService.ProducerServiceBinder) iBinder;
            mProducer = binder.getService();
            mProducerBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mProducerBound = false;
            mProducer = null;
        }
    };

    private boolean mConsumerBound;
    private boolean mProducerBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TextView) findViewById(R.id.input_consumer_delay)).setText(String.valueOf(Utils.CONSUMER_DELAY));
        ((TextView) findViewById(R.id.input_producer_delay)).setText(String.valueOf(Utils.PRODUCER_DELAY));
        findViewById(R.id.set_consumer_delay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mConsumerBound) {
                    int value = getValueFromInput(((TextView) findViewById(R.id.input_consumer_delay)));
                    if (value > 0) {
                        mConsumer.setConsumerDelay(value);
                    }
                }
            }
        });

        findViewById(R.id.set_producer_delay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mProducerBound) {
                    int value = getValueFromInput(((TextView) findViewById(R.id.input_producer_delay)));
                    if (value > 0) {
                        mProducer.setProducerDelay(value);
                    }
                }
            }
        });
    }

    private int getValueFromInput(TextView textView) {
        if (textView == null) {
            return -1;
        }
        String value = textView.getText().toString();
        int num = -1;
        try {
            num = Integer.valueOf(value);
        } catch (Exception e) {
            Snackbar.make(findViewById(android.R.id.content), R.string.no_num_error, Snackbar.LENGTH_LONG).
                    show();
        }
        return num;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, ProducerService.class);
        bindService(intent, mProducerConnection, Context.BIND_AUTO_CREATE);

        intent = new Intent(this, ConsumerService.class);
        bindService(intent, mConsumerConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).
                registerReceiver(mConsumerReceiver, new IntentFilter(ConsumerService.CONSUMED_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mConsumerBound) {
            unbindService(mConsumerConnection);
        }
        if (mProducerBound) {
            unbindService(mProducerConnection);
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mConsumerReceiver);
    }

}
