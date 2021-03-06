package com.android.platformer;

/**
 * Created by NK on 31-3-2015.
 */
public class PlayerSquare extends Player{
    /**
     * Initializes square player
     * @param myroom
     * @param solidTile
     */
    public PlayerSquare(Room myroom, int solidTile){

        this.myroom = myroom;

        spriteWhite = "block_white";
        spriteBlack = "block_black";
        spriteFrames = 1;

        setPlayerColor(solidTile);
        playerGravity = 0.5;

        MAXXSPEED = 4;
        MAXYSPEED = 8;
        SENSITIVITY = 30;
        THRESHOLD = 5;
        BOUNCEFRICTION = 2;

        playerFriction = 0.1;

        startAnimate();
        setAnimationSpeed(0);
    }

    // overriding the bounce function because the SQUARE player does not bounce at all
    @Override
    protected void doBounce() {
        setySpeed(0);
        setY(getY() / getFrameHeight() * getFrameHeight()); // snap Y to prevent getting stuck

        if (firstTimeDown) {
            //play sound
            myroom.soundControl.gameSound.stopSound(4);
            myroom.soundControl.gameSound.playSound(4, 0);
            firstTimeDown = false;
        }
    }
}
