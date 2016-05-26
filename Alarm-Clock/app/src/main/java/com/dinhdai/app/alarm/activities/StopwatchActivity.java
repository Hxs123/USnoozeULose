package com.dinhdai.app.alarm.activities;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dinhdai.app.alarm.R;

import java.util.Calendar;

public class StopwatchActivity extends Activity implements View.OnClickListener {
    private TextView stopWatchTextView;
    private Button startButton;
    private Button stopButton;
    private Button resetButton;
    private Thread thread;

    int seconds;
    int minutes;
    int percentSeconds;

    boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        stopWatchTextView = (TextView) findViewById(R.id.stopWatchTextView);
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        resetButton = (Button) findViewById(R.id.resetButton);

        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

        seconds = 0;
        minutes = 0;
        percentSeconds = 0;
    }

    public void update() {
        runOnUiThread(new Runnable() {
            public void run() {
                percentSeconds ++;
                if (percentSeconds == 60) {
                    percentSeconds = 0;
                    seconds++;
                }
                if (seconds == 60) {
                    minutes++;
                    seconds = 0;
                }
                updateUI();
            }
        });
    }

    class CountDownRunner implements Runnable {
        // @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    update();
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startButton:
                if (running) {
                    return;
                }
                Runnable runnable = new CountDownRunner();
                thread = new Thread(runnable);
                thread.start();
                running = true;
                break;

            case R.id.stopButton:
                thread.interrupt();
                running = false;
                break;

            case R.id.resetButton:
                thread.interrupt();
                percentSeconds = 0;
                seconds = 0;
                minutes = 0;
                stopWatchTextView.setText("00:00:00");
                running = false;
                break;
        }
    }

    private void updateUI() {
        stopWatchTextView.setText(String.format("%02d", minutes) + ":"
                + String.format("%02d", seconds) + ":"
                + String.format("%02d", percentSeconds));
    }
}
