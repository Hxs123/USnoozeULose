package com.dinhdai.app.alarm.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.dinhdai.app.alarm.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by DINHDAI
 */
public class ClockActivity extends Activity {

    private TextView timeLabel;
    private TextView dateLabel;
    private SimpleDateFormat timeFormat;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        timeLabel = (TextView) findViewById(R.id.timeLabel);
        dateLabel = (TextView) findViewById(R.id.dateLabel);

        timeFormat = new SimpleDateFormat("hh:mm:ss a");
        dateFormat = new SimpleDateFormat("dd, MMM, yyyy");

        Runnable runnable = new CountDownRunner();
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    public void update() {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    Calendar c = Calendar.getInstance();
                    String formattedTime = timeFormat.format(c.getTime());
                    String formattedDate = dateFormat.format(c.getTime());
                    dateLabel.setText(formattedDate);
                    timeLabel.setText(formattedTime);

                } catch (Exception e) {

                }
            }
        });
    }

    class CountDownRunner implements Runnable {
        // @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    update();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }
}