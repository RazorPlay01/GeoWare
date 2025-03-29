package com.github.razorplay01.geowaremod.games.bubblepuzzle;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.razorplayapi.util.GameStatus;
import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import com.github.razorplay01.razorplayapi.util.texture.Texture;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

import java.util.List;
import java.util.Random;

@Getter
public class Player {
    private float x, y;
    private float width, height;
    private float angle;
    private Bubble currentBubble;
    private Bubble nextBubble;
    private RectangleHitbox hitbox;
    private GameScreen screen;
    private Random random = new Random();

    public Player(float x, float y, float width, float height, GameScreen screen) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.angle = 90;
        this.screen = screen;
        this.hitbox = new RectangleHitbox("player", x, y, width, height, 0, 0, 0xFF00FF00);
        this.currentBubble = new Bubble(x + width / 2f, y + height / 2f, 10, generateRandomColor(), screen);
        this.nextBubble = new Bubble(x + width / 2f, y + height / 2f, 10, generateRandomColor(), screen);
    }

    public void rotateLeft() {
        this.angle += 2;
        if (this.angle >= 180) this.angle -= 180;
    }

    public void rotateRight() {
        this.angle -= 2;
        if (this.angle < 0) this.angle += 180;
    }

    public Bubble shoot() {
        List<Bubble> bubbles = ((BubblePuzzleGame) screen.getGame()).getBubbles();
        boolean isAnyBubbleMoving = bubbles.stream().anyMatch(bubble -> bubble.isMoving() || bubble.isFalling());

        if (screen.getGame().getStatus() == GameStatus.ACTIVE && !isAnyBubbleMoving) {
            currentBubble.launch(angle, 10);
            Bubble shotBubble = currentBubble;

            currentBubble = nextBubble;
            currentBubble.setX(x + width / 2f);
            currentBubble.setY(y + height / 2f);

            nextBubble = new Bubble(x + width / 2f, y + height / 2f, 10, generateRandomColor(), screen);
            this.angle = 90;
            return shotBubble;
        }
        return null;
    }

    private Texture generateRandomColor() {
        BubbleColor[] colors = BubbleColor.values();
        return colors[random.nextInt(colors.length)].getTexture();
    }

    public void draw(DrawContext context) {
        currentBubble.draw(context); // La visibilidad se controla en BubblePuzzleGame
    }

    public void drawCannon(DrawContext context) {
        float centerX = x + width / 2f;
        float centerY = y + height / 2f;
        int yOffset = -15;

        Texture arrow = new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/puntero.png"),
                120, 263, 11, 106, 255, 415, 0.8f);
        int arrowWidth = (int) (arrow.width() * arrow.scale());
        int arrowHeight = (int) (arrow.height() * arrow.scale());

        context.getMatrices().push();
        float arrowPivotY = centerY + yOffset / 2;
        context.getMatrices().translate(centerX, arrowPivotY, 0);
        context.getMatrices().multiply(new Quaternionf().rotateZ((float) Math.toRadians(-angle + 90)));
        int arrowX = -arrowWidth / 2;
        int arrowY = -arrowHeight;
        context.drawTexture(
                arrow.identifier(),
                arrowX, arrowY,
                arrowWidth, arrowHeight,
                arrow.u(), arrow.v(),
                arrow.width(), arrow.height(),
                arrow.textureWidth(), arrow.textureHeight()
        );
        context.getMatrices().pop();

        Texture cannon = new Texture(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/cannon.png"),
                27, 29, 48, 49, 100, 100, 0.8f);
        int renderWidth = (int) (cannon.width() * cannon.scale());
        int renderHeight = (int) (cannon.height() * cannon.scale());

        context.getMatrices().push();
        float pivotOffset = renderHeight * 0.25f;
        float pivotY = centerY + pivotOffset + yOffset;
        context.getMatrices().translate(centerX, pivotY, 0);
        context.getMatrices().multiply(new Quaternionf().rotateZ((float) Math.toRadians(-angle + 90)));
        int textureX = -renderWidth / 2;
        int textureY = -(int) (renderHeight - pivotOffset);
        context.drawTexture(
                cannon.identifier(),
                textureX, textureY,
                renderWidth, renderHeight,
                cannon.u(), cannon.v(),
                cannon.width(), cannon.height(),
                cannon.textureWidth(), cannon.textureHeight()
        );
        context.getMatrices().pop();
    }
}