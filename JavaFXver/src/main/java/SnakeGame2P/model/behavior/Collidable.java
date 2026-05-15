package SnakeGame2P.model.behavior;

import javafx.geometry.Point2D;

/**
 * Interface สำหรับวัตถุที่สามารถชนกันได้.
 * @author Unnawas
 */
public interface Collidable {
    /**
     * Polymorphism through Inheritance.
     * Snake implement this with special head collision case.
     * GameObject implement this by making it return whether it occupies the same position as other
     * @param other the other game object
     */
    boolean isCollidingWith(Collidable other);

    /**
     * return a game object's position
     */
    Point2D getPosition();
}