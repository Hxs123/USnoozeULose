package com.usnoozeulose.app.alarm.activities;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TabHost;

import com.usnoozeulose.app.alarm.R;

public class MainActivity extends TabActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences sharedPrefs = getLocalActivityManager().getCurrentActivity().
                getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE);

        String userId = sharedPrefs.getString("userId", null);

        // userid does not exist
        if (userId == null) {

            // need to get login page

            SharedPreferences.Editor sharedPrefsEditor = sharedPrefs.edit();
            sharedPrefsEditor.putString("userId", "");
            sharedPrefsEditor.apply();

        }

        /* Alarm */

        TabHost tabHost = getTabHost();

        TabHost.TabSpec alarm = tabHost.newTabSpec("b");
        alarm.setIndicator("Alarm List");
        Intent alarmIntent = new Intent(this, AlarmActivity.class);
        alarm.setContent(alarmIntent);

        tabHost.addTab(alarm);

    }


}
