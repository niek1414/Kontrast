package com.android.platformer;

import android.gameengine.kontrast.sound.GameSound;

/**
 * Created by NK on 9-4-2015
 * Project name is Kontrast in com.android.platformer
 */
public class SoundControl {

    public GameSound gameSound = new GameSound();
    private Room game;

    /**
     * Initializes sound control
     * @param game
     */
    public SoundControl(Room game){
        this.game = game;
    }

    /**
     * Initialize sounds
     */
    public void initializeSound(){
        //add sounds
        //player
        gameSound.addSound(0, "jump_single");
        gameSound.addSound(1, "hold_single");
        gameSound.addSound(2, "release_single");
        gameSound.addSound(3, "launch_single");

        gameSound.addSound(4, "land_single");
        gameSound.addSound(5, "bounce_single");

        gameSound.addSound(6, "respawn_single");
        gameSound.addSound(7, "checkpoint_single");
        gameSound.addSound(8, "next_lvl_single");

        //splash
        gameSound.addSound(9, "sheet_single");

        //change player
        gameSound.addSound(10, "change_ball_single");

    }
}
