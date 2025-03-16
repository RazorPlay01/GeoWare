package com.github.razorplay01.geowaremod.arkanoid.entity;

import com.github.razorplay01.geowaremod.arkanoid.ArkanoidGame;
import com.github.razorplay01.razorplayapi.util.Entity;
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

    /**
     * Maneja la colisión con los bordes de la pantalla.
     */
    protected void verifyScreenBoundsCollision() {
        int screenX = gameScreen.getGame().getScreen().getGameScreenXPos();
        int screenY = gameScreen.getGame().getScreen().getGameScreenYPos();
        int screenWidth = gameScreen.getGame().getScreenWidth();
        int screenHeight = gameScreen.getGame().getScreenHeight();

        // Colisión con el borde izquierdo
        if (xPos < screenX) {
            xPos = screenX;
            onScreenBoundaryCollision(ScreenSide.LEFT);
            return;
        }

        // Colisión con el borde derecho
        if (xPos + width > screenX + screenWidth) {
            xPos = screenX + screenWidth - width;
            onScreenBoundaryCollision(ScreenSide.RIGHT);
            return;
        }

        // Colisión con el borde superior
        if (yPos < screenY) {
            yPos = screenY;
            onScreenBoundaryCollision(ScreenSide.TOP);
            return;
        }

        // Colisión con el borde inferior
        if (yPos + height > screenY + screenHeight) {
            yPos = screenY + screenHeight - height;
            onScreenBoundaryCollision(ScreenSide.BOTTOM);
        }
    }

    /**
     * Método llamado cuando la entidad colisiona con un borde de la pantalla.
     * Puede ser sobrescrito por las subclases para personalizar el comportamiento.
     *
     * @param side El lado de la pantalla con el que colisiona: "left", "right", "top", o "bottom".
     */
    protected void onScreenBoundaryCollision(ScreenSide side) {
        if (side.equals(ScreenSide.LEFT) || side.equals(ScreenSide.RIGHT)) {
            velocityX = 0;
        } else if (side.equals(ScreenSide.TOP) || side.equals(ScreenSide.BOTTOM)) {
            velocityY = 0;
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