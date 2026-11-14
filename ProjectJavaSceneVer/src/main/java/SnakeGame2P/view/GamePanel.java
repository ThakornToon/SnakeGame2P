package SnakeGame2P.view;

import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.model.entity.Food;
import SnakeGame2P.model.entity.Bullet;
import SnakeGame2P.utils.GameState;
import SnakeGame2P.audio.AudioManager;
import SnakeGame2P.utils.Constants;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * JPanel หลักของเกม รับผิดชอบในการแสดงผลและการรับ event จากผู้ใช้
 */
public class GamePanel extends JPanel {
    // ========== ข้อมูลเกม ==========
    private Snake snake1;
    private Snake snake2;
    private Food food;
    private GameState currentState;
    private String winnerText;
    private List<Bullet> bullets = new ArrayList<>();
    private int remainingTimeMs = 0;

    // ========== Components ==========
    private final GameRenderer renderer;
    private final AudioManager audioManager;

    // ========== ปุ่มต่างๆ ==========
    private JButton startButton;
    private JButton restartButton;
    private JButton howToPlayButton;

    // ========== Toast notification ==========
    /** ข้อความ toast ที่แสดงชั่วคราว (เช่น สถานะ mute) */
    private String toastMessage = null;
    /** เวลาที่ toast จะหายไป (System.currentTimeMillis) */
    private long toastExpireTime = 0;
    /** ระยะเวลาแสดง toast (ms) */
    private static final int TOAST_DURATION_MS = 1500;
    /** Timer สำหรับ repaint เพื่อให้ toast หายไปหลังหมดเวลา */
    private Timer toastTimer = null;

    /**
     * Constructor
     */
    public GamePanel(AudioManager audioManager) {
        this.audioManager = audioManager;
        this.renderer = new GameRenderer(this);
        this.currentState = GameState.MENU;

        setPreferredSize(new Dimension(Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        setLayout(null);

        createButtons();
    }

    /**
     * สร้างปุ่มทั้งหมด
     */
    private void createButtons() {
        startButton     = createButton("เริ่มเกม",   400, 300, 200, 60, 30);
        howToPlayButton = createButton("วิธีเล่น",   400, 380, 200, 40, 20);
        restartButton   = createButton("เริ่มเกมใหม่", 400, 400, 200, 50, 20);
        restartButton.setVisible(false);
    }

    /**
     * สร้างปุ่มแต่ละปุ่ม
     */
    private JButton createButton(String text, int x, int y, int width, int height, int fontSize) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, fontSize));
        button.setFocusable(false);
        button.setBounds(x, y, width, height);
        add(button);
        return button;
    }

    /**
     * อัพเดทข้อมูลเกมและบังคับให้วาดใหม่
     */
    public void updateGameData(Snake snake1, Snake snake2, Food food, GameState state,
                               String winner, List<Bullet> bullets, int remainingTimeMs) {
        this.snake1 = snake1;
        this.snake2 = snake2;
        this.food = food;
        this.currentState = state;
        this.winnerText = winner;
        this.bullets = new ArrayList<>(bullets);
        this.remainingTimeMs = remainingTimeMs;
        repaint();
    }

    /**
     * เรียกโดย Swing เมื่อต้องการวาด Component
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.render(g, currentState, snake1, snake2, food, winnerText);
        drawToastIfActive(g);
    }

    /**
     * วาด toast notification ชั่วคราวที่มุมขวาล่าง
     */
    private void drawToastIfActive(Graphics g) {
        if (toastMessage == null || System.currentTimeMillis() > toastExpireTime) {
            toastMessage = null;
            return;
        }
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g2d.getFontMetrics();
        int tw = fm.stringWidth(toastMessage);
        int th = fm.getHeight();
        int margin = 14;
        int pw = tw + 24;
        int ph = th + 12;
        // มุมขวาล่าง ห่างขอบ 16px
        int px = Constants.BOARD_WIDTH  - pw - 16;
        int py = Constants.BOARD_HEIGHT - ph - 16;

        // Background pill
        g2d.setColor(new Color(20, 20, 20, 215));
        g2d.fillRoundRect(px, py, pw, ph, 14, 14);
        g2d.setColor(new Color(160, 160, 160, 160));
        g2d.setStroke(new java.awt.BasicStroke(1.5f));
        g2d.drawRoundRect(px, py, pw, ph, 14, 14);

        // Text
        g2d.setColor(Color.WHITE);
        g2d.drawString(toastMessage, px + margin, py + th);
    }

    /**
     * แสดง toast notification ชั่วคราว พร้อม Timer สำหรับ repaint เมื่อหมดเวลา
     */
    public void showMuteToast(boolean muted) {
        toastMessage = muted ? "🔇 ปิดเสียงแล้ว" : "🔊 เปิดเสียงแล้ว";
        toastExpireTime = System.currentTimeMillis() + TOAST_DURATION_MS;

        // หยุด timer เก่าก่อนเสมอ เพื่อป้องกัน timer ซ้อนกัน
        if (toastTimer != null && toastTimer.isRunning()) {
            toastTimer.stop();
        }
        // สร้าง timer ใหม่ให้ repaint หลังหมดเวลา (fires once)
        toastTimer = new Timer(TOAST_DURATION_MS, e -> repaint());
        toastTimer.setRepeats(false);
        toastTimer.start();

        repaint();
    }

    // ========== Getters สำหรับปุ่ม ==========
    public JButton getStartButton()     { return startButton; }
    public JButton getRestartButton()   { return restartButton; }
    public JButton getHowToPlayButton() { return howToPlayButton; }

    // ========== Setters สำหรับการแสดง/ซ่อนปุ่ม ==========
    public void setRestartButtonVisible(boolean visible) {
        restartButton.setVisible(visible);
    }

    public void setMenuButtonsVisible(boolean visible) {
        startButton.setVisible(visible);
        howToPlayButton.setVisible(visible);
    }

    // ========== Getters สำหรับกระสุนและเวลา ==========
    public List<Bullet> getBullets() {
        return bullets;
    }

    public int getRemainingTimeMs() {
        return remainingTimeMs;
    }
}
