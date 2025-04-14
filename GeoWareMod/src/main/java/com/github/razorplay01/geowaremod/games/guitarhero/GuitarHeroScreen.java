package com.github.razorplay01.geowaremod.games.guitarhero;

import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import net.minecraft.text.Text;

public class GuitarHeroScreen extends GameScreen {
    public GuitarHeroScreen(int timeLimitSeconds, int prevScore) {
        super(Text.empty());
        this.game = new GuitarHeroGame(this, timeLimitSeconds, prevScore);
    }
}