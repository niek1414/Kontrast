package com.android.platformer;

/**
 * Created by NK on 31-3-2015.
 */
public class PlayerDefault extends Player{

    public PlayerDefault(Room myroom, int solidTile){

        this.myroom = myroom;
        spriteWhite = "player_default_white";
        spriteBlack = "player_default_black";
        spriteFrames = 5;

        setPlayerColor(solidTile);
        playerGravity = 0.5;
        playerFriction = 0.1;

        MAXXSPEED = 6;
        MAXYSPEED = 8;
        SENSITIVITY = 20;
        THRESHOLD = 5;
        BOUNCEFRICTION = 2;

        startAnimate();
        setAnimationSpeed(0);

        //add sounds
        gameSound.addSound(0, "bounce_single");
        gameSound.addSound(1, "respawn_single");
        gameSound.addSound(2, "checkpoint_single");
    }

}
