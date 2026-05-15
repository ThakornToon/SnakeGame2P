package SnakeGame2P.controller;

import SnakeGame2P.model.entity.*;
import SnakeGame2P.utils.GameState;
import SnakeGame2P.view.GamePanel;
import SnakeGame2P.audio.AudioManager;
import SnakeGame2P.utils.Constants;
import SnakeGame2P.utils.Direction;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;

/**
 * JavaFX Game Controller,
 * Handle player inputs and game initialization
 * @author Unnawas
 */
public class GameController {

    // ========== Components ==========
    private final GamePanel panel;
    private final AudioManager audioManager;
    private final PowerBoostManager powerBoostManager;
    private final Scene scene;

    // ========== Model ==========
    private Snake snake1;
    private Snake snake2;
    private Food food;
    private GameState currentState;
    private String winnerText = "";

    // ========== Game Loop ==========
    private AnimationTimer gameLoop;
    private long lastUpdate = 0;

    /**
     * Constructor: set up ปุ่มกับคีย์บอร์ด, เล่นเสียง, etc
     * @param panel Handle ข้อมูลกระดานเกมสำหรับใช้เล่น
     * @param scene JavaFX Scene
     * @param audioManager Handle เสียงทั้งหมดในเกม
     */
    public GameController(GamePanel panel, Scene scene, AudioManager audioManager) {
        this.panel = panel;
        this.scene = scene;
        this.audioManager = audioManager;
        this.powerBoostManager = new PowerBoostManager();

        currentState = GameState.MENU;

        setupButtons();
        setupKeyboard();

        panel.updateMuteButton(audioManager.isMuted());
        playMusicForCurrentState();
        updateView();
    }

    // ================= UI SETUP =================

    /**
     * ใส่ event listener ไว้ที่ปุ่ม เริ่ม, เริ่มใหม่, tutorial, เปิด/ปิดเสียง
     */
    private void setupButtons() {
        panel.getStartButton().setOnAction(e -> startGame());
        panel.getRestartButton().setOnAction(e -> restartGame());
        panel.getHowToPlayButton().setOnAction(e -> showHowToPlay());
        panel.getMuteButton().setOnAction(e -> toggleMute());
    }

    /**
     * ใส่ event listener สำหรับการ ออกเกม, หยุดเกม, เคลื่อนไหว
     */
    private void setupKeyboard() {
        scene.setOnKeyPressed(e -> handleKeyPressed(e.getCode()));
        scene.setOnKeyReleased(e -> handleKeyReleased(e.getCode()));
    }

    // ================= INPUT =================

    /**
     * route ปุ่มที่กดไปหา การทำงานต่่างๆ
     * @param key OnKeyPressed.KeyCode, such as E,R,T,F
     */
    private void handleKeyPressed(KeyCode key) {

        if (key == KeyCode.ESCAPE) {
            togglePause();
            return;
        }

        if (key == KeyCode.R && currentState == GameState.PAUSED) {
            returnToMenu();
            return;
        }

        if (currentState != GameState.PLAYING) return;
        if (snake1 == null || snake2 == null) return;

        handleMovementKeys(key);
    }

    /**
     * จัดการทิศทางการเคลื่อนที่ของงู
     * @param key OnKeyPressed.KeyCode, such as W,A,S,D
     */
    private void handleMovementKeys(KeyCode key) {
        switch (key) {
            // Player 1
            case W -> snake1.setDirection(Direction.UP);
            case S -> snake1.setDirection(Direction.DOWN);
            case A -> snake1.setDirection(Direction.LEFT);
            case D -> snake1.setDirection(Direction.RIGHT);
            case SHIFT -> powerBoostManager.activatePowerBoost(snake1);

            // Player 2
            case UP -> snake2.setDirection(Direction.UP);
            case DOWN -> snake2.setDirection(Direction.DOWN);
            case LEFT -> snake2.setDirection(Direction.LEFT);
            case RIGHT -> snake2.setDirection(Direction.RIGHT);
            case M -> powerBoostManager.activatePowerBoost(snake2);

            default -> {}
        }
    }

