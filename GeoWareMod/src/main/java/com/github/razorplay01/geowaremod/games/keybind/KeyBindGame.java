package com.github.razorplay01.geowaremod.games.keybind;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.razorplayapi.util.GameStatus;
import com.github.razorplay01.razorplayapi.util.Particle;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import com.github.razorplay01.razorplayapi.util.stage.Game;
import com.github.razorplay01.razorplayapi.util.texture.Animation;
import com.github.razorplay01.razorplayapi.util.texture.Texture;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import io.github.kosmx.emotes.api.events.client.ClientEmoteAPI;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.*;

@Getter
public class KeyBindGame extends Game {
    private static final int GAME_WIDTH = 272;
    private static final int GAME_HEIGHT = 50;
    private static final int CIRCLE_RADIUS = 12;
    private static final float CIRCLE_SPEED = 2.0f;
    private FinalCircle finalCircle;
    private final List<MovingCircle> movingCircles;
    private final List<Particle> particles; // Lista para partículas
    private final Random random;

    private final float scale = 1;

    private final Key wKey;
    private final Key aKey;
    private final Key sKey;
    private final Key dKey;
    private final Key upKey;
    private final Key leftKey;
    private final Key downKey;
    private final Key rightKey;

    public KeyBindGame(GameScreen screen, int timeLimitSeconds, int prevScore) {
        super(screen, 5, timeLimitSeconds, prevScore);
        movingCircles = new ArrayList<>();
        particles = new ArrayList<>();
        random = new Random();
        float animationSpeed = 0.0001f;
        float keyScale = scale;
        Identifier keysTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/keybind/keys.png");
        Identifier wAnimation = Identifier.of(GeoWareMod.MOD_ID, "textures/games/keybind/w_animation.png");
        Identifier aAnimation = Identifier.of(GeoWareMod.MOD_ID, "textures/games/keybind/a_animation.png");
        Identifier sAnimation = Identifier.of(GeoWareMod.MOD_ID, "textures/games/keybind/s_animation.png");
        Identifier dAnimation = Identifier.of(GeoWareMod.MOD_ID, "textures/games/keybind/d_animation.png");
        Identifier lAnimation = Identifier.of(GeoWareMod.MOD_ID, "textures/games/keybind/l_animation.png");
        Identifier jAnimation = Identifier.of(GeoWareMod.MOD_ID, "textures/games/keybind/j_animation.png");
        Identifier kAnimation = Identifier.of(GeoWareMod.MOD_ID, "textures/games/keybind/k_animation.png");
        Identifier hAnimation = Identifier.of(GeoWareMod.MOD_ID, "textures/games/keybind/h_animation.png");

        this.wKey = new Key(GLFW.GLFW_KEY_W,
                new Texture(keysTexture, 24, 0, 24, 24, 48, 96, keyScale),
                new Animation(List.of(
                        new Texture(wAnimation, 0, 0, 24, 24, 24, 144, keyScale),
                        new Texture(wAnimation, 0, 24, 24, 24, 24, 144, keyScale),
                        new Texture(wAnimation, 0, 48, 24, 24, 24, 144, keyScale),
                        new Texture(wAnimation, 0, 72, 24, 24, 24, 144, keyScale),
                        new Texture(wAnimation, 0, 96, 24, 24, 24, 144, keyScale),
                        new Texture(wAnimation, 0, 120, 24, 24, 24, 144, keyScale)),
                        animationSpeed, false));
        this.aKey = new Key(GLFW.GLFW_KEY_A,
                new Texture(keysTexture, 24, 72, 24, 24, 48, 96, keyScale),
                new Animation(List.of(
                        new Texture(aAnimation, 0, 0, 24, 24, 24, 144, keyScale),
                        new Texture(aAnimation, 0, 24, 24, 24, 24, 144, keyScale),
                        new Texture(aAnimation, 0, 48, 24, 24, 24, 144, keyScale),
                        new Texture(aAnimation, 0, 72, 24, 24, 24, 144, keyScale),
                        new Texture(aAnimation, 0, 96, 24, 24, 24, 144, keyScale),
                        new Texture(aAnimation, 0, 120, 24, 24, 24, 144, keyScale)),
                        animationSpeed, false));
        this.sKey = new Key(GLFW.GLFW_KEY_S,
                new Texture(keysTexture, 24, 24, 24, 24, 48, 96, keyScale),
                new Animation(List.of(
                        new Texture(sAnimation, 0, 0, 24, 24, 24, 144, keyScale),
                        new Texture(sAnimation, 0, 24, 24, 24, 24, 144, keyScale),
                        new Texture(sAnimation, 0, 48, 24, 24, 24, 144, keyScale),
                        new Texture(sAnimation, 0, 72, 24, 24, 24, 144, keyScale),
                        new Texture(sAnimation, 0, 96, 24, 24, 24, 144, keyScale),
                        new Texture(sAnimation, 0, 120, 24, 24, 24, 144, keyScale)),
                        animationSpeed, false));
        this.dKey = new Key(GLFW.GLFW_KEY_D,
                new Texture(keysTexture, 24, 48, 24, 24, 48, 96, keyScale),
                new Animation(List.of(
                        new Texture(dAnimation, 0, 0, 24, 24, 24, 144, keyScale),
                        new Texture(dAnimation, 0, 24, 24, 24, 24, 144, keyScale),
                        new Texture(dAnimation, 0, 48, 24, 24, 24, 144, keyScale),
                        new Texture(dAnimation, 0, 72, 24, 24, 24, 144, keyScale),
                        new Texture(dAnimation, 0, 96, 24, 24, 24, 144, keyScale),
                        new Texture(dAnimation, 0, 120, 24, 24, 24, 144, keyScale)),
                        animationSpeed, false));
        this.upKey = new Key(GLFW.GLFW_KEY_L,
                new Texture(keysTexture, 0, 0, 24, 24, 48, 96, keyScale),
                new Animation(List.of(
                        new Texture(lAnimation, 0, 0, 24, 24, 24, 144, keyScale),
                        new Texture(lAnimation, 0, 24, 24, 24, 24, 144, keyScale),
                        new Texture(lAnimation, 0, 48, 24, 24, 24, 144, keyScale),
                        new Texture(lAnimation, 0, 72, 24, 24, 24, 144, keyScale),
                        new Texture(lAnimation, 0, 96, 24, 24, 24, 144, keyScale),
                        new Texture(lAnimation, 0, 120, 24, 24, 24, 144, keyScale)),
                        animationSpeed, false));
        this.leftKey = new Key(GLFW.GLFW_KEY_J,
                new Texture(keysTexture, 0, 72, 24, 24, 48, 96, keyScale),
                new Animation(List.of(
                        new Texture(jAnimation, 0, 0, 24, 24, 24, 144, keyScale),
                        new Texture(jAnimation, 0, 24, 24, 24, 24, 144, keyScale),
                        new Texture(jAnimation, 0, 48, 24, 24, 24, 144, keyScale),
                        new Texture(jAnimation, 0, 72, 24, 24, 24, 144, keyScale),
                        new Texture(jAnimation, 0, 96, 24, 24, 24, 144, keyScale),
                        new Texture(jAnimation, 0, 120, 24, 24, 24, 144, keyScale)),
                        animationSpeed, false));
        this.downKey = new Key(GLFW.GLFW_KEY_K,
                new Texture(keysTexture, 0, 24, 24, 24, 48, 96, keyScale),
                new Animation(List.of(
                        new Texture(kAnimation, 0, 0, 24, 24, 24, 144, keyScale),
                        new Texture(kAnimation, 0, 24, 24, 24, 24, 144, keyScale),
                        new Texture(kAnimation, 0, 48, 24, 24, 24, 144, keyScale),
                        new Texture(kAnimation, 0, 72, 24, 24, 24, 144, keyScale),
                        new Texture(kAnimation, 0, 96, 24, 24, 24, 144, keyScale),
                        new Texture(kAnimation, 0, 120, 24, 24, 24, 144, keyScale)),
                        animationSpeed, false));
        this.rightKey = new Key(GLFW.GLFW_KEY_H,
                new Texture(keysTexture, 0, 48, 24, 24, 48, 96, keyScale),
                new Animation(List.of(
                        new Texture(hAnimation, 0, 0, 24, 24, 24, 144, keyScale),
                        new Texture(hAnimation, 0, 24, 24, 24, 24, 144, keyScale),
                        new Texture(hAnimation, 0, 48, 24, 24, 24, 144, keyScale),
                        new Texture(hAnimation, 0, 72, 24, 24, 24, 144, keyScale),
                        new Texture(hAnimation, 0, 96, 24, 24, 24, 144, keyScale),
                        new Texture(hAnimation, 0, 120, 24, 24, 24, 144, keyScale)),
                        animationSpeed, false));
    }

