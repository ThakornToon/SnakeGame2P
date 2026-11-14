package SnakeGame2P.view.renderer;

import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.model.entity.Food;
import SnakeGame2P.utils.Constants;
import SnakeGame2P.view.GamePanel;
import java.awt.*;

/**
 * Renders the main menu screen.
 *
 * The two Swing buttons (Start @ y300-360, How-to-play @ y380-420) sit in the
 * vertical centre, so the drawn content is arranged to flow cleanly AROUND them:
 *
 *   y   0-150 : title + subtitle (centred)
 *   y 160-270 : two snake previews (P1 left / P2 right) with controls
 *   y 280-435 : button "card" backdrop (buttons drawn on top by Swing)
 *   y 448-588 : food showcase card (4 animated icons + labels)
 *   y 600-665 : rules summary
 *   y     688 : music credit
 */
public class MenuRenderer implements GameRendererStrategy {
    private final GamePanel panel;

    public MenuRenderer(GamePanel panel) {
        this.panel = panel;
    }

    @Override
    public void render(Graphics g, Snake snake1, Snake snake2, Food food, String additionalData) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawBackground(g2d);
        drawTitle(g2d);
        drawSnakePreviews(g2d);
        drawButtonCard(g2d);
        drawFoodShowcase(g2d);
        drawRulesSummary(g2d);
        drawMusicCredit(g2d);
    }

    // ────────────────────────────────────────────────── helpers

    /** Draws text horizontally centred on {@code cx}. */
    private void drawCentered(Graphics2D g2d, String text, int cx, int y) {
        int w = g2d.getFontMetrics().stringWidth(text);
        g2d.drawString(text, cx - w / 2, y);
    }

    // ────────────────────────────────────────────────── background

    private void drawBackground(Graphics2D g2d) {
        // Vertical gradient: dark green → near-black
        GradientPaint grad = new GradientPaint(
                0, 0, new Color(6, 26, 8),
                0, Constants.BOARD_HEIGHT, new Color(2, 6, 2));
        g2d.setPaint(grad);
        g2d.fillRect(0, 0, Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT);

        // Faint dotted grid for texture
        g2d.setColor(new Color(18, 38, 18));
        for (int x = 0; x < Constants.BOARD_WIDTH; x += Constants.UNIT_SIZE) {
            for (int y = 0; y < Constants.BOARD_HEIGHT; y += Constants.UNIT_SIZE) {
                g2d.fillRect(x, y, 1, 1);
            }
        }

        // Outer glowing border frame
        g2d.setColor(new Color(30, 90, 30, 160));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRoundRect(12, 12, Constants.BOARD_WIDTH - 24, Constants.BOARD_HEIGHT - 24, 18, 18);
        g2d.setStroke(new BasicStroke(1f));
    }

    // ────────────────────────────────────────────────── title

    private void drawTitle(Graphics2D g2d) {
        int cx = Constants.BOARD_WIDTH / 2;
        double glow = (Math.sin(System.currentTimeMillis() / 700.0) + 1.0) / 2.0; // 0→1

        g2d.setFont(new Font("Arial", Font.BOLD, 58));
        int ty = 72;

        // Glow / shadow layer
        g2d.setColor(new Color(0, 170, 0, (int)(50 + glow * 90)));
        drawCentered(g2d, "เกมงู 2 ผู้เล่น", cx + 3, ty + 3);
        // Main title
        g2d.setColor(new Color(45, 235, 45));
        drawCentered(g2d, "เกมงู 2 ผู้เล่น", cx, ty);

        // English subtitle
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        g2d.setColor(new Color(90, 170, 90));
        drawCentered(g2d, "2 - P L A Y E R   S N A K E", cx, ty + 52);

        // Divider
        g2d.setColor(new Color(35, 95, 35));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawLine(cx - 280, ty + 70, cx + 280, ty + 70);
        g2d.setStroke(new BasicStroke(1f));
    }

    // ────────────────────────────────────────────────── snake previews

    private void drawSnakePreviews(Graphics2D g2d) {
        int snakeY = 185;
        int step = Constants.UNIT_SIZE + 2;
        int previewW = 5 * step - 2;

        // P1 — left
        int p1x = 130;
        int p1cx = p1x + previewW / 2;
        drawSnakeSegments(g2d, p1x, snakeY, Constants.SNAKE1_HEAD_COLOR,
                Constants.SNAKE1_BODY_COLOR, true);
        drawPreviewLabels(g2d, p1cx, snakeY, Constants.SNAKE1_HEAD_COLOR,
                "PLAYER 1", "Move: W A S D", "Boost: SHIFT", "Shoot: SPACE");

        // P2 — right
        int p2x = Constants.BOARD_WIDTH - 130 - previewW;
        int p2cx = p2x + previewW / 2;
        drawSnakeSegments(g2d, p2x, snakeY, Constants.SNAKE2_HEAD_COLOR,
                Constants.SNAKE2_BODY_COLOR, false);
        drawPreviewLabels(g2d, p2cx, snakeY, Constants.SNAKE2_HEAD_COLOR,
                "PLAYER 2", "Move: ↑ ↓ ← →", "Boost: M", "Shoot: /");
    }

    private void drawPreviewLabels(Graphics2D g2d, int cx, int snakeY, Color headColor,
                                    String title, String move, String boost, String shoot) {
        g2d.setColor(headColor);
        g2d.setFont(new Font("Arial", Font.BOLD, 15));
        drawCentered(g2d, title, cx, snakeY - 12);

        g2d.setColor(new Color(190, 190, 190));
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        drawCentered(g2d, move,  cx, snakeY + 42);
        drawCentered(g2d, boost, cx, snakeY + 59);
        drawCentered(g2d, shoot, cx, snakeY + 76);
    }

    /** Draws a 5-segment rounded snake preview with an eye on the head. */
    private void drawSnakeSegments(Graphics2D g2d, int x, int y,
                                    Color headColor, Color bodyColor, boolean right) {
        int S = Constants.UNIT_SIZE;
        int step = S + 2;
        int arc = 8, pad = 2;
        for (int i = 0; i < 5; i++) {
            int sx = right ? x + i * step : x + (4 - i) * step;
            g2d.setColor(i == 0 ? headColor : bodyColor);
            g2d.fillRoundRect(sx + pad, y + pad, S - pad * 2, S - pad * 2, arc, arc);
        }
        // Eye on the head segment (facing outward)
        int headX = right ? x : x + 4 * step;
        int eyeX = right ? headX + S - 9 : headX + 3;
        g2d.setColor(Color.WHITE);
        g2d.fillOval(eyeX, y + 6, 6, 6);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(eyeX + (right ? 1 : 1), y + 7, 4, 4);
    }

    // ────────────────────────────────────────────────── button backdrop

    /** Subtle rounded card behind the centre Swing buttons to frame them. */
    private void drawButtonCard(Graphics2D g2d) {
        int cardX = (Constants.BOARD_WIDTH - 290) / 2;
        g2d.setColor(new Color(0, 0, 0, 110));
        g2d.fillRoundRect(cardX, 280, 290, 158, 16, 16);
        g2d.setColor(new Color(40, 110, 40, 150));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(cardX, 280, 290, 158, 16, 16);
        g2d.setStroke(new BasicStroke(1f));
    }

    // ────────────────────────────────────────────────── food showcase

    private void drawFoodShowcase(Graphics2D g2d) {
        int cardX = 80, cardY = 448, cardW = Constants.BOARD_WIDTH - 160, cardH = 142;
        int cx = Constants.BOARD_WIDTH / 2;

        // Card backdrop
        g2d.setColor(new Color(0, 0, 0, 90));
        g2d.fillRoundRect(cardX, cardY, cardW, cardH, 14, 14);
        g2d.setColor(new Color(40, 90, 40, 120));
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawRoundRect(cardX, cardY, cardW, cardH, 14, 14);

        // Header
        g2d.setColor(new Color(150, 200, 150));
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        drawCentered(g2d, "ไอเทมอาหาร  /  FOOD ITEMS", cx, cardY + 24);

        // 4 icons evenly distributed across the card
        int[] iconCx = {cardX + cardW / 8, cardX + cardW * 3 / 8,
                        cardX + cardW * 5 / 8, cardX + cardW * 7 / 8};
        Color[] colors  = {Constants.FOOD_COLOR, Constants.GHOST_FOOD_COLOR,
                           Constants.SHIELD_FOOD_COLOR, Constants.FREEZE_FOOD_COLOR};
        String[] names  = {"Normal", "Ghost", "Shield", "Freeze"};
        String[] th     = {"ธรรมดา", "ล่องหน", "โล่", "แช่แข็ง"};
        String[] effect = {"+1 point", "Phase 5s", "Block hit", "Freeze 3s"};

        double pulse = (Math.sin(System.currentTimeMillis() / 400.0) + 1.0) / 2.0;
        int S = Constants.UNIT_SIZE + 8;
        int iconY = cardY + 40;

        for (int i = 0; i < 4; i++) {
            int shrink = (int)(pulse * 3);
            int size = S - shrink * 2;
            int ox = iconCx[i] - size / 2;
            int oy = iconY + shrink;

            // Glow
            g2d.setColor(new Color(colors[i].getRed(), colors[i].getGreen(),
                    colors[i].getBlue(), 55));
            g2d.fillOval(ox - 4, oy - 4, size + 8, size + 8);
            // Body
            g2d.setColor(colors[i]);
            g2d.fillOval(ox, oy, size, size);
            // Shine
            g2d.setColor(new Color(255, 255, 255, 150));
            g2d.fillOval(ox + size / 5, oy + size / 6, size / 4, size / 5);

            // Labels (centred under the icon)
            int labelBase = iconY + S + 16;
            g2d.setFont(new Font("Arial", Font.BOLD, 13));
            g2d.setColor(colors[i]);
            drawCentered(g2d, names[i], iconCx[i], labelBase);

            g2d.setFont(new Font("Arial", Font.PLAIN, 11));
            g2d.setColor(new Color(170, 170, 170));
            drawCentered(g2d, th[i], iconCx[i], labelBase + 15);

            g2d.setColor(new Color(225, 225, 225));
            drawCentered(g2d, effect[i], iconCx[i], labelBase + 30);
        }
    }

    // ────────────────────────────────────────────────── rules summary

    private void drawRulesSummary(Graphics2D g2d) {
        int cx = Constants.BOARD_WIDTH / 2;
        int y = 612;

        g2d.setFont(new Font("Arial", Font.PLAIN, 13));
        g2d.setColor(new Color(185, 185, 185));

        String[] lines = {
            "เวลา 180 วิ — หมดเวลา: คะแนนมากกว่าชนะ → ตัวยาวกว่าชนะ → เสมอ  •  Match: 180 s",
            "ยิงใช้ความยาว 3 (ยาว ≥ 6)  •  โล่กันชน 1 ครั้ง  •  ล่องหนทะลุคู่ต่อสู้ 5 วิ",
            "ESC — หยุด/เล่นต่อ   •   R — กลับเมนู (ขณะหยุด)   •   P — ปิด/เปิดเสียง",
        };
        for (int i = 0; i < lines.length; i++) {
            drawCentered(g2d, lines[i], cx, y + i * 19);
        }
    }

    // ────────────────────────────────────────────────── music credit

    private void drawMusicCredit(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.ITALIC, 11));
        g2d.setColor(new Color(75, 105, 75));
        drawCentered(g2d, "BGM: Start-Menu  •  Spider-Dance  •  Home (Music Box)",
                Constants.BOARD_WIDTH / 2, Constants.BOARD_HEIGHT - 22);
    }
}
