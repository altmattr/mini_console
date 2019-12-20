package studentwork;

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

public class BlackHole extends mqapp.MQApp {

/* Assignment 2 (COMP115). S1, 2015.
   By Rifhad Mahbub (SID: 44647662). */

int score;
int starCount = 100;
int shipRed = 127, shipGreen = 127, shipBlue = 127;
int cameraDisplacementX, cameraDisplacementY;
int topScore = -1;
int startTime, endTime;
int gameState; // '0' Start Screen, '1' Main Game, '2' End Screen.
float playerX, playerY;
float direction;
float increment;
float speed;
float wormX, wormY;
float wormDiameter;
float wormDiamChange; // Rate of wormhole diameter change.
float [] starX = new float[starCount]; // Ready for 100 stars.
float [] starY = new float[starCount];
float [] starSize = new float[starCount]; // Star sizes vary randomly too.
boolean gameSaved;
boolean showMenu = true;
boolean clickChangeEnabled = false;
String playerName;
String topPlayer;
String leaderBoardLoc = "./data/leaderboard.csv";
Table hiScores;
boolean localSave = false; //DEFAULT FALSE. Only set to true if the leaderBoardLoc is valid.
//Item related variables below.
int randomNumber;
int eventExpireTime = 10000; // milliseconds. Item effects last this long.
int scoreMultiplier, scoreUp; // Use of scoreUp variable is needed to prevent floating text display glitch. (Otherwise text can change to incorrect value while still displayed).
float itemX, itemY;
float itemStartTime1, itemStartTime2, itemStartTime3;
float wormCollisionTime, floatingTextX, floatingTextY; // Used for floating score text.
boolean rngEventsEnabled = true; //Debug setting. Default value: true. Set to false to disable items.
boolean speedSlowed;
boolean scoreMultiplierEnabled;
boolean shielded;
//End variable declarations.

// Check wormhole and player collision.
public boolean warp(){
  return (dist(wormX,wormY,playerX,playerY) <= (wormDiameter/2 + 10));
}

// Function to draw a blackhole and check collision.
public void blackHoleAt(float holeX, float holeY){
  strokeWeight(1);
  stroke(255);
  fill(0);
  ellipse(holeX,holeY,40,40);
  if (dist(holeX,holeY,playerX,playerY) <= (40/2 + 10) && (!shielded)){ // Also checks if shielded (added feature), if not, then game over.
    gameState = 2;
    endTime = millis();
  }
}

public void setup(){
  size(600,600);
  playerX = 300;
  playerY = 300;
  direction = 0;
  increment = 1;
  speed = 5;
  wormDiameter = 0;
  wormDiamChange = 1;
  score = 0;
  floatingTextX = -1;
  floatingTextY = -1;
  gameSaved = false;
  playerName = "";
  if (localSave){
    hiScores = loadTable(leaderBoardLoc, "header"); //Obtain the data from leaderboard.csv, store it in a table called hiScores.
  }

  resetCameraPos(); // Reset star movement limits.
  randomiseWormPos(); // Set the wormhole's position.
  randomiseStars();
  
  setRNG(); // Set random numbers associated with item spawn chance and item spawn location.
  speedSlowed = false;
  scoreMultiplierEnabled = false;
  shielded = false;
  scoreMultiplier = 1;
  scoreUp = 0;
  
  // Only show the main menu on the first launch and when user requested from the end screen.
  if (showMenu){
    gameState = 0;
  }
  else{
    gameState = 1;
  }
}

public void draw(){
  // Pre-Game State. Main Menu.
  if (gameState == 0){
    background(0);
    
    textAlign(LEFT);
    strokeWeight(1);
    fill(255);
    textSize(15);
    text("Amount of Stars: " + starCount,90,100);
    starCount = toggleIntButton(255,85,10,starCount,1,0,200);
    
    fill(255);
    text("Items Enabled: " + rngEventsEnabled, 90,130);
    rngEventsEnabled = toggleBoolButton(245,118,15,rngEventsEnabled);
    
    fill(255);
    text("Direction Change On Click: " + clickChangeEnabled, 90, 160);
    clickChangeEnabled = toggleBoolButton(340,148,15,clickChangeEnabled);
    
    fill(255);
    text("Ship Color: Red(" + shipRed + ")     Green(" + shipGreen + ")        Blue(" + shipBlue + ")",90,190);
    shipRed = toggleIntButton(255,210,10,shipRed,1,0,255);
    shipGreen = toggleIntButton(285,210,10,shipGreen,1,0,255);
    shipBlue = toggleIntButton(315,210,10,shipBlue,1,0,255);
    
    textAlign(CENTER);
    fill(255);
    text("Ship Preview",width/2,280);
    noFill();
    rect(220,250,160,160);
    displaySpaceShip(width/2,330);
    
    fill(255);
    
    if (mouseX >= 210 && mouseX <= 395 && mouseY >= 530 && mouseY <= 550){
      textSize(25);
      text("Click here to start!",width/2,550);
    }
    else{
      textSize(18);
      text("Press ENTER to start!",width/2,550);
    }
    
  }
  // Main Game State.
  if (gameState == 1){
    background(0);
    textAlign(LEFT);
  
    playerX = playerX + speed * cos(direction);
    playerY = playerY + speed * sin(direction);
    direction = direction + increment * 0.03f;

    handleBorderCollision(); // HANDLE IT!
  
    // Draw stars.
    for (int i = 0; i < starCount; i++){
      noStroke();
      fill(255);
      ellipse(starX[i],starY[i],starSize[i],starSize[i]);
    }
    
    // Draw wormhole.
    strokeWeight(1);
    stroke(255,100,0);
    fill(255-(255*wormDiameter/80),0,255*wormDiameter/80); // Color varies dependant on diameter.
    wormDiameter += wormDiamChange;
    if (wormDiameter < 0 || wormDiameter > 80){
      wormDiamChange = -wormDiamChange;
      wormDiameter += 2*wormDiamChange; // Stops two 0's or two 80's from occuring in a row.
    }
    ellipse(wormX,wormY,wormDiameter,wormDiameter);
    
    // Handles all random item related events.
    if (rngEventsEnabled){
      rngEvent(); 
    }
    
    // Draw blackhole & check collision.
    blackHoleAt(100,40);
    blackHoleAt(400,500);
    
    // Draw spaceship.
    displaySpaceShip(playerX,playerY);
    if (shielded){
      noFill();
      strokeWeight(3);
      stroke(240,70,240);
      ellipse(playerX,playerY,30,30);
    }
  
    // On wormhole collision.
    if (warp()){
      resetCameraPos();
      randomiseWormPos();
      randomiseStars();
      setRNG();
      
      scoreUp = scoreMultiplier; // scoreUp variabls stops floating text from showing the wrong number if the floating text is still visible after score multiplier expires.
      score += scoreUp;
      
      wormCollisionTime = millis(); // Used for floating text timer.
      floatingTextX = playerX;
      floatingTextY = playerY;
    }
    
    // Floating Score Text.
    floatingText(scoreUp);
  
    // Score Display.
    textSize(16);
    fill(255);
    text("Score: " + score,10,590);
    
    // Event Display.
    displayItemTimers();
  }
  
  // Game Over State.
  else if (gameState == 2){
    endGame();
  }
}

// Function to draw spaceship.
public void displaySpaceShip(float posX, float posY){
    stroke(255);
    fill(shipRed,shipGreen,shipBlue);
    ellipse(posX,posY,20,20);
    fill(0,90,200);
    ellipse(posX,posY,8,8);
}

// Function to handle collision with borders.
public void handleBorderCollision(){
  if (playerX < 0){
    playerX = width;
  }
  if (playerX > width){
    playerX = 0;
  }
  if (playerY < 0){
    playerY = height;
  }
  if (playerY > height){
    playerY = 0;
  }
}

// Function to set random star positions, AND random star sizes (added feature).
public void randomiseStars(){
  for (int i = 0; i < starCount; i ++){
    starX[i] = random(0,width);
    starY[i] = random(0,height);
    starSize[i] = random(1.0f,4.0f);
  }
}

// Function to set random wormhole position.
public void randomiseWormPos(){
  wormDiameter = 0;
  wormX = random(0,width);
  wormY = random(0,height);
}

// Function called when the game is over.
public void endGame(){
  background(0);
  textAlign(CENTER);
  stroke(150);
  strokeWeight(2);
  noFill();
  rect(60,20,480,560);
  
  fill(100,255,250);
  textSize(20);
  text("Play again? Press '1'",width/2,60);
  text("Return to the Main Menu? Press '2'",width/2,80);
  text("That game took you " + (endTime-startTime)/1000 + " seconds!",width/2,100);
  
  fill(255);
  textSize(40);
  text("Game Over",width/2,240);
  textSize(20);
  text("Final Score: " + score,width/2,280);
  
  textSize(20);
  text("Enter your name (4 letters): "+ playerName,width/2,340);
  
  if (playerName.length() == 4){
    fill(255);
    text("Press ENTER to save your score.",width/2,440);
  }
  
  if (gameSaved == true){
    fill(255);
    text("SAVED!",width/2,480);
    //Show top player.
    fill(100,255,250);
    textSize(20);
    text(topPlayer + " has the best score of " + topScore + ".",width/2,540);
  }
}

// Function to save the game.
public void saveGame(){
  if (localSave){
    TableRow row = hiScores.addRow(); // Create a new TableRow, called row, for the hiScores table.
    //Set values of that row.
    row.setString("user",playerName);
    row.setInt("score",score);
    row.setInt("time",(endTime-startTime)/1000);
    saveTable(hiScores, leaderBoardLoc); //Save the data from the hiScores table back to leaderboard.csv.
  }
  else{
    return;
  }
}

// Function to get the Best Scoring Player. (On a draw, the player who has had the best score for longer is considered the winner.)
public void getBestScore(){
  if (localSave){
    for (int i = 0; i < hiScores.getRowCount(); i++){
      TableRow row = hiScores.getRow(i);
      if (row.getInt("score") > topScore){
        topScore = row.getInt("score");
        topPlayer = row.getString("user");
        }
      }
  }
  else{
    if (score > topScore){
      topScore = score;
      topPlayer = playerName;
      }
  }
}

// Function to reset Camera Position.
public void resetCameraPos(){
  cameraDisplacementX = 0;
  cameraDisplacementY = 0;
}

// Function called to start the main game state (gameState 1) from main menu. Load new settings.
public void startMain(){
  starX = new float[starCount];
  starY = new float[starCount];
  starSize = new float[starCount];
  randomiseStars(); // Set star positions and sizes.
  startTime = millis();
  gameState = 1;
}

// Function to display floating text.
public void floatingText(int number){
  if (millis() - wormCollisionTime < 2000){
    fill(255-(millis()-wormCollisionTime)/9); //Fade the floating text out.
    textSize(15);
    text("+ " + number, floatingTextX, floatingTextY);
    floatingTextY -= 0.3f; // "Float" the text (make it rise).
  }
}

//Function to get random numbers for item spawn chance, and item location.
public void setRNG(){
  randomNumber = (int)random(0,100);
  itemX = random(0,600);
  itemY = random(0,600);
}

// Function to handle RNG Events. Draws items, checks collision and handles event timers.
public void rngEvent(){
  // Speed Slower.
  if (randomNumber >= 0 && randomNumber <= 20){
    noStroke();
    fill(0,255,255);
    ellipse(itemX,itemY,20,20);
    if (dist(playerX,playerY,itemX,itemY) <= 30){
      speed = 2.5f;
      itemX = -30;
      itemY = -30;
      itemStartTime1 = millis();
      speedSlowed = true;
    }
  }
  // Score Multiplier.
  else if (randomNumber >= 21 && randomNumber <= 41){
    noStroke();
    fill(0,255,0);
    ellipse(itemX,itemY,20,20);
    if (dist(playerX,playerY,itemX,itemY) <= 30){
      scoreMultiplier *= 2;
      itemX = -30;
      itemY = -30;
      itemStartTime2 = millis();
      scoreMultiplierEnabled = true;
    }
  }
  // Shield.
  else if (randomNumber >= 42 && randomNumber <= 62){
    noStroke();
    fill(240,70,240);
    ellipse(itemX,itemY,20,20);
    if (dist(playerX,playerY,itemX,itemY) <= 30){
      itemX = -30;
      itemY = -30;
      itemStartTime3 = millis();
      shielded = true;
    }
  }
  // Check if event over. Events last 10 seconds.
  if (speedSlowed && (millis() - itemStartTime1 >= eventExpireTime)){
    speed = 5;
    speedSlowed = false;
  }
  if (scoreMultiplierEnabled && (millis() - itemStartTime2 >= eventExpireTime)){
    scoreMultiplier = 1;
    scoreMultiplierEnabled = false;
  }
  if (shielded && (millis() - itemStartTime3 >= eventExpireTime)){
    shielded = false;
  }
}

// Display text for when items are active, and show their timers.
public void displayItemTimers(){
  if (scoreMultiplierEnabled){
    fill(0,255,0);
    text("Multiplier: x" + scoreMultiplier + " (" + (int)(eventExpireTime-(millis()-itemStartTime2))/1000 + ")",10,570);
  }
  if (speedSlowed){
    fill(0,255,255);
    text("Speed Slowed! (" + (int)(eventExpireTime-(millis()-itemStartTime1))/1000 + ")",450,590);
  }
  if (shielded){
    fill(240,70,240);
    if (speedSlowed){
      text("Shields Active! (" + (int)(eventExpireTime-(millis()-itemStartTime3))/1000 + ")",450,570);
    }
    else{
      text("Shields Active! (" + (int)(eventExpireTime-(millis()-itemStartTime3))/1000 + ")",450,590);
    }
  }
}

// Toggle integer value up/down button.
public int toggleIntButton(int togX, int togY, int togSize, int toggleValue, int toggleByAmount, int toggleValueMin, int toggleValueMax){
  stroke(255);
  fill(200);
  rect(togX,togY,togSize,togSize*2);
  line(togX,togY+togSize,togX+togSize,togY+togSize);
  fill(50);
  triangle(togX,togY+togSize,togX+togSize/2,togY,togX+togSize,togY+togSize);
  triangle(togX,togY+togSize,togX+togSize/2,togY+2*togSize,togX+togSize,togY+togSize);
  if ((mousePressed) && (mouseX >=  togX) && (mouseX <= (togX + togSize)) && (mouseY >= togY) && (mouseY <= (togY + togSize)) && toggleValue < toggleValueMax){
    toggleValue += toggleByAmount;
  }
  if ((mousePressed) && (mouseX >=  togX) && (mouseX <= (togX + togSize)) && (mouseY > togY+togSize) && (mouseY <= (togY + 2*togSize)) && toggleValue > toggleValueMin){
    toggleValue -= toggleByAmount;
  }
  return toggleValue;
}

// Toggle boolean button.
public boolean toggleBoolButton(int togX, int togY, int togSize, boolean toggleBoolValue){
  stroke(255);
  fill(200);
  rect(togX,togY,togSize*2,togSize);
  line(togX+togSize,togY,togX+togSize,togY+togSize);
  if (toggleBoolValue){
    fill(0,170,0);
    ellipse(togX+togSize*1.5f,togY+togSize/2,togSize/2,togSize/2);
  }
  if (!toggleBoolValue){
    fill(255,0,0);
    ellipse(togX+togSize/2,togY+togSize/2,togSize/2,togSize/2);
  }
  if ((mousePressed) && (mouseX >=  togX) && (mouseX <= (togX + 2*togSize)) && (mouseY >= togY) && (mouseY <= (togY + togSize))){
    toggleBoolValue = true;
  }
  if ((mousePressed) && (mouseX >  togX + togSize) && (mouseX <= (togX + 2*togSize)) && (mouseY >= togY) && (mouseY <= (togY + togSize))){
    toggleBoolValue = false;
  }
  return toggleBoolValue;
}

// KeyPress function.
public void keyPressed(){
  // Keypresses during the game.
  if (gameState == 1){
    if (key == 'a'){
      if (cameraDisplacementX < 10){
        cameraDisplacementX ++;
        for (int i = 0; i < starCount; i++){
          starX[i] += 1;
        }
      }
    }
    else if (key =='d'){
      if (cameraDisplacementX > -10){
        cameraDisplacementX --;
        for (int i = 0; i < starCount; i++){
          starX[i] -= 1;
        }
      }
    }
    else if (key == 'w'){
      if (cameraDisplacementY < 10){
        cameraDisplacementY ++;
        for (int i = 0; i < starCount; i++){
          starY[i] += 1;
        }
      }
    }
    else if (key == 's'){
      if (cameraDisplacementY > -10){
        cameraDisplacementY --;
        for (int i = 0; i < starCount; i++){
          starY[i] -= 1;
        }
      }
    }
    else {
      increment = -increment; // Changes between clockwise and anti-clockwise.
    }
  }
  // Keypresses after the game.
  if (gameState == 2){
    if (playerName.length() < 4 && key >= 'a' && key <= 'z'){
      playerName = playerName + key;
    }
    if (gameSaved == false && playerName.length() == 4 &&  key == 10){ // Key 10 is the enter key.
      gameSaved = true;
      saveGame();
      getBestScore();
    }
    if (key == '1'){
      showMenu = false;
      setup();
      startTime = millis();
    }
    if (key == '2'){
      showMenu = true;
      setup();
    }
  }
}

public void mousePressed(){
 if (gameState == 0){
   if (mouseX >= 210 && mouseX <= 395 && mouseY >= 530 && mouseY <= 550){
     startMain();
   }
 } 
 if (gameState == 1){
   if (clickChangeEnabled){
      increment = -increment; 
   }
 }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "studentwork.BlackHole" };
    runSketch(appletArgs, new BlackHole());
  }
}
