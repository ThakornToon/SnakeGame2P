package SnakeGame2P.model.entity;

import SnakeGame2P.utils.Constants;
import SnakeGame2P.utils.Direction;
import SnakeGame2P.model.behavior.Movable;
import SnakeGame2P.model.behavior.Collidable;
import SnakeGame2P.model.behavior.PowerBoostable;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * JavaFX Snake class
 * @author Unnawas
 */
public class Snake extends GameObject implements Movable, PowerBoostable {
    /**
     * Abscissa of the segments
     */
    private final List<Integer> xPositions = new ArrayList<>();
    /**
     * Ordinate of the segments
     */
    private final List<Integer> yPositions = new ArrayList<>();
    /**
     * direction enum
     */
    private Direction direction;
    private boolean powerBoostActive = false;
    private long lastShrinkTime = 0;
    private int foodEaten = 0;

    private final Color headColor;
    private final Color bodyColor;
    private final Color boostHeadColor;

    /**
     * Constructor: Initialize all fields, create the segments on the left side if the head faces right, else vice versa
     * @param startX initial abscissa
     * @param startY initial ordinate
     * @param initialDirection initial direction enum
     * @param headColor initial head color
     * @param bodyColor initial body color
     * @param boostHeadColor initial head color when power boosted
     */
    public Snake(int startX, int startY, Direction initialDirection,
                 Color headColor, Color bodyColor, Color boostHeadColor) {

        super(startX, startY);

        this.direction = initialDirection;
        this.headColor = headColor;
        this.bodyColor = bodyColor;
        this.boostHeadColor = boostHeadColor;

        // initial body (3 segments)
        for (int i = 0; i < 3; i++) {
            int offset = i * (initialDirection == Direction.RIGHT ? -Constants.UNIT_SIZE : Constants.UNIT_SIZE);
            xPositions.add(startX + offset);
            yPositions.add(startY);
        }
    }

    // ================= MOVEMENT =================

    /**
     * Shift all segments' position to the previous segments' position.
     * Move the head according to direction enum.
     */
    @Override
    public void move() {
        for (int i = xPositions.size() - 1; i > 0; i--) {
            xPositions.set(i, xPositions.get(i - 1));
            yPositions.set(i, yPositions.get(i - 1));
        }

        switch (direction) {
            case UP -> yPositions.set(0, yPositions.get(0) - Constants.UNIT_SIZE);
            case DOWN -> yPositions.set(0, yPositions.get(0) + Constants.UNIT_SIZE);
            case LEFT -> xPositions.set(0, xPositions.get(0) - Constants.UNIT_SIZE);
            case RIGHT -> xPositions.set(0, xPositions.get(0) + Constants.UNIT_SIZE);
        }

        setPosition(xPositions.get(0), yPositions.get(0));
    }

    /**
     * Set new direction, cannot turn the snake back to where it came from directly
     * @param newDirection Up, Down, Left, or Right
     */
    @Override
    public void setDirection(Direction newDirection) {
        if (!newDirection.isOpposite(direction)) {
            this.direction = newDirection;
        }
    }

    /**
     * Getter for direction
     * @return direction
     */
    @Override
    public Direction getDirection() {
        return direction;
    }

    // ================= POWER BOOST =================

    /**
     * Power boost the snake
     */
    @Override
    public void activatePowerBoost() {
        if (!powerBoostActive && getLength() > 3) {
            powerBoostActive = true;
            lastShrinkTime = System.currentTimeMillis();
        }
    }

    /**
     * Deactivate power boost
     */
    @Override
    public void deactivatePowerBoost() {
        powerBoostActive = false;
    }

    /**
     * Getter for power boost
     * @return whether it is active
     */
    @Override
    public boolean isPowerBoostActive() {
        return powerBoostActive;
    }

    /**
     * Shrink the snake if the snake is longer than 3 Constants.UNIT_SIZE
     */
    @Override
    public void applyPowerBoostEffect() {
        if (powerBoostActive && getLength() > 3) {
            shrink();
            if (getLength() <= 3) {
                deactivatePowerBoost();
            }
        }
    }

    // ================= GROW / SHRINK =================

    /**
     * Make the snake grow, new segment appear behind last segment
     * It grows when a food is eaten
     */
    public void grow() {
        xPositions.add(xPositions.get(xPositions.size() - 1));
        yPositions.add(yPositions.get(yPositions.size() - 1));
        foodEaten++;
    }

    /**
     * Remove the last segment
     */
    private void shrink() {
        if (getLength() > 3) {
            xPositions.remove(xPositions.size() - 1);
            yPositions.remove(yPositions.size() - 1);
        }
    }

    // ================= COLLISION =================

    /**
     * Check for collision with other snake or food
     * @param other an object that is capable of collision
     * @return whether it is colliding with other
     */
    @Override
    public boolean isCollidingWith(Collidable other) {
        if (other instanceof Snake) {
            return isCollidingWithSnake((Snake) other);
        }
        return super.isCollidingWith(other);
    }

    /**
     * Check whether this is colliding with any part of other snake
     * @param other the other snake to be checked
     * @return whether this is colliding with any part of other snake
     */
    public boolean isCollidingWithSnake(Snake other) {
        Point2D head = getHead();

        for (int i = 0; i < other.getLength(); i++) {
            if (i == 0 && this == other) continue;

            if (head.getX() == other.xPositions.get(i) &&
                    head.getY() == other.yPositions.get(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check Whether the head is colliding with any segments
     * @return Whether the head is colliding with any segments
     */
    public boolean isCollidingWithSelf() {
        Point2D head = getHead();

        for (int i = 1; i < xPositions.size(); i++) {
            if (head.getX() == xPositions.get(i) &&
                    head.getY() == yPositions.get(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param point the point to be checked against
     * @return Whether a segment is at point
     */
    public boolean isBodyAt(Point2D point) {
        for (int i = 1; i < xPositions.size(); i++) {
            if (point.getX() == xPositions.get(i) &&
                    point.getY() == yPositions.get(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return Whether the snake head is beyond the game board
     */
    public boolean isOutOfBounds() {
        Point2D head = getHead();

        return head.getX() < 0 || head.getX() >= Constants.BOARD_WIDTH ||
                head.getY() < 0 || head.getY() >= Constants.BOARD_HEIGHT;
    }

    // ================= RENDER =================

    /**
     * Draw the sake
     * @param gc JavaFX GraphicsContext
     */
    @Override
    public void draw(GraphicsContext gc) {
        for (int i = 0; i < getLength(); i++) {

            if (i == 0) {
                gc.setFill(powerBoostActive ? boostHeadColor : headColor);
            } else {
                gc.setFill(bodyColor);
            }

            gc.fillRect(
                    xPositions.get(i),
                    yPositions.get(i),
                    Constants.UNIT_SIZE,
                    Constants.UNIT_SIZE
            );
        }
    }

    // ================= GETTERS =================

    public Point2D getHead() {
        return new Point2D(xPositions.get(0), yPositions.get(0));
    }

    public int getLength() {
        return xPositions.size();
    }

    public int getFoodEaten() {
        return foodEaten;
    }

    public long getLastShrinkTime() {
        return lastShrinkTime;
    }

    public void setLastShrinkTime(long time) {
        this.lastShrinkTime = time;
    }

    public List<Integer> getXPositions() {
        return new ArrayList<>(xPositions);
    }

    public List<Integer> getYPositions() {
        return new ArrayList<>(yPositions);
    }
}