package com.android.platformer;

/**
 * Created by ERWIN on 6-4-2015
 * Project name is Kontrast in com.android.platformer
 */
public class MovableTrap extends Trap{
    private boolean isFalling;

    public MovableTrap (Room myroom, int trapColor) {
        super(myroom,trapColor);
        isFalling = false;
    }

    private void valNaarBenedenWanneerSpelerDichtbijIs() {
        double playerX = myroom.getPlayer().getX();
        double playerY = myroom.getPlayer().getY();
        if (!isFalling) {
            if (playerX > getX() - getFrameWidth() && playerX < getX() + (getFrameWidth() * 2) &&
                playerY > getY() && playerY < getY() + 120) {
                setySpeed(2);
                isFalling = true;
            }
        } else {
            setySpeed(getySpeed() + 0.2);
        }
    }
    @Override
    public void update() {
        super.update();

        valNaarBenedenWanneerSpelerDichtbijIs();
    }
}