    @Override
    public void init() {
        super.init();
        finalCircle = new FinalCircle(screen.getGameScreenXPos() + 20, screen.getGameScreenYPos() + 29, CIRCLE_RADIUS);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // finalCircle.draw(context); // No dibujamos finalCircle porque está en el fondo
        for (MovingCircle circle : movingCircles) {
            circle.draw(context);
        }
        // Renderizar partículas
        for (Particle particle : particles) {
            particle.render(context);
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier marcoTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/keybind/fondo.png");
        context.drawTexture(marcoTexture, screen.getGameScreenXPos(), screen.getGameScreenYPos(),
                getScreenWidth(), getScreenHeight(), 0, 0, GAME_WIDTH, GAME_HEIGHT, GAME_WIDTH, GAME_HEIGHT);
    }

    @Override
    public int getScreenWidth() {
        return GAME_WIDTH;
    }

    @Override
    public int getScreenHeight() {
        return GAME_HEIGHT;
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        Iterator<MovingCircle> iterator = movingCircles.iterator();
        while (iterator.hasNext()) {
            MovingCircle circle = iterator.next();
            if (circle.checkKey(keyCode)) {
                iterator.remove(); // Eliminar el círculo inmediatamente
                break; // Solo procesar el primer círculo coincidente
            }
        }
    }

    @Override
    public void update() {
        super.update();

        if (status == GameStatus.ACTIVE) {
            // Generar nuevos círculos
            if (random.nextInt(100) < 5) {
                Key randomKey = getRandomKey();
                if (!isCircleAtSpawnPosition()) {
                    movingCircles.add(new MovingCircle(
                            CIRCLE_RADIUS, CIRCLE_SPEED, randomKey.keyCode(),
                            randomKey.keyTexture(), randomKey.keyAnimation(), this));
                }
            }

            // Actualizar círculos
            ArrayList<MovingCircle> circlesToRemove = new ArrayList<>();
            for (MovingCircle circle : movingCircles) {
                circle.update();
                if (!circle.isActive()) {
                    circlesToRemove.add(circle);
                }
            }
            movingCircles.removeAll(circlesToRemove);

            // Actualizar partículas
            particles.removeIf(Particle::isFinished);
            for (Particle particle : particles) {
                particle.update();
            }
        }
    }

    public void addParticle(Particle particle) {
        particles.add(particle);
    }

    private boolean isCircleAtSpawnPosition() {
        for (MovingCircle circle : movingCircles) {
            if (Math.abs(circle.getXPos() - (getScreen().getGameScreenXPos() + getScreenWidth() - CIRCLE_RADIUS * 2 - 5)) < CIRCLE_RADIUS * 2) {
                return true;
            }
        }
        return false;
    }

    private Key getRandomKey() {
        Key[] validKeys = {wKey, aKey, sKey, dKey, upKey, leftKey, downKey, rightKey};
        return validKeys[random.nextInt(validKeys.length)];
    }

    public void playRandomEmote() {
        Collection<KeyframeAnimation> emoteList = ClientEmoteAPI.clientEmoteList();
        if (emoteList != null && !emoteList.isEmpty()) {
            KeyframeAnimation[] emotes = emoteList.toArray(new KeyframeAnimation[0]);
            KeyframeAnimation randomEmote = emotes[random.nextInt(emotes.length)];
            ClientEmoteAPI.playEmote(randomEmote);
        }
    }
}