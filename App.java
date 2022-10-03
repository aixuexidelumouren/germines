package gremlins;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.data.JSONArray;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.*;


public class App extends PApplet {

    public static final int WIDTH = 720;
    public static final int HEIGHT = 720;
    public static final int SPRITESIZE = 20;
    public static final int BOTTOMBAR = 60;

    public static final int FPS = 60;
    public static final double interval = 0.0166666667; 

    public static final Random randomGenerator = new Random();
	public static float enemyCooldown;

    public String configPath;
    String layoutFilePath = null;
    public boolean spellCasting;
    public boolean level1pass;
    public boolean gameend;
    public boolean casting;
    
    public PImage brickwall;
    public PImage brickwall0;
    public PImage brickwall1;
    public PImage brickwall2;
    public PImage brickwall3;

    public PImage stonewall;
    public PImage gremlin;
    public PImage slime;
    public PImage fireball;
    public PImage Exit;
    public PImage web;
    public PImage heart;

    public ArrayList<Projectile> fireballs1 ;
    public ArrayList<Projectile> slimes;
    public ArrayList<Gremlin> gremlins;
    public ArrayList<Tiles> stoneWalls;
    public ArrayList<Tiles> brickWalls;
    public Wizard player;
    public Door door;
    public Web webs;
    public double timer = 0;
    public double lastspell = -3;
    public int iniTexttimer = FPS * 1;
    public int nextLevelTimer = FPS * 2;
    public float full = 0;
    public int lives;
    public int i = 0;
    JSONObject conf;
    public double d;

    public App() {      
        this.player = new Wizard();
        this.fireballs1 = new ArrayList<Projectile>();
        this.slimes = new ArrayList<Projectile>();
        this.gremlins = new ArrayList<Gremlin>();
        this.stoneWalls = new ArrayList<Tiles>();
        this.brickWalls = new ArrayList<Tiles>();
        this.door = new Door();
        this.webs = new Web();
        this.configPath = "config.json";
        level1pass = false;
        gameend = false;
        conf = loadJSONObject(new File(this.configPath));
        lives = conf.getInt("lives");
        this.player.setLive(lives);
    }

    /**
     * Initialise the setting of the window size.
    */
    public void settings() {size(WIDTH, HEIGHT);}

    /**
     * Load all resources such as images. Initialise the elements such as the player, enemies and map elements.
    */
    public void setup() {
        frameRate(FPS);
        // Load images during setup
        this.heart = loadImage(this.getClass().getResource("heart.png").getPath().replace("%20", " "));
        this.web = loadImage(this.getClass().getResource("web.png").getPath().replace("%20", " "));
        this.fireball = loadImage(this.getClass().getResource("fireball.png").getPath().replace("%20", " "));
        this.player.setSprite(this.loadImage(this.getClass().getResource("wizard1.png").getPath().replace("%20", " ")));
        this.stonewall = loadImage(this.getClass().getResource("stonewall.png").getPath().replace("%20", " "));
        this.brickwall = loadImage(this.getClass().getResource("brickwall.png").getPath().replace("%20", " "));
        this.brickwall0 = loadImage(this.getClass().getResource("brickwall_destroyed0.png").getPath().replace("%20", " "));
        this.brickwall1 = loadImage(this.getClass().getResource("brickwall_destroyed1.png").getPath().replace("%20", " "));
        this.brickwall2 = loadImage(this.getClass().getResource("brickwall_destroyed2.png").getPath().replace("%20", " "));
        this.brickwall3 = loadImage(this.getClass().getResource("brickwall_destroyed3.png").getPath().replace("%20", " "));
        this.gremlin = loadImage(this.getClass().getResource("gremlin.png").getPath().replace("%20", " "));
        this.slime = loadImage(this.getClass().getResource("slime.png").getPath().replace("%20", " "));
        this.Exit = loadImage(this.getClass().getResource("door.png").getPath().replace("%20", " "));
        conf = loadJSONObject(new File(this.configPath));
        JSONArray levels = conf.getJSONArray("levels");
        for(int i =0;i<2;i++) {

        }
        JSONObject eachLevel = levels.getJSONObject(i);
        layoutFilePath = eachLevel.getString("layout");
        this.player.setCooldown(eachLevel.getFloat("wizard_cooldown"));
        d = (80/this.player.getCooldown())/FPS;
        enemyCooldown = eachLevel.getFloat("enemy_cooldown");
        int mapRow = 0;
        try(Scanner map = new Scanner(new File(layoutFilePath))){
            while(map.hasNextLine()){
                String mapConfig = map.nextLine();
                int mapCol = 0;
                for(char c : mapConfig.toCharArray()){
                    if(c == 'X') {
                        Tiles stoneWall = new StoneWall(mapCol, mapRow);
                        stoneWalls.add(stoneWall);
                        
                    }else if(c == 'B') {
                        Tiles brickWall = new BrickWall(mapCol, mapRow);
                        brickWalls.add(brickWall);
                    }
                    else if(c == 'G') {
                        Gremlin gremlin = new Gremlin(mapCol,mapRow);
                        gremlins.add(gremlin);
                        gremlin.setD(randomGenerator.nextInt(4));
                    }
                    else if(c == 'W') {
                        this.player.setX(mapCol);
                        this.player.setY(mapRow);
                    }
                    else if(c == 'E'){
                        this.door.setX(mapCol);
                        this.door.setY(mapRow);
                    }
                    else if(c == 'O') {
                        this.webs.setX(mapCol);
                        this.webs.setY(mapRow);
                    }
                    mapCol += 20;
                }
                mapRow += 20; 
            } 
        }catch(FileNotFoundException e){
            throw new RuntimeException( "map is not configured");
        } 
    }
    /**
     * Receive key pressed signal from the keyboard.
    */
    public void keyPressed() {
        // Left: 37
        // Up: 38
        // Right: 39
        // Down: 40
        if (this.keyCode == 37) {
            this.player.pressLeft();
            this.player.setSprite(this.loadImage(this.getClass().getResource("wizard0.png").getPath().replace("%20", " ")));
        } else if (this.keyCode == 39) {
            this.player.pressRight();
            this.player.setSprite(this.loadImage(this.getClass().getResource("wizard1.png").getPath().replace("%20", " ")));
        } else if (this.keyCode == 38) {
            this.player.pressUp();
            this.player.setSprite(this.loadImage(this.getClass().getResource("wizard2.png").getPath().replace("%20", " ")));
        } else if (this.keyCode == 40) {
            this.player.pressDown();
            this.player.setSprite(this.loadImage(this.getClass().getResource("wizard3.png").getPath().replace("%20", " ")));
        }
        if(this.keyCode == 32) {
            double gap = timer - lastspell;
            if(gap >= this.player.getCooldown()){
                casting = true;
                lastspell = timer;
                Projectile fireball = new Projectile();
                fireballs1.add(fireball);
                fireball.setX(this.player.getX());
                fireball.setY(this.player.getY());
                fireball.setD(this.player.getDirec());
            }
        }
        if(gameend && keyPressed) {
            i = 0;
            fireballs1.clear();
            slimes.clear();
            gremlins.clear();
            stoneWalls.clear();
            brickWalls.clear();
            level1pass = false;
            gameend = false;
            this.player.setLive(lives);
            nextLevelTimer = FPS * 2;
            iniTexttimer = FPS * 3;
            setup();
            loop();
        }
    }
    
