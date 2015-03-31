package com.android.platformer;

import android.gameengine.icadroids.input.TouchInput;
import android.util.Log;

/**
 * Created by NK on 31-3-2015.
 */
public class PlayerHold extends Player{

    private boolean reset;
    private float oldVal;
    private long startTime;
    private boolean afterRelease;

    public PlayerHold(Room myroom){

        this.myroom = myroom;

        setSprite("player_12", 5);
        playerGravity = 0.5;
        playerFriction = 0.1;

        startAnimate();
        setAnimationSpeed(0);

        reset = true;
        oldVal = 0f;
        startTime = System.currentTimeMillis();
        afterRelease = false;
    }

    @Override
    protected void checkGravity() {
        //set gravity
        if (placeFree(getX(), getY() + getFrameHeight()) && placeFree(getX() + getFrameWidth() - 1, getY() + getFrameHeight())) {
            setySpeed(getySpeed() + playerGravity);
            //Log.d("Gravity", "falling");
        } else {
            doBounce();

            if (TouchInput.onPress) {
                if (reset == true){
                    startTime = System.currentTimeMillis();
                    reset = false;
                    afterRelease = true;
                    Log.e("HI", "RESETTTTTTTTTTTTTTTTT");
                }

            } else if (afterRelease == true && TouchInput.onRelease){
                Log.e("HI", "UUUUUUUUUUUUUUUUUUUUUUUUU");
                reset = true;
                if (placeFree(getX(), getY() - 1) && placeFree(getX() + getFrameWidth() - 1, getY() - 1)) {
                    float jumpHeight = ((startTime - System.currentTimeMillis()));
                    setySpeed(jumpHeight);
                    Log.e("HI", "Hold: " + jumpHeight);
                    Log.e("HI", "S: " + startTime);
                    Log.e("HI", "C: " + System.currentTimeMillis());
                    afterRelease = false;
                }
            }
        }
    }

}