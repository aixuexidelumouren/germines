package shapes;

import processing.core.PImage;

/**
 * Represents a square.
 */
public class Square extends Shape {

    /**
     * Whether to move left.
     */
    private boolean moveLeft;
    private boolean moveRight;

    /**
     * Creates a new square with coordinates (20, 20)
     */
    public Square() {
        super(0, 0);
        this.moveLeft = false;
        this.moveRight = false;
    }

    /**
     * Updates the square every frame.
     */
    public void tick() {
        if(moveLeft) {
//            this.moveRight = false;
            this.x -= 25;
            this.moveLeft = false;
        }else if(moveRight) {
//            this.moveLeft = false;
            this.x+=25;
            this.moveRight = false;
        }else{
            this.moveLeft = false;
            this.moveRight = false;

        }
        // If moveLeft is true, move left by decrementing x
//        if (moveLeft) {
//            this.x--;
//        } else if(moveRight){
//            // Move right by incrementing x
//            this.x++;
//        }
    }

    /**
     * Called in App when the left key is pressed.
     */
    public void pressLeft() {

        this.moveLeft = true;
        this.moveRight = false;
    }

    /**
     * Called in App when the right key is pressed.
     */
    public void pressRight() {

        this.moveLeft = false;
        this.moveRight = true;
    }
}
