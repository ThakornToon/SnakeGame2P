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
 * Represents a player-controlled snake.
 * Inherits position/collision from GameObject; implements Movable and PowerBoostable.
 *
 * Segment list: index 0 = head, last index = tail.
 * Move algorithm: shift all segments one step backward, then advance index 0 in the current direction.
 *
 * คลาสแทนตัวงู สืบทอดจาก GameObject และ implement Movable, PowerBoostable
 */
public class Snake extends GameObject implements Movable, PowerBoostable {
    /** X coordinates of each body segment; index 0 is the head. */
    private final List<Integer> xPositions = new ArrayList<>();

    /** Y coordinates of each body segment; index 0 is the head. */
    private final List<Integer> yPositions = new ArrayList<>();

    private Direction direction;

    /** true while the player is holding the speed-boost key. */
    private boolean powerBoostActive = false;

    /** Wall-clock time of the last speed-boost shrink, used to enforce the 500 ms interval. */
    private long lastShrinkTime = 0;

    /** When active, this snake phases through the opponent (no collision). */
    private boolean ghostModeActive = false;
    private long ghostModeEndTime = 0;

    /** When active, the next collision is absorbed instead of causing game-over. */
    private boolean shieldActive = false;

    /** When true, the snake cannot move until freezeEndTime elapses. */
    private boolean frozen = false;
    private long freezeEndTime = 0;

    /** Cumulative food eaten; used as the match score (can go negative from bullet hits). */
    private int foodEaten = 0;

    /** Timestamp of the last successful shot, used to enforce the shoot cooldown. */
    private long lastShootTime = 0;

    /** Number of consecutive foods eaten within COMBO_WINDOW_MS of each other. */
    private int comboCount = 0;
    /** Timestamp of the last food eaten, used to extend or reset the combo chain. */
    private long lastEatTime = 0;

    private final Color headColor;
    private final Color bodyColor;
    /** Head color shown while speed boost is active. */
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

