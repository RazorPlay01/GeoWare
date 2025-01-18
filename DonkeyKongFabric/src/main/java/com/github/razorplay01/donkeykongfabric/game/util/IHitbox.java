package com.github.razorplay01.donkeykongfabric.game.util;

public interface IHitbox {
    void updateHitboxes();
    Hitbox getHitboxByName(String name);
    Hitbox getDefaultHitbox();
}
