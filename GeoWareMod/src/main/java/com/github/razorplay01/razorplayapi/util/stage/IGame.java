package com.github.razorplay01.razorplayapi.util.stage;

import net.minecraft.client.gui.DrawContext;

public interface IGame {
    void init();
    void update();
    void render(DrawContext context, int mouseX, int mouseY, float delta);
    void renderBackground(DrawContext context, int mouseX, int mouseY, float delta);
    int getScreenWidth();
    int getScreenHeight();
}
