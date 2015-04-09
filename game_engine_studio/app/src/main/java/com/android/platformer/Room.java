package com.android.platformer;

import android.gameengine.kontrast.dashboard.DashboardTextView;
import android.gameengine.kontrast.engine.GameEngine;
import android.gameengine.kontrast.engine.Viewport;
import android.gameengine.kontrast.input.MotionSensor;
import android.gameengine.kontrast.input.OnScreenButtons;
import android.gameengine.kontrast.input.TouchInput;
import android.gameengine.kontrast.tiles.GameTiles;
import android.gameengine.kontrast.tiles.Tile;
import android.gameengine.kontrast.sound.MusicPlayer;
import android.gameengine.kontrast.sound.GameSound;
import android.util.Log;

public class Room extends GameEngine {

    private int roomNumber;

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

    public int[] blackTiles;
    public int[] whiteTiles;

    private RoomData_1 roomData_1;
    private RoomData_2 roomData_2;

    private MusicPlayer musicPlayer;
    public SoundControl soundControl;

    private String[] ambientSound = {
            "startup_ambience",
            "level_1_ambience",
            "level_2_ambience",
            "level_3_ambience",
            "level_4_ambience",
            "level_5_ambience",
            "level_4_ambience",
            "level_4_ambience",
            "level_4_ambience",
            "level_4_ambience",
            "level_4_ambience"
    };


	@Override
	protected void initialize() {
        soundControl = new SoundControl(this);
        soundControl.initializeSound();

        Splash startSplash = new Splash(this);
        addGameObject(startSplash, 0, 0);
        player.setAllowMovement(false);

        roomNumber = 0;

		// Set up control mechanisms to use
		TouchInput.use = true;
		MotionSensor.use = true;
		OnScreenButtons.use = false;

        previousTimeMillis = System.currentTimeMillis();

        createRoomEnvironment();
	}



    private void resetToMainRoom(){
        if (TouchInput.onPress && TouchInput.fingerCount == 4 && roomNumber != 0) {
            goToRoom(0);
        }
    }

    // handle switching player types
    private void switchPlayerControl(){
        if (TouchInput.onPress && TouchInput.fingerCount == 2) {
            switchTrigger = true;
        } else if (switchTrigger && TouchInput.onRelease && player.getAllowMovement() == true) {

            // play sound
            soundControl.gameSound.stopSound(10);
            soundControl.gameSound.playSound(10, 0);

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
            ((Player)getPlayer()).setAllowMovement(true);
            addGameObject(player, playerX, playerY);
            useViewport(player);
            switchTrigger = false;
        }
    }

    // function that clears the room of all it's objects
    private void destroyRoom(){
        deleteAllGameObjectsOfType(Trap.class);
        deleteAllGameObjectsOfType(MovableTrap.class);
        deleteAllGameObjectsOfType(Portal.class);
        deleteAllGameObjectsOfType(Tile.class);
        deleteAllGameObjectsOfType(Sign.class);
        deleteGameObject(player);
        player.setAllowMovement(true);
    }

    // function that goes to the given room
    public void goToRoom(int roomNumber){
        this.roomNumber = roomNumber;
        destroyRoom();
        createRoomEnvironment();
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

	/**
	 * Create background with tiles
	 */
	private void createRoomEnvironment() {
		String[] tileImagesNames = { "block_black", "block_white", "block_grey", "block_solid", "block_white", "block_black", "block_white", "block_black", "block_white", "block_black",  "portal",
                                    //black block    white block    grey block    solid block   player black   player white   trap black      trap white    moveTrap black  moveTrap white finish portal
                                    //0              1              2             3             4              5              6               7             8               9              10

                                    "dummy",        "dummy",        "dummy",     "dummy",       "dummy",       "dummy",       "dummy",        "dummy",      "dummy",        "dummy",
                            //level: 1               2               3            4              5              6              7               8             9               10
                            //      11              12              13           14             15              16            17              18            19               20

                                    "dummy",        "dummy",        "dummy",     "dummy",       "dummy",       "dummy",      "dummy",        "dummy",       "dummy",        "dummy",
                            //level: 1               2               3            4              5              6              7               8             9               10
                            //      21              22              23           24             25              26            27              28            29               30

        };
        blackTiles = new int[]{0,3,5,7,9};
        whiteTiles = new int[]{1,3,4,6,8};

        // retrieve room data
        // retrieve room data
        int[][][] tilemap;
        int offset;
        if (roomNumber <= 5) {
            tilemap = roomData_1.getRoomData();
            offset = 0;
        } else {
            tilemap = roomData_2.getRoomData();
            offset = 5;
        }

        // set tile size
        int tileSize = (12);

        // initialize tiles
		GameTiles myTiles = new GameTiles(tileImagesNames, tilemap[roomNumber - offset], tileSize);
		setTileMap(myTiles);

        // initialize game objects
        for (int i = 0; i < tilemap[roomNumber - offset].length; i++){
            for (int j = 0; j < tilemap[roomNumber - offset][i].length; j++){

                if (tilemap[roomNumber - offset][i][j] > 10 && tilemap[roomNumber - offset][i][j] < 21) { // portals
                    Portal levelPortal = new Portal(false, tilemap[roomNumber - offset][i][j] - 10);
                    addGameObject(levelPortal, j * tileSize, i * tileSize);
                } else if (tilemap[roomNumber - offset][i][j] > 20 && tilemap[roomNumber - offset][i][j] < 31) { // level sign
                    Sign levelSign = new Sign(tilemap[roomNumber - offset][i][j] - 20);
                    addGameObject(levelSign, j * tileSize, i * tileSize);
                }
                switch (tilemap[roomNumber - offset][i][j]) {
                    case 4: // player black
                        player = new PlayerDefault(this, 0);
                        playerType = DEFAULT;
                        addGameObject(player, j * tileSize, i * tileSize);
                        player.setCheckPoint();
                        useViewport(player);
                        break;
                    case 5: // player white
                        player = new PlayerDefault(this, 1);
                        playerType = DEFAULT;
                        addGameObject(player, j * tileSize, i * tileSize);
                        player.setCheckPoint();
                        useViewport(player);
                        break;
                    case 6: // trap black
                        Trap trapBlack = new Trap(this, 0);
                        addGameObject(trapBlack, j * tileSize, i * tileSize);
                        trapBlack.setAppearance();
                        break;
                    case 7: // trap white
                        Trap trapWhite = new Trap(this, 1);
                        addGameObject(trapWhite, j * tileSize, i * tileSize);
                        trapWhite.setAppearance();
                        break;
                    case 8: // movable trap black
                        MovableTrap movableTrapBlack = new MovableTrap(this, 0);
                        addGameObject(movableTrapBlack, j * tileSize, i * tileSize);
                        movableTrapBlack.setAppearance();
                        break;
                    case 9: // movable trap white
                        MovableTrap movableTrapWhite = new MovableTrap(this, 1);
                        addGameObject(movableTrapWhite, j * tileSize, i * tileSize);
                        movableTrapWhite.setAppearance();
                        break;
                    case 10: // finish
                        Portal goalPortal = new Portal(true, roomNumber);
                        addGameObject(goalPortal, j * tileSize, i * tileSize);
                        break;
                }
            }
        }
        //set ambient sound
        musicPlayer.stop();
        musicPlayer.play(ambientSound[roomNumber], true);

		Log.d("ROOM", "GameTiles and objects created");
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
        //score == roomTimer = (System.currentTimeMillis() - previousTimeMillis) / 1000;
        switchPlayerControl();
        resetToMainRoom();
	}

}