package com.android.platformer;

import java.util.ArrayList;
import java.util.List;

import android.gameengine.kontrast.input.MotionSensor;
import android.gameengine.kontrast.input.TouchInput;
import android.gameengine.kontrast.objects.GameObject;
import android.gameengine.kontrast.objects.MoveableGameObject;
import android.gameengine.kontrast.objects.collisions.ICollision;
import android.gameengine.kontrast.objects.collisions.TileCollision;
import android.gameengine.kontrast.tiles.Tile;
import android.util.Log;


public abstract class Player extends MoveableGameObject implements ICollision
{

    private enum Color{
        BLACK, WHITE
    }

    private Color playerType = Color.BLACK;
    private int solidTile;

	//Reference to the game itself
    protected Room myroom;

	// variables
    protected double playerGravity;
    protected double playerFriction;
    protected int MAXXSPEED;
    protected int MAXYSPEED;
    protected int THRESHOLD;
    protected int SENSITIVITY;
    protected float BOUNCEFRICTION;
    protected String spriteWhite;
    protected String spriteBlack;
    protected int spriteFrames;

    protected boolean allowMovement = true;


//    Default constructor
//public Player(int solidTile)
//{
//
//}


    // awesome home-made function that checks if a given position is collision free
    // TODO: make tile type dependant on the player!
    protected boolean placeFree(int x, int y){
        Tile myTile = getTileOnPosition(x, y);
        if (myTile != null) {
            if (myTile.getTileType() == solidTile || myTile.getTileType() == 3)
                return false;
        }
        return true;
    }

    protected void inGrey(){
        Tile myTile = getTileOnPosition(getX() + (getFrameWidth() / 2), getY() + (getFrameHeight() / 2));
        if (myTile != null) {
            if (myTile.getTileType() == 2) {
                // remove grey tile
                Tile adjacentTiles[] = getAdjacentTiles(myTile);

                flipTiles(adjacentTiles);

                // change player
                if (playerType == Color.BLACK) {
                    playerType = Color.WHITE;
                    setSprite(spriteWhite, spriteFrames);
                } else {
                    playerType = Color.BLACK;
                    setSprite(spriteBlack, spriteFrames);
                }
                // change solid Tile
                if (solidTile == 0) {
                    solidTile = 1;
                } else {
                    solidTile = 0;
                }

            }
        }
    }

    protected Tile[] getAdjacentTiles(Tile myTile){
        Tile adjacentTiles[] = {
                getTileOnIndex(myTile.getTileNumberX() + 1, myTile.getTileNumberY() + 1),
                getTileOnIndex(myTile.getTileNumberX(), myTile.getTileNumberY() + 1),
                getTileOnIndex(myTile.getTileNumberX() - 1, myTile.getTileNumberY() + 1),
                getTileOnIndex(myTile.getTileNumberX() + 1, myTile.getTileNumberY()),
                getTileOnIndex(myTile.getTileNumberX(), myTile.getTileNumberY()),
                getTileOnIndex(myTile.getTileNumberX() - 1, myTile.getTileNumberY()),
                getTileOnIndex(myTile.getTileNumberX() + 1, myTile.getTileNumberY() - 1),
                getTileOnIndex(myTile.getTileNumberX(), myTile.getTileNumberY() - 1),
                getTileOnIndex(myTile.getTileNumberX() - 1, myTile.getTileNumberY() - 1)
        };
        return adjacentTiles;
    }

    protected void flipTiles(Tile adjacentTiles[]){
        for (int i = 0; i < adjacentTiles.length; i++) {
            if (adjacentTiles[i] != null) {
                if (adjacentTiles[i].getTileType() == 2) {
                    adjacentTiles[i].setTileType(solidTile);
                    flipTiles(getAdjacentTiles(adjacentTiles[i]));
                }
            }
        }
    }

    protected void doBounce() {
        if (placeFree(getX(), getY() - 1) && placeFree(getX() + getFrameWidth() - 1, getY() - 1) && getySpeed() > 3){
            setySpeed(-getySpeed() + BOUNCEFRICTION);
            Log.d("Collision", "bounce");
        } else {
            //Log.d("Collision", "no bounce");
            setySpeed(0);
            setY(getY() / getFrameHeight() * getFrameHeight()); // snap Y to prevent getting stuck
        }
    }

    // handle collisions and movement
	@Override
	public void update()
	{
        super.update();

        // round x and y position to prevent getting stuck occasionally
        setY(Math.round(getY()));
        setX(Math.round(getX()));

        //setySpeed(Math.round(getySpeed()));
        //setxSpeed(Math.round(getxSpeed()));

        checkCollisions();

		// Handle input. Both on screen buttons and tilting are supported.
		// Buttons take precedence.
		//boolean buttonPressed = false;

        checkGravity();

        checkMoveLeft();
        checkMoveRight();

//        //ceiling collision
//        if (getySpeed() < 0) {
//            if (!placeFree(getX(), getY() - (int)getySpeed()) && !placeFree(getX() + getFrameWidth() - 1, getY() - (int)getySpeed())) {
//                Log.d("Collision", "ceiling");
//                setySpeed(0);
//                setY(getY() / getFrameHeight() * getFrameHeight()); // snap Y to prevent getting stuck
//            }
//        }
        limitSpeed();

        setPlayerAnimation();

        inGrey();

	}

