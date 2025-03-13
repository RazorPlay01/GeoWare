package com.github.razorplay01.modbubblepuzzle.util.hitbox;

public interface CollisionListener {
    void onCollisionEnter(Hitbox other);
    void onCollisionExit(Hitbox other);
}