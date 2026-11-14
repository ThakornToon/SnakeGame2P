package SnakeGame2P.view.renderer;

import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.model.entity.Food;
import SnakeGame2P.model.entity.Bullet;
import SnakeGame2P.view.GamePanel;
import SnakeGame2P.utils.Constants;
import java.awt.*;
import java.util.List;

/**
 * Renderer for the in-game playing screen.
 *
 * Layout (1000×700 board):
 *   - P1 panel  : top-left  (x=5,   y=5,  w=230, h=68) — green border
 *   - P2 panel  : top-right (x=765, y=5,  w=230, h=68) — orange border
 *   - Timer     : top-center (centered), with ESC hint below it
 *   - Status row: y≈80, compact badges for active power-up effects
 *   - Grid + game objects rendered below HUD
 */
public class PlayingRenderer implements GameRendererStrategy {
    private final GamePanel panel;

    // Panel geometry constants
    private static final int PANEL_W = 230;
    private static final int PANEL_H = 68;
    private static final int PANEL_Y = 5;
    private static final int P1_PANEL_X = 5;
    private static final int P2_PANEL_X = Constants.BOARD_WIDTH - PANEL_W - 5;

    public PlayingRenderer(GamePanel panel) {
        this.panel = panel;
    }

    @Override
    public void render(Graphics g, Snake snake1, Snake snake2, Food food, String additionalData) {
        if (snake1 == null || snake2 == null) return;

        drawGrid(g);

        // HUD layers (behind game objects for visual clarity)
        drawPlayerPanel(g, snake1, "P1 (Green)",
                P1_PANEL_X, PANEL_Y, Constants.SNAKE1_HEAD_COLOR,
                "SPACE", Constants.SNAKE1_HEAD_COLOR);
        drawPlayerPanel(g, snake2, "P2 (Orange)",
                P2_PANEL_X, PANEL_Y, Constants.SNAKE2_HEAD_COLOR,
                "/", Constants.SNAKE2_HEAD_COLOR);
        drawTimer(g);
        drawStatusBadges(g, snake1, snake2);

        // Game objects on top of HUD
        if (food != null) food.draw(g);
        snake1.draw(g);
        snake2.draw(g);
        drawBullets(g);
    }

    // ────────────────────────────────────────────────────────── Grid

    private void drawGrid(Graphics g) {
        g.setColor(Constants.GRID_COLOR);
        for (int i = 0; i < Constants.BOARD_WIDTH / Constants.UNIT_SIZE; i++) {
            g.drawLine(i * Constants.UNIT_SIZE, 0, i * Constants.UNIT_SIZE, Constants.BOARD_HEIGHT);
        }
        for (int i = 0; i < Constants.BOARD_HEIGHT / Constants.UNIT_SIZE; i++) {
            g.drawLine(0, i * Constants.UNIT_SIZE, Constants.BOARD_WIDTH, i * Constants.UNIT_SIZE);
        }
    }

    // ────────────────────────────────────────────────────────── Player panel

    /**
     * Draws a dark rounded panel showing the player's score, length, combo, and shoot status.
     */
    private void drawPlayerPanel(Graphics g, Snake snake, String label,
                                  int px, int py, Color borderColor,
                                  String shootKey, Color headerColor) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Stroke savedStroke = g2d.getStroke();

