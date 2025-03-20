package com.github.razorplay01.geowaremod.donkeykong.game.entity.player;

import com.github.razorplay01.geowaremod.donkeykong.game.entity.DonkeyKongEntity;
import com.github.razorplay01.razorplayapi.util.texture.Animation;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;

import static com.github.razorplay01.geowaremod.donkeykong.game.entity.DonkeyKongEntity.renderTexture;
import static com.github.razorplay01.geowaremod.donkeykong.game.util.TextureProvider.*;

public class PlayerAnimationManager {
    private final Animation walkAnimationR;
    private final Animation walkAnimationL;
    private final Animation climbAnimation;
    private final Animation idleAnimationR;
    private final Animation idleAnimationL;
    private final Animation jumpAnimationR;
    private final Animation jumpAnimationL;
    private final Animation winAnimationR;
    private final Animation winAnimationL;
    private final Animation loseAnimation;
    private final Animation hammetAnimationR;
    private final Animation hammetAnimationL;
    private final Animation hammetWalkAnimationR;
    private final Animation hammetWalkAnimationL;

    private Animation currentAnimation;
    @Getter
    private boolean facingRight;
    private final Player player;

    public PlayerAnimationManager(Player player) {
        // Inicialización de todas las animaciones
        walkAnimationR = new Animation(PLAYER_WALK_R_TEXTURES, 0.01f, true);
        walkAnimationL = new Animation(PLAYER_WALK_L_TEXTURES, 0.01f, true);
        climbAnimation = new Animation(PLAYER_CLIMB_TEXTURES, 0.01f, true);
        idleAnimationR = new Animation(PLAYER_IDLE_R_TEXTURES, 1f, true);
        idleAnimationL = new Animation(PLAYER_IDLE_L_TEXTURES, 1f, true);
        jumpAnimationR = new Animation(PLAYER_JUMP_R_TEXTURES, 1f, true);
        jumpAnimationL = new Animation(PLAYER_JUMP_L_TEXTURES, 1f, true);
        winAnimationR = new Animation(PLAYER_WIN_R_TEXTURES, 1f, true);
        winAnimationL = new Animation(PLAYER_WIN_L_TEXTURES, 1f, true);
        loseAnimation = new Animation(PLAYER_DIE_TEXTURES, 0.05f, false);
        hammetAnimationR = new Animation(PLAYER_IDLE_HAMMET_R_TEXTURES, 0.01f, true);
        hammetAnimationL = new Animation(PLAYER_IDLE_HAMMET_L_TEXTURES, 0.01f, true);
        hammetWalkAnimationR = new Animation(PLAYER_WALK_HAMMET_R_TEXTURES, 0.01f, true);
        hammetWalkAnimationL = new Animation(PLAYER_WALK_HAMMET_L_TEXTURES, 0.01f, true);

        this.facingRight = true;
        this.currentAnimation = idleAnimationR;
        this.player = player;
    }

    public void updateAnimation(PlayerState state, boolean isMoving) {
        Animation nextAnimation;

        if (state == PlayerState.WITH_HAMMER) {
            if (Math.abs(player.getVelocityX()) > 0) {
                nextAnimation = facingRight ? hammetWalkAnimationR : hammetWalkAnimationL;
            } else {
                nextAnimation = facingRight ? hammetAnimationR : hammetAnimationL;
            }
        } else {
            nextAnimation = getAnimationForState(state);
        }

        if (nextAnimation != currentAnimation) {
            currentAnimation = nextAnimation;
            currentAnimation.reset();
        }

        if (state == PlayerState.CLIMBING) {
            if (isMoving) {
                currentAnimation.update();
            } else {
                currentAnimation.setCurrentFrame(0);
            }
        } else {
            currentAnimation.update();
        }
    }

    public Animation getAnimationForState(PlayerState state) {
        return switch (state) {
            case WALKING -> facingRight ? walkAnimationR : walkAnimationL;
            case CLIMBING -> climbAnimation;
            case JUMPING -> facingRight ? jumpAnimationR : jumpAnimationL;
            case WINNING -> facingRight ? winAnimationR : winAnimationL;
            case LOSING -> loseAnimation;
            case WITH_HAMMER -> {
                if (Math.abs(player.getVelocityX()) > 0) {  // Si se está moviendo
                    yield facingRight ? hammetWalkAnimationR : hammetWalkAnimationL;
                } else {
                    yield facingRight ? hammetAnimationR : hammetAnimationL;
                }
            }
            default -> facingRight ? idleAnimationR : idleAnimationL;
        };
    }

    public void render(DrawContext context, DonkeyKongEntity player) {
        int xOffset = 0;
        int yOffset = 0;
        if (currentAnimation == hammetAnimationL || currentAnimation == hammetAnimationR ||
                currentAnimation == hammetWalkAnimationL || currentAnimation == hammetWalkAnimationR) {
            yOffset = -6;
        } else {
            yOffset = -1;
        }
        renderTexture(context, player, currentAnimation, xOffset, yOffset);
    }

    public void setFacingDirection(boolean facingRight) {
        this.facingRight = facingRight;
    }
}