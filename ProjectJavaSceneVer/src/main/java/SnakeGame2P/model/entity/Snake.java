package SnakeGame2P.model.entity;

import SnakeGame2P.utils.Constants;
import SnakeGame2P.utils.Direction;
import SnakeGame2P.model.behavior.Movable;
import SnakeGame2P.model.behavior.Collidable;
import SnakeGame2P.model.behavior.PowerBoostable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * คลาสแทนตัวงู สืบทอดจาก GameObject และ implement Movable, PowerBoostable
 * ใช้หลักการ Inheritance และ Polymorphism ผ่าน Interface
 * สามารถเคลื่อนที่และเสริมพลังได้
 */
public class Snake extends GameObject implements Movable, PowerBoostable {
    /** List เก็บตำแหน่ง X ของแต่ละส่วนลำตัว (หัวอยู่ index 0) */
    private final List<Integer> xPositions = new ArrayList<>();

    /** List เก็บตำแหน่ง Y ของแต่ละส่วนลำตัว (หัวอยู่ index 0) */
    private final List<Integer> yPositions = new ArrayList<>();

    /** ทิศทางปัจจุบันของงู */
    private Direction direction;

    /** สถานะการเสริมพลัง (true = กำลังเสริมพลัง) */
    private boolean powerBoostActive = false;

    /** เวลาล่าสุดที่ตัวสั้นลง (ใช้สำหรับคิดเวลาที่เสริมพลังไป) */
    private long lastShrinkTime = 0;

    /** จำนวนอาหารที่กิน (คะแนน) */
    private int foodEaten = 0;

    /** สีหัวงูในสถานะปกติ */
    private final Color headColor;

    /** สีลำตัวงู */
    private final Color bodyColor;

    /** สีหัวงูในสถานะเสริมพลัง */
    private final Color boostHeadColor;

    /**
     * สร้างงูใหม่
     * startX ตำแหน่ง X เริ่มต้น
     * startY ตำแหน่ง Y เริ่มต้น
     * initialDirection ทิศทางเริ่มต้น
     * headColor สีหัวปกติ
     * bodyColor สีลำตัว
     * boostHeadColor สีหัวขณะเสริมพลัง
     */
    public Snake(int startX, int startY, Direction initialDirection,
                 Color headColor, Color bodyColor, Color boostHeadColor) {
        super(startX, startY); // เรียก constructor ของ GameObject
        this.headColor = headColor;
        this.bodyColor = bodyColor;
        this.boostHeadColor = boostHeadColor;
        this.direction = initialDirection;

        // สร้างลำตัวเริ่มต้น 3 ส่วน
        for (int i = 0; i < 3; i++) {
            int offset = i * (initialDirection == Direction.RIGHT ? -Constants.UNIT_SIZE : Constants.UNIT_SIZE);
            xPositions.add(startX + offset);
            yPositions.add(startY);
        }
    }

    /**
     * เคลื่อนที่งูไปข้างหน้าตามทิศทาง
     * implements from Movable interface
     */
    @Override
    public void move() {
        // เลื่อนตำแหน่งทุกส่วนไปข้างหน้า
        for (int i = xPositions.size() - 1; i > 0; i--) {
            xPositions.set(i, xPositions.get(i - 1));
            yPositions.set(i, yPositions.get(i - 1));
        }

        // เคลื่อนที่หัวไปตามทิศทาง
        switch (direction) {
            case UP:    yPositions.set(0, yPositions.get(0) - Constants.UNIT_SIZE); break;
            case DOWN:  yPositions.set(0, yPositions.get(0) + Constants.UNIT_SIZE); break;
            case LEFT:  xPositions.set(0, xPositions.get(0) - Constants.UNIT_SIZE); break;
            case RIGHT: xPositions.set(0, xPositions.get(0) + Constants.UNIT_SIZE); break;
            default: break;
        }

        // อัพเดทตำแหน่งของ GameObject (หัว)
        setPosition(xPositions.get(0), yPositions.get(0));
    }

    /**
     * ตั้งค่าทิศทาง
     * implements from Movable interface
     */
    @Override
    public void setDirection(Direction newDirection) {
        if (!newDirection.isOpposite(direction)) {
            this.direction = newDirection;
        }
    }

    /**
     * ดึงทิศทางปัจจุบัน
     * implements from Movable interface
     */
    @Override
    public Direction getDirection() {
        return direction;
    }

