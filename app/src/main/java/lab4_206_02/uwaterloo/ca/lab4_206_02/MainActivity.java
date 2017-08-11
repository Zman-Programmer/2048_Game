package lab4_206_02.uwaterloo.ca.lab4_206_02;

import android.graphics.Color;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    //declare the textview for the direction
    TextView direction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //declare the linear layout
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.layout);
        //set it to be a square with the width of the screen
        rl.getLayoutParams().width = 720; //gameboard size
        rl.getLayoutParams().height = 720;
        //set the gameboard picture to be the packground of the phone
        rl.setBackgroundResource(R.drawable.gameboard);


        //start of the textview of the dirextion the phone is currently in
        direction = new TextView(getApplicationContext());
        direction.setText("DIRECTION");
        direction.setTextColor(Color.parseColor("#000000"));
        direction.setTextSize(20);
        rl.addView(direction);

        //Set up an animation timer of 16ms (60 frames per second)
        Timer myTimer = new Timer();
        //create a new Game loop task (look at game loop task class)
        GameLoopTask gameLoop = new GameLoopTask(this, getApplicationContext(), rl);
        //create a timer scheduale to run ever 50ms starting at 50ms
        myTimer.schedule(gameLoop, 50, 50);

        //declare your sensor manager for the accelerometer
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        //now call the accelerometer class and pass in a lot of parameters such as graphs
        SensorEventListener a = new AccelerometerSensorEventListener(direction, gameLoop);
        //set the Sensor Event Listener to AcceleromterSensorEventListener to get values
        final AccelerometerSensorEventListener y = (AccelerometerSensorEventListener) a;
        //register the sensor
        //set the SENSOR DELAY to GAME to get optimal performance
        sensorManager.registerListener(a, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
    }
}

