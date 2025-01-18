package com.github.razorplay01.donkeykongfabric.game.stages;

import com.github.razorplay01.donkeykongfabric.DonkeyKongFabric;
import com.github.razorplay01.donkeykongfabric.game.Player;
import com.github.razorplay01.donkeykongfabric.game.mapobject.Ladder;
import com.github.razorplay01.donkeykongfabric.game.mapobject.Platform;
import com.github.razorplay01.donkeykongfabric.game.util.BarrelSpawner;
import com.github.razorplay01.donkeykongfabric.screen.GameScreen;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import static com.github.razorplay01.donkeykongfabric.game.Player.PLAYER_HEIGHT;

@Getter
public class TestGame extends Game {
    @Setter
    private boolean showJumpMessage = false;
    private final TextRenderer textRenderer = client.textRenderer;
    private final BarrelSpawner barrelSpawner;

    public TestGame(GameScreen screen) {
        super(Identifier.of(DonkeyKongFabric.MOD_ID, "textures/gui/map_base.png"), screen);
        this.barrelSpawner = new BarrelSpawner(screen, 80, 0.7f);
    }

    @Override
    public void init() {
        if (this.getPlatforms().isEmpty() || this.getPlayer() == null) {
            createGameMap();
        }
        this.player = new Player(screen.getScreenXPos() + 36f, platforms.getFirst().getYPos() - PLAYER_HEIGHT, screen);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render platforms and ladders
        for (Platform platform : getPlatforms()) {
            platform.render(context);
        }
        for (Ladder ladder : getLadders()) {
            ladder.render(context);
        }

        // Update and render barrels
        barrelSpawner.removeAndSpawnBarrels(context);

        // Update and render player
        updateAndRenderPlayer(context, mouseX, mouseY, delta);

        // Render score and time
        renderScore(context, textRenderer, getPlayer().getScore(), screen.getScreenXPos() + 10, screen.getScreenYPos() + 40, 1.0f);
        renderTime(context, textRenderer, 60, screen.getScreenXPos() + 10, screen.getScreenYPos() + 60, 1.0f);

        barrelSpawner.update();
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(
                getBackgroundImage(),
                screen.getScreenXPos(),
                screen.getScreenYPos(),
                0,
                0,
                getScreenWidth(),
                getScreenHeight(),
                getScreenWidth(),
                getScreenHeight()
        );
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
    public void createGameMap() {
        createFirstPlatform();
        createSecondPlatform();
        createThirdPlatform();
        createFourthPlatform();
        createFifthPlatform();
        createSixthPlatform();
        createFinalPlatform();
    }

    private void createFirstPlatform() {
        int platformWidth = 16;
        int platformHeight = 8;
        float bottomY = (float) screen.getScreenYPos() + getScreenHeight() - platformHeight;

        createPlatformLine(screen.getScreenXPos(), bottomY, platformWidth, platformHeight, 7, 1, 0);
        createPlatformLine((float) screen.getScreenXPos() + (7 * platformWidth), bottomY - 1, platformWidth, platformHeight, 7, 1, -1f);
        createLadder(screen.getScreenXPos() + 80f, screen.getScreenYPos() + 240f, 8, 8, false);
    }

    private void createSecondPlatform() {
        int platformWidth = 16;
        int platformHeight = 8;
        float bottomY = (float) screen.getScreenYPos() + getScreenHeight() - platformHeight;

        createPlatformLine(screen.getScreenXPos(), bottomY - 40, platformWidth, platformHeight, 13, 1, 1f);
        createLadder(screen.getScreenXPos() + 80f, screen.getScreenYPos() + 213f, 8, 11, true);
        createLadder(screen.getScreenXPos() + 184f, screen.getScreenYPos() + 219f, 8, 24, true);
    }

    private void createThirdPlatform() {
        int platformWidth = 16;
        int platformHeight = 8;
        float bottomY = (float) screen.getScreenYPos() + getScreenHeight() - platformHeight;

        createPlatformLine((float) screen.getScreenXPos() + getScreenWidth() - platformWidth, bottomY - 73f, platformWidth, platformHeight, 13, -1, 1f);
        createLadder(screen.getScreenXPos() + 96f, screen.getScreenYPos() + 182f, 8, 32, true);
        createLadder(screen.getScreenXPos() + 64f, screen.getScreenYPos() + 176f, 8, 8, false);
        createLadder(screen.getScreenXPos() + 32f, screen.getScreenYPos() + 186f, 8, 24, true);
    }

    private void createFourthPlatform() {
        int platformWidth = 16;
        int platformHeight = 8;
        float bottomY = (float) screen.getScreenYPos() + getScreenHeight() - platformHeight;

        createPlatformLine(screen.getScreenXPos(), bottomY - 106f, platformWidth, platformHeight, 13, 1, 1f);

        createLadder(screen.getScreenXPos() + 64f, screen.getScreenYPos() + 146f, 8, 14, true);
        createLadder(screen.getScreenXPos() + 112f, screen.getScreenYPos() + 149f, 8, 32, true);
        createLadder(screen.getScreenXPos() + 168f, screen.getScreenYPos() + 144f, 8, 8, false);
        createLadder(screen.getScreenXPos() + 184f, screen.getScreenYPos() + 153f, 8, 24, true);
    }

    private void createFifthPlatform() {
        int platformWidth = 16;
        int platformHeight = 8;
        float bottomY = (float) screen.getScreenYPos() + getScreenHeight() - platformHeight;

        createPlatformLine((float) screen.getScreenXPos() + getScreenWidth() - platformWidth, bottomY - 139f, platformWidth, platformHeight, 13, -1, 1f);

        createLadder(screen.getScreenXPos() + 168f, screen.getScreenYPos() + 112f, 8, 16, true);
        createLadder(screen.getScreenXPos() + 88f, screen.getScreenYPos() + 104f, 8, 13, false);
        createLadder(screen.getScreenXPos() + 72f, screen.getScreenYPos() + 118f, 8, 28, true);
        createLadder(screen.getScreenXPos() + 32f, screen.getScreenYPos() + 120f, 8, 24, true);
    }

    private void createSixthPlatform() {
        int platformWidth = 16;
        int platformHeight = 8;
        float bottomY = (float) screen.getScreenYPos() + getScreenHeight() - platformHeight;

        createPlatformLine(screen.getScreenXPos(), bottomY - 164f, platformWidth, platformHeight, 8, 1, 0);
        createPlatformLine(screen.getScreenXPos() + (8f * platformWidth), bottomY - 164f, platformWidth, platformHeight, 5, 1, 1f);

        createLadder(screen.getScreenXPos() + 88f, screen.getScreenYPos() + 84f, 8, 12, true);
        createLadder(screen.getScreenXPos() + 184f, screen.getScreenYPos() + 87f, 8, 24, true);
    }

    private void createFinalPlatform() {
        int platformWidth = 16;
        int platformHeight = 8;

        createPlatformLine(screen.getScreenXPos() + 88f, screen.getScreenYPos() + 56f, platformWidth, platformHeight, 3, 1, 0);
        createLadder(screen.getScreenXPos() + 128f, screen.getScreenYPos() + 56f, 8, 28, false);

        this.getPlatforms().add(new Platform(screen.getScreenXPos() + 64f, screen.getScreenYPos() + 24f, 8, 8));
        this.getPlatforms().add(new Platform(screen.getScreenXPos() + 80f, screen.getScreenYPos() + 24f, 8, 8));
        createLadder(screen.getScreenXPos() + 64f, screen.getScreenYPos() + 24f, 8, 60, false);
        createLadder(screen.getScreenXPos() + 80f, screen.getScreenYPos() + 24f, 8, 60, false);
    }
}
