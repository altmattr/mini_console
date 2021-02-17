package studentwork;

import processing.core.*;

public class Stacker extends mqapp.MQApp {

    public String name() {
        return "Stacker";
    }

    public String author() {
        return "Andrew Kefala";
    }

    public String description() {
        return "Arcade money hog";
    }

    double[] xSpeed = new double[6];
    double[] xPos = new double[14];
    int[] lives = new int[14];
    boolean movingDown = true;
    boolean delay = false;
    boolean pause = false;
    int count = 0;
    double xGrid;
    double yGrid;
    int lvl = 0;
    boolean win = false;
    int difficulty = 1;
    boolean dead = false;
    int score;
    PFont uiFont, largeFont;

    static public void main(String[] passedArgs) {
        runSketch(appletArgs, new Stacker());
    }

    public void setup() {
        size(504, 882);
        xGrid = width / 8.0;
        yGrid = 9 * height / 128.0;
        for (int i = 0; i < xPos.length; i++) {
            xPos[i] = i * xGrid;
        }
        for (int i = 0; i < xPos.length; i++) {
            xSpeed[i] = width / 8.0;
        }
        for (int i = 0; i < xPos.length; i++) {
            lives[i] = 4;
        }

        uiFont = loadFont("shared/Avenir-LightOblique-28.vlw");
        largeFont = loadFont("shared/HiraMaruPro-W4-60.vlw");
    }

    public void draw() {

        background(67, 205, 153);
        score = 0;
        for (int i = 1; i < lvl; i++) {
            int sc = constrain(lives[i], 0, 4);
            score += sc;
        }
        if (win) {
            background(30, 30, 255);
            fill(0, 0, 20);
            textFont(largeFont);
            textAlign(CENTER, CENTER);
            text("YOU WON!", width / 2, height / 3);
            textFont(uiFont);
            textAlign(CENTER, CENTER);
            text("Press Space to play again", width / 2, 2 * height / 3);
            text("Score : " + score, width / 2, 7 * height / 12);
        } else if (dead) {
            background(0, 0, 20);
            fill(30, 30, 255);
            textFont(largeFont);
            textAlign(CENTER, CENTER);
            text("GAME OVER!", width / 2, height / 3);
            textFont(uiFont);
            textAlign(CENTER, CENTER);
            text("Press Space to play again", width / 2, 2 * height / 3);
            text("Score : " + score, width / 2, 7 * height / 12);
        } else {
            move();
            display();
            if (lives[13] <= 0) {
                dead = true;
            }
        }
    }

    void move() {
        count++;
        count = count % (90 / (difficulty * (lvl + 1)));

        if (count == 0) {
            delay = false;
        } else {
            delay = true;
        }

        if (xPos[lvl] <= 0) {
            movingDown = true;
        }
        if (xPos[lvl] >= ((8 - lives[lvl]) * width / 8) - (width / 16)) {
            movingDown = false;
        }

        if (delay || pause) {
            xPos[lvl] = xPos[lvl] + 0;
        } else if (!delay && movingDown && !pause) {
            xPos[lvl] = xPos[lvl] + xSpeed[lvl];
        } else if (!delay && !movingDown && !pause) {
            xPos[lvl] = xPos[lvl] - xSpeed[lvl];
        }
    }

    void display() {
        strokeWeight(6);
        stroke(0, 0, 20);
        fill(30, 30, 255);
        for (int j = 0; j <= lvl; j++) {
            for (int i = 0; i < lives[j]; i++) {
                rect((float)(xPos[j] + (i * yGrid)), (float)((height - yGrid) - (j * yGrid)), (float)xGrid, (float)yGrid);
            }
        }
    }

    void startAgain() {
        count = 0;
        pause = false;
        delay = false;
        movingDown = true;
        xGrid = width / 8.0;
        yGrid = 9 * height / 128.0;
        lvl = 0;
        win = false;
        dead = false;
        for (int i = 0; i < xPos.length; i++) {
            xPos[i] = i * yGrid;
        }
        for (int i = 0; i < xPos.length; i++) {
            xSpeed[i] = width / 8.0;
        }
        for (int i = 0; i < xPos.length; i++) {
            lives[i] = 4;
        }
    }

    public void keyPressed() {
        if (keyCode == ' ') {
            if (win || dead) {
                startAgain();
            } else {
                if (lvl > 0) {
                    if (xPos[lvl] >= xPos[lvl - 1] + 4 * xGrid || xPos[lvl] <= xPos[lvl - 1] - 4 * xGrid) {
                        for (int i = lvl; i < lives.length; i++) {
                            lives[i] -= 4;
                        }
                    } else if (xPos[lvl] >= xPos[lvl - 1] + 3 * xGrid) {
                        for (int i = lvl; i < lives.length; i++) {
                            lives[i] -= 3;
                        }
                    } else if (xPos[lvl] <= xPos[lvl - 1] - 3 * xGrid) {
                        xPos[lvl] = xPos[lvl] + 3 * xGrid;
                        for (int i = lvl; i < lives.length; i++) {
                            lives[i] -= 3;
                        }
                    } else if (xPos[lvl] >= xPos[lvl - 1] + 2 * xGrid) {
                        for (int i = lvl; i < lives.length; i++) {
                            lives[i] -= 2;
                        }
                    } else if (xPos[lvl] <= xPos[lvl - 1] - 2 * xGrid) {
                        xPos[lvl] = xPos[lvl] + 2 * yGrid;
                        for (int i = lvl; i < lives.length; i++) {
                            lives[i] -= 2;
                        }
                    } else if (xPos[lvl] >= xPos[lvl - 1] + 1 * xGrid) {
                        for (int i = lvl; i < lives.length; i++) {
                            lives[i] -= 1;
                        }
                    } else if (xPos[lvl] <= xPos[lvl - 1] - 1 * xGrid) {
                        xPos[lvl] = xPos[lvl] + yGrid;
                        for (int i = lvl; i < lives.length; i++) {
                            lives[i] -= 1;
                        }
                    }
                }
                if (lvl < 13) {
                    xSpeed[lvl] = 0;
                    lvl++;
                    xPos[lvl] = xPos[lvl - 1];
                } else if (lvl == 13 && lives[13] > 0) {
                    win = true;
                }
            }
        }
    }

}
