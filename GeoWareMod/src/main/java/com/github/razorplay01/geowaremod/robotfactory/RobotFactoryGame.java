package com.github.razorplay01.geowaremod.robotfactory;

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
        this.tableZone = new RectangleHitbox("table", 160, 110, 80, 80, 0, 0, 0xFF808080);
        this.trashZone = new RectangleHitbox("trash", 170, 280, 60, 20, 0, 0, 0xFF404040);
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

        // Actualizar piezas
        for (RobotPart part : new ArrayList<>(parts)) {
            part.update();
            if (!part.isHeld() && trashZone.intersects(part.getHitbox())) {
                parts.remove(part);
            }
        }

        // Actualizar partículas
        particles.removeIf(Particle::isFinished);
        particles.forEach(Particle::update);

        // Combinar piezas en la mesa
        if (tablePart != null && heldPart != null && tableZone.intersects(heldPart.getHitbox()) &&
                tablePart.getFamily() == heldPart.getFamily() && tablePart.getType() != heldPart.getType()) {
            addScore(10, tableZone.getXPos(), tableZone.getYPos());
            addCompletionParticle(tableZone.getXPos(), tableZone.getYPos());
            parts.remove(tablePart);
            parts.remove(heldPart);
            tablePart = null;
            heldPart = null;
        }


        // Actualizar posición de la pieza sostenida
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
        int xOffset = screen.getGameScreenXPos();
        int yOffset = screen.getGameScreenYPos();
        context.fill(xOffset, yOffset, xOffset + getScreenWidth(), yOffset + getScreenHeight(), 0xFF000000);
    }

    @Override
    public int getScreenWidth() {
        return 400;
    }

    @Override
    public int getScreenHeight() {
        return 300;
    }

    @Override
    public void handleMouseInput(double mouseX, double mouseY, int button) {
        int xOffset = screen.getGameScreenXPos();
        int yOffset = screen.getGameScreenYPos();
        mouseX -= xOffset;
        mouseY -= yOffset;

        if (button == 0) { // Clic izquierdo
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
                        tablePart.setVelocityX(0); // Detener movimiento en la mesa
                    } else {
                        // Mesa llena: teletransportar por encima de la basura
                        heldPart.setXPos(trashZone.getXPos() + trashZone.getWidth() / 2 - heldPart.getWidth() / 2); // Centrar en X
                        heldPart.setYPos(trashZone.getYPos() - 50); // 50 píxeles por encima
                        heldPart.setVelocityX(0); // Detener movimiento horizontal
                        heldPart.setVelocityY(0.5f); // Caer hacia abajo
                    }
                } else {
                    // Fuera de la mesa: teletransportar por encima de la basura
                    heldPart.setXPos(trashZone.getXPos() + trashZone.getWidth() / 2 - heldPart.getWidth() / 2); // Centrar en X
                    heldPart.setYPos(trashZone.getYPos() - 50); // 50 píxeles por encima
                    heldPart.setVelocityX(0); // Detener movimiento horizontal
                    heldPart.setVelocityY(0.5f); // Caer hacia abajo
                }
                heldPart = null;
            }
        }
    }

    private void spawnPart() {
        RobotPart.RobotFamily family = RobotPart.RobotFamily.values()[random.nextInt(4)];
        RobotPart.PartType type = RobotPart.PartType.values()[random.nextInt(2)];
        float xPos = spawnLeft ? 10 : 358;
        parts.add(new RobotPart(family, type, xPos, 260, screen));
        spawnLeft = !spawnLeft;
        if (status == GameStatus.ACTIVE) {
            scheduleSpawn();
        }
    }

    private void scheduleSpawn() {
        scheduleTask(this::spawnPart, 1000);
    }

    private void addCompletionParticle(float xPos, float yPos) {
        List<Texture> textures = new ArrayList<>();
        textures.add(new Texture(Identifier.of("minecraft", "textures/particle/generic_0.png"), 0, 0, 8, 8, 8, 8, 1.0f));
        Animation animation = new Animation(textures, 0.1f, false);
        particles.add(new Particle(xPos, yPos, 16, 16, screen, animation));
    }
}