    protected void checkCollisions() {
        // collisions with objects
        ArrayList<GameObject> gebotst = getCollidedObjects();
        if (gebotst != null) {
//			for (GameObject g : gebotst)
//			{
//				if (g instanceof Trap)
//				{
//					Log.d("YOU FALL", "ON A TRAP WITH YOUR BUTT.");
//                    myroom.deleteGameObject(g);
//				} //else if (g instanceof SOMTING ALLLLESSSSSSSSSSSSSS)
////				{
////					// Log.d("Gepakt", "Ai, wat nu...");
////				}
//			}
        }
    }

    protected void checkGravity() {
        //set gravity
        if (placeFree(getX(), getY() + getFrameHeight()) && placeFree(getX() + getFrameWidth() - 1, getY() + getFrameHeight())) {
            setySpeed(getySpeed() + playerGravity);
            //Log.d("Gravity", "falling");
        } else {
            doBounce();

            if (TouchInput.onPress) {
                if (placeFree(getX(), getY() - 1) && placeFree(getX() + getFrameWidth() - 1, getY() - 1)) {
                    float jumpHeight = -8;
                    setySpeed(jumpHeight);
                    Log.e("HI", "Default jump: " + jumpHeight);
                }
            }
        }
    }

    protected void checkMoveLeft() {
        // moving left
        if (placeFree(getX() - 1, getY()) && placeFree(getX() - 1, getY() + getFrameHeight() - 1) && allowMovement) {
            if (MotionSensor.getPitch() < -THRESHOLD) {
                setxSpeed(getxSpeed() + (MotionSensor.getPitch() + THRESHOLD) / SENSITIVITY);
            }

            // set horizontal friction
            setxSpeed((1 - playerFriction) * getxSpeed());
        }
    }

    protected void checkMoveRight() {
        // moving right
        if (placeFree(getX() + getFrameWidth() + 1, getY()) && placeFree(getX() + getFrameWidth() + 1, getY() + getFrameHeight() - 1) && allowMovement) {
            if (MotionSensor.getPitch() > THRESHOLD) {
                setxSpeed(getxSpeed() + (MotionSensor.getPitch() - THRESHOLD) / SENSITIVITY);
            }

            // set horizontal friction
            setxSpeed((1 - playerFriction) * getxSpeed());
        }
    }

    protected void limitSpeed() {
        // limit to max speed
        if (getxSpeed() >= MAXXSPEED) {
            setxSpeed(MAXXSPEED);
        }
        if (getxSpeed() <= -MAXXSPEED) {
            setxSpeed(-MAXXSPEED);
        }

        if (getySpeed() >= MAXYSPEED) {
            setySpeed(MAXYSPEED);
        }
        if (getySpeed() <= -MAXYSPEED) {
            setySpeed(-MAXYSPEED);
        }
    }

    protected void setPlayerAnimation() {
        //animations
        if (getX() != getPrevX()) {
            setAnimationSpeed(MAXXSPEED - Math.abs((int) getxSpeed()));
            //Log.d("ANIMATION", "aniSpeed: " + (MAXXSPEED - Math.abs((int) getxSpeed())));
        } else {
            setAnimationSpeed(-1);
        }
    }

    // handle tile collisions
    // TODO: make tile type dependant on the player!
	@Override
	public void collisionOccurred(List<TileCollision> collidedTiles) {
        // Do we know for certain that the for-each loop goes through the list
        // front to end?
        // If not, we have to use a different iterator!
        // ^ what the heck do you even mean by that?
        for (TileCollision tc : collidedTiles) {
            if (tc.theTile.getTileType() == solidTile || tc.theTile.getTileType() == 3) {

                Log.d("Collision", "colliding " + tc.collisionSide);

                moveUpToTileSide(tc);

                // stop speeds
                if (tc.collisionSide == tc.TOP) {
                    //doBounce();
                } else if (tc.collisionSide == tc.BOTTOM) {
                    setySpeed(0);
                } else if ((tc.collisionSide == tc.LEFT || tc.collisionSide == tc.RIGHT)) {
                    if (Math.abs(getxSpeed()) < 4){
                        setxSpeed(0);
                    } else {
                        if (getxSpeed() > 4 && placeFree(getX() - 1, getY()) && placeFree(getX() - 1, getY() + getFrameHeight() - 1)) {
                            setxSpeed(-getxSpeed());
                        } else if (getxSpeed() < -4 && placeFree(getX() + getFrameWidth() + 1, getY()) && placeFree(getX() + getFrameWidth() + 1, getY() + getFrameHeight() - 1)) {
                            setxSpeed(-getxSpeed());
                        } else {
                            setxSpeed(0);
                        }
                    }
                }
                return; // might be considered ugly by some colleagues... // I love it
            }
        }
    }
    protected void setPlayerColor(int solidTile) {
        this.solidTile = solidTile;
        if (solidTile == 0) {
            playerType = Color.BLACK;
            setSprite(spriteBlack, spriteFrames);
        } else {
            playerType = Color.WHITE;
            setSprite(spriteWhite, spriteFrames);
        }
    }

    public int getSolidTile(){
        return solidTile;
    }
}




// (taken out of update function)
// Example of how to use the touch screen..
// To use this, comment out the input from OnScreenButtons and MotionSensor
// and switch the use-settings in class Room
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