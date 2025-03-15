package com.github.razorplay01.geowaremod.keybind;

import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import net.minecraft.text.Text;

public class KeyBindGameScreen extends GameScreen {
    public KeyBindGameScreen(int timeLimitSeconds, int prevScore) {
        super(Text.empty());
        this.game = new KeyBindGame(this, timeLimitSeconds, prevScore);
    }
}