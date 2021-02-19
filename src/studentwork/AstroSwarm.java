package studentwork;

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 

public class AstroSwarm extends mqapp.MQApp {

    static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "studentwork.AstroSwarm" };
    runSketch(appletArgs, new AstroSwarm());
  }

    /*    --Notes for marker--
The function drawing my main creature is called drawPlanet(), the functions drawing the mini creatures are drawSlowMinis() and drawFastMinis().
(The slower mini creatures are drawn below the main creature, the faster mini creatures above it - to create a 3D effect.)
The variable responsible for the swarming effect is miniSpeedMultiplier.

The function responsible for the random enemy movement is newEnemyPath(). genSpeed is the variable which determines the speed of the enemy. It is randomised within newEnemyPath().
*/


//enemy variables
int enemyStartingEdge;                                                             //which of the 4 edges to start from
int enemyEndEdge;                                                                  //which of the 4 edges to end to
int sXPos;                                                                         //starting x position
int eXPos;                                                                         //ending x position
int sYPos;                                                                         //starting y position
int eYPos;                                                                         //ending y position

float enemyXChange; 
float enemyYChange;
float genSpeed;                                                                     // speed of enemy (determines x and y change)
float distanceXY;                                                                 // length of the calculated path
float time;                                                                       // number of draw runs it will take to cross distanceXY with a speed of genSpeed
int distanceX;                                                                    // the x distance between sXPos and eXPos
int distanceY;                                                                    // the y distance between sYPos and eYPos
int enemyDelay;                                                                   // the time between the enemy disappearing and a new one appearing

int mainCreatureSize;                                                             // size of the main creature is relative to this
int enemySize;                                                                    // size of the enemy is relative to this
float enemyX;                                                                     //x position of enemy
float enemyY;                                                                     //y position of enemy
float leftEdge;
float rightEdge;
float topEdge;
float bottomEdge;
float asteroidBumpsPos[][];

// mini variables
float miniOffsetLimit;
float [] miniX; 
float [] miniY;
float [] miniSpeed;
int miniMaxSpeed;
int miniMinSpeed;
float [] miniXOffset;
float [] miniYOffset; 
float [] miniYChange; 
float [] miniXChange; 
float [] miniXDist; 
float [] miniYDist; 
float [] miniDist; 
float [] miniTime;
float [] miniSpeedMultiplier;


// background variables
int starNumber;
int nebulaNumber;
float [][] starPos;
float [][] nebulaPos;

int [][] nebulaColours;
int [][] flashingColours;
int green;
int peach;
int pink;
int blue;
int white;
int lightGreen;
int yellow;
int aqua;
int purple;
int lightYellow;

int drawRunsPerColour;
int [][] mainCreatureColours;
int mainPink;
int mainGreen;
int mainBlue;

int [][] otherColours;
int otherLightPurple;
int otherDarkPurple;
int otherDarkBlue;

int [] currentColour;
int [] currentMainColour;
int currentMainColourCheck;
int currentFlashingColourCheck;
int colourRChange;
int colourGChange;
int colourBChange;

int [] lastPos;
int limit;


// variables important for gameplay
boolean gameOver;
int level;
int highscore;
boolean newHighscore;
int globalTimeCounter;
int colourTimeCounter;
float difficultyLevel;
float easy;
float medium;
float hard;

int inGame;
int inMenu;
int inSettings;
int gameOverScreen;

int currentMode;



