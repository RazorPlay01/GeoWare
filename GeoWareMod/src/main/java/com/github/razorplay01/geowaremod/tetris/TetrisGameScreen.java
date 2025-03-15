package com.github.razorplay01.geowaremod.tetris;

import com.github.razorplay01.geowaremod.tetris.game.TetrisGame;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import net.minecraft.text.Text;

public class TetrisGameScreen extends GameScreen {
    public TetrisGameScreen(int prevScore, int initDelay, int timeLimitSeconds, float speedMultiplier) {
        super(Text.empty());
        this.game = new TetrisGame(this, initDelay, timeLimitSeconds, prevScore, speedMultiplier);
    }
}