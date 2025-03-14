package com.github.razorplay01.modbubblepuzzle;

import com.github.razorplay01.modbubblepuzzle.util.game.GameScreen;
import net.minecraft.text.Text;

public class BubblePuzzleScreen extends GameScreen {
    public BubblePuzzleScreen(int prevScore, int initDelay, int timeLimitSeconds, int level) {
        super(Text.literal("Bubble Puzzle"));
        this.game = new BubblePuzzleGame(this, prevScore, initDelay, timeLimitSeconds, level);
    }
}