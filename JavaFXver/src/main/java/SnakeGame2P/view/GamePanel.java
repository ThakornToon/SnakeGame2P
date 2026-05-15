package SnakeGame2P.view;

import SnakeGame2P.Main;
import SnakeGame2P.audio.AudioManager;
import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.model.entity.Food;
import SnakeGame2P.utils.Constants;
import SnakeGame2P.utils.GameState;
import SnakeGame2P.utils.Direction;
import SnakeGame2P.view.GameRenderer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * JavaFX version of GamePanel
 * - Handles drawing the game
 * - Contains buttons
 * - Receives keyboard input
 * @author Thakorn
 */
public class GamePanel extends Pane {

    // ========== Game data ==========
    private Snake snake1;
    private Snake snake2;
    private Food food;
    private GameState currentState;
    private String winnerText;

    // ========== Renderer & Audio ==========
    private final GameRenderer renderer;
    private final AudioManager audioManager;

    // ========== Buttons ==========
    private final Button startButton;
    private final Button restartButton;
    private final Button howToPlayButton;
    private final Button muteButton;

    // ========== Drawing ==========
    private final Canvas canvas;
    private final GraphicsContext gc;

    /**
     * วาดเมนู
     * @param audioManager the audio manager
     */
    public GamePanel(AudioManager audioManager) {
        this.audioManager = audioManager;
        this.renderer = new GameRenderer();
        this.currentState = GameState.MENU;

        setPrefSize(Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT);
        setStyle("-fx-background-color: black;");

        // Canvas for drawing
        canvas = new Canvas(Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        // Create buttons
        startButton = createButton("เริ่มเกม", 400, 300, 200, 60, Main.thaiFontMedium);
        howToPlayButton = createButton("วิธีเล่น", 400, 380, 200, 40, Main.thaiFont);
        restartButton = createButton("เริ่มเกมใหม่", 400, 400, 200, 50, Main.thaiFontMedium);
        restartButton.setVisible(false);
        muteButton = createButton("🔊 ปิดเสียง", 850, 20, 120, 30, Main.thaiFontSmall);

    }

    /**
     * Handle button creation in one function
     * @param text text
     * @param x button horizontal position
     * @param y button altitude
     * @param width button width
     * @param height button height
     * @param font text's font
     * @return created button
     */
    private Button createButton(String text, double x, double y, double width, double height, Font font) { 
        Button btn = new Button(text);
        btn.setLayoutX(x);
        btn.setLayoutY(y);
        btn.setPrefSize(width, height);
        btn.setFont(font);
        btn.setStyle("-fx-cursor: hand; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        btn.setFocusTraversable(false);
        getChildren().add(btn);
        return btn;
    }

    // ========== Keyboard input setup ==========

    /**
     * set up movement events listener
     * @param scene scene
     */
    public void setupInput(javafx.scene.Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (currentState != GameState.PLAYING) return;
            if (snake1 == null || snake2 == null) return;

            // Player 1: WASD
            if (event.getCode() == KeyCode.W) snake1.setDirection(Direction.UP);
            if (event.getCode() == KeyCode.S) snake1.setDirection(Direction.DOWN);
            if (event.getCode() == KeyCode.A) snake1.setDirection(Direction.LEFT);
            if (event.getCode() == KeyCode.D) snake1.setDirection(Direction.RIGHT);
            // Player 2: Arrow Keys
            if (event.getCode() == KeyCode.UP) snake2.setDirection(Direction.UP);
            if (event.getCode() == KeyCode.DOWN) snake2.setDirection(Direction.DOWN);
            if (event.getCode() == KeyCode.LEFT) snake2.setDirection(Direction.LEFT);
            if (event.getCode() == KeyCode.RIGHT) snake2.setDirection(Direction.RIGHT);
        });
    }

    // ========== Update game data and redraw ==========

    /**
     * Called every frame to update the game
     * @param snake1 player 1's snake
     * @param snake2 player 2's snake
     * @param food the food object
     * @param state Whether it's in the menu, paused, playing, or game over screen
     * @param winner the winner
     */
    public void updateGameData(Snake snake1, Snake snake2, Food food, GameState state, String winner) {
        this.snake1 = snake1;
        this.snake2 = snake2;
        this.food = food;
        this.currentState = state;
        this.winnerText = winner;
        draw(); // redraw
    }

    /**
     * draw the board
     */
    private void draw() {
        gc.clearRect(0, 0, Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT);
        renderer.render(gc, currentState, snake1, snake2, food, winnerText);
    }

    // ========== Button access ==========
    public Button getStartButton() { return startButton; }
    public Button getRestartButton() { return restartButton; }
    public Button getHowToPlayButton() { return howToPlayButton; }
    public Button getMuteButton() { return muteButton; }

    /**
     * Make the restart button visible or invisible
     * @param visible whether it should be visible or not
     */
    public void setRestartVisible(boolean visible) {
        restartButton.setVisible(visible);
        if (!visible) {
            // เมื่อซ่อนปุ่ม restart ให้ request focus กลับมาที่ panel
            requestFocus();
        }
    }

    /**
     * Set menu visible or invisible
     * @param visible Whether it should be visible or not!
     */
    public void setMenuVisible(boolean visible) {
        startButton.setVisible(visible);
        howToPlayButton.setVisible(visible);
        if (!visible) {
            requestFocus();
        }
    }

    /**
     * Change mute button text: 🔇 เปิดเสียง if muted, else 🔊 ปิดเสียง"
     * @param muted whether the sound is muted or not!
     */
    public void updateMuteButton(boolean muted) {
        muteButton.setText(muted ? "🔇 เปิดเสียง" : "🔊 ปิดเสียง");

        if (muted) {
            muteButton.setStyle("-fx-cursor: hand; -fx-background-color: #f44336; -fx-text-fill: white;");
        } else {
            muteButton.setStyle("-fx-cursor: hand; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        }
    }
}