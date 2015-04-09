package com.android.platformer;

import android.gameengine.kontrast.input.TouchInput;
import android.util.Log;

/**
 * Created by NK on 31-3-2015.
 */
public class PlayerLaunch extends Player{

    private boolean reset;
    private float oldVal;
    private long startTime;
    private boolean afterRelease;

    public PlayerLaunch(Room myroom, int solidTile){

        this.myroom = myroom;

        spriteWhite = "player_launch_white";
        spriteBlack = "player_launch_black";
        spriteFrames = 3;

        setPlayerColor(solidTile);
        playerGravity = 0.5;
        playerFriction = 0.1;

        MAXXSPEED = 8;
        MAXYSPEED = 10;
        SENSITIVITY = 15;
        THRESHOLD = 5;
        BOUNCEFRICTION = 5;

        startAnimate();
        setAnimationSpeed(0);

        reset = true;
        oldVal = 0f;
        startTime = System.nanoTime();
        afterRelease = false;

        //add sounds
        gameSound.addSound(0, "bounce_single");
        gameSound.addSound(1, "respawn_single");
        gameSound.addSound(2, "checkpoint_single");
    }

    // the LAUNCH player has a different jump ability, overriding the gravity function
    @Override
    protected void checkGravity() {
        //set gravity
        if (placeFree(getX(), getY() + getFrameHeight()) && placeFree(getX() + getFrameWidth() - 1, getY() + getFrameHeight())) {
            setySpeed(getySpeed() + playerGravity);
            //Log.d("Gravity", "falling");
        } else {
            doBounce();

            if (TouchInput.onPress) {
                if (allowMovement && reset == true){
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
                    Log.d("LAUNCH", "Launch: " + jumpHeight);
                    Log.d("LAUNCH", "LaunchTime: " + (System.nanoTime() - startTime) / 100000000f);
                    Log.d("LAUNCH", "LaunchDistance: " + (oldVal - TouchInput.yPos) * 0.5f);
                    afterRelease = false;
                }
            }
        }
    }

}
