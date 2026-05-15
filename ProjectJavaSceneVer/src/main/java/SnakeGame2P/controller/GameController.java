package SnakeGame2P.controller;

import SnakeGame2P.model.entity.*;
import SnakeGame2P.utils.GameState;
import SnakeGame2P.view.GamePanel;
import SnakeGame2P.audio.AudioManager;
import SnakeGame2P.utils.Constants;
import SnakeGame2P.utils.Direction;
import javax.swing.*;
import java.awt.event.*;

/**
 * คลาสควบคุมเกมหลัก
 * จัดการลอจิกทั้งหมดของเกม รวมถึงการเคลื่อนที่ การชน และสถานะต่างๆ
 */
public class GameController implements ActionListener {

    // ========== Components ==========
    private final GamePanel panel;
    private final AudioManager audioManager;
    private final PowerBoostManager powerBoostManager;

    // ========== Model Objects ==========
    private Snake snake1;
    private Snake snake2;
    private Food food;
    private GameState currentState;
    private String winnerText = "";

    // ========== Game Loop ==========
    private Timer timer;

    /**
     * Constructor
     * panel GamePanel ที่จะควบคุม
     * audioManager ตัวจัดการเสียง
     */
    public GameController(GamePanel panel, AudioManager audioManager) {
        this.panel = panel;
        this.audioManager = audioManager;
        this.powerBoostManager = new PowerBoostManager();
        this.currentState = GameState.MENU;

        setupButtonListeners();
        setupKeyboardListener();

        audioManager.playBackgroundMusic(Constants.SOUND_MENU);
        updateView();
    }

    /**
     * ตั้งค่า ActionListener ให้กับปุ่มต่างๆ
     */
    private void setupButtonListeners() {
        panel.getStartButton().addActionListener(e -> startGame());
        panel.getRestartButton().addActionListener(e -> restartGame());
        panel.getHowToPlayButton().addActionListener(e -> showHowToPlay());
        panel.getMuteButton().addActionListener(e -> toggleMute());
    }

