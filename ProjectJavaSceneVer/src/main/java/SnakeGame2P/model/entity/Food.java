package SnakeGame2P.model.entity;

import SnakeGame2P.utils.Constants;
import SnakeGame2P.model.behavior.Consumable;
import java.awt.*;
import java.util.Random;

/**
 * Abstract base for all food items in the game.
 * Follows OCP: new food types are added by subclassing, not by modifying this class.
 * Each subclass defines its own visual (draw) and effect (onConsumed).
 *
 * คลาสแม่แบบสำหรับอาหารทุกประเภทในเกม ใช้หลักการ Polymorphism และ OCP
 */
public abstract class Food extends GameObject implements Consumable {

    /** Shared RNG — one instance per class, not per method call. */
    private static final Random RANDOM = new Random();

    /** Wall-clock time when this food object was created (ms). Used for expiry and visuals. */
    private final long spawnTime = System.currentTimeMillis();

    /**
     * สร้างอาหารใหม่และสุ่มตำแหน่งเริ่มต้น
     */
    public Food() {
        super(0, 0);
        generateNewPosition();
    }

    /** Returns true when this food has been on the board longer than FOOD_EXPIRE_MS. */
    public boolean isExpired() {
        return System.currentTimeMillis() - spawnTime >= Constants.FOOD_EXPIRE_MS;
    }

    /**
     * Fraction of lifetime remaining: 1.0 = just spawned, 0.0 = expired.
     * Subclasses use this to drive expiry-warning visuals.
     */
    public double getLifeRatio() {
        return Math.max(0.0, 1.0 - (double)(System.currentTimeMillis() - spawnTime) / Constants.FOOD_EXPIRE_MS);
    }

    /**
     * Smooth 0→1 sinusoidal pulse oscillating at the given period (ms).
     * Use shorter periods to pulse faster (e.g. when food is about to expire).
     */
    protected double getPulse(double periodMs) {
        return (Math.sin(System.currentTimeMillis() / periodMs) + 1.0) / 2.0;
    }

    /**
     * สุ่มตำแหน่งใหม่บนบอร์ดเกมตามขนาดช่อง UNIT_SIZE
     */
    public void generateNewPosition() {
        int x = RANDOM.nextInt(Constants.BOARD_WIDTH / Constants.UNIT_SIZE) * Constants.UNIT_SIZE;
        int y = RANDOM.nextInt(Constants.BOARD_HEIGHT / Constants.UNIT_SIZE) * Constants.UNIT_SIZE;
        setPosition(x, y);
    }

    /**
     * ตรวจสอบว่าหัวงูชนกับอาหาร (ถูกกิน) หรือไม่
     */
    @Override
    public boolean isEatenBy(Snake snake) {
        return snake.getHead().equals(getPosition());
    }

    /**
     * เมธอดเชิงนามธรรมที่จะทำงานเมื่ออาหารถูกกิน
     * บังคับให้ subclass ต้องกำหนดพฤติกรรมเอง (Polymorphic Behavior)
     */
    @Override
    public abstract void onConsumed(Snake eater, Snake opponent);

    /**
     * ตรวจสอบว่าอาหารซ้อนทับกับงูผู้เล่น 1 หรือผู้เล่น 2 หรือไม่
     */
    public boolean isOccupiedBy(Snake snake1, Snake snake2) {
        return isOccupiedBySnake(snake1) || isOccupiedBySnake(snake2);
    }

    /**
     * ตรวจสอบว่าอาหารซ้อนทับกับตัวงูที่กำหนดหรือไม่
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
}