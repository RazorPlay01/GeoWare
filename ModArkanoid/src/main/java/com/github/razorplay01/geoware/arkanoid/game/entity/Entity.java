package com.github.razorplay01.geoware.arkanoid.game.entity;

import com.github.razorplay01.geoware.arkanoid.game.mapobject.MapObject;
import com.github.razorplay01.geoware.arkanoid.game.util.Animation;
import com.github.razorplay01.geoware.arkanoid.game.util.ScreenSide;
import com.github.razorplay01.geoware.arkanoid.game.util.records.Hitbox;
import com.github.razorplay01.geoware.arkanoid.game.util.records.IHitbox;
import com.github.razorplay01.geoware.arkanoid.game.util.texture.Texture;
import com.github.razorplay01.geoware.arkanoid.screen.GameScreen;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import java.util.ArrayList;
import java.util.List;

import static com.github.razorplay01.geoware.arkanoid.Arkanoid.IS_DEBUG_MODE_ENABLE;

@Getter
@Setter
public abstract class Entity implements IHitbox {
    protected float xPos;
    protected float yPos;
    protected float width;
    protected float height;

    protected float velocityX = 0;
    protected float velocityY = 0;

    protected GameScreen gameScreen;

    protected final List<Hitbox> hitboxes = new ArrayList<>();

    // Constantes comunes
    protected float gravity = 0.5f;
    protected float maxFallSpeed = 4f;
    protected float speed = 1f;

    protected int color;

