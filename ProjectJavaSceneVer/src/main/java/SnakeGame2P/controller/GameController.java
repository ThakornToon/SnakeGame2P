package SnakeGame2P.controller;

import SnakeGame2P.model.entity.*;
import SnakeGame2P.utils.GameState;
import SnakeGame2P.view.GamePanel;
import SnakeGame2P.audio.AudioManager;
import SnakeGame2P.utils.Constants;
import SnakeGame2P.utils.Direction;
import javax.swing.*;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Main game controller — owns all game logic: movement, collisions, and state transitions.
 *
 * คลาสควบคุมเกมหลัก จัดการลอจิกทั้งหมดของเกม รวมถึงการเคลื่อนที่ การชน และสถานะต่างๆ
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

    // ========== Timer & Bullets ==========
    /** Remaining match time in milliseconds. Counts down each game tick. */
    private int remainingTimeMs = 0;
    /** All bullets currently on the board. */
    private final List<Bullet> bullets = new ArrayList<>();

    // ========== Game Loop ==========
    private Timer timer;
    /** Tick counter used to control normal snake speed (move every 2 ticks). */
    private int tickCount = 0;

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
            if (currentState == GameState.PLAYING || currentState == GameState.PAUSED) {
                togglePause();
            }
            return;
        }

        // R: กลับเมนู (เฉพาะตอน Pause)
        if (key == KeyEvent.VK_R && currentState == GameState.PAUSED) {
            returnToMenu();
            return;
        }

        // P: สลับปิด/เปิดเสียง (ใช้ได้ทุกสถานะยกเว้นตอนกำลังเล่น)
        if (key == KeyEvent.VK_P && currentState != GameState.PLAYING) {
            toggleMute();
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

            case KeyEvent.VK_SPACE:
                shootBullet(snake1);
                break;

            case KeyEvent.VK_SLASH:
                shootBullet(snake2);
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
     * แสดงหน้าต่างวิธีเล่นแบบละเอียดครบถ้วน
     */
    private void showHowToPlay() {
        String howToPlay = """
            🎮 วิธีเล่นเกมงู 2 ผู้เล่น  |  How to Play – 2-Player Snake
            ══════════════════════════════════════════════════════════════════

            🐍 การควบคุมผู้เล่น  |  Player Controls:
            ──────────────────────────────────────────────────────────────────
            ผู้เล่น 1 (งูเขียว  |  Player 1 - Green Snake):
              • W / A / S / D  : เคลื่อนที่ ขึ้น / ซ้าย / ลง / ขวา
              • กด SHIFT ค้าง   : เร่งความเร็ว 2 เท่า (ยาว > 3, หด 1 หน่วยทุก 0.5 วิ)
              • SPACEBAR       : ยิงกระสุน (ยาว ≥ 6, ลด 3 หน่วย, คูลดาวน์ 0.5 วิ)

            ผู้เล่น 2 (งูส้ม  |  Player 2 - Orange Snake):
              • ลูกศร ↑ / ← / ↓ / → : เคลื่อนที่ ขึ้น / ซ้าย / ลง / ขวา
              • กด M ค้าง            : เร่งความเร็ว 2 เท่า (ยาว > 3, หด 1 หน่วยทุก 0.5 วิ)
              • / (Slash)            : ยิงกระสุน (ยาว ≥ 6, ลด 3 หน่วย, คูลดาวน์ 0.5 วิ)

            🍎 ไอเทมอาหารและบัฟพิเศษ  |  Food & Power-ups:
            ──────────────────────────────────────────────────────────────────
              • 🔴 สีแดง (Normal Food) : ความยาว +1, คะแนน +1
              • 🟣 สีม่วง (Ghost Food)  : ความยาว +1, ล่องหนทะลุตัวคู่แข่งได้ 5 วินาที
              • 🟡 สีทอง (Shield Food) : ความยาว +1, ได้รับโล่ป้องกันการชน 1 ครั้ง
              • 🔵 สีฟ้า (Freeze Food) : ความยาว +1, แช่แข็งคู่ต่อสู้ 3 วินาที (ขยับไม่ได้)
              * หมายเหตุ: อาหารมีอายุ 8 วินาที หากไม่มีใครงับกินจะหายไปและสุ่มเกิดใหม่

            🔥 ระบบคอมโบ & คะแนนโบนัส  |  Combo Scoring:
            ──────────────────────────────────────────────────────────────────
              • หากกินอาหารต่อเนื่องภายใน 3 วินาที จะเพิ่มตัวคูณคอมโบ (สูงสุด ×5 COMBO!)
              • ได้รับคะแนนโบนัสพิเศษเพิ่มตามจำนวนคอมโบ (+1 ถึง +4 คะแนนพิเศษ)

            ⚡ โหมดเร่งความเร็วด่วน (Fast Phase):
            ──────────────────────────────────────────────────────────────────
              • ในช่วง 60 วินาทีสุดท้ายของเกม จะเข้าสู่ "FAST PHASE!"
              • ความเร็วรอบเดินของเกมจะเพิ่มขึ้นเป็น 2 เท่าทันที
              • ตัวจับเวลาจะเปลี่ยนเป็นสีส้ม และกระพริบสีแดงเตือนในช่วง 10 วินาทีสุดท้าย

            💥 ระบบกระสุน  |  Bullets:
            ──────────────────────────────────────────────────────────────────
              • ยิงพุ่งออกจากหัวงูด้วยความเร็วสูงไปตามทิศทางที่งูกำลังเคลื่อนที่
              • ยิงโดนคู่ต่อสู้ : ลดคะแนนคู่ต่อสู้ 2 แต้ม และทำให้งูคู่ต่อสู้หดสั้นลง 1 หน่วย
              • ยิงโดนอาหาร     : ทำลายอาหารและสุ่มตำแหน่งอาหารเกิดใหม่ทันที
              • กระสุนจะไม่ชนงูฝ่ายเดียวกันที่เป็นคนยิง

            🛡️ กฎการชนและปฏิกิริยาของโล่  |  Collisions & Shield:
            ──────────────────────────────────────────────────────────────────
              • หัวชนหัว (Head-on)   : ตัวยาวกว่าชนะ / ถ้ายาวเท่ากันถือว่าเสมอ
                                        (หากฝ่ายใดมีโล่ ฝ่ายมีโล่ชนะ / หากมีโล่ทั้งคู่ โล่จะแตกทั้งสองฝ่าย)
              • หัวชนลำตัว (Body hit): ฝ่ายที่หัวพุ่งชนลำตัวฝ่ายตรงข้ามจะแพ้ทันที
              • ชนขอบจอ (Wall hit)   : แพ้ทันที (หากมีโล่: โล่จะแตกและวาร์ปหัวงูไปขอบฝั่งตรงข้าม)
              • ชนตัวเอง (Self hit)   : แพ้ทันที (หากมีโล่: โล่จะแตกและได้รับสถานะล่องหน 1.5 วินาที)
              • โล่แตกจากการชนลำตัว   : โล่จะแตกและงูจะได้รับสถานะล่องหน 1.5 วินาทีเพื่อเลื้อยหลบออก

            ⏱️ กติกาการตัดสินผู้ชนะ  |  Win Conditions:
            ──────────────────────────────────────────────────────────────────
              1. ฝ่ายตรงข้ามชนตาย (และไม่มีโล่ป้องกัน)
              2. หากหมดเวลาแข่งขัน (180 วินาที):
                 - ตัดสินจากคะแนนสะสม (ใครแต้มมากกว่าชนะ)
                 - หากแต้มเท่ากัน ตัดสินจากความยาวของลำตัว (ใครตัวยาวกว่าชนะ)
                 - หากเท่ากันทั้งแต้มและความยาว จะถือว่า "เสมอ"

            ⌨️ ปุ่มคำสั่งระบบ  |  System Controls:
            ──────────────────────────────────────────────────────────────────
              • ESC : หยุดเกมชั่วคราว (Pause) / เล่นเกมต่อ (Resume)
              • R   : กลับสู่หน้าเมนูหลัก (ใช้ได้ขณะหน้าจอ Pause)
              • P   : สลับเปิด/ปิดเสียงเพลงและเอฟเฟกต์ (Toggle Mute ในหน้า Menu หรือ Pause)

            ขอให้สนุกกับการประลองฝีมือ! 🐍✨
            """;

        JOptionPane.showMessageDialog(panel, howToPlay, "วิธีเล่นเกมงู 2 ผู้เล่น (How to Play)", JOptionPane.INFORMATION_MESSAGE);
        panel.requestFocusInWindow();
    }

    /**
     * สลับสถานะปิดเสียง
     */
    private void toggleMute() {
        audioManager.toggleMute();
        panel.showMuteToast(audioManager.isMuted());

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

        generateNewFood();

        tickCount = 0; // รีเซ็ตตัวนับรอบการติ๊ก
        bullets.clear(); // รีเซ็ตรายการกระสุน
        remainingTimeMs = Constants.GAME_DURATION_SEC * 1000; // รีเซ็ตตัวจับเวลา

        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        timer = new Timer(Constants.GAME_TICK_DELAY, this);
        timer.start();
    }

    /**
     * อัพเดทการแสดงผล
     */
    private void updateView() {
        panel.updateGameData(snake1, snake2, food, currentState, winnerText, bullets, remainingTimeMs);
    }

    /**
     * เรียกโดย Timer ทุกๆ DELAY ms
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentState != GameState.PLAYING) return;

        tickCount++;

        // Decrement by the delay actually used for this tick (may differ during fast phase)
        int prevDelay = timer.getDelay();
        remainingTimeMs -= prevDelay;
        if (remainingTimeMs <= 0) {
            remainingTimeMs = 0;
            handleTimeOut();
            return;
        }

        // Speed phase: halve tick delay in the last FAST_PHASE_THRESHOLD_SEC seconds
        int targetDelay = remainingTimeMs <= Constants.FAST_PHASE_THRESHOLD_SEC * 1000
                ? Constants.GAME_TICK_DELAY / 2 : Constants.GAME_TICK_DELAY;
        if (prevDelay != targetDelay) timer.setDelay(targetDelay);

        powerBoostManager.handlePowerBoosts(snake1, snake2);
        moveSnakes();

        for (int step = 0; step < Constants.BULLET_SPEED_MULTIPLIER; step++) {
            updateAndCheckBullets();
        }

        // Respawn expired food before collision check so a snake can't eat a ghost item
        if (food.isExpired()) {
            generateNewFood();
        }
        checkFoodCollision();
        checkCollisions();

        if (currentState == GameState.PLAYING) {
            updateView();
        }
    }

    /**
     * Advances both snakes by one step, respecting freeze and speed-boost states.
     *
     * Normal speed  = move every 2 ticks (tickCount % 2 == 0).
     * Speed-boosted = move every tick (double speed).
     * Frozen        = never move until the freeze timer expires.
     */
    private void moveSnakes() {
        boolean snake1ShouldMove = !snake1.isFrozen() && (snake1.isPowerBoostActive() || tickCount % 2 == 0);
        if (snake1ShouldMove) {
            snake1.move();
        }

        boolean snake2ShouldMove = !snake2.isFrozen() && (snake2.isPowerBoostActive() || tickCount % 2 == 0);
        if (snake2ShouldMove) {
            snake2.move();
        }
    }

    /**
     * Checks whether either snake ate the food this tick.
     * Awards combo bonus points (+1 per consecutive eat within COMBO_WINDOW_MS).
     */
    private void checkFoodCollision() {
        if (food.isEatenBy(snake1)) {
            food.onConsumed(snake1, snake2);
            int bonus = Math.max(0, snake1.getComboCount() - 1);
            if (bonus > 0) snake1.addBonusScore(bonus);
            audioManager.playSound(Constants.SOUND_EAT);
            generateNewFood();
        }

        if (food.isEatenBy(snake2)) {
            food.onConsumed(snake2, snake1);
            int bonus = Math.max(0, snake2.getComboCount() - 1);
            if (bonus > 0) snake2.addBonusScore(bonus);
            audioManager.playSound(Constants.SOUND_EAT);
            generateNewFood();
        }
    }

    /**
     * สร้างอาหารใหม่แบบสุ่มประเภทในตำแหน่งที่ไม่ซ้อนทับงู
     */
    private void generateNewFood() {
        do {
            food = FoodFactory.createRandomFood();
        } while (food.isOccupiedBy(snake1, snake2));
    }

    /**
     * วาปหัวงูไปยังขอบฝั่งตรงข้ามเมื่อชนขอบจอ (ใช้กับเกราะ Shield)
     */
    private void warpSnakeToOppositeEdge(Snake snake) {
        int newX = snake.getHead().x;
        int newY = snake.getHead().y;
        if (newX < 0) newX = Constants.BOARD_WIDTH - Constants.UNIT_SIZE;
        else if (newX >= Constants.BOARD_WIDTH) newX = 0;
        if (newY < 0) newY = Constants.BOARD_HEIGHT - Constants.UNIT_SIZE;
        else if (newY >= Constants.BOARD_HEIGHT) newY = 0;
        snake.teleportHead(newX, newY);
    }

    /**
     * ตรวจสอบการชนทุกประเภท
     */
    private void checkCollisions() {
        // 1. ชนขอบจอ
        if (snake1.isOutOfBounds()) {
            if (snake1.isShieldActive()) {
                snake1.deactivateShield();
                warpSnakeToOppositeEdge(snake1);
                audioManager.playSound(Constants.SOUND_EAT);
            } else {
                winnerText = "ผู้เล่น 2 ชนะ! (งูเขียวตกขอบ)";
                gameOver();
                return;
            }
        }

        if (snake2.isOutOfBounds()) {
            if (snake2.isShieldActive()) {
                snake2.deactivateShield();
                warpSnakeToOppositeEdge(snake2);
                audioManager.playSound(Constants.SOUND_EAT);
            } else {
                winnerText = "ผู้เล่น 1 ชนะ! (งูส้มตกขอบ)";
                gameOver();
                return;
            }
        }

        // 2. ชนตัวเอง
        if (snake1.isCollidingWithSelf()) {
            if (snake1.isShieldActive()) {
                snake1.deactivateShield();
                snake1.activateGhostMode(1500); // มอบสถานะล่องหน 1.5 วิเพื่อเลื้อยหลบออก
                audioManager.playSound(Constants.SOUND_EAT);
            } else {
                winnerText = "ผู้เล่น 2 ชนะ! (งูเขียวชนตัวเอง)";
                gameOver();
                return;
            }
        }

        if (snake2.isCollidingWithSelf()) {
            if (snake2.isShieldActive()) {
                snake2.deactivateShield();
                snake2.activateGhostMode(1500); // มอบสถานะล่องหน 1.5 วิเพื่อเลื้อยหลบออก
                audioManager.playSound(Constants.SOUND_EAT);
            } else {
                winnerText = "ผู้เล่น 1 ชนะ! (งูส้มชนตัวเอง)";
                gameOver();
                return;
            }
        }

        // ตรวจสอบการชนกับฝ่ายตรงข้าม (เฉพาะเมื่อไม่มีงูตัวใดอยู่ในโหมดล่องหน)
        if (!snake1.isGhostModeActive() && !snake2.isGhostModeActive()) {
            // 3. หัวชนหัว
            if (snake1.getHead().equals(snake2.getHead())) {
                if (snake1.isShieldActive() && snake2.isShieldActive()) {
                    // ป้องกันตัวเองทั้งคู่ -> เสียเกราะคู่ และสลับเข้าร่างล่องหนเพื่อความปลอดภัย
                    snake1.deactivateShield();
                    snake1.activateGhostMode(1500);
                    snake2.deactivateShield();
                    snake2.activateGhostMode(1500);
                    audioManager.playSound(Constants.SOUND_EAT);
                } else if (snake1.isShieldActive() && !snake2.isShieldActive()) {
                    winnerText = "ผู้เล่น 1 ชนะ! (รอดด้วยโล่ป้องกัน)";
                    gameOver();
                    return;
                } else if (!snake1.isShieldActive() && snake2.isShieldActive()) {
                    winnerText = "ผู้เล่น 2 ชนะ! (รอดด้วยโล่ป้องกัน)";
                    gameOver();
                    return;
                } else {
                    determineHeadOnHeadWinner();
                    gameOver();
                    return;
                }
            }

            // 4. หัวชนลำตัว
            if (snake2.isBodyAt(snake1.getHead())) {
                if (snake1.isShieldActive()) {
                    snake1.deactivateShield();
                    snake1.activateGhostMode(1500); // เสียโล่แต่เดินต่อได้
                    audioManager.playSound(Constants.SOUND_EAT);
                } else {
                    winnerText = "ผู้เล่น 2 ชนะ! (งูเขียวชนลำตัวงูส้ม)";
                    gameOver();
                    return;
                }
            }

            if (snake1.isBodyAt(snake2.getHead())) {
                if (snake2.isShieldActive()) {
                    snake2.deactivateShield();
                    snake2.activateGhostMode(1500);
                    audioManager.playSound(Constants.SOUND_EAT);
                } else {
                    winnerText = "ผู้เล่น 1 ชนะ! (งูส้มชนลำตัวงูเขียว)";
                    gameOver();
                    return;
                }
            }
        }
    }

    /**
     * ทำการยิงกระสุนจากงูที่กำหนด
     * @param shooter งูที่เป็นผู้ยิง
     */
    private void shootBullet(Snake shooter) {
        Direction dir = shooter.getDirection();
        Point head = shooter.getHead();
        Color bulletColor = (shooter == snake1) ? new Color(0, 255, 0) : new Color(255, 140, 0);
        if (shooter.shoot()) {
            bullets.add(new Bullet(head.x, head.y, dir, shooter, bulletColor));
            audioManager.playSound(Constants.SOUND_EAT);
        }
    }

    /**
     * Advances each bullet one step, then checks for three removal conditions:
     *   1. Out of bounds — remove bullet.
     *   2. Hit food     — respawn food, remove bullet.
     *   3. Hit opponent — penalise opponent (score & length), remove bullet.
     *
     * Called BULLET_SPEED_MULTIPLIER times per tick to avoid tunnelling through gaps.
     */
    private void updateAndCheckBullets() {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.move();

            // 1. ตรวจสอบการตกขอบจอ
            if (bullet.isOutOfBounds()) {
                iterator.remove();
                continue;
            }

            // 2. ตรวจสอบการชนกับอาหาร (ทำลายอาหารแล้วสร้างอาหารใหม่ตามเงื่อนไขข้อ 5.2)
            if (bullet.getPosition().equals(food.getPosition())) {
                generateNewFood();
                audioManager.playSound(Constants.SOUND_EAT); // เล่นเสียงเพื่อแสดงความเสียหาย
                iterator.remove();
                continue;
            }

            // 3. ตรวจสอบการชนกับงูฝ่ายตรงข้าม (ใช้การชนแบบ Polymorphic ผ่าน interface Collidable)
            Snake opponent = (bullet.getShooter() == snake1) ? snake2 : snake1;
            if (bullet.isCollidingWith(opponent)) {
                opponent.decreaseScore(Constants.BULLET_SCORE_PENALTY);
                opponent.shrinkSafely(); // ลดความยาวคู่ต่อสู้ลง 1 หน่วย (แต่ไม่ต่ำกว่าความยาวเริ่มต้น 3 หน่วย)
                audioManager.playSound(Constants.SOUND_EAT); // เล่นเสียงชนงู
                iterator.remove();
            }
        }
    }

    /**
     * จัดการเมื่อตัวจับเวลาหมดเวลา
     */
    private void handleTimeOut() {
        int score1 = snake1.getFoodEaten();
        int score2 = snake2.getFoodEaten();

        if (score1 > score2) {
            winnerText = "หมดเวลา! ผู้เล่น 1 ชนะด้วยคะแนน (" + score1 + " VS " + score2 + ")";
        } else if (score2 > score1) {
            winnerText = "หมดเวลา! ผู้เล่น 2 ชนะด้วยคะแนน (" + score2 + " VS " + score1 + ")";
        } else {
            // กรณีแต้มเท่ากัน ให้เปรียบเทียบขนาดความยาว
            int len1 = snake1.getLength();
            int len2 = snake2.getLength();
            if (len1 > len2) {
                winnerText = "หมดเวลา! เสมอคะแนน แต่งูเขียวตัวยาวกว่าชนะ (" + len1 + " VS " + len2 + ")";
            } else if (len2 > len1) {
                winnerText = "หมดเวลา! เสมอคะแนน แต่งูส้มตัวยาวกว่าชนะ (" + len2 + " VS " + len1 + ")";
            } else {
                winnerText = "หมดเวลา! เสมอกันทั้งคะแนนและความยาว (" + score1 + " แต้ม, ยาว " + len1 + ")";
            }
        }
        gameOver();
    }

    /**
     * ตัดสินผู้ชนะเมื่อหัวชนหัว
     */
    private void determineHeadOnHeadWinner() {
        if (snake1.getLength() > snake2.getLength()) {
            winnerText = "ผู้เล่น 1 ชนะ! (ตัวใหญ่กว่า)";
        } else if (snake2.getLength() > snake1.getLength()) {
            winnerText = "ผู้เล่น 2 ชนะ! (ตัวใหญ่กว่า)";
        } else {
            winnerText = "เสมอ! (ตัวเท่ากัน)";
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