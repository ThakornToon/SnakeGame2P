package SnakeGame2P.model.entity;

import SnakeGame2P.utils.Constants;
import SnakeGame2P.model.behavior.Consumable;
import java.awt.*;
import java.util.Random;

/**
 * คลาสแทนอาหาร สืบทอดจาก GameObject และ implement Consumable
 * สามารถถูกกินได้
 */
public class Food extends GameObject implements Consumable {
    /** ตัวสร้างเลขสุ่ม */
    private final Random random = new Random();

    /**
     * สร้างอาหารใหม่
     */
    public Food() {
        super(0, 0);
        generateNewPosition();
    }

    /**
     * สุ่มตำแหน่งใหม่
     */
    public void generateNewPosition() {
        int x = random.nextInt(Constants.BOARD_WIDTH / Constants.UNIT_SIZE) * Constants.UNIT_SIZE;
        int y = random.nextInt(Constants.BOARD_HEIGHT / Constants.UNIT_SIZE) * Constants.UNIT_SIZE;
        setPosition(x, y);
    }

    /**
     * ตรวจสอบว่าถูกงูกินหรือไม่
     * implements from Consumable interface
     * snake งูที่ต้องการตรวจสอบ
     * return true ถ้าถูกกิน
     */
    @Override
    public boolean isEatenBy(Snake snake) {
        return snake.getHead().equals(getPosition());
    }

    /**
     * เอฟเฟกต์เมื่อถูกกิน (งูยาวขึ้น)
     * implements from Consumable interface
     * snake งูที่กิน
     */
    @Override
    public void onConsumed(Snake snake) {
        snake.grow();
    }

    /**
     * ตรวจสอบว่าอาหารซ้อนทับกับงูหรือไม่
     * snake1 งูผู้เล่น 1
     * snake2 งูผู้เล่น 2
     * return true ถ้าซ้อนทับ
     */
    public boolean isOccupiedBy(Snake snake1, Snake snake2) {
        return isOccupiedBySnake(snake1) || isOccupiedBySnake(snake2);
    }

    /**
     * ตรวจสอบว่าอาหารซ้อนทับกับงูตัวใดตัวหนึ่งหรือไม่
     * snake งูที่ต้องการตรวจสอบ
     * return true ถ้าซ้อนทับ
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
     * วาดอาหาร
     * implements from GameObject abstract method
     * g Graphics object
     */
    @Override
    public void draw(Graphics g) {
        g.setColor(Constants.FOOD_COLOR);
        g.fillOval(getX(), getY(), Constants.UNIT_SIZE, Constants.UNIT_SIZE);
    }
}