package com.android.platformer;

import android.gameengine.icadroids.input.TouchInput;
import android.util.Log;

/**
 * Created by NK on 31-3-2015.
 */
public class PlayerLaunch extends Player{

    private boolean reset;
    private float oldVal;
    private long startTime;
    private boolean afterRelease;

    public PlayerLaunch(Room myroom){

        this.myroom = myroom;

        setSprite("player_12", 5);
        playerGravity = 0.5;
        playerFriction = 0.1;

        startAnimate();
        setAnimationSpeed(0);

        reset = true;
        oldVal = 0f;
        startTime = System.nanoTime();
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
                    oldVal = TouchInput.yPos;
                    startTime = System.nanoTime();
                    reset = false;
                    afterRelease = true;
                }

            } else if (afterRelease == true){
                reset = true;
                if (placeFree(getX(), getY() - 1) && placeFree(getX() + getFrameWidth() - 1, getY() - 1)) {
                    float jumpHeight = (-((((oldVal - TouchInput.yPos) * 0.5f)) / ((System.nanoTime() - startTime) / 100000000f))) * 0.09f;
                    setySpeed(jumpHeight);
                    Log.e("HI", "Launch: " + jumpHeight);
                    Log.e("HI", "LaunchTIME: " + (System.nanoTime() - startTime) / 100000000f);
                    afterRelease = false;
                }
            }
        }
    }

}
