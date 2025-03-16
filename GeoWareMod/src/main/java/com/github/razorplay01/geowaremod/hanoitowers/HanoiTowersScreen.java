package com.github.razorplay01.geowaremod.hanoitowers;

import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import net.minecraft.text.Text;

public class HanoiTowersScreen extends GameScreen {
    public HanoiTowersScreen(int prevScore, int initDelay, int timeLimitSeconds, int rings) {
        super(Text.empty());
        this.game = new HanoiTowersGame(this, initDelay, timeLimitSeconds, prevScore, rings);
    }
}
