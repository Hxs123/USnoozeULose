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
package com.usnoozeulose.app.alarm.alert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.usnoozeulose.app.alarm.Alarm;
import com.usnoozeulose.app.alarm.R;

import java.util.concurrent.TimeUnit;

public class AlarmAlertActivity extends Activity implements OnClickListener {
	private int total;
	private final int SNOOZE_LENGTH = 300000; //5000;
	private final int MAX_SNOOZES = 3;
	private int snoozeCount;
	private Alarm alarm;
	private MediaPlayer mediaPlayer;

	private Vibrator vibrator;

	private boolean alarmActive;
    private Button turnAlarmOffButton;
	private Button snoozeButton;
    private TextView alarmLabelTextView;
    private ImageView alarmIconImageView;
	private CountDownTimer countDownTimer;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		setContentView(R.layout.activity_alarm_alert);

		snoozeCount = 0;

		Bundle bundle = this.getIntent().getExtras();
		alarm = (Alarm) bundle.getSerializable("alarm");
		if(bundle.getSerializable("total") == null){
			total = 0;
		}
		else
			total = (Integer) bundle.getSerializable("total");

		this.setTitle(alarm.getAlarmName());

        turnAlarmOffButton = (Button) findViewById(R.id.turnAlarmOffButton);
        turnAlarmOffButton.setOnClickListener(this);

		snoozeButton = (Button) findViewById(R.id.snoozeButton);
		snoozeButton.setOnClickListener(this);

        alarmIconImageView = (ImageView) findViewById(R.id.alarmIconImageView);
        Animation shakeAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        alarmIconImageView.setAnimation(shakeAnimation);

