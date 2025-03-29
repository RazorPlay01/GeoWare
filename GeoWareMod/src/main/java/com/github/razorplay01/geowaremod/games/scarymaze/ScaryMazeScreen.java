package com.github.razorplay01.geowaremod.games.scarymaze;

import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import net.minecraft.text.Text;

public class ScaryMazeScreen extends GameScreen {
    public ScaryMazeScreen(int prevScore, int initDelay, int timeLimitSeconds, int level) {
        super(Text.empty());
        this.game = new ScaryMazeGame(this, initDelay, timeLimitSeconds, prevScore, level);
    }
}
