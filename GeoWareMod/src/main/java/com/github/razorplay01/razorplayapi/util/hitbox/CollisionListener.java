package com.github.razorplay01.razorplayapi.util.hitbox;

public interface CollisionListener {
    void onCollisionEnter(Hitbox other);
    void onCollisionExit(Hitbox other);
}