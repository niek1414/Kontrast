package com.android.platformer;

import android.gameengine.kontrast.objects.GameObject;

/**
 * Created by NK on 7-4-2015.
 */
public class Portal extends GameObject {
    private int levelNumber;
    private boolean isGoal;

    Portal(boolean isGoal, int levelNumber){
        this.levelNumber = levelNumber;
        this.isGoal = isGoal;
        setSprite("portal");
    }

    public boolean getIsGoal(){
        return isGoal;
    }

    public int getLevelNumber(){
        return levelNumber;
    }
}
