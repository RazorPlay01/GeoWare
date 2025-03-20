package com.github.razorplay01.geowaremod.donkeykong;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.geowaremod.donkeykong.game.entity.Fire;
import com.github.razorplay01.geowaremod.donkeykong.game.entity.barrel.Barrel;
import com.github.razorplay01.geowaremod.donkeykong.game.entity.barrel.DonkeyKong;
import com.github.razorplay01.geowaremod.donkeykong.game.entity.item.HammetItem;
import com.github.razorplay01.geowaremod.donkeykong.game.entity.item.ItemEntity;
import com.github.razorplay01.geowaremod.donkeykong.game.entity.player.Player;
import com.github.razorplay01.geowaremod.donkeykong.game.mapobject.Ladder;
import com.github.razorplay01.geowaremod.donkeykong.game.mapobject.Platform;
import com.github.razorplay01.geowaremod.donkeykong.game.mapobject.VictoryZone;
import com.github.razorplay01.razorplayapi.util.GameStatus;
import com.github.razorplay01.razorplayapi.util.Particle;
import com.github.razorplay01.razorplayapi.util.render.CustomDrawContext;
import com.github.razorplay01.razorplayapi.util.stage.Game;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
public class DonkeyKongGame extends Game {
    public static final String HITBOX_DEFAULT = "default";
    public static final String HITBOX_LADDER = "ladder";
    public static final String HITBOX_PLAYER = "player";
    public static final String HITBOX_BARREL = "barrel";

    public static final int PLAYER_HITBOX_COLOR = 0x50FF0000;
    public static final int LADDER_HITBOX_COLOR = 0xAA00ffec;

    private static final int PLATFORM_WIDTH = 16;
    private static final int PLATFORM_HEIGHT = 8;

    protected final List<Barrel> barrels = new ArrayList<>();
    protected final List<Ladder> ladders = new ArrayList<>();
    protected final List<Platform> platforms = new ArrayList<>();
    protected final List<VictoryZone> victoryPlatforms = new ArrayList<>();
    protected final List<Fire> fires = new ArrayList<>();
    protected final List<ItemEntity> items = new ArrayList<>();
    protected final List<Particle> particles = new ArrayList<>();
    protected Player player;
    protected DonkeyKong donkeyKong;
    protected final Identifier backgroundImage;

    private final int spawnInterval;
    private final float spawnProbability;

    protected DonkeyKongGame(Screen screen, int initDelay, int timeLimitSeconds, int prevScore, int spawnInterval, float spawnProbability) {
        super(screen, initDelay, timeLimitSeconds, prevScore);
        this.backgroundImage = Identifier.of(GeoWareMod.MOD_ID, "textures/gui/map_base.png");
        this.spawnInterval = spawnInterval;
        this.spawnProbability = spawnProbability;
    }

    @Override
    public void init() {
        super.init();
        if (platforms.isEmpty()) {
            createGameMap();
        }
        this.player = new Player(screen.getGameScreenXPos() + 36f, screen.getGameScreenYPos() + getScreenHeight() - 16f - PLATFORM_HEIGHT, screen);
        this.donkeyKong = new DonkeyKong(screen.getGameScreenXPos() + 18f, screen.getGameScreenYPos() + 52f, screen, spawnInterval, spawnProbability);
        this.items.add(new HammetItem(screen.getGameScreenXPos() + 167f, screen.getGameScreenYPos() + 190f, 13, 13, screen));
        this.items.add(new HammetItem(screen.getGameScreenXPos() + 16f, screen.getGameScreenYPos() + 92f, 13, 13, screen));
        this.victoryPlatforms.add(new VictoryZone(screen, screen.getGameScreenXPos() + 88f, screen.getGameScreenYPos() + 36f, 48, 20, 0xAAFFFFFF));
        //this.fires.add(new Fire(screen.getGameScreenXPos() + 50f, screen.getGameScreenYPos() + getScreenHeight() - 16 - PLATFORM_HEIGHT, screen));
    }

