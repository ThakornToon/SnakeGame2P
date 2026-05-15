package SnakeGame2P.model.entity;

import SnakeGame2P.model.behavior.Collidable;
import java.awt.*;

/**
 * คลาสแม่ (Abstract Class) สำหรับวัตถุทั้งหมดในเกม
 * ใช้หลักการ Inheritance เพื่อให้คลาสลูกสืบทอดคุณสมบัติร่วม
 * สามารถชนกันได้
 */
public abstract class GameObject implements Collidable {
    /** ตำแหน่งของวัตถุ */
    protected Point position;

    /**
     * Constructor
     * x ตำแหน่ง X เริ่มต้น
     * y ตำแหน่ง Y เริ่มต้น
     */
    public GameObject(int x, int y) {
        this.position = new Point(x, y);
    }

    /**
     * ดึงตำแหน่ง X
     * return ตำแหน่ง X
     */
    public int getX() {
        return position.x;
    }

    /**
     * ดึงตำแหน่ง Y
     * return ตำแหน่ง Y
     */
    public int getY() {
        return position.y;
    }

    /**
     * ดึงตำแหน่งเป็น Point
     * return Point (x, y)
     */
    public Point getPosition() {
        return new Point(position);
    }

    /**
     * ตั้งค่าตำแหน่งใหม่
     * x ตำแหน่ง X ใหม่
     * y ตำแหน่ง Y ใหม่
     */
    public void setPosition(int x, int y) {
        this.position.setLocation(x, y);
    }

    /**
     * ตรวจสอบว่าตำแหน่งนี้ชนกับวัตถุอื่นหรือไม่
     * implements จาก Collidable interface
     * other วัตถุอื่นที่ต้องการตรวจสอบ
     * return true ถ้าชนกัน
     */
    @Override
    public boolean isCollidingWith(Collidable other) {
        return this.position.equals(other.getPosition());
    }

    /**
     * เมธอดนามธรรมสำหรับวาดวัตถุ
     * บังคับให้คลาสลูกต้อง implement
     * g Graphics object
     */
    public abstract void draw(Graphics g);
}