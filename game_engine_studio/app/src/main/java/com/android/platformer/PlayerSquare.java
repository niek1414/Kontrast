package com.android.platformer;

/**
 * Created by NK on 31-3-2015.
 */
public class PlayerSquare extends Player{

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

        //add sounds
        gameSound.addSound(0, "bounce_single");
        gameSound.addSound(1, "respawn_single");
        gameSound.addSound(2, "checkpoint_single");
    }

    // overriding the bounce function because the SQUARE player does not bounce at all
    @Override
    protected void doBounce() {
        setySpeed(0);
        setY(getY() / getFrameHeight() * getFrameHeight()); // snap Y to prevent getting stuck
    }
}