    /**
     * Handle การยกนิ้วออกจากปุ่ม สำหรับ power Boost
     * @param key OnKeyPressed.KeyCode, such as SHIFT, M
     */
    private void handleKeyReleased(KeyCode key) {
        if (currentState != GameState.PLAYING) return;
        if (snake1 == null || snake2 == null) return;

        switch (key) {
            case SHIFT -> powerBoostManager.deactivatePowerBoost(snake1);
            case M -> powerBoostManager.deactivatePowerBoost(snake2);
            default -> {}
        }
    }

    // ================= GAME FLOW =================

    /**
     * initialize snake game loop
     */
    public void startGame() {
        currentState = GameState.PLAYING;

        panel.setMenuVisible(false);
        panel.setRestartVisible(false);

        playMusicForCurrentState();

        initializeGame();
        startLoop();
        updateView();
        panel.requestFocus();
    }

    /**
     * take player back to game menu.
     * This and returnToMenu() do the same thing.
     * This one for handling game state.
     */
    public void restartGame() {
        stopLoop();

        currentState = GameState.MENU;
        panel.setMenuVisible(true);
        panel.setRestartVisible(false);

        playMusicForCurrentState();

        updateView();
        panel.requestFocus();
    }

    /**
     * take player back to game menu.
     * This and restartGame() do the same thing.
     * This one for UI logic.
     */
    public void returnToMenu() {
        restartGame();
    }

    /**
     * Handle game pausing
     */
    public void togglePause() {
        if (currentState == GameState.PLAYING) {
            currentState = GameState.PAUSED;
            stopLoop();
            playMusicForCurrentState();
        } else if (currentState == GameState.PAUSED) {
            currentState = GameState.PLAYING;
            startLoop();
            playMusicForCurrentState();
        }
        updateView();
        panel.requestFocus();
    }

    // ================= INITIALIZE =================

    /**
     * Spawn both players and a food
     */
    private void initializeGame() {
        snake1 = new Snake(Constants.SNAKE1_START_X, Constants.SNAKE1_START_Y,
                Direction.RIGHT, Constants.SNAKE1_HEAD_COLOR,
                Constants.SNAKE1_BODY_COLOR, Constants.SNAKE1_BOOST_HEAD_COLOR);

        snake2 = new Snake(Constants.SNAKE2_START_X, Constants.SNAKE2_START_Y,
                Direction.LEFT, Constants.SNAKE2_HEAD_COLOR,
                Constants.SNAKE2_BODY_COLOR, Constants.SNAKE2_BOOST_HEAD_COLOR);

        food = new Food();
        generateNewFood();
    }

    /**
     * Generate food object
     */
    private void generateNewFood() {
        do {
            food.generateNewPosition();
        } while (food.isOccupiedBy(snake1, snake2));
    }

    // ================= GAME LOOP =================

    /**
     * called to start the snake game loop
     */
    private void startLoop() {
        lastUpdate = System.nanoTime();

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate < Constants.NORMAL_SPEED_DELAY * 1_000_000) return;

