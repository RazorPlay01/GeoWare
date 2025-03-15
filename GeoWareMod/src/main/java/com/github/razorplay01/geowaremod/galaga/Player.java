package com.github.razorplay01.geowaremod.galaga;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.razorplayapi.util.Entity;
import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import com.github.razorplay01.razorplayapi.util.texture.Animation;
import com.github.razorplay01.razorplayapi.util.texture.Texture;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
@Setter
public class Player extends Entity {
    private final Animation IdleAnimation = new Animation(List.of(
            new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/game/galaka_a.png"), 0, 0, 16, 16, 336, 256, 1.0f))
            , 1f, false);

    private final RectangleHitbox hitbox;

    private boolean movingLeft;
    private boolean movingRight;

    private final List<Bullet> bullets = new ArrayList<>();
    private long lastShotTime = 0;
    private static final long SHOOT_DELAY = 500;

    protected Player(GameScreen gameScreen) {
        super(gameScreen.getGame().getScreen().getGameScreenXPos() + gameScreen.getGame().getScreenWidth() / 2f - 5,
                gameScreen.getGame().getScreen().getGameScreenYPos() + gameScreen.getGame().getScreenHeight() - 20f,
                10, 10, gameScreen);
        this.hitbox = new RectangleHitbox("hitbox", this.xPos, this.yPos, 10, 10, 0, 0, 0xFFFFFFFF);
        this.speed = 2f;
    }

    public void update() {
        velocityX = 0;
        if (movingLeft && !movingRight) {
            velocityX = -speed;
        } else if (movingRight && !movingLeft) {
            velocityX = speed;
        }
        xPos += velocityX;
        this.hitbox.updatePosition(this.xPos, this.yPos);
        verifyScreenBoundsCollision();
        updateBullets(); // Actualiza las balas
    }

    public void moveLeft() {
        movingLeft = true;
    }

    public void stopMovingLeft() {
        movingLeft = false;
    }

    public void moveRight() {
        movingRight = true;
    }

    public void stopMovingRight() {
        movingRight = false;
    }

    public void render(DrawContext context) {
        for (Bullet bullet : bullets) {
            bullet.render(context);
        }
        renderTexture(context, this, IdleAnimation, 0, 0);
    }

    public void shoot() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime >= SHOOT_DELAY) {
            Bullet bullet = new Bullet(xPos + hitbox.getWidth() / 2f - 1, yPos, gameScreen);
            bullets.add(bullet);
            lastShotTime = currentTime;
        }
    }

    public void updateBullets() {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.update();
            if (bullet.isOffScreen()) {
                iterator.remove();
            }
        }
    }

    public void verifyScreenBoundsCollision() {
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
        if (xPos + hitbox.getWidth() > screenX + screenWidth) {
            xPos = screenX + screenWidth - hitbox.getWidth();
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
        if (yPos + hitbox.getHeight() > screenY + screenHeight) {
            yPos = screenY + screenHeight - hitbox.getHeight();
            onScreenBoundaryCollision(ScreenSide.BOTTOM);
        }
    }

    public void onScreenBoundaryCollision(ScreenSide side) {
        if (side.equals(ScreenSide.LEFT) || side.equals(ScreenSide.RIGHT)) {
            velocityX = 0;
        } else if (side.equals(ScreenSide.TOP) || side.equals(ScreenSide.BOTTOM)) {
            velocityY = 0;
        }
    }


    @Getter
    @AllArgsConstructor
    public enum ScreenSide {
        TOP("top"),
        BOTTOM("bottom"),
        LEFT("left"),
        RIGHT("right");
        private final String side;
    }
}
