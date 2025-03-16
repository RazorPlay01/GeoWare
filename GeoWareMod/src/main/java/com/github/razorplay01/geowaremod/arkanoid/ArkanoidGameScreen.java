package com.github.razorplay01.geowaremod.arkanoid;

import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import lombok.Getter;
import net.minecraft.text.Text;

@Getter
public class ArkanoidGameScreen extends GameScreen {
    public ArkanoidGameScreen(int prevScore, int initDelay, int timeLimitSeconds, int level) {
        super(Text.empty());
        this.game = new ArkanoidGame(this, initDelay, timeLimitSeconds, prevScore, level);
    }
}
