package com.github.razorplay01.geowaremod.robotfactory;

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

    public RobotFactoryGame(GameScreen screen, int timeLimitSeconds, int prevScore) {
        super(screen, 5, timeLimitSeconds, prevScore);
        this.tableZone = new RectangleHitbox("table", 80, 32, 48, 48, 0, 0, 0xFF808080);
        this.trashZone = new RectangleHitbox("trash", 81, 104, 46, 12, 0, 0, 0xFF404040);
    }

    @Override
    public void init() {
        super.init();
        parts.clear();
        particles.clear();
        heldPart = null;
        tablePart = null;
        spawnScheduled = false;
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
            addScore(100);
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
        tableZone.draw(context);
        trashZone.draw(context);
        for (RobotPart part : parts) {
            part.render(context);
        }
        for (Particle particle : particles) {
            particle.render(context);
        }
        context.getMatrices().pop();
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier marcoTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/robotfactory/fondo.png");
        context.drawTexture(marcoTexture, screen.getGameScreenXPos(), screen.getGameScreenYPos(),
                getScreenWidth(), getScreenHeight(), 0, 0, 208, 116, 208, 116);
    }

    @Override
    public int getScreenWidth() {
        return 208;
    }

    @Override
    public int getScreenHeight() {
        return 116;
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
                        heldPart.setYPos(trashZone.getYPos() - 22);
                        heldPart.setVelocityX(0);
                        heldPart.setVelocityY(0.5f);
                    }
                } else {
                    heldPart.setXPos(trashZone.getXPos() + trashZone.getWidth() / 2 - heldPart.getWidth() / 2);
                    heldPart.setYPos(trashZone.getYPos() - 22);
                    heldPart.setVelocityX(0);
                    heldPart.setVelocityY(0.5f);
                }
                heldPart = null;
            }
        }
    }

    private void spawnPart() {
        RobotPart.RobotFamily family = RobotPart.RobotFamily.values()[random.nextInt(10)];
        RobotPart.PartType type = RobotPart.PartType.values()[random.nextInt(2)];
        float xPos = spawnLeft ? 7 : 185;
        parts.add(new RobotPart(family, type, xPos, 90, screen, spawnLeft));
        spawnLeft = !spawnLeft;
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
                0, 0, 32, 32, 32, 32, 1.0f);
        textures.add(texture);
        textures.add(texture);
        Animation animation = new Animation(textures, 0.5f, false);
        particles.add(new Particle(xPos + 24 - 16, yPos + 24 - 16, 32, 32, screen, animation));
    }
}