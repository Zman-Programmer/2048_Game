package lab4_206_02.uwaterloo.ca.lab4_206_02;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/**
 * Created by Zman on 2017-06-24.
 */

public class GameBlock extends android.support.v7.widget.AppCompatImageView {
    //keeps track of the current block location for x
    public int myCoordX = 0;
    //tracks the current y location
    public int myCorrdY = 0;
    //the location the block should be at i x
    public int Xgoal = 0;
    //location where the block should be at in y
    public int Ygoal = 0;
    //velocity
    public int velocity = 0;
    //the textview of the number on the game block
    protected TextView numberTV;
    //acceleration
    private int acceleration = 8;
    //the local copy of the relative layout
    public RelativeLayout relativelay;
    //the local relative layout
    private GameLoopTask myGL;
    //the number on the gameblock
    int number;
    //the number of frames that is required before the thing resets
    int theFinalCountdown = 15;
    //the initial direction of the block
    private GameLoopTask.gameDirection targetDirection = GameLoopTask.gameDirection.NO_MOVEMENT;
    //if the block needs to be removed then call this
    protected boolean toBeDestroyed = false;
    //the temporary block number of the things to merge
    private GameBlock targetMergeBlock = null;

    //this method returns the current Coordinates of this gameblock
    public int[] getCurrentCoordinate(){
        //stores it into the temp int array
        int[] thisCoord = new int[2];
        thisCoord[0] = myCoordX;
        thisCoord[1] = myCorrdY;
        //returns this array to the gameloop for determining what is filled or not
        return thisCoord;
    }

    //Allowing blocks to return their goal location
    public int[] getTargetCoordinate(){
        //stores the goal into the local int array
        int[] thisCoord = new int[2];
        thisCoord[0] = Xgoal;
        thisCoord[1] = Ygoal;
        //returns the goal location
        return thisCoord;
    }


    //method calculates the number of merges possible
    protected int calculateMerges(int[] numA, int numOfOccupants){
        //the total number of merges
        int numMerges = 0;
        //the switch statement for determining all the possible cases
        switch(numOfOccupants){
            //when there is nothing to merge
            case 0:
                return 0;
            //when there is only one block in row or colom
            case 1:
                if(numA[0] == numA[1]) {
                    numMerges = 1;
                    toBeDestroyed = true;
                }
                break;
            //when there is two other blocks in the colom
            case 2:
                if(numA[0] == numA[1]) {
                    numMerges = 1;
                }
                else if(numA[1] == numA[2]){
                    numMerges = 1;
                    toBeDestroyed = true;
                }
                break;
            //when there is three
            case 3:
                if(numA[0] == numA[1]){
                    if(numA[2] == numA[3]) {
                        numMerges = 2;
                        toBeDestroyed = true;
                    }
                    else {
                        numMerges = 1;
                    }
                }
                else if(numA[1] == numA[2]){
                    numMerges = 1;
                }
                else if(numA[2] == numA[3]){
                    numMerges = 1;
                    toBeDestroyed = true;
                }

                break;
            //do nothing and tell them there is nothing here to merge
            default:
                return 0;
        }
        //returns the number of totol merges
        return numMerges;
    }

    //the constructor for the Game Block
    public GameBlock(Context myContext, RelativeLayout rl, int CoordX, int CoordY, GameLoopTask gbGL){
        //set the context to be super (use from the parent class)
        super(myContext);
        //set up everything for the blok image view
        this.setImageResource(R.drawable.gameblock);
        this.setX(CoordX);
        this.setY(CoordY);
        this.setScaleX(1.0f);
        this.setScaleY(1.0f);
        rl.addView(this);
        //image.bringToFront();

        //save the game loop in to the local variable
        this.myGL = gbGL;
        //same for the relative layout
        this.relativelay = rl;

        //declare the random generator
        Random myRandomGen = new Random();
        //generates the random number on the block, either 2 or 4
        number = (myRandomGen.nextInt(2) + 1) * 2;
        //sets the x and y corrdinates
        myCoordX = CoordX;
        myCorrdY = CoordY;
        //and the goal coordinates
        Xgoal = myCoordX;
        Ygoal = myCorrdY;

        //sets the text view
        numberTV = new TextView(myContext);
        numberTV.setX(CoordX + 30);
        numberTV.setY(CoordY + 30);
        numberTV.setText(String.format("%d",number));
        numberTV.setTextSize(40.0f);
        numberTV.setTextColor(Color.BLACK);
        //adds the textview
        relativelay.addView(numberTV);
        //bring it to the front of the view
        numberTV.bringToFront();
        //initialize the direction to be no where
        targetDirection = GameLoopTask.gameDirection.NO_MOVEMENT;
    }

