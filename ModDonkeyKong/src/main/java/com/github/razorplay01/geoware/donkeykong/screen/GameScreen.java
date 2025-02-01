package com.github.razorplay01.geoware.donkeykong.screen;

import com.github.razorplay01.geoware.donkeykong.game.stages.TestGame;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

@Getter
public class GameScreen extends Screen {
    private final TestGame testGame = new TestGame(this);
    private Integer screenXPos;
    private Integer screenYPos;

    public GameScreen() {
        super(Text.empty());
    }

    @Override
    protected void init() {
        if (screenXPos == null || screenYPos == null) {
            this.screenXPos = (width / 2) - (testGame.getScreenWidth() / 2);
            this.screenYPos = (height / 2) - (testGame.getScreenHeight() / 2);
            testGame.init();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        //testGame.renderBackground(context, mouseX, mouseY, delta);
        testGame.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!testGame.isGameStarted() || testGame.isGameEnded()) {
            return false;
        }
        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == client.options.leftKey.getDefaultKey().getCode()) {
            testGame.getPlayer().moveLeft();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == client.options.rightKey.getDefaultKey().getCode()) {
            testGame.getPlayer().moveRight();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP || keyCode == client.options.forwardKey.getDefaultKey().getCode()) {
            testGame.getPlayer().moveUp(testGame.getLadders());
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == client.options.backKey.getDefaultKey().getCode()) {
            testGame.getPlayer().moveDown(testGame.getLadders());
            return true;
        } else if (keyCode == client.options.jumpKey.getDefaultKey().getCode()) {
            testGame.getPlayer().jump(testGame.getScreenHeight());
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_A) {
            testGame.getPlayer().stopMovingLeft();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_D) {
            testGame.getPlayer().stopMovingRight();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_W) {
            testGame.getPlayer().stopMovingUp();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_S) {
            testGame.getPlayer().stopMovingDown();
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }
}