public void setup() {                                              //Explanation of variables
  size(600, 600);

  limit = 100;                                              // maximum number of mini creatures
  drawRunsPerColour = 20;                                   // sets the rate of colour change of the minicreatures and large planet's ring

  //background variables;
  starNumber = 200;                                         // number of stars generated for the background
  starPos = new float [2][starNumber];                      // position of each star - only generated once, during setup

  nebulaNumber = 8;                                         // number of nebulas generated for the background
  nebulaPos = new float [2][nebulaNumber];                  // position of each nebula
  nebulaColours = new int [nebulaNumber][3];                // colour of each nebula

  // enemy variables:
  enemyX = -enemySize;                                      // actual X position of enemy
  enemyY = -enemySize;                                      // actual Y position of enemy
  enemyXChange = 0;                                         // X distance covered by enemy during each draw run
  enemyYChange = 0;                                         // Y distance covered by enemy during each draw run
  enemyDelay = 0;                                           // delay between disappearance of one enemy and appearance of the next                               
  enemySize = width/10;                                     // diameter of the enemy
  asteroidBumpsPos = new float [8][2];                      // used to draw the asteroid

  asteroidBumpsPos[0][0] = -enemySize/3.2f;
  asteroidBumpsPos[0][1] = -enemySize/3.2f;
  asteroidBumpsPos[1][0] = enemySize/3.2f;
  asteroidBumpsPos[1][1] = -enemySize/3.2f;
  asteroidBumpsPos[2][0] = enemySize/2.5f;
  asteroidBumpsPos[2][1] = -enemySize/10;
  asteroidBumpsPos[3][0] = -enemySize/3.1f;
  asteroidBumpsPos[3][1] = enemySize/3.1f;
  asteroidBumpsPos[4][0] = 0;
  asteroidBumpsPos[4][1] = enemySize/2.3f;
  asteroidBumpsPos[5][0] = enemySize/2.8f;
  asteroidBumpsPos[5][1] = enemySize/5;
  asteroidBumpsPos[6][0] = -enemySize/2.5f;
  asteroidBumpsPos[6][1] = -enemySize/12;
  asteroidBumpsPos[7][0] = enemySize/15;
  asteroidBumpsPos[7][1] = -enemySize/2.5f;

  mainCreatureSize = width/7;                               // diameter of the main creature

  // mini variables:
  miniX = new float [limit];                                // actual X position of mini creatures
  miniY = new float [limit];                                // actual Y position of mini creatures
  miniSpeed = new float [limit];                            // speed of each individual mini creature
  miniSpeedMultiplier = new float [limit];                  /* miniSpeed is divided by this to ultimately determine
                                                               the appropriate X and Y change for each mini creature 
                                                               in each draw run. When the mini creature is far away 
                                                               from the main creature, this value is low - resulting 
                                                               in a relatively high speed. When the mini creature is close, 
                                                               it consequently moves slower. ('swarming' effect)*/
  miniMinSpeed = 15;
  miniMaxSpeed = 45;
  miniXOffset = new float [limit];                          // sets the mini creature's position relative to the main creature
  miniYOffset = new float [limit];
  miniOffsetLimit = 1.5f*mainCreatureSize;                   // the max offset that the minis can have from the main creature
  miniTime = new float [limit];                             /* the draw runs needed for the mini creature to move from their 
   current position to their goal (their X/Y offset from the main creature) */
  miniXChange = new float [limit];                          // X change of each mini per draw run
  miniYChange = new float [limit];                          // Y change of each mini per draw run 

  miniXDist = new float [limit];                            // X distance from each mini to its desired location
  miniYDist = new float [limit];                            // Y distance from each mini to its desired location
  miniDist = new float [limit];                             // total distance from each mini to their desired location

  //some variables concerning game mode, menu, settings, etc:
  inGame = 0;                
  inMenu = 1;
  inSettings = 2;
  gameOverScreen = 3;


  lastPos = new int [2];                                    // last position of the main creature before game ended
  currentColour = new int [3];                              // current colour of rings and mini creatures
  mainCreatureColours = new int [3][3];                     // stores the three possible colours for the main creature

  mainPink = 0;
  mainGreen = 1;
  mainBlue = 2;

  mainCreatureColours[mainPink][0] = 195;
  mainCreatureColours[mainPink][1] = 15; 
  mainCreatureColours[mainPink][2] = 110;

  mainCreatureColours[mainGreen][0] = 8;
  mainCreatureColours[mainGreen][1] = 138;
  mainCreatureColours[mainGreen][2] = 110;

  mainCreatureColours[mainBlue][0] = 72;
  mainCreatureColours[mainBlue][1] = 15;
  mainCreatureColours[mainBlue][2] = 195; 

  currentMainColour = new int [3];                          //stores the currently selected colour of the main creature
  currentMainColourCheck = mainPink;

  otherColours = new int [3][3];                            //some colours used for menus and fonts

  otherLightPurple = 0;
  otherDarkPurple = 1;
  otherDarkBlue = 2;

  otherColours[otherLightPurple][0] = 200;
  otherColours[otherLightPurple][1] = 60;
  otherColours[otherLightPurple][2] = 250;

  otherColours[otherDarkPurple][0] = 90;
  otherColours[otherDarkPurple][1] = 20;
  otherColours[otherDarkPurple][2] = 180;

  otherColours[otherDarkBlue][0] = 31;
  otherColours[otherDarkBlue][1] = 59;
  otherColours[otherDarkBlue][2] = 196;



  flashingColours = new int [10][3];                                // I stored some flashing colours in this array

  green = 0;
  peach = 1;
  pink = 2;
  blue = 3;
  white = 4;
  lightGreen = 5;
  yellow = 6;
  aqua = 7;
  purple = 8;
  lightYellow = 9;


  flashingColours [green][0] = 109;                          
  flashingColours [green][1] = 255;
  flashingColours [green][2] = 69;

  flashingColours [peach][0] = 255;         
  flashingColours [peach][1] = 157;
  flashingColours [peach][2] = 105;

  flashingColours [pink][0] = 255;          
  flashingColours [pink][1] = 140;
  flashingColours [pink][2] = 180;

  flashingColours [blue][0] = 122;   
  flashingColours [blue][1] = 202;
  flashingColours [blue][2] = 255;

  flashingColours [white][0] = 255;        
  flashingColours [white][1] = 255;
  flashingColours [white][2] = 255;

  flashingColours [lightGreen][0] = 3;       
  flashingColours [lightGreen][1] = 252; 
  flashingColours [lightGreen][2] = 144;

  flashingColours [yellow][0] = 230;   
  flashingColours [yellow][1] = 215;
  flashingColours [yellow][2] = 60;

  flashingColours [aqua][0] = 122;  
  flashingColours [aqua][1] = 255;
  flashingColours [aqua][2] = 253;

  flashingColours [purple][0] = 217;    
  flashingColours [purple][1] = 140;
  flashingColours [purple][2] = 255;

  flashingColours [lightYellow][0] = 245;    
  flashingColours [lightYellow][1] = 230;
  flashingColours [lightYellow][2] = 85;

  currentFlashingColourCheck = (int)(random(0, flashingColours.length)); // used to make sure the flashing colour doesn't repeat itself

  for (int i = 0; i < 3; i += 1) {
    currentColour[i] = flashingColours[currentFlashingColourCheck][i];    // sets currentColour to a random colour from the 'flashingColours' array
    currentMainColour[i] = mainCreatureColours[mainPink][i];
  }

  colourRChange = 0;                                                                  // initializes the colour change per draw run at 0
  colourGChange = 0;
  colourBChange = 0;



  easy = 1.0f;
  medium = 1.5f;
  hard = 2.0f;

  //setting up for game...
  difficultyLevel = easy;
  currentMode = inMenu;
  globalTimeCounter = 0;                                                             // time counter (counts from 0 to 5, then repeats
  colourTimeCounter = drawRunsPerColour;                                             // time counter for colour change (counts from 0 to drawRunsPerColour)
  initializeNebulas();                                                               // initializes position and colour of each nebula
  initializeStars();                                                                 // initializes position of each star
  highscore = 0;
  newHighscore = false;

  leftEdge = 0;
  rightEdge = 0;
  topEdge = 0;
  bottomEdge = 0;
}

