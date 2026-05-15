package SnakeGame2P.view;

import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.model.entity.Food;
import SnakeGame2P.utils.GameState;
import SnakeGame2P.audio.AudioManager;
import SnakeGame2P.utils.Constants;
import javax.swing.*;
import java.awt.*;

/**
 * JPanel หลักของเกม รับผิดชอบในการแสดงผลและการรับ event จากผู้ใช้
 * เพิ่มคุณสมบัติให้ JPanel
 */
public class GamePanel extends JPanel {
    // ========== ข้อมูลเกม ==========
    private Snake snake1;
    private Snake snake2;
    private Food food;
    private GameState currentState;
    private String winnerText;

    // ========== Components ==========
    private final GameRenderer renderer;
    private final AudioManager audioManager;

    // ========== ปุ่มต่างๆ ==========
    private JButton startButton;
    private JButton restartButton;
    private JButton howToPlayButton;
    private JButton muteButton;

    /**
     * Constructor
     * audioManager ตัวจัดการเสียง
     */
    public GamePanel(AudioManager audioManager) {
        this.audioManager = audioManager;
        this.renderer = new GameRenderer();
        this.currentState = GameState.MENU;

        // ตั้งค่าพื้นฐานของ Panel (extend JPanel)
        setPreferredSize(new Dimension(Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true); // ให้รับ keyboard input ได้
        setLayout(null); // ใช้ Absolute Layout สำหรับวางปุ่ม

        // สร้างปุ่มต่างๆ
        createButtons();
    }

    /**
     * สร้างปุ่มทั้งหมด
     */
    private void createButtons() {
        startButton = createButton("เริ่มเกม", 400, 300, 200, 60, 30);
        howToPlayButton = createButton("วิธีเล่น", 400, 380, 200, 40, 20);
        muteButton = createButton("🔊 ปิดเสียง", 850, 20, 120, 30, 16);
        restartButton = createButton("เริ่มเกมใหม่", 400, 400, 200, 50, 20);
        restartButton.setVisible(false); // ซ่อนปุ่มเริ่มใหม่ไว้ก่อน
    }

    /**
     * สร้างปุ่มแต่ละปุ่ม
     * return JButton ที่สร้างแล้ว
     */
    private JButton createButton(String text, int x, int y, int width, int height, int fontSize) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, fontSize));
        button.setFocusable(false); // ป้องกันไม่ให้ปุ่มแย่ง focus จาก panel
        button.setBounds(x, y, width, height);
        add(button);
        return button;
    }

    /**
     * อัพเดทข้อมูลเกมและบังคับให้วาดใหม่
     */
    public void updateGameData(Snake snake1, Snake snake2, Food food, GameState state, String winner) {
        this.snake1 = snake1;
        this.snake2 = snake2;
        this.food = food;
        this.currentState = state;
        this.winnerText = winner;
        repaint(); // ขอให้ Swing วาดใหม่ด้วยข้อมูลล่าสุด
    }

    /**
     * เรียกโดย Swing เมื่อต้องการวาด Component
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // ใช้ Polymorphism ผ่าน GameRenderer เพื่อวาดตามสถานะ
        renderer.render(g, currentState, snake1, snake2, food, winnerText);
    }

    // ========== Getters สำหรับปุ่ม ==========
    public JButton getStartButton() { return startButton; }
    public JButton getRestartButton() { return restartButton; }
    public JButton getHowToPlayButton() { return howToPlayButton; }
    public JButton getMuteButton() { return muteButton; }

    // ========== Setters สำหรับปุ่ม ในการแสดง/ซ่อน ==========
    public void setRestartButtonVisible(boolean visible) {
        restartButton.setVisible(visible);
    }

    public void setMenuButtonsVisible(boolean visible) {
        startButton.setVisible(visible);
        howToPlayButton.setVisible(visible);
    }

    public void updateMuteButton(boolean muted) {
        muteButton.setText(muted ? "🔇 เปิดเสียง" : "🔊 ปิดเสียง");
    }
}