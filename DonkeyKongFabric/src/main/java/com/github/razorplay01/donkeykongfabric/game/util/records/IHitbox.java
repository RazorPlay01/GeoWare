package com.github.razorplay01.donkeykongfabric.game.util.records;

public interface IHitbox {
    void updateHitboxes();
    Hitbox getHitboxByName(String name);
    Hitbox getDefaultHitbox();
}
