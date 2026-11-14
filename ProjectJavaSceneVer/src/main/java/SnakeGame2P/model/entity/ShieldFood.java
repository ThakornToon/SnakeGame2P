package SnakeGame2P.model.entity;

import SnakeGame2P.utils.Constants;
import java.awt.*;

/**
 * Shield food (gold) — grow +1 + absorb next collision.
 * Shows a pulsing gold disc with two rotating arc decorations.
 */
public class ShieldFood extends Food {

    @Override
    public void onConsumed(Snake eater, Snake opponent) {
        eater.grow();
        eater.activateShield();
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

        // Gold glow
        g2d.setColor(new Color(255, 215, 0, 55));
        g2d.fillOval(x - 4, y - 4, size + 8, size + 8);

        // Main body
        Color body = warning && (System.currentTimeMillis() / 250) % 2 == 0
                ? new Color(255, 100, 0)
                : Constants.SHIELD_FOOD_COLOR;
        g2d.setColor(body);
        g2d.fillOval(x, y, size, size);

        // Shine
        g2d.setColor(new Color(255, 255, 255, 180));
        g2d.fillOval(x + size / 5, y + size / 6, size / 4, size / 5);

        // Two rotating arcs (shield emblem)
        int arcAngle = (int)(System.currentTimeMillis() / 8) % 360;
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setColor(new Color(255, 255, 200, 200));
        g2d.drawArc(x - 3, y - 3, size + 6, size + 6, arcAngle, 70);
        g2d.drawArc(x - 3, y - 3, size + 6, size + 6, arcAngle + 180, 70);
        g2d.setStroke(oldStroke);
    }
}