public void draw() {   
  if (globalTimeCounter == 5) {                                                      //this counter determines the star and nebula movement
    globalTimeCounter = 0;
  } else {
    globalTimeCounter++;
  }

  drawBackground();
  changeFlashingColours();

  if (currentMode == inMenu) {
    drawMenu();
    drawPlanet(mouseX, mouseY, mainCreatureSize/4, currentMainColour[0], currentMainColour[1], currentMainColour[2]);
  } else if (currentMode == inSettings) {
    drawSettings();
    drawPlanet(mouseX, mouseY, mainCreatureSize/4, currentMainColour[0], currentMainColour[1], currentMainColour[2]);
  } else if (currentMode == gameOverScreen) {
    drawTrail();
    drawAsteroid(enemySize);
    drawSlowMinis(currentColour[0], currentColour[1], currentColour[2]);
    drawPlanet(lastPos[0], lastPos[1], mainCreatureSize, currentMainColour[0], currentMainColour[1], currentMainColour[2]);
    drawFastMinis(currentColour[0], currentColour[1], currentColour[2]);
    drawGameOverScreen();
  } else if (currentMode == inGame) {
    drawTrail();
    drawAsteroid(enemySize);
    drawSlowMinis(currentColour[0], currentColour[1], currentColour[2]);
    drawPlanet(mouseX, mouseY, mainCreatureSize, currentMainColour[0], currentMainColour[1], currentMainColour[2]);
    drawFastMinis(currentColour[0], currentColour[1], currentColour[2]);

    displayScore();
    
    checkForCollision();

    if (!gameOver) {
      moveEnemy();
      moveMinis();
    }

    leftEdge = width+enemyDelay*enemySize;
    rightEdge = -enemyDelay*enemySize;
    topEdge = -enemyDelay*enemySize;
    bottomEdge = height+enemyDelay*enemySize;

    //following if statement checks if the enemy has left the screen and ends the game if the maximum level (limit) has been reached. Otherwise, a new enemy path is generated.

    if ((!gameOver) && ((enemyX >= leftEdge)||(enemyX <= rightEdge)||(enemyY <= topEdge)||(enemyY >= bottomEdge))) {
      if (level == limit) {
        currentMode = gameOverScreen;
        lastPos[0] = mouseX;
        lastPos[1] = mouseY;
        if (level > highscore) {
          highscore = level;
          newHighscore = true;
        }
      } else {
        newEnemyPath();
        level++;
      }
    }
  }
}                                                                                                                                                      


