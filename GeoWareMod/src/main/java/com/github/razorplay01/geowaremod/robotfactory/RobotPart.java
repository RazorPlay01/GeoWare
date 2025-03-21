package com.github.razorplay01.geowaremod.robotfactory;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.razorplayapi.util.Entity;
import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import com.github.razorplay01.razorplayapi.util.texture.Texture;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class RobotPart extends Entity {
    @Getter
    private final RobotFamily family;
    @Getter
    private final PartType type;
    private boolean isHeld;
    private final Texture texture;

    public enum RobotFamily {
        SHIP1("nave1.2.16x", "nave1.3.16x", "nave1.32x"),
        SHIP2("nave2.2.16x", "nave2.3.16x", "nave2.32x"),
        SHIP3("nave3.2.16x", "nave3.3.16x", "nave3.32x"),
        SHIP4("nave4.2.16x", "nave4.3.16x", "nave4.32x"),
        SHIP5("nave5.2.16x", "nave5.3.16x", "nave5.32x"),
        SHIP6("nave6.2.16x", "nave6.3.16x", "nave6.32x"),
        SHIP7("nave7.2.16x", "nave7.3.16x", "nave7.32x"),
        SHIP8("nave8.2.16x", "nave8.3.16x", "nave8.32x"),
        SHIP9("nave9.2.16x", "nave9.3.16x", "nave9.32x"),
        SHIP10("nave10.2.16x", "nave10.3.16x", "nave10.32x");

        public final String headTexture;
        public final String bodyTexture;
        public final String completeTexture;

        RobotFamily(String headTexture, String bodyTexture, String completeTexture) {
            this.headTexture = headTexture;
            this.bodyTexture = bodyTexture;
            this.completeTexture = completeTexture;
        }
    }

    public enum PartType {
        HEAD, BODY
    }

    public RobotPart(RobotFamily family, PartType type, float xPos, float yPos, GameScreen gameScreen, boolean spawnedLeft) {
        super(xPos, yPos, 16, 16, gameScreen);
        this.family = family;
        this.type = type;

        String textureName = (type == PartType.HEAD) ? family.headTexture : family.bodyTexture;
        this.texture = new Texture(
                Identifier.of(GeoWareMod.MOD_ID, "textures/games/robotfactory/parts/" + textureName + ".png"),
                0, 0, 16, 16, 16, 16, 1.0f
        );

        this.hitboxes.add(new RectangleHitbox("robot_part", xPos, yPos, 16, 16, 0, 0, 0xFFFFFFFF));
        this.velocityX = spawnedLeft ? 1f : -1f; // Left spawn: move right, Right spawn: move left
        this.velocityY = 0.0f;
        this.isHeld = false;
    }

    @Override
    protected void update() {
        if (!isHeld) {
            xPos += velocityX;
            yPos += velocityY;
            if (xPos < 0) xPos = 0;
            if (xPos > (gameScreen.getGame().getScreenWidth() - width))
                xPos = gameScreen.getGame().getScreenWidth() - width;
            hitboxes.get(0).updatePosition(xPos, yPos);
        }
    }

    @Override
    protected void render(DrawContext context) {
        context.drawTexture(
                texture.identifier(),
                (int) xPos, (int) yPos,
                (int) width, (int) height,
                texture.u(), texture.v(),
                texture.width(), texture.height(),
                texture.textureWidth(), texture.textureHeight()
        );
    }

    public void setHeld(boolean held, double mouseX, double mouseY) {
        this.isHeld = held;
        if (held) {
            xPos = (float) mouseX - width / 2;
            yPos = (float) mouseY - height / 2;
            velocityX = 0;
            velocityY = 0;
            hitboxes.get(0).updatePosition(xPos, yPos);
        }
    }

    public RectangleHitbox getHitbox() {
        return (RectangleHitbox) hitboxes.get(0);
    }

    public boolean isHeld() {
        return isHeld;
    }
}