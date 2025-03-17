package com.github.razorplay01.geowaremod.arkanoid.entity;

import com.github.razorplay01.geowaremod.arkanoid.ArkanoidGame;
import com.github.razorplay01.razorplayapi.util.Entity;
import com.github.razorplay01.razorplayapi.util.ScreenSide;
import com.github.razorplay01.razorplayapi.util.hitbox.Hitbox;
import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

@Getter
@Setter
public abstract class ArkanoidEntity extends Entity {
    protected float velocityX = 0;
    protected float velocityY = 0;

    // Constantes comunes
    protected float gravity = 0.5f;
    protected float maxFallSpeed = 4f;
    protected float speed = 1f;

    protected int color;

    protected final ArkanoidGame arkanoidGame;

    public ArkanoidEntity(float xPos, float yPos, float width, float height, GameScreen gameScreen, int color) {
        super(xPos, yPos, width, height, gameScreen);
        this.color = color;
        this.arkanoidGame = (ArkanoidGame) gameScreen.getGame();
        this.hitboxes.add(new RectangleHitbox("default", xPos, yPos, width, height, 0, 0, color));
    }

    public void updateHitboxes() {
        for (Hitbox hitbox : hitboxes) {
            hitbox.updatePosition(xPos, yPos);
        }
    }

    public Hitbox getDefaultHitbox() {
        return hitboxes.getFirst();
    }

    protected abstract void update();

    public void render(DrawContext context) {
        renderHitboxes(context);
    }

    public void renderHitboxes(DrawContext context) {
        for (Hitbox hitbox : hitboxes) {
            hitbox.draw(context);
        }
    }
}