void drawMenu() {
  int loops = 30;
  for (float i = 0; i <= loops; i+= 0.2f) {
    fill(235+((currentColour[0]-235)/loops)*i, 40+((currentColour[1]-40)/loops)*i, 140+((currentColour[2]-140)/loops)*i, 140-i*4.5f);    //draws the 'ASTROSWARM' sign
    textSize((width/25)+i*width/300);
    text("ASTROSWARM", 21*width/60-i*width/95, 14*height/30-i*height/300);
  }
  fill(currentColour[0], currentColour[1], currentColour[2], 255);
  text("ASTROSWARM", 21*width/60-loops*width/95, 14*height/30-loops*height/300); 

  if (globalTimeCounter >= 0 && globalTimeCounter < 3) {
    fill(otherColours[otherLightPurple][0], otherColours[otherLightPurple][1], otherColours[otherLightPurple][2]);
  } else {
    fill(flashingColours[lightYellow][0], flashingColours[lightYellow][1], flashingColours[lightYellow][2]);
  }
  textSize(width/25);
  text("click anywhere to play", 17.5f*width/60, 18*height/30);



  if (mouseX >= width/3 && mouseX <= 2*width/3 && mouseY >= 17*height/20 && mouseY <= 146*height/160) {                    // 'settings' sign
    fill(otherColours[otherLightPurple][0], otherColours[otherLightPurple][01], otherColours[otherLightPurple][2], 80);
  } else {
    fill(otherColours[otherDarkPurple][0], otherColours[otherDarkPurple][01], otherColours[otherDarkPurple][2], 80);
  }

  rectMode(CORNER);
  rect(width/3, 17*height/20, width/3, height/16);

  textSize(height/25);
  fill(otherColours[otherLightPurple][0], otherColours[otherLightPurple][01], otherColours[otherLightPurple][2], 180);
  text("SETTINGS", 1.635f*width/4, 8.95f*height/10);

  textSize(height/30);
  fill(flashingColours[lightYellow][0], flashingColours[lightYellow][1], flashingColours[lightYellow][2], 200);
  String highscoreInfo = "highscore = " + highscore; 
  text(highscoreInfo, width/60, 3*height/60);
}

