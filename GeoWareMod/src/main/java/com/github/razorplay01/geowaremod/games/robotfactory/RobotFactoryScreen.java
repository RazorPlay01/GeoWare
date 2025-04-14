package com.github.razorplay01.geowaremod.games.robotfactory;

import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import net.minecraft.text.Text;

public class RobotFactoryScreen extends GameScreen {
    public RobotFactoryScreen(int timeLimitSeconds, int prevScore, float speedMultiplier, boolean enableRotation, int partQuantity) {
        super(Text.of("Robot Factory"));
        this.game = new RobotFactoryGame(this, timeLimitSeconds, prevScore, speedMultiplier, enableRotation, partQuantity);
    }
}