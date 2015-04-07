package com.android.platformer;

import android.gameengine.kontrast.objects.MoveableGameObject;
import android.util.Log;

/**
 * Created by NK on 7-4-2015.
 */
public class Sign extends MoveableGameObject{
    private int levelNumber;

    public Sign(int levelNumber){
        this.levelNumber = levelNumber;
        setSprite("sign", 10);
        startAnimate();
        setAnimationSpeed(0);
        setFrameNumber(levelNumber - 1);
    }
}
