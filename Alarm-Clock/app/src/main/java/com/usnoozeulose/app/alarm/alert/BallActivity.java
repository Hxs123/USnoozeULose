package com.usnoozeulose.app.alarm.alert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.usnoozeulose.app.alarm.Alarm;
import com.usnoozeulose.app.alarm.Ball;
import com.usnoozeulose.app.alarm.R;
import com.usnoozeulose.app.alarm.activities.AlarmActivity;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BallActivity extends Activity implements SensorEventListener {
    int minutes;
    int seconds;
    boolean draw = true;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdate;

    boolean complete;
    boolean stop = false;

    boolean level1 = false;
    boolean level2 = false;
    boolean level3 = false;

    boolean restart = true;

    boolean buttonActivate = false;

    String time = " 0";
    CountDownTimer timer;
    boolean fail;

    private Paint paint = new Paint();

    AnimatedView animatedView = null;

    Ball b1 = new Ball(0, 25);
    Ball b2 = new Ball(0, 35);
    Ball b3 = new Ball(0, 45);
    Ball b4 = new Ball(0, 55);
    Ball b5 = new Ball(0, 65);

    boolean isPop = false;

    Ball[] ballArray = new Ball[]{b1, b2, b3, b4, b5};

    ShapeDrawable ball1 = new ShapeDrawable();
    ShapeDrawable ball2 = new ShapeDrawable();
    ShapeDrawable ball3 = new ShapeDrawable();
    ShapeDrawable ball4 = new ShapeDrawable();
    ShapeDrawable ball5 = new ShapeDrawable();

    private int holeX;
    private int holeY;
    private int holeS;
    ShapeDrawable hole = new ShapeDrawable();

    //public static int x;
    //public static int y;
    public static int phoneHeight;
    public static int phoneWidth;
    boolean actionUpFlag;

    int[] androidColors;

    private Button start;
    private TextView title;
    private View.OnClickListener onClickListener;

    private Alarm alarm;

    private Vibrator vibrator;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] pattern = { 1000, 200, 200, 200 };
        vibrator.vibrate(pattern, 0);

        Bundle bundle = this.getIntent().getExtras();
        alarm = (Alarm) bundle.getSerializable("alarm");

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        phoneHeight = displaymetrics.heightPixels;
        phoneWidth = displaymetrics.widthPixels;

        holeX = (int)(Math.random()*1100 + 150);
        holeY = (int)(Math.random()*2000 + 250);
        while(holeY <= phoneHeight/3 + 300 && holeY >= phoneHeight/3 - 250){
            holeY = (int)(Math.random()*2000 + 250);
        }
        holeY = (int)(Math.random()*180 + 250);
        holeS = 110 * 2;

        androidColors = getResources().getIntArray(R.array.androidcolors);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lastUpdate = System.currentTimeMillis();

        animatedView = new AnimatedView(BallActivity.this);
        setContentView(animatedView);

        timer = new CountDownTimer(20000, 1000) {
            public void onTick(long millisUntilFinished) {
                minutes = (int) TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished);
                seconds = (int) TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)-
                        (int) TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));

                time = ("Time Left: "+String.format("%d min, %d sec", minutes, seconds));
            }

            public void onFinish() {
                minutes = 0;
                seconds = 0;
                time =("TOO LATE YOU MUST'VE SNOOZED");
            }
        }.start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            int inHoleCounter = 0;
            for(int i = 0; i < ballArray.length; i++){
                int x = ballArray[i].getX();
                int y = ballArray[i].getY();
                int xChange = (int)(event.values[0] * ballArray[i].getSpeed());
                int yChange = (int)Math.round(event.values[1] * ballArray[i].getSpeed());

                int dia = ballArray[i].getRadius() * 2;
                ballArray[i].setInHole(holeX, holeY, holeS);
                if(x >= 0 && x <= phoneWidth - dia && !ballArray[i].getInHole()) {
                    if (i % 2 == 1) {
                        ballArray[i].setX(x + xChange);
                    } else {
                        ballArray[i].setX(x - xChange);
                    }
                    if(ballArray[i].getX() < 0){
                        ballArray[i].setX(0);
                    }
                    if(ballArray[i].getX() >= phoneWidth - dia){
                        ballArray[i].setX(phoneWidth - dia);
                    }
                }

                if(y >= 0 && y <= phoneHeight - dia - 300 && !ballArray[i].getInHole()){
                    if(i % 2 == 1){
                        ballArray[i].setY(y - yChange);
                    }
                    else {
                        ballArray[i].setY(y + yChange);
                    }
                    if(ballArray[i].getY() < 0){
                        ballArray[i].setY(0);
                    }
                    if(ballArray[i].getY() >= phoneHeight - dia - 300){
                        ballArray[i].setY(phoneHeight - dia - 300);
                    }
                }
            }
        }
    }

    public class AnimatedView extends ImageView {

        static final int width = 175;
        static final int height = 175;

        public AnimatedView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub


            //BALL IS CREATED LMAO
            ball1 = new ShapeDrawable(new OvalShape());
            ball2 = new ShapeDrawable(new OvalShape());
            ball3 = new ShapeDrawable(new OvalShape());
            ball4 = new ShapeDrawable(new OvalShape());
            ball5 = new ShapeDrawable(new OvalShape());

            ball1.getPaint().setColor(androidColors[new Random().nextInt(androidColors.length)]);
            ball2.getPaint().setColor(androidColors[new Random().nextInt(androidColors.length)]);
            ball3.getPaint().setColor(androidColors[new Random().nextInt(androidColors.length)]);
            ball4.getPaint().setColor(androidColors[new Random().nextInt(androidColors.length)]);
            ball5.getPaint().setColor(androidColors[new Random().nextInt(androidColors.length)]);

            hole = new ShapeDrawable(new RectShape());
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if(draw && restart){
                ball1.setBounds(b1.getX(), b1.getY(), b1.getX() + 2 * b1.getRadius(), b1.getY() + 2 * b1.getRadius());
                ball2.setBounds(b2.getX(), b2.getY(), b2.getX() + 2 * b2.getRadius(), b2.getY() + 2 * b2.getRadius());
                ball3.setBounds(b3.getX(), b3.getY(), b3.getX() + 2 * b3.getRadius(), b3.getY() + 2 * b3.getRadius());
                ball4.setBounds(b4.getX(), b4.getY(), b4.getX() + 2 * b4.getRadius(), b4.getY() + 2 * b4.getRadius());
                ball5.setBounds(b5.getX(), b5.getY(), b5.getX() + 2 * b5.getRadius(), b5.getY() + 2 * b5.getRadius());

                hole.setBounds(holeX, holeY - 100, holeX + holeS, holeY + holeS - 100);
                hole.draw(canvas);
                ball5.draw(canvas);
                ball4.draw(canvas);
                ball3.draw(canvas);
                ball2.draw(canvas);
                ball1.draw(canvas);


                complete = true;
                paint.setTextSize(140);
                paint.setColor(Color.MAGENTA);

                int i = 0;
                while (complete && i < 5) {
                    if (!ballArray[i].getInHole()) {
                        complete = false;
                    }
                    i++;
                }
                paint.setColor(Color.BLACK);
                paint.setTextSize(80);
                canvas.drawText(time, 40, 90, paint);
                buttonActivate = false;
                fail = false;
                if (minutes == 0 && seconds == 0) {
                    fail = true;
                    paint.setColor(Color.BLACK);
                    paint.setTextSize(140);
                    restart = false;
                    buttonActivate = true;
                    stop = true;

                    if (vibrator != null)
                        vibrator.cancel();



                    Intent intent = new Intent(getApplicationContext(), AlarmAlertActivity.class);
                    intent.putExtra("alarm", alarm);
                    //BILL THE CUSTOMER HERE RAAAAA
                    //RAAAAA
                    //RAAAAAAAAAAA\

                    //intent.putExtra("alarm", alarm);
                    startActivity(intent);
                } else if (complete && !fail && !stop) {
                    timer.cancel();
                    paint.setTextSize(140);
                    paint.setColor(Color.MAGENTA);
                    restart = false;
                    buttonActivate = true;

                    if (vibrator != null)
                        vibrator.cancel();

                    Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                    intent.putExtra("snoozeOn", false);
                    startActivity(intent);
                }
                invalidate();
            }
            else{
                draw = false;
            }
        }
    }
}