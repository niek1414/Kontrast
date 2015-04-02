package com.android.platformer;

/**
 * Created by NK on 31-3-2015.
 */
public class PlayerDefault extends Player{

    public PlayerDefault(Room myroom){

        this.myroom = myroom;

        setSprite("player_default", 5);
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
