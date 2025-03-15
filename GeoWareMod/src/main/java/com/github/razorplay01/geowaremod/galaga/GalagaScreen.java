package com.github.razorplay01.geowaremod.galaga;

import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import net.minecraft.text.Text;

public class GalagaScreen extends GameScreen {
    public GalagaScreen(int currentPattern, int timeLimitSeconds, int prevScore) {
        super(Text.empty());
        this.game = new GalagaGame(this, currentPattern, timeLimitSeconds, prevScore);
    }
}
