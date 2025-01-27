package com.github.razorplay01.geoware.arkanoid.game.mapobject;

import com.github.razorplay01.geoware.arkanoid.game.util.records.Hitbox;
import com.github.razorplay01.geoware.arkanoid.game.util.records.IHitbox;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

import static com.github.razorplay01.geoware.arkanoid.Arkanoid.IS_DEBUG_MODE_ENABLE;


@Getter
@Setter
public abstract class MapObject implements IHitbox {
    private float xPos;
    private float yPos;
    private float width;
    private float height;
    protected final List<Hitbox> hitboxes = new ArrayList<>();

    protected MapObject(float xPos, float yPos, float width, float height, int debugColor) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        this.hitboxes.add(new Hitbox(Hitbox.HITBOX_DEFAULT, xPos, yPos, width, height, 0, 0, debugColor));
    }

    public void render(DrawContext context) {
        if (IS_DEBUG_MODE_ENABLE) {
            for (Hitbox hitbox : hitboxes) {
                context.fill(
                        (int) hitbox.xPos(),
                        (int) hitbox.yPos(),
                        (int) (hitbox.xPos() + hitbox.width()),
                        (int) (hitbox.yPos() + hitbox.height()),
                        hitbox.color()
                );
                context.drawBorder(
                        (int) hitbox.xPos(),
                        (int) hitbox.yPos(),
                        (int) hitbox.width(),
                        (int) hitbox.height(),
                        hitbox.color() | 0xFF000000 // Hacer el borde más opaco
                );
            }
        }
    }

    @Override
    public void updateHitboxes() {
        for (int i = 0; i < hitboxes.size(); i++) {
            Hitbox hitbox = hitboxes.get(i);

            hitboxes.set(i, new Hitbox(
                    hitbox.name(),
                    xPos + hitbox.xOffset(),
                    yPos + hitbox.yOffset(),
                    hitbox.width(),
                    hitbox.height(),
                    hitbox.xOffset(),
                    hitbox.yOffset(),
                    hitbox.color()
            ));
        }
    }

    @Override
    public Hitbox getHitboxByName(String name) {
        for (Hitbox hitbox : hitboxes) {
            if (hitbox.name().equals(name)) {
                return hitbox;
            }
        }
        return null;
    }

    @Override
    public Hitbox getDefaultHitbox() {
        for (Hitbox hitbox : hitboxes) {
            if (hitbox.name().equals(Hitbox.HITBOX_DEFAULT)) {
                return hitbox;
            }
        }
        return null;
    }
}