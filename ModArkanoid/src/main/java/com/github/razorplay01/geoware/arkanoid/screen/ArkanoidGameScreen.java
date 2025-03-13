package com.github.razorplay01.geoware.arkanoid.screen;

import com.github.razorplay01.geoware.arkanoid.game.stages.ArkanoidGame;
import com.github.razorplay01.geoware.arkanoid.game.util.game.GameScreen;
import lombok.Getter;
import net.minecraft.text.Text;

@Getter
public class ArkanoidGameScreen extends GameScreen {
    public ArkanoidGameScreen(int prevScore, int initDelay, int timeLimitSeconds, int level) {
        super(Text.empty());
        this.game = new ArkanoidGame(this, initDelay, timeLimitSeconds, prevScore, level);
    }
}
