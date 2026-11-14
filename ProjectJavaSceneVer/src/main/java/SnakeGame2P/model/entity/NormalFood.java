package SnakeGame2P.model.entity;

import SnakeGame2P.utils.Constants;
import java.awt.*;

/**
 * Normal red food — grow +1.
 * Pulses in size and shows a shine spot; warns by flashing orange when almost expired.
 */
public class NormalFood extends Food {

    @Override
    public void onConsumed(Snake eater, Snake opponent) {
        eater.grow();
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double lifeRatio = getLifeRatio();
        boolean warning = lifeRatio < 0.25;
        double period = warning ? 200.0 : 450.0;
        double pulse = getPulse(period);              // 0→1 oscillation
        int S = Constants.UNIT_SIZE;
        int shrink = (int)(pulse * 3);                // pulsing radius ±3px
        int x = getX() + shrink;
        int y = getY() + shrink;
        int size = S - shrink * 2;

        // Glow ring behind the apple
        Color glowColor = warning
                ? new Color(255, 140, 0, 60)
                : new Color(255, 60, 60, 50);
        g2d.setColor(glowColor);
        g2d.fillOval(x - 3, y - 3, size + 6, size + 6);

        // Main body
        Color body = warning && (System.currentTimeMillis() / 250) % 2 == 0
                ? new Color(255, 140, 0)   // flash orange
                : Constants.FOOD_COLOR;
        g2d.setColor(body);
        g2d.fillOval(x, y, size, size);

        // Shine highlight (top-left quadrant)
        g2d.setColor(new Color(255, 255, 255, 160));
        g2d.fillOval(x + size / 5, y + size / 6, size / 4, size / 5);
    }
}
