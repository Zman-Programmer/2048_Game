package lab4_206_02.uwaterloo.ca.lab4_206_02;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Zman on 2017-06-30.
 */


public abstract class GameBlockTemplate extends android.support.v7.widget.AppCompatImageView {


    public GameBlockTemplate(Context gbCTX){
        super(gbCTX);
    }

    public abstract boolean setDesitination(GameLoopTask.gameDirection myDir);

    public abstract void move();

}