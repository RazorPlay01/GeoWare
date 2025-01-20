package com.github.razorplay01.donkeykongfabric.game.entity.player;

import com.github.razorplay01.donkeykongfabric.DonkeyKongFabric;
import com.github.razorplay01.donkeykongfabric.game.util.Animation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class PlayerAnimationManager {
    private static final Identifier TEXTURE = Identifier.of(DonkeyKongFabric.MOD_ID, "textures/gui/game/player.png");

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

    private Animation currentAnimation;
    private boolean facingRight;

    public PlayerAnimationManager() {
        // Inicialización de todas las animaciones
        walkAnimationR = new Animation(36, 18, 18, 18, 2, 0.2f, true, true);
        walkAnimationL = new Animation(54, 18, 18, 18, 2, 0.2f, true, true);
        climbAnimation = new Animation(0, 0, 18, 18, 2, 0.1f, false, true);
        idleAnimationR = new Animation(0, 36, 18, 18, 1, 0.1f, false, true);
        idleAnimationL = new Animation(18, 36, 18, 18, 1, 0.1f, false, true);
        jumpAnimationR = new Animation(36, 0, 18, 18, 1, 0.1f, false, true);
        jumpAnimationL = new Animation(54, 0, 18, 18, 1, 0.1f, false, true);
        winAnimationR = new Animation(0, 18, 18, 18, 1, 0.1f, false, true);
        winAnimationL = new Animation(18, 18, 18, 18, 1, 0.1f, false, true);
        loseAnimation = new Animation(0, 54, 18, 18, 4, 0.5f, false, false);

        this.facingRight = true;
        this.currentAnimation = idleAnimationR;
    }

    public void updateAnimation(PlayerState state, boolean isMoving) {
        currentAnimation = getAnimationForState(state);

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
            default -> facingRight ? idleAnimationR : idleAnimationL;
        };
    }

    public void render(DrawContext context, float xPos, float yPos) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        context.drawTexture(
                TEXTURE,
                (int) xPos - 3,
                (int) yPos - 2,
                currentAnimation.getCurrentU(),
                currentAnimation.getCurrentV(),
                18,
                18,
                72,
                72
        );
    }

    public void setFacingDirection(boolean facingRight) {
        this.facingRight = facingRight;
    }

    public void resetAnimation() {
        currentAnimation.reset();
    }
}