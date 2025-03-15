package com.github.razorplay01.geowaremod.galaga;

import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

@Getter
@Setter
public class Bullet {
    private float xPos;
    private float yPos;
    private float speed = 3f; // Velocidad de la bala hacia arriba
    private final RectangleHitbox hitbox;
    private final GameScreen gameScreen;

    public Bullet(float xPos, float yPos, GameScreen gameScreen) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.gameScreen = gameScreen;
        this.hitbox = new RectangleHitbox("bullet_hitbox", xPos, yPos, 2, 5, 0, 0, 0xFFFFFF00); // Color amarillo
    }

    public void update() {
        yPos -= speed; // Mueve la bala hacia arriba
        hitbox.updatePosition(xPos, yPos);
    }

    public void render(DrawContext context) {
        hitbox.draw(context);
    }

    public boolean isOffScreen() {
        return yPos < gameScreen.getGame().getScreen().getGameScreenYPos();
    }
}