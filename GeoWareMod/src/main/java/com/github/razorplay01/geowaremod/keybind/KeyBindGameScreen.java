package com.github.razorplay01.geowaremod.keybind;

import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class KeyBindGameScreen extends GameScreen {
    public KeyBindGameScreen(int timeLimitSeconds, int prevScore) {
        super(Text.empty());
        this.game = new KeyBindGame(this, timeLimitSeconds, prevScore);
    }

    @Override
    protected void init() {
        if (gameScreenXPos == null || gameScreenYPos == null) {
            this.gameScreenXPos = (width / 2) - (game.getScreenWidth() / 2);
            this.gameScreenYPos = (height / 4) - (game.getScreenHeight() / 2);
        }
        this.game.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        game.renderBackground(context, mouseX, mouseY, delta);
        game.render(context, mouseX, mouseY, delta);
        if (this.game.getInitDelay().isRunning() && !this.game.getInitDelay().isFinished()) {
            renderInitGameScreen(context);
        }

        if (this.game.getGameDutarion().isRunning() && !this.game.getGameDutarion().isFinished()) {
            long remainingTime = this.game.getGameDutarion().getRemainingTime() / 1000;
            String scoreMessage = String.format("%02d:%02d", (remainingTime / 60), (remainingTime % 60));
            context.drawText(this.textRenderer, scoreMessage,
                    gameScreenXPos + 160, this.gameScreenYPos + 3, 0xFF62584b, false);
            context.drawText(this.textRenderer, String.valueOf(game.getGameScore()),
                    gameScreenXPos + 110, this.gameScreenYPos + 3, 0xFF62584b, false);

        }

        if (this.game.getFinalTimer().isRunning() && !this.game.getFinalTimer().isFinished()) {
            renderEndGameScreen(context);
        }
        this.game.getFloatingTexts().forEach(floatingText -> floatingText.render(context));
    }
}