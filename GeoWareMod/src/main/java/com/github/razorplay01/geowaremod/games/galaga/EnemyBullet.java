package com.github.razorplay01.geowaremod.games.galaga;

import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

@Getter
@Setter
public class EnemyBullet {
    private float xPos;
    private float yPos;
    private float speed = 2f; // Velocidad hacia abajo
    private float targetX; // Posición X objetivo (fija al disparar)
    private final RectangleHitbox hitbox;
    private final GameScreen gameScreen;

    public EnemyBullet(float xPos, float yPos, float targetX, GameScreen gameScreen) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.targetX = targetX; // Fija la trayectoria X al disparar
        this.gameScreen = gameScreen;
        this.hitbox = new RectangleHitbox("enemy_bullet_hitbox", xPos, yPos, 2, 5, 0, 0, 0xFFFF0000); // Color rojo
    }

    public void update() {
        // Mueve hacia abajo, ajustando X hacia targetX
        float dx = targetX - xPos;
        float dy = gameScreen.getGame().getScreenHeight(); // Mueve hacia abajo (fijo)
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        if (distance > 1) {
            float[] unitVector = {dx / distance * speed, dy / distance * speed};
            xPos += unitVector[0];
            yPos += unitVector[1];
        }
        hitbox.updatePosition(xPos, yPos);
    }

    public void render(DrawContext context) {
        hitbox.draw(context);
    }

    public boolean isOffScreen() {
        float screenX = gameScreen.getGameScreenXPos();
        float screenY = gameScreen.getGameScreenYPos();
        float screenWidth = gameScreen.getGame().getScreenWidth();
        float screenHeight = gameScreen.getGame().getScreenHeight();
        return xPos < screenX || xPos > screenX + screenWidth || yPos > screenY + screenHeight;
    }

    public boolean collidesWith(Player player) {
        return hitbox.intersects(player.getHitbox());
    }
}