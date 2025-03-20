package com.github.razorplay01.geowaremod.fruitfocus;

import com.github.razorplay01.razorplayapi.util.texture.Texture;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.github.razorplay01.geowaremod.fruitfocus.FruitFocusGame.fruitsTexture;

@Getter
@AllArgsConstructor
public enum Fruit {

    CHERRY("cherry", new Texture(fruitsTexture, 0, 0, 16, 16, 48, 48, 1)),
    APPLE("apple", new Texture(fruitsTexture, 16, 0, 16, 16, 48, 48, 1)),
    BANANA("banana", new Texture(fruitsTexture, 32, 0, 16, 16, 48, 48, 1)),

    LEMON("lemon", new Texture(fruitsTexture, 0, 16, 16, 16, 48, 48, 1)),
    PEACH("peach", new Texture(fruitsTexture, 16, 16, 16, 16, 48, 48, 1)),
    KIWI("kiwi", new Texture(fruitsTexture, 32, 16, 16, 16, 48, 48, 1)),

    GRAPE("grape", new Texture(fruitsTexture, 32, 32, 16, 16, 48, 48, 1));

    private final String name;
    private final Texture texture;
}
