package com.android.platformer;

import android.gameengine.kontrast.dashboard.DashboardTextView;
import android.gameengine.kontrast.engine.GameEngine;
import android.gameengine.kontrast.engine.Viewport;
import android.gameengine.kontrast.input.MotionSensor;
import android.gameengine.kontrast.input.OnScreenButtons;
import android.gameengine.kontrast.input.TouchInput;
import android.gameengine.kontrast.tiles.GameTiles;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;

public class Room extends GameEngine {


    private Player player;
    private int playerType;
    private final int DEFAULT = 0;
    private final int HOLD = 1;
    private final int LAUNCH = 2;
    private final int SQUARE = 3;

    private boolean switchTrigger = false;

	private DashboardTextView scoreDisplay;
    private long roomTimer;
    private long previousTimeMillis;

    public float roomPosition;

	@Override
	protected void initialize() {

		// Set up control mechanisms to use
		TouchInput.use = true;
		MotionSensor.use = true;
		OnScreenButtons.use = false;

        previousTimeMillis = System.currentTimeMillis();

		createTileEnvironment();

        player = new PlayerDefault(this, 0);
        playerType = DEFAULT;

        addGameObject(player, 60, 68);

        useViewport(player);
		
		// Example of how to add a Dashboard to a game
		scoreDisplay = new DashboardTextView(this);
		scoreDisplay.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
		scoreDisplay.setTextColor(Color.BLACK);
		addToDashboard(scoreDisplay);
		
		// Example of how to add an image to the dashboard.
		/*
			DashboardImageView imageDisplay = new DashboardImageView(this, "bg");
			addToDashboard(imageDisplay);
		*/
		
		createDashboard();
	}

    private void switchPlayerControl(){
        if (TouchInput.onPress && TouchInput.fingerCount == 2) {
            switchTrigger = true;
        } else if (switchTrigger && TouchInput.onRelease) {

            int playerX = player.getX();
            int playerY = player.getY();
            int solidTile = player.getSolidTile();

            deleteGameObject(player);

            switch (playerType){
                case DEFAULT:
                    player = new PlayerHold(this, solidTile);
                    playerType = HOLD;
                    break;
                case HOLD:
                    player = new PlayerLaunch(this, solidTile);
                    playerType = LAUNCH;
                    break;
                case LAUNCH:
                    player = new PlayerSquare(this, solidTile);
                    playerType = SQUARE;
                    break;
                case SQUARE:
                    player = new PlayerDefault(this, solidTile);
                    playerType = DEFAULT;
                    break;
                default: Log.e("ERROR", "SOMETHING WENT WRONG IN LINE 87 OF THE CLASS ROOM.JAVA");
            }
            addGameObject(player, playerX, playerY);
            useViewport(player);
            switchTrigger = false;
        }
    }

    public void useViewport(Player follow) {
        // Switch it on
        Viewport.useViewport = true;
        // Zoom in, 2x
        setZoomFactor(getScreenHeight() / 240);
        // Make viewport follow the player
        setPlayer(follow);
        // player will not be center screen
        setPlayerPositionOnScreen(Viewport.PLAYER_BOTTOM, Viewport.PLAYER_CENTER);
        // Determines how quickly viewport moves (see API for details)
        setPlayerPositionTolerance(0.8, 0.5);
    }

	private void createDashboard(){
		
		//this.scoreDisplay.setWidgetWidth(20);
		this.scoreDisplay.setWidgetHeight((int)(getScreenHeight() * 0.125));
		this.scoreDisplay.setWidgetBackgroundColor(Color.WHITE);
		this.scoreDisplay.setWidgetX(10);
		this.scoreDisplay.setWidgetY(10);
		// If you want to modify the layout of a dashboard widget,
		// you need to so so using its run method.
		this.scoreDisplay.run(new Runnable(){
			public void run() {
				scoreDisplay.setPadding(10, 10, 10, 10);
			}
		});
	}