    /**
     * เปิดใช้งานโหมดเสริมพลัง
     * implements from PowerBoostable interface
     */
    @Override
    public void activatePowerBoost() {
        if (!powerBoostActive && getLength() > 3) {
            this.powerBoostActive = true;
            setLastShrinkTime(System.currentTimeMillis());
        }
    }

    /**
     * ปิดใช้งานโหมดเสริมพลัง
     * implements from PowerBoostable interface
     */
    @Override
    public void deactivatePowerBoost() {
        this.powerBoostActive = false;
    }

    /**
     * ตรวจสอบว่าอยู่ในโหมดเสริมพลังหรือไม่
     * implements from PowerBoostable interface
     */
    @Override
    public boolean isPowerBoostActive() {
        return powerBoostActive;
    }

    /**
     * ใช้เอฟเฟกต์เสริมพลัง (ตัวสั้นลง)
     * implements from PowerBoostable interface
     */
    @Override
    public void applyPowerBoostEffect() {
        if (powerBoostActive && getLength() > 3) {
            shrink();
            // หลังจากหดแล้ว ถ้าเหลือ 3 ให้ปิดโหมดเสริมพลัง
            if (getLength() <= 3) {
                deactivatePowerBoost();
            }
        }
    }

    /**
     * ทำให้งูยาวขึ้น (เมื่อกินอาหาร)
     */
    public void grow() {
        xPositions.add(xPositions.get(xPositions.size() - 1));
        yPositions.add(yPositions.get(yPositions.size() - 1));
        foodEaten++;
    }

    /**
     * ทำให้งูสั้นลง (เมื่อเสริมพลัง)
     */
    private void shrink() {
        if (xPositions.size() > 3) {
            xPositions.remove(xPositions.size() - 1);
            yPositions.remove(yPositions.size() - 1);
        }
    }

    /**
     * ตรวจสอบว่าชนตัวเองหรือไม่
     * Override method จาก GameObject
     * return true ถ้าชนตัวเอง
     */
    @Override
    public boolean isCollidingWith(Collidable other) {
        if (other instanceof Snake) {
            return isCollidingWithSnake((Snake) other);
        }
        return super.isCollidingWith(other);
    }

    /**
     * ตรวจสอบว่าชนกับงูตัวอื่นหรือไม่
     * other งูตัวอื่น
     * return true ถ้าชนกัน
     */
    public boolean isCollidingWithSnake(Snake other) {
        Point head = getHead();
        for (int i = 0; i < other.getLength(); i++) {
            if (i == 0 && this == other) continue; // ข้ามหัวตัวเองถ้าเป็นงูเดียวกัน
            if (head.x == other.xPositions.get(i) && head.y == other.yPositions.get(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ตรวจสอบว่าชนตัวเองหรือไม่
     * return true ถ้าชนตัวเอง
     */
    public boolean isCollidingWithSelf() {
        Point head = getHead();
        for (int i = 1; i < xPositions.size(); i++) {
            if (head.x == xPositions.get(i) && head.y == yPositions.get(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ตรวจสอบว่ามีลำตัวส่วนใดอยู่ที่ตำแหน่งที่กำหนดหรือไม่
     * point ตำแหน่งที่ต้องการตรวจสอบ
     * return true ถ้ามีลำตัวส่วนใดอยู่ที่ตำแหน่งนั้น
     */
    public boolean isBodyAt(Point point) {
        for (int i = 1; i < xPositions.size(); i++) {
            if (point.x == xPositions.get(i) && point.y == yPositions.get(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ตรวจสอบว่าออกนอกขอบจอหรือไม่
     * return true ถ้าออกนอกขอบ
     */
    public boolean isOutOfBounds() {
        Point head = getHead();
        return head.x < 0 || head.x >= Constants.BOARD_WIDTH ||
                head.y < 0 || head.y >= Constants.BOARD_HEIGHT;
    }

    /**
     * วาดงู
     * implements from GameObject abstract method
     * g Graphics object
     */
    @Override
    public void draw(Graphics g) {
        for (int i = 0; i < getLength(); i++) {
            if (i == 0) {
                g.setColor(powerBoostActive ? boostHeadColor : headColor);
            } else {
                g.setColor(bodyColor);
            }
            g.fillRect(xPositions.get(i), yPositions.get(i),
                    Constants.UNIT_SIZE, Constants.UNIT_SIZE);
        }
    }

    // ========== Getters และ Setters ==========

    public Point getHead() {
        return new Point(xPositions.get(0), yPositions.get(0));
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