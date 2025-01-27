package com.github.razorplay01.geoware.arkanoid.game.mapobject;

import com.github.razorplay01.geoware.arkanoid.game.util.BrickColor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

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
        drawBrick(context, (int) this.getXPos(), (int) this.getYPos(), this.getColor().getColor());
        super.render(context);
    }

    private void drawBrick(DrawContext context, int x, int y, int baseColor) {
        // Crear un color más oscuro para el borde
        int borderColor = darkenColor(baseColor, 0.7f);

        // Dibujar el relleno del ladrillo
        context.fill(x + 1, y + 1, (int) (x + this.getWidth() - 1), (int) (y + this.getHeight() - 1), baseColor);

        // Dibujar los bordes
        context.drawHorizontalLine(x, (int) (x + this.getWidth() - 1), y, borderColor); // Borde superior
        context.drawHorizontalLine(x, (int) (x + this.getWidth() - 1), (int) (y + this.getHeight() - 1), borderColor); // Borde inferior
        context.drawVerticalLine(x, y, (int) (y + this.getHeight() - 1), borderColor); // Borde izquierdo
        context.drawVerticalLine((int) (x + this.getWidth() - 1), y, (int) (y + this.getHeight() - 1), borderColor); // Borde derecho
    }

    private int darkenColor(int color, float factor) {
        int a = (color >> 24) & 0xff;
        int r = (int) ((color >> 16 & 0xff) * factor);
        int g = (int) ((color >> 8 & 0xff) * factor);
        int b = (int) ((color & 0xff) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}