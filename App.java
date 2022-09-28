/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package shapes;

// Import PApplet from the processing package
import processing.core.PApplet;

/**
 * Handles the Window of the program.
 */
public class App extends PApplet {

    /**
     * The width of the window.
     */
    public static final int WIDTH = 580;

    /**
     * The height of the window.
     */
    public static final int HEIGHT = 480;

    /**
     * The number of frames per second.
     */
    public static final int FPS = 60;

    /**
     * The square object that is at the top of the screen.
     */
    public Square square;

    /**
     * The circle object that is at the bottom of the screen.
     */
    public Circle circle;

    /**
     * Creates a new App object with a circle and a square.
     */
    public App() {
        this.square = new Square();
        this.circle = new Circle();
    }

    /**
     * Sets up parameters for the project, namely its width and height.
     */
    public void settings() {
        size(WIDTH, HEIGHT);
    }
    // size used to override the setting method, it must use size in the first line in settings

    /**
     * Sets up the program, setting the frame rate.
     * 
     * Loads in all images.
     */
    public void setup() {
        frameRate(FPS);

        // Use PApplet loadImage() to load PImage sprites
        // This must happen within setup()
        this.square.setSprite(this.loadImage("src/main/resources/square.png"));
        this.circle.setSprite(this.loadImage("src/main/resources/circle.png"));
    }

    /**
     * Draws and updates the program.
     */
    public void draw() {
        background(143, 75 , 40); // background RBG set the color  for the Processing widnow

        // First update all the game objects
        this.square.tick();
        this.circle.tick();

        // Then draw all the game objects
        this.square.draw(this);
        this.circle.draw(this);
    }

    /**
     * Called every frame if a key is down.
     * 
     * You can access the key with the keyCode variable.
     */
    public void keyPressed() {
        // Left: 37
        // Up: 38
        // Right: 39
        // Down: 40
        if (this.keyCode == 37) {
            this.square.pressLeft();
        } else if (this.keyCode == 39) {
            this.square.pressRight();
        }
    }

    /**
     * Runs the program.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        PApplet.main("shapes.App");
    }
}
