package SnakeGame2P.model.entity;

import SnakeGame2P.utils.Constants;
import SnakeGame2P.model.behavior.Consumable;

import javafx.scene.canvas.GraphicsContext;

import java.util.Random;

/**
 * JavaFX Food class
 * @author Unnawas
 */
public class Food extends GameObject implements Consumable {
    /**
     * The random object. It is called to get a pseudo-randomized value!
     */
    private final Random random = new Random();

    /**
     * Spawn food at randomized position!
     */
    public Food() {
        super(0, 0);
        generateNewPosition();
    }

    /**
     * Create randomized (x, y), and set this.position to the created (x, y)
     */
    public void generateNewPosition() {
        int x = random.nextInt(Constants.BOARD_WIDTH / Constants.UNIT_SIZE) * Constants.UNIT_SIZE;
        int y = random.nextInt(Constants.BOARD_HEIGHT / Constants.UNIT_SIZE) * Constants.UNIT_SIZE;
        setPosition(x, y);
    }

    /**
     * Check whether a snake head occupies the same position
     */
    @Override
    public boolean isEatenBy(Snake snake) {
        return snake.getHead().equals(getPosition());
    }

    /**
     * Elongates the snake when this is consumed
     */
    @Override
    public void onConsumed(Snake snake) {
        snake.grow();
    }

    /**
     * Check whether both players are simultaneously touching this food
     */
    public boolean isOccupiedBy(Snake snake1, Snake snake2) {
        return isOccupiedBySnake(snake1) || isOccupiedBySnake(snake2);
    }

    /**
     * Check whether a snake is touching this food
     * @param snake the snake object to be checked
     * @return whether the snake is touching this food
     */
    private boolean isOccupiedBySnake(Snake snake) {
        for (int i = 0; i < snake.getLength(); i++) {
            if (getX() == snake.getXPositions().get(i) &&
                    getY() == snake.getYPositions().get(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Draw the food (a circle)
     * @param gc JavaFx Graphics Context
     */
    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Constants.FOOD_COLOR); // ต้องเป็น javafx.scene.paint.Color
        gc.fillOval(getX(), getY(), Constants.UNIT_SIZE, Constants.UNIT_SIZE);
    }
}