        alarmLabelTextView = (TextView) findViewById(R.id.alarmLabelTextView);
        alarmLabelTextView.setText(alarm.getAlarmName());

		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);

		PhoneStateListener phoneStateListener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				switch (state) {
				case TelephonyManager.CALL_STATE_RINGING:
					Log.d(getClass().getSimpleName(), "Incoming call: "
							+ incomingNumber);
					try {
						mediaPlayer.pause();
					} catch (IllegalStateException e) {

					}
					break;
				case TelephonyManager.CALL_STATE_IDLE:
					Log.d(getClass().getSimpleName(), "Call State Idle");
					try {
						mediaPlayer.start();
					} catch (IllegalStateException e) {

					}
					break;
				}
				super.onCallStateChanged(state, incomingNumber);
			}
		};

		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);

		startAlarm();
	}

	@Override
	protected void onResume() {
		super.onResume();
		alarmActive = true;
	}

	private void startAlarm() {
		final AlarmAlertActivity alarmAlertInstance = this;
		Toast.makeText(alarmAlertInstance, "You owe: " + total + " dollars!", Toast.LENGTH_LONG).show();
		countDownTimer = new CountDownTimer(15000, 1000) {
			@Override
			public void onTick(long l) {
				int minutes = (int) TimeUnit.MILLISECONDS.toMinutes( l);
				int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(l)-
						(int) TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l));

				alarmLabelTextView.setText("Time Left: "+String.format("%d min, %d sec", minutes, seconds));
			}

			/* Handles the end of the snooze cycle.
            * Displays a message saying that snooze time is over and displays the buttons again
            * Starts the alarm again unless the snooze count is greater than MAX_SNOOZES. */
			public void onFinish() {
				mediaPlayer.stop();
				mediaPlayer.release();
				vibrator.cancel();
				Intent intent = new Intent(getApplicationContext(), BallActivity.class);
				intent.putExtra("alarm", alarm);
				intent.putExtra("total", total);
				startActivity(intent);
			}
		};
		countDownTimer.start();

		if (alarm.getAlarmTonePath() != "") {
			mediaPlayer = new MediaPlayer();
			if (alarm.getVibrate()) {
				vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
				long[] pattern = { 1000, 200, 200, 200 };
				vibrator.vibrate(pattern, 0);
			}
			try {
				mediaPlayer.setVolume(1.0f, 1.0f);
				mediaPlayer.setDataSource(this,
						Uri.parse(alarm.getAlarmTonePath()));
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mediaPlayer.setLooping(true);
				mediaPlayer.prepare();
				mediaPlayer.start();

			} catch (Exception e) {
				mediaPlayer.release();
				alarmActive = false;
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		if (!alarmActive)
			super.onBackPressed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		StaticWakeLock.lockOff(this);
	}

	@Override
	protected void onDestroy() {
		try {
			if (vibrator != null)
				vibrator.cancel();
		} catch (Exception e) {

		}
		try {
			mediaPlayer.stop();
		} catch (Exception e) {

		}
		try {
			mediaPlayer.release();
		} catch (Exception e) {

		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		countDownTimer.cancel();
		if (!alarmActive)
			return;
		String button = (String) v.getTag();
		v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        if (v.getId() == R.id.turnAlarmOffButton) {
            alarmActive = false;
            if (vibrator != null)
                vibrator.cancel();
            try {
                mediaPlayer.stop();
            } catch (IllegalStateException ise) {

            }
            try {
                mediaPlayer.release();
            } catch (Exception e) {

            }
			if (countDownTimer != null) {
				countDownTimer.cancel();
			}
			vibrator.cancel();
			Intent intent = new Intent(getApplicationContext(), BallActivity.class);
			intent.putExtra("alarm", alarm);
			intent.putExtra("total", total);
			startActivity(intent);
            this.finish();
        }
		/*
		 *  Handles the snoozeButton press.
		 *	Creates a countdowntimer that will reactivate the alarm.
		 */
		if (v.getId() == R.id.snoozeButton) {
			countDownTimer.cancel();
			/* To-Do Snooze Logic*/
			Toast.makeText(this, "Snoozing and Losing!!!", Toast.LENGTH_SHORT).show();
			final AlarmAlertActivity alarmAlertInstance = this;
			snoozeCount++;

			/* TODO Insert Code to call method to take away 5 bucks and donate to charity. */

			/* Make the two buttons invisible */
			final View snoozeView = findViewById(R.id.snoozeButton);
			snoozeView.setVisibility(View.GONE);
//			final View alarmView = findViewById(R.id.turnAlarmOffButton);
//			alarmView.setVisibility(View.GONE);

			vibrator.cancel();

			/* Creates a timer for snoozing and then starts it.*/
			countDownTimer = new CountDownTimer(15000, 1000) {
				@Override
				public void onTick(long l) {
					int minutes = (int) TimeUnit.MILLISECONDS.toMinutes( l);
					int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(l)-
							(int) TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l));

					alarmLabelTextView.setText("Time Left: "+String.format("%d min, %d sec", minutes, seconds));
				}

				/* Handles the end of the snooze cycle.
				* Displays a message saying that snooze time is over and displays the buttons again
				* Starts the alarm again unless the snooze count is greater than MAX_SNOOZES. */
				public void onFinish() {
					//Toast.makeText(alarmAlertInstance, "BILLED YOU BAD!!!", Toast.LENGTH_LONG).show();
					total += 5;
					//Toast.makeText(alarmAlertInstance, "Snooze Time is Over!!!", Toast.LENGTH_LONG).show();
					startAlarm();
					snoozeView.setVisibility(View.VISIBLE);
//					alarmView.setVisibility(View.VISIBLE);
				}
			};
			countDownTimer.start();

			/* Turns off the vibration and the alarm.*/
			if (vibrator != null)
				vibrator.cancel();
			try {
				mediaPlayer.stop();
			} catch (IllegalStateException ise) {

			}
			try {
				mediaPlayer.release();
			} catch (Exception e) {

			}
		}
	}

	/* Accesses the user's bank account and sends a certain
	 * amount of money to another bank account
	 */
	public void send5BucksToCharity() {


	}
}
