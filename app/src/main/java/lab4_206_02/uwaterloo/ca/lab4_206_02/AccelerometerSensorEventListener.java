package lab4_206_02.uwaterloo.ca.lab4_206_02;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Zman on 2017-07-10.
 */


//Beginning of the ACCELEROTMER CLASS =============================================================
class AccelerometerSensorEventListener implements SensorEventListener {
    //the filter reading for x
    float filterReadingx=0;
    //the filter reading for y
    float filterReadingy = 0;
    //the filter reading for z
    float filterReadingz =0;
    //the local textview for the current direction of the phone
    TextView setDirection;
    //the Finite state machine that is create locally
    private myFMS FMS1;
    //the game loop
    private GameLoopTask gameLoop;

    //class contructor
    public AccelerometerSensorEventListener(TextView direction, GameLoopTask gameLoopin){
        //set the direction to be displayed
        setDirection = direction;
        //create the new finite state machine and pass in the textview as a parameter
        FMS1 = new myFMS(setDirection);
        //set up the game loop
        gameLoop = gameLoopin;
    }


    //sets the sensor reading accuracy, set as default
    public void onAccuracyChanged (Sensor s, int i){ }

    //method for updating the values on the graph, array, and textview
    public void onSensorChanged (SensorEvent se) {
        //filter the reading for x to make it smooth
        filterReadingx += (se.values[0] - filterReadingx)/6;
        //filter the reading for y to make it smooth
        filterReadingy += (se.values[1] - filterReadingy)/6;
        //filter the reading for z to make it smooth
        filterReadingz += (se.values[2] - filterReadingz)/6;
        //create a float array to store the 3 adjusted readings temp
        float [] currentpoint = new float[3];
        //store each of the points into the new array for x
        currentpoint[0]= filterReadingx;
        //store each of the points into the new array for y
        currentpoint[1]= filterReadingy;
        //store each of the points into the new array for z
        currentpoint[2]= filterReadingz;

        //if the sensor is of type accelerometer then continuie
        if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            //pass in the readings of the x and y smoothed out readings
            FMS1.supplyReading(currentpoint[0], currentpoint[1]);
            //for storing temporarly the state of the state
            String temp = String.valueOf(FMS1.getState());

            //Log.e("Direction:" , ""+temp);
            //switch case on what to do in each of the diff scenarios
            switch (temp){
                //when it is up
                case "UP":
                    //set the diretion to be up
                    gameLoop.setDirection(GameLoopTask.gameDirection.UP);
                    //display it on the textview
                    setDirection.setText("UP");
                    break;
                //when it is down
                case "DOWN":
                    //set the direction to be down
                    gameLoop.setDirection(GameLoopTask.gameDirection.DOWN);
                    //set the textview to be DOWN
                    setDirection.setText("DOWN");
                    break;
                //when it is LEFT
                case "LEFT":
                    //set the direction to be left
                    gameLoop.setDirection(GameLoopTask.gameDirection.LEFT);
                    //set the textview to be LEFT
                    setDirection.setText("LEFT");
                    break;
                //case where it is right
                case "RIGHT":
                    //set the direction to be right
                    gameLoop.setDirection(GameLoopTask.gameDirection.RIGHT);
                    //set teh textview to be RIGHT
                    setDirection.setText("RIGHT");
                    break;
                //default
                default:
                    //just set the rest of the cases to be NO_MOVEMENT
                    gameLoop.setDirection(GameLoopTask.gameDirection.NO_MOVEMENT);
            }
        }
    }
}
//End of ACCELEROMETER CLASS ======================================================================
