package com.github.razorplay01.geowaremod.donkeykong.game.entity.item;

import com.github.razorplay01.geowaremod.donkeykong.game.entity.DonkeyKongEntity;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import net.minecraft.client.gui.DrawContext;

public abstract class ItemEntity extends DonkeyKongEntity {
    protected ItemEntity(float xPos, float yPos, float width, float height, GameScreen gameScreen, int debugColor) {
        super(xPos, yPos, width, height, gameScreen, debugColor);
        this.velocityX = 0;
        this.velocityY = 0;
        this.gravity = 0;
    }

    public abstract void render(DrawContext context);
}