                updateGame();
                lastUpdate = now;
            }
        };
        gameLoop.start();
    }

    /**
     * Stop the loop, of course
     */
    private void stopLoop() {
        if (gameLoop != null) gameLoop.stop();
    }

    /**
     * Move snake according to their direction, check whether it ate a food or touched other snake.
     * Called every frame?
     */
    private void updateGame() {
        if (currentState != GameState.PLAYING) return;

        powerBoostManager.handlePowerBoosts(snake1, snake2);

        snake1.move();
        snake2.move();

        checkFoodCollision();
        checkCollisions();

        if (currentState == GameState.PLAYING) {
            updateView();
        }
    }

    // ================= GAME LOGIC =================

    /**
     * Make the snake longer and create new food
     */
    private void checkFoodCollision() {
        if (food.isEatenBy(snake1)) {
            food.onConsumed(snake1);
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
     * Handle game end when a player moves beyond the screen.
     * Also handle suicide and crashing onto another snake.
     */
    private void checkCollisions() {

        if (snake1.isOutOfBounds()) {
            winnerText = "Player 2 Wins! (งูเขียวตกขอบ)";
            gameOver();
            return;
        }

        if (snake2.isOutOfBounds()) {
            winnerText = "Player 1 Wins! (งูส้มตกขอบ)";
            gameOver();
            return;
        }

        if (snake1.isCollidingWithSelf()) {
            winnerText = "Player 2 Wins! (งูเขียวชนตัวเอง)";
            gameOver();
            return;
        }

        if (snake2.isCollidingWithSelf()) {
            winnerText = "Player 1 Wins! (งูส้มชนตัวเอง)";
            gameOver();
            return;
        }

        if (snake1.getHead().equals(snake2.getHead())) {
            determineHeadOnHeadWinner();
            gameOver();
            return;
        }

        if (snake2.isBodyAt(snake1.getHead())) {
            winnerText = "Player 2 Wins! (งูเขียวชนลำตัวงูส้ม)";
            gameOver();
            return;
        }

        if (snake1.isBodyAt(snake2.getHead())) {
            winnerText = "Player 1 Wins! (งูส้มชนลำตัวงูเขียว)";
            gameOver();
            return;
        }
    }

    /**
     * Handle the case where a player or both uses power boost and collide at the head.
     * The player with power boost wins.
     * If both have power boost, determine victory from snake length.
     * Else, it's a draw.
     */
    private void determineHeadOnHeadWinner() {
        if (snake1.isPowerBoostActive() && !snake2.isPowerBoostActive()) {
            winnerText = "Player 1 Wins! (เสริมพลังชนะ)";
        } else if (!snake1.isPowerBoostActive() && snake2.isPowerBoostActive()) {
            winnerText = "Player 2 Wins! (เสริมพลังชนะ)";
        } else {
            if (snake1.getLength() > snake2.getLength()) {
                winnerText = "Player 1 Wins! (ตัวใหญ่กว่า)";
            } else if (snake2.getLength() > snake1.getLength()) {
                winnerText = "Player 2 Wins! (ตัวใหญ่กว่า)";
            } else {
                winnerText = "Draw! (ตัวเท่ากัน)";
            }
        }
    }

    /**
     * Stop game loop, show restart button
     */
    private void gameOver() {
        currentState = GameState.GAME_OVER;
        stopLoop();

        playMusicForCurrentState();

        panel.setRestartVisible(true);
        updateView();
        panel.requestFocus();
    }

    // ================= VIEW =================

    /**
     * This is called every frame to update game data.
     */
    private void updateView() {
        panel.updateGameData(snake1, snake2, food, currentState, winnerText);
    }

    // ================= UI =================

    /**
     * Tutorial box
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

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("วิธีเล่น");
        alert.setHeaderText(null);
        alert.setContentText(howToPlay);
        alert.showAndWait();

        panel.requestFocus();
    }

    /**
     * Mute or not mute the sound
     */
    private void toggleMute() {
        audioManager.toggleMute();
        panel.updateMuteButton(audioManager.isMuted());

        if (!audioManager.isMuted()) {
            playMusicForCurrentState();
        }

        panel.requestFocus();
    }

    /**
     * Play music by current GameState
     */
    private void playMusicForCurrentState() {
        if (audioManager.isMuted()) return;

        audioManager.stopAllMusic();

        switch (currentState) {
            case MENU:
                audioManager.playBackgroundMusic(Constants.SOUND_MENU);
                break;
            case PLAYING:
                audioManager.playBackgroundMusic(Constants.SOUND_BACKGROUND);
                break;
            case GAME_OVER:
                audioManager.playBackgroundMusic(Constants.SOUND_GAME_OVER);
                break;
            case PAUSED:
                // PAUSED ไม่เล่นเพลง
                break;
        }
    }
}