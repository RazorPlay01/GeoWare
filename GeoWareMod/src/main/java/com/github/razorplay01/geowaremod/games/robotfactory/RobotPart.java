package com.github.razorplay01.geowaremod.games.robotfactory;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.razorplayapi.util.Entity;
import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import com.github.razorplay01.razorplayapi.util.texture.Texture;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class RobotPart extends Entity {
    @Getter
    private final RobotFamily family;
    @Getter
    private final PartType type;
    private boolean isHeld;
    private final Texture texture;
    private final float scale;
    private final float speedMultiplier;
    private final float rotationAngle; // Rotation in degrees (0, 90, -90, or 180)

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

    public RobotPart(RobotFamily family, PartType type, float xPos, float yPos, GameScreen gameScreen, boolean spawnedLeft, float scale, float speedMultiplier, float rotationAngle) {
        super(xPos * scale, yPos * scale, 16 * scale, 16 * scale, gameScreen);
        this.family = family;
        this.type = type;
        this.scale = scale;
        this.speedMultiplier = speedMultiplier;
        this.rotationAngle = rotationAngle;

        String textureName = (type == PartType.HEAD) ? family.headTexture : family.bodyTexture;
        this.texture = new Texture(
                Identifier.of(GeoWareMod.MOD_ID, "textures/games/robotfactory/parts/" + textureName + ".png"),
                0, 0, (int) (16 * scale), (int) (16 * scale), 16, 16, 1.0f
        );

        this.hitboxes.add(new RectangleHitbox("robot_part", xPos * scale, yPos * scale, 16 * scale, 16 * scale, 0, 0, 0xFFFFFFFF));
        this.velocityX = (spawnedLeft ? 1f : -1f) * speedMultiplier * scale; // Apply speed multiplier to initial movement
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
    protected void render(DrawContext context, float delta) {
        // Push a new matrix onto the stack to apply rotation
        context.getMatrices().push();

        // Translate to the center of the part to rotate around its center
        float centerX = xPos + width / 2;
        float centerY = yPos + height / 2;
        context.getMatrices().translate(centerX, centerY, 0f);

        // Apply the rotation (in degrees)
        context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotationAngle));

        // Translate back to the original position
        context.getMatrices().translate(-centerX, -centerY, 0f);

        // Render the texture
        context.drawTexture(
                texture.identifier(),
                (int) xPos, (int) yPos,
                (int) width, (int) height,
                0, 0, // Use full texture region
                16, 16, // Source texture dimensions
                16, 16  // Actual texture file dimensions
        );

        // Pop the matrix to restore the original state
        context.getMatrices().pop();
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

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY * speedMultiplier; // Apply speed multiplier to vertical movement (for discarding)
    }

    public RectangleHitbox getHitbox() {
        return (RectangleHitbox) hitboxes.get(0);
    }

    public boolean isHeld() {
        return isHeld;
    }
}