package SnakeGame2P.model.entity;

import SnakeGame2P.utils.Constants;
import java.awt.*;

/**
 * Ghost food (purple) — grow +1 + ghost mode 5 s.
 * Pulses at 50 % opacity with two white dots orbiting the body.
 */
public class GhostFood extends Food {

    @Override
    public void onConsumed(Snake eater, Snake opponent) {
        eater.grow();
        eater.activateGhostMode(Constants.GHOST_MODE_DURATION);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double lifeRatio = getLifeRatio();
        boolean warning = lifeRatio < 0.25;
        double period = warning ? 200.0 : 500.0;
        double pulse = getPulse(period);
        int S = Constants.UNIT_SIZE;
        int shrink = (int)(pulse * 3);
        int x = getX() + shrink;
        int y = getY() + shrink;
        int size = S - shrink * 2;
        int cx = getX() + S / 2;
        int cy = getY() + S / 2;

        Composite orig = g2d.getComposite();

        // Semi-transparent body to give the "ghost" feel
        float alpha = warning && (System.currentTimeMillis() / 250) % 2 == 0 ? 1.0f : 0.75f;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        Color body = warning && (System.currentTimeMillis() / 250) % 2 == 0
                ? new Color(255, 140, 0)
                : Constants.GHOST_FOOD_COLOR;
        g2d.setColor(body);
        g2d.fillOval(x, y, size, size);

        // Inner white core
        g2d.setColor(new Color(255, 255, 255, 180));
        int innerSize = size / 3;
        g2d.fillOval(cx - innerSize / 2, cy - innerSize / 2, innerSize, innerSize);

        g2d.setComposite(orig);

        // Two orbiting white dots
        long t = System.currentTimeMillis();
        double angle = t / 600.0;
        int orbitR = S / 2 + 4;
        int dotSize = 4;

        g2d.setColor(new Color(255, 255, 255, 200));
        int d1x = (int)(cx + Math.cos(angle) * orbitR) - dotSize / 2;
        int d1y = (int)(cy + Math.sin(angle) * orbitR) - dotSize / 2;
        g2d.fillOval(d1x, d1y, dotSize, dotSize);

        int d2x = (int)(cx + Math.cos(angle + Math.PI) * orbitR) - dotSize / 2;
        int d2y = (int)(cy + Math.sin(angle + Math.PI) * orbitR) - dotSize / 2;
        g2d.fillOval(d2x, d2y, dotSize, dotSize);
    }
}
