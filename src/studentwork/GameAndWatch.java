package studentwork;

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.sound.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class GameAndWatch extends mqapp.MQApp {

    public String name(){return "Mr Game and Watch";}
    public String author(){return "Tanner Schineller";}
    public String description(){return "A classic reborn (but better)";}

    static public void main(String[] passedArgs) {
        runSketch(appletArgs, new GameAndWatch());
    }

public void setup() {
  readSettings();
  //size(100,100);
  //size(1024, 696);
  size(displayWidth, displayHeight);

  // This block imports all the sound files used
  hammerB = new SoundFile(this, soundDir +  "hammerBeep.wav");
  bucketB = new SoundFile(this, soundDir +  "bucketBeep.wav");
  plierB = new SoundFile(this, soundDir +  "plierBeep.wav");
  screwdriverB = new SoundFile(this, soundDir +  "screwdriverBeep.wav");
  spannerB = new SoundFile(this, soundDir +  "spannerBeep.wav");
  deathB = new SoundFile(this, soundDir +  "deathBeep.wav");
  doorB = new SoundFile(this, soundDir +  "doorBeep.wav");
  setVolume();

  loadAssets();

  highScores = loadTable("gw/highscores.csv", "header, csv");

  //surface.setResizable(true);

  //prepares the fonts
  retro = createFont(fontDir + "DS-DIGI.TTF", 72);
  arialbd = createFont(fontDir + "arialbd.ttf", 36);
  arial = createFont(fontDir + "arial.ttf", 36);

  // This section is here to ensure that the contained code is only run on the initial setup of the program, and not each time setup is called.
  if (firstSetup) {
    frameRate(30);
    alpha = 255;
    createPictographicMenuButtons();
    createSettingsButtons();
    createInGameButtons();
    createPauseMenuButtons();
    createConfirmationButtons();
    createHighScoreButtons();
    firstSetup = false;
  }

  // In order to inprve efficiency of switching between screens, when entering into the settings menu
  // the majority of the setup is ignored (which) includes a large amount of random number calculation
  if (z == 3) {
    createSettingsButtons();
  } else {  

    //Varible Initialization
    score = 0;
    position = 0;
    doorTimerStart = millis();
    for (int l = 0; l < lastToolUpdate.length; l++)
      lastToolUpdate[l] = millis();
    i = 1;
    misses = 0;
    pauseTime = 0;
    open = true;
    safe = true;
    alive = true;
    pressed = false;
    drawn = false;
    startup = false;
    paused = false;
    reset = false;
    tools = new boolean[5][6];
    rotation = random(-PI/8, PI/8);
    startRotation = rotation;

    // The timing of this program is based around the framerate of the game
    // under the assumption that on any modern computer it should run at the preset 30 frames a second
    // So each time setup is called the frameCount is set to 0 so as to allow the running of the startup sequence
    lastFrameTime = millis();




    doorTimer = PApplet.parseInt(random(3, 8));
    // DEBUGGING println(doorTimer);

    circleGap = 4;
    //number of cirlces that fit accross the screen
    numberOfCircles = 8;
    diameter = 512 / numberOfCircles - circleGap;
  }
}


public void draw() {
  //The following chunck will allow the entire program to be set to any size and maintain it's proper aspect ratio while also remaining centered on the window.
  float scaleFactor = min(width/512.0f, height/348.0f);
  
  if (width/512.0f > height/348.0f)
    translate((width - 512.0f * scaleFactor)/2, 0);
  else
    translate(0, (height - 348.0f * scaleFactor)/2);

  scale(scaleFactor);
  
  //End of the resizing code.
  
  

  //this switch stack handles the various drawing modes for the game and based off a global variable z the game will switch modes.
  switch (z) {
  case 0:
    mainMenu();
    break;
  case 1:
    fullMode();
    break;
  case 2:
    markingMode();
    break;
  case 3:
    settingsMenu();
    break;
  case 4:
    highScoresMenu();
    break;
  case 5:
    enterNewHighScore();
    break;
  case 6:
    selectResolution();
    break;
  }
}

public void mousePressed() {
  switch (z) {
  case 0 : 
    for (PictographicButton b : pictographicMenuButtons) {
      if (b.mouseOver()) {
        b.display(); 
        b.action();
      }
    }
    break; 
  case 1 : 
    if (startup) {
      if (exit) {
        for (TextButton b : confirmationButtons) {
          if (b.mouseOver()) {

            b.action();
          }
        }
      } else if (paused) {
        for (TextButton b : pauseMenuButtons) {
          if (b.mouseOver()) {

            b.action();
          }
        }
      } else {
        for (PictographicButton b : inGameButtons) {
          if (b.mouseOver()) {
            b.action();
          }
        }
      }
    }
    // DEBUGGING*/ println(mouseX +", " + mouseY);
    if (misses == 3 && startup && !highScore) {
      if (highScoreCheck() < 5 && highScoreCheck() >= 0) {
        z = 5;
      } else {
        z = 0; 
        setup();
      }
    }
    break; 
  case 2 : 
    if (startup) {
      if (exit) {
        for (TextButton b : confirmationButtons) {
          if (b.mouseOver()) {
            b.action();
          }
        }
      } else if (paused) {
        for (TextButton b : pauseMenuButtons) {
          if (b.mouseOver()) {
            b.action();
          }
        }
      } else {
        for (PictographicButton b : inGameButtons) {
          if (b.mouseOver()) {
            b.action();
          }
        }
      }
    }
    if ((misses * numberOfCircles) + score >= ((348/(diameter+circleGap)) * numberOfCircles) && startup) {
      z = 0; 
      setup();
    }
    break;
  case 3:
    for (TextButton b : settingsButts) {
      if (b.mouseOver()) {
        b.action();
      }
    }
    break;
  case 4:
    if (reset) {
      for (TextButton b : confirmationButtons) {
        if (b.mouseOver()) {
          b.action();
        }
      }
    }
    if (!reset) {
      for (TextButton b : highScoreButtons) {
        if (b.mouseOver()) {
          b.action();
        }
      }
    }
    break;
  case 6:
    for (TextButton b : resolutionButts) {
      if (b.mouseOver()) {
        b.action();
      }
    }
    break;
  }
}

//User Input
public void keyPressed() {
  switch (z) {
  case 0 : 
    //stuff
    break; 
  case 1 : 
    if (key == ' ' && startup) {
      if (paused) {
        paused = false;
        updateAllTimes(pauseTime);
      } else {
        pauseTime = millis();
        paused = true;
        firstPauseFrame = true;
      }
    } else if (key == CODED && alive && !pressed && (misses != 3 && z == 1)) {
      rotation = random(-PI/8, PI/8); 
      if (keyCode == LEFT) {
        position += -1; 
        i = -1; 
        pressed = true;
      } else if (keyCode == RIGHT) {
        position += 1; 
        i = 1; 
        pressed = true;
      }
    }
    break; 
  case 2 : 
    if (key == ' ' && startup) {
      if (paused) {
        paused = false;
        updateAllTimes(pauseTime);
      } else {      
        pauseTime = millis();
        paused = true;
        firstPauseFrame = true;
      }
    } else if (key == CODED && alive && !pressed) {
      rotation = random(-PI/8, PI/8); 
      if (keyCode == LEFT) {
        position += -1; 
        i = -1; 
        pressed = true;
      } else if (keyCode == RIGHT) {
        position += 1; 
        i = 1; 
        pressed = true;
      }
    }
    break;
  case 5:
    if (highScore) {
      if (keyCode == BACKSPACE && name.length() > 0) {
        name = name.substring(0, name.length()-1);
        //println(name);
      } else if (keyCode == ENTER) {
        int row = highScoreCheck();
        for (int i = 4; i>row; i--) {
          highScores.setString(i, 0, highScores.getString(i-1, 0));
          highScores.setInt(i, 1, highScores.getInt(i-1, 1));
        }
        highScores.setString(row, 0, name);
        highScores.setInt(row, 1, score);
        saveTable(highScores, "data/gw/highScores.csv");
        highScore = false;
        z = 4;
      } else if (key != CODED && keyCode != BACKSPACE) {
        pushStyle();
        textSize(30);
        if (textWidth(name) < 512/4-4) {
          name+=key;
          //println(name);
        }
        popStyle();
      }
    }
    break;
  }
}

//Ensures that the arrow key must be pressed and released before movement is possble again
//prevents holding down the key for constant movement
public void keyReleased() {
  pressed = false;
}

public void loadAssets() {
    println(colorDir + "background.png");
  backdrop = loadImage(colorDir + "background.png");
  startupOverlay = loadImage(colorDir + "startup overlay.png");
  aliveGW = loadImage(colorDir + "alive.png");
  deadGW = loadImage(colorDir + "dead.png");
  leftDoor = loadImage(colorDir + "leftDoor.png");
  deadHead = loadImage(colorDir + "deadHead.png");
  openDoor = loadImage(colorDir + "openDoor.png");
  closedDoor = loadImage(colorDir + "closedDoor.png");
  hammer = loadImage(colorDir + "hammer.png");
  bucket = loadImage(colorDir + "bucket.png");
  pliers = loadImage(colorDir + "pliers.png");
  screwdriver = loadImage(colorDir + "screwdriver.png");
  spanner = loadImage(colorDir + "spanner.png");
  missCircle = loadImage(colorDir + "missCircle.png");
  pointCircle = loadImage(colorDir + "point.png");
  trophy = loadImage(colorDir + "trophy.png");
  gears = loadImage(colorDir + "Gears.png");
}

public void readSettings() {
  Table settings = loadTable("gw/settings.csv", "header, csv");
  colorDir = settings.getString(0, 0);
  volume = settings.getInt(1, 0);
  if (colorDir.equals("bright//")) {
    backgroundColor = 0xffFFFFFF;
    bright = true;
  } else {
    backgroundColor = 0xffAEC0C1;
    bright = false;
  }

  size(Integer.parseInt(settings.getString(2, 0).substring(0, settings.getString(2, 0).indexOf('x'))), Integer.parseInt(settings.getString(2, 0).substring(settings.getString(2, 0).indexOf('x')+1)));
}

public void setVolume() {
  hammerB.amp(volume/100.0f);
  hammerB.amp(volume/100.0f);
  bucketB.amp(volume/100.0f);
  plierB.amp(volume/100.0f);
  screwdriverB.amp(volume/100.0f);
  spannerB.amp(volume/100.0f);
  deathB.amp(volume/100.0f);
  doorB.amp(volume/100.0f);
}
//Constrains the position variable to ensure Mr.GW is always on screen
public void constrainPos() {
  if (open) {
    if (safe) {
      position = constrain(position, 0, 7);
    } else {
      position = constrain(position, 2, 7);
    }
  } else {
    if (safe) {
      position = constrain(position, 0, 6);
    } else {
      position = constrain(position, 2, 6);
    }
  }
  if (position != 0) {
    safe = false;
  }
}

//Checks the door timer against the total frames to check if the door should open or close
public void timerCheck() {
  if (millis() - doorTimerStart > doorTimer*1000 && alive) {
    open = !open; 
    doorTimerStart = millis(); 
    doorTimer = PApplet.parseInt(random(3, 8)); 
    //println(doorTimer);
  }
}

//sets the difficulty value
public void difficulty() {
  difficulty = PApplet.parseInt(millis()/10000-misses*2); 
  difficulty = constrain(difficulty, 0, 10); 
  spawnDifficulty = constrain(difficulty, 0, 7);
}

//returns the row that the high new high score should occupy,
//if there isn't a new high score it returns the value 5 
//which is outside the bounds of the highscore table and so it is ignored
public int highScoreCheck() {
  int row = 5;
  for (int i = highScores.getRowCount() - 1; i >= 0; i--) {
    if (highScores.getInt(i, 1) <= score) {
      row--;
      continue;
    } else {
      //println("row is " + row);
      return row;
    }
  }
  return 0;
}

public void updateAllTimes(long time) {
  doorTimerStart += millis() - time;
  lastFrameTime += millis() - time;
  deathTime += millis() - time;
  lastTime += millis() - time;
  for (int l = 0; l < lastToolUpdate.length; l++)
    lastToolUpdate[l] += millis() - time;
}
//The following is all of the functions that create the fill the button class object arrays with the relevant buttons


//Creates the main menu buttons
public void createPictographicMenuButtons() {
  int index = 0;
  //Left most button that activates Full mode
  pictographicMenuButtons[index++] = new PictographicButton(new PVector (512/2-165, 348/2-25), 100, 100, backgroundColor) {

    @Override public void action() {
      z = 1; 
      setup();
    }

    @Override public void pictograph() {
      PFont retro = createFont(fontDir + "DS-DIGI.TTF", 72);
      int tempI = i;
      boolean tempAlive = alive;
      pushMatrix(); 
      i=1; 
      alive = true; 
      translate(centerCoor.x, centerCoor.y); 
      textAlign(CENTER, CENTER);
      textFont(retro);
      textSize(30);
      fill(0);
      text("1981", 0, -36);
      MrGW(); 
      popMatrix();
      i = tempI;
      alive = tempAlive;
    }

    @Override public void overlay() {
      pushStyle();
      rectMode(CENTER);
      fill(backgroundColor); 
      stroke(0); 
      strokeWeight(3); 
      rect(centerCoor.x, centerCoor.y, buttonWidth, buttonHeight); 
      fill(0); 
      textFont(font); 
      textAlign(CENTER, CENTER); 
      textSize(12); 
      text("Play Mr.GW with the original numerical scoring.", centerCoor.x, centerCoor.y, buttonWidth-6, buttonHeight-6);
      popStyle();
    }
  };

  //Right most button that activates the marking mode
  pictographicMenuButtons[index++] = new PictographicButton(new PVector (512/2+165, 348/2-25), 100, 100, backgroundColor) {

    @Override public void action() {
      z = 2; 
      setup();
    }

    @Override public void pictograph() {
      int tempI = i;
      boolean tempAlive = alive;
      pushMatrix(); 
      i=1; 
      alive = true; 
      translate(centerCoor.x, centerCoor.y); 
      gradientCircle(new PVector(-30, -30), 30, missColor);
      gradientCircle(new PVector(30, -30), 30, scoreColor);
      MrGW(); 
      popMatrix();
      i = tempI;
      alive = tempAlive;
    }

    @Override public void overlay() {
      pushStyle();
      rectMode(CENTER);
      fill(backgroundColor); 
      stroke(0); 
      strokeWeight(3); 
      rect(centerCoor.x, centerCoor.y, buttonWidth, buttonHeight); 
      fill(0); 
      textFont(font); 
      textAlign(CENTER, CENTER); 
      textSize(12); 
      text("Play the marking version of Mr.GW with circular scoring.", centerCoor.x, centerCoor.y, buttonWidth-6, buttonHeight-6);
      popStyle();
    }
  };

  // the left middle button fo the settings menu
  pictographicMenuButtons[index++] = new PictographicButton(new PVector (512/2-55, 348/2-25), 100, 100, backgroundColor, gears) {

    @Override public void action() {
      z = 3; 
      setup();
    }

    @Override public void pictograph() {
      imageMode(CENTER);
      image(pic, centerCoor.x, centerCoor.y, 94, 82);
      imageMode(CORNER);
    }

    @Override public void overlay() {
      pushStyle();
      rectMode(CENTER);
      fill(backgroundColor); 
      stroke(0); 
      strokeWeight(3); 
      rect(centerCoor.x, centerCoor.y, buttonWidth, buttonHeight); 
      fill(0); 
      textFont(font); 
      textAlign(CENTER, CENTER); 
      textSize(12); 
      text("Open the settings menu to alter settings such as the color scheme.", centerCoor.x, centerCoor.y, buttonWidth-6, buttonHeight-6);
      popStyle();
    }
  };

  //the right middle button that opens the leaderboard
  pictographicMenuButtons[index++] = new PictographicButton(new PVector (512/2+55, 348/2-25), 100, 100, backgroundColor, trophy) {
    @Override public void action() {
      z = 4;
      setup();
    }

    @Override public void pictograph() {
      imageMode(CENTER);
      image(pic, centerCoor.x, centerCoor.y, 90, 83);
      imageMode(CORNER);
    }

    @Override public void overlay() {
      pushStyle();
      rectMode(CENTER);
      fill(backgroundColor); 
      stroke(0); 
      strokeWeight(3); 
      rect(centerCoor.x, centerCoor.y, buttonWidth, buttonHeight); 
      fill(0); 
      textFont(font); 
      textAlign(CENTER, CENTER); 
      textSize(12); 
      text("View or reset the high scores.", centerCoor.x, centerCoor.y, buttonWidth-6, buttonHeight-6);
      popStyle();
    }
  };
}

//creates the buttons seen within the settings menu
public void createSettingsButtons() {
  int index = 0;

  settingsButts[index++] = new TextButton(new PVector(512/2, 75), 200, 45, bright ? "Color: Bright" : "Color: Retro", 30, backgroundColor) {
    @Override public void action() {
      if (bright) {
        this.buttonText("Color: Retro");
        backgroundColor = 0xffAEC0C1;
        colorDir = "retro//";
        loadAssets();
        alpha = 50;
        bright = false;
      } else {
        this.buttonText("Color: Bright");
        backgroundColor = 0xffFFFFFF;
        colorDir = "bright//";
        loadAssets();
        alpha = 255;
        bright = true;
      }
    }
  };

  settingsButts[index++] = new TextButton(new PVector(512/2, 125), 200, 45, "Resolution", 30, backgroundColor) {
    @Override public void action() {
      z = 6;
    }
  };

  settingsButts[index++] = new TextButton(new PVector(512/2-70, 225), 60, 45, "<", 30, backgroundColor) {
    @Override public void action() {
      volume -= 10;
      volume = constrain(volume, 0, 100);
      setVolume();
    }
  };

  settingsButts[index++] = new TextButton(new PVector(512/2+70, 225), 60, 45, ">", 30, backgroundColor) {
    @Override public void action() {
      volume += 10;
      volume = constrain(volume, 0, 100);
      setVolume();
    }
  };

  settingsButts[index++] = new TextButton(new PVector(512/2, 300), "Apply", 30, backgroundColor) {
    @Override public void action() {
      z = 0;
      Table settings = loadTable("settings.csv", "header, csv");
      settings.setString(0, 0, colorDir);
      settings.setInt(1, 0, volume);
      settings.setString(2, 0, width+"x"+height);
      saveTable(settings, "data/gw/settings.csv", "header, csv");
      createPictographicMenuButtons();
    }
  };
}

//creates the buttons seen within the game
public void createInGameButtons() {
  int index = 0;

  inGameButtons[index++] = new PictographicButton(new PVector(487, 25), 40, 40, backgroundColor) {
    @Override public void action() {
      exit = true;
      firstPauseFrame = true;
      pauseTime = millis();
    }

    @Override public void pictograph() {
      pushStyle();
      pushMatrix();
      strokeWeight(5);
      line(upperLeftCoor.x+11, upperLeftCoor.y+11, lowerRightCoor.x-11, lowerRightCoor.y-11);
      line(lowerRightCoor.x-11, upperLeftCoor.y+11, upperLeftCoor.x+11, lowerRightCoor.y-11);
      popMatrix();
      popStyle();
    }

    @Override public void overlay() {
    }
  };

  inGameButtons[index++] = new PictographicButton(new PVector(442, 25), 40, 40, backgroundColor) {
    @Override public void action() {
      paused = true;
      firstPauseFrame = true;
      pauseTime = millis();
    }

    @Override public void pictograph() {
      pushStyle();
      pushMatrix();
      noStroke();
      fill(0);
      rectMode(CENTER);
      rect(centerCoor.x-buttonWidth/6, centerCoor.y, buttonWidth/5, 3*buttonHeight/5);
      rect(centerCoor.x+buttonWidth/6, centerCoor.y, buttonWidth/5, 3*buttonHeight/5);
      popMatrix();
      popStyle();
    }

    @Override public void overlay() {
    }
  };
}

//the resume button for when the game is paused
public void createPauseMenuButtons() {
  int index = 0;

  pauseMenuButtons[index++] = new TextButton(new PVector(512/2, 348/2), "Resume", 30, backgroundColor) {
    @Override public void action() {
      paused = false;
      updateAllTimes(pauseTime);
    }
  };
}

//creates yes and no buttons for the game
public void createConfirmationButtons() {
  int index = 0;

  confirmationButtons[index++] = new TextButton(new PVector((512/2-75), 348/2), 90, 45, "Yes", 30, backgroundColor) {
    @Override public void action() {
      if (exit) {
        exit = false;
        z = 0;
        setup();
      } else if (reset) {
        highScores = loadTable("gw/highscoresReset.csv", "header, csv");
        saveTable(highScores, "data/gw/highscores.csv", "header, csv");
        reset = false;
      }
    }
  };

  confirmationButtons[index++] = new TextButton(new PVector((512/2+75), 348/2), 90, 45, "No", 30, backgroundColor) {
    @Override public void action() {
      if (exit) {
        exit = false;
        paused = false;
        updateAllTimes(pauseTime);
      } else if (reset) {
        reset = false;
      }
    }
  };
}

// creates the buttons seen within the highscore menu
public void createHighScoreButtons() {
  int index = 0;

  highScoreButtons[index++] = new TextButton(new PVector((512/2-75), 306), 90, 45, "Back", 30, backgroundColor) {
    @Override public void action() {
      z = 0;
      setup();
    }
  };

  highScoreButtons[index++] = new TextButton(new PVector((512/2+75), 306), 90, 45, "Reset", 30, backgroundColor) {
    @Override public void action() {
      reset = true;
    }
  };
}

//Button and setup variables
int z = 0;                                                       //z is the variable that determines the mode os the game
boolean firstSetup = true, bright;  // a couple booleans that determine the colorscheme and the whether setup is being run for the first

//initializes the arrays for all of the buttons
PictographicButton[] pictographicMenuButtons = new PictographicButton[4];  
TextButton[] settingsButts = new TextButton[5];
TextButton[] resolutionButts = new TextButton[3];
PictographicButton[] inGameButtons = new PictographicButton[2];
TextButton[] pauseMenuButtons = new TextButton[1];
TextButton[] confirmationButtons = new TextButton[2];
TextButton[] highScoreButtons = new TextButton[2];

//Mr. GW Variables
//Imports the official processing foundation sound library

SoundFile hammerB, bucketB, plierB, screwdriverB, spannerB, deathB, doorB;

//various variables
int score, position, doorTimer, i, misses, alpha, pauseFrame;
int deadPosition = 0; 
int difficulty, spawnDifficulty, fallenTools;
int [] toolStart = {67, 167, 367, 633, 900}; // the toolstart offset is here to ensure that no two tools even update/fall at the same time, this is done by offsetting the each tool frame by a prime number
boolean open, safe, alive, timeSaved, pressed, drawn, startup, paused, exit, firstPauseFrame, reset, highScore;
boolean [][] tools;
float rotation, startRotation;
PFont retro, arialbd, arial;
int backgroundColor = 0xffFFFFFF, treeColor = 0xff18C824, balconyColor = 0xffFF5500; // colors for the background and backdrop
String name = "", colorDir = "gw/bright//", soundDir = "gw/sounds//", fontDir = "gw/fonts//";

// Circle variables
int diameter, circleGap, numberOfCircles;
int missColor = 0xffFF0000, scoreColor = 0xff000000; //colors for the circles

Table highScores;

PImage backdrop, startupOverlay, aliveGW, deadGW, leftDoor, deadHead, openDoor, closedDoor;
PImage hammer, bucket, pliers, screwdriver, spanner, missCircle, pointCircle, trophy, gears;

long lastFrameTime, pauseTime, doorTimerStart, lastMoveTime, deathTime, lastTime = 0;
long[] lastToolUpdate = {0, 0, 0, 0, 0};
int volume;
//draws a gradient circle
public void gradientCircle(PVector coor, int d, int c) {
  diameter = d;
  if(c == missColor)
    image(missCircle, coor.x-d/2, coor.y-d/2, d, d);
    else
    image(pointCircle, coor.x-d/2, coor.y-d/2, d,d);
}

//Controls the drawing of the right door
public void rightDoor() {
  pushMatrix(); 
  strokeWeight(2); 
  if (open) {
    image(openDoor, 455, 208, 36, 104);
  } else {
    image(closedDoor, 420, 235, 28, 36);
  }
  popMatrix();
}
//assorted text boxes and overlays that are used in both game modes

//the game over screen
public void gameOver() {
  pushStyle();
  fill(backgroundColor);
  stroke(0);
  strokeWeight(2);
  rect(30, 80, 460, 115);
  fill(0);
  textAlign(CENTER, BOTTOM);
  textSize(72);
  text("GAME OVER", 256, 163);
  textFont(arial);
  textSize(24);
  text("CLICK TO RETURN TO MAIN MENU", 256, 188);
  popStyle();
}

//the opaque rectangle which shrouds the game and the rectangle that surrounds the text
public void pauseShroud() {
  pushMatrix();
  pushStyle();
  fill(backgroundColor, 125);
  noStroke();
  rectMode(CENTER);
  rect(512/2, 348/2, 512, 348);
  firstPauseFrame = false;
  fill(backgroundColor);
  stroke(0);
  rect(512/2, 348/2-50, 400, 200);
  popStyle();
  popMatrix();
}

//The pause menu text and button
public void pauseOverlay() {
  pushStyle();
  pushMatrix();
  textFont(arialbd);
  textAlign(CENTER, CENTER);
  textSize(90);
  fill(0);
  text("PAUSED", 512/2, 348/2-100);
  for (TextButton b : pauseMenuButtons) {
    b.display();
  }
  popMatrix();
  popStyle();
}

//The exit menu text and buttons
public void exitCurrentGameOverlay() {
  pushStyle();
  pushMatrix();
  textFont(arialbd);
  textAlign(CENTER, CENTER);
  textSize(90);
  fill(0);
  text("QUIT?", 512/2, 348/2-100);
  for (TextButton b : confirmationButtons) {
    b.display();
  }
  popMatrix();
  popStyle();
}
//This tab contains all of the different modes draw loops

//used to display the main menu and the game shown in the background
public void mainMenu() {
  background(0);

  //Main Gameplay
  if (misses == 3 && !drawn) {
    misses = 0;
    score = 0;
    position = 0;
    alive = true;
    safe = true;
    tools = new boolean[5][6];
  } else if (misses <= 3) {
    startup = true;
    toolUpdate();
    constrainPos();
    timerCheck();
    difficulty();

    image(backdrop, 0, 0);
    score();
    misses();
    toolsDraw();
    //ground();
    //gameB();
    //trees();
    //rightBuilding();
    rightDoor();
    movement();
    //leftBackdrop();
    image(leftDoor, 0, 224, 41, 78);
    fill(backgroundColor, 175);
    noStroke();
    rect(0, 0, 512, 348);
  }
  for (PictographicButton b : pictographicMenuButtons) {  
    b.display();
  }
  for (PictographicButton b : pictographicMenuButtons) {
    if (b.mouseOver()) {
      b.overlay();
    }
  }
}

//The Full games mode which has higher scoring, shown in text and no circles
public void fullMode() {
  //Decides whether to display the boot sequence
  if (!startup) {
    startup();
  }
  if (firstPauseFrame) {
    pauseShroud();
  }
  if (exit) {
    exitCurrentGameOverlay();
  } else if (paused) {
    pauseOverlay();
  } else {
    //Main Gameplay
    if (misses != 3 && startup) {
      toolUpdate();
      constrainPos();
      timerCheck();
      difficulty();
      background(0);

      image(backdrop, 0, 0, 512, 348);
      score();
      misses();
      toolsDraw();
      //ground();
      //gameB();
      //trees();
      //rightBuilding();
      rightDoor();
      movement();
      image(leftDoor, 0, 224, 41, 78);
      //leftBackdrop();  
      for (PictographicButton b : inGameButtons) {
        b.display();
      }
      //Game Over screen
    } else if (misses == 3 && startup) {
      gameOver();
    }
  }
}

//the marking mode for MR.GW which includes the circular marking
public void markingMode() {
  //Decides whether to display the boot sequence
  if (!startup) {
    startup();
  }
  if (firstPauseFrame) {
    pauseShroud();
  }
  if (exit) {
    exitCurrentGameOverlay();
  } else if (paused) {
    pauseOverlay();
  } else {
    //Main Gameplay
    if ((misses * numberOfCircles) + score < ((348/(diameter+circleGap)) * numberOfCircles) && startup) {
      toolUpdate();
      constrainPos();
      timerCheck();
      difficulty();
      background(0); 

      image(backdrop, 0, 0); 
      toolsDraw();
      //ground();
      //gameB();
      //trees();
      //rightBuilding();
      rightDoor();
      movement();
      image(leftDoor, 0, 224, 41, 78);

      //leftBackdrop();

      //THE ONE FUNCTION THAT RULES THEM ALL
      oneFunctionToRuleThemAll(new PVector (circleGap/2 + diameter/2, circleGap/2 + diameter/2), (circleGap + diameter), (circleGap + diameter), missColor, scoreColor, misses, score);

      for (PictographicButton b : inGameButtons) {
        b.display();
      }
      //Game Over screen
    } else if ((misses * numberOfCircles) + score >= ((348/(diameter+circleGap)) * numberOfCircles) && startup) {
      fill(backgroundColor);
      stroke(0);
      strokeWeight(2);
      rect(30, 80, 460, 115);
      fill(0);
      textAlign(CENTER, BOTTOM);
      textSize(72);
      text("GAME OVER", 256, 163);
      textSize(18);
      text("CLICK TO RETURN TO MAIN MENU", 256, 188);
    }
  }
}

//dsplays the settins menu
public void settingsMenu() {
  pushStyle();
  background(0);
  fill(backgroundColor);
  noStroke();
  rect(0, 0, 512, 348);
  textSize(30);
  textAlign(CENTER, CENTER);
  fill(0);
  text("Volume", 512/2, 175);
  text(volume, 512/2, 225);
  popStyle();
  for (TextButton b : settingsButts) {
    b.display();
  }
}

//enters the mode to view and reset the high scores
public void highScoresMenu() {
  if (reset) {
    pushStyle();
    pushMatrix();
    rectMode(CENTER);
    fill(backgroundColor);
    stroke(0);
    rect(512/2, 348/2-50, 450, 200);
    textFont(arialbd);
    textAlign(CENTER, CENTER);
    textSize(64);
    fill(0);
    text("Are you sure?", 512/2, 348/2-125);
    textSize(32);
    text("All high scores will be lost.", 512/2, 348/2-75);
    for (TextButton b : confirmationButtons) {
      b.display();
    }
    popMatrix();
    popStyle();
  } else {
    pushStyle();
    pushMatrix();
    fill(0);
    pushStyle();
    background(0);
    fill(backgroundColor);
    noStroke();
    rect(0, 0, 512, 348);
    popStyle();
    textFont(arialbd);
    textSize(60);
    textAlign(CENTER, CENTER);
    text("Leaderboard", 512/2, 28);
    strokeWeight(3);
    for (int i = 1; i <= 3; i++) {
      line(512/4*i, 64, 512/4*i, 264);
    }
    for (int i = 0; i < 6; i++) {
      line(512/4, 64 + 40 * i, 3*512/4, 64 + 40 * i);
    }
    for (int i = 0; i < highScores.getRowCount(); i++) {
      //println(i);
      textFont(arial);

      textAlign(LEFT, BOTTOM);
      textSize(30);
      text(highScores.getString(i, 0), 512/4+4, 104 + 40 * i);
      text(highScores.getString(i, 1), 512/4*2+4, 104+40*i);
      //println("row " + i + " is " + highScores.getString(i,1));
    }
    for (TextButton b : highScoreButtons) {
      b.display();
    }
    popMatrix();
    popStyle();
  }
}

//enters the menu that allows you to input a new high score.
public void enterNewHighScore() {
  int row = highScoreCheck();
  if (row >= 0 && row <= highScores.getRowCount()-1) {
    highScore = true;
    pushStyle();
    pushStyle();
    pushStyle();
    background(0);
    fill(backgroundColor);
    noStroke();
    rect(0, 0, 512, 348);
    popStyle();    
    textFont(arialbd);
    textSize(48);
    textAlign(CENTER, TOP);
    fill(0);
    text("NEW HIGH SCORE", 512/2, 0);
    textSize(24);
    text("Enter Name Below:", 512/2, 60);
    text("Press enter to finalise", 512/2, 348/2+20);
    line(75, 348/2, 437, 348/2);
    textAlign(CENTER, BOTTOM);
    textSize(50);
    line(512/2+textWidth(name)/2, 348/2-5, 512/2+textWidth(name)/2, 348/2- 50);
    text(name, 512/2, 348/2);
    popStyle();
    popStyle();
  }
}

public void selectResolution() {
  pushStyle();
  background(0);
  fill(backgroundColor);
  noStroke();
  rect(0, 0, 512, 348);
  popStyle();  
  for (TextButton b : resolutionButts) {
    b.display();
  }
}
//this is the function that determines where and how MR.GW should be drawn
public void movement() { 
  
  //the following block of code is only executed while in the main menu
  //it is what determies the movement of MR.GW automatically that gives the illusion of the game being played
  int deltaPos = 0;
  if (millis()-lastMoveTime > 500 && z == 0) {           //this only occurs every half second
  //the following stack of if statements determines the probability of MR.GW being moved left, right, or staying the same place
    lastMoveTime = millis();
    if (position == 0 || position == 2) {
      deltaPos = PApplet.parseInt(random(0, 2));        
    } else if(open && position == 6) {
      deltaPos = PApplet.parseInt(random(1,2));
    } else if (open && (position != 0 || position != 2)) {
      deltaPos = PApplet.parseInt(random(-1, 2));
    } else if (!open && position == 6) {
      deltaPos = PApplet.parseInt(random(-2, 0));
    } else if (!open && (position !=6 || position != 0 || position != 2)) {
      deltaPos = PApplet.parseInt(random(-2, 2));
    }
    
    //based off of the previous randoms, the following actually turn that into the movenement that is seen
    switch (deltaPos) {
    case -2:
    case -1:
      i = -1;
      rotation = random(-PI/8, PI/8); 
      position--;
      break;
    case 0:
      break;
    case 1:
    case 2:
      i = 1;
      rotation = random(-PI/8, PI/8); 
      position++;
      break;
    }
    constrainPos();
  }

//this ensures that MR.GW has been drawn, dead, on the screen before pausing all of functions
  if (drawn && startup) {
    if (millis()-lastTime >= 1500) {
      resetGW();
      updateAllTimes(deathTime);
      drawn = false;
    } else {
      //println(millis()-lastTime);
      position = deadPosition;
      rotation = PI/2; 
      i = -1; 
      pushMatrix();
      translate(position*64-30, 307); 
      rotate(rotation); 
      MrGW();
      popMatrix();
    }
  }
  
  //draws a dead GW in the Center for the startup menu
    if (!alive && !drawn && !startup) {
    rotation = PI/2; 
    i = -1; 
    pushMatrix();
    translate(position*64-30, 307); 
    rotate(rotation); 
    MrGW(); 
    deadPosition = position;
    popMatrix();
  }
  
  //draws a dead mr.gw for normal gameplay when he dies
  if (!alive && !drawn && startup) {
    lastTime = millis(); 
    rotation = PI/2; 
    i = -1; 
    pushMatrix();
    translate(position*64-30, 307); 
    rotate(rotation); 
    MrGW(); 
    deadPosition = position;
    popMatrix();
    drawn = true;
  }

//draws MR.GW whenever he is alive
  pushMatrix();
  if (alive) {
    if (position == 0) {
      translate(42, 250); 
      i = 1; 
      alive = true; 
      MrGW();
    } else if (position <7) {
      translate(position*64-30, 250); 
      rotate(rotation); 
      MrGW();
    } else {
      translate(position*64-10, 250); 
      rotate(rotation); 
      MrGW(); 
      if (startup && z != 0) {
        doorB.play();
      }
      i = 1; 
      if (z == 1 || z == 0) {
        score += 5;
      } else if (z == 2) {
        score += 3;
      }
      resetGW();
    }
  }
  popMatrix();
}

//Resets the variable nessecary for Mr.GW to be back in the safe door at left
public void resetGW() {
  alive = true;
  position = 0; 
  safe = true;
}
public void MrGW() {
  pushMatrix(); 
  if (alive) {
    scale(i,1);
    image(aliveGW, -29, -15, 49, 58);
  }

  if (!alive) {
    scale(-1,1);
    image(deadGW, -25, -24, 49, 71);
  }
  popMatrix();
}
//Here is my one function to rule them all

public void oneFunctionToRuleThemAll(PVector startCOOR, int deltaX, int deltaY, int missC, int scoreC, int i, int s) {
  int loopIterations, row, col; 
  int c; 
  loopIterations = i*numberOfCircles+s; 
  row = col = 0; 
  for (int j = 0; j < loopIterations; j++) {
    // this if statement ensures that no cirlce are draw beneath
    // the screen once the maximun number of cirlces has been reached.
    if (j>=((348/(diameter+circleGap)) * numberOfCircles)) {
      break;
    }
    
    //based off of which loopIteration(circle) it is drawing and the numberOfCircles that fit across the screen,
    //the row and column are determined and used to place the circle
    row = j / numberOfCircles; 
    col = j % numberOfCircles; 
    
    //checks whether the circle should be red or black
    if (j < i * numberOfCircles)
      c = missC; 
    else
      c = scoreC; 
    gradientCircle(new PVector (col * deltaX + startCOOR.x, row * deltaY + startCOOR.y), diameter, c);
  }
} 
//the class that I built to create pictogrpahic buttons with overlays
abstract class PictographicButton {

  PVector centerCoor, upperLeftCoor, lowerRightCoor; 
  int buttonWidth, buttonHeight, textSize; 
  String overlayText; 
  PVector [] buttonCoors; 
  PFont font;
  PImage pic;

  PVector centerCoorDefault, upperLeftCoorDefault, lowerRightCoorDefault; 
  int buttonWidthDefault, buttonHeightDefault, textSizeDefault; 
  String overlayTextDefault; 
  PVector[] buttonCoorsDefault; 

  PictographicButton(PVector coor, int w, int h, int c) {  
    centerCoorDefault = centerCoor = coor; 
    upperLeftCoorDefault = upperLeftCoor = new PVector (coor.x - w/2, coor.y - h/2); 
    lowerRightCoorDefault = lowerRightCoor = new PVector (coor.x + w/2, coor.y + h/2); 
    buttonWidthDefault = buttonWidth = w; 
    buttonHeightDefault = buttonHeight = h; 
    buttonCoorsDefault = buttonCoors = new PVector[] {upperLeftCoor, lowerRightCoor}; 
    font = createFont("arial", 36);
  }

  PictographicButton(PVector coor, int w, int h, int c, PImage p) {  
    centerCoorDefault = centerCoor = coor; 
    upperLeftCoorDefault = upperLeftCoor = new PVector (coor.x - w/2, coor.y - h/2); 
    lowerRightCoorDefault = lowerRightCoor = new PVector (coor.x + w/2, coor.y + h/2); 
    buttonWidthDefault = buttonWidth = w; 
    buttonHeightDefault = buttonHeight = h; 
    buttonCoorsDefault = buttonCoors = new PVector[] {upperLeftCoor, lowerRightCoor}; 
    font = createFont("arial", 36);
    pic = p;
  }


  //all of the "gets"
  public PVector upperLeftCoor() {
    return upperLeftCoor;
  }

  public PVector lowerRightCoor() {
    return lowerRightCoor;
  }

  public int buttonWidth() {
    return buttonWidth();
  }

  public int buttonHeight() {
    return buttonHeight;
  }

  public PVector[] buttonCoors() {
    return buttonCoors;
  }

  //all of the "sets"
  public void upperLeftCoor(PVector coor) {
    upperLeftCoor = coor; 
    buttonCoors[0] = coor;
  }

  public void lowerRightCoor(PVector coor) {
    lowerRightCoor = coor; 
    buttonCoors[1] = coor;
  }

  public void buttonWidth(int w) {
    buttonWidth = w;
  }

  public void buttonHeight(int h) {
    buttonHeight = h;
  }

  public void buttonCoors(PVector[] coors) {
    buttonCoors = coors;
  }

  public boolean mouseOver() {
    if (mouseX > screenX(upperLeftCoor.x, upperLeftCoor.y) && mouseX < screenX(lowerRightCoor.x, lowerRightCoor.y) && mouseY > screenY(upperLeftCoor.x, upperLeftCoor.y) && mouseY < screenY(lowerRightCoor.x, lowerRightCoor.y)) {
      return true;
    }
    return false;
  }

  public void reset() {
    centerCoor = centerCoorDefault; 
    upperLeftCoor= upperLeftCoorDefault; 
    lowerRightCoor = lowerRightCoorDefault; 
    buttonWidth = buttonWidthDefault; 
    buttonHeight = buttonHeightDefault; 
    buttonCoors = buttonCoorsDefault; 
  }

  public void display() {
    strokeWeight(3); 
    stroke(0); 
    fill(backgroundColor); 
    rectMode(CENTER); 
    rect(centerCoor.x, centerCoor.y, buttonWidth, buttonHeight); 
    rectMode(CORNER);
    pictograph();
  }

  public abstract void pictograph();

  public abstract void overlay(); 

  public abstract void action();
}
//shows the start up boot
public void startup() {
  if (lastFrameTime > millis()-1500) {
    image(backdrop, 0, 0, 512, 348);
    image(startupOverlay, 0, 0, 512, 348);
    image(leftDoor, 0, 224, 41, 78);
  } else {
    score = 0; 
    position = 0; 
    doorTimerStart = millis();
    for (int l = 0; l < lastToolUpdate.length; l++)
      lastToolUpdate[l] = millis();
    frameCount = 0; 
    i = 1; 
    misses = 0; 
    open = true; 
    safe = true; 
    alive = true; 
    pressed = false; 
    drawn = false; 
    startup = true; 
    tools = new boolean[5][6];
  }
}
//the class I built for basic text buttons
abstract class TextButton {

  PVector centerCoor, upperLeftCoor, lowerRightCoor; 
  int buttonWidth, buttonHeight, textSize; 
  String buttonText; 
  PVector [] buttonCoors; 
  PFont font; 

  PVector centerCoorDefault, upperLeftCoorDefault, lowerRightCoorDefault; 
  int buttonWidthDefault, buttonHeightDefault, textSizeDefault; 
  String buttonTextDefault; 
  PVector[] buttonCoorsDefault; 

  // first constructor based of defining the button size
  TextButton(PVector coor, int w, int h, String text, int tS, int c) {  
    centerCoorDefault = centerCoor = coor; 
    upperLeftCoorDefault = upperLeftCoor = new PVector (coor.x - w/2, coor.y - h/2); 
    lowerRightCoorDefault = lowerRightCoor = new PVector (coor.x + w/2, coor.y + h/2); 
    buttonWidthDefault = buttonWidth = w; 
    buttonHeightDefault = buttonHeight = h; 
    buttonTextDefault = buttonText = text; 
    textSizeDefault = textSize = tS; 
    buttonCoorsDefault = buttonCoors = new PVector[] {upperLeftCoor, lowerRightCoor}; 
    font = createFont("arial", 36);
  }

  //second constructor based around defining the size of the butotn off the test size
  TextButton(PVector coor, String text, int tS, int c) {
    buttonTextDefault = buttonText = text; 
    textSizeDefault = textSize = tS; 
    textSize(tS); 
    buttonWidthDefault = buttonWidth = PApplet.parseInt(textWidth(buttonText) + 40); 
    buttonHeightDefault = buttonHeight = PApplet.parseInt(textAscent()+ textDescent() + 4); 
    centerCoorDefault = centerCoor = coor; 
    upperLeftCoorDefault = upperLeftCoor = new PVector (coor.x - buttonWidth/2, coor.y - buttonHeight/2); 
    lowerRightCoorDefault = lowerRightCoor = new PVector (coor.x + buttonWidth/2, coor.y + buttonHeight/2); 
    buttonCoorsDefault = buttonCoors = new PVector[] {upperLeftCoor, lowerRightCoor}; 
    font = createFont("arial", 36);
  }

  public PVector upperLeftCoor() {
    return upperLeftCoor;
  }

  public PVector lowerRightCoor() {
    return lowerRightCoor;
  }

  public int buttonWidth() {
    return buttonWidth();
  }

  public int buttonHeight() {
    return buttonHeight;
  }

  public String buttonText() {
    return buttonText;
  }

  public PVector[] buttonCoors() {
    return buttonCoors;
  }

  public void upperLeftCoor(PVector coor) {
    upperLeftCoor = coor; 
    buttonCoors[0] = coor;
  }

  public void lowerRightCoor(PVector coor) {
    lowerRightCoor = coor; 
    buttonCoors[1] = coor;
  }

  public void buttonWidth(int w) {
    buttonWidth = w;
  }

  public void buttonHeight(int h) {
    buttonHeight = h;
  }

  public void buttonText(String text) {
    buttonText = text;
  }

  public void buttonCoors(PVector[] coors) {
    buttonCoors = coors;
  }

  public boolean mouseOver() {
    if (mouseX > screenX(upperLeftCoor.x, upperLeftCoor.y) && mouseX < screenX(lowerRightCoor.x, lowerRightCoor.y) && mouseY > screenY(upperLeftCoor.x, upperLeftCoor.y) && mouseY < screenY(lowerRightCoor.x, lowerRightCoor.y)) {
      return true;
    }
    return false;
  }

  public void display() {
    strokeWeight(3);
    stroke(0);
    fill(backgroundColor); 
    rectMode(CENTER); 
    rect(centerCoor.x, centerCoor.y, buttonWidth, buttonHeight); 
    rectMode(CORNER); 
    fill(0); 
    textFont(font); 
    textAlign(CENTER, CENTER); 
    textSize(textSize); 
    text(buttonText, upperLeftCoor.x + buttonWidth/2, upperLeftCoor.y + 2*buttonHeight/5);
  }

  public abstract void action();
}
//displays the score
public void score() {
  pushMatrix(); 
  fill(0); 
  textAlign(RIGHT, TOP); 
  textFont(retro); 
  if (startup) {
    text(score, 226, 0);
  }
  if (!startup) {
    text("12:00", 226, 0);
  }
  popMatrix();
}

//displays the number of misses
public void misses() {
  pushMatrix(); 
  textAlign(CENTER, TOP); 
  textFont(arialbd); 
  textSize(18); 
  text("MISS", 300, 35); 
  popMatrix(); 
  for (int i = 0; i<misses; i++) {
    image(deadHead, 325 - i*40, 5, 30, 30); 
  }
}
//determines when and if a tool will enter the screen
public void toolUpdate() {
  if (alive) {
    for (int j = 0; j<toolStart.length; j++) {
      if ((millis()-toolStart[j]-lastToolUpdate[j]) > (1500-difficulty*70)) {
        lastToolUpdate[j] = millis();

        //tests if the player is hit
        if (tools[j][4] && position == j+2) {

          //only for the main menu "play"
          if (z == 0) {
            int q = PApplet.parseInt(random(1, 6));
            if (q%3 != 0) {
              rotation = random(-PI/8, PI/8); 
              if (q == 2 || q == 1) {
                if (position == 2) {
                  position++;
                  i = 1;
                } else {
                  position--;
                  i = -1;
                }
              } else if (q == 4 || q == 5) {
                if (position == 6 && !open) {
                  position--;
                  i = -1;
                } else {
                  position++;
                  i = 1;
                }
              }
            } else {
              misses++; 
              if (z == 2) {
                score--;
              } 
              if (z == 1) {
                fallenTools--;
              }          
              alive = false; 
              deathTime = millis();
              if (startup && z != 0) {
                deathB.play();
              }
            }
          } 

          //actual play
          else {
            misses++; 
            if (z == 2) {
              score--;
            } 
            if (z == 1) {
              fallenTools--;
            }          
            alive = false; 
            deathTime = millis();
            if (startup && z != 0) {
              deathB.play();
            }
          }
        }

        //moves the tools down
        for (int y = tools[0].length-1; y>=1; y--) {
          tools[j][y] = tools[j][y-1]; 
          if (tools[j][y] == true && y == 5) {
            if (z == 1 || z == 0) {
              fallenTools += 1;
              if (fallenTools == 3) {
                score++; 
                fallenTools = 0;
              }
            } else if (z == 2) {
              score++;
            }
          }
        }

        //randomly fills the top row with tools
        int q = PApplet.parseInt(random(0, 10-spawnDifficulty)); 
        if (q<=1) {
          tools[j][0] = true;
        } else {
          tools[j][0] = false;
        }

        //this loop checks if a tools is in a column and if so, to play the sound related to the tool
        for (int y = 0; y<tools[j].length-1; y++) {
          if (tools[j][y] && startup && z != 0) {
            switch (j) {
            case 0 : 
              hammerB.play(); 
              break; 
            case 1 : 
              bucketB.play(); 
              break; 
            case 2 : 
              plierB.play(); 
              break; 
            case 3 : 
              screwdriverB.play(); 
              break; 
            case 4 : 
              spannerB.play(); 
              break;
            }
            break;
          }
        }
      }
    }
  }
}

//draws the tools where they should be with the proper rotation
public void toolsDraw() {
  for (int x = 0; x<tools.length; x++) {
    for (int y = 0; y<tools[0].length-1; y++) {
      if (y != 5) {
        if (tools[x][y]) {
          pushMatrix(); 
          translate((x+2)*64-30, (y)*35+80); 
          if (x<=2) {
            rotate(-(y+1)*PI/8);
          } else {
            rotate((y+1)*PI/8);
          }
          switch (x) {
          case 0 : 
            image(hammer, -10, -11, 22, 45);
            break; 
          case 1 : 
            image(bucket, -13, -13, 29, 34);
            break; 
          case 2 : 
            image(pliers, -12, -15, 23, 39);
            break; 
          case 3 : 
            image(screwdriver, -5, -15, 9, 41);
            break; 
          case 4 : 
            image(spanner, -9, -21, 17, 41);
            break;
          }
          popMatrix();
        }
      }
    }
  }
}

}