    /**
     * ตั้งค่า Keyboard Listener
     */
    private void setupKeyboardListener() {
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyReleased(e);
            }
        });
    }

    /**
     * จัดการเมื่อกดปุ่ม
     */
    private void handleKeyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // ESC: Pause/Resume
        if (key == KeyEvent.VK_ESCAPE) {
            if (currentState == GameState.PLAYING) {
                togglePause();
            } else if (currentState == GameState.PAUSED) {
                togglePause();
            }
            return;
        }

        // R: กลับเมนู (เฉพาะตอน Pause)
        if (key == KeyEvent.VK_R && currentState == GameState.PAUSED) {
            returnToMenu();
            return;
        }

        // ถ้าไม่ใช่สถานะเล่น หรือกำลัง Pause ไม่รับคำสั่งเคลื่อนที่
        if (currentState != GameState.PLAYING) return;
        if (snake1 == null || snake2 == null) return;

        handleMovementKeys(key);
    }

    /**
     * จัดการปุ่มสำหรับเคลื่อนที่
     */
    private void handleMovementKeys(int key) {
        switch (key) {
            // ผู้เล่น 1
            case KeyEvent.VK_W:
                snake1.setDirection(Direction.UP);
                break;
            case KeyEvent.VK_S:
                snake1.setDirection(Direction.DOWN);
                break;
            case KeyEvent.VK_A:
                snake1.setDirection(Direction.LEFT);
                break;
            case KeyEvent.VK_D:
                snake1.setDirection(Direction.RIGHT);
                break;
            case KeyEvent.VK_SHIFT:
                powerBoostManager.activatePowerBoost(snake1);
                break;

            // ผู้เล่น 2
            case KeyEvent.VK_UP:
                snake2.setDirection(Direction.UP);
                break;
            case KeyEvent.VK_DOWN:
                snake2.setDirection(Direction.DOWN);
                break;
            case KeyEvent.VK_LEFT:
                snake2.setDirection(Direction.LEFT);
                break;
            case KeyEvent.VK_RIGHT:
                snake2.setDirection(Direction.RIGHT);
                break;
            case KeyEvent.VK_M:
                powerBoostManager.activatePowerBoost(snake2);
                break;
        }
    }

    /**
     * จัดการเมื่อปล่อยปุ่ม (ยกเลิกการเสริมพลัง)
     */
    private void handleKeyReleased(KeyEvent e) {
        if (currentState != GameState.PLAYING) return;
        if (snake1 == null || snake2 == null) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_SHIFT:
                powerBoostManager.deactivatePowerBoost(snake1);
                break;
            case KeyEvent.VK_M:
                powerBoostManager.deactivatePowerBoost(snake2);
                break;
        }
    }

    /**
     * แสดงหน้าต่างวิธีเล่น
     */
    private void showHowToPlay() {
        String howToPlay = """
            วิธีเล่นเกมงู 2 ผู้เล่น
            
            ผู้เล่น 1 (งูเขียว):
            - ใช้ WASD เพื่อเคลื่อนที่
            - W: ขึ้น, A: ซ้าย, S: ลง, D: ขวา
            - กด SHIFT ค้างเพื่อเสริมพลัง (ตัวจะสั้นลงทุก 0.5 วินาที)
            
            ผู้เล่น 2 (งูส้ม):
            - ใช้ลูกศรเพื่อเคลื่อนที่
            - ↑: ขึ้น, ↓: ลง, ←: ซ้าย, →: ขวา
            - กด M ค้างเพื่อเสริมพลัง (ตัวจะสั้นลงทุก 0.5 วินาที)
            
            กติกาการชน:
            - ถ้าหัวชนกัน: คนที่กำลังเสริมพลังจะชนะ
            - ถ้าชนลำตัว: คนที่ชนลำตัวคนอื่นจะแพ้
            - ถ้าหัวชนกันกรณีอื่นๆ: ใครตัวใหญ่กว่าชนะ
            
            กติกาทั่วไป:
            - ชนขอบจอ แพ้
            - ชนตัวเอง แพ้
            
            การควบคุมเพิ่มเติม:
            - กด ESC เพื่อหยุดเกมชั่วคราว / เล่นต่อ
            - ขณะหยุดเกม กด R เพื่อกลับหน้าเมนู
            
            Enjoy! 🐍
            """;

        JOptionPane.showMessageDialog(panel, howToPlay, "วิธีเล่น", JOptionPane.INFORMATION_MESSAGE);
        panel.requestFocusInWindow();
    }

    /**
     * สลับสถานะปิดเสียง
     */
    private void toggleMute() {
        audioManager.toggleMute();
        panel.updateMuteButton(audioManager.isMuted());

        if (!audioManager.isMuted()) {
            if (currentState == GameState.MENU) {
                audioManager.playBackgroundMusic(Constants.SOUND_MENU);
            } else if (currentState == GameState.PLAYING) {
                audioManager.playBackgroundMusic(Constants.SOUND_BACKGROUND);
            }
        }
        panel.requestFocusInWindow();
    }

    /**
     * เริ่มเกมใหม่
     */
    public void startGame() {
        currentState = GameState.PLAYING;
        panel.setMenuButtonsVisible(false);
        panel.setRestartButtonVisible(false);

        audioManager.stopAllMusic();
        audioManager.playBackgroundMusic(Constants.SOUND_BACKGROUND);

        initializeGame();
        updateView();
        panel.requestFocusInWindow();
    }

    /**
     * เริ่มเกมใหม่ (หลังจากจบเกม)
     */
    public void restartGame() {
        if (timer != null) {
            timer.stop();
        }
        currentState = GameState.MENU;
        panel.setMenuButtonsVisible(true);
        panel.setRestartButtonVisible(false);

        audioManager.stopAllMusic();
        audioManager.playBackgroundMusic(Constants.SOUND_MENU);

        updateView();
        panel.requestFocusInWindow();
    }

    /**
     * กลับไปหน้าเมนู (จากหน้า Pause)
     */
    public void returnToMenu() {
        if (timer != null) {
            timer.stop();
        }
        currentState = GameState.MENU;
        panel.setMenuButtonsVisible(true);
        panel.setRestartButtonVisible(false);

        audioManager.stopAllMusic();
        audioManager.playBackgroundMusic(Constants.SOUND_MENU);

        updateView();
        panel.requestFocusInWindow();
    }

    /**
     * สลับสถานะ Pause
     */
    public void togglePause() {
        if (currentState == GameState.PLAYING) {
            currentState = GameState.PAUSED;
            if (timer != null) timer.stop();
            audioManager.stopAllMusic();
        } else if (currentState == GameState.PAUSED) {
            currentState = GameState.PLAYING;
            if (timer != null) timer.start();
            if (!audioManager.isMuted()) {
                audioManager.playBackgroundMusic(Constants.SOUND_BACKGROUND);
            }
        }
        updateView();
        panel.requestFocusInWindow();
    }

    /**
     * ตั้งค่าเริ่มต้นของเกม
     */
    private void initializeGame() {
        snake1 = new Snake(Constants.SNAKE1_START_X, Constants.SNAKE1_START_Y,
                Direction.RIGHT, Constants.SNAKE1_HEAD_COLOR,
                Constants.SNAKE1_BODY_COLOR, Constants.SNAKE1_BOOST_HEAD_COLOR);

        snake2 = new Snake(Constants.SNAKE2_START_X, Constants.SNAKE2_START_Y,
                Direction.LEFT, Constants.SNAKE2_HEAD_COLOR,
                Constants.SNAKE2_BODY_COLOR, Constants.SNAKE2_BOOST_HEAD_COLOR);

        food = new Food();
        while (food.isOccupiedBy(snake1, snake2)) {
            food.generateNewPosition();
        }

        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        timer = new Timer(Constants.NORMAL_SPEED_DELAY, this);
        timer.start();
    }

    /**
     * อัพเดทการแสดงผล
     */
    private void updateView() {
        panel.updateGameData(snake1, snake2, food, currentState, winnerText);
    }

    /**
     * เรียกโดย Timer ทุกๆ DELAY ms
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentState != GameState.PLAYING) return;

        // จัดการเสริมพลัง (ใช้ PowerBoostManager)
        powerBoostManager.handlePowerBoosts(snake1, snake2);

        // เคลื่อนที่
        moveSnakes();

        // ตรวจสอบการกินอาหาร
        checkFoodCollision();

        // ตรวจสอบการชน
        checkCollisions();

        if (currentState == GameState.PLAYING) {
            updateView();
        }
    }

    /**
     * เคลื่อนที่งูทั้งสองตัว
     */
    private void moveSnakes() {
        snake1.move();
        snake2.move();
    }

    /**
     * ตรวจสอบการกินอาหาร
     */
    private void checkFoodCollision() {
        if (food.isEatenBy(snake1)) {
            food.onConsumed(snake1); // ใช้ Polymorphism ผ่าน Interface
            audioManager.playSound(Constants.SOUND_EAT);
            generateNewFood();
        }

        if (food.isEatenBy(snake2)) {
            food.onConsumed(snake2);
            audioManager.playSound(Constants.SOUND_EAT);
            generateNewFood();
        }
    }

    /**
     * สร้างอาหารใหม่ในตำแหน่งที่ไม่ซ้อนทับงู
     */
    private void generateNewFood() {
        do {
            food.generateNewPosition();
        } while (food.isOccupiedBy(snake1, snake2));
    }

    /**
     * ตรวจสอบการชนทุกประเภท
     */
    private void checkCollisions() {
        // 1. ชนขอบจอ
        if (snake1.isOutOfBounds()) {
            winnerText = "ผู้เล่น 2 ชนะ! (งูเขียวตกขอบ)";
            gameOver();
            return;
        }

        if (snake2.isOutOfBounds()) {
            winnerText = "ผู้เล่น 1 ชนะ! (งูส้มตกขอบ)";
            gameOver();
            return;
        }

        // 2. ชนตัวเอง
        if (snake1.isCollidingWithSelf()) {
            winnerText = "ผู้เล่น 2 ชนะ! (งูเขียวชนตัวเอง)";
            gameOver();
            return;
        }

        if (snake2.isCollidingWithSelf()) {
            winnerText = "ผู้เล่น 1 ชนะ! (งูส้มชนตัวเอง)";
            gameOver();
            return;
        }

        // 3. หัวชนหัว
        if (snake1.getHead().equals(snake2.getHead())) {
            determineHeadOnHeadWinner();
            gameOver();
            return;
        }

        // 4. หัวชนลำตัว
        if (snake2.isBodyAt(snake1.getHead())) {
            winnerText = "ผู้เล่น 2 ชนะ! (งูเขียวชนลำตัวงูส้ม)";
            gameOver();
            return;
        }

        if (snake1.isBodyAt(snake2.getHead())) {
            winnerText = "ผู้เล่น 1 ชนะ! (งูส้มชนลำตัวงูเขียว)";
            gameOver();
            return;
        }
    }

    /**
     * ตัดสินผู้ชนะเมื่อหัวชนหัว
     */
    private void determineHeadOnHeadWinner() {
        if (snake1.isPowerBoostActive() && !snake2.isPowerBoostActive()) {
            winnerText = "ผู้เล่น 1 ชนะ! (เสริมพลังชนะ)";
        } else if (!snake1.isPowerBoostActive() && snake2.isPowerBoostActive()) {
            winnerText = "ผู้เล่น 2 ชนะ! (เสริมพลังชนะ)";
        } else {
            if (snake1.getLength() > snake2.getLength()) {
                winnerText = "ผู้เล่น 1 ชนะ! (ตัวใหญ่กว่า)";
            } else if (snake2.getLength() > snake1.getLength()) {
                winnerText = "ผู้เล่น 2 ชนะ! (ตัวใหญ่กว่า)";
            } else {
                winnerText = "เสมอ! (ตัวเท่ากัน)";
            }
        }
    }

    /**
     * จบเกม
     */
    private void gameOver() {
        currentState = GameState.GAME_OVER;
        if (timer != null) {
            timer.stop();
        }

        audioManager.stopAllMusic();
        audioManager.playSound(Constants.SOUND_GAME_OVER);

        panel.setRestartButtonVisible(true);
        updateView();
        panel.requestFocusInWindow();
    }
}