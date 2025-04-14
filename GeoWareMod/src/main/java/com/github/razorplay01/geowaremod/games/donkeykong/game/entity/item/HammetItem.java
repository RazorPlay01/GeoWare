package com.github.razorplay01.geowaremod.games.donkeykong.game.entity.item;

import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import com.github.razorplay01.razorplayapi.util.texture.Animation;
import net.minecraft.client.gui.DrawContext;

import static com.github.razorplay01.geowaremod.games.donkeykong.game.util.TextureProvider.HAMMET_ITEM_TEXTURES;
import static com.github.razorplay01.geowaremod.games.donkeykong.DonkeyKongGame.*;

public class HammetItem extends ItemEntity {
    private final Animation IdleAnimation = new Animation(HAMMET_ITEM_TEXTURES, 1f, false);

    public HammetItem(float xPos, float yPos, float width, float height, GameScreen gameScreen) {
        super(xPos, yPos, width, height, gameScreen, 0xFFff0059);
        this.hitboxes.add(new RectangleHitbox(HITBOX_PLAYER, xPos + (this.width - 8) / 2, yPos + (this.height - 8) / 2, 8, 8, (this.width - 8) / 2, (this.height - 8) / 2, PLAYER_HITBOX_COLOR));
    }

    @Override
    protected void update() {

    }

    @Override
    public void render(DrawContext context, float delta) {
        int xOffset = 0;
        int yOffset = 0;
        renderTexture(context, this, IdleAnimation, xOffset, yOffset);
    }
}
