package com.github.razorplay01.geowaremod.fruitfocus;

import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import net.minecraft.text.Text;

public class FruitFocusGameScreen extends GameScreen {
    public FruitFocusGameScreen(int timeLimitSeconds, int prevScore) {
        super(Text.empty());
        this.game = new FruitFocusGame(this, timeLimitSeconds, prevScore);
    }
}