    protected Entity(float xPos, float yPos, float width, float height, GameScreen gameScreen, int debugColor) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        this.gameScreen = gameScreen;
        this.color = debugColor;
        this.hitboxes.add(new Hitbox(Hitbox.HITBOX_DEFAULT, xPos, yPos, width, height, 0, 0, debugColor));
    }

    /**
     * Actualiza las posiciones de los hitboxes en función de la posición actual de la entidad.
     */
    @Override
    public void updateHitboxes() {
        for (int i = 0; i < hitboxes.size(); i++) {
            Hitbox hitbox = hitboxes.get(i);

            hitboxes.set(i, new Hitbox(
                    hitbox.name(),
                    xPos + hitbox.xOffset(),
                    yPos + hitbox.yOffset(),
                    hitbox.width(),
                    hitbox.height(),
                    hitbox.xOffset(),
                    hitbox.yOffset(),
                    hitbox.color()
            ));
        }
    }

    /**
     * Verifica colisiones con objetos del tipo `MapObject` basándose en un hitbox específico.
     *
     * @param mapObjects Lista de objetos para verificar colisiones.
     * @param hitboxName Nombre del hitbox que se usará para la colisión.
     * @return El primer objeto con el que colisiona, o null si no hay colisión.
     */
    protected MapObject checkObjectCollision(List<? extends MapObject> mapObjects, String hitboxName) {
        Hitbox targetHitbox = getHitboxByName(hitboxName); // Usar el Hitbox con el nombre especificado
        if (targetHitbox == null) return null;

        for (MapObject mapObject : mapObjects) {
            Hitbox mapObjectHitbox = mapObject.getDefaultHitbox(); // Obtener el Hitbox por defecto de la escalera
            if (mapObjectHitbox != null && targetHitbox.intersects(mapObjectHitbox)) {
                return mapObject; // Retorna el objeto con el que colisiona
            }
        }
        return null;
    }

    /**
     * Verifica colisiones con otras entidades basándose en un hitbox específico.
     *
     * @param entities   Lista de entidades para verificar colisiones.
     * @param hitboxName Nombre del hitbox que se usará para la colisión.
     * @return La primera entidad con la que colisiona, o null si no hay colisión.
     */
    protected Entity checkEntityCollision(List<? extends Entity> entities, String hitboxName) {
        Hitbox targetHitbox = getHitboxByName(hitboxName);
        if (targetHitbox == null) return null;

        for (Entity entity : entities) {
            if (entity == this) continue;

            Hitbox entityHitbox = entity.getHitboxByName(Hitbox.HITBOX_DEFAULT);
            if (entityHitbox != null && targetHitbox.intersects(entityHitbox)) {
                return entity;
            }
        }
        return null;
    }

    /**
     * Maneja la colisión con los bordes de la pantalla.
     */
    protected void verifyScreenBoundsCollision() {
        int screenX = gameScreen.getTestGame().getScreen().getScreenXPos();
        int screenY = gameScreen.getTestGame().getScreen().getScreenYPos();
        int screenWidth = gameScreen.getTestGame().getScreenWidth();
        int screenHeight = gameScreen.getTestGame().getScreenHeight();

        // Colisión con el borde izquierdo
        if (xPos < screenX) {
            xPos = screenX;
            onScreenBoundaryCollision(ScreenSide.LEFT);
            return;
        }

        // Colisión con el borde derecho
        if (xPos + width > screenX + screenWidth) {
            xPos = screenX + screenWidth - width;
            onScreenBoundaryCollision(ScreenSide.RIGHT);
            return;
        }

        // Colisión con el borde superior
        if (yPos < screenY) {
            yPos = screenY;
            onScreenBoundaryCollision(ScreenSide.TOP);
            return;
        }

        // Colisión con el borde inferior
        if (yPos + height > screenY + screenHeight) {
            yPos = screenY + screenHeight - height;
            onScreenBoundaryCollision(ScreenSide.BOTTOM);
        }
    }

    /**
     * Método llamado cuando la entidad colisiona con un borde de la pantalla.
     * Puede ser sobrescrito por las subclases para personalizar el comportamiento.
     *
     * @param side El lado de la pantalla con el que colisiona: "left", "right", "top", o "bottom".
     */
    protected void onScreenBoundaryCollision(ScreenSide side) {
        if (side.equals(ScreenSide.LEFT) || side.equals(ScreenSide.RIGHT)) {
            velocityX = 0;
        } else if (side.equals(ScreenSide.TOP) || side.equals(ScreenSide.BOTTOM)) {
            velocityY = 0;
        }
    }

    /**
     * Obtiene un `Hitbox` por su nombre.
     */
    @Override
    public Hitbox getHitboxByName(String name) {
        for (Hitbox hitbox : hitboxes) {
            if (hitbox.name().equals(name)) {
                return hitbox;
            }
        }
        return null;
    }

    @Override
    public Hitbox getDefaultHitbox() {
        for (Hitbox hitbox : hitboxes) {
            if (hitbox.name().equals(Hitbox.HITBOX_DEFAULT)) {
                return hitbox;
            }
        }
        return null;
    }

    protected abstract void update();

    public void render(DrawContext context) {
        if (IS_DEBUG_MODE_ENABLE) {
            renderHitboxes(context);
        }
    }

    public void renderHitboxes(DrawContext context) {
        for (Hitbox hitbox : hitboxes) {
            context.fill(
                    (int) hitbox.xPos(),
                    (int) hitbox.yPos(),
                    (int) (hitbox.xPos() + hitbox.width()),
                    (int) (hitbox.yPos() + hitbox.height()),
                    hitbox.color() // Usar el color especificado
            );

            // Opcional: dibujar el borde de la hitbox
            context.drawBorder(
                    (int) hitbox.xPos(),
                    (int) hitbox.yPos(),
                    (int) hitbox.width(),
                    (int) hitbox.height(),
                    hitbox.color() | 0xFF000000 // Hacer el borde más opaco
            );
        }
    }

    public static void renderTexture(DrawContext context, Entity entity, Animation currentAnimation, int xOffset, int yOffset) {
        Texture currentTexture = currentAnimation.getCurrentTexture();

        // Calcular dimensiones escaladas de la textura
        int width = (int) (currentAnimation.getFrameWidth() * currentTexture.scale());
        int height = (int) (currentAnimation.getFrameHeight() * currentTexture.scale());

        // Calculo para centrar la textura en la entidad
        int centeredX = (int) (entity.getXPos() + xOffset + (entity.getWidth() - width) / 2.0);
        int centeredY = (int) (entity.getYPos() + yOffset + (entity.getHeight() - height) / 2.0);

        context.drawTexture(
                currentTexture.identifier(),
                centeredX,
                centeredY,
                width,
                height,
                currentAnimation.getCurrentU(),
                currentAnimation.getCurrentV(),
                currentAnimation.getFrameWidth(),
                currentAnimation.getFrameHeight(),
                currentTexture.textureWidth(),
                currentTexture.textureHeight()
        );
    }
}