        // Background + border
        g2d.setColor(new Color(0, 0, 0, 170));
        g2d.fillRoundRect(px, py, PANEL_W, PANEL_H, 10, 10);
        g2d.setColor(new Color(borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), 200));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRoundRect(px, py, PANEL_W, PANEL_H, 10, 10);
        g2d.setStroke(savedStroke);

        int lx = px + 9;

        // Header
        g2d.setColor(headerColor);
        g2d.setFont(new Font("Arial", Font.BOLD, 13));
        g2d.drawString(label, lx, py + 17);

        // Score
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 13));
        g2d.drawString("Score: " + snake.getFoodEaten(), lx, py + 35);

        // Length
        g2d.drawString("Length: " + snake.getLength(), lx, py + 52);

        // Combo indicator (right portion of panel)
        int combo = snake.getComboCount();
        if (combo > 1) {
            g2d.setColor(new Color(255, 215, 0));
            g2d.setFont(new Font("Arial", Font.BOLD, 13));
            g2d.drawString("x" + combo + " COMBO!", px + 135, py + 35);
        }

        // Shoot status
        long cd = snake.getShootCooldownRemaining();
        boolean canLengthShoot = snake.getLength() >= 6;
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        if (!canLengthShoot) {
            g2d.setColor(new Color(220, 60, 60));
            g2d.drawString("[" + shootKey + "] too short", px + 120, py + 52);
        } else if (cd > 0) {
            g2d.setColor(Color.YELLOW);
            g2d.drawString(String.format("[%s] CD %.1fs", shootKey, cd / 1000.0), px + 110, py + 52);
        } else {
            g2d.setColor(new Color(80, 210, 80));
            g2d.drawString("[" + shootKey + "] ready!", px + 120, py + 52);
        }
    }

    // ────────────────────────────────────────────────────────── Timer

    /**
     * Draws the countdown timer centered at the top, with an ESC hint below.
     * Turns orange in the fast phase (last 60 s) and blinks red in the last 10 s.
     */
    private void drawTimer(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int msLeft = panel.getRemainingTimeMs();
        int totalSeconds = msLeft / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String timeStr = String.format("%02d:%02d", minutes, seconds);

        boolean fastPhase = msLeft <= Constants.FAST_PHASE_THRESHOLD_SEC * 1000;
        boolean warn = totalSeconds <= 10;
        boolean blink = (System.currentTimeMillis() / 250) % 2 == 0;

        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        int tw = fm.stringWidth(timeStr);
        int tx = (Constants.BOARD_WIDTH - tw) / 2;
        int ty = 32;

        // Timer box
        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.fillRoundRect(tx - 10, ty - 22, tw + 20, 30, 8, 8);

        // Box border: blinks red when ≤10 s
        if (warn) {
            g2d.setColor(blink ? new Color(255, 0, 0, 180) : new Color(80, 0, 0, 100));
        } else if (fastPhase) {
            g2d.setColor(new Color(200, 80, 0, 130));
        } else {
            g2d.setColor(new Color(80, 80, 80, 100));
        }
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(tx - 10, ty - 22, tw + 20, 30, 8, 8);
        g2d.setStroke(new BasicStroke(1f));

        // Timer text
        if (warn) {
            g2d.setColor(blink ? new Color(255, 50, 50) : Color.WHITE);
        } else if (fastPhase) {
            g2d.setColor(new Color(255, 140, 0));
        } else {
            g2d.setColor(Color.WHITE);
        }
        g2d.drawString(timeStr, tx, ty);

        // ESC hint
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.setColor(new Color(120, 120, 120));
        String esc = "ESC Pause  •  P Mute";
        int ew = g2d.getFontMetrics().stringWidth(esc);
        g2d.drawString(esc, (Constants.BOARD_WIDTH - ew) / 2, ty + 13);

        // "FAST PHASE" banner when in the last 60 s but not yet 10 s
        if (fastPhase && !warn) {
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            String banner = "FAST PHASE!";
            int bw = g2d.getFontMetrics().stringWidth(banner);
            int bx = (Constants.BOARD_WIDTH - bw - 14) / 2;
            g2d.setColor(new Color(255, 80, 0, 180));
            g2d.fillRoundRect(bx, ty + 17, bw + 14, 16, 6, 6);
            g2d.setColor(new Color(255, 220, 80));
            g2d.drawString(banner, bx + 7, ty + 29);
        }
    }

    // ────────────────────────────────────────────────────────── Status badges

    /**
     * Draws compact colored badges below the player panels for each active power-up state.
     * Only renders when at least one effect is active, keeping the board clean otherwise.
     */
    private void drawStatusBadges(Graphics g, Snake snake1, Snake snake2) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int badgeY = PANEL_Y + PANEL_H + 8;
        drawBadge(g2d, 5, badgeY, snake1, snake2);
    }

    /**
     * Draws all active status badges for both snakes, left to right.
     * Returns the x position after the last badge.
     */
    private int drawBadge(Graphics2D g2d, int startX, int y, Snake snake1, Snake snake2) {
        int x = startX;
        int gap = 5;

        if (snake1.isPowerBoostActive()) {
            x = badge(g2d, x, y, "P1 BOOST", new Color(0, 220, 220), Color.BLACK) + gap;
        }
        if (snake2.isPowerBoostActive()) {
            x = badge(g2d, x, y, "P2 BOOST", new Color(255, 100, 255), Color.BLACK) + gap;
        }
        if (snake1.isGhostModeActive()) {
            x = badge(g2d, x, y,
                    String.format("P1 GHOST %.1fs", snake1.getGhostModeRemainingTime() / 1000.0),
                    new Color(186, 85, 211), Color.WHITE) + gap;
        }
        if (snake2.isGhostModeActive()) {
            x = badge(g2d, x, y,
                    String.format("P2 GHOST %.1fs", snake2.getGhostModeRemainingTime() / 1000.0),
                    new Color(186, 85, 211), Color.WHITE) + gap;
        }
        if (snake1.isShieldActive()) {
            x = badge(g2d, x, y, "P1 SHIELD", Constants.SHIELD_FOOD_COLOR, Color.BLACK) + gap;
        }
        if (snake2.isShieldActive()) {
            x = badge(g2d, x, y, "P2 SHIELD", Constants.SHIELD_FOOD_COLOR, Color.BLACK) + gap;
        }
        if (snake1.isFrozen()) {
            x = badge(g2d, x, y,
                    String.format("P1 FROZEN %.1fs", snake1.getFreezeRemainingTime() / 1000.0),
                    Constants.FREEZE_FOOD_COLOR, Color.BLACK) + gap;
        }
        if (snake2.isFrozen()) {
            x = badge(g2d, x, y,
                    String.format("P2 FROZEN %.1fs", snake2.getFreezeRemainingTime() / 1000.0),
                    Constants.FREEZE_FOOD_COLOR, Color.BLACK) + gap;
        }
        return x;
    }

    /** Draws a single colored pill-shaped badge, returns x after the badge. */
    private int badge(Graphics2D g2d, int x, int y, String text, Color bg, Color fg) {
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        FontMetrics fm = g2d.getFontMetrics();
        int tw = fm.stringWidth(text);
        int bw = tw + 12;
        int bh = 16;

        g2d.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 200));
        g2d.fillRoundRect(x, y, bw, bh, 8, 8);
        g2d.setColor(fg);
        g2d.drawString(text, x + 6, y + bh - 3);

        return x + bw;
    }

    // ────────────────────────────────────────────────────────── Bullets

    private void drawBullets(Graphics g) {
        List<Bullet> bullets = panel.getBullets();
        if (bullets == null) return;
        // Snapshot to avoid ConcurrentModificationException during iteration
        Bullet[] snapshot = bullets.toArray(new Bullet[0]);
        for (Bullet bullet : snapshot) {
            bullet.draw(g);
        }
    }
}