void drawSettings() {
  rectMode(CORNER);
  fill(otherColours[otherDarkBlue][0], otherColours[otherDarkBlue][01], otherColours[otherDarkBlue][2], 80);
  rect(width/8, 3*height/20, width/4, 2*height/20);
  rect(5*width/8, 3*height/20, width/4, 2*height/20);
  fill(otherColours[otherLightPurple][0], otherColours[otherLightPurple][01], otherColours[otherLightPurple][2], 180);
  textSize(height/25);
  text("DIFFICULTY", 1.1f*width/8, 4.25f*height/20);                                                                        // draws 'difficulty' sign
  text("COLOUR", 5.35f*width/8, 4.25f*height/20);                                                                           // draws 'colours' sign

  int settingsPlanet1Size = 2*height/20;
  int settingsPlanet2Size = 2*height/20;
  int settingsPlanet3Size = 2*height/20;

  if (dist(mouseX, mouseY, 6*width/8, 7*height/20) <= height/20 || currentMainColourCheck == mainPink) {                   // next section = draws planets + light spot below if necessary
    drawNebula(6*width/8, 7*height/20, 3.5f*height/20, 255, 255, 255, 4, 70);
    settingsPlanet1Size = 5*height/40;
  } 
  if (dist(mouseX, mouseY, 6*width/8, 10*height/20) <= height/20 || currentMainColourCheck == mainGreen) {
    drawNebula(6*width/8, 10*height/20, 3.5f*height/20, 255, 255, 255, 4, 70);
    settingsPlanet2Size = 5*height/40;
  }
  if (dist(mouseX, mouseY, 6*width/8, 13*height/20) <= height/20 || currentMainColourCheck == mainBlue) {
    drawNebula(6*width/8, 13*height/20, 3.5f*height/20, 255, 255, 255, 4, 70);
    settingsPlanet3Size = 5*height/40;
  }

  drawPlanet(6*width/8, 7*height/20, settingsPlanet1Size, mainCreatureColours[mainPink][0], mainCreatureColours[mainPink][1], mainCreatureColours[mainPink][2]);
  drawPlanet(6*width/8, 10*height/20, settingsPlanet2Size, mainCreatureColours[mainGreen][0], mainCreatureColours[mainGreen][1], mainCreatureColours[mainGreen][2]);
  drawPlanet(6*width/8, 13*height/20, settingsPlanet3Size, mainCreatureColours[mainBlue][0], mainCreatureColours[mainBlue][1], mainCreatureColours[mainBlue][2]);

  
  stroke(otherColours[otherDarkBlue][0], otherColours[otherDarkBlue][01], otherColours[otherDarkBlue][2], 80);
  strokeWeight(width/100);
  if (mouseX >= width/8 && mouseX <= 3*width/8 && mouseY >= 6*height/20 && mouseY <= 8*height/20 || difficultyLevel == easy) {         // draws 'easy' sign
    fill(otherColours[otherLightPurple][0], otherColours[otherLightPurple][01], otherColours[otherLightPurple][2], 80);
  } else {
    fill(otherColours[otherDarkPurple][0], otherColours[otherDarkPurple][01], otherColours[otherDarkPurple][2], 80);
  }
  rect(width/8, 6*height/20, width/4, 2*height/20);

  if (mouseX >= width/8 && mouseX <= 3*width/8 && mouseY >= 9*height/20 && mouseY <= 11*height/20 || difficultyLevel == medium) {      // draws 'medium' sign
    fill(otherColours[otherLightPurple][0], otherColours[otherLightPurple][01], otherColours[otherLightPurple][2], 80);
  } else {
    fill(otherColours[otherDarkPurple][0], otherColours[otherDarkPurple][01], otherColours[otherDarkPurple][2], 80);
  }
  rect(width/8, 9*height/20, width/4, 2*height/20);

  if (mouseX >= width/8 && mouseX <= 3*width/8 && mouseY >= 12*height/20 && mouseY <= 14*height/20 || difficultyLevel == hard) {        // draws 'hard' sign
    fill(otherColours[otherLightPurple][0], otherColours[otherLightPurple][01], otherColours[otherLightPurple][2], 80);
  } else {
    fill(otherColours[otherDarkPurple][0], otherColours[otherDarkPurple][01], otherColours[otherDarkPurple][2], 80);
  }
  rect(width/8, 12*height/20, width/4, 2*height/20);

  noStroke();
  if (mouseX >= width/3 && mouseX <= 2*width/3 && mouseY >= 17*height/20 && mouseY <= 146*height/160) {                                 // draws 'return to menu' sign
    fill(otherColours[otherLightPurple][0], otherColours[otherLightPurple][01], otherColours[otherLightPurple][2], 80);
  } else {
    fill(otherColours[otherDarkPurple][0], otherColours[otherDarkPurple][01], otherColours[otherDarkPurple][2], 80);
  }
  rect(width/3, 17*height/20, width/3, height/16);


  fill(otherColours[otherLightPurple][0], otherColours[otherLightPurple][01], otherColours[otherLightPurple][2], 180);
  text("easy", 1.3f*width/8, 7.25f*height/20);
  text("medium", 1.3f*width/8, 10.25f*height/20);
  text("hard", 1.3f*width/8, 13.25f*height/20);
  text("return to menu", 1.42f*width/4, 8.95f*height/10);
}

void drawGameOverScreen() {
  fill(0.1f*(otherColours[otherDarkBlue][0]), 0.1f*(otherColours[otherDarkBlue][1]), 0.1f*(otherColours[otherDarkBlue][2]), 120);
  rectMode(CORNER);
  noStroke();
  rect(width/20, height/20, 9*width/10, 9*height/10);
  
  fill(otherColours[otherLightPurple][0], otherColours[otherLightPurple][1], otherColours[otherLightPurple][2], 255);                      //draws "game over" and "you won" signs
  textSize(width/10);
  if (gameOver) {
    text("GAME OVER", 12*width/60, 15*height/30);
  } else {
    text("YOU WON!", 13*width/60, 15*height/30);
  }
  textSize(width/15);                                                                                                              //displays score
  String levelinfo = "score = " + level;
  fill(flashingColours[white][0], flashingColours[white][1], flashingColours[white][2]);
  text(levelinfo, 19*width/60, 19*height/30);

  fill(otherColours[otherLightPurple][0], otherColours[otherLightPurple][1], otherColours[otherLightPurple][2]);
  textSize(width/23);
  text("click anywhere to return to menu", 8*width/60, 23*height/30);

  if (newHighscore) {                                                                                                              //displays "new highscore" if appropriate
    if (globalTimeCounter >= 0 && globalTimeCounter < 3) {
      fill(otherColours[otherLightPurple][0], otherColours[otherLightPurple][1], otherColours[otherLightPurple][2]);
    } else {
      fill(flashingColours[lightYellow][0], flashingColours[lightYellow][1], flashingColours[lightYellow][2]);
    }
    textSize(width/25);
    text("NEW HIGHSCORE", 19.5f*width/60, 8*height/30);
  }
}

