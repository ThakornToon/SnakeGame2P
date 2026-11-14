package SnakeGame2P.model.entity;

import SnakeGame2P.utils.Constants;
import SnakeGame2P.utils.Direction;
import SnakeGame2P.model.behavior.Movable;
import SnakeGame2P.model.behavior.Collidable;
import java.awt.*;

/**
 * คลาสสำหรับกระสุนที่ปล่อยออกมาจากงูเพื่อโจมตีฝ่ายตรงข้าม
 * สืบทอดจาก GameObject และ implement Movable
 */
public class Bullet extends GameObject implements Movable {
    /** ทิศทางการเคลื่อนที่ของกระสุน */
    private Direction direction;

    /** งูที่เป็นคนยิงกระสุนนี้ออกไป */
    private final Snake shooter;

    /** สีของกระสุน */
    private final Color color;

    /**
     * คอนสตรัคเตอร์สำหรับสร้างกระสุน
     * @param x ตำแหน่ง X เริ่มต้น
     * @param y ตำแหน่ง Y เริ่มต้น
     * @param direction ทิศทางการวิ่งของกระสุน
     * @param shooter งูที่เป็นผู้ยิง
     * @param color สีของกระสุน
     */
    public Bullet(int x, int y, Direction direction, Snake shooter, Color color) {
        super(x, y);
        this.direction = direction;
        this.shooter = shooter;
        this.color = color;
    }

    /**
     * เคลื่อนที่กระสุนไปข้างหน้า 1 ช่อง (ตามขนาด UNIT_SIZE)
     */
    @Override
    public void move() {
        switch (direction) {
            case UP:    position.y -= Constants.UNIT_SIZE; break;
            case DOWN:  position.y += Constants.UNIT_SIZE; break;
            case LEFT:  position.x -= Constants.UNIT_SIZE; break;
            case RIGHT: position.x += Constants.UNIT_SIZE; break;
            default: break;
        }
    }

    /**
     * กำหนดทิศทางการเคลื่อนที่ของกระสุน
     */
    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * ดึงทิศทางปัจจุบันของกระสุน
     */
    @Override
    public Direction getDirection() {
        return direction;
    }

    /**
     * ดึงงูที่เป็นผู้ยิงกระสุนนี้
     * @return Snake ผู้ยิง
     */
    public Snake getShooter() {
        return shooter;
    }

    /**
     * ตรวจสอบว่ากระสุนออกนอกขอบเขตหน้าจอหลักหรือไม่
     * @return true หากออกนอกจอ
     */
    public boolean isOutOfBounds() {
        return position.x < 0 || position.x >= Constants.BOARD_WIDTH ||
               position.y < 0 || position.y >= Constants.BOARD_HEIGHT;
    }

    /**
     * วาดกระสุนลงบนหน้าจอ
     * วาดเป็นทรงกลมมีเงาเรืองแสงอยู่ตรงกลางช่องกริด
     */
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        // เปิดใช้งาน Antialiasing เพื่อให้กระสุนกลมมนสวยงาม
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // คำนวณขนาดและตำแหน่งให้กระสุนอยู่ตรงกลางช่องกริดพอดี
        int bulletSize = (int) (Constants.UNIT_SIZE * 0.5); // ขนาดเป็น 50% ของช่องปกติ
        int offset = (Constants.UNIT_SIZE - bulletSize) / 2;
        int drawX = position.x + offset;
        int drawY = position.y + offset;

        // วาดแสงเรืองแสงภายนอก (Glow effect)
        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
        g2d.fillOval(drawX - 2, drawY - 2, bulletSize + 4, bulletSize + 4);

        // วาดตัวกระสุนหลัก
        g2d.setColor(color);
        g2d.fillOval(drawX, drawY, bulletSize, bulletSize);
    }

    /**
     * ตรวจสอบการชนของกระสุนกับวัตถุอื่น
     * หากตรวจสอบกับ Snake จะทำการตรวจสอบการชนกับทุกเซกเมนต์ (หัวและลำตัว) ของงูตัวนั้น
     */
    @Override
    public boolean isCollidingWith(Collidable other) {
        if (other instanceof Snake) {
            Snake snake = (Snake) other;
            for (int i = 0; i < snake.getLength(); i++) {
                if (this.position.x == snake.getXPositions().get(i) &&
                    this.position.y == snake.getYPositions().get(i)) {
                    return true;
                }
            }
            return false;
        }
        return super.isCollidingWith(other);
    }
}
