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
package com.usnoozeulose.app.alarm;

import java.util.ArrayList;
import java.util.List;

import com.usnoozeulose.app.alarm.activities.AlarmActivity;
import com.usnoozeulose.app.alarm.database.Database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmListAdapter extends BaseAdapter {

	private AlarmActivity alarmActivity;
	private List<Alarm> alarms = new ArrayList<Alarm>();

	public static final String ALARM_FIELDS[] = { Database.COLUMN_ALARM_ACTIVE,
			Database.COLUMN_ALARM_TIME, Database.COLUMN_ALARM_DAYS };

	public AlarmListAdapter(AlarmActivity alarmActivity) {
		this.alarmActivity = alarmActivity;
	}

	@Override
	public int getCount() {
		return alarms.size();
	}

	@Override
	public Object getItem(int position) {
		return alarms.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, final ViewGroup viewGroup) {
		if (null == view)
			view = LayoutInflater.from(alarmActivity).inflate(
					R.layout.list_item_alarm, null	);

		Alarm alarm = (Alarm) getItem(position);

		final Switch aSwitch = (Switch) view.findViewById(R.id.alarmOnOffSwitch);
		aSwitch.setChecked(alarm.getAlarmActive());
		aSwitch.setTag(position);
//		if (aSwitch.isChecked()) {
//			setSwitchesFalse(aSwitch, alarm, viewGroup);
//		}
		aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (compoundButton.isPressed()) {
					Alarm alarm = (Alarm) getItem((Integer) aSwitch.getTag());
					alarm.setAlarmActive(b);
					Database.update(alarm);
					alarmActivity.callMathAlarmScheduleService();
					if (aSwitch.isChecked()) {
						Toast.makeText(alarmActivity, alarm.getTimeUntilNextAlarmMessage(), Toast.LENGTH_SHORT).show();
						setSwitchesFalse(aSwitch, alarm, viewGroup);
					}
				}
			}
		});

		TextView alarmTimeView = (TextView) view
				.findViewById(R.id.textView_alarm_time);
		alarmTimeView.setText(alarm.getAlarmTimeString());


		TextView alarmDaysView = (TextView) view
				.findViewById(R.id.textView_alarm_days);
		alarmDaysView.setText(alarm.getRepeatDaysString());


		return view;
	}

	public void setSwitchesFalse(Switch aSwitch, Alarm alarm, ViewGroup viewGroup) {
		for(Alarm a : alarms) {
			if (alarm != a) {
				a.setAlarmActive(false);
			}
//			else {
//				Toast.makeText(alarmActivity, "Same Alarm", Toast.LENGTH_SHORT).show();
//			}
		}
		int childCount = viewGroup.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View v = viewGroup.getChildAt(i);
			Switch mySwitch= (Switch) v.findViewById(R.id.alarmOnOffSwitch);
			if (mySwitch != aSwitch) {
				mySwitch.setChecked(false);
			}
//			else {
//				Toast.makeText(alarmActivity, "Same Switch", Toast.LENGTH_SHORT).show();
//			}
		}
	}

	public List<Alarm> getMathAlarms() {
		return alarms;
	}

	public void setMathAlarms(List<Alarm> alarms) {
		this.alarms = alarms;
	}

}