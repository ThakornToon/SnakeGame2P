package SnakeGame2P.model.entity;

import SnakeGame2P.model.behavior.Collidable;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

/**
 * Abstract base class for all game objects (JavaFX version)
 * It is not supposed to be present in real game which is why we choose abstract class!
 * @author Unnawas
 */
public abstract class GameObject implements Collidable {

    /** The object's position */
    protected Point2D position;

    /**
     * Constructor: set position to parameters
     * @param x the abscissa
     * @param y the ordinate
     */
    public GameObject(int x, int y) {
        setPosition(x, y);
    }

    /**
     * Getter for abscissa
     */
    public int getX() {
        return (int) position.getX();
    }

    /**
     * Getter for ordinate
     */
    public int getY() {
        return (int) position.getY();
    }

    /**
     * Getter for the object's position
     */
    @Override
    public Point2D getPosition() {
        return position;
    }

    /**
     * Setter for the object's position
     */
    public void setPosition(int x, int y) {
        this.position = new Point2D(x, y); // Point2D เป็น immutable
    }

    /**
     * Check whether the object is colliding with other
     * @param other an object that is capable of collision
     * @return whether this and other are colliding
     */
    @Override
    public boolean isCollidingWith(Collidable other) {
        return this.position.equals(other.getPosition());
    }

    /**
     * draw
     */
    public abstract void draw(GraphicsContext gc);
}