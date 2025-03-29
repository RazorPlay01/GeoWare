package com.github.razorplay01.geowaremod.games.arkanoid.mapobject;

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
            case PURPLE ->
                    context.drawTexture(texture, (int) getXPos(), (int) getYPos(), (int) getWidth(), (int) getHeight(), 0, 0, 35, 18, 35, 108);
            case WHITE ->
                    context.drawTexture(texture, (int) getXPos(), (int) getYPos(), (int) getWidth(), (int) getHeight(), 0, 18, 35, 18, 35, 108);
            case YELLOW ->
                    context.drawTexture(texture, (int) getXPos(), (int) getYPos(), (int) getWidth(), (int) getHeight(), 0, 36, 35, 18, 35, 108);
            case RED ->
                    context.drawTexture(texture, (int) getXPos(), (int) getYPos(), (int) getWidth(), (int) getHeight(), 0, 54, 35, 18, 35, 108);
            case BLUE ->
                    context.drawTexture(texture, (int) getXPos(), (int) getYPos(), (int) getWidth(), (int) getHeight(), 0, 72, 35, 18, 35, 108);
            case GREEN ->
                    context.drawTexture(texture, (int) getXPos(), (int) getYPos(), (int) getWidth(), (int) getHeight(), 0, 90, 35, 18, 35, 108);

        }
    }
}