	/**
	 * Create background with tiles
	 */
	private void createTileEnvironment() {
		String[] tileImagesNames = { "block_black", "block_white", "block_grey", "block_solid"};
		// layout: better not let the Eclipse formatter get at this...
		int[][] tilemap = {
                {3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3},
                {3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3},
                {3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3},
                {3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3},
                {3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3},
                {3,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3},
                {3,0,1,1,1,0,1,1,1,1,0,0,0,0,1,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,3},
                {3,0,1,1,1,0,1,1,1,0,0,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,3},
                {3,0,1,1,0,0,1,1,0,0,0,1,1,1,1,1,1,1,0,0,1,1,0,1,1,1,1,1,1,1,1,1,3},
                {3,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0,0,0,1,1,0,1,1,1,1,1,1,1,1,1,3},
                {3,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,1,1,1,1,0,0,1,0,2,2,2,0,3},
                {3,0,0,0,1,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0,3},
                {3,0,0,1,1,0,0,0,0,0,0,1,1,0,0,1,0,0,0,0,1,1,1,0,0,0,0,0,1,0,1,0,3},
                {3,0,0,0,0,0,1,0,0,0,0,1,1,0,0,1,0,0,0,0,1,1,0,0,0,0,0,0,1,0,1,0,3},
                {3,0,0,0,1,0,1,1,0,0,0,0,0,0,0,1,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,3},
                {3,0,1,0,1,0,0,0,0,1,0,0,0,1,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,3},
                {3,0,0,0,1,0,0,0,1,1,0,0,0,0,0,0,0,1,1,1,0,0,0,1,1,1,0,0,0,0,1,1,3},
                {3,0,0,1,1,0,0,1,1,1,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,3},
                {3,1,0,0,1,0,1,1,1,1,0,1,1,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1,1,1,1,3},
                {3,0,0,0,1,1,1,1,1,1,0,1,1,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,3},
                {3,2,2,2,1,1,1,0,1,1,0,1,1,0,0,1,1,0,1,1,1,1,1,1,0,1,1,1,1,1,0,1,3},
                {3,1,1,1,1,1,0,1,1,1,0,1,0,0,0,1,1,0,1,1,1,0,1,1,1,1,1,0,1,1,0,1,3},
                {3,0,0,0,0,0,1,1,1,1,0,0,0,1,1,1,1,0,1,0,0,0,0,0,0,0,0,0,1,1,0,1,3},
                {3,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,2,1,3},
                {3,1,0,1,1,1,0,0,0,1,1,1,1,1,1,0,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,3},
                {3,1,1,0,1,1,1,1,1,1,1,1,0,0,0,0,1,1,1,0,1,1,1,0,1,1,0,0,1,1,1,1,3},
                {3,0,1,0,0,1,1,1,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,3},
                {3,0,1,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,3},
                {3,0,1,1,0,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,3},
                {3,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3},
                {3,0,0,0,0,0,0,0,0,0,0,2,2,2,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,3},
                {3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {3,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {3,0,1,0,1,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
                {3,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,3},
                {3,1,1,1,1,1,0,0,0,0,0,0,0,1,1,1,1,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,3},
                {3,1,0,0,0,1,0,0,0,0,0,0,1,1,1,1,1,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,3},
                {3,0,1,1,1,0,0,0,0,0,0,0,1,1,1,1,1,1,0,0,1,1,0,0,0,1,1,1,1,1,1,0,3},
                {3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3}
		};
        int tileSize = (12);
		GameTiles myTiles = new GameTiles(tileImagesNames, tilemap, tileSize);
		setTileMap(myTiles);
		Log.d("Room", "GameTiles created");

	}

	/**
	 * Update the game. At this moment, we only need to update the Dashboard.
	 * Note: the Dashboard settings will be adjusted!!!
	 * 
	 * @see android.gameengine.kontrast.engine.GameEngine#update()
	 */
	@Override
	public void update() {
		super.update();
        roomTimer = (System.currentTimeMillis() - previousTimeMillis) / 1000;
		this.scoreDisplay.setTextString("Time: " + String.valueOf(this.roomTimer));

        switchPlayerControl();
	}
}