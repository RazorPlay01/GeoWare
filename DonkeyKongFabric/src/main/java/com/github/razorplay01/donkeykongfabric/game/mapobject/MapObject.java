package com.github.razorplay01.donkeykongfabric.game.mapobject;

import com.github.razorplay01.donkeykongfabric.game.util.DebugPoint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

import static com.github.razorplay01.donkeykongfabric.DonkeyKongFabric.IS_DEBUG_MODE_ENABLE;

@Getter
@Setter
@AllArgsConstructor
public abstract class MapObject {
    private float xPos;
    private float yPos;
    private float width;
    private float height;
    private int debugColor;
    private final List<DebugPoint> debugPoints = new ArrayList<>();

    protected MapObject(float xPos, float yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = 8;
        this.height = 8;
        updateDebugPoints();
    }

    public void render(DrawContext context) {
        if (IS_DEBUG_MODE_ENABLE) {
            context.fill(
                    (int) getXPos(),
                    (int) getYPos(),
                    (int) (getXPos() + getWidth()),
                    (int) (getYPos() + getHeight()),
                    debugColor
            );
            renderDebugPoints(context, debugPoints);
        }
    }

    public void updateDebugPoints() {
        debugPoints.clear();

        debugPoints.add(new DebugPoint(xPos, yPos, 0xFF000000)); // Esquina superior izquierda
        debugPoints.add(new DebugPoint(xPos + width, yPos, 0xFF000000)); // Esquina superior derecha
        debugPoints.add(new DebugPoint(xPos, yPos + height, 0xFF000000)); // Esquina inferior izquierda
        debugPoints.add(new DebugPoint(xPos + width, yPos + height, 0xFF000000)); // Esquina inferior derecha
    }

    public static void renderDebugPoints(DrawContext context, List<DebugPoint> debugPoints) {
        for (DebugPoint point : debugPoints) {
            context.fill(
                    (int) point.xPos() - 1,
                    (int) point.yPos() - 1,
                    (int) point.xPos() + 1,
                    (int) point.yPos() + 1,
                    point.renderColor()
            );
        }
    }
}