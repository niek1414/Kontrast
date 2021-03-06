package com.android.platformer;

import android.gameengine.kontrast.objects.MoveableGameObject;
import android.gameengine.kontrast.tiles.Tile;

/**
 * Created by NK on 6-4-2015
 * Project name is Kontrast in com.android.platformer
 */

public class Trap extends MoveableGameObject /*implements ICollision*/ {

    protected int trapColor; // 0 = black, 1 = white
    protected Room myroom;

    public Trap(Room myroom, int trapColor) {
        this.myroom = myroom;
        this.trapColor = trapColor;
    }

    @Override
    public void update() {
        super.update();
    }

    // set sprite and frame
    protected void setAppearance() {
        if (trapColor == 1) {
            setSprite("trap_white", 4);
        } else {
            setSprite("trap_black", 4);
        }
        setDirectionFrame();
    }

    //set the correct direction frame, dependant on adjacent tiles
    public void setDirectionFrame() {
        startAnimate();
        setAnimationSpeed(0);

        Tile myTile = getTileOnPosition(getX(), getY());
        if (myTile != null) {
            int posX = myTile.getTileNumberX();
            int posY = myTile.getTileNumberY();

            Tile toCheck[] = {
                getTileOnIndex(posX, posY - 1), // up
                getTileOnIndex(posX, posY + 1), // down
                getTileOnIndex(posX + 1, posY), // left
                getTileOnIndex(posX - 1, posY)  // right
            } ;

            for (int i = 0; i < toCheck.length; i++) {
                if (toCheck[i] != null){
                    if (trapColor == 1) {
                        if (toCheck[i].getTileType() == 3 || toCheck[i].getTileType() == 1) {
                            //Log.e("TRAPLOG", "Adjacent Tile Found!" + i);
                            setFrameNumber(i);
                            return; // might be considered ugly by some teachers... :D
                        }
                    } else if (trapColor == 0) {
                        if (toCheck[i].getTileType() == 3 || toCheck[i].getTileType() == 0) {
                            //Log.e("TRAPLOG", "Adjacent Tile Found!" + i);
                            setFrameNumber(i);
                            return; // might be considered ugly by some teachers... :D
                        }
                    }
                }
            }
        }
    }
}
