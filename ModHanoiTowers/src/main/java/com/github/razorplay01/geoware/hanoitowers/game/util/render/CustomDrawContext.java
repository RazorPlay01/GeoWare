package com.github.razorplay01.geoware.hanoitowers.game.util.render;

import com.github.razorplay01.geoware.hanoitowers.game.util.game.GameScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;

import java.awt.geom.Point2D;

public class CustomDrawContext extends DrawContext {
    public CustomDrawContext(MinecraftClient client, VertexConsumerProvider.Immediate vertexConsumers) {
        super(client, vertexConsumers);
    }

    public static CustomDrawContext wrap(DrawContext context) {
        return new CustomDrawContext(MinecraftClient.getInstance(), context.getVertexConsumers());
    }

    public void drawBasicBackground(GameScreen screen) {
        // Dibujar el fondo de la zona de juego
        fill(screen.getGameScreenXPos(),
                screen.getGameScreenYPos(),
                screen.getGameScreenXPos() + screen.getGame().getScreenWidth(),
                screen.getGameScreenYPos() + screen.getGame().getScreenHeight(),
                0xFF000000);

        // Dibujar el borde de la zona de juego
        drawBorder(screen.getGameScreenXPos() - 1,
                screen.getGameScreenYPos() - 1,
                screen.getGame().getScreenWidth() + 2,
                screen.getGame().getScreenHeight() + 2,
                0xFFFFFFFF);
    }