void displayScore() {
  textSize(height/30);
  fill(flashingColours[lightYellow][0], flashingColours[lightYellow][1], flashingColours[lightYellow][2], 200);
  String CurrentLevelInfo = "current score = " + level; 
  text(CurrentLevelInfo, width/60, 3*height/60);
}

void drawBackground() {
  background(0);
  for (int i = 0; i < nebulaNumber; i++) {
    drawNebula(nebulaPos[0][i], nebulaPos[1][i], 9*width/12, nebulaColours[i][0], nebulaColours[i][1], nebulaColours[i][2], 2, 100);
    if (globalTimeCounter == 0){
      nebulaPos[0][i]+= 1;
      if (nebulaPos[0][i] >= 1.5f*width) {
        nebulaPos[0][i] = random(-2.5f*width, -0.5f*width);
        nebulaPos[1][i] = random(-0.25f*height, 1.25f*height);
        nebulaColours[i][0] = (int)(random(100, 256));
        nebulaColours[i][1] = 66;
        nebulaColours[i][2] = (int)(random(100, 256));
      }
    }
  }
  drawStars();
}

void drawNebula(float x, float y, float size, float colour1, float colour2, float colour3, float opacity, float sharpness) { //opacity sets opacity of Nebula, sharpness sets how small the ellipses get
  noStroke();
  fill(colour1, colour2, colour3, opacity);
  for (int i = (int)(size); i > size-(size/255)*sharpness; i-=size/40) {
    ellipse(x, y, i, i);
  }
}

void drawStars() {
  for (int i = 0; i < starNumber; i++) {
    int white = color (255);
    set((int)(starPos[0][i]), (int)(starPos[1][i]), white);
    if (globalTimeCounter%2 == 0) {
      starPos[0][i]+= 1;
      if (starPos[0][i] >= width) {
        starPos[0][i]-= width;
      }
    }
  }
}


void drawPlanet(float x, float y, int size, int R, int G, int B) {  

  stroke(20);
  strokeWeight(size/60);
  fill(0.8f*R, 0.8f*G, 0.8f*B);
  circle(x, y, size);

  noStroke();
  fill(currentColour[0], currentColour[1], currentColour[2], 100);
  for (int i = 0; i >= -3; i -= 1) {
    ellipse(x, y, (2+i*0.2f)*size, (0.5f+i*0.05f)*size);
  }

  fill(0.8f*R, 0.8f*G, 0.8f*B, 255);
  ellipse(x, y, size, 0.2f*size);

  stroke(20);
  strokeWeight(size/60);
  arc(x, y, size, size, PI, 2*PI);
}

void changeFlashingColours() {

  if (colourTimeCounter == drawRunsPerColour) {

    int nextColour = currentFlashingColourCheck;
    while (currentFlashingColourCheck == nextColour) {
      nextColour = (int)(random(0, flashingColours.length));
    }
    currentFlashingColourCheck = nextColour;
    colourRChange = (flashingColours[nextColour][0] - currentColour[0])/drawRunsPerColour;
    colourGChange = (flashingColours[nextColour][1] - currentColour[1])/drawRunsPerColour;
    colourBChange = (flashingColours[nextColour][2] - currentColour[2])/drawRunsPerColour;
  }

  currentColour[0] = currentColour[0] + colourRChange;
  currentColour[1] = currentColour[1] + colourGChange;
  currentColour[2] = currentColour[2] + colourBChange;

  if (colourTimeCounter == drawRunsPerColour) {                                                                             //counter of draw runs for planet ring colour change
    colourTimeCounter = 0;
  } else {
    colourTimeCounter++;
  }
}


void drawAsteroid(int size) {
  strokeWeight(size/30);
  stroke(20);
  fill(80);
  for (int i = 0; i < 5; i+=1) {
    circle(enemyX+asteroidBumpsPos[i][0], enemyY+asteroidBumpsPos[i][1], size/4);
  }

  strokeWeight(size/50);
  fill(60);
  for (int i = 5; i < asteroidBumpsPos.length; i+=1) {
    circle(enemyX+asteroidBumpsPos[i][0], enemyY+asteroidBumpsPos[i][1], size/4);
  }

  strokeWeight(size/50);
  fill(80);
  circle(enemyX, enemyY, size);

  noStroke();
  for (int i = 0; i < 5; i+=1) {
    circle(enemyX+asteroidBumpsPos[i][0], enemyY+asteroidBumpsPos[i][1], size/4);
  }

  drawNebula(enemyX, enemyY, size, 10, 10, 10, 15, 150);
}

void drawTrail() {                                                                                                       //draws a trail for the enemy
  for (float i = 6; i > 0; i-= 0.10) {
    noStroke();
    fill(255, 120, 100+i*20, 16-i*2.5f);
    circle(enemyX-i*enemyXChange, enemyY-i*enemyYChange, enemySize);
  }
}