    //gets the currrent direction of the game block to see
    //if the brick is sstill moving
    public GameLoopTask.gameDirection getDirection(){
        return targetDirection;
    }

    //this methond destroys the block \
    //gets rid of it
    public void destroyMe(){
        //first removes the image viewfrom the view
        relativelay.removeView(numberTV);
        //then removes the text
        relativelay.removeView(this);
        //doubles the number of one of them to merge
        targetMergeBlock.doubleMyNumber();
    }

    //check if the block is still mving or has stopped moving
    public boolean stoppedMoving(){
        //if the block is at it's goal then
        if(Xgoal == myCoordX && Ygoal == myCorrdY){
            //return true
            return true;
        }
        //else do nothing
        else {
            return false;
        }
    }

    //this method sets the Xgoal and the Ygoal
    public void setDesitination(GameLoopTask.gameDirection directioninput) {

        //declare variables
        targetDirection = directioninput;
        //the temporary X and Y coordinates
        int testCoordX = myCoordX;
        int testCoordY = myCorrdY;
        //total number of other blocks on the board
        int numOfOccupants = 0;
        //the total number of merges infront of it
        int numOfMerges = 0;
        //the numbers of the blocks that it is trying to merge with
        int[] occupantNumbers = {0,0,0,0};
        //temporary test block
        GameBlock testBlock;
        //the switch statement of based on what direction it is moving
        switch (directioninput) {
            //when up
            case UP:
                //if the square is not on the first row then
                if (myCorrdY > 0) {
                    //set the desired location to be one up
                    Ygoal = 0;
                }
                //in the case where the goal location is where it is already at
                else {
                    Ygoal = myCorrdY;
                }
                //check all the blocks infront of the current game bloick
                while(testCoordY != Ygoal){
                    //iterate through all of them
                    testCoordY -= 180;
                    //test if the location is occupied
                    testBlock = myGL.isOccupied(myCoordX, testCoordY);

                    //if it is not null, then check
                    if(testBlock != null){
                        //set the block infront to the local GameBlok variable
                        targetMergeBlock = testBlock;
                        //get the numbers of the game block
                        occupantNumbers[numOfOccupants] = testBlock.getBlockNumber();
                        //then increase the number of gameblocks in the way
                        numOfOccupants++;
                    }
                }
                //get the number of the block infront of it
                occupantNumbers[numOfOccupants] = number;
                //calculate the number of merges by calling calculateMerges
                numOfMerges = calculateMerges(occupantNumbers, numOfOccupants);
                //set the Ygoal based on the logic diagram
                Ygoal = Ygoal + numOfOccupants * 180- numOfMerges * 180;
                break;
            //when down
            case DOWN:
                //is the square is not on the last row then
                if (myCorrdY < 540) {
                    //set the desired location to be one down
                    Ygoal = 540;
                }
                else {
                    Ygoal = myCorrdY;
                }
                //please refer to the UP logicn since it is all very similar
                while(testCoordY != Ygoal){
                    testCoordY += 180;
                    testBlock = myGL.isOccupied(myCoordX, testCoordY);

                    if(testBlock != null){
                        targetMergeBlock = testBlock;
                        occupantNumbers[numOfOccupants] = testBlock.getBlockNumber();
                        numOfOccupants++;
                    }

                }
                //please refer to the UP logicn since it is all very similar
                occupantNumbers[numOfOccupants] = number;
                numOfMerges = calculateMerges(occupantNumbers, numOfOccupants);
                Ygoal = Ygoal- numOfOccupants * 180 + numOfMerges * 180;

                break;
            // when right
            case RIGHT:
                //if the square is not at the last colom then
                if (myCoordX < 540) {
                    //set the desired location to be one to square to the right
                    Xgoal = 540;
                }
                else {
                    Xgoal = myCoordX;
                }
                //please refer to the UP logicn since it is all very similar
                while(testCoordX != Xgoal){
                    testCoordX += 180;
                    testBlock = myGL.isOccupied(testCoordX, myCorrdY);

                    if(testBlock != null){
                        targetMergeBlock = testBlock;
                        occupantNumbers[numOfOccupants] = testBlock.getBlockNumber();
                        numOfOccupants++;
                    }
                }
                //please refer to the UP logicn since it is all very similar
                occupantNumbers[numOfOccupants] = number;
                numOfMerges = calculateMerges(occupantNumbers, numOfOccupants);
                Xgoal = Xgoal- numOfOccupants * 180 + numOfMerges * 180;

                break;
            //when left
            case LEFT:
                //if the square is not on the the first colom
                if (myCoordX > 0) {
                    //then set the disired location to be one colom to the left
                    Xgoal = 0;
                }
                else {
                    Xgoal = myCoordX;
                }
                //please refer to the UP logicn since it is all very similar
                while(testCoordX != Xgoal){
                    testCoordX -= 180;
                    testBlock = myGL.isOccupied(testCoordX, myCorrdY);

                    if(testBlock != null){
                        targetMergeBlock = testBlock;
                        occupantNumbers[numOfOccupants] = testBlock.getBlockNumber();
                        numOfOccupants++;
                    }
                }
                //please refer to the UP logicn since it is all very similar
                occupantNumbers[numOfOccupants] = number;
                numOfMerges = calculateMerges(occupantNumbers, numOfOccupants);
                Xgoal = Xgoal + numOfOccupants * 180- numOfMerges * 180;
                break;
            default:
                //do nothing
        }
    }

