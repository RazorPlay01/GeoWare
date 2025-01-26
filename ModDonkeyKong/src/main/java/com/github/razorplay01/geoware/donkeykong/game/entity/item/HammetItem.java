package com.github.razorplay01.geoware.donkeykong.game.entity.item;

import com.github.razorplay01.geoware.donkeykong.game.util.Animation;
import com.github.razorplay01.geoware.donkeykong.game.util.records.Hitbox;
import com.github.razorplay01.geoware.donkeykong.screen.GameScreen;
import net.minecraft.client.gui.DrawContext;

import static com.github.razorplay01.geoware.donkeykong.game.util.records.Hitbox.HITBOX_PLAYER;
import static com.github.razorplay01.geoware.donkeykong.game.util.records.Hitbox.PLAYER_HITBOX_COLOR;
import static com.github.razorplay01.geoware.donkeykong.game.util.texture.TextureProvider.HAMMET_ITEM_TEXTURES;

public class HammetItem extends ItemEntity {
    private final Animation IdleAnimation = new Animation(HAMMET_ITEM_TEXTURES, 1f, false);

    public HammetItem(float xPos, float yPos, float width, float height, GameScreen gameScreen) {
        super(xPos, yPos, width, height, gameScreen, 0xFFff0059);
        this.hitboxes.add(new Hitbox(HITBOX_PLAYER, xPos + (this.width - 8) / 2, yPos + (this.height - 8) / 2, 8, 8, (this.width - 8) / 2, (this.height - 8) / 2, PLAYER_HITBOX_COLOR));
    }

    @Override
    protected void update() {

    }

    @Override
    public void render(DrawContext context) {
        int xOffset = 0;
        int yOffset = 0;

        renderTexture(context, this, IdleAnimation, xOffset, yOffset);
        super.render(context);
    }
}
