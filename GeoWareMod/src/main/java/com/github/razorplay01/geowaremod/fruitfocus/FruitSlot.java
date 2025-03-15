package com.github.razorplay01.geowaremod.fruitfocus;

import com.github.razorplay01.razorplayapi.util.hitbox.CircleHitbox;
import com.github.razorplay01.razorplayapi.util.render.CustomDrawContext;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

@Getter
@Setter
@AllArgsConstructor
public class FruitSlot {
    private Fruit fruit;
    private float xPos;
    private float yPos;
    private boolean isHidden;
    private CircleHitbox hitbox;
    private int color;
    private GameScreen screen;


    public void render(DrawContext context) {
        // Renderizar el círculo
        CustomDrawContext customDrawContext = CustomDrawContext.wrap(context);
        customDrawContext.drawFilledCircle((int) xPos, (int) yPos, (int) hitbox.getRadius(), color);

        // Solo mostrar el texto si el slot no está oculto o está descubierto
        if (!isHidden || ((FruitFocusGame) screen.getGame()).getDiscoveredSlots().contains(this)) {
            // Renderizar el texto de la fruta
            Text text = Text.literal(fruit.getName());
            int textWidth = screen.getGame().getTextRenderer().getWidth(text);
            float textX = hitbox.getXPos() - textWidth / 2f;
            float textY = hitbox.getYPos() - 4; // Ajusta según necesites
            context.drawText(screen.getGame().getTextRenderer(), text, (int) textX, (int) textY, 0xFFFFFFFF, true);
        }
    }
}
