/* ========================================
 =            THE ROCKET GAME              =
 =          CODED BY BEN TALESE            =
 ======================================== */

package studentwork;

import processing.sound.*;
import processing.core.*; 


public class Rocket extends mqapp.MQApp {
  
  PVector rocketPos;               // rocket properties
  int rocketHealth;
  int rocketFuel;
  boolean engineOn;
  
  int PREF_WIDTH = 900;
  int PREF_HEIGHT=1500;
  
  boolean accelerate = false;      // rocket movement
  boolean decelerate = false;
  boolean bankLeft = false;
  boolean bankRight = false;
  boolean warpDriveOn = false;
  int warpTime;
  
  int distance;                                       // distance from start
  int goalDistance = 250000;                          // 250,000m distance to moon from start
  
  int starCount = 50;                                 // star properties (amount, position, size)
  int[] starXPos = new int[starCount];
  int[] starYPos = new int[starCount];
  int[] starSize = new int[starCount];
  
  int totalMeteoroids = 9;
  int meteoroidCount = 9;                             // meteoroid properties (amount, position, size)
  int[] meteoroidXPos = new int[meteoroidCount+1];
  int[] meteoroidYPos = new int[meteoroidCount+1];
  int[] meteoroidSize = new int[meteoroidCount+1];
  int[] meteoroidSpeed = new int[meteoroidCount+1];
  int[] meteoroidShade = new int[meteoroidCount+1];
  
  boolean checkpoint = false;                         // other game properties
  int checkpointTimer = 300;
  int sizeDifficultyMultiplier = 5;
  int speedDifficultyMultiplier = 1;
  boolean gameFinished = false;
  boolean gameLost = false;
  boolean immunity;
  int immunityTime;
  int moonYPos;
  int flagYPos;
  int explosionSize;
  int explosionTime;
  
  SoundFile music;

  static public void main(String[] passedArgs) {
        runSketch(appletArgs, new Rocket());
    }
  
