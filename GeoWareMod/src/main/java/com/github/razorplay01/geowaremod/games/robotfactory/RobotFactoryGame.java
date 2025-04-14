package com.github.razorplay01.geowaremod.games.robotfactory;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.razorplayapi.util.GameStatus;
import com.github.razorplay01.razorplayapi.util.Particle;
import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import com.github.razorplay01.razorplayapi.util.stage.Game;
import com.github.razorplay01.razorplayapi.util.texture.Animation;
import com.github.razorplay01.razorplayapi.util.texture.Texture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RobotFactoryGame extends Game {
    private final List<RobotPart> parts = new ArrayList<>();
    private final List<Particle> particles = new ArrayList<>();
    private RobotPart heldPart = null;
    private RobotPart tablePart = null;
    private final RectangleHitbox tableZone;
    private final RectangleHitbox trashZone;
    private final Random random = new Random();
    private boolean spawnLeft = true;
    private boolean spawnScheduled = false;
    private final float scale = 2;
    private final float speedMultiplier; // Speed multiplier for part movement
    private final boolean enableRotation; // Flag to enable/disable rotation

    private final List<Texture> door_r = Texture.createTextureList(Identifier.of(GeoWareMod.MOD_ID, "textures/games/robotfactory/door_r.png"), 78, 66, 78, 1122, 17, scale, false);
    private final List<Texture> door_l = Texture.createTextureList(Identifier.of(GeoWareMod.MOD_ID, "textures/games/robotfactory/door_l.png"), 78, 66, 78, 1122, 17, scale, false);
    private final Animation doorRAnimation = new Animation(door_r, 1f, true);
    private final Animation doorLAnimation = new Animation(door_l, 1f, true);

    private final int partQuantity;

    public RobotFactoryGame(GameScreen screen, int timeLimitSeconds, int prevScore, float speedMultiplier, boolean enableRotation, int partQuantity) {
        super(screen, 5, timeLimitSeconds, prevScore);
        this.partQuantity = partQuantity;
        this.speedMultiplier = speedMultiplier;
        this.enableRotation = enableRotation;
        this.tableZone = new RectangleHitbox("table", 80 * scale, 32 * scale, 48 * scale, 48 * scale, 0, 0, 0xFF808080);
        this.trashZone = new RectangleHitbox("trash", 81 * scale, 104 * scale, 46 * scale, 12 * scale, 0, 0, 0xFF404040);
    }

    @Override
    public void init() {
        super.init();
        parts.clear();
        particles.clear();
        heldPart = null;
        tablePart = null;
        spawnScheduled = false;
        spawnLeft = true; // Reset to ensure consistent starting side
    }

    @Override
    public void update() {
        super.update();

        if (status == GameStatus.ACTIVE && !spawnScheduled) {
            scheduleSpawn();
            spawnScheduled = true;
        }

        for (RobotPart part : new ArrayList<>(parts)) {
            part.update();
            if (!part.isHeld() && trashZone.intersects(part.getHitbox())) {
                parts.remove(part);
            }
        }

        particles.removeIf(Particle::isFinished);
        particles.forEach(Particle::update);

        if (tablePart != null && heldPart != null && tableZone.intersects(heldPart.getHitbox()) &&
                tablePart.getFamily() == heldPart.getFamily() && tablePart.getType() != heldPart.getType()) {
            addScore(1);
            addCompletionParticle(tableZone.getXPos(), tableZone.getYPos(), tablePart.getFamily());
            parts.remove(tablePart);
            parts.remove(heldPart);
            tablePart = null;
            heldPart = null;
        }

        if (heldPart != null) {
            double mouseX = MinecraftClient.getInstance().mouse.getX();
            double mouseY = MinecraftClient.getInstance().mouse.getY();
            double scaledMouseX = mouseX * (MinecraftClient.getInstance().getWindow().getScaledWidth() / (double) MinecraftClient.getInstance().getWindow().getWidth());
            double scaledMouseY = mouseY * (MinecraftClient.getInstance().getWindow().getScaledHeight() / (double) MinecraftClient.getInstance().getWindow().getHeight());
            heldPart.setHeld(true, scaledMouseX - screen.getGameScreenXPos(), scaledMouseY - screen.getGameScreenYPos());
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        int xOffset = screen.getGameScreenXPos();
        int yOffset = screen.getGameScreenYPos();
        context.getMatrices().push();
        context.getMatrices().translate(xOffset, yOffset, 0);
        //tableZone.draw(context);
        //trashZone.draw(context);
        for (RobotPart part : parts) {
            part.render(context, delta);
        }
        for (Particle particle : particles) {
            particle.render(context, delta);
        }
        context.getMatrices().pop();
        doorRAnimation.update(delta);
        doorLAnimation.update(delta);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier marcoTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/robotfactory/fondo.png");
        context.drawTexture(marcoTexture, screen.getGameScreenXPos(), screen.getGameScreenYPos(),
                getScreenWidth(), getScreenHeight(), 0, 0, (int) (208 * scale), (int) (116 * scale), (int) (208 * scale), (int) (116 * scale));
        doorRAnimation.renderAnimation(context,
                (int) (screen.getGameScreenXPos() + getScreenWidth() - (78 * scale)),
                (int) (screen.getGameScreenYPos() + getScreenHeight() - (66 * scale)),
                (int) (78 * scale), (int) (66 * scale));
        doorLAnimation.renderAnimation(context,
                screen.getGameScreenXPos(),
                (int) (screen.getGameScreenYPos() + getScreenHeight() - (66 * scale)),
                (int) (78 * scale), (int) (66 * scale));
    }

    @Override
    public int getScreenWidth() {
        return (int) (208 * scale);
    }

    @Override
    public int getScreenHeight() {
        return (int) (116 * scale);
    }

    @Override
    public void handleMouseInput(double mouseX, double mouseY, int button) {
        int xOffset = screen.getGameScreenXPos();
        int yOffset = screen.getGameScreenYPos();
        mouseX -= xOffset;
        mouseY -= yOffset;

        if (button == 0) {
            if (heldPart == null) {
                for (RobotPart part : parts) {
                    if (part.getHitbox().isMouseOver(mouseX, mouseY)) {
                        heldPart = part;
                        heldPart.setHeld(true, mouseX, mouseY);
                        if (part == tablePart) tablePart = null;
                        break;
                    }
                }
            } else {
                heldPart.setHeld(false, mouseX, mouseY);
                if (tableZone.intersects(heldPart.getHitbox())) {
                    if (tablePart == null) {
                        tablePart = heldPart;
                        tablePart.setVelocityX(0);
                    } else {
                        heldPart.setXPos(trashZone.getXPos() + trashZone.getWidth() / 2 - heldPart.getWidth() / 2);
                        heldPart.setYPos(trashZone.getYPos() - 22 * scale);
                        heldPart.setVelocityX(0);
                        heldPart.setVelocityY(0.5f * scale); // Speed multiplier is applied in setVelocityY
                    }
                } else {
                    heldPart.setXPos(trashZone.getXPos() + trashZone.getWidth() / 2 - heldPart.getWidth() / 2);
                    heldPart.setYPos(trashZone.getYPos() - 22 * scale);
                    heldPart.setVelocityX(0);
                    heldPart.setVelocityY(0.5f * scale); // Speed multiplier is applied in setVelocityY
                }
                heldPart = null;
            }
        }
    }

    private void spawnPart() {
        RobotPart.RobotFamily family = RobotPart.RobotFamily.values()[random.nextInt(partQuantity)];
        RobotPart.PartType type = spawnLeft ? RobotPart.PartType.HEAD : RobotPart.PartType.BODY;
        float xPos = spawnLeft ? 7 : 185;

        float rotationAngle = 0;
        if (enableRotation) {
            int rotationChoice = random.nextInt(4); // 0, 1, 2, or 3
            rotationAngle = switch (rotationChoice) {
                case 0 -> 0;    // No rotation
                case 1 -> 90;   // 90 degrees (right)
                case 2 -> -90;  // -90 degrees (left)
                case 3 -> 180;  // 180 degrees (upside down)
                default -> 0;
            };
        }

        parts.add(new RobotPart(family, type, xPos, 90, screen, spawnLeft, scale, speedMultiplier, rotationAngle));
        spawnLeft = !spawnLeft; // Alternate the side for the next spawn

        if (status == GameStatus.ACTIVE) {
            scheduleSpawn();
        }
    }

    private void scheduleSpawn() {
        scheduleTask(this::spawnPart, 1000);
    }

    private void addCompletionParticle(float xPos, float yPos, RobotPart.RobotFamily family) {
        List<Texture> textures = new ArrayList<>();
        Texture texture = new Texture(
                Identifier.of(GeoWareMod.MOD_ID, "textures/games/robotfactory/parts/" + family.completeTexture + ".png"),
                0, 0, 32, 32, 32, 32, scale
        );
        textures.add(texture);
        textures.add(texture);
        Animation animation = new Animation(textures, 0.5f, false);
        particles.add(new Particle(xPos + 24 * scale - 16 * scale, yPos + 24 * scale - 16 * scale, 32 * scale, 32 * scale, screen, animation));
    }
}