    //returns the current gameblocks number
    public int getBlockNumber(){
        return number;
    }

    //returns to the GameLoopTask if the current block needs to be destroyed
    public boolean isToBeDestroyed(){
        return toBeDestroyed;
    }

    //this method doubles the current number of the current gameblock
    public void doubleMyNumber(){
        //multiply by two
        number *= 2;
        //set the textview to be that number
        numberTV.setText(String.format("%d", number));
    }











    //method to move the square image view
    //@Override
    public void move(){

        Log.e("The Direction", ""+targetDirection);
        switch(targetDirection){
        case LEFT:

        //targetCoordX = GameLoop.LEFT_BOUNDARY;

        if(myCoordX > Xgoal){
            if((myCoordX - velocity) <= Xgoal){
                myCoordX = Xgoal;
                velocity = 0;
            }
            else {
                myCoordX -= velocity;
                velocity += acceleration;
            }
        }

        break;

        case RIGHT:

        //targetCoordX = GameLoop.RIGHT_BOUNDARY;

        if(myCoordX < Xgoal){
            if((myCoordX + velocity) >= Xgoal){
                myCoordX = Xgoal;
                velocity = 0;
            }
            else {
                myCoordX += velocity;
                velocity += acceleration;
            }
        }

        break;

        case UP:

        //targetCoordY = GameLoop.UP_BOUNDARY;

        if(myCorrdY > Ygoal){
            if((myCorrdY - velocity) <= Ygoal){
                myCorrdY = Ygoal;
                velocity = 0;
            }
            else {
                myCorrdY -= velocity;
                velocity += acceleration;
            }
        }

        break;

        case DOWN:

        //targetCoordY = GameLoop.DOWN_BOUNDARY;

        if(myCorrdY < Ygoal){
            if((myCorrdY + velocity) >= Ygoal){
                myCorrdY = Ygoal;
                velocity = 0;
            }
            else {
                myCorrdY += velocity;
                velocity += acceleration;
            }
        }

        break;

        default:
        break;

    }

        this.setX(myCoordX);
        this.setY(myCorrdY);

        numberTV.setX(myCoordX + 30);
        numberTV.setY(myCorrdY + 30);
        numberTV.bringToFront();

        if(targetDirection != GameLoopTask.gameDirection.NO_MOVEMENT){
            theFinalCountdown--;
        }

        if(theFinalCountdown == 0) {
            targetDirection = GameLoopTask.gameDirection.NO_MOVEMENT;
            theFinalCountdown = 15;
        }

   }
}