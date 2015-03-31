package com.android.platformer;

/**
 * Created by NK on 31-3-2015.
 */
public class PlayerDefault extends Player{

    public PlayerDefault(Room myroom){

        this.myroom = myroom;

        setSprite("player_12", 5);
        playerGravity = 0.5;
        playerFriction = 0.1;

        startAnimate();
        setAnimationSpeed(0);
    }

}
