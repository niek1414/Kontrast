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


public class Player extends MoveableGameObject implements ICollision
{

	//Reference to the game itself
	private Room myroom;

	// variables
	private int score;
    private double playerGravity;
    private double playerFriction;
    private final int MAXXSPEED;
    private final int MAXYSPEED;
    private final int SENSITIVITY;
    private boolean isCollidingSide;
    private boolean isCollidingSurface;

	// constructor
	public Player(Room myroom)
	{
		this.myroom = myroom;
		
		setSprite("player_12", 1);
        playerGravity = 0.5;
        playerFriction = 0.1;

        MAXXSPEED = 8;
        MAXYSPEED = 8;
        SENSITIVITY = 5;

		score = 0;
	}

    // awesome home-made function that checks if a given position is collision free
    // TODO: make tile type dependant on the player!
    private boolean placeFree(int x, int y){
        Tile myTile = getTileOnPosition(x, y);
        if (myTile != null) {
            if (myTile.getTileType() == 0)
                return false;
        }
        return true;
    }

    // handle collisions and movement
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

        //set gravity
        if (placeFree(getX(), getY() + getFrameHeight()) && placeFree(getX() + getFrameWidth() - 1, getY() + getFrameHeight()) && !isCollidingSurface) {
            setySpeed(getySpeed() + playerGravity);
            //Log.d("Gravity", "falling");

        } else {
            setySpeed(0);
            //Log.d("Gravity", "still");
        }

        // moving left
        if (MotionSensor.getPitch() < -SENSITIVITY) {
            if (placeFree(getX() - 1, getY()) && placeFree(getX() - 1, getY() + getFrameHeight() - 1) && !isCollidingSide){
                setxSpeed(getxSpeed() + (MotionSensor.getPitch()+SENSITIVITY) / 20);
            }
        }

        // moving right
        if (MotionSensor.getPitch() > SENSITIVITY) {
            if (placeFree(getX() + getFrameWidth() + 1, getY()) && placeFree(getX() + getFrameWidth() + 1, getY() + getFrameHeight() - 1) && !isCollidingSide){
                setxSpeed(getxSpeed() + (MotionSensor.getPitch()-SENSITIVITY) / 20);
            }
        }

        // jumping
        if (TouchInput.onPress) {
            if (placeFree(getX(), getY() - 1) && placeFree(getX() + getFrameWidth() - 1, getY() - 1) && !isCollidingSurface) {
                setySpeed(-8);
            }
        }

        // limit to max speed
        if (getxSpeed() >= MAXXSPEED){
            setxSpeed(MAXXSPEED);
        }
        if (getxSpeed() <= -MAXXSPEED){
            setxSpeed(-MAXXSPEED);
        }

        if (getySpeed() >= MAXYSPEED){
            setySpeed(MAXYSPEED);
        }
        if (getySpeed() <= -MAXYSPEED){
            setySpeed(-MAXYSPEED);
        }

        // set horizontal friction
        //setxSpeed((1-playerFriction) * getxSpeed());

        isCollidingSide = false;
        isCollidingSurface = false;

        // round x and y position to prevent getting stuck occasionally
        setY(Math.round(getY()));
        setX(Math.round(getX()));
	}

    // handle tile collisions
    // TODO: make tile type dependant on the player!
	@Override
	public void collisionOccurred(List<TileCollision> collidedTiles)
	{
		// Do we know for certain that the for-each loop goes through the list
		// front to end?
		// If not, we have to use a different iterator!
        // ^ what the heck do you even mean by that?
		for (TileCollision tc : collidedTiles)
		{
			if (tc.theTile.getTileType() == 0)
			{

                Log.d("Collision", "colliding" + tc.collisionSide);
                moveUpToTileSide(tc);

                // stop speeds
                if (tc.collisionSide == tc.TOP || tc.collisionSide == tc.BOTTOM) {
                    setySpeed(0);
                    isCollidingSurface = true;
                }
                if ((tc.collisionSide == tc.LEFT || tc.collisionSide == tc.RIGHT)) {
                    setxSpeed(0);
                    isCollidingSide = true;
                }
				//return; // might be considered ugly by some colleagues... // I love it
			}
		}
	}

    // get player score
	public int getScore()
	{
		return score;
	}

}




// (taken out of update function)
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