    public void drawLine(int x1, int y1, int x2, int y2, int color) {
        // Implementación básica de línea usando el algoritmo de Bresenham
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            // Dibujar un pixel
            this.fill(x1, y1, x1 + 1, y1 + 1, color);

            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    public void drawCircle(int centerX, int centerY, int radius, int color) {
        int x = radius;
        int y = 0;
        int err = 0;

        while (x >= y) {
            // Dibujar los 8 puntos simétricos del círculo
            this.fill(centerX + x, centerY + y, centerX + x + 1, centerY + y + 1, color);
            this.fill(centerX + y, centerY + x, centerX + y + 1, centerY + x + 1, color);
            this.fill(centerX - y, centerY + x, centerX - y + 1, centerY + x + 1, color);
            this.fill(centerX - x, centerY + y, centerX - x + 1, centerY + y + 1, color);
            this.fill(centerX - x, centerY - y, centerX - x + 1, centerY - y + 1, color);
            this.fill(centerX - y, centerY - x, centerX - y + 1, centerY - x + 1, color);
            this.fill(centerX + y, centerY - x, centerX + y + 1, centerY - x + 1, color);
            this.fill(centerX + x, centerY - y, centerX + x + 1, centerY - y + 1, color);

            if (err <= 0) {
                y += 1;
                err += 2 * y + 1;
            }
            if (err > 0) {
                x -= 1;
                err -= 2 * x + 1;
            }
        }
    }

    public void drawFilledCircle(int centerX, int centerY, int radius, int color) {
        // Dibujar el borde del círculo
        drawCircle(centerX, centerY, radius, color);

        // Rellenar el círculo
        int x = radius;
        int y = 0;
        int err = 0;

        while (x >= y) {
            // Dibujar líneas horizontales para rellenar el círculo
            this.fill(centerX - x, centerY + y, centerX + x, centerY + y + 1, color);
            this.fill(centerX - y, centerY + x, centerX + y, centerY + x + 1, color);
            this.fill(centerX - x, centerY - y, centerX + x, centerY - y + 1, color);
            this.fill(centerX - y, centerY - x, centerX + y, centerY - x + 1, color);

            if (err <= 0) {
                y += 1;
                err += 2 * y + 1;
            }
            if (err > 0) {
                x -= 1;
                err -= 2 * x + 1;
            }
        }
    }

    public void drawFilledCircleWithBorder(int centerX, int centerY, int radius, int fillColor, int borderColor) {
        // Dibujar el relleno del círculo
        drawFilledCircle(centerX, centerY, radius, fillColor);

        // Dibujar el borde del círculo
        drawCircle(centerX, centerY, radius, borderColor);
    }

    public void drawPolygon(Point2D[] points, int color) {
        // Dibujar los bordes del polígono
        for (int i = 0; i < points.length; i++) {
            Point2D current = points[i];
            Point2D next = points[(i + 1) % points.length];

            drawLine(
                    (int) current.getX(),
                    (int) current.getY(),
                    (int) next.getX(),
                    (int) next.getY(),
                    color
            );
        }

        // Opcional: rellenar el polígono
        fillPolygon(points, color);
    }

    private void fillPolygon(Point2D[] points, int color) {
        // Encontrar los límites del polígono
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Point2D point : points) {
            minX = Math.min(minX, (int) point.getX());
            maxX = Math.max(maxX, (int) point.getX());
            minY = Math.min(minY, (int) point.getY());
            maxY = Math.max(maxY, (int) point.getY());
        }

        // Comprobar cada punto dentro del rectángulo delimitador
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                if (isPointInPolygon(x, y, points)) {
                    fill(x, y, x + 1, y + 1, color);
                }
            }
        }
    }

    private boolean isPointInPolygon(int x, int y, Point2D[] points) {
        boolean inside = false;
        for (int i = 0, j = points.length - 1; i < points.length; j = i++) {
            if (((points[i].getY() > y) != (points[j].getY() > y)) &&
                    (x < (points[j].getX() - points[i].getX()) * (y - points[i].getY()) /
                            (points[j].getY() - points[i].getY()) + points[i].getX())) {
                inside = !inside;
            }
        }
        return inside;
    }

    public void drawRect(int x1, int y1, int x2, int y2, int color) {
        this.fill(x1, y1, x2, y1 + 1, color); // Borde superior
        this.fill(x1, y2 - 1, x2, y2, color); // Borde inferior
        this.fill(x1, y1, x1 + 1, y2, color); // Borde izquierdo
        this.fill(x2 - 1, y1, x2, y2, color); // Borde derecho
    }

    public void drawRoundedRect(int x1, int y1, int x2, int y2, int radius, int color) {
        // Dibujar los bordes rectos
        this.fill(x1 + radius, y1, x2 - radius, y1 + 1, color); // Borde superior
        this.fill(x1 + radius, y2 - 1, x2 - radius, y2, color); // Borde inferior
        this.fill(x1, y1 + radius, x1 + 1, y2 - radius, color); // Borde izquierdo
        this.fill(x2 - 1, y1 + radius, x2, y2 - radius, color); // Borde derecho

        // Dibujar las esquinas redondeadas
        this.drawCircle(x1 + radius, y1 + radius, radius, color); // Esquina superior izquierda
        this.drawCircle(x2 - radius, y1 + radius, radius, color); // Esquina superior derecha
        this.drawCircle(x1 + radius, y2 - radius, radius, color); // Esquina inferior izquierda
        this.drawCircle(x2 - radius, y2 - radius, radius, color); // Esquina inferior derecha
    }

    public void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        this.drawLine(x1, y1, x2, y2, color);
        this.drawLine(x2, y2, x3, y3, color);
        this.drawLine(x3, y3, x1, y1, color);
    }

    public void drawEllipse(int centerX, int centerY, int width, int height, int color) {
        int a = width / 2;
        int b = height / 2;
        int x = 0;
        int y = b;
        int a2 = a * a;
        int b2 = b * b;
        int err = b2 - (2 * b - 1) * a2;

        while (y >= 0) {
            // Dibujar los 4 puntos simétricos de la elipse
            this.fill(centerX + x, centerY + y, centerX + x + 1, centerY + y + 1, color);
            this.fill(centerX - x, centerY + y, centerX - x + 1, centerY + y + 1, color);
            this.fill(centerX + x, centerY - y, centerX + x + 1, centerY - y + 1, color);
            this.fill(centerX - x, centerY - y, centerX - x + 1, centerY - y + 1, color);

            if (err <= 0) {
                x++;
                err += (2 * x + 1) * b2;
            }
            if (err > 0) {
                y--;
                err -= (2 * y - 1) * a2;
            }
        }
    }

    public void drawPolygon(int centerX, int centerY, int radius, int sides, int color) {
        double angleIncrement = 2 * Math.PI / sides;
        int[] xPoints = new int[sides];
        int[] yPoints = new int[sides];

        for (int i = 0; i < sides; i++) {
            xPoints[i] = (int) (centerX + radius * Math.cos(i * angleIncrement));
            yPoints[i] = (int) (centerY + radius * Math.sin(i * angleIncrement));
        }

        for (int i = 0; i < sides; i++) {
            int next = (i + 1) % sides;
            this.drawLine(xPoints[i], yPoints[i], xPoints[next], yPoints[next], color);
        }
    }

    public void drawArc(int centerX, int centerY, int radius, int startAngle, int endAngle, int color) {
        for (int angle = startAngle; angle <= endAngle; angle++) {
            double rad = Math.toRadians(angle);
            int x = (int) (centerX + radius * Math.cos(rad));
            int y = (int) (centerY + radius * Math.sin(rad));
            this.fill(x, y, x + 1, y + 1, color);
        }
    }

    public void drawBezierCurve(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, int color) {
        for (float t = 0; t <= 1; t += 0.01) {
            int x = (int) (Math.pow(1 - t, 3) * x1 + 3 * Math.pow(1 - t, 2) * t * x2 + 3 * (1 - t) * Math.pow(t, 2) * x3 + Math.pow(t, 3) * x4);
            int y = (int) (Math.pow(1 - t, 3) * y1 + 3 * Math.pow(1 - t, 2) * t * y2 + 3 * (1 - t) * Math.pow(t, 2) * y3 + Math.pow(t, 3) * y4);
            this.fill(x, y, x + 1, y + 1, color);
        }
    }
}