void drawSlowMinis(int R, int G, int B) {                                                                                    //draws the mini creatures
  for (int i = 0; i < level; i++) {
    if (miniSpeed[i] <= miniMinSpeed+0.5*(miniMaxSpeed-miniMinSpeed)){
      drawPlanet(miniX[i], miniY[i], mainCreatureSize/5, R, G, B);
    }
  }
}

void drawFastMinis(int R, int G, int B) {                                                                                    //draws the mini creatures
  for (int i = 0; i < level; i++) {
    if (miniSpeed[i] > miniMinSpeed+0.5*(miniMaxSpeed-miniMinSpeed)){
      drawPlanet(miniX[i], miniY[i], mainCreatureSize/5, R, G, B);
    }
  }
}

void moveEnemy() {                                                                                                        // moves the enemy   
  enemyX = enemyX + enemyXChange;
  enemyY = enemyY + enemyYChange;
}

void miniCalc() {                                                                                                        // calculations concerning mini movement
  for (int i = 0; i < level; i++) {

    miniDist[i] = dist(miniX[i], miniY[i], mouseX+miniXOffset[i], mouseY+miniYOffset[i]);
    miniSpeedMultiplier[i] = 0.5f*width/miniDist[i];
    miniXDist[i] = mouseX+miniXOffset[i] - miniX[i];
    miniYDist[i] = mouseY+miniYOffset[i] - miniY[i];

    miniTime[i] = miniDist[i]/(miniSpeed[i]/miniSpeedMultiplier[i]);

    miniXChange[i] = miniXDist[i]/miniTime[i];
    miniYChange[i] = miniYDist[i]/miniTime[i];
  }
}


void moveMinis() {                                                                                                        // moves the mini creatures
  miniCalc();
  for (int i = 0; i < level; i++) {
    if ((miniDist[i] > miniXChange[i]) && (miniDist[i] > miniYChange[i])) {
      miniX[i] = miniX[i] + miniXChange[i];
      miniY[i] = miniY[i] + miniYChange[i];
    }
  }
}



void checkForCollision() {                                                                                                         // checks for collision with the enemy
  if (dist(mouseX, mouseY, enemyX, enemyY) <= (mainCreatureSize/2 + enemySize/2)) { //(mainCreatureSize/2 + enemySize/2)) {       // checking main creature
    gameOver = true;
  }
  for (int i = 0; i < level; i ++) {                                                                                               // checking minis
    if (dist(miniX[i], miniY[i], enemyX, enemyY) <= (mainCreatureSize/10 + enemySize/2)) {
      gameOver = true;
    }
  }
  /* if a collision is detected, the game mode is set to gameOverScreen 
   and the last position of the mouse is saved */
  if (gameOver) {                                                                                                                  
    currentMode = gameOverScreen;
    if (level > highscore) {
      highscore = level;
      newHighscore = true;
    }
    lastPos[0] = mouseX;
    lastPos[1] = mouseY;
  }
}


void newEnemyPath() {
  /* assigning an edge for the enemy to start from and a 
   random X and Y coordinate on that edge */
  enemyStartingEdge = (int)(random(0, 4));
  if (enemyStartingEdge == 0) {
    sXPos = (int)(random(width/10, 9*width/10));
    sYPos = - enemySize/2;
  } else if (enemyStartingEdge == 1) {
    sXPos = width + enemySize/2;
    sYPos = (int)(random(height/10, 9*height/10));
  } else if (enemyStartingEdge == 2) {
    sXPos = (int)(random(width/10, 9*width/10));
    sYPos = height + enemySize/2;
  } else if (enemyStartingEdge == 3) {
    sXPos = - enemySize/2;
    sYPos = (int)(random(height/10, 9*height/10));
  }
  /* assigning an edge for the enemy to end at and a 
   random X and Y coordinate on that edge */
  enemyEndEdge = enemyStartingEdge;

  while (enemyEndEdge == enemyStartingEdge) {
    enemyEndEdge = (int)(random(0, 4));
  }

  if (enemyEndEdge == 0) {
    eXPos = (int)(random(0, width));
    eYPos = - enemySize/2;
  } else if (enemyEndEdge == 1) {
    eXPos = width + enemySize/2;
    eYPos = (int)(random(0, height));
  } else if (enemyEndEdge == 2) {
    eXPos = (int)(random(0, width));
    eYPos = height + enemySize/2;
  } else if (enemyEndEdge == 3) {
    eXPos = - enemySize/2;
    eYPos = (int)(random(0, height));
  }  

  /* assigning a speed for the enemy, then calculating the overall distance, 
   x distance and y distance between the end and starting positions for this enemy 
   - then using the speed to determine how large the x and y increments have to be for the enemy to 
   move at this speed */

  genSpeed = random(7, 15)*difficultyLevel*(1+(2.0f/limit)*level);              // the speed of each enemy is randomly generated and then modified according to the selected difficulty and current level

  distanceXY = dist(sXPos, sYPos, eXPos, eYPos);
  distanceX = eXPos - sXPos;
  distanceY = eYPos - sYPos;

  time = distanceXY/genSpeed;

  enemyXChange = distanceX/time;
  enemyYChange = distanceY/time;

  /* enemyX and Y are set equal to the starting position and a delay after the enemy 
   has left the screen is randomly generated */

  enemyX = sXPos; 
  enemyY = sYPos;
  enemyDelay = (int)(random(3, 5)*difficultyLevel*(1+(1.0/limit)*level));
}

