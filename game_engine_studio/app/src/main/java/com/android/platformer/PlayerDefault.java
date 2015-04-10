package com.android.platformer;

/**
 * Created by NK on 31-3-2015.
 */
public class PlayerDefault extends Player{
    /**
     * Initializes default player
     * @param myroom
     * @param solidTile
     */
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
    }

}
