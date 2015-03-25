package com.android.platformer;

import java.util.ArrayList;
import java.util.List;

import android.gameengine.icadroids.input.MotionSensor;
import android.gameengine.icadroids.input.OnScreenButtons;
import android.gameengine.icadroids.input.TouchInput;
import android.gameengine.icadroids.objects.GameObject;
import android.gameengine.icadroids.objects.MoveableGameObject;
import android.gameengine.icadroids.objects.collisions.ICollision;
import android.gameengine.icadroids.objects.collisions.TileCollision;
import android.gameengine.icadroids.tiles.Tile;
import android.graphics.Point;
import android.util.Log;

/**
 * Vis is the player object of the game. It swims through the game, eats
 * strawberries and avoids the monster (although right now, nothing bad happens
 * when the monster gets you, just i.b.)
 * 
 * @author Paul Bergervoet
 */
public class Player extends MoveableGameObject implements ICollision
{

	/**
	 * Reference to the game itself
	 */
	private Room myroom;
	
	/**
	 * Total score from strawberries eaten
	 */
	private int score;
    private double playerGravity;
    private double playerFriction;

	/**
	 * Constructor of Vis
	 */
	public Player(Room myroom)
	{
		this.myroom = myroom;
		
		setSprite("player_12", 1);
        playerGravity = 0.5;
        playerFriction = 0.5;

		score = 0;
	}

	/**
	 * update 'Vis': handle collisions and input from buttons / motion sensor
	 * 
	 * @see android.gameengine.icadroids.objects.MoveableGameObject#update()
	 */

    private boolean placeFree(int x, int y){ // TODO: type blok afhankelijk maken van de player!!
        // getX() + getFrameWidth()
        // getY() + getFrameHeight()
        Tile myTile = getTileOnPosition(x, y);

        if (myTile != null) {
            Log.e("TILE", "" + myTile.getTileType());
            if (myTile.getTileType() == 0) {
                return false;
            }
        }
        return true;
    }

	@Override
	public void update()
	{
		super.update();
		// collisions with objects
		ArrayList<GameObject> gebotst = getCollidedObjects();
		if (gebotst != null)
		{
//			for (GameObject g : gebotst)
//			{
//				if (g instanceof Strawberry)
//				{
//					score = score + ((Strawberry) g).getPoints();
//					// Log.d("hapje!!!", "score is nu " + score);
//                    myroom.deleteGameObject(g);
//				} else if (g instanceof Monster)
//				{
//					// Log.d("Gepakt", "Ai, wat nu...");
//				}
//			}
		}
		// Handle input. Both on screen buttons and tilting are supported.
		// Buttons take precedence.
		boolean buttonPressed = false;


        //test gravity
        if (placeFree(getX(), getY() + getFrameHeight() + (int)getySpeed()) &&
              placeFree(getX() + getFrameWidth(), getY() + getFrameHeight() + (int)getySpeed())) {
            setySpeed(getySpeed() + playerGravity);
        }
//        else {
//            if (getySpeed() > 0) {
//                setySpeed(0);
//                for (int i = 0; i <= getySpeed(); i++){
//                    if (!placeFree(getX(), getY() + getFrameHeight() + (int)getySpeed()) ||
//                            !placeFree(getX() + getFrameWidth(), getY() + getFrameHeight() + i)) {
//                        setY(getY()+i);
//                    }
//                }
//                //setY(Math.ceil(getY() * 12) / 12);
//            }
//        }

        if (MotionSensor.getPitch() > 3 || MotionSensor.getPitch() < -3) {
            setxSpeed(MotionSensor.getPitch() / 2);
        }

        if (TouchInput.onPress) {
            setySpeed(-8);
        }
		
		// Example of how to use the touch screen: Vis swims towards touch location.
		// To use this, comment out the input from OnScreenButtons and MotionSensor 
		// and switch the use-settings in class Vissenkom
		/*
		 	// get readings from the TouchInput
		 	float targetX = TouchInput.xPos;
		 	float targetY = TouchInput.xPos;
		 	// When using the viewport, translate screen locations to game world 
		 	Point p = mygame.translateToGamePosition(targetX, targetY);
		 	// Move in the direction of the point that has been touched
			setSpeed(8);
		 	moveTowardsAPoint(p.x, p.y);
		*/ 
	}

	/**
	 * Handle tile collisions: Vis bounces off tiles of type 1 only.
	 * 
	 * @see android.gameengine.icadroids.objects.collisions.ICollision#collisionOccurred(java.util.List)
	 */
	@Override
	public void collisionOccurred(List<TileCollision> collidedTiles)
	{
		// Do we know for certain that the for-each loop goes through the list
		// front to end?
		// If not, we have to use a different iterator!
		for (TileCollision tc : collidedTiles)
		{
			if (tc.theTile.getTileType() == 0)
			{
				moveUpToTileSide(tc);
				if (tc.collisionSide == tc.LEFT || tc.collisionSide == tc.RIGHT) {
                    setxSpeed(0);
                }
                if (tc.collisionSide == tc.BOTTOM || tc.collisionSide == tc.TOP) {
                    setySpeed(0);
                }
				return; // might be considered ugly by some colleagues...
			}
		}
	}

	/**
	 * Get the score
	 * 
	 * @return current value of score
	 */
	public int getScore()
	{
		return score;
	}

}
