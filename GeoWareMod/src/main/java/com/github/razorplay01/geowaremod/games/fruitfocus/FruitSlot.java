package com.github.razorplay01.geowaremod.games.fruitfocus;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import com.github.razorplay01.razorplayapi.util.texture.Texture;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

@Getter
@Setter
@AllArgsConstructor
public class FruitSlot {
    private Fruit fruit;
    private float xPos;
    private float yPos;
    private boolean isHidden;
    private RectangleHitbox hitbox;
    private int color;
    private GameScreen screen;


    public void render(DrawContext context) {
        FruitFocusGame game = (FruitFocusGame) screen.getGame();
        float scaleFactor = game.getScaleFactor();

        if (isHidden) {
            Identifier hiddenTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/fruitfocus/hidden.png");
            context.drawTexture(hiddenTexture, (int) xPos, (int) yPos, (int) (16 * scaleFactor), (int) (16 * scaleFactor), 0, 0, 16, 16, 16, 16);
        }
        if (!isHidden || ((FruitFocusGame) screen.getGame()).getDiscoveredSlots().contains(this)) {
            Texture texture = fruit.getTexture();
            context.drawTexture(texture.identifier(), (int) xPos, (int) yPos, (int) (16 * scaleFactor), (int) (16 * scaleFactor), texture.u(), texture.v(), texture.width(), texture.height(), texture.textureWidth(), texture.textureHeight());
        }
    }
}
