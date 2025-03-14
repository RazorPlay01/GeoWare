package com.github.razorplay01.geoware.donkeykong;

import com.github.razorplay01.geoware.donkeykong.util.game.GameScreen;
import net.minecraft.text.Text;

public class DonkeyKongScreen extends GameScreen {
    public DonkeyKongScreen(int prevScore, int initDelay, int timeLimitSeconds, int spawnInterval, float spawnProbability) {
        super(Text.empty());
        this.game = new DonkeyKongGame(this, initDelay, timeLimitSeconds, prevScore, spawnInterval, spawnProbability);
    }
}
