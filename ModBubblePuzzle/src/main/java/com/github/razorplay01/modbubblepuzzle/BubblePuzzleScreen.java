package com.github.razorplay01.modbubblepuzzle;

import com.github.razorplay01.modbubblepuzzle.util.screen.GameScreen;
import net.minecraft.text.Text;

public class BubblePuzzleScreen extends GameScreen {
    public BubblePuzzleScreen() {
        super(Text.literal("Bubble Puzzle"));
        this.game = new BubblePuzzleGame(this);
    }
}