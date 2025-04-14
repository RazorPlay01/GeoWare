package com.github.razorplay01.razorplayapi.util.screen;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.razorplayapi.util.render.CustomDrawContext;
import com.github.razorplay01.razorplayapi.util.stage.Game;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Getter
public abstract class GameScreen extends Screen {
    protected Game game;
    protected Integer gameScreenXPos;
    protected Integer gameScreenYPos;
    private float deltaTime;

    protected GameScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        if (gameScreenXPos == null || gameScreenYPos == null) {
            this.gameScreenXPos = (width / 2) - (game.getScreenWidth() / 2);
            this.gameScreenYPos = (height / 2) - (game.getScreenHeight() / 2);
        }
        this.game.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
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

    @Override
    public void tick() {
        game.update();
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        game.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        game.keyReleased(keyCode, scanCode, modifiers);
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        game.handleMouseInput(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        game.handlemouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    protected void renderGameScore(DrawContext context) {
        CustomDrawContext customDrawContext = CustomDrawContext.wrap(context);
        customDrawContext.drawText(this.textRenderer, "Score: " + game.getGameScore(),
                gameScreenXPos + this.game.getScreenWidth() + 20, this.gameScreenYPos + 20, 0xFFFFFF00, true);
    }

    protected void renderGameDuration(DrawContext context) {
        CustomDrawContext customDrawContext = CustomDrawContext.wrap(context);
        long remainingTime = this.game.getGameDutarion().getRemainingTime() / 1000;
        String scoreMessage = "Timer: " + String.format("%02d:%02d", (remainingTime / 60), (remainingTime % 60));
        customDrawContext.drawText(this.textRenderer, scoreMessage,
                gameScreenXPos + this.game.getScreenWidth() + 20, this.gameScreenYPos, 0xFFFFFF00, true);
    }

    protected void renderEndGameScreen(DrawContext context) {
        CustomDrawContext customDrawContext = CustomDrawContext.wrap(context);
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Fondo semi-transparente
        context.fill(0, 0, this.width, this.height, 0xCC000000);

        // Mensajes
        String endMessage = game.getGameDutarion().isFinished() ? "TIME'S UP!" : "GAME ENDED!";
        String scoreMessage = "Game Score: " + game.getGameScore();
        String prevScoreMessage = "Prev Score: " + game.getPrevScore();
        String totalScoreMessage = "Total Score: " + (game.getPrevScore() + game.getGameScore());

        // Correctly get and format remaining time for the final timer:
        long remainingTimeMillis = game.getFinalTimer().getRemainingTime();
        String timeMessage = String.format("Cerrando en %.2f segundos...", remainingTimeMillis / 1000.0f); // Show milliseconds

        int messageWidth = this.textRenderer.getWidth(endMessage);
        int scoreWidth = this.textRenderer.getWidth(scoreMessage);
        int prevScoreWidth = this.textRenderer.getWidth(totalScoreMessage);
        int totalScoreWidth = this.textRenderer.getWidth(totalScoreMessage);
        int timeWidth = this.textRenderer.getWidth(timeMessage);

        // Renderizar todos los mensajes centrados
        customDrawContext.drawText(this.textRenderer, endMessage, centerX - messageWidth / 2, centerY - 40, 0xFFFFFF00, true);
        customDrawContext.drawText(this.textRenderer, scoreMessage, centerX - scoreWidth / 2, centerY, 0xFFFFFFFF, true);
        customDrawContext.drawText(this.textRenderer, prevScoreMessage, centerX - prevScoreWidth / 2, centerY + 20, 0xFFFFFFFF, true);
        customDrawContext.drawText(this.textRenderer, totalScoreMessage, centerX - totalScoreWidth / 2, centerY + 40, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, timeMessage, centerX - timeWidth / 2, centerY + 70, 0xFFAAAAAA, true);
    }

    protected void renderInitGameScreen(DrawContext context) {
        CustomDrawContext customDrawContext = CustomDrawContext.wrap(context);
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Mensajes
        long remainingTimeMillis = game.getInitDelay().getRemainingTime(); // Get milliseconds
        String timeMessage = String.format("Iniciando en %.2f segundos...", remainingTimeMillis / 1000.0f); // Format with milliseconds

        int timeWidth = this.textRenderer.getWidth(timeMessage);

        context.drawText(this.textRenderer, timeMessage, centerX - timeWidth / 2, centerY, 0xFFAAAAAA, true);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().options.getGuiScale().setValue(GeoWareMod.guiScale);
        super.close();
    }
}
