package com.github.razorplay01.geoware.hanoitowers;

import com.github.razorplay01.geoware.hanoitowers.game.util.game.GameScreen;
import net.minecraft.text.Text;

public class HanoiTowersScreen extends GameScreen {
    public HanoiTowersScreen(int prevScore, int initDelay, int timeLimitSeconds, int rings) {
        super(Text.empty());
        this.game = new HanoiTowersGame(this, initDelay, timeLimitSeconds, prevScore, rings);
    }
}