public void mousePressed() {
  if (currentMode == inMenu) {                                                                                //inMenu
    if (mouseX >= width/3 && mouseX <= 2*width/3 && mouseY >= 17*height/20 && mouseY <= 146*height/160) {
      currentMode = inSettings;
    } else {
      startNewGame();
    }
  } else if (currentMode == gameOverScreen) {                                                                  //gameOverScreen
    currentMode = inMenu;
  } else if (currentMode == inSettings) {                                                                      //inSettings
    if (mouseX >= width/3 && mouseX <= 2*width/3 && mouseY >= 17*height/20 && mouseY <= 146*height/160) {
      currentMode = inMenu;
    } else if (mouseX >= width/8 && mouseX <= 3*width/8 && mouseY >= 6*height/20 && mouseY <= 8*height/20) {
      difficultyLevel = easy;
    } else if (mouseX >= width/8 && mouseX <= 3*width/8 && mouseY >= 9*height/20 && mouseY <= 11*height/20) {
      difficultyLevel = medium;
    } else if (mouseX >= width/8 && mouseX <= 3*width/8 && mouseY >= 12*height/20 && mouseY <= 14*height/20) {
      difficultyLevel = hard;
    } else if (dist(mouseX, mouseY, 6*width/8, 7*height/20) <= height/20) {
      for (int i = 0; i < 3; i+=1) {
        currentMainColour[i] = mainCreatureColours[mainPink][i];
        currentMainColourCheck = mainPink;
      }
    } else if (dist(mouseX, mouseY, 6*width/8, 10*height/20) <= height/20) {
      for (int i = 0; i < 3; i+=1) {
        currentMainColour[i] = mainCreatureColours[mainGreen][i];
        currentMainColourCheck = mainGreen;
      }
    } else if (dist(mouseX, mouseY, 6*width/8, 13*height/20) <= height/20) {
      for (int i = 0; i < 3; i+=1) {
        currentMainColour[i] = mainCreatureColours[mainBlue][i];
        currentMainColourCheck = mainBlue;
      }
    }
  }
}

void startNewGame() {
  level = 0;                                                                              // level is reset to 0
  newHighscore = false;
  gameOver = false;                                                                       // gameover is reset to false
  newEnemyPath();
  initializeMinis();                                                                      // a new position for each mini creature is created
  currentMode = inGame;                                                                   // mode is set to inGame
}

void initializeNebulas() {                                                                 // initializes nebula position (only runs once, in setup)
  for (int i = 0; i < nebulaNumber; i++) {
    nebulaPos[0][i] = random(-0.5f*width, 1.5f*width);
    nebulaPos[1][i] = random(-0.25f*height, 1.25f*height);
  }
  for (int i = 0; i < nebulaNumber; i++) {
    nebulaColours[i][0] = (int)(random(100, 256));
    nebulaColours[i][1] = 66;
    nebulaColours[i][2] = (int)(random(100, 256));
  }
}

void initializeStars() {                                                                   // initializes position of stars (only runs once, in setup)
  for (int i = 0; i < starNumber; i++) {                       
    starPos[0][i] = random(0, width);   //sets star x values
    starPos[1][i] = random(0, height);  //sets star y values
  }
}

void initializeMinis() {                                                                   // creates a new set of positions for the mini creatures (runs each time a new game starts)
  for (int i = 0; i < limit; i++) {                                
    miniX[i] = width/2;
    miniY[i] = height/2; 
    miniXOffset[i] = 0;
    miniYOffset[i] = 0;
    while (dist(miniXOffset[i], miniYOffset[i], 0, 0) >= miniOffsetLimit || dist(miniXOffset[i], miniYOffset[i], 0, 0) <= mainCreatureSize) {
      miniXOffset[i] = random(-miniOffsetLimit, miniOffsetLimit);
      miniYOffset[i] = random(-miniOffsetLimit, miniOffsetLimit);
    }
    miniSpeed[i] = random(miniMinSpeed, miniMaxSpeed+1);
  }
}

}