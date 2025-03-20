package com.github.razorplay01.geowaremod.arkanoid.mapobject;

import com.github.razorplay01.geowaremod.GeoWareMod;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

@Getter
@Setter
public class Brick extends MapObject {
    private BrickColor color;

    public Brick(float xPos, float yPos, float width, float height, BrickColor color) {
        super(xPos, yPos, width, height, 0xAAcf6800);
        this.color = color;
    }

    @Override
    public void render(DrawContext context) {
        Identifier texture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/arkanoid/bricks.png");
        switch (color) {
            case GREEN ->
                    context.drawTexture(texture, (int) getXPos(), (int) getYPos(), (int) getWidth(), (int) getHeight(), 0, 0, 24, 11, 24, 33);
            case RED ->
                    context.drawTexture(texture, (int) getXPos(), (int) getYPos(), (int) getWidth(), (int) getHeight(), 0, 11, 24, 11, 24, 33);
            case BLUE ->
                    context.drawTexture(texture, (int) getXPos(), (int) getYPos(), (int) getWidth(), (int) getHeight(), 0, 22, 24, 11, 24, 33);
        }
    }
}