  public void setup() {              // initialise the game properties
    size(displayWidth, displayHeight);
    PFont av = loadFont("shared/Avenir-LightOblique-78.vlw");
    music = new SoundFile(this, "rocket/mission.wav");
    textFont(av, 78);
    gameReset();
  }
  
  
  public void draw() {              // display the game on the screen
    background(253, 134, 38);
    startScreen();
    drawStars();
    spawnMeteoroids();
    moveRocket();
    drawRocket();
    checkpoint();
    checkCollision();
    immunityShield();
    displayHUD();
    gameFinish();
    gameLost();
  }
  
  
  void gameReset() {
    rocketPos = new PVector(width/2, height-100);        // rocket starting position
    rocketHealth = 150;                                  // rocket total health (5 * 30 for spacing of health bars)
    rocketFuel = 150;                                    // rocket total fuel (5 * 30 for spacing of fuel bars)
    engineOn = false;                                    // control whether the ship appears active or static
    distance = 0;                                        // starting distance
    meteoroidCount = 9;                                  // reset active meteoroids to stage 0 amount
    sizeDifficultyMultiplier = 5;                        // multiplier for meteoroid size, decreases per stage
    speedDifficultyMultiplier = 1;                       // multiplier for meteoroid speed, increases per stage
    gameFinished = false;                                // control condition for finishing game
    gameLost = false;                                    // control condition for losing game
    immunity = false;                                    // control condition for rocket shield
    moonYPos = -600;                                     // starting position of moon (off screen)
    flagYPos = -200;                                     // starting position of flag (off screen)
    explosionSize = 50;                                  // starting size of rocket explosion (drawn if rocket health = 0)
    explosionTime = 255;                                 // starting time of rocket explosion (controls opacity of explosion)
  
    for (int i = 0; i <= meteoroidCount; i++) {          // initialise meteoroid values (position, shade, size and speed)
      meteoroidXPos[i] = (int)random(5, width-4);
      meteoroidYPos[i] = ((int)random(30, 2000)) * -1;
      meteoroidShade[i] = (int)random(100, 220);
      meteoroidSize[i] = (int)random(20, 60) * sizeDifficultyMultiplier;
      meteoroidSpeed[i] = (int)random(4, 8) * speedDifficultyMultiplier;
      if (meteoroidSize[i] > 150) {                      // stop meteoroids that are too big from moving at max possible speed (too difficult otherwise)
        meteoroidSpeed[i] = meteoroidSpeed[i]/2;
      }
    }
  
    for (int i = 0; i < starXPos.length; i++) {          // initialise star values (position and size)
      starXPos[i] = (int)random(5, width-4);
      starYPos[i] = (int)random(5, height-4);
      starSize[i] = (int)random(1, 7);
    }
  }
  
  
  void gameFinish() {
    if (distance >= goalDistance) {                      // if the player reaches the goal without losing all health, they finish the game
      gameFinished = true;
  
      int moonSize = 1200;                               // draw the moon (size of 1200px and in the centre of the screen)
      int moonXPos = width/2;
      fill(200, 200, 200);                               // moon shade
      strokeWeight(20);                                  // moon stroke size
      circle(moonXPos, moonYPos, moonSize);              // moon circle
      noStroke();
      fill(100, 100, 100);
      circle(moonXPos - 300, moonYPos - 70, 50);         // moon spot
      circle(moonXPos - 270, moonYPos - 50, 80);         // moon spot
      fill(150, 150, 150);
      circle(moonXPos + 100, moonYPos - 100, 250);       // moon spot
      circle(moonXPos - 200, moonYPos + 300, 300);       // moon spot
      fill(170, 170, 170);
      circle(moonXPos + 100, moonYPos + 200, 200);       // moon spot
      fill(190, 190, 190);
      circle(moonXPos - 100, moonYPos - 400, 150);       // moon spot
      circle(moonXPos - 180, moonYPos - 250, 220);       // moon spot
      circle(moonXPos + 150, moonYPos - 350, 70);        // moon spot
      circle(moonXPos + 300, moonYPos + 300, 200);       // moon spot
  
      if (moonYPos < height + 50) {
        moonYPos += 5;                                   // if the moon hasn't reached the bottom of the screen, increase the y position of the moon
      }
  
      if (moonYPos > height/2 || moonYPos > rocketPos.y) {    // if the moon has crossed half way down the screen or passed the rocket, set
        rocketPos.x = width/2;                                // the rocket position at the centre of the screen and sitting on top of the moon's
        rocketPos.y = height/1.65f;                            // stop position
  
        fill(195, 220, 255);                                  // display winning message and restart instructions
        textSize(128);
        textAlign(CENTER);
        text("YOU WON!", width/2, height/2 - height/8);
        textSize(40);
        text("PRESS SPACEBAR TO TRY AGAIN", width/2, height/2 - width/11);
      }
  
      if (gameFinished) {                                     // if the game has finished, display a green flag on a pole flying down to
        float flagXPos = rocketPos.x + 100;                   // rest next to the rocket on the moon
        fill(230, 230, 230);
        rect(flagXPos, flagYPos, 10, 150, 5);
        fill(0, 255, 0);
        triangle(flagXPos, flagYPos - 75, flagXPos + 40, flagYPos - 55, flagXPos, flagYPos - 35);
  
        if (flagYPos < rocketPos.y - 20 && moonYPos == height + 50) {
          flagYPos += 15;
        }
      }
      
    }
  }
  
  
  void immunityShield() {
    if (immunity && immunityTime != 0 && !gameLost) {        // if the rocket shield has been activated, the shield time hasn't run out and
      immunityTime--;                                        // the game hasn't been lost (health isn't 0), display a blue shield around
      fill(100, 200, 200, immunityTime);                     // the rocket with opacity controlled by the shield timer (immunityTime)
      strokeWeight(5);
      stroke(200, 255, 255, 150);
      circle(rocketPos.x, rocketPos.y - 15, 230);
    } else {
      immunity = false;                                      // once immunity time reaches 0, stop displaying the shield
    }
  }
  
  
  void spawnMeteoroids() {
    if (distance > 3000) {                                   // if rocket has reached 3000m, start spawning meteoroids from the top of the screen
      for (int i = 0; i <= meteoroidCount; i++) {            // and increment their y position so they come towards the player from the top
        fill(meteoroidShade[i]);
        stroke(150, 0, 0);
        strokeWeight(4);
        circle(meteoroidXPos[i], meteoroidYPos[i], meteoroidSize[i]);
        meteoroidYPos[i] += meteoroidSpeed[i];
  
        if (warpDriveOn) {                                   // if player has activated warp speed, move the meteoroids down the screen faster
          meteoroidYPos[i] += 80;
        }
  
        if (meteoroidYPos[i] > height + 200 && !checkpoint && !gameFinished && !gameLost) {          // if a meteoroid falls off the screen
          meteoroidYPos[i] = (int)random(30, 2000) * -1;                                             // respawn it with new randomised values
          meteoroidShade[i] = (int)random(100, 220);
          meteoroidXPos[i] = (int)random(5, width-4);
          meteoroidSize[i] = (int)random(20, 60) * sizeDifficultyMultiplier;
          meteoroidSpeed[i] = (int)random(4, 8) * speedDifficultyMultiplier;
          if (meteoroidSize[i] > 150) {                                            // stop meteoroids that are too big from
            meteoroidSpeed[i] = meteoroidSpeed[i]/2;                               // moving at max possible speed (too difficult otherwise)
          }
        }
        
      }
    }
  }
  
  
  void checkpoint() {
    // player starts in stage 0
    // stage 1
    if (distance >= 49900 && distance <= 50100) {            // check if rocket has reached a checkpoint zone (roughly every 50,000m)
      checkpoint = true;                                     // if player has reached a checkpoint, increase difficulty and decrease
      sizeDifficultyMultiplier = 4;                          // amount of meteoroids
      speedDifficultyMultiplier = 2;
      meteoroidCount = 8;
    }
    
    // stage 2
    if (distance >= 99900 && distance <= 100100) {
      checkpoint = true;
      sizeDifficultyMultiplier = 3;
      speedDifficultyMultiplier = 3;
      meteoroidCount = 7;
    }
    
    // stage 3
    if (distance >= 149900 && distance <= 150100) {
      checkpoint = true;
      sizeDifficultyMultiplier = 2;
      speedDifficultyMultiplier = 4;
      meteoroidCount = 6;
    }
    
    // stage 4
    if (distance >= 199900 && distance <= 200100) {
      checkpoint = true;
      sizeDifficultyMultiplier = 1;
      speedDifficultyMultiplier = 5;
      meteoroidCount = 9;
    }
  
    if (checkpoint) {                                      // if in a checkpoint zone, refill rocket health and fuel and give a small rest period
      rocketHealth = 150;
      rocketFuel = 150;
      warpTime = 0;                                        // set warp time to 0 so player doesn't go into the next stage at warp speed
      checkpointTimer--;                                   // and instantly gets hit by a meteoroid
      
      if (checkpointTimer <= 0) {                          // if rest period has ended, the rocket is pushed out of the checkpoint zone (+250m) to avoid
        checkpoint = false;                                // continuous checkpoint bug and rest timer is reset
        distance = distance + 250;
        checkpointTimer = 300;
        for (int i = 0; i < totalMeteoroids; i++) {        // fixes a bug where unused meteoroids are frozen in place but not drawn due to meteoroidCount
          meteoroidYPos[i] = (int)random(30, 2000) * -1;   // decreasing, making an invisible collision
        }
      }
      
    }
  }
  
  
  void displayHUD() {                  // display heads up display info
    int hudXPos = 30;            // x position of health and fuel bar
    int hudYPos = height - 40;   // y position of helath and fuel bar
    int barWidth = 30;           // individual bar width for health and fuel
    int barHeight = 50;          // individual bar height for health and fuel
    strokeWeight(3);             // outline width of bars
  
    // health gauge
    for (int i = 0; i < rocketHealth; i += 30) {      // display a bar for each lot of 30 health, increase the x pos of the next health bar by the same width
      if (immunity) {                      // display bars in blue if shield is active
        fill(0, 255, 200);
        stroke(0, 50, 100);
      } else if (rocketHealth <= 30) {     // display the last bar of health in red
        fill(200, 0, 0);
        stroke(100, 0, 0);
      } else {                             // if all other conditions are false, display health bars in green
        fill(20, 200, 20);
        stroke(0, 100, 0);
      }
      rect(hudXPos + i, hudYPos, barWidth, barHeight);      // draw the bars of health based off values
    }
  
    // fuel gauge
    for (int i = 0; i < rocketFuel; i += 30) {       // display a bar for each lot of 30 fuel, increase the x pos of the next fuel bar by the same width
      fill(255, 255, 0);                             // display fuel bars in yellow
      stroke(100, 100, 0);
      rect(hudXPos + i, hudYPos - 53, barWidth, barHeight);    // draw the bars of fuel based off values
    }
  
    // progress gauge
    int progressBarXPos = width - 30;            // x position of progress bar
    int progressBarYPos = height - 145;          // y position of progress bar
    int progressBarHeight = 260;                 // height of the progress bar
    int progress = distance / 1000;              // divide the current distance by 1000 so it can be more easily displayed in the progress bar (values: 0 - 250)
    noFill();
    stroke(200, 200, 200);
    rect(progressBarXPos, progressBarYPos, barWidth, progressBarHeight);       // draw the progress bar based off values
  
    // display checkpoints on progress bar
    for (int i = 50; i <= 200; i += 50) {          // for each 50,000m (50/250), display a horizontal line on the progress bar showing where the checkpoint are
      line(progressBarXPos - 15, (progressBarYPos - 130) + i, progressBarXPos + 15, (progressBarYPos - 130) + i);
    }
  
    // rocket position indicator
    fill(0, 255, 0);                // based off the current progress value, display a green arrow which indicates where the rocket is
    stroke(0, 200, 0);
    strokeWeight(2);
    triangle(progressBarXPos, (progressBarYPos + 115) - progress, progressBarXPos - 5, (progressBarYPos + 125) - progress, progressBarXPos + 5, (progressBarYPos + 125) - progress);
  
    // moon indicator
    fill(200, 200, 200);            // display a tiny moon at the top of the progress bar
    stroke(150, 150, 150);
    circle(progressBarXPos, progressBarYPos - 140, 40);
  }
  
  
  void checkCollision() {                          // if shield hasn't been triggered, the game hasn't finished and
    boolean collision = false;                     // the rocket position minus any meteoroid position is too low, there has been a collision
    if (!immunity && !gameFinished) {
      immunityTime = 255;                                    // set 4.25 seconds of immunity (255 / 60fps = 4.25), 255 for max opacity
      for (int i = 0; i < meteoroidXPos.length; i++) {
        if (dist(rocketPos.x, rocketPos.y - 100, meteoroidXPos[i], meteoroidYPos[i]) < meteoroidSize[i]/2) {    // nosetip collision check
          collision = true;
        }
        if (dist(rocketPos.x - 65, rocketPos.y + 5, meteoroidXPos[i], meteoroidYPos[i]) < meteoroidSize[i]/2) {    // left wing collision check
          collision = true;
        }
        if (dist(rocketPos.x + 65, rocketPos.y + 5, meteoroidXPos[i], meteoroidYPos[i]) < meteoroidSize[i]/2) {    // right wing collision check
          collision = true;
        }
        if (dist(rocketPos.x - 35, rocketPos.y - 42, meteoroidXPos[i], meteoroidYPos[i]) < meteoroidSize[i]/2) {    // left-mid wing collision check
          collision = true;
        }
        if (dist(rocketPos.x + 35, rocketPos.y - 42, meteoroidXPos[i], meteoroidYPos[i]) < meteoroidSize[i]/2) {    // right-mid wing collision check
          collision = true;
        }
      }
    }
  
    if (!warpDriveOn) {              // if player hasn't activated warp speed, and if a collision has been detected,
      if (collision) {               // reduce rocket health by 1 bar (30) and activate the immunity shield
        rocketHealth -= 30;
        immunity = true;
      }
    }
  
    if (rocketHealth == 0) {         // if the player has lost all rocket health, they have lost the game
      gameLost = true;
      music.stop();
    }
    
  }
  
  
  void gameLost() {                  // if the game has been lost (health = 0), display the rocket explosion
    if (gameLost) {                  // and the game over message with restart instructions
      // explosion
      noStroke();
      fill(255, 0, 0, explosionTime);
      circle(rocketPos.x, rocketPos.y, explosionSize);
      if (explosionSize < width * 7) {
        explosionSize += 20;
        explosionTime--;
      }
  
      // game over message
      fill(255, 200, 165);
      textSize(128);
      textAlign(CENTER);
      text("GAME OVER", width/2, height/2 - height/8);
  
      // game restart message
      textSize(40);
      text("PRESS SPACEBAR TO TRY AGAIN", width/2, height/2 - height/11);
    }
  }
  
  
  void startScreen() {                // if the player hasn't started the game, display the game title and controls information
    if (!engineOn) {
      fill(255, 255, 255);
      textAlign(CENTER);
      textSize(90);
      text("THE ROCKET GAME", width/2, height/2 - height/8);
      textSize(40);
      text("CREATED BY BEN TALESE", width/2, height/2 - height/11);
      textSize(30);
      text("PRESS W OR UP ARROW TO START", width/2, height/2);
  
      int controlsXPos = width/2;
      int controlsYPos = height/2 + height/8;
      textSize(20);
      text("CONTROLS", controlsXPos, controlsYPos);
      text("ACCELERATE = W / UP ARROW", controlsXPos, controlsYPos + 20);
      text("DECELERATE = S / DOWN ARROW", controlsXPos, controlsYPos + 40);
      text("BANK LEFT = A / LEFT ARROW", controlsXPos, controlsYPos + 60);
      text("BANK RIGHT = D / RIGHT ARROW", controlsXPos, controlsYPos + 80);
      text("WARP SPEED = ACCELERATE + SHIFT", controlsXPos, controlsYPos + 100);
      text("BOOST LEFT = BANK LEFT + SHIFT", controlsXPos, controlsYPos + 120);
      text("BOOST RIGHT = BANK RIGHT + SHIFT", controlsXPos, controlsYPos + 140);
    }
  }
  
  
  void drawRocket() {            // display the rocket if the game hasn't been lost
    if (!gameLost) {
      // animated engine flame
      int flamedistance = 60;                                                        // flame distance from the engine
      PVector bigFlameSize = new PVector(random(20, 25), random(30, 60));            // randomised values for flame sizes (small, medium and big)
      PVector mediumFlameSize = new PVector(random(12, 18), random(25, 50));
      PVector smallFlameSize = new PVector(random(10, 15), random(20, 40));
      int bigFlameColour;                                                          // variables to store the colours for the 3 flames
      int mediumFlameColour;
      int smallFlameColour;
  
      if (engineOn && !checkpoint && !gameFinished) {                                // if the player has started the game, it isn't a checkpoint zone and
        if (warpDriveOn) {                                                           // the game hasn't been won, display the engine exhaust flames
          bigFlameSize.x = random(60, 75);
          bigFlameSize.y = random(90, 60);                                           // if player has activated warp speed, display biggest
          mediumFlameSize.x = random(36, 54);                                        // randomised flame sizes
          mediumFlameSize.y = random(75, 150);
          smallFlameSize.x = random(30, 45);
          smallFlameSize.y = random(60, 120);
        } else {
          if (accelerate) {                                                          // if player is accelerating, display bigger
            bigFlameSize.x = random(40, 50);                                         // randomised flame sizes
            bigFlameSize.y = random(60, 120);
            mediumFlameSize.x = random(24, 36);
            mediumFlameSize.y = random(50, 100);
            smallFlameSize.x = random(20, 30);
            smallFlameSize.y = random(40, 80);
          } else if (decelerate) {                                                   // if player is decelerating, display smallest
            bigFlameSize.x = random(10, 13);                                         // randomised flame sizes
            bigFlameSize.y = random(15, 30);
            mediumFlameSize.x = random(6, 9);
            mediumFlameSize.y = random(13, 25);
            smallFlameSize.x = random(5, 8);
            smallFlameSize.y = random(10, 20);
          } else {                                                                   // if no controls are being activated, display
            bigFlameSize.x = random(20, 25);                                         // normal randomised flame sizes
            bigFlameSize.y = random(30, 60);
            mediumFlameSize.x = random(12, 18);
            mediumFlameSize.y = random(25, 50);
            smallFlameSize.x = random(10, 15);
            smallFlameSize.y = random(20, 40);
          }
        }
  
        if (warpDriveOn) {                                                // if player has activated warp speed,
          bigFlameColour = color(110, 240, 238);                            // display flame colours in shades of blue,
          mediumFlameColour = color(35, 148, 204);                        // otherwise display flames in shades of red
          smallFlameColour = color(16, 26, 227);
        } else {
          bigFlameColour = color(110, 240, 238);
          mediumFlameColour = color(35, 148, 204);
          smallFlameColour = color(16, 26, 227);
        }
  
        noStroke();        // disable outline for all flames
        // big flame
        fill(bigFlameColour);
        ellipse(rocketPos.x, rocketPos.y + flamedistance, bigFlameSize.x, bigFlameSize.y);
  
        // medium flame
        fill(mediumFlameColour);
        ellipse(rocketPos.x, rocketPos.y + flamedistance, mediumFlameSize.x, mediumFlameSize.y);
  
        // small flame
        fill(smallFlameColour);
        ellipse(rocketPos.x, rocketPos.y + flamedistance, smallFlameSize.x, smallFlameSize.y);
      }
  
      // draw the body of the rocket
      if (bankLeft && !gameFinished) {                // if banking left and game isn't finished, display the rocket tilting left
        rectMode(CENTER);
        noStroke();
        fill(180);
        rect(rocketPos.x, rocketPos.y + 50, 30, 10);                                                                        // engine
        fill(255);
        ellipse(rocketPos.x, rocketPos.y - 50, 40, 80);                                                                     // cockpit
        triangle(rocketPos.x, rocketPos.y - 120, rocketPos.x - 15, rocketPos.y - 50, rocketPos.x + 15, rocketPos.y - 50);   // nosetip
        triangle(rocketPos.x, rocketPos.y - 90, rocketPos.x - 50, rocketPos.y + 10, rocketPos.x, rocketPos.y-10);           // front left wing
        triangle(rocketPos.x-5, rocketPos.y - 90, rocketPos.x + 90, rocketPos.y + 10, rocketPos.x, rocketPos.y-10);           // front right wing
        arc(rocketPos.x - 20, rocketPos.y + 50, 40, 80, PI-QUARTER_PI, PI+HALF_PI);                                         // back left wing
        arc(rocketPos.x + 15, rocketPos.y + 50, 80, 80, PI+HALF_PI, TWO_PI+QUARTER_PI);                                     // back right wing
        rect(rocketPos.x-3, rocketPos.y, 37, 100);                                                                            // main rocket body
  
        // rocket details
        fill(0, 255, 255);
        strokeWeight(2);
        stroke(200, 100, 20);
        arc(rocketPos.x-3, rocketPos.y - 60, 25, 50, PI, TWO_PI);                                                             // cockpit window
        line(rocketPos.x - 25, rocketPos.y + 35, rocketPos.x - 37, rocketPos.y + 65);
        line(rocketPos.x - 25, rocketPos.y + 25, rocketPos.x - 36, rocketPos.y + 50);
        strokeWeight(3);
        line(rocketPos.x + 5, rocketPos.y - 50, rocketPos.x + 5, rocketPos.y + 30);
        line(rocketPos.x + 10, rocketPos.y - 35, rocketPos.x + 10, rocketPos.y + 40);
        
      } else if (bankRight && !gameFinished) {                // if banking right and game isn't finished, display the rocket tilting left
        rectMode(CENTER);
        noStroke();
        fill(180);
        rect(rocketPos.x, rocketPos.y + 50, 30, 10);                                                                        // engine
        fill(255);
        ellipse(rocketPos.x, rocketPos.y - 50, 40, 80);                                                                     // cockpit
        triangle(rocketPos.x, rocketPos.y - 120, rocketPos.x - 15, rocketPos.y - 50, rocketPos.x + 15, rocketPos.y - 50);   // nosetip
        triangle(rocketPos.x + 5, rocketPos.y - 90, rocketPos.x - 90, rocketPos.y + 10, rocketPos.x, rocketPos.y-10);           // front left wing
        triangle(rocketPos.x, rocketPos.y - 90, rocketPos.x + 50, rocketPos.y + 10, rocketPos.x, rocketPos.y-10);           // front right wing
        arc(rocketPos.x - 15, rocketPos.y + 50, 80, 80, PI-QUARTER_PI, PI+HALF_PI);                                         // back left wing
        arc(rocketPos.x + 20, rocketPos.y + 50, 40, 80, PI+HALF_PI, TWO_PI+QUARTER_PI);                                     // back right wing
        rect(rocketPos.x + 3, rocketPos.y, 37, 100);                                                                            // main rocket body
  
        // rocket details
        fill(0, 255, 255);
        strokeWeight(2);
        stroke(200, 100, 20);
        arc(rocketPos.x+3, rocketPos.y - 60, 25, 50, PI, TWO_PI);                                                             // cockpit window
        line(rocketPos.x - 25, rocketPos.y + 35, rocketPos.x - 50, rocketPos.y + 65);
        line(rocketPos.x - 25, rocketPos.y + 25, rocketPos.x - 47, rocketPos.y + 50);
        strokeWeight(3);
        line(rocketPos.x + 13, rocketPos.y - 50, rocketPos.x + 13, rocketPos.y + 30);
        line(rocketPos.x + 18, rocketPos.y - 35, rocketPos.x + 18, rocketPos.y + 40);
        
      } else {
        rectMode(CENTER);
        noStroke();
        fill(180);
        rect(rocketPos.x, rocketPos.y + 50, 30, 10);                                                                        // engine
        fill(255);
        ellipse(rocketPos.x, rocketPos.y - 50, 40, 80);                                                                     // cockpit
        triangle(rocketPos.x, rocketPos.y - 120, rocketPos.x - 15, rocketPos.y - 50, rocketPos.x + 15, rocketPos.y - 50);   // nosetip
        triangle(rocketPos.x, rocketPos.y - 90, rocketPos.x - 70, rocketPos.y + 10, rocketPos.x, rocketPos.y-10);           // front left wing
        triangle(rocketPos.x, rocketPos.y - 90, rocketPos.x + 70, rocketPos.y + 10, rocketPos.x, rocketPos.y-10);           // front right wing
        arc(rocketPos.x - 20, rocketPos.y + 50, 60, 80, PI-QUARTER_PI, PI+HALF_PI);                                         // back left wing
        arc(rocketPos.x + 20, rocketPos.y + 50, 60, 80, PI+HALF_PI, TWO_PI+QUARTER_PI);                                     // back right wing
        rect(rocketPos.x, rocketPos.y, 40, 100);                                                                            // main rocket body
  
        // rocket details
        fill(0, 255, 255);
        strokeWeight(2);
        stroke(200, 100, 20);
        arc(rocketPos.x, rocketPos.y - 60, 30, 50, PI, TWO_PI);                                                             // cockpit window
        line(rocketPos.x - 25, rocketPos.y + 35, rocketPos.x - 45, rocketPos.y + 65);
        line(rocketPos.x - 25, rocketPos.y + 25, rocketPos.x - 42, rocketPos.y + 50);
        strokeWeight(3);
        line(rocketPos.x + 10, rocketPos.y - 50, rocketPos.x + 10, rocketPos.y + 30);
        line(rocketPos.x + 15, rocketPos.y - 35, rocketPos.x + 15, rocketPos.y + 40);
      }
    }
  }
  
  
  void moveRocket() {
    if (engineOn && !checkpoint && !gameFinished && !gameLost) {         // if the player has started the game, it isn't a check point, the game hasn't
      distance += 20;                                                    // been won and the game hasn't been lost, distance will increase by 20 (1200m/s)
      if (!warpDriveOn) {                                  // if player hasn't activated warp speed, the rocket can be controlled
        if (accelerate && rocketPos.y > 50) {
          rocketPos.y -= 5;                                // if player is accelerating and rocket hasn't reached the top of the screen, move the rocket
          distance += 20;                                  // up the screen and increase speed of rocket (2400m/s)
        }
        if (decelerate && rocketPos.y < height - 50) {     // if player is decelerating and rocket hasn't reached the bottom of the screen, move the rocket
          rocketPos.y += 5;                                // down the screen and decrease the speed of the rocket (600m/s)
          distance -= 10;
        }
        if (bankLeft && rocketPos.x > 50) {                // if player is banking left and the rocket hasn't reached the left side of the screen, move the
          rocketPos.x -= 10;                               // rocket to the left
        }
        if (bankRight && rocketPos.x < width - 50) {       // if the player is banking right and the rocket hasn't reached the right side of the screen, move
          rocketPos.x += 10;                               // the rocket to the right
        }
      }
      if (warpDriveOn) {                                   // if player has activated warp speed, the warp speed function is activated
        warpSpeed();
      }
    }
  }
  
  
  public void keyPressed() {                                      // if any of the control keys are pressed, the corresponding boolean is set to true
    if (!engineOn && (key == 'w' || keyCode == UP)) {                     // this controls up, down, left, right
      music.loop();
      engineOn = true;
    }
    if (engineOn) {
      if (key == 'w' || keyCode == UP) {
        accelerate = true;
      }
      if (key == 's' || keyCode == DOWN) {
        decelerate = true;
      }
      if (key == 'a' || keyCode == LEFT) {
        bankLeft = true;
      }
      if (key == 'd' || keyCode == RIGHT) {
        bankRight = true;
      }
    }
  
    if (gameLost || gameFinished) {                  // if the game has been lost or won, the player can press the spacebar
      if (keyCode == ' ') {                          // to activate the reset function
        gameReset();
      }
    }
  }
  
  
  public void  keyReleased () {                             // if any control keys (up, down, left or right) are released, the rocket will stop moving
    if (key == 'w' || keyCode == UP) {               // in the corresponding direction
      accelerate = false;
    }
    if (key == 's' || keyCode == DOWN) {
      decelerate = false;
    }
    if (key == 'a' || keyCode == LEFT) {
      bankLeft = false;
    }
    if (key == 'd' || keyCode == RIGHT) {
      bankRight = false;
    }
    
    if (engineOn && rocketFuel != 0 && !gameFinished && !gameLost) {                // if the player has started the game, the rocket fuel isn't empty and
      //BOOST LEFT                                                                  // the game isn't won or lost, the player can use boost
      if (bankLeft && keyCode == SHIFT && rocketPos.x > 200) {              // if the player was banking left, isn't too far left and also released the shift key,
        rocketPos.x -= 150;                                                 // the rocket will jump to the left and consume 1 bar (30) of fuel
        rocketFuel -= 30;
      }
      //BOOST RIGHT                                                         // if the player was banking right, isn't too far right and also released the shift key,
      if (bankRight && keyCode == SHIFT && rocketPos.x < width - 200) {     // the rocket will jump to the right and consume 1 bar (30) of fuel
        rocketPos.x += 150;
        rocketFuel -= 30;
      }
      // WARP SPEED                                                         // if the player was accelerating, released the shift key and the rocket has full fuel,
      if (accelerate && keyCode == SHIFT && rocketFuel >= 150) {            // warp speed is activated, all the rocket fuel is consumed and
        warpDriveOn = true;                                                 // warp time is set to 4 seconds
        rocketFuel -= 150;
        warpTime = 250;
      }
    }
  }
  
  
  void warpSpeed() {                        // if warp time hasn't reached 0, warp time decreases and speed is increased by 60 (6,000m/s)
    if (warpTime != 0) {                    // otherwise, if warp time reaches 0, warp speed is deactivated
      warpTime--;
      distance += 60;
    } else {
      warpDriveOn = false;
    }
  }
  
  
  void drawStars() {
    fill(255,255,0);
    noStroke();
    for (int i = 0; i < starXPos.length; i++) {             // cycle through all stars
      circle(starXPos[i], starYPos[i], starSize[i]);        // draw stars at current attributes stored in the star arrays
      if (!engineOn || checkpoint || gameFinished) {
        starYPos[i] += 3;
      } else if (gameLost) {
        starYPos[i] += 1;
      } else if (warpDriveOn) {
        starYPos[i] += 120;
      } else if (accelerate) {
        starYPos[i] += 30;            // if accelerating, stars move faster
      } else if (decelerate) {
        starYPos[i] += 15;            // if decelerating, stars move slower
      } else {
        starYPos[i] += 20;            // otherwise, stars move at normal speed
      }
      if (starYPos[i] > height + 15) {   // if stars reach the bottom of the screen, respawn them at the top with randomised locations and size
        starYPos[i] = (int)random(-20, -5);
        starXPos[i] = (int)random(5, width-4);
        starSize[i] = (int)random(1, 7);
      }
    }
  }  
}
