// by Elise McCabe, converted to macquarie mini my Matt Roberts

package studentwork;

public class Stacker extends mqapp.MQApp {

    public String name(){return "Stacker";}
    public String author(){return "???";}
    public String description(){return "Arcade money hog";}

//built on top of Matt's solution
/* Press:
1 - easy mode
2 - normal mode (default)
3 - hard mode
p - pause
c - change colour, can change color of every new stack
r - rounded rectangles
space - next stack

rules:
each subsequent stack will move faster after being placed
a win streak of 10+ will reduce the stack to 3 blocks
a win streak of 20+ will reduce the stack to 2 blocks
a win streak of 30+ will reduce the stack to 1 block

if resizing, reselect difficulty mode
*/

float xPos, yPos, boxWidth, boxHeight, speed; 
boolean move; 
int currentBox, countX, upperBoxLimit, lowerBoxLimit, boxRadius;
int[][] saved, colour;
int score = 0;
String difficulty = "normal";
int R = 255; 
int G = 147;
int B = 79;

public void setup() {
  size(displayWidth, displayHeight);
  boxWidth = 0.0703125f * width;  
  boxHeight = 0.125f * height;
  yPos = 0;
  xPos = 0;
  speed = height/720.0f; 
  countX = 0;
  saved = new int[14][3]; //stores x, steppedY and numOfBox values
  colour = new int[14][3]; //stores RGB values
  currentBox = 4;
  upperBoxLimit = 0;
  lowerBoxLimit = 0;
  move = true; 
  boxRadius = 0;
}

public void draw() {
  background(240, 240, 240);
  grid();
  oldBox();
  int steppedPos = roundStepY((int)yPos);
  fill(R, G, B);
  for (int i = 0; i < currentBox; i ++) {
    rect(xPos, (int)((steppedPos + i) * boxHeight), boxWidth, boxHeight, boxRadius);
  }
  fill(0);
  textAlign(RIGHT, TOP);
  textSize(16);
  text("win streak = " + score, width - 5, 5);

  //line(boxWidth + xPos, yPos, 2*boxWidth + xPos, yPos); //visual
  if (move == true) { //increment
    yPos = yPos + speed;
    if (yPos + boxHeight * currentBox > height || yPos < 0) { //change up or down
      speed = speed * -1;
      yPos = yPos + speed;
    }
  }
}

public void keyPressed() {
  if (key == 32) { //SPACE
    saved[countX][0] = (int)xPos; //stores x value
    saved[countX][1] = roundStepY((int)yPos); //stores stepped y value
    saved[countX][2] = currentBox; //stores number of boxes
    colour[countX][0] = R;
    colour[countX][1] = G;
    colour[countX][2] = B;
    speedUp();
    if (countX == 0) {
      nextCol();
    } else if (countX < 13 && checkBox() == true) { //checks for current column
      newBoxLength();
      nextCol();
    } else if (countX == 13 && checkBox() == true) { //win
      println("restart - win");
      score = score + 1;
      setup(); //reset board
      if (score >= 30) {
        currentBox = currentBox - 3;
      } else if (score >= 20) {
        currentBox = currentBox - 2;
      } else if (score >= 10) {
        currentBox = currentBox - 1;
      } 
    } else { //lose
      println("restart - lose");
      score = 0;
      setup(); //restart
    }
  }
  if (key == 'p') { //pause 
    move = !move;
  }
  if (key == 'c') {
    R = (int)random(0, 256);
    G = (int)random(0, 256);
    B = (int)random(0, 256);
  }
  if (key == 'r') {
    boxRadius = 15;
  }
  if (key == '1') {
    score = 0;
    setup();
    difficulty = "easy";
  }
  if (key == '2') {
    score = 0;
    setup();
    difficulty = "normal";
  }
  if (key == '3') {
    score = 0;
    setup();
    difficulty = "hard";
  }
}

void speedUp() { 
  if (difficulty == "easy") {
    speed = speed * 1.15f;
  } else if (difficulty == "normal") {
    speed = speed * 1.25f;
  } else if (difficulty == "hard") {
    speed = speed * 1.35f;
  }
}

void newBoxLength() {
  saved[countX][2] = saved[countX - 1][2] - abs((saved[countX][1] - (upperBoxLimit + lowerBoxLimit)/2)); //new box length
  if (upperBoxLimit <= saved[countX][1] && saved[countX][1] <= (upperBoxLimit + lowerBoxLimit)/2) {
    saved[countX][1] = saved[countX - 1][1]; //when box is above last, move yPos back
  }
  currentBox = saved[countX][2];
}

void nextCol() { 
  xPos = xPos + boxWidth; //move right
  //resets stack next to one before
  if (upperBoxLimit <= saved[countX][1] && saved[countX][1] <= (upperBoxLimit + lowerBoxLimit)/2) {//inside upper range
    yPos = (saved[countX][1]) * boxHeight; //moves yPos of moving stack back to stopped stack
  } 
  countX = countX + 1;
}

boolean checkBox() {
  upperBoxLimit = saved[countX - 1][1] - saved[countX - 1][2] + 1; //upper box grid position of first box
  lowerBoxLimit = saved[countX - 1][1] + saved[countX - 1][2] - 1; //lower box grid position of last box
  //for the blocks to be valid
  if (upperBoxLimit <= saved[countX][1] && saved[countX][1] <= lowerBoxLimit) {
    return true;
  } else { //out of bounds
    return false;
  }
}

int roundStepY(int a) {
  a = (int)(yPos/(height/8)+0.5);
  return a;
}

void grid() {
  strokeWeight(1);
  stroke(0);
  for (int vertical = 0; vertical <= width; vertical = vertical + (int)boxWidth) { //vertical lines
    line(vertical, 0, vertical, height);
  }
  for (int horizontal = 0; horizontal <= height; horizontal = horizontal + (int)boxHeight) { //horizontal lines
    line(0, horizontal, width, horizontal);
  }
}

void oldBox() {
  strokeWeight(1);
  for (int i = 0; i < saved.length; i = i + 1) { //repeats for all columns
    fill(colour[i][0], colour[i][1], colour[i][2]);
    for (int j = 0; j < saved[i][2]; j = j + 1) { //repeats for each box
      rect(saved[i][0], (saved[i][1] + j) * boxHeight, boxWidth, boxHeight, boxRadius);
    }
  }
} 
}
