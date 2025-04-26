package com.github.razorplay01.geowaremod.games.guitarhero;

import com.github.razorplay01.geowaremod.GeoWareMod;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class BossBar {
    private int bbnosLife = 10;
    private int oclockLife = 10;
    private long bbnosDeathTime = -1; // Tiempo en ms cuando bbnosLife llega a 0
    private long oclockDeathTime = -1; // Tiempo en ms cuando oclockLife llega a 0
    private final List<Identifier> bbnosDeathFrames;
    private final List<Identifier> oclockDeathFrames;
    private static final long FRAME_DURATION = 300; // 500ms por frame
    private static final int FRAME_COUNT = 5; // 4 frames por animación

    public BossBar() {
        // Inicializar listas de texturas de animación
        bbnosDeathFrames = Arrays.asList(
                Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/bbn0/bbno10.png"),
                Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/bbn0/bbno11.png"),
                Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/bbn0/bbno12.png"),
                Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/bbn0/bbno13.png"),
                Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/bbn0/bbno14.png")
        );
        oclockDeathFrames = Arrays.asList(
                Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/oclock/oclock10.png"),
                Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/oclock/oclock11.png"),
                Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/oclock/oclock12.png"),
                Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/oclock/oclock13.png"),
                Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/oclock/oclock14.png")
        );
    }

    public void addbbnosLife(int value) {
        bbnosLife = Math.min(10, bbnosLife + value);
        if (bbnosLife > 0) {
            bbnosDeathTime = -1; // Resetear animación si recupera vida
        }
    }

    public void addoclockLife(int value) {
        oclockLife = Math.min(10, oclockLife + value);
        if (oclockLife > 0) {
            oclockDeathTime = -1; // Resetear animación si recupera vida
        }
    }

    public void removebbnosLife(int value) {
        bbnosLife = Math.max(0, bbnosLife - value);
        if (bbnosLife == 0 && bbnosDeathTime == -1) {
            bbnosDeathTime = System.currentTimeMillis(); // Iniciar animación
        }
    }

    public void removeoclockLife(int value) {
        oclockLife = Math.max(0, oclockLife - value);
        if (oclockLife == 0 && oclockDeathTime == -1) {
            oclockDeathTime = System.currentTimeMillis(); // Iniciar animación
        }
    }

    public Identifier getbbnosTexture() {
        return switch (bbnosLife) {
            case 0 -> Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/bbn0/bbno10.png");
            case 1 -> Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/bbn0/bbno9.png");
            case 2 -> Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/bbn0/bbno8.png");
            case 3 -> Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/bbn0/bbno7.png");
            case 4 -> Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/bbn0/bbno6.png");
            case 5 -> Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/bbn0/bbno5.png");
            case 6 -> Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/bbn0/bbno4.png");
            case 7 -> Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/bbn0/bbno3.png");
            case 8 -> Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/bbn0/bbno2.png");
            case 9 -> Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/bbn0/bbno1.png");
            default -> Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/bbn0/bbno0.png");
        };
    }

    public Identifier getoclockTexture() {
        return switch (oclockLife) {
            case 0 -> Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/oclock/oclock10.png");
            case 1 -> Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/oclock/oclock9.png");
            case 2 -> Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/oclock/oclock8.png");
            case 3 -> Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/oclock/oclock7.png");
            case 4 -> Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/oclock/oclock6.png");
            case 5 -> Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/oclock/oclock5.png");
            case 6 -> Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/oclock/oclock4.png");
            case 7 -> Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/oclock/oclock3.png");
            case 8 -> Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/oclock/oclock2.png");
            case 9 -> Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/oclock/oclock1.png");
            default -> Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/oclock/oclock0.png");
        };
    }

    public Identifier getbbnosDeathFrame(long currentTime) {
        if (bbnosDeathTime == -1) {
            return getbbnosTexture(); // No hay animación activa
        }
        long elapsed = currentTime - bbnosDeathTime;
        int frameIndex = Math.min((int) (elapsed / FRAME_DURATION), FRAME_COUNT - 1);
        return bbnosDeathFrames.get(frameIndex);
    }

    public Identifier getoclockDeathFrame(long currentTime) {
        if (oclockDeathTime == -1) {
            return getoclockTexture(); // No hay animación activa
        }
        long elapsed = currentTime - oclockDeathTime;
        int frameIndex = Math.min((int) (elapsed / FRAME_DURATION), FRAME_COUNT - 1);
        return oclockDeathFrames.get(frameIndex);
    }

    public void render(DrawContext context, Screen screen, long currentTime) {
        int totalWidth = 128 + 128;
        int xPos = (screen.width - totalWidth) / 2;

        // Renderizar bbnos
        context.drawTexture(
                getbbnosDeathFrame(currentTime),
                xPos,
                0,
                0,
                0,
                128,
                38,
                128,
                38
        );

        // Renderizar oclock
        context.drawTexture(
                getoclockDeathFrame(currentTime),
                xPos + 128,
                0,
                0,
                0,
                128,
                38,
                128,
                38
        );
    }
}