    @Override
    public void update() {
        super.update();
        if (getStatus() == GameStatus.ACTIVE) {
            player.update();
            donkeyKong.update();

            for (VictoryZone victoryPlatform : getVictoryPlatforms()) {
                player.checkVictoryPlatform(victoryPlatform);
            }

            if (player.isLosing() || player.isWinning()) {
                getGameDutarion().stop();
                if (player.isWinning()) {
                    calculateVictoryBonus();
                }
                setStatus(GameStatus.ENDING);
            }
        }
        updateParticles();
        updateBarrels();
        fires.forEach(Fire::update);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        platforms.forEach(platform -> platform.render(context));
        ladders.forEach(ladder -> ladder.render(context));
        items.forEach(item -> item.render(context));
        particles.forEach(particle -> particle.render(context));
        victoryPlatforms.forEach(victory -> victory.render(context));
        fires.forEach(fire -> fire.render(context));
        barrels.forEach(barrel -> barrel.render(context));

        donkeyKong.render(context);
        player.render(context);
        if (getStatus() == GameStatus.ENDING && (player.isLosing() || player.isWinning())) {
            player.update();
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        CustomDrawContext customDrawContext = CustomDrawContext.wrap(context);
        customDrawContext.drawBasicBackground(screen);
    }

    @Override
    public int getScreenWidth() {
        return 224;
    }

    @Override
    public int getScreenHeight() {
        return 256;
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (status == GameStatus.ACTIVE) {
            if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == client.options.leftKey.getDefaultKey().getCode()) {
                getPlayer().moveLeft();
            } else if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == client.options.rightKey.getDefaultKey().getCode()) {
                getPlayer().moveRight();
            } else if (keyCode == GLFW.GLFW_KEY_UP || keyCode == client.options.forwardKey.getDefaultKey().getCode()) {
                getPlayer().moveUp(getLadders());
            } else if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == client.options.backKey.getDefaultKey().getCode()) {
                getPlayer().moveDown(getLadders());
            } else if (keyCode == client.options.jumpKey.getDefaultKey().getCode()) {
                getPlayer().jump(getScreenHeight());
            }
        }
    }

    @Override
    public void keyReleased(int keyCode, int scanCode, int modifiers) {
        if (status == GameStatus.ACTIVE) {
            if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_A) {
                getPlayer().stopMovingLeft();
            } else if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_D) {
                getPlayer().stopMovingRight();
            } else if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_W) {
                getPlayer().stopMovingUp();
            } else if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_S) {
                getPlayer().stopMovingDown();
            }
        }
    }

    private void calculateVictoryBonus() {
        if (player.isWinning()) {
            long remainingTimeSeconds = gameDutarion.getRemainingTime() / 1000;  // Convert to seconds
            int timeBonus = (int) remainingTimeSeconds * 100;  // 100 points per remaining second
            addScore(timeBonus);
        }
    }

    private void updateBarrels() {
        List<Barrel> barrelsToRemove = new ArrayList<>();
        for (Barrel barrel : getBarrels()) {
            barrel.update();
            if (barrel.isRemove()) {
                barrelsToRemove.add(barrel);
            }
        }
        getBarrels().removeAll(barrelsToRemove);
    }

    private void updateParticles() {
        List<Particle> particles = getParticles();
        Iterator<Particle> iterator = particles.iterator();

        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            particle.update(); // Actualizamos la partícula
            if (particle.isFinished()) {
                iterator.remove(); // Eliminamos partículas finalizadas
            }
        }
    }

    private void createGameMap() {
        platforms.clear();
        ladders.clear();
        createFirstPlatform();
        createSecondPlatform();
        createThirdPlatform();
        createFourthPlatform();
        createFifthPlatform();
        createSixthPlatform();
        createFinalPlatform();
    }


    public void createPlatformLine(float startX, float startY, float platformWidth, float platformHeight, int segments, int directionX, float slopeY) {
        float currentX = startX;
        float currentY = startY;
        for (int i = 0; i < segments; i++) {
            platforms.add(new Platform(screen, currentX, currentY, platformWidth, platformHeight));
            currentX += directionX * platformWidth;
            currentY += slopeY;
        }
    }

    private void createLadder(float xPos, float yPos, float width, float height, boolean canPassThroughPlatform) {
        this.ladders.add(new Ladder(screen, xPos, yPos, width, height, canPassThroughPlatform));
    }

    private void createFirstPlatform() {
        float bottomY = (float) screen.getGameScreenYPos() + getScreenHeight() - PLATFORM_HEIGHT;
        createPlatformLine(screen.getGameScreenXPos(), bottomY, PLATFORM_WIDTH, PLATFORM_HEIGHT, 7, 1, 0);
        createPlatformLine((float) screen.getGameScreenXPos() + (7 * PLATFORM_WIDTH), bottomY - 1, PLATFORM_WIDTH, PLATFORM_HEIGHT, 7, 1, -1f);
        createLadder(screen.getGameScreenXPos() + 80f, screen.getGameScreenYPos() + 240f, 8, 8, false);
    }

    private void createSecondPlatform() {
        float bottomY = (float) screen.getGameScreenYPos() + getScreenHeight() - PLATFORM_HEIGHT;
        createPlatformLine(screen.getGameScreenXPos(), bottomY - 40, PLATFORM_WIDTH, PLATFORM_HEIGHT, 13, 1, 1f);
        createLadder(screen.getGameScreenXPos() + 80f, screen.getGameScreenYPos() + 213f, 8, 11, true);
        createLadder(screen.getGameScreenXPos() + 184f, screen.getGameScreenYPos() + 219f, 8, 24, true);
    }

    private void createThirdPlatform() {
        float bottomY = (float) screen.getGameScreenYPos() + getScreenHeight() - PLATFORM_HEIGHT;
        createPlatformLine((float) screen.getGameScreenXPos() + getScreenWidth() - PLATFORM_WIDTH, bottomY - 73f, PLATFORM_WIDTH, PLATFORM_HEIGHT, 13, -1, 1f);
        createLadder(screen.getGameScreenXPos() + 96f, screen.getGameScreenYPos() + 182f, 8, 32, true);
        createLadder(screen.getGameScreenXPos() + 64f, screen.getGameScreenYPos() + 176f, 8, 8, false);
        createLadder(screen.getGameScreenXPos() + 32f, screen.getGameScreenYPos() + 186f, 8, 24, true);
    }

    private void createFourthPlatform() {
        float bottomY = (float) screen.getGameScreenYPos() + getScreenHeight() - PLATFORM_HEIGHT;
        createPlatformLine(screen.getGameScreenXPos(), bottomY - 106f, PLATFORM_WIDTH, PLATFORM_HEIGHT, 13, 1, 1f);
        createLadder(screen.getGameScreenXPos() + 64f, screen.getGameScreenYPos() + 146f, 8, 14, true);
        createLadder(screen.getGameScreenXPos() + 112f, screen.getGameScreenYPos() + 149f, 8, 32, true);
        createLadder(screen.getGameScreenXPos() + 168f, screen.getGameScreenYPos() + 144f, 8, 8, false);
        createLadder(screen.getGameScreenXPos() + 184f, screen.getGameScreenYPos() + 153f, 8, 24, true);
    }

    private void createFifthPlatform() {
        float bottomY = (float) screen.getGameScreenYPos() + getScreenHeight() - PLATFORM_HEIGHT;
        createPlatformLine((float) screen.getGameScreenXPos() + getScreenWidth() - PLATFORM_WIDTH, bottomY - 139f, PLATFORM_WIDTH, PLATFORM_HEIGHT, 13, -1, 1f);
        createLadder(screen.getGameScreenXPos() + 168f, screen.getGameScreenYPos() + 112f, 8, 16, true);
        createLadder(screen.getGameScreenXPos() + 88f, screen.getGameScreenYPos() + 104f, 8, 13, false);
        createLadder(screen.getGameScreenXPos() + 72f, screen.getGameScreenYPos() + 118f, 8, 28, true);
        createLadder(screen.getGameScreenXPos() + 32f, screen.getGameScreenYPos() + 120f, 8, 24, true);
    }

    private void createSixthPlatform() {
        float bottomY = (float) screen.getGameScreenYPos() + getScreenHeight() - PLATFORM_HEIGHT;
        createPlatformLine(screen.getGameScreenXPos(), bottomY - 164f, PLATFORM_WIDTH, PLATFORM_HEIGHT, 8, 1, 0);
        createPlatformLine(screen.getGameScreenXPos() + (8f * PLATFORM_WIDTH), bottomY - 164f, PLATFORM_WIDTH, PLATFORM_HEIGHT, 5, 1, 1f);
        createLadder(screen.getGameScreenXPos() + 88f, screen.getGameScreenYPos() + 84f, 8, 12, true);
        createLadder(screen.getGameScreenXPos() + 184f, screen.getGameScreenYPos() + 87f, 8, 24, true);
    }

    private void createFinalPlatform() {
        createPlatformLine(screen.getGameScreenXPos() + 88f, screen.getGameScreenYPos() + 56f, PLATFORM_WIDTH, PLATFORM_HEIGHT, 3, 1, 0);
        createLadder(screen.getGameScreenXPos() + 128f, screen.getGameScreenYPos() + 56f, 8, 28, false);
        platforms.add(new Platform(screen, screen.getGameScreenXPos() + 64f, screen.getGameScreenYPos() + 24f, 8, 8));
        platforms.add(new Platform(screen, screen.getGameScreenXPos() + 80f, screen.getGameScreenYPos() + 24f, 8, 8));
        createLadder(screen.getGameScreenXPos() + 64f, screen.getGameScreenYPos() + 24f, 8, 60, false);
        createLadder(screen.getGameScreenXPos() + 80f, screen.getGameScreenYPos() + 24f, 8, 60, false);
    }
}
