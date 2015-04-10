package com.android.platformer;

import android.gameengine.kontrast.objects.GameObject;

/**
 * Created by NK on 7-4-2015.
 */
public class Portal extends GameObject {
    private int levelNumber;
    private boolean isGoal;

    /**
     * Initializes portal
     * @param isGoal
     * @param levelNumber
     */
    public Portal(boolean isGoal, int levelNumber){
        this.levelNumber = levelNumber;
        this.isGoal = isGoal;
        setSprite("portal");
    }

    /**
     * returns whether the portal is used as finish or not.
     * @return boolean goal
     */
    public boolean getIsGoal(){
        return isGoal;
    }

    /**
     * returns the number of the level to go to.
     * @return int levelNumber
     */
    public int getLevelNumber(){
        return levelNumber;
    }
}
