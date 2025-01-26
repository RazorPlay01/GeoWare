package com.github.razorplay01.geoware.donkeykong.game.stages;

import net.minecraft.client.gui.DrawContext;

public interface IGame {
    void init();
    void render(DrawContext context, int mouseX, int mouseY, float delta);
    void renderBackground(DrawContext context, int mouseX, int mouseY, float delta);
    void updateAndRenderPlayer(DrawContext context, int mouseX, int mouseY, float delta);
    void createGameMap();
    int getScreenWidth();
    int getScreenHeight();
}
