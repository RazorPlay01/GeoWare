package com.github.razorplay01.geowaremod.games.tetris;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.geowaremod.games.tetris.game.TetrisGame;
import com.github.razorplay01.razorplayapi.util.render.CustomDrawContext;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TetrisGameScreen extends GameScreen {
    public TetrisGameScreen(int prevScore, int initDelay, int timeLimitSeconds, float speedMultiplier) {
        super(Text.empty());
        this.game = new TetrisGame(this, initDelay, timeLimitSeconds, prevScore, speedMultiplier);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        game.renderBackground(context, mouseX, mouseY, delta);
        this.deltaTime = delta;
        game.renderBackground(context, mouseX, mouseY, delta);
        game.render(context, mouseX, mouseY, delta);
        renderGameScore(context);
        if (this.game.getInitDelay().isRunning() && !this.game.getInitDelay().isFinished()) {
            renderInitGameScreen(context);
        }

        if (this.game.getGameDutarion().isRunning() && !this.game.getGameDutarion().isFinished()) {
            renderGameDuration(context);
        }

        if (this.game.getFinalTimer().isRunning() && !this.game.getFinalTimer().isFinished()) {
            renderEndGameScreen(context);
        }
        this.game.getFloatingTexts().forEach(floatingText -> floatingText.render(context));
    }

    protected void renderGameScore(DrawContext context) {
        Identifier marcoTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/tetris/marco2.png");
        int xPos = gameScreenXPos + this.game.getScreenWidth() + 14;
        int yPos = this.gameScreenYPos + 53;
        context.drawTexture(marcoTexture, xPos - 5, yPos,
                64, 16, 0, 0, 64, 16, 64, 16);

        CustomDrawContext customDrawContext = CustomDrawContext.wrap(context);
        customDrawContext.drawText(this.textRenderer, "Score: " + game.getGameScore(),
                xPos + 2, yPos + 5, 0xFFFFFF00, true);
    }

    protected void renderGameDuration(DrawContext context) {
        Identifier marcoTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/tetris/marco2.png");
        int xPos = gameScreenXPos + this.game.getScreenWidth() + 14;
        int yPos = this.gameScreenYPos + 68;
        context.drawTexture(marcoTexture, xPos - 5, yPos,
                64, 16, 0, 0, 64, 16, 64, 16);

        CustomDrawContext customDrawContext = CustomDrawContext.wrap(context);
        long remainingTime = this.game.getGameDutarion().getRemainingTime() / 1000;
        String scoreMessage = "Timer: " + String.format("%02d:%02d", (remainingTime / 60), (remainingTime % 60));
        customDrawContext.drawText(this.textRenderer, scoreMessage,
                xPos + 2, yPos + 5, 0xFFFFFF00, true);
    }
}