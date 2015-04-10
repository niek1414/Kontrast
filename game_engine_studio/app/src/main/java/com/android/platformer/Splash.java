package com.android.platformer;

import android.gameengine.kontrast.input.TouchInput;
import android.gameengine.kontrast.objects.GameObject;

/**
 * Created by NK on 7-4-2015.
 */

public class Splash extends GameObject{
    Room game;
    private int sheet;
    private boolean hasTapped;

    /**
     * Initialize splash screen
     * @param game
     */
    public Splash(Room game){
        this.game = game;
        sheet = 0;
        hasTapped = false;
        setSprite("splash_start");
    }

    /**
     * function that removes the splash screen and allows player movement
     */
    public void destroy(){
        ((Player)this.game.getPlayer()).setAllowMovement(true);
        deleteThisGameObject();
    }

    @Override
    public void update() {
        super.update();
        if (TouchInput.onPress && !hasTapped){
            hasTapped = true;
        }
        if (TouchInput.onRelease && hasTapped){
            hasTapped = false;

            // play sound
            game.soundControl.gameSound.stopSound(9);
            game.soundControl.gameSound.playSound(9, 0);

            if (sheet == 0){
                sheet = 1;
                setSprite("splash_tut");
            } else if (sheet == 1){
                sheet = 2;
                setSprite("splash_tut2");
            } else if (sheet == 2) {
                destroy();
            }
        }
    }
}
