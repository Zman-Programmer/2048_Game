package lab4_206_02.uwaterloo.ca.lab4_206_02;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Random;
import java.util.TimerTask;

import static android.R.attr.direction;


/**
 * Created by Zman on 2017-06-24.
 */

class GameLoopTask extends TimerTask implements HandGestureControllerModuleInterface {
    //the local main activity
    private Activity myActivity;
    //local context
    private Context myContext;
    //local relative layout
    private RelativeLayout myRL;
    //the enum for all the directions that the square can go
    enum gameDirection{UP, DOWN, LEFT, RIGHT, NO_MOVEMENT};
    //the random generator
    private Random myRandomGen;
    //if true make a new block
    private boolean generateBlock;
    //create a linked list for all the blocks
    private LinkedList<GameBlock> myGBList;
    //the total max number of blocks
    public static final int NUMBER_OF_SLOTS = 16;
    //the winning number
    public static final int WINNING_NUMBER = 32;
    boolean createNewBlock = false;
    //if true, then they either lost or won
    private boolean endGameFlag = false;
    //the top grid coordinates
    final static int TOP_BOUNDARY = 0;
    final static int LEFT_BOUNDARY = 0;
    final static int RIGHT_BOUNDARY = 540;
    final static int DOWN_BOUNDARY = 540;
    TextView theFinalMessage;

    //the current direction of the square
    gameDirection currentGameDirection = gameDirection.NO_MOVEMENT;
    //the previous direction of the square
    gameDirection previousGameDirection = gameDirection.NO_MOVEMENT;
    TextView endGame;

    //returns if there is a block at the X and Y Coordinates
    public GameBlock isOccupied(int coordX, int coordY){
        //create a new int value
        int[] checkCoord;
        //check all the elements in the gameblock
        for(GameBlock gb : myGBList){
            //get the XY coordinates
            checkCoord = gb.getCurrentCoordinate();
            //if that blocks XY are the same as the fed in one.
            if(checkCoord[0] == coordX && checkCoord[1] == coordY){
                //Log.d("Game Loop Report: ", "Occupant Found @ " + coordX + "," + coordY);
                //return the current gameblock
                return gb;
            }
        }
        //else return null
        return null;

    }

