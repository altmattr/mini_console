package studentwork;

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.sound.*; 
import controlP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Tetris extends mqapp.MQApp {

public String name(){return "Tetris";}
public String author(){return "By Nataly Falero, Andrew, Alyssa Fedele, et.al.";}
public String description(){return "Just like you remember";}

int PREF_WIDTH = 500;
int PREF_HEIGHT = 800;
float scaleFactor;

//Shape colours
int alpha = 150;
int black = color(0);
int blue = color(128, 206, 255, alpha);
int red = color(255, 105, 97, alpha);
int orange = color(255, 179, 71, alpha);
int green = color(207, 240, 204, alpha);
int pink = color(242, 184, 216, alpha);
int purple = color(177, 156, 217, alpha);
int yellow = color(253, 253, 150, alpha);
int[] shapeColors = {black, blue, red, orange, green, pink, purple, yellow};

//Shape and reference position
PVector startingPos = new PVector(4, 0);
PVector currentPos = new PVector(startingPos.x, startingPos.y);
PVector[] activeShape = new PVector[4];

//Global variables
int activeShapeRef = 1;
int currentDirection = 0;
int count = 0;
int scrheight;
int score = 0;
int linesCleared = 0;
int howManyLines = 0;
int volume = 10;
int difficulty = 1;
int boomCount;
int grid = 50;
int speed = 60;

boolean gameRun = false;
boolean superSpeed = true;
boolean youDied = true;
boolean changeCol = false;

//Gameboard
int[][] isFilled; // [x][y] [cols][rows]

// Sound

SoundFile Boom;
SoundFile tetrisMusic;

//Slider

ControlP5 volControl;
ControlP5 diffControl;

//Image
PImage logo;
PImage boom;


public void setup() {
  
  size(500*(displayHeight/800), displayHeight);
  
  //Initialize gameboard
  scrheight = height/50;
  isFilled = new int[10][scrheight + 4];
  
  //Pick new shape
  activeShapeRef = (int)random(1, 8);
  
  //Fill gameboard with black tiles
  for (int i = 0; i < isFilled.length; i++) {
    for (int j =0; j < isFilled[i].length; j++) {
      isFilled[i][j] = black;
    }
  }
  
  //Draw shape
  strokeWeight(5);
  for (int i = 0; i < activeShape.length; i++) {
    activeShape[i] = new PVector(0, 0);
  }
  //Sound
  Boom = new SoundFile(this, "tetris/Boom.wav");
  tetrisMusic = new SoundFile(this, "tetris/tetrisMusic.wav");
  tetrisMusic.loop();
  
  //Slider
  volControl = new ControlP5(this);
  diffControl = new ControlP5(this);
  diffControl.addSlider("difficulty").setPosition(width/14, height/40).setRange(1, 5);
  volControl.addSlider("volume").setPosition(2*width/3, (height/40)).setRange(0, 100);

  //Image
  logo = loadImage("tetris/logo.png");
  boom = loadImage("tetris/boomstart.png");
  
}

public void draw() {
  
  //Game volume
  tetrisMusic.amp((float)volume/500);
  
  //Do this if game is active/alive
  if (gameRun) {
    background(0);
    
    //Moves the gamescreen so that the top two rows are hidden off screen
    translate(0, -100);
    
    //Draw gameboard with placed pieces
    for (int i = 0; i < isFilled.length; i++) {
      for (int j =0; j < isFilled[i].length; j++) {
        stroke(0);
        fill(isFilled[i][j]);
        rect(i*grid, j*grid, grid, grid, 7);
      }
    }
    
    //Change colour for new shape
    if (changeCol == true) {
      activeShapeRef = (int)random(1, 8);
      changeCol = false;
    }
    
    //Draw shape
    for (int i = 0; i < activeShape.length; i++) {
      fill(255);
      rect(activeShape[i].x * grid, (activeShape[i].y) * grid, grid, grid, 7);
    }
    for (int i = 0; i < activeShape.length; i++) {
      fill(shapeColors[activeShapeRef]);
      rect(activeShape[i].x * grid, (activeShape[i].y) * grid, grid, grid, 7);
    }
    assignShape ();
    
    //Game logic
    moveShape();
    softDrop();
    BOOMTetrisForJeff();
    checkTopRow();
    score();
    displayScore();
    
    //BOOM happens here!
    if (frameCount  > 30) {
      if (frameCount < boomCount + 25) {
        boom.resize(250, 156);
        image(boom, random(125, width - 125), random(200, height));
      }
    }
  } else {
    
    //Display the start screen
    startScreen();
  }
}
public void BOOMTetrisForJeff () {
  for (int i = 0; i < isFilled[0].length; i++) {
    //Check if each row is full
    if (rowCheck(i)) {
      //If so clear that row
      clearRow(i);
    }
  }
}

public boolean rowCheck(int index) {
  for (int i = 0; i < isFilled.length; i++) {
    if (isFilled[i][index] == shapeColors[0]) {
      return false;
    }
  }
  return true;
}

public void clearRow(int index) {
  for (int j = index; j >= 1; j--) {
    for (int i = 0; i < isFilled.length; i++) {
      isFilled[i][j] = isFilled[i][j-1];
    }
  }
  //Increments Lines and Score when a row is cleared
  howManyLines++;
  linesCleared++;
}

public void score() {
  if (howManyLines == 1) {
    score += difficulty*40;
  }
  if (howManyLines == 2) {
    score += difficulty*100;
  }
  if (howManyLines == 3) {
    score += difficulty*300;
  }
  if (howManyLines == 4) {
    score += difficulty*1200;
    //Sound
    Boom.play();
    //Boom Animation Delay Logic
    boomCount = frameCount;
  }
  howManyLines = 0;
}

//Draws the score and lines cleared in top left
public void displayScore() {
  fill(255);
  textAlign(LEFT);
  textSize(15);
  text("Lines: "+ linesCleared, width/50, 125);
  text("Score: " + score, width/50, 140);
}
public void moveShape() {
  if (frameCount % speed == 0 && !youDied) {
    //If the piece isn't colliding with anything
    if (!isCollision()) {
      //Move piece down one tile
      currentPos.y++;
    } else {
      //Colour that location on the gameboard with the active shapes colour
      for (int i = 0; i < activeShape.length; i++) {
        isFilled[(int)activeShape[i].x][(int)activeShape[i].y] = shapeColors[activeShapeRef];

        //Move the reference position to it's intial value at the top and reset direction
        currentDirection = 0;
        currentPos.y = startingPos.y;
        currentPos.x = startingPos.x;
        changeCol = true;
        //Soft drop logic to stop soft drop upon collision
        if (keyPressed && (key == 's' || key == 'S' || keyCode == DOWN)) {
          superSpeed = false;
          speed = (int)60/difficulty;
        }
      }
    }
  }
}

//Checks for collisions
public boolean isCollision() {
  for (int i = 0; i < activeShape.length; i++) {    
    if ((int)activeShape[i].y - 1 >=scrheight || isFilled[(int)activeShape[i].x][(int)activeShape[i].y + 1] != shapeColors[0]) {
      return true;
    }
  }
  return false;
}

//Checks if it's safe to rotate a piece
public boolean rotateCheck() {
  //Increments Direction
  if (currentDirection < 3) {
    currentDirection++;
  } else {
    currentDirection = 0;
  }
  //Creates a ghost shape for testing
  assignShape ();
  PVector[] checkShape = new PVector[4];
  for (int i = 0; i < activeShape.length; i++) {
    int xPos = (int)activeShape[i].x;
    int yPos = (int)activeShape[i].y;
    checkShape[i] = new PVector(xPos, yPos);
  }

  //Puts the direction of the active shape back as it was
  if (currentDirection > 0) {
    currentDirection--;
  } else {
    currentDirection = 3;
  }
  assignShape ();

  //The check itself
  for (int i = 0; i < checkShape.length; i++) {
    if ((int)checkShape[i].x < 0 || (int)checkShape[i].x + 1 > 10 || (int)checkShape[i].y < 0 || (int)checkShape[i].y + 1 > scrheight + 2 || isFilled[(int)checkShape[i].x][(int)checkShape[i].y] != shapeColors[0]) {
      return false;
    }
  }
  return true;
}
//Switch to call the active shape
public void assignShape () {
  fill(shapeColors[activeShapeRef]);
  switch (activeShapeRef) {
  case 1:
    LSHAPE_B();
    break;
  case 2:
    LSHAPE_R();
    break;
  case 3:
    BOX_O();
    break;
  case 4:
    ZSHAPE_G();
    break;
  case 5:
    ZSHAPE_P();
    break;
  case 6:
    TSHAPE();
    break;
  case 7:
    LONG();
    break;
  default:
  }
}

//Positions of the four tiles of each shape
public void LONG() {
  if (currentDirection == 0 || currentDirection == 2) {
    activeShape[0].x = currentPos.x;
    activeShape[0].y = currentPos.y;
    activeShape[1].x = currentPos.x+1;
    activeShape[1].y = currentPos.y;
    activeShape[2].x = currentPos.x+2;
    activeShape[2].y = currentPos.y;
    activeShape[3].x = currentPos.x-1;
    activeShape[3].y = currentPos.y;
  } else if (currentDirection == 1 || currentDirection == 3) {
    activeShape[0].x = currentPos.x;
    activeShape[0].y = currentPos.y;
    activeShape[1].x = currentPos.x;
    activeShape[1].y = currentPos.y+1;
    activeShape[2].x = currentPos.x;
    activeShape[2].y = currentPos.y+2;
    activeShape[3].x = currentPos.x;
    activeShape[3].y = currentPos.y-1;
  }
}

public void LSHAPE_R() {
  if (currentDirection == 0) {
    activeShape[0].x = currentPos.x;
    activeShape[0].y = currentPos.y;
    activeShape[1].x = currentPos.x+1;
    activeShape[1].y = currentPos.y;
    activeShape[2].x = currentPos.x-1;
    activeShape[2].y = currentPos.y;
    activeShape[3].x = currentPos.x+1;
    activeShape[3].y = currentPos.y-1;
  } else if (currentDirection == 1) {
    activeShape[0].x = currentPos.x;
    activeShape[0].y = currentPos.y;
    activeShape[1].x = currentPos.x;
    activeShape[1].y = currentPos.y+1;
    activeShape[2].x = currentPos.x;
    activeShape[2].y = currentPos.y-1;
    activeShape[3].x = currentPos.x-1;
    activeShape[3].y = currentPos.y-1;
  } else if (currentDirection == 2) {
    activeShape[0].x = currentPos.x;
    activeShape[0].y = currentPos.y;
    activeShape[1].x = currentPos.x+1;
    activeShape[1].y = currentPos.y;
    activeShape[2].x = currentPos.x-1;
    activeShape[2].y = currentPos.y;
    activeShape[3].x = currentPos.x-1;
    activeShape[3].y = currentPos.y+1;
  } else if (currentDirection == 3) {
    activeShape[0].x = currentPos.x;
    activeShape[0].y = currentPos.y;
    activeShape[1].x = currentPos.x;
    activeShape[1].y = currentPos.y+1;
    activeShape[2].x = currentPos.x;
    activeShape[2].y = currentPos.y-1;
    activeShape[3].x = currentPos.x+1;
    activeShape[3].y = currentPos.y+1;
  }
}
public void LSHAPE_B() {
  if (currentDirection == 0) {
    activeShape[0].x = currentPos.x;
    activeShape[0].y = currentPos.y;
    activeShape[1].x = currentPos.x+1;
    activeShape[1].y = currentPos.y;
    activeShape[2].x = currentPos.x-1;
    activeShape[2].y = currentPos.y;
    activeShape[3].x = currentPos.x+1;
    activeShape[3].y = currentPos.y+1;
  } else if (currentDirection == 1) {
    activeShape[0].x = currentPos.x;
    activeShape[0].y = currentPos.y;
    activeShape[1].x = currentPos.x;
    activeShape[1].y = currentPos.y+1;
    activeShape[2].x = currentPos.x;
    activeShape[2].y = currentPos.y-1;
    activeShape[3].x = currentPos.x+1;
    activeShape[3].y = currentPos.y-1;
  } else if (currentDirection == 2) {
    activeShape[0].x = currentPos.x;
    activeShape[0].y = currentPos.y;
    activeShape[1].x = currentPos.x+1;
    activeShape[1].y = currentPos.y;
    activeShape[2].x = currentPos.x-1;
    activeShape[2].y = currentPos.y;
    activeShape[3].x = currentPos.x-1;
    activeShape[3].y = currentPos.y-1;
  } else if (currentDirection == 3) {
    activeShape[0].x = currentPos.x;
    activeShape[0].y = currentPos.y;
    activeShape[1].x = currentPos.x;
    activeShape[1].y = currentPos.y+1;
    activeShape[2].x = currentPos.x;
    activeShape[2].y = currentPos.y-1;
    activeShape[3].x = currentPos.x-1;
    activeShape[3].y = currentPos.y+1;
  }
}

public void TSHAPE() {
  if (currentDirection == 0) {
    activeShape[0].x = currentPos.x;
    activeShape[0].y = currentPos.y;
    activeShape[1].x = currentPos.x+1;
    activeShape[1].y = currentPos.y;
    activeShape[2].x = currentPos.x-1;
    activeShape[2].y = currentPos.y;
    activeShape[3].x = currentPos.x;
    activeShape[3].y = currentPos.y-1;
  } else if (currentDirection == 1) {
    activeShape[0].x = currentPos.x;
    activeShape[0].y = currentPos.y;
    activeShape[1].x = currentPos.x;
    activeShape[1].y = currentPos.y+1;
    activeShape[2].x = currentPos.x;
    activeShape[2].y = currentPos.y-1;
    activeShape[3].x = currentPos.x-1;
    activeShape[3].y = currentPos.y;
  } else if (currentDirection == 2) {
    activeShape[0].x = currentPos.x;
    activeShape[0].y = currentPos.y;
    activeShape[1].x = currentPos.x+1;
    activeShape[1].y = currentPos.y;
    activeShape[2].x = currentPos.x-1;
    activeShape[2].y = currentPos.y;
    activeShape[3].x = currentPos.x;
    activeShape[3].y = currentPos.y+1;
  } else if (currentDirection == 3) {
    activeShape[0].x = currentPos.x;
    activeShape[0].y = currentPos.y;
    activeShape[1].x = currentPos.x;
    activeShape[1].y = currentPos.y+1;
    activeShape[2].x = currentPos.x;
    activeShape[2].y = currentPos.y-1;
    activeShape[3].x = currentPos.x+1;
    activeShape[3].y = currentPos.y;
  }
}

public void ZSHAPE_P() {
  if (currentDirection == 0 || currentDirection == 2) {
    activeShape[0].x = currentPos.x;
    activeShape[0].y = currentPos.y;
    activeShape[1].x = currentPos.x+1;
    activeShape[1].y = currentPos.y;
    activeShape[2].x = currentPos.x;
    activeShape[2].y = currentPos.y-1;
    activeShape[3].x = currentPos.x-1;
    activeShape[3].y = currentPos.y-1;
  } else if (currentDirection == 1 || currentDirection == 3) {
    activeShape[0].x = currentPos.x;
    activeShape[0].y = currentPos.y;
    activeShape[1].x = currentPos.x+1;
    activeShape[1].y = currentPos.y;
    activeShape[2].x = currentPos.x+1;
    activeShape[2].y = currentPos.y-1;
    activeShape[3].x = currentPos.x;
    activeShape[3].y = currentPos.y+1;
  }
}

public void ZSHAPE_G() {
  if (currentDirection == 0 || currentDirection == 2) {
    activeShape[0].x = currentPos.x;
    activeShape[0].y = currentPos.y;
    activeShape[1].x = currentPos.x;
    activeShape[1].y = currentPos.y-1;
    activeShape[2].x = currentPos.x+1;
    activeShape[2].y = currentPos.y-1;
    activeShape[3].x = currentPos.x-1;
    activeShape[3].y = currentPos.y;
  } else if (currentDirection == 1 || currentDirection == 3) {
    activeShape[0].x = currentPos.x;
    activeShape[0].y = currentPos.y;
    activeShape[1].x = currentPos.x+1;
    activeShape[1].y = currentPos.y;
    activeShape[2].x = currentPos.x+1;
    activeShape[2].y = currentPos.y+1;
    activeShape[3].x = currentPos.x;
    activeShape[3].y = currentPos.y-1;
  }
}

public void BOX_O() {
  if (currentDirection == 0 || currentDirection == 1 || currentDirection == 2 || currentDirection == 3) {
    activeShape[0].x = currentPos.x;
    activeShape[0].y = currentPos.y;
    activeShape[1].x = currentPos.x+1;
    activeShape[1].y = currentPos.y;
    activeShape[2].x = currentPos.x;
    activeShape[2].y = currentPos.y+1;
    activeShape[3].x = currentPos.x+1;
    activeShape[3].y = currentPos.y+1;
  }
}
public void checkTopRow() {
  for (int i = 0; i < isFilled.length; i++) {
    //If any position off the screen isn't black
    if (isFilled[i][0] != shapeColors[0] || isFilled[i][1] != shapeColors[0]) { //[i][2] is the top row visible to the player at the moment
      //YOU DIED!
      youDied = true;
      gameOver();
    }
  }
}

public void gameOver() {
  translate(0, 0);
  background(20);
  fill(255, 0, 0);
  textSize(50);
  textAlign(CENTER, CENTER);
  text("GAME OVER!", width / 2, height / 2 - height / 8);
  textSize(15);
  textAlign(CENTER, CENTER);
  text("Press Space to play again", width / 2, 2 * height / 3);
  text("Press Enter to reset", width / 2, height / 2 + height / 8);
  noLoop();
}

public void startScreen() {
  translate(0, 0);
  background(20);
  fill(0);
  rectMode(CENTER);
  textAlign(CENTER, CENTER);
  imageMode(CENTER);
  image(logo, width / 2, 0.9f * (height / 3 + 200));
  boom.resize(400, 250);
  image(boom, width / 2, 0.9f * (height / 3 - 70));
  fill(255);
  textSize(22);
  text("Press Space", width / 2, (height / 3) + 200);
  textSize(16);
  text("A or LEFT to move left", width / 4, (5 * height / 6) - 20);
  text("D or RIGHT to move right", width / 4, (5 * height / 6) + 20);
  text("S or DOWN to drop", 3 *width / 4, (5 * height / 6) - 20);
  text("W or UP to rotate", 3 * width / 4, (5 * height / 6) + 20);
  rectMode(CORNER);
}



public void restart() {
  //Re-initialising variables
  youDied = false;
  activeShapeRef = 1;
  currentDirection = 0;
  count = 0;
  currentPos.y = startingPos.y;
  currentPos.x = startingPos.x;
  howManyLines = 0;
  linesCleared = 0;
  score = 0;
  superSpeed = true;


  //Re-run setup
  activeShapeRef = (int)random(1, 8);
  for (int i = 0; i < isFilled.length; i++) {
    for (int j =0; j < isFilled[i].length; j++) {
      isFilled[i][j] = black;
    }
  }
  for (int i = 0; i < activeShape.length; i++) {
    activeShape[i] = new PVector(0, 0);
  }
}
public void keyPressed() {

  //Checking if there is a shape or screen edge to the sides
  boolean shapeToLeft = false;
  boolean shapeToRight = false;
  for (int i = 0; i < activeShape.length; i++) {
    try {
      if (isFilled[(int)(activeShape[i].x - 1)][(int)activeShape[i].y] != shapeColors[0])
        shapeToLeft = true;
    } 
    catch(Exception e) {
      shapeToLeft = true;
    }
    if (shapeToLeft) break;
  }
  for (int i = 0; i < activeShape.length; i++) {
    try {
      if (isFilled[(int)(activeShape[i].x + 1)][(int)activeShape[i].y] != shapeColors[0])
        shapeToRight = true;
    }
    catch(Exception e) {
      if (shapeToRight) break;
    }
  }
  float minX = min(min(activeShape[0].x, activeShape[1].x), min(activeShape[2].x, activeShape[3].x));
  float maxX = max(max(activeShape[0].x, activeShape[1].x), max(activeShape[2].x, activeShape[3].x));

  //Controls
  if ((keyCode == 'a' || keyCode == 'A' || keyCode == LEFT) && minX > 0 && !shapeToLeft) {
    // add another condition (&&) that checks if everything to the left is white
    currentPos.x -= 1;
  }
  if ((keyCode == 'd' || keyCode == 'D' || keyCode == RIGHT) && maxX < 9 && !shapeToRight) {
    // add another condition (&&) that checks if everything to the right is white
    currentPos.x += 1;
  }
  if ((keyCode == 'w' || keyCode == 'W' || keyCode == UP || (keyCode == ' ' && !youDied)) && rotateCheck()) {
    currentDirection++;
    currentDirection %= 4;
  }
  if ((keyCode == 'c' || keyCode == 'C') && !youDied) {
    activeShapeRef = 7;
  }
  if (youDied && keyCode==' ') {
    gameRun = true;
    restart();
    loop();
  }
  if (youDied && (keyCode == RETURN || keyCode == ENTER)) {
    gameRun = false;
    startScreen();
    loop();
  }
}

//Soft Drop
public void softDrop() {
  if (superSpeed == true) {
    if (keyPressed && (key == 's' || key == 'S' || keyCode == DOWN)) {
      speed = (int)5/difficulty;
    } else {
      speed = (int)60/difficulty;
    }
  }
}

public void keyReleased() {
  if (key == 's' || key == 'S' || keyCode == DOWN) {
    superSpeed = true;
  }
}
  public void settings() {  size(500, 800); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "mq_console_tetris" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
