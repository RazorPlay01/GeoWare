package com.github.razorplay01.geowaremod.donkeykong.game.entity;

import com.github.razorplay01.geowaremod.donkeykong.DonkeyKongGame;
import com.github.razorplay01.geowaremod.donkeykong.game.mapobject.Ladder;
import com.github.razorplay01.geowaremod.donkeykong.game.mapobject.Platform;
import com.github.razorplay01.razorplayapi.util.ScreenSide;
import com.github.razorplay01.razorplayapi.util.Entity;
import com.github.razorplay01.razorplayapi.util.hitbox.Hitbox;
import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class DonkeyKongEntity extends Entity {
    protected boolean isOnLadder = false;
    protected Ladder currentLadder;
    protected Platform currentPlatform;

    protected GameScreen gameScreen;
    protected DonkeyKongGame game;

    // Constantes comunes
    protected float gravity = 1.5f;
    protected float maxFallSpeed = 12f;

    protected DonkeyKongEntity(float xPos, float yPos, float width, float height, GameScreen gameScreen, int debugColor) {
        super(xPos, yPos, width, height, gameScreen);
        this.speed = 3f;
        this.gameScreen = gameScreen;
        this.game = (DonkeyKongGame) gameScreen.getGame();
        this.hitboxes.add(new RectangleHitbox(Hitbox.HITBOX_DEFAULT, xPos, yPos, width, height, 0, 0, debugColor));
    }

    public void updateHitboxes() {
        for (Hitbox hitbox : hitboxes) {
            hitbox.updatePosition(xPos, yPos);
        }
    }

    public Hitbox getHitboxByName(String name) {
        for (Hitbox hitbox : hitboxes) {
            if (hitbox.getHitboxId().equals(name)) return hitbox;
        }
        return null;
    }

    public Hitbox getDefaultHitbox() {
        return hitboxes.getFirst();
    }

    protected abstract void update();
}