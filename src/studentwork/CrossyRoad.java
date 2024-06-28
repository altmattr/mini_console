package studentwork;

/* autogenerated by Processing revision 1293 on 2024-06-28 */
import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class CrossyRoad extends mqapp.MQApp {

//Sienna Grove, 47120851 
//[X] I declare that I have not seen anyone else's code
//[X] I declare that I haven't shown my code to anyone else.
final int N_LANES = 3; 
final int N_CARS_IN_LANE = 10;
final int MIN_GAP = 50; 
final int MAX_LIVES = 3; 
final int WIN_SCORE = 5; 

int amountOfSharks = N_CARS_IN_LANE * N_LANES;

float [] sharkX = new float[amountOfSharks]; 
float [] sharkY = new float [amountOfSharks]; 
float [] sharkSpeed = new float [amountOfSharks];

float surfX, surfY, surfDia;
float sharkLength, sharkBodyLength;
float sharkGap;
float laneGap;
float sharkHeight = laneGap/3;// ensures by shark fits into the lanes 
int bakersDia;
int currentScore = 0;
int currentLives = MAX_LIVES;
int allSharksOnScreen = amountOfSharks+11;
int lanes = N_LANES;

public void setup() {
  background(0xFF52B3C9);
  /* size commented out by preprocessor */;
  init ();
}

public void init () {
  laneGap = (height/2)/ N_LANES; // laneGap is used in all places needing scalabilitiy 
  surfX = width/2;
  /* 
  surfY starting location, will
  be off screen unless more than 
  2 lanes, so less than needs their own formula
  */
  
  surfY = (height/2) + laneGap*2 + laneGap/2; 
  if (lanes == 1) {
            surfY = (height/2) + laneGap/2;
  } 
  if (lanes == 2) {
            surfY= (height/2) + laneGap+(laneGap/2);
  }
  surfDia = 15; 
  sharkHeight = laneGap/3;
  bakersDia = width/50;
  sharkBodyLength = sharkHeight * 3;
  sharkLength = sharkBodyLength + (sharkBodyLength*0.9f);
  sharkGap= MIN_GAP+sharkLength;
  float minSpeed = 3;
  float maxSpeed = 7;
  int countToLanes=0;
  
  for (int i=0; i<amountOfSharks; i++) { 
      sharkSpeed [i] = random (minSpeed, maxSpeed);  // sharks speed randomised
      sharkY [i] = (laneGap/2) + (countToLanes*laneGap);  //sharks Y value inside lanes
      countToLanes++; 
      if (countToLanes >= N_LANES) { // sharks are only in the places there are lanes 
                        countToLanes = 0;
      }
      sharkX[i] = i*(-sharkGap)- sharkLength; // sharks starting values spread out 
  }
}

public void draw () { 
  myBeachBackground();

  drawMyLanes();

  drawMySurfer (surfX, surfY);

  drawAndMoveMySharks ();

  collisionDetectionShark();

  scoreIncrease ();

  livesDecrease ();
}

public void drawAndMoveMySharks () {

  for (int i = 0; i < amountOfSharks; i++) {
    drawMyShark (sharkX[i], sharkY[i]);
    sharkX[i] += sharkSpeed [i]; // shark increases by the speed assigned to it 
     
     /* 
     if all sharks havent been on screen 
     atleast once, sharks must reset
     behind the furtherest original shark
     otherwise overlapping occurs, once theyve 
     all been on screen they start closer
     */
     
     if (sharkX [i] >= width+sharkLength) {
                    if (allSharksOnScreen>1) {
                          sharkX[i] = amountOfSharks*(-sharkGap);
                          allSharksOnScreen--;
                    }
                    else {
                        sharkX[i] = 2*(-sharkGap); 
                    }
     }
  }
}

public void collisionDetectionShark () {
  /* 
   compares an array item with 
   the one infront of it. If array item 1 
   is too close to item 2, item 1 
   speed equals item 2 speed.
   */
  for (int i = 0; i < amountOfSharks; i++) { 
            for (int k = 0; k < amountOfSharks; k++) {
                      if (sharkY[i] == sharkY[k]) { // ensuring they sharks are in the same lane
                                    if (sharkX[k] > (sharkX[i]-sharkGap) && (sharkX[k] < sharkX[i]+sharkLength/2)){ // shark is closer than chosen gap, but not further ahead than the shark infront
                                                  sharkSpeed[k] = sharkSpeed[i] ;
                                    }
                        }
            }  
  }
}

public void drawMySurfer (float a, float b) {
  int legLength= 10;
  float headDia= laneGap/5; 
  //board
  fill (0xFFC952B3, 250);// high opacity 
  strokeWeight (0.5f);
  noStroke ();
  ellipse (a, b, laneGap/3, laneGap); 
  //body
  stroke (0);
  strokeWeight (1);
  line (surfX, surfY-headDia/2, surfX, surfY+(laneGap/3.5f)); 
  //head
  fill (0xFFC952B3);
  circle (surfX, b-headDia, headDia);
  //legs
  line (surfX, surfY+(laneGap/3.5f), surfX+legLength/2, surfY+(laneGap/3.5f)+5); 
  line (surfX, surfY+(laneGap/3.5f), surfX-legLength/2, surfY+(laneGap/3.5f)+5);
  //arms
  float arms= (dist (surfX, surfY-laneGap/10, surfX, surfY+(laneGap/3.5f)))/2;  // ensures surfer arms are in the center of the body no matter the size
  line (surfX-surfDia/2, (surfY-surfDia+surfDia/2)+ arms, surfX+surfDia/2, (surfY-surfDia+surfDia/2)+ arms);
}

/* 
 this is the surfers movement, all
 movements are by the size of the 
 lanes and will only occur if the 
 surfer is on screen
 */

public void keyPressed () { 
  if (keyCode == UP) {
              surfY=surfY-laneGap;
  }
  if (keyCode == DOWN &&surfY <= height-laneGap) {
              surfY=surfY+laneGap;
  }
  if (keyCode == LEFT&& surfX >= (laneGap/3)/2) {
              surfX=surfX-laneGap;
  }
  if (keyCode == RIGHT && surfX <= width-(laneGap/3)/2) {
              surfX = surfX + laneGap;
  }
}

public void drawMyLanes () {
  int lanes=2; // minimum amount of lanes,  1 line= 2 lanes 
  float lineDia=20;
  float lineY= 0;
  /*
  this creates the lanes scalable to
   the final N_LANES,lanes will be 
   within the top half of the screen
   and increase by laneGap. 
   */
  for (lanes = 2; lanes <= N_LANES+2; lanes++) { // 
              int dashedOdd=0; 
              for (float lineX = (lineDia/2); lineX < width; lineX = lineX + lineDia) {  //dotted lane design
                            if (dashedOdd % 2 == 0) {
                                              fill (0xFF7FB8F2, 180); // decreased opacity 
                                              noStroke ();
                                              arc(lineX, lineY, lineDia, lineDia, 0, PI, OPEN); 
                             }
                             else {
                                noFill (); // clear
                                noStroke ();
                                arc(lineX, lineY, lineDia, lineDia, 0, PI, OPEN);
                             } 
                             dashedOdd++;
              }
              lineY = lineY  +laneGap;
  }
}

public void livesDecrease () {
  for (int i = 0; i < sharkY.length; i++) { 
            if (dist (sharkX[i], sharkY[i], surfX, surfY) <= sharkLength/2) { //collision detection between surfer and sharks
                     currentLives = currentLives-1; 
                     if (currentLives <= 0) { // if out of lives and collision occurs game over
                                      background (255, 0, 0);
                                      textAlign (CENTER);
                                      fill (0);
                                      textSize (140);
                                      text ("Game over", width/2, height/2); 
                                      textSize (90);
                                      text ("Better luck next time", width/2, 3*height/4);
                                      strokeWeight (5);
                                      line (width/4, height/2+5, 3*width/4, height/2+5);
                                      fill (0xFF1C7FFC);
                                      noLoop ();
                     } 
                     else {
                        surferRestart ();
                     }
            }  
  }
}

public void scoreIncrease () {
  if (surfY-25 <= 0) { //surfer passed all the lanes
               currentScore=currentScore+1; // score increases by one 
               surferRestart ();
  }
  if (currentScore >= WIN_SCORE) {  // max score reached
                    background (0xFFB0FA62); // winner screen
                    textAlign (CENTER);
                    fill (0);
                    textSize (140);
                    text ("Congratulations!!", width/2, height/2);
                    textSize (90);
                    text ("You win!!!", width/2, 3*height/4);
                    strokeWeight (5);
                    fill (0xFF1C7FFC);
                    noLoop ();
  }
}

public void myBeachBackground () { // all display and background effects 
  background(0xFF52B3C9);
  drawMyShore ();
  posMyBakers ();
  scoreAndLiveDisplay ();
}

//functions inside other functions 

public void drawMyShark (float x, float y) {
  int finns = 4;
  int eyes = 3;
  strokeWeight (1);
   fill (0xFF1C7FFC);
   // tail
   stroke(0);
   triangle (x-laneGap/3, y+finns, x-laneGap/3, y-finns, x-(sharkBodyLength*0.9f), y);
   //body
   stroke (1);
   ellipse (x, y, sharkBodyLength, sharkHeight);
   ellipse (x-3, y, sharkHeight/2, 3);
   //finns
   triangle (x-finns, y-sharkHeight/2, x+finns, y-sharkHeight/2, x-finns/2,y-sharkHeight); 
   triangle (x-finns, y+sharkHeight/2, x+finns, y+sharkHeight/2, x-finns/2, y+sharkHeight); 
   //eyes 
   fill (255);
   circle (x+sharkHeight, y-eyes, eyes);
   circle (x+sharkHeight, y+eyes, eyes);
}




public void surferRestart () { //make sure surfer is reset to the correct location

  surfX = width/2;
  surfY = height/2 + laneGap*2 + laneGap/2; 
  // the size of the sharks and surfers are pretty big and thus the sizing doesnt work without new parameters
  if (lanes == 2) {
           surfY= (height/2) + laneGap + (laneGap/2);
  }  
  if (lanes == 1) {
            surfY = (height/2) + laneGap/2;
  }
}

public void drawMySunbakers (float backersPosX, float bakersPosY) {
  // towel 
  fill (255);
  noStroke();
  rect (backersPosX, bakersPosY, bakersDia, bakersDia*1.9f); 
  //head
  stroke(0);
  circle (backersPosX+12, bakersPosY+20-surfDia, surfDia);
  //body
  stroke (0);
  strokeWeight (1);
  line (backersPosX+12, bakersPosY+20-surfDia+surfDia/2, backersPosX+12, bakersPosY+10+20); 
  line (backersPosX+12, bakersPosY+20+10, backersPosX+12+5, bakersPosY+20+surfDia);
  line (backersPosX+12, bakersPosY+20+10, backersPosX+12-5, bakersPosY+20+surfDia);
  line (backersPosX+12-surfDia/2, bakersPosY+20+3, backersPosX+12+surfDia/2, bakersPosY+20+3);
}

public void posMyBakers () {
 float bakersY = 3.6f*(height/4);

  for (int bakersX = (bakersDia/2); bakersX < width; bakersX = bakersX + (bakersDia+100)) { // all the sunbakers are spread out
           drawMySunbakers (bakersX, bakersY);
  }
}

public void drawMyShore () { 
  float shoreY, shoreX, shoreDia;
  shoreDia= 20; 
  shoreY= 3.5f*height/4;

  fill (0xFFEECFAD);
  noStroke ();
  //sand
  rect (0, 3.5f* height/4, width, 0.5f *height/4); 
  // whitewash
  for (shoreX = (shoreDia/2); shoreX < width; shoreX=shoreX+shoreDia+2) { //ellipses touch
                fill (255);
                noStroke ();
                ellipse(shoreX, shoreY, shoreDia, shoreDia/4);
  }
}

public void scoreAndLiveDisplay () {
  textAlign (CENTER);
  fill (0);
  text ("Score = " + currentScore, 3.8f*width/4, 3.1f* height/4); // score displayed in the bottom corner
  text ("lives left = " + currentLives, 3.8f*width/4, 3.3f* height/4); // lives left displayed in the bottom corner
}


  public void settings() { size(1200, 400); }

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] {"--full-screen", "--bgcolour=#66666", "studentwork.CrossyRoad"};
    runSketch(appletArgs, new CrossyRoad());
  }
}

