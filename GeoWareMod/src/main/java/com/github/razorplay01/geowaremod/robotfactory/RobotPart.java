package com.github.razorplay01.geowaremod.robotfactory;

import com.github.razorplay01.razorplayapi.util.Entity;
import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;

public class RobotPart extends Entity {
    @Getter
    private final RobotFamily family;
    @Getter
    private final PartType type;
    private boolean isHeld;

    public enum RobotFamily {
        RED(0xFFFF0000), BLUE(0xFF0000FF), GREEN(0xFF00FF00), PURPLE(0xFF800080);
        public final int color;
        RobotFamily(int color) { this.color = color; }
    }

    public enum PartType {
        HEAD, BODY
    }

    public RobotPart(RobotFamily family, PartType type, float xPos, float yPos, GameScreen gameScreen) {
        super(xPos, yPos, 32, 32, gameScreen);
        this.family = family;
        this.type = type;
        this.hitboxes.add(new RectangleHitbox("robot_part", xPos, yPos, 32, 32, 0, 0, family.color));
        this.velocityX = (xPos < 200) ? 1f : -1f;
        this.velocityY = 0.0f; // Inicialmente sin movimiento vertical
        this.isHeld = false;
    }

    @Override
    protected void update() {
        if (!isHeld) {
            xPos += velocityX;
            yPos += velocityY; // Aplicar movimiento vertical
            // Limitar posición dentro del área de juego
            if (xPos < 0) xPos = 0;
            if (xPos > 368) xPos = 368; // 400 - 32
            hitboxes.get(0).updatePosition(xPos, yPos);
        }
    }

    @Override
    protected void render(DrawContext context) {
        hitboxes.get(0).draw(context);
    }

    public void setHeld(boolean held, double mouseX, double mouseY) {
        this.isHeld = held;
        if (held) {
            xPos = (float) mouseX - width / 2;
            yPos = (float) mouseY - height / 2;
            velocityX = 0; // Detener movimiento mientras está sostenida
            velocityY = 0;
            hitboxes.get(0).updatePosition(xPos, yPos);
        }
    }

    public RectangleHitbox getHitbox() { return (RectangleHitbox) hitboxes.get(0); }
    public boolean isHeld() { return isHeld; }
}