    /**
     * Receive key released signal from the keyboard.
    */
   public void keyReleased(){
    if (this.keyCode == 37) {
        this.player.moveLeft = false;
    } 
    if (this.keyCode == 39) {
        this.player.moveRight = false;
    } 
    if (this.keyCode == 38) { 
        this.player.moveUp = false;
    } 
    if (this.keyCode == 40) {
        this.player.moveDown = false;
   }
}


    /**
     * Draw all elements in the game by current frame. 
	 */

    public void draw() {
        background(241,147,156);
        if (iniTexttimer > 0) {
            fill(28,103,88);
            this.rect(0,0,720,720);
            fill(214,205,164);
            textSize(80);
            this.text("GREMLINS", 200, 360);
            textSize(20);
            this.text("Luzy's first game",400,600);
            iniTexttimer -= 1;
            return;
        }
        if(this.player.getLive() == 0) {
            fill(28,103,88);
            this.rect(0,0,720,720);
            textSize(80);
            fill(214,205,164);
            this.text("Game over", 190, 360);
            gameend = true;
            fireballs1.clear();
            slimes.clear();
            gremlins.clear();
            stoneWalls.clear();
            brickWalls.clear();
            noLoop();
            return;
        }
        if( this.player.getX() == this.door.getX() && this.player.getY()==this.door.getY()) {
            if(i == 0) {
                i = 1;
                fireballs1.clear();
                slimes.clear();
                gremlins.clear();
                stoneWalls.clear();
                brickWalls.clear();
                level1pass = true;
                setup();
                return;
            }
            if(i == 1) {
                fill(28,103,88);
                this.rect(0,0,720,720);
                textSize(80);
                fill(214,205,164);
                this.text("You win", 230, 360);
                gameend = true;
                casting = false;
                fireballs1.clear();
                slimes.clear();
                gremlins.clear();
                stoneWalls.clear();
                brickWalls.clear();
                noLoop();
                return;
            }
        }
        if(level1pass && nextLevelTimer>0) {
            fill(28,103,88);
            this.rect(0,0,720,720);
            textSize(80);
            fill(214,205,164);
            this.text("level2", 250, 360);
            nextLevelTimer --;
            return;
        }
        fill(71,181,255);
        textSize(20);
        int w = 0;
        for(int numL=0;numL<this.player.getLive();numL++){
            this.image(this.loadImage(this.getClass().getResource("wizard1.png").getPath().replace("%20", " ")),55+w, 680);
            w+=30;
        }
        this.text("Live :",5,700);
        this.text("Level"+ String.valueOf(i+1),200,700);
        stroke(0);
        fill(241,147,156);
        this.rect(630,690,80,5);
        fill(71,181,255);
        if(casting){
            fill(71,181,255);
            this.rect(630,690,full,5);   
            full += d;
            if(full>=80){
                casting = false;
                full = 0;  
            }  

        }else{
            this.rect(630,690,80,5);
        }
        timer = timer + interval;
        textSize(10);
        fill(0);
        text(String.valueOf(timer),650,720);
        ArrayList<Projectile> toRomveFire = new ArrayList<>();
        ArrayList<Projectile> toRomveSlime = new ArrayList<>();
        ArrayList<Tiles> toRomveBrick = new ArrayList<>();
        ArrayList<Gremlin> toRomveGremlins = new ArrayList<>();
        ArrayList<Projectile> hitFire = new ArrayList<>();
        this.player.checkPosition(stoneWalls);
        this.player.checkPosition(brickWalls);
        this.player.tick();
        this.player.draw(this);
        this.player.isValid = true;
        this.image(this.Exit,this.door.getX(),this.door.getY(),20,20);
        this.image(this.web,this.webs.getX(),this.webs.getY(),20,20);

       
        
        for(Projectile f : fireballs1){
            f.tick(); 
            f.checkPosition(stoneWalls);
            f.checkPosition(brickWalls);
            f.checkHitGremlin(gremlins);
            if(!f.isValid){
                toRomveFire.add(f);
                continue;
            }
            if(f.hit){
                hitFire.add(f);
                continue;
            }
            this.image(this.fireball,f.getX(),f.getY()); 
        }
        fireballs1.removeAll(toRomveFire);

        for(Gremlin g : gremlins){
            g.checkPosition(stoneWalls);
            g.checkPosition(brickWalls);
            g.checkPosition0(fireballs1);
            g.checkHitWizard(this.player);
            int gx = g.getX();
            int gy = g.getY();
            g.tick();
            if(g.isValid && g.isAlive){this.image(this.gremlin,g.getX(),g.getY()); }
            else{
                if(!g.isValid){
                    this.image(this.gremlin,gx,gy);
                    g.setD(randomGenerator.nextInt(4));
                    g.isValid = true;                    
                }
                if(!g.isAlive){
                    toRomveGremlins.add(g);
                }
            }
            for(Projectile s : g.slimes) {
                s.tick();
                s.checkPosition(stoneWalls);
                s.checkPosition(brickWalls);
                s.checkHitWizard(this.player);
                if(!s.isValid) {
                    toRomveSlime.add(s);
                    continue;
                }
                if(s.hit) {
                    toRomveSlime.add(s);
                    fireballs1.clear();
                    slimes.clear();
                    gremlins.clear();
                    stoneWalls.clear();
                    brickWalls.clear();
                    setup();
                    this.player.dead();    
                    return;
                }
                this.image(this.slime,s.getX(),s.getY()); 
            }
            g.slimes.removeAll(toRomveSlime);
            if(g.hit) {
                fireballs1.clear();
                slimes.clear();
                gremlins.clear();
                stoneWalls.clear();
                brickWalls.clear();
                setup();
                this.player.dead();
                return;
            }
        }
        gremlins.removeAll(toRomveGremlins);
        fireballs1.removeAll(hitFire);
        


        for(Tiles s: stoneWalls) {
            this.image(this.stonewall, s.getX(), s.getY());
        }

        for(Tiles b: brickWalls) {
            b.checkPosition(toRomveFire);
            b.tick();
            if(b.isValid){
                this.image(this.brickwall, b.getX(), b.getY());
            }else{
                if(b.state == 0){
                    this.image(this.brickwall0, b.getX(), b.getY());
                }
                if(b.state == 1){
                    this.image(this.brickwall1, b.getX(), b.getY());
                }
                if(b.state == 2){
                    this.image(this.brickwall2, b.getX(), b.getY());
                }
                if(b.state == 3){
                    this.image(this.brickwall3, b.getX(), b.getY());
                    toRomveBrick.add(b);
                }
                
            }
            
        }
        brickWalls.removeAll(toRomveBrick);
        for(Gremlin g: toRomveGremlins) {
            Gremlin newg = g.respawn();
            newg.checkPosition(stoneWalls);
            newg.checkPosition(brickWalls);
            if(newg.isValid){
                gremlins.add(newg);
            }else{
                newg.setX(webs.getX());
                newg.setY(webs.getY());
                gremlins.add(newg);
            }
        }
    }



    public static void main(String[] args) {
        PApplet.main("gremlins.App");
    }
}
