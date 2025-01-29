package com.github.razorplay01.geoware.hanoitowers.game.util.records;


public interface IHitbox {
    void updateHitboxes();
    Hitbox getHitboxByName(String name);
    Hitbox getDefaultHitbox();
}