    //method that creates the image view and adds it to the relative layout and linked list
    private void createBlock(){
        //create a boolean of all the occupations of the blocks
        boolean[][] boardOccupence = {{false, false, false, false},
                {false, false, false, false},
                {false, false, false, false},
                {false, false, false, false}};
        //temporary value of where it is located currently
        int[] currentGBCoord;
        //the grid 1 to 4 location
        int[] currentGBindex = {0,0};
        //the new gameblocks coordinates
        int[] newGBCoord = {0,0};
        //the number of empty spots in the grid
        int numberOfEmptySlots = NUMBER_OF_SLOTS;
        //generates a number of one of the empt
        int randomSlotNum = 0;

        myRandomGen = new Random();

        //for every gameblock in the linked list
        for(GameBlock gb: myGBList){
            //get the XY coordinates of all the blocks
            currentGBCoord = gb.getTargetCoordinate();
            currentGBindex[0] = (currentGBCoord[0]) /180;
            currentGBindex[1] = (currentGBCoord[1]) /180;
            boardOccupence[currentGBindex[1]][currentGBindex[0]] = true;
            numberOfEmptySlots--;
        }
        //checks to see if there are any more slots to create another block in
        if(numberOfEmptySlots == 0){
            //if not then raise the endgame flag to lose
            endGameFlag = true;
            return;
        }
        //randomly generates a block in any of the empty slots
        randomSlotNum = myRandomGen.nextInt(numberOfEmptySlots);
        //iterates through all the spaces in  the grid
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(!boardOccupence[i][j]){
                    if(randomSlotNum == 0){
                        newGBCoord[0] = j * 180;
                        newGBCoord[1] = i * 180;
                    }
                    randomSlotNum--;
                }
            }
        }
        //creates a new block
        GameBlock newBlock;
        //all the locations and parameters secified
        newBlock = new GameBlock(myContext, myRL, newGBCoord[0], newGBCoord[1], this);
        //add the block to the linked list
        myGBList.add(newBlock);
    }

    //the constructor
    public GameLoopTask(Activity myAct, Context myCon, RelativeLayout myRel){
        myActivity = myAct;
        myContext = myCon;
        myRL = myRel;
        myGBList = new LinkedList<GameBlock>();
        createBlock();
    }

    //the set direction from the FSM
    public void setDirection(gameDirection newDirection){
        //the temp string value that hold the direction
        String temp = String.valueOf(newDirection);
        //Log.d("Direction", temp);

        //switch case for all the possible outcomes
        switch (temp){
            //when it is up
            case "UP":
                currentGameDirection = gameDirection.UP;
                break;
            //down
            case "DOWN":
                currentGameDirection = gameDirection.DOWN;
                break;
            //right
            case "RIGHT":
                currentGameDirection = gameDirection.RIGHT;
                break;
            //left
            case "LEFT":
                currentGameDirection = gameDirection.LEFT;
                break;
            //default set to no_movement
            default:
                currentGameDirection = gameDirection.NO_MOVEMENT;
        }
        //so that it only runs once
        if(currentGameDirection!= previousGameDirection && currentGameDirection!= gameDirection.NO_MOVEMENT){
            //for all the blocks set the destination
            for(GameBlock gb : myGBList){
                gb.setDesitination(newDirection);
            }
            //set the create a new block once seting all the destinations
            createNewBlock = true;
            //set the previous direction to be the current direction
            previousGameDirection = currentGameDirection;

        }
    }
    //run method will run each time
    public synchronized void run(){
        //set to run on the UI thread
        myActivity.runOnUiThread(
                new Runnable(){
                    public synchronized void run(){
                        //the boolean to see if there is no Motion currently
                        boolean noMotion = true;
                        //create a new linked list for removing the game blocks
                        LinkedList<GameBlock> removalList = new LinkedList<GameBlock>();
                        //for all the game blocks in the linkedlist
                        for(GameBlock gb : myGBList) {
                            //if they reached the goal number
                            //they win the game and stop everything
                            if(gb.getBlockNumber() == WINNING_NUMBER){
                                //create a new textview
                                endGameFlag = true;
                                theFinalMessage = new TextView(myContext);
                                theFinalMessage.setText("YOU HAVE WON!!!");
                                theFinalMessage.setTextColor(Color.parseColor("#000000"));
                                theFinalMessage.setTextSize(40);
                                myRL.addView(theFinalMessage);
                            }
                            //else, if the endgame flag is still up that means they lost
                            if(endGameFlag == true){
                                //so create a new textview for when they lose
                                theFinalMessage = new TextView(myContext);
                                theFinalMessage.setText("YOU HAVE LOST");
                                theFinalMessage.setTextColor(Color.parseColor("#000000"));
                                theFinalMessage.setTextSize(40);
                                myRL.addView(theFinalMessage);
                            }
                            //but if they have not won or lost yet, continue on normally
                            if(endGameFlag!=true){
                                //first move the game blocks
                                gb.move();
                                //check if any need s to be destroyed and all to the to be destroyed list
                                if(gb.isToBeDestroyed()){
                                    removalList.add(gb);
                                }
                                //if there is mothion then set the nomotion to be false
                                if(gb.getDirection() != gameDirection.NO_MOVEMENT) {
                                    noMotion = false;
                                }
                            }
                        }
                        //outside once everyting has finished running
                        if(noMotion && endGameFlag != true){
                            ////the conditions to create a new block
                            if(createNewBlock == true && myGBList.get(0).stoppedMoving()){
                                //create a new block
                                createBlock();
                                //turn off the createNewBlock so no more are created
                                createNewBlock = false;
                            }
                            //finally at the very end get rid of the all the unnecissary gameblccks
                            //garbage collection
                            for(GameBlock gb : removalList){
                                gb.destroyMe();
                                myGBList.remove(gb);
                            }
                        }
                    }
                }
        );
    }
}

