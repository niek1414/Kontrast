package com.android.platformer;

import android.gameengine.kontrast.objects.GameObject;
import android.gameengine.kontrast.input.TouchInput;

import game.MyAndroidGame;

/**
 * Created by NK on 7-4-2015.
 */

public class Splash extends GameObject{
    Room game;

    public Splash(Room game){
        this.game = game;
        setSprite("splash_start");
    }

    public void destroy(){
        ((Player)this.game.getPlayer()).setAllowMovement(true);
        deleteThisGameObject();
    }

    @Override
    public void update() {
        super.update();
        if (TouchInput.onPress){
            destroy();
        }
    }
}
