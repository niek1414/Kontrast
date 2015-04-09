package com.android.platformer;

import android.gameengine.kontrast.engine.GameEngine;
import android.gameengine.kontrast.input.MotionSensor;
import android.gameengine.kontrast.input.TouchInput;
import android.gameengine.kontrast.objects.GameObject;
import android.gameengine.kontrast.objects.MoveableGameObject;
import android.gameengine.kontrast.objects.collisions.ICollision;
import android.gameengine.kontrast.objects.collisions.TileCollision;
import android.gameengine.kontrast.sound.GameSound;
import android.gameengine.kontrast.tiles.Tile;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public abstract class Player extends MoveableGameObject implements ICollision
{

    // enum for the player colour
    private enum Color{
        BLACK, WHITE
    }

	//Reference to the game itself
    protected Room myroom;

	// variables
    private Color playerType = Color.BLACK;
    private int solidTile;
    protected GameSound gameSound = new GameSound();
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
    protected static double spawnX;
    protected static double spawnY;
    protected static boolean allowMovement = false;

    // update function
    @Override
    public void update()
    {
        super.update();

        // round x and y position to prevent getting stuck occasionally
        setY(Math.round(getY()));
        setX(Math.round(getX()));

        checkCollisions();
        checkGravity();

        checkMoveLeft();
        checkMoveRight();

        limitSpeed();
        setPlayerAnimation();

        inGrey();
    }

    // awesome home-made function that checks whether given position is collision free
    protected boolean placeFree(int x, int y){
        Tile myTile = getTileOnPosition(x, y);
        if (myTile != null) {
            if(TileIsSolid(myTile)){
                return false;
            }
        }
        return true;
    }

    // check whether a given tile is solid to the player
    private boolean TileIsSolid(Tile targetTile) {
        // array containing tiles
        int[] currentColorArray;

        // assign the right coloured tiles to the array
        if (playerType == Color.WHITE) {
            currentColorArray = myroom.whiteTiles;
        } else {
            currentColorArray = myroom.blackTiles;
        }

        // loop through the tile array and return whether it is solid to the player
        for (int i = 0; i < currentColorArray.length; i++) {
            if (targetTile.getTileType() == currentColorArray[i]) {
                return true;
            }
        }
        return false;
    }

    // function that handles the player entering grey tiles
    protected void inGrey(){
        // assign collided tile to a variable
        Tile myTile = getTileOnPosition(getX() + (getFrameWidth() / 2), getY() + (getFrameHeight() / 2));
        if (myTile != null) {
            if (myTile.getTileType() == 2) {
                // remove grey tile
                Tile adjacentTiles[] = getAdjacentTiles(myTile);

                // flip the tiles to a black or white tile (dependant on player colour)
                flipTiles(adjacentTiles);

                // change player colour
                if (playerType == Color.BLACK) {
                    playerType = Color.WHITE;
                    setSprite(spriteWhite, spriteFrames);
                } else {
                    playerType = Color.BLACK;
                    setSprite(spriteBlack, spriteFrames);
                }

                // change solid tile value
                if (solidTile == 0) {
                    solidTile = 1;
                } else {
                    solidTile = 0;
                }

                // set new player checkpoint
                setX(myTile.getTileX());
                setY(myTile.getTileY());
                setCheckPoint();

                //play sound
                gameSound.stopSound(2);
                gameSound.playSound(2, 0);
            }
        }
    }

    // function that finds tiles adjacent to the given tile
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

    // function that flips grey tiles into black or white tiles (dependant on the player)
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

    // function that handles bouncing
    protected void doBounce() {
        // bounce if the ySpeed is high enough
        if (placeFree(getX(), getY() - 1) && placeFree(getX() + getFrameWidth() - 1, getY() - 1) && getySpeed() > 3){
            setySpeed(-getySpeed() + BOUNCEFRICTION);
            gameSound.stopSound(0);
            gameSound.playSound(0, 0);
            Log.d("Collision", "bounce");
        } else {
            //Log.d("Collision", "no bounce");
            setySpeed(0);
            setY(getY() / getFrameHeight() * getFrameHeight()); // snap Y to prevent getting stuck
        }
    }

    // function that jumps the player back to it's checkpoint
    private void respawn() {
        setSpeed(0);
        setX(spawnX);
        setY(spawnY);
    }

    // function that handles collisions
    protected void checkCollisions() {
        // collisions with objects
        ArrayList<GameObject> gebotst = getCollidedObjects();
        if (gebotst != null) {
			for (GameObject g : gebotst) {
                // colliding with traps
				if (g instanceof Trap || g instanceof MovableTrap)
				{
                    gameSound.stopSound(1);
                    gameSound.playSound(1, 0);
                    respawn();
					Log.d("GAME", "YOU FALL ON A TRAP WITH YOUR BUTT.");
				}
                // colliding with Portals
                else if (g instanceof Portal) {
                    if (((Portal) g).getIsGoal()){
                        //level getLevelNumber() is done
                        myroom.goToRoom(0);
                    } else {
                        myroom.goToRoom(((Portal) g).getLevelNumber());
                    }
                    deleteThisGameObject();
                }
			}
        }
    }

    // function that handles gravity and jumping
    protected void checkGravity() {
        //set gravity
        if (placeFree(getX(), getY() + getFrameHeight()) && placeFree(getX() + getFrameWidth() - 1, getY() + getFrameHeight())) {
            setySpeed(getySpeed() + playerGravity);
            //Log.d("Gravity", "falling");
        } else {
            doBounce();
            if (TouchInput.onPress) {
                if (allowMovement && placeFree(getX(), getY() - 1) && placeFree(getX() + getFrameWidth() - 1, getY() - 1)) {
                    float jumpHeight = -8;
                    setySpeed(jumpHeight);
                    Log.d("PLAYER", "Default jump: " + jumpHeight);
                }
            }
        }
    }

    // handle moving left
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

    // handle moving right
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

    // limit maximum player speeds
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

    // handle player animation
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
	@Override
	public void collisionOccurred(List<TileCollision> collidedTiles) {
        // Do we know for certain that the for-each loop goes through the list
        // front to end?
        // If not, we have to use a different iterator!
        // ^ ????????????
        for (TileCollision tc : collidedTiles) {
            if (TileIsSolid(tc.theTile)) {

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
                        // bouncing against walls
                        if (getxSpeed() > 4 && placeFree(getX() - 1, getY()) && placeFree(getX() - 1, getY() + getFrameHeight() - 1)) {
                            setxSpeed(-getxSpeed());
                        } else if (getxSpeed() < -4 && placeFree(getX() + getFrameWidth() + 1, getY()) && placeFree(getX() + getFrameWidth() + 1, getY() + getFrameHeight() - 1)) {
                            setxSpeed(-getxSpeed());
                        } else {
                            setxSpeed(0);
                        }
                    }
                }
                return; // might be considered ugly by some colleagues... // =D
            }
        }
    }

    // setter for the player colour
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

    // getter for the solid tile value
    public int getSolidTile(){
        return solidTile;
    }

    // set current player position as checkpoint
    public void setCheckPoint() {
        spawnX = getX();
        spawnY = getY();
    }

    // setter to allow movement
    public static void setAllowMovement(boolean allow){
        allowMovement = allow;
    }

    // getter for allow movement
    public static boolean getAllowMovement(){
        return allowMovement;
    }
}


/***** slope code below - Cut short due to time constraints... *****/
//    private void snapToSlope(GameObject slope){
//        setySpeed(0);
//
//        int distanceX;
//        int distanceY = slope.getY() - getY();
//
//        Log.d("SLOPE", "SNAPTEST");
//        //if (distanceX >= 12){
//            if (((Slope)slope).isLeft){
//                distanceX = slope.getX() - getX();
//                if(distanceX <= distanceY){
//                    setY(getY() - getFrameHeight() - (distanceX - distanceY));
//                    Log.d("SLOPE", "Links"+(distanceX - distanceY));
//                }
//            } else {
//                distanceX = 12- (slope.getX() - getX() + getFrameWidth());
//                if(distanceX <= distanceY){
//                    setY(getY() - getFrameHeight() - (distanceX - distanceY));
//                    Log.d("SLOPE", "Rechts"+(distanceX - distanceY));
//                }
//            }
//        //}
//    }