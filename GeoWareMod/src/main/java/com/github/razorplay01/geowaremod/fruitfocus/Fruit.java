package com.github.razorplay01.geowaremod.fruitfocus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.Identifier;

@Getter
@AllArgsConstructor
public enum Fruit {
    APPLE("apple", null),
    BANANA("banana", null),
    CHERRY("cherry", null),
    GRAPE("grape", null),
    LEMON("lemon", null),
    ORANGE("orange", null),
    PEACH("peach", null);

    private String name;
    private Identifier texture;
}
