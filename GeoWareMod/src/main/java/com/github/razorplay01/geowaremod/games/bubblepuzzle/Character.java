package com.github.razorplay01.geowaremod.games.bubblepuzzle;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.razorplayapi.util.texture.Animation;
import com.github.razorplay01.razorplayapi.util.texture.Texture;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.Map;

@Getter
public class Character {
    private float x, y; // Posición del personaje
    private float width, height; // Dimensiones renderizadas
    private Animation currentAnimation; // Animación actual
    private final Map<BubbleColor, Animation> reloadAnimations; // Animaciones de recarga por color
    private final Animation startAnimation; // Animación de inicio
    private final Animation winAnimation; // Animación de victoria
    private final Animation loseIdleAnimation; // Animación de derrota e idle
    private boolean isReloading; // Estado de recarga

    public Character(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isReloading = false;

        float reloadSpeed = 0.2f;
        Animation green_ball_animation = new Animation(
                Texture.createTextureList(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/green_ball_animation.png"), 101, 98, 101, 980, 10, 1.0f, false),
                reloadSpeed, false
        );
        Animation pink_ball_animation = new Animation(
                Texture.createTextureList(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/pink_ball_animation.png"), 101, 98, 101, 980, 10, 1.0f, false),
                reloadSpeed, false
        );
        Animation red_ball_animation = new Animation(
                Texture.createTextureList(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/red_ball_animation.png"), 101, 98, 101, 980, 10, 1.0f, false),
                reloadSpeed, false
        );
        Animation black_ball_animation = new Animation(
                Texture.createTextureList(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/black_ball_animation.png"), 101, 98, 101, 980, 10, 1.0f, false),
                reloadSpeed, false
        );
        Animation orange_ball_animation = new Animation(
                Texture.createTextureList(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/orange_ball_animation.png"), 101, 98, 101, 980, 10, 1.0f, false),
                reloadSpeed, false
        );
        Animation purple_ball_animation = new Animation(
                Texture.createTextureList(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/purple_ball_animation.png"), 101, 98, 101, 980, 10, 1.0f, false),
                reloadSpeed, false
        );
        Animation white_ball_animation = new Animation(
                Texture.createTextureList(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/white_ball_animation.png"), 101, 98, 101, 980, 10, 1.0f, false),
                reloadSpeed, false
        );
        Animation blue_ball_animation = new Animation(
                Texture.createTextureList(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/blue_ball_animation.png"), 101, 98, 101, 980, 10, 1.0f, false),
                reloadSpeed, false
        );
        Animation yellow_ball_animation = new Animation(
                Texture.createTextureList(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/yellow_ball_animation.png"), 101, 98, 101, 980, 10, 1.0f, false),
                reloadSpeed, false
        );

        // Animaciones de recarga por color
        this.reloadAnimations = new EnumMap<>(BubbleColor.class);
        this.reloadAnimations.put(BubbleColor.GREEN, green_ball_animation);
        this.reloadAnimations.put(BubbleColor.PINK, pink_ball_animation);
        this.reloadAnimations.put(BubbleColor.RED, red_ball_animation);
        this.reloadAnimations.put(BubbleColor.BLACK, black_ball_animation);
        this.reloadAnimations.put(BubbleColor.ORANGE, orange_ball_animation);
        this.reloadAnimations.put(BubbleColor.PURPLE, purple_ball_animation);
        this.reloadAnimations.put(BubbleColor.WHITE, white_ball_animation);
        this.reloadAnimations.put(BubbleColor.BLUE, blue_ball_animation);
        this.reloadAnimations.put(BubbleColor.YELLOW, yellow_ball_animation);

        // Animaciones adicionales
        this.startAnimation = new Animation(
                Texture.createTextureList(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/start_animation.png"), 101, 98, 101, 1078, 11, 1.0f, false),
                0.2f, true
        );
        this.winAnimation = new Animation(
                Texture.createTextureList(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/win_animation.png"), 101, 98, 101, 196, 2, 1.0f, false),
                0.5f, true
        );
        this.loseIdleAnimation = new Animation(
                Texture.createTextureList(Identifier.of(GeoWareMod.MOD_ID, "textures/games/bubblepuzzle/idle_animation.png"), 101, 98, 101, 392, 4, 1.0f, false),
                0.3f, true
        );
        this.currentAnimation = loseIdleAnimation;
    }

    public void startReload(BubbleColor color) {
        this.currentAnimation = reloadAnimations.get(color);
        this.currentAnimation.reset(); // Reiniciar la animación
        this.isReloading = true;
    }

    public void startGameBegin() {
        this.currentAnimation = startAnimation;
        this.currentAnimation.reset();
        this.isReloading = false;
    }

    public void startWin() {
        this.currentAnimation = winAnimation;
        this.currentAnimation.reset();
        this.isReloading = false;
    }

    public void startLose() {
        this.currentAnimation = loseIdleAnimation;
        this.currentAnimation.reset();
        this.isReloading = false;
    }

    public void update() {
        currentAnimation.update();
        if (isReloading && currentAnimation.isFinished()) {
            this.isReloading = false;
            this.currentAnimation = loseIdleAnimation;
        }
    }

    public void render(DrawContext context) {
        currentAnimation.renderAnimation(context, (int) x, (int) y, (int) width, (int) height);
    }
}