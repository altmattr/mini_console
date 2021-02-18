// by Elise McCabe, converted to macquarie mini my Matt Roberts

package studentwork;

public class BoxCarrier extends mqapp.MQApp {

    float personX = 175; 
    float personY = 120;
    float speed = random (1, 6);
    boolean personRight = true; // therefore personLeft = false personRight
    boolean stopped = true;
    int counter = 0;
    float bounce = 0;
    float boxX = 430;
    float boxY = 140;
    boolean boxHeld = false; // this = boxDropped 
    float goal = random(100, 400);
    boolean gameWon = false;
    float dist;
    float endS;
    float scaleX;
    float scaleY;

    static public void main(String[] passedArgs) {
        runSketch(appletArgs, new BoxCarrier());
    }

    public void setup() {
        // original size is 500x200
        size(displayWidth, displayHeight);
        scaleX = displayWidth/500;
        scaleY = displayHeight/200;
        background(0, 122, 204);
        rectMode(CENTER);
        //frameRate(24); TODO: how to control the frame rate
    }

    public void draw() {
        scale(min(scaleX, scaleY));
        counter ++;
        background(0, 122, 204);
        fill(100, 200, 150);
        noStroke(); 
        rect(width/2, 180, 500, 63);
        if (gameWon) {
            if (counter < 60){
                background(255, 100, 0);
                fill(0);
                rect(width/2, 180, 500, 63);
            } else {
                personX = 101;
                personRight = true;
                gameWon = false;
                boxY = 140;
                boxX = random(70,430);
                goal = random(150, 350);
            }
            drawPerson();
        } else {
            if (stopped) {
                speed = 0;
            } else {
                speed = random (1, 6);
            }
            if (personX <= width-100 || personX >= 100) {
                if (personRight) {
                    personX += speed;
                } else {
                    personX -= speed;
                }
            } else {
                speed = 0;
                stopped = true;
            }
            if (personX >= width-100 || personX <= 100) {
                stopped = true;
            }
            if (boxHeld) {
                fill(30,170,200);
                noStroke();
                rect(goal, 150, 70, 10);
                stroke(0);
            }
            drawPerson();
            if (boxHeld == false) {
                fill(50, 180, 130);
                noStroke();
                rect(boxX, boxY, 30, 30);
                stroke(0);
            }
        }
    }

    public void drawPerson() {
        if (stopped == false) {
            bounce = 0;
        } else {
            if (counter % 12 == 0) {
                if (bounce == 2) {
                    bounce = 0;
                } else {
                    bounce = 2;
                }
            }
        }
        fill(0);
        rect(personX, personY + bounce, 30, 45);
        rect(personX, personY - 15 + bounce, 40, 30);
        rect(personX - 9, personY + 20, 5, 15);
        rect(personX + 9, personY + 20, 5, 15);
        fill(140);
        if (personRight) {
            rect(personX + 10, personY - 15  + bounce, 10, 5);
        } else {
            rect(personX - 10, personY - 15 + bounce, 10, 5);
        }
        if (boxHeld) {
            fill(103, 72, 47);
            noStroke();
            if (personRight) {
                rect(personX + 30, personY + bounce, 30, 30);
            } else {
                rect(personX -30, personY + bounce, 30, 30);
            }
            stroke(0);
        }
    }

    public void keyPressed() {
        if (key == 'a' || keyCode == LEFT) {
            if (personX >= 100) {
                personRight = false;
                stopped = false;
            }
        }
        if (key == 'd' || keyCode == RIGHT) {
            if (personX <= displayWidth-100) {
                personRight = true;
                stopped = false;
            }
        }
        if (key == 's' || keyCode == DOWN) {
            stopped = true;
            if (boxHeld == true) {
                boxHeld = false;
                boxY = 140;
                if (personRight) {
                    boxX = personX + 25;
                } else {
                    boxX = personX - 25;
                }
                if (boxX >= goal - 25 && boxX <= goal + 25) {
                    gameWon = true;
                    boxX = -100;
                    boxY = -100;
                    counter = 0;
                    dist = goal - 100;
                }
            }
        }
        if (key == 'w' || keyCode == UP) {
            if (boxHeld == false && personX >= boxX - 30 && personX <= boxX + 30) {
                boxHeld = true;
                stopped = true;
            }
        }
    }

}
