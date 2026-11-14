package SnakeGame2P.model.entity;

import SnakeGame2P.utils.Constants;
import java.awt.*;

/**
 * Freeze food (ice blue) — grow +1 + freeze opponent 3 s.
 * Shows a pulsing ice disc with a six-pointed snowflake radiating from the centre.
 */
public class FreezeFood extends Food {

    @Override
    public void onConsumed(Snake eater, Snake opponent) {
        eater.grow();
        opponent.freeze(Constants.FREEZE_DURATION);
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

        // Ice glow
        g2d.setColor(new Color(173, 216, 230, 70));
        g2d.fillOval(x - 4, y - 4, size + 8, size + 8);

        // Main body
        Color body = warning && (System.currentTimeMillis() / 250) % 2 == 0
                ? new Color(255, 140, 0)
                : Constants.FREEZE_FOOD_COLOR;
        g2d.setColor(body);
        g2d.fillOval(x, y, size, size);

        // Shine
        g2d.setColor(new Color(255, 255, 255, 180));
        g2d.fillOval(x + size / 5, y + size / 6, size / 4, size / 5);

        // Six-point snowflake radiating from centre
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setColor(new Color(224, 255, 255, 220));
        int armLen = size / 2 - 1;
        // Slowly rotate the snowflake
        double rotOffset = (System.currentTimeMillis() / 2000.0) * Math.PI;
        for (int i = 0; i < 6; i++) {
            double angle = rotOffset + Math.PI * i / 3;
            int ex = (int)(cx + Math.cos(angle) * armLen);
            int ey = (int)(cy + Math.sin(angle) * armLen);
            g2d.drawLine(cx, cy, ex, ey);
            // Small cross-bar at 60% of each arm
            double barLen = armLen * 0.35;
            int bx = (int)(cx + Math.cos(angle) * armLen * 0.6);
            int by = (int)(cy + Math.sin(angle) * armLen * 0.6);
            int b1x = (int)(bx + Math.cos(angle + Math.PI / 2) * barLen / 2);
            int b1y = (int)(by + Math.sin(angle + Math.PI / 2) * barLen / 2);
            int b2x = (int)(bx + Math.cos(angle - Math.PI / 2) * barLen / 2);
            int b2y = (int)(by + Math.sin(angle - Math.PI / 2) * barLen / 2);
            g2d.drawLine(b1x, b1y, b2x, b2y);
        }
        g2d.setStroke(oldStroke);
    }
}