        for (int i = 0; i < Constants.SNAKE_INITIAL_LENGTH; i++) {
            int offset = i * (initialDirection == Direction.RIGHT ? -Constants.UNIT_SIZE : Constants.UNIT_SIZE);
            xPositions.add(startX + offset);
            yPositions.add(startY);
        }
    }

    /**
     * Advances the snake one grid unit in its current direction.
     * Each segment copies the coordinates of the segment ahead of it (shift backward),
     * then the head is moved to the new position.
     */
    @Override
    public void move() {
        // Shift every segment one step back (tail end first to avoid overwriting)
        for (int i = xPositions.size() - 1; i > 0; i--) {
            xPositions.set(i, xPositions.get(i - 1));
            yPositions.set(i, yPositions.get(i - 1));
        }

        // Move head in the current direction
        switch (direction) {
            case UP:    yPositions.set(0, yPositions.get(0) - Constants.UNIT_SIZE); break;
            case DOWN:  yPositions.set(0, yPositions.get(0) + Constants.UNIT_SIZE); break;
            case LEFT:  xPositions.set(0, xPositions.get(0) - Constants.UNIT_SIZE); break;
            case RIGHT: xPositions.set(0, xPositions.get(0) + Constants.UNIT_SIZE); break;
            default: break;
        }

        // Sync the inherited GameObject position field with the new head position
        setPosition(xPositions.get(0), yPositions.get(0));
    }

    /**
     * Changes direction, ignoring 180-degree reversals to prevent instant self-collision.
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
        if (!powerBoostActive && getLength() > Constants.SNAKE_INITIAL_LENGTH) {
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
     * เปิดใช้งานโหมดทะลุตัวฝ่ายตรงข้าม (Ghost Mode)
     * durationMs ระยะเวลาของโหมด (มิลลิวินาที)
     */
    public void activateGhostMode(long durationMs) {
        this.ghostModeActive = true;
        this.ghostModeEndTime = System.currentTimeMillis() + durationMs;
    }

    /**
     * ปิดใช้งานโหมดทะลุตัวฝ่ายตรงข้าม (Ghost Mode)
     */
    public void deactivateGhostMode() {
        this.ghostModeActive = false;
        this.ghostModeEndTime = 0;
    }

    /**
     * ตรวจสอบว่าอยู่ในโหมดทะลุตัวฝ่ายตรงข้ามหรือไม่
     */
    public boolean isGhostModeActive() {
        if (ghostModeActive && System.currentTimeMillis() > ghostModeEndTime) {
            deactivateGhostMode();
        }
        return ghostModeActive;
    }

    /**
     * ดึงเวลาที่เหลือในโหมดทะลุตัวฝ่ายตรงข้าม (มิลลิวินาที)
     */
    public long getGhostModeRemainingTime() {
        if (!isGhostModeActive()) return 0;
        long remaining = ghostModeEndTime - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    /**
     * เปิดใช้งานเกราะป้องกัน (Shield)
     */
    public void activateShield() {
        this.shieldActive = true;
    }

    /**
     * ปิดใช้งานเกราะป้องกัน
     */
    public void deactivateShield() {
        this.shieldActive = false;
    }

    /**
     * ตรวจสอบว่าเกราะป้องกันทำงานอยู่หรือไม่
     */
    public boolean isShieldActive() {
        return shieldActive;
    }

    /**
     * เปิดใช้งานสถานะแช่แข็ง (Freeze)
     * durationMs ระยะเวลาในการแช่แข็ง (มิลลิวินาที)
     */
    public void freeze(long durationMs) {
        this.frozen = true;
        this.freezeEndTime = System.currentTimeMillis() + durationMs;
    }

    /**
     * ปิดใช้งานสถานะแช่แข็ง
     */
    public void unfreeze() {
        this.frozen = false;
        this.freezeEndTime = 0;
    }

    /**
     * ตรวจสอบว่าถูกแช่แข็งอยู่หรือไม่
     */
    public boolean isFrozen() {
        if (frozen && System.currentTimeMillis() > freezeEndTime) {
            unfreeze();
        }
        return frozen;
    }

    /**
     * ดึงเวลาที่เหลือในการแช่แข็ง (มิลลิวินาที)
     */
    public long getFreezeRemainingTime() {
        if (!isFrozen()) return 0;
        long remaining = freezeEndTime - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    /**
     * ย้ายตำแหน่งหัวงูไปยังพิกัดใหม่ (เช่น การวาปเมื่อชนขอบจอแล้วมีเกราะป้องกัน)
     */
    public void teleportHead(int newX, int newY) {
        if (!xPositions.isEmpty()) {
            xPositions.set(0, newX);
        }
        if (!yPositions.isEmpty()) {
            yPositions.set(0, newY);
        }
        setPosition(newX, newY);
    }

    /**
     * ใช้เอฟเฟกต์เสริมพลัง (ตัวสั้นลง)
     * implements from PowerBoostable interface
     */
    @Override
    public void applyPowerBoostEffect() {
        if (powerBoostActive && getLength() > Constants.SNAKE_INITIAL_LENGTH) {
            shrink();
            setLastShrinkTime(System.currentTimeMillis()); // รีเซ็ตตัวจับเวลาเพื่อให้หดทุก 500ms
            if (getLength() <= Constants.SNAKE_INITIAL_LENGTH) {
                deactivatePowerBoost();
            }
        }
    }

    /**
     * Grows the snake by one segment and updates the combo chain.
     * Combo increments if the previous eat was within COMBO_WINDOW_MS; otherwise resets to 1.
     */
    public void grow() {
        long now = System.currentTimeMillis();
        if (lastEatTime > 0 && now - lastEatTime <= Constants.COMBO_WINDOW_MS) {
            comboCount = Math.min(comboCount + 1, 5); // cap at 5x
        } else {
            comboCount = 1;
        }
        lastEatTime = now;
        xPositions.add(xPositions.get(xPositions.size() - 1));
        yPositions.add(yPositions.get(yPositions.size() - 1));
        foodEaten++;
    }

    /** Adds bonus points (called by GameController after a combo eat). */
    public void addBonusScore(int bonus) {
        this.foodEaten += bonus;
    }

    /** Returns the current combo multiplier (1 = no combo, 2+ = active combo). */
    public int getComboCount() {
        return comboCount;
    }

    /**
     * ทำให้งูสั้นลง (เมื่อเสริมพลัง)
     */
    private void shrink() {
        if (xPositions.size() > Constants.SNAKE_INITIAL_LENGTH) {
            xPositions.remove(xPositions.size() - 1);
            yPositions.remove(yPositions.size() - 1);
        }
    }

    /**
     * Returns true if this snake may fire a bullet right now.
     * Requirements: length after firing stays ≥ SNAKE_INITIAL_LENGTH AND cooldown has elapsed.
     */
    public boolean canShoot() {
        boolean hasEnoughLength = getLength() - Constants.BULLET_LENGTH_COST >= Constants.SNAKE_INITIAL_LENGTH;
        boolean isCooledDown = System.currentTimeMillis() - lastShootTime >= Constants.BULLET_COOLDOWN_MS;
        return hasEnoughLength && isCooledDown;
    }

    /**
     * Fires a bullet: shrinks the snake by BULLET_LENGTH_COST and records the shoot timestamp.
     * @return true if the shot succeeded (canShoot() was true)
     */
    public boolean shoot() {
        if (!canShoot()) {
            return false;
        }
        for (int i = 0; i < Constants.BULLET_LENGTH_COST; i++) {
            shrink();
        }
        lastShootTime = System.currentTimeMillis();
        return true;
    }

    /**
     * Reduces score by {@code amount}. Score can go negative (penalised by bullets).
     */
    public void decreaseScore(int amount) {
        this.foodEaten -= amount;
    }

    /**
     * Shrinks the snake by one segment, but never below SNAKE_INITIAL_LENGTH.
     * Called when the snake is hit by an opponent's bullet.
     * @return true if shrink succeeded; false if already at minimum length
     */
    public boolean shrinkSafely() {
        if (getLength() > Constants.SNAKE_INITIAL_LENGTH) {
            shrink();
            return true;
        }
        return false;
    }

    /**
     * ดึงเวลาที่เหลือในการคูลดาวน์ยิงกระสุน (มิลลิวินาที)
     */
    public long getShootCooldownRemaining() {
        long elapsed = System.currentTimeMillis() - lastShootTime;
        return Math.max(0, Constants.BULLET_COOLDOWN_MS - elapsed);
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
     * Draws the snake with rounded segments, a body gradient, and an eye on the head.
     * Ghost mode = 45% opacity. Frozen segments get an ice-blue tint overlay.
     * Shield = animated pulsing golden ring around the head.
     */
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Composite originalComposite = g2d.getComposite();
        boolean isGhost = isGhostModeActive();
        boolean isFrozen = isFrozen();
        int len = getLength();
        int arc = 8;   // rounded corner radius
        int pad = 2;   // gap between segments

        if (isGhost) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.45f));
        }

        for (int i = 0; i < len; i++) {
            int sx = xPositions.get(i) + pad;
            int sy = yPositions.get(i) + pad;
            int sw = Constants.UNIT_SIZE - pad * 2;
            int sh = Constants.UNIT_SIZE - pad * 2;

            if (i == 0) {
                // Head: use boost color or normal head color
                g2d.setColor(powerBoostActive ? boostHeadColor : headColor);
            } else {
                // Body: gradient — darkens slightly toward the tail
                g2d.setColor(darken(bodyColor, 1.0f - (float) i / len * 0.5f));
            }
            g2d.fillRoundRect(sx, sy, sw, sh, arc, arc);

            // Ice-blue tint overlay when frozen
            if (isFrozen) {
                g2d.setColor(new Color(135, 206, 250, 110));
                g2d.fillRoundRect(sx, sy, sw, sh, arc, arc);
            }
        }

        // Restore alpha before drawing head decorations
        g2d.setComposite(originalComposite);

        // Eye on the head (direction-aware)
        if (len > 0) {
            drawEye(g2d);
        }

        // Animated pulsing shield ring around the head
        if (shieldActive && len > 0) {
            float alpha = 0.55f + 0.45f * (float) Math.sin(System.currentTimeMillis() / 180.0);
            Stroke oldStroke = g2d.getStroke();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            // Inner ring
            g2d.setColor(Constants.SHIELD_FOOD_COLOR);
            g2d.setStroke(new BasicStroke(2.5f));
            g2d.drawOval(xPositions.get(0) - 5, yPositions.get(0) - 5,
                    Constants.UNIT_SIZE + 10, Constants.UNIT_SIZE + 10);
            // Outer faint ring
            g2d.setColor(new Color(255, 215, 0, 90));
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawOval(xPositions.get(0) - 9, yPositions.get(0) - 9,
                    Constants.UNIT_SIZE + 18, Constants.UNIT_SIZE + 18);
            g2d.setStroke(oldStroke);
            g2d.setComposite(originalComposite);
        }
    }

    /** Darkens a color by {@code factor} (1.0 = original, 0.5 = half brightness). */
    private static Color darken(Color c, float factor) {
        return new Color(
                Math.max(0, Math.min(255, (int)(c.getRed()   * factor))),
                Math.max(0, Math.min(255, (int)(c.getGreen() * factor))),
                Math.max(0, Math.min(255, (int)(c.getBlue()  * factor)))
        );
    }

    /** Draws a small eye on the head segment, positioned toward the current direction. */
    private void drawEye(Graphics2D g2d) {
        int hx = xPositions.get(0);
        int hy = yPositions.get(0);
        int S = Constants.UNIT_SIZE;
        // Eye position: at the "front" quadrant of the head
        int ex, ey;
        switch (direction) {
            case RIGHT: ex = hx + S - 8; ey = hy + 5;  break;
            case LEFT:  ex = hx + 3;     ey = hy + 5;  break;
            case UP:    ex = hx + 5;     ey = hy + 3;  break;
            case DOWN:  ex = hx + 5;     ey = hy + S - 8; break;
            default:    ex = hx + S - 8; ey = hy + 5;  break;
        }
        g2d.setColor(Color.WHITE);
        g2d.fillOval(ex, ey, 6, 6);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(ex + 1, ey + 1, 4, 4);
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