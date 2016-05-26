package com.dinhdai.app.alarm.activities;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.dinhdai.app.alarm.R;

public class MainActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabHost tabHost = getTabHost();


        TabSpec clock = tabHost.newTabSpec("d");
        clock.setIndicator("Clock");
        Intent clockIntent = new Intent(this, ClockActivity.class);
        clock.setContent(clockIntent);


        TabSpec alarm = tabHost.newTabSpec("b");
        alarm.setIndicator("Alarm Clock");
        Intent alarmIntent = new Intent(this, AlarmActivity.class);
        alarm.setContent(alarmIntent);


        TabSpec stopwatch = tabHost.newTabSpec("s");
        stopwatch.setIndicator("Stopwatch");
        Intent stopwatchIntent = new Intent(this, StopwatchActivity.class);
        stopwatch.setContent(stopwatchIntent);


        tabHost.addTab(clock);
        tabHost.addTab(alarm);
        tabHost.addTab(stopwatch);

    }
}
