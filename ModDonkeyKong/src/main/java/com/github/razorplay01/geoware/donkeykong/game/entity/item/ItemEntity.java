package com.github.razorplay01.geoware.donkeykong.game.entity.item;

import com.github.razorplay01.geoware.donkeykong.game.entity.Entity;
import com.github.razorplay01.geoware.donkeykong.util.game.GameScreen;

public abstract class ItemEntity extends Entity {
    protected ItemEntity(float xPos, float yPos, float width, float height, GameScreen gameScreen, int debugColor) {
        super(xPos, yPos, width, height, gameScreen, debugColor);
        this.velocityX = 0;
        this.velocityY = 0;
        this.gravity = 0;
    }
}
