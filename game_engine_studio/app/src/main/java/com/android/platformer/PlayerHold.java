package com.android.platformer;

import android.gameengine.kontrast.input.TouchInput;
import android.util.Log;

/**
 * Created by NK on 31-3-2015.
 */
public class PlayerHold extends Player{

    private boolean reset;
    private float oldVal;
    private long startTime;
    private boolean afterRelease;

    public PlayerHold(Room myroom, int solidTile){
        this.myroom = myroom;

        spriteWhite = "player_hold_white";
        spriteBlack = "player_hold_black";
        spriteFrames = 10;

        setPlayerColor(solidTile);
        playerGravity = 0.5;
        playerFriction = 0.1;

        MAXXSPEED = 6;
        MAXYSPEED = 9;
        SENSITIVITY = 20;
        THRESHOLD = 5;
        BOUNCEFRICTION = 2;

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
                if (reset == true && getySpeed() == 0){
                    startTime = System.currentTimeMillis();
                    reset = false;
                    afterRelease = true;
                    Log.e("HI", "RESETTTTTTTTTTTTTTTTT");
                    allowMovement = false;
                    setxSpeed(0);
                }
                if (afterRelease == true){
                    int scale = (int) (-((startTime - System.currentTimeMillis()) / 100));

                    if (scale > 0 && scale < 10) {
                        setFrameNumber(scale);
                    }
                }

            } else if (afterRelease == true && TouchInput.onRelease){
                Log.e("HI", "UUUUUUUUUUUUUUUUUUUUUUUUU");
                reset = true;
                if (placeFree(getX(), getY() - 1) && placeFree(getX() + getFrameWidth() - 1, getY() - 1)) {
                    float jumpHeight = ((startTime - System.currentTimeMillis()) / 100);
                    setySpeed(jumpHeight);
                    Log.e("HI", "Hold: " + jumpHeight);
                    Log.e("HI", "S: " + startTime);
                    Log.e("HI", "C: " + System.currentTimeMillis());
                }
                afterRelease = false;
                allowMovement = true;
                setFrameNumber(0);
            }
        }
    }

    @Override
    protected void setPlayerAnimation() {}
}
