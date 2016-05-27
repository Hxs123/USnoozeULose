/* Copyright 2014 Sheldon Neilson www.neilson.co.za
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.usnoozeulose.app.alarm.activities;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.usnoozeulose.app.alarm.Alarm;
import com.usnoozeulose.app.alarm.AlarmListAdapter;
import com.usnoozeulose.app.alarm.BaseActivity;
import com.usnoozeulose.app.alarm.R;
import com.usnoozeulose.app.alarm.database.Database;
import com.usnoozeulose.app.alarm.preferences.AlarmPreferencesActivity;

import java.util.List;

public class AlarmActivity extends BaseActivity implements View.OnClickListener {

    private ListView mathAlarmListView;
    private AlarmListAdapter alarmListAdapter;
    private Button addAlarmButton;
    private int total;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.cancel();

        Bundle bundle = this.getIntent().getExtras();
        if(bundle != null){
            if(bundle.getSerializable("total") == null){
                total = 0;
            }
            else
                total = (Integer) bundle.getSerializable("total");

            if(total != 0){
                //BILL THE PERSON ACTUALLY RIGHT HERE
            }
            final AlarmActivity alarmAlertInstance = this;
            Toast.makeText(alarmAlertInstance, "You were billed: " + total + " dollars!", Toast.LENGTH_LONG).show();
        }


        addAlarmButton = (Button) findViewById(R.id.addAlarmButton);
        addAlarmButton.setOnClickListener(this);

        mathAlarmListView = (ListView) findViewById(android.R.id.list);
        mathAlarmListView.setLongClickable(true);
        mathAlarmListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                final Alarm alarm = (Alarm) alarmListAdapter.getItem(position);
                Builder dialog = new Builder(AlarmActivity.this);
                dialog.setTitle("Delete");
                dialog.setMessage("Delete this alarm?");
                dialog.setPositiveButton("Ok", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Database.init(AlarmActivity.this);
                        Database.deleteEntry(alarm);
                        AlarmActivity.this.callMathAlarmScheduleService();

                        updateAlarmList();
                    }
                });
                dialog.setNegativeButton("Cancel", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

                return true;
            }
        });

        callMathAlarmScheduleService();

        alarmListAdapter = new AlarmListAdapter(this);
        this.mathAlarmListView.setAdapter(alarmListAdapter);
        mathAlarmListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Alarm alarm = (Alarm) alarmListAdapter.getItem(position);
                Intent intent = new Intent(AlarmActivity.this, AlarmPreferencesActivity.class);
                intent.putExtra("alarm", alarm);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        Database.deactivate();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAlarmList();
    }

    public void updateAlarmList() {
        Database.init(AlarmActivity.this);
        final List<Alarm> alarms = Database.getAll();
        alarmListAdapter.setMathAlarms(alarms);

        runOnUiThread(new Runnable() {
            public void run() {
                AlarmActivity.this.alarmListAdapter.notifyDataSetChanged();
                if (alarms.size() > 0) {
                    findViewById(android.R.id.empty).setVisibility(View.INVISIBLE);
                } else {
                    findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent newAlarmIntent = new Intent(this, AlarmPreferencesActivity.class);
        startActivity(newAlarmIntent);
    }
}