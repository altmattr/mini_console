package studentwork;

import processing.core.*;
import processing.data.*;

//import processing.sound.*;


public class GameAndWatch extends mqapp.MQApp {

    public String name(){return "GameAndWatch";}
    public String author() {return "created by Tanner Schineller";}
    public String description(){return "Based on the classic game";}


        public void setup() {
            size(512, 348);
            // This section is here to ensure that the contained code is only run on the initial setup of the program, and not each time setup is called.
            if (firstSetup) {
                frameRate(30);
                alpha = 255;
                createPictographicMenuButtons();
                createSettingsButtons();
                firstSetup = false;
            }

            // In order to inprve efficiency of switching between screens, when entering into the settings menu
            // the majority of the setup is ignored (which) includes a large amount of random number calculation
            if (z == 3) {
                createSettingsButtons();
            } else {
                createInGameButtons();
                createPauseMenuButtons();
                createConfirmationButtons();
                createHighScoreButtons();
                //Varible Initialization
                score = 0;
                position = 0;
                pastFrameCount = 0;
                i = 1;
                misses = 0;
                open = true;
                safe = true;
                alive = true;
                pressed = false;
                drawn = false;
                startup = false;
                paused = false;
                reset = false;
                tools = new boolean[5][6];
                rotation = random(-PI / 8, PI / 8);
                startRotation = rotation;

                // The timing of this program is based around the framerate of the game
                // under the assumption that on any modern computer it should run at the preset 30 frames a second
                // So each time setup is called the frameCount is set to 0 so as to allow the running of the startup sequence
                frameCount = 0;

                // This block imports all the sound files used
                /*
                hammerB = new SoundFile(this, "hammerBeep.wav");
                bucketB = new SoundFile(this, "bucketBeep.wav");
                plierB = new SoundFile(this, "plierBeep.wav");
                screwdriverB = new SoundFile(this, "screwdriverBeep.wav");
                spannerB = new SoundFile(this, "spannerBeep.wav");
                deathB = new SoundFile(this, "deathBeep.wav");
                doorB = new SoundFile(this, "doorBeep.wav");
                */

                //The following nested loop fills a the groundCover matrix with procedurally generated grass, where the variables include the strokeWeight,
                //color(RGB), number of points, followed by that number of randomly generated points.

                //groundSaturation is the number of squigles that are generated
                groundSaturation = 500;
                groundCover = new int[groundSaturation][4];
                for (int g = 0; g < groundSaturation; g++) {
                    //generates the g squigles strokeWeight
                    groundCover[g][0] = PApplet.parseInt(random(2, 5));
                    //generates the g squigles red, green,  and blue values for fill()
                    groundCover[g][1] = PApplet.parseInt(random(45, 50));
                    groundCover[g][2] = PApplet.parseInt(random(120, 150));
                    groundCover[g][3] = PApplet.parseInt(random(40, 50));
                    //generates how many vertices should be included in the squigle
                    int numberOfVertices = PApplet.parseInt(random(3, 10));
                    //sets the g column length to be large enough to accomodate the previously generated numberOfVertices
                    groundCover[g] = (int[]) expand(groundCover[g], 3 + numberOfVertices * 2);
                    //randomly chooses the startpoint of the squigle and for both x and y, from now on the even column entries represent x's and the odd ones are y's
                    groundCover[g][4] = PApplet.parseInt(random(30, 380));
                    groundCover[g][5] = PApplet.parseInt(random(295, 320));
                    //this for loop fills the remainder of slots in the column with the rest of the squigles x's and y's based on the starting point
                    for (int c = 0; c < numberOfVertices * 2 - 3; c++) {
                        //this one declaration is able to set both the x's and y's based on the modulus of the current loop iteration value 'c'.
                        //the delcaration checks back two spots in the matrix and bases the new random values off of it.
                        //and whether of not the declaration is settings a value in the acceptable x range or the acceptable y range
                        //is handled by multiplying by the 0 created by (some value modulus 2)
                        groundCover[g][6 + c] = PApplet.parseInt(random(groundCover[g][4 + c] - ((groundCover[g][4 + c] - 295) % 2), groundCover[g][4 + c] + 20 - (15 * ((6 + c) % 2))));
                    }
                    //DEBUGGING println(groundCover[g]);
                }

                //prepares the fonts

                //font imports

                retro = createFont("DS-DIGI.TTF", 72);
                arialbd = createFont("arialbd.ttf", 36);
                arial = createFont("arial.ttf", 36);


                highScores = loadTable("highscores.csv", "header, csv"); //imports table file

                doorTimer = PApplet.parseInt(random(3, 8));
                // DEBUGGING println(doorTimer);

                circleGap = 4;
                //number of cirlces that fit accross the screen
                numberOfCircles = 8;
                diameter = width / numberOfCircles - circleGap;
            }
        }


        public void draw() {
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
            }
        }

        public void mousePressed() {
            switch (z) {
                case 0:
                    for (PictographicButton b : pictographicMenuButtons) {
                        if (b.mouseOver()) {
                            b.display();
                            b.action();
                        }
                    }
                    break;
                case 1:
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
                        if (highScoreCheck() < 4 && highScoreCheck() >= 0) {
                            z = 5;
                        } else {
                            z = 0;
                            setup();
                        }
                    }
                    break;
                case 2:
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
                    if ((misses * numberOfCircles) + score >= ((height / (diameter + circleGap)) * numberOfCircles) && startup) {
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
            }
        }

        //User Input
        public void keyPressed() {
            switch (z) {
                case 0:
                    //stuff
                    break;
                case 1:
                    if (key == CODED && alive && !pressed && (misses != 3 && z == 1)) {
                        rotation = random(-PI / 8, PI / 8);
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
                case 2:
                    if (key == CODED && alive && !pressed) {
                        rotation = random(-PI / 8, PI / 8);
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
                            name = name.substring(0, name.length() - 1);
                            println(name);
                        } else if (keyCode == ENTER) {
                            int row = highScoreCheck();
                            for (int i = 4; i > row; i--) {
                                highScores.setString(i, 0, highScores.getString(i - 1, 0));
                                highScores.setInt(i, 1, highScores.getInt(i - 1, 1));
                            }
                            highScores.setString(row, 0, name);
                            highScores.setInt(row, 1, score);
                            saveTable(highScores, "data/highScores.csv");
                            highScore = false;
                            z = 4;
                        } else if (key != CODED && keyCode != BACKSPACE) {
                            pushStyle();
                            textSize(30);
                            if (textWidth(name) < width / 4 - 4) {
                                name += key;
                                println(name);
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

        //Does the actual interpretation of the groundCover Matrix in a set of nested loops.
        public void ground() {
            pushMatrix();
            for (int g = 0; g < groundSaturation; g++) {
                //DEBUGGING println(g);
                strokeWeight(groundCover[g][0]);
                noFill();
                stroke(groundCover[g][1], groundCover[g][2], groundCover[g][3], alpha);
                beginShape();
                vertex(groundCover[g][4], groundCover[g][5]);
                for (int c = 0; c < groundCover[g].length - 7; c += 2) {
                    quadraticVertex(groundCover[g][c + 4] + groundCover[g][0] + 2, groundCover[g][c + 5] - groundCover[g][0] - 2, groundCover[g][c + 6], groundCover[g][c + 7]);
                }
                endShape();
            }
            popMatrix();
        }

        //Controls the drawing of the right door
        public void rightDoor() {
            pushMatrix();
            strokeWeight(2);
            if (open) {
                fill(backgroundColor);
                quad(455, 225, 455, 300, 485, 310, 485, 210);
                fill(0);
                quad(462, 230, 462, 250, 475, 250, 475, 225);
                ellipse(488, 265, 2, 6);
                ellipse(481, 265, 4, 6);
            } else {
                fill(0);
                rect(425, 235, 20, 20);
                ellipse(423, 265, 6, 6);
            }
            popMatrix();
        }

        //Draws the right building
        public void rightBuilding() {
            pushMatrix();
            pushMatrix();
            fill(backgroundColor);
            noStroke();
            rect(405, 290, 10, 11);
            popMatrix();
            strokeWeight(2);
            stroke(0);
            line(405, 200, 405, 300);
            noStroke();
            fill(0);
            pushMatrix();
            translate(390, 200);
            rotate(-PI / 6);
            rect(0, 0, 90, 9);
            popMatrix();
            pushMatrix();
            translate(468, 155);
            rotate(PI / 6);
            rect(0, 0, 90, 9);
            popMatrix();
            fill(backgroundColor);
            stroke(0);
            rect(415, 225, 40, 75);
            popMatrix();
        }

        //Draws the trees, I used a seperate drawing program I wrote to more easily acquire all of these points,
//then output the lines of code to a .txt file.
        public void trees() {
            pushMatrix();
            noStroke();
            fill(treeColor);
            triangle(501, 66, 482, 93, 520, 96);
            triangle(504, 80, 476, 116, 526, 120);
            triangle(503, 99, 473, 133, 523, 139);
            triangle(502, 116, 468, 150, 525, 161);
            triangle(498, 136, 464, 165, 532, 191);
            triangle(460, 94, 472, 110, 452, 111);
            triangle(461, 102, 454, 121, 469, 119);
            triangle(462, 105, 447, 124, 473, 118);
            triangle(463, 111, 450, 134, 473, 125);
            triangle(464, 120, 448, 146, 471, 139);
            triangle(461, 131, 456, 151, 467, 149);
            triangle(432, 107, 443, 123, 423, 124);
            triangle(433, 115, 421, 131, 446, 130);
            triangle(435, 123, 418, 142, 448, 138);
            triangle(436, 133, 413, 155, 449, 148);
            triangle(434, 141, 409, 174, 457, 162);
            triangle(433, 148, 400, 195, 468, 163);
            popMatrix();
        }

        //draws the the left door, and balconies
        public void leftBackdrop() {
            pushMatrix();
            fill(balconyColor);
            noStroke();
            for (int i = 60; i <= 190; i += 65) {
                rect(0, i, 60, 10);
            }
            for (int i = 102; i <= 168; i += 65) {
                rect(0, i, 50, 4);
            }
            stroke(balconyColor);
            strokeWeight(2);
            for (int i = 0; i < 44; i += 11) {
                line(7 + i, 130, 7 + i, 105);
            }
            for (int i = 0; i < 44; i += 11) {
                line(7 + i, 195, 7 + i, 170);
            }

            stroke(0);
            fill(backgroundColor);
            strokeWeight(2);
            rect(-1, 225, 40, 75);

            fill(0);
            ellipse(31, 265, 6, 6);
            rect(8, 240, 17, 15);
            popMatrix();
        }

        //Draws the bell man in the top right corner, seen during the startup phase. Was draw using a the same drawing program I wrote to draw Mr.GW's head
        public void bellMan() {
            pushMatrix();
            pushStyle();
            translate(376, -6);
            scale(.125f);
            fill(0);
            noStroke();
            fill(0);
            //leftBell
            beginShape();
            vertex(318, 748);
            curveVertex(318, 748);
            curveVertex(333, 682);
            curveVertex(362, 666);
            curveVertex(396, 622);
            curveVertex(393, 588);
            curveVertex(363, 560);
            curveVertex(318, 555);
            curveVertex(271, 593);
            curveVertex(201, 605);
            curveVertex(199, 612);
            curveVertex(242, 662);
            curveVertex(224, 664);
            curveVertex(217, 692);
            curveVertex(228, 704);
            curveVertex(247, 704);
            curveVertex(258, 696);
            curveVertex(262, 683);
            curveVertex(317, 744);
            endShape();

            //right Bell
            beginShape();
            vertex(666, 695);
            curveVertex(666, 695);
            curveVertex(632, 688);
            curveVertex(594, 649);
            curveVertex(565, 607);
            curveVertex(543, 595);
            curveVertex(524, 596);
            curveVertex(481, 621);
            curveVertex(476, 644);
            curveVertex(486, 691);
            curveVertex(514, 743);
            curveVertex(501, 799);
            curveVertex(567, 761);
            curveVertex(568, 775);
            curveVertex(605, 780);
            curveVertex(609, 759);
            curveVertex(594, 746);
            curveVertex(670, 696);
            endShape();

            //black portion of Bell Man
            beginShape();
            vertex(1200, 573);
            curveVertex(962, 574);
            curveVertex(907, 563);
            curveVertex(827, 532);
            curveVertex(800, 511);
            curveVertex(774, 511);
            curveVertex(699, 567);
            curveVertex(634, 568);
            curveVertex(511, 528);
            curveVertex(451, 520);
            curveVertex(423, 486);
            curveVertex(421, 439);
            curveVertex(479, 428);
            curveVertex(529, 451);
            curveVertex(539, 491);
            curveVertex(584, 507);
            curveVertex(661, 513);
            curveVertex(745, 447);
            curveVertex(755, 408);
            curveVertex(730, 390);
            curveVertex(674, 415);
            curveVertex(586, 408);
            curveVertex(535, 360);
            curveVertex(474, 357);
            curveVertex(439, 331);
            curveVertex(447, 311);
            curveVertex(473, 299);
            curveVertex(495, 295);
            curveVertex(496, 256);
            curveVertex(424, 253);
            curveVertex(420, 244);
            curveVertex(426, 234);
            curveVertex(469, 228);
            curveVertex(488, 167);
            curveVertex(524, 124);
            curveVertex(574, 95);
            curveVertex(668, 86);
            curveVertex(722, 104);
            curveVertex(769, 137);
            curveVertex(795, 179);
            curveVertex(800, 204);
            curveVertex(802, 246);
            curveVertex(800, 292);
            curveVertex(784, 325);
            curveVertex(783, 369);
            curveVertex(827, 393);
            curveVertex(1023, 413);
            vertex(1200, 413);
            endShape();

            //mouth
            fill(backgroundColor);
            beginShape();
            vertex(606, 332);
            curveVertex(614, 328);
            curveVertex(634, 327);
            curveVertex(658, 317);
            curveVertex(701, 296);
            curveVertex(715, 288);
            curveVertex(737, 287);
            curveVertex(751, 303);
            curveVertex(751, 327);
            curveVertex(734, 336);
            curveVertex(713, 341);
            curveVertex(700, 346);
            curveVertex(694, 354);
            curveVertex(679, 370);
            curveVertex(622, 370);
            curveVertex(604, 360);
            curveVertex(603, 336);
            curveVertex(603, 336);
            vertex(606, 332);
            endShape();

            //Eye
            beginShape();
            vertex(610, 234);
            curveVertex(625, 250);
            curveVertex(630, 250);
            curveVertex(590, 283);
            curveVertex(545, 285);
            curveVertex(529, 269);
            curveVertex(528, 256);
            curveVertex(542, 241);
            curveVertex(573, 233);
            curveVertex(612, 235);
            vertex(610, 234);
            endShape();

            popMatrix();
            popStyle();
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
            if (frameCount - pastFrameCount == doorTimer * 30 && alive) {
                open = !open;
                pastFrameCount = frameCount;
                doorTimer = PApplet.parseInt(random(3, 8));
                println(doorTimer);
            }
        }

        //sets the difficulty value
        public void difficulty() {
            difficulty = PApplet.parseInt((frameCount) / 300 - misses * 2);
            difficulty = constrain(difficulty, 0, 7);
            spawnDifficulty = constrain(difficulty, 0, 4);
        }

        //returns the row that the high new high score should occupy,
//if there isn't a new high score it returns the value 100
//which is outside the bounds of the highscore table and so it is ignored
        public int highScoreCheck() {
            int row = 5;
            for (int i = highScores.getRowCount() - 1; i > 0; i--) {
                if (highScores.getInt(i, 1) <= score) {
                    row--;
                    continue;
                } else if (i == highScores.getRowCount() - 1) {
                    return 100;
                } else if (i < highScores.getRowCount() - 1) {
                    return row;
                }
            }
            return 0;
        }
//The following is all of the functions that create the fill the button class object arrays with the relevant buttons


        //Creates the main menu buttons
        public void createPictographicMenuButtons() {
            int index = 0;
            //Left most button that activates Full mode
            pictographicMenuButtons[index++] = new PictographicButton(new PVector(width / 2 - 165, height / 2 - 25), 100, 100, backgroundColor) {

                @Override
                public void action() {
                    z = 1;
                    setup();
                }

                @Override
                public void pictograph() {
                    //PFont retro = createFont("DS-DIGI.TTF", 72); requires import of respective font
                    int tempI = i;
                    boolean tempAlive = alive;
                    pushMatrix();
                    i = 1;
                    alive = true;
                    translate(centerCoor.x, centerCoor.y);
                    textAlign(CENTER, CENTER);
                    //textFont(retro); requires import of respective font
                    textSize(30);
                    fill(0);
                    text("1981", 0, -36);
                    MrGW();
                    popMatrix();
                    i = tempI;
                    alive = tempAlive;
                }

                @Override
                public void overlay() {
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
                    text("Play Mr.GW with the original numerical scoring.", centerCoor.x, centerCoor.y, buttonWidth - 6, buttonHeight - 6);
                    popStyle();
                }
            };

//Right most button that activates the marking mode
            pictographicMenuButtons[index++] = new PictographicButton(new PVector(width / 2 + 165, height / 2 - 25), 100, 100, backgroundColor) {

                @Override
                public void action() {
                    z = 2;
                    setup();
                }

                @Override
                public void pictograph() {
                    int tempI = i;
                    boolean tempAlive = alive;
                    pushMatrix();
                    i = 1;
                    alive = true;
                    translate(centerCoor.x, centerCoor.y);
                    gradientCircle(new PVector(-30, -30), 30, missColor);
                    gradientCircle(new PVector(30, -30), 30, scoreColor);
                    MrGW();
                    popMatrix();
                    i = tempI;
                    alive = tempAlive;
                }

                @Override
                public void overlay() {
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
                    text("Play the marking version of Mr.GW with circular scoring.", centerCoor.x, centerCoor.y, buttonWidth - 6, buttonHeight - 6);
                    popStyle();
                }
            };

// the left middle button fo the settings menu
            pictographicMenuButtons[index++] = new PictographicButton(new PVector(width / 2 - 55, height / 2 - 25), 100, 100, backgroundColor) {

                @Override
                public void action() {
                    z = 3;
                    setup();
                }

                @Override
                public void pictograph() {
                    pushMatrix();
                    translate(centerCoor.x - 7, centerCoor.y - 5);
                    scale(.8f);
                    gear();
                    popMatrix();
                }

                @Override
                public void overlay() {
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
                    text("Open the settings menu to alter settings such as the color scheme.", centerCoor.x, centerCoor.y, buttonWidth - 6, buttonHeight - 6);
                    popStyle();
                }
            };

//the right middle button that opens the leaderboard
            pictographicMenuButtons[index++] = new PictographicButton(new PVector(width / 2 + 55, height / 2 - 25), 100, 100, backgroundColor) {
                @Override
                public void action() {
                    z = 4;
                    setup();
                }

                @Override
                public void pictograph() {
                    pushStyle();
                    pushMatrix();
                    translate(centerCoor.x, centerCoor.y);
                    trophy();
                    popMatrix();
                    popStyle();
                }

                @Override
                public void overlay() {
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
                    text("View or reset the high scores.", centerCoor.x, centerCoor.y, buttonWidth - 6, buttonHeight - 6);
                    popStyle();
                }
            };
        }

        //creates the buttons seen within the settings menu
        public void createSettingsButtons() {
            int index = 0;

            if (bright == true) {
                settingsButts[index++] = new TextButton(new PVector(width / 2, 75), "Color: Bright", 30, backgroundColor) {
                    @Override
                    public void action() {
                        backgroundColor = 0xffAEC0C1;
                        treeColor = 0xff5B9B43;
                        balconyColor = 0xffA56A32;
                        alpha = 50;
                        bright = false;
                        setup();
                    }
                };
            } else {
                settingsButts[index++] = new TextButton(new PVector(width / 2, 75), "Color: Retro", 30, backgroundColor) {
                    @Override
                    public void action() {
                        backgroundColor = 0xffFFFFFF;
                        treeColor = 0xff18C824;
                        balconyColor = 0xffFF5500;
                        alpha = 255;
                        bright = true;
                        setup();
                    }
                };
            }

            settingsButts[index++] = new TextButton(new PVector(width / 2, 300), "Apply", 30, backgroundColor) {
                @Override
                public void action() {
                    z = 0;
                    createPictographicMenuButtons();
                    setup();
                }
            };
        }

        //creates the buttons seen within the game
        public void createInGameButtons() {
            int index = 0;

            inGameButtons[index++] = new PictographicButton(new PVector(487, 25), 40, 40, backgroundColor) {
                @Override
                public void action() {
                    pauseFrame = frameCount;
                    exit = true;
                    firstPauseFrame = true;
                }

                @Override
                public void pictograph() {
                    pushStyle();
                    pushMatrix();
                    strokeWeight(5);
                    line(upperLeftCoor.x + 11, upperLeftCoor.y + 11, lowerRightCoor.x - 11, lowerRightCoor.y - 11);
                    line(lowerRightCoor.x - 11, upperLeftCoor.y + 11, upperLeftCoor.x + 11, lowerRightCoor.y - 11);
                    popMatrix();
                    popStyle();
                }

                @Override
                public void overlay() {
                }
            };

            inGameButtons[index++] = new PictographicButton(new PVector(442, 25), 40, 40, backgroundColor) {
                @Override
                public void action() {
                    paused = true;
                    pauseFrame = frameCount;
                    firstPauseFrame = true;
                }

                @Override
                public void pictograph() {
                    pushStyle();
                    pushMatrix();
                    noStroke();
                    fill(0);
                    rectMode(CENTER);
                    rect(centerCoor.x - buttonWidth / 6, centerCoor.y, buttonWidth / 5, 3 * buttonHeight / 5);
                    rect(centerCoor.x + buttonWidth / 6, centerCoor.y, buttonWidth / 5, 3 * buttonHeight / 5);
                    popMatrix();
                    popStyle();
                }

                @Override
                public void overlay() {
                }
            };
        }

        //the resume button for when the game is paused
        public void createPauseMenuButtons() {
            int index = 0;

            pauseMenuButtons[index++] = new TextButton(new PVector(width / 2, height / 2), "Resume", 30, backgroundColor) {
                @Override
                public void action() {
                    paused = false;
                    frameCount = pauseFrame;
                }
            };
        }

        //creates yes and no buttons for the game
        public void createConfirmationButtons() {
            int index = 0;

            confirmationButtons[index++] = new TextButton(new PVector((width / 2 - 75), height / 2), 90, 45, "Yes", 30, backgroundColor) {
                @Override
                public void action() {
                    if (exit) {
                        exit = false;
                        z = 0;
                        setup();
                    } else if (reset) {
                        highScores = loadTable("highscoresReset.csv", "header, csv");
                        saveTable(highScores, "data/highscores.csv", "header, csv");
                        reset = false;
                    }
                }
            };

            confirmationButtons[index++] = new TextButton(new PVector((width / 2 + 75), height / 2), 90, 45, "No", 30, backgroundColor) {
                @Override
                public void action() {
                    if (exit) {
                        exit = false;
                        frameCount = pauseFrame;
                    } else if (reset) {
                        reset = false;
                    }
                }
            };
        }

        // creates the buttons seen within the highscore menu
        public void createHighScoreButtons() {
            int index = 0;

            highScoreButtons[index++] = new TextButton(new PVector((width / 2 - 75), 306), 90, 45, "Back", 30, backgroundColor) {
                @Override
                public void action() {
                    z = 0;
                    setup();
                }
            };

            highScoreButtons[index++] = new TextButton(new PVector((width / 2 + 75), 306), 90, 45, "Reset", 30, backgroundColor) {
                @Override
                public void action() {
                    reset = true;
                }
            };
        }

        //Button and setup variables
        int z = 0;                                                       //z is the variable that determines the mode os the game
        boolean firstSetup = true, bright = true;                        // a couple booleans that determine the colorscheme and the whether setup is being run for the first

        //initializes the arrays for all of the buttons
        PictographicButton[] pictographicMenuButtons = new PictographicButton[4];
        TextButton[] settingsButts = new TextButton[2];
        PictographicButton[] inGameButtons = new PictographicButton[2];
        TextButton[] pauseMenuButtons = new TextButton[1];
        TextButton[] confirmationButtons = new TextButton[2];
        TextButton[] highScoreButtons = new TextButton[2];

//Mr. GW Variables
//Imports the official processing foundation sound library

        //SoundFile hammerB, bucketB, plierB, screwdriverB, spannerB, deathB, doorB;

        //various variables
        int score, position, doorTimer, pastFrameCount, i, misses, alpha, deathFrame, pauseFrame;
        int lastTime = 0, deadPosition = 0;
        int groundSaturation, difficulty, spawnDifficulty, fallenTools;
        int[][] groundCover;
        int[] toolStart = {2, 5, 11, 19, 27}; // the toolstart offset is here to ensure that no two tools even update/fall at the same time, this is done by offsetting the each tool frame by a prime number
        boolean open, safe, alive, timeSaved, pressed, drawn, startup, paused, exit, firstPauseFrame, reset, highScore;
        boolean[][] tools;
        float rotation, startRotation;
        PFont retro, arialbd, arial;
        int backgroundColor = 0xffFFFFFF, treeColor = 0xff18C824, balconyColor = 0xffFF5500; // colors for the background and backdrop
        String name = "";

        // Circle variables
        int diameter, circleGap, numberOfCircles;
        int missColor = 0xffFF0000, scoreColor = 0xff000000; //colors for the circles


        Table highScores;

        //draws a gradient circle
        public void gradientCircle(PVector coor, int d, int c) {
            diameter = d;
            for (int i = diameter; i > 0; i--) {
                noStroke();
                fill(c, i);
                ellipse(coor.x, coor.y, diameter - i, diameter - i);
            }
        }

        //draws a hammer
        public void hammer() {
            pushMatrix();
            fill(0);
            noStroke();
            arc(0, -1, 24, 16, -PI / 2, 0);
            fill(backgroundColor);
            ellipse(5, 1, 14, 9);
            fill(0);
            rect(-10, -9, 13, 7, 2);
            strokeWeight(2);
            stroke(0);
            line(0, -10, 0, 32);
            strokeWeight(4);
            line(0, 8, 0, 32);
            popMatrix();
        }

        //draws a bucket
        public void bucket() {
            pushMatrix();
            rotate(9 * PI / 10);
            fill(0);
            noStroke();
            quad(-13, -10, 13, -10, 10, 10, -10, 10);
            ellipse(0, -19, 10, 6);
            noFill();
            strokeWeight(3);
            stroke(0);
            ellipse(0, -10, 23, 20);
            popMatrix();
        }

        //draws pliers
        public void pliers() {
            pushMatrix();
            scale(1.5f);
            fill(0);
            noStroke();
            rect(-4, -2, 8, 4);
            quad(-4, -2, 4, -2, 3, -7, -3, -7);
            quad(3, -7, -3, -7, -2, -10, 2, -10);
            fill(backgroundColor);
            ellipse(0, -3, 4, 4);
            stroke(backgroundColor);
            strokeWeight(.5f);
            line(0, -2, 0, -10);
            strokeWeight(2);
            stroke(0);
            noFill();
            bezier(-2, 0, -8, 10, -7, 10, -6, 15);
            bezier(2, 0, 8, 10, 7, 10, 6, 15);
            popMatrix();
        }

        //draws a screwdriver
        public void screwdriver() {
            pushMatrix();
            scale(1.5f);
            fill(0);
            noStroke();
            rect(-3, -10, 6, 11, 2);
            triangle(0, -1, -3, 3, 3, 3);
            quad(-0, 10, 0, 10, 2, 12, -2, 12);
            quad(2, 12, -2, 12, -1, 17, 1, 17);
            strokeWeight(2);
            stroke(0);
            line(0, 0, 0, 10);
            popMatrix();
        }

        //draws a spanner
        public void spanner() {
            pushMatrix();
            scale(1.75f);
            fill(0);
            noStroke();
            rect(-2, -6, 4, 12);
            pushMatrix();
            translate(0, -8);
            rotate(PI / 8);
            ellipse(0, 0, 10, 7);
            fill(backgroundColor);
            rect(-2, -4, 4, 5, 1);
            popMatrix();
            pushMatrix();
            fill(0);
            translate(0, 8);
            rotate(PI / 8);
            ellipse(0, 0, 10, 7);
            fill(backgroundColor);
            rect(-2, -1, 4, 5, 1);
            popMatrix();
            popMatrix();
        }

        //Draws a dead face to display the misses
        public void deadHead(int i) {
            pushMatrix();
            fill(0);
            noStroke();
            ellipse(340 - i * 40, 20, 30, 30);
            popMatrix();
            pushMatrix();
            strokeWeight(2);
            stroke(backgroundColor);
            translate(333 - i * 40, 15);
            line(-3, -3, 3, 3);
            line(-3, 3, 3, -3);
            popMatrix();
            pushMatrix();
            translate(346 - i * 40, 15);
            line(-3, -3, 3, 3);
            line(-3, 3, 3, -3);
            popMatrix();
            pushMatrix();
            noStroke();
            fill(backgroundColor);
            ellipse(340 - i * 40, 25, 15, 5);
            ellipse(335 - i * 40, 27, 5, 5);
            ellipse(345 - i * 40, 27, 5, 5);
            popMatrix();
        }

        //draws the gears shown in the main menu settings button
        public void gear() {
            pushStyle();
            pushMatrix();
            fill(0);
            noStroke();
            rotate(-PI / 10);
            scale(.17f);
            translate(-960, -540);
            beginShape();
            curveVertex(1103, 398);
            curveVertex(1118, 356);
            curveVertex(1162, 355);
            curveVertex(1178, 398);
            curveVertex(1224, 417);
            curveVertex(1266, 391);
            curveVertex(1297, 420);
            curveVertex(1282, 469);
            curveVertex(1294, 494);
            curveVertex(1342, 500);
            curveVertex(1353, 543);
            curveVertex(1307, 567);
            curveVertex(1303, 603);
            curveVertex(1341, 642);
            curveVertex(1319, 681);
            curveVertex(1266, 671);
            curveVertex(1237, 697);
            curveVertex(1240, 746);
            curveVertex(1200, 761);
            curveVertex(1167, 724);
            curveVertex(1110, 725);
            curveVertex(1083, 762);
            curveVertex(1043, 748);
            curveVertex(1039, 693);
            curveVertex(1011, 665);
            curveVertex(970, 671);
            curveVertex(953, 638);
            curveVertex(977, 598);
            curveVertex(973, 572);
            curveVertex(928, 547);
            curveVertex(936, 507);
            curveVertex(987, 496);
            curveVertex(1003, 469);
            curveVertex(984, 422);
            curveVertex(1015, 394);
            curveVertex(1057, 416);
            curveVertex(1101, 399);
            curveVertex(1103, 398);
            endShape();

            beginShape();
            curveVertex(943, 473);
            curveVertex(970, 479);
            curveVertex(983, 455);
            curveVertex(963, 432);
            curveVertex(966, 402);
            curveVertex(992, 387);
            curveVertex(988, 361);
            curveVertex(961, 355);
            curveVertex(948, 334);
            curveVertex(957, 308);
            curveVertex(935, 286);
            curveVertex(912, 300);
            curveVertex(875, 284);
            curveVertex(865, 262);
            curveVertex(836, 263);
            curveVertex(829, 288);
            curveVertex(794, 301);
            curveVertex(771, 289);
            curveVertex(746, 307);
            curveVertex(758, 332);
            curveVertex(746, 356);
            curveVertex(715, 364);
            curveVertex(712, 389);
            curveVertex(734, 404);
            curveVertex(740, 430);
            curveVertex(724, 448);
            curveVertex(736, 472);
            curveVertex(760, 469);
            curveVertex(784, 495);
            curveVertex(788, 524);
            curveVertex(813, 533);
            curveVertex(828, 512);
            curveVertex(876, 515);
            curveVertex(887, 532);
            curveVertex(918, 525);
            curveVertex(918, 496);
            curveVertex(938, 473);
            curveVertex(938, 473);
            endShape();

            beginShape();
            curveVertex(725, 539);
            curveVertex(757, 526);
            curveVertex(773, 547);
            curveVertex(825, 554);
            curveVertex(842, 527);
            curveVertex(876, 541);
            curveVertex(876, 578);
            curveVertex(897, 593);
            curveVertex(935, 588);
            curveVertex(951, 619);
            curveVertex(928, 644);
            curveVertex(933, 673);
            curveVertex(968, 692);
            curveVertex(966, 725);
            curveVertex(923, 734);
            curveVertex(907, 764);
            curveVertex(922, 801);
            curveVertex(900, 822);
            curveVertex(867, 805);
            curveVertex(820, 820);
            curveVertex(808, 855);
            curveVertex(774, 856);
            curveVertex(760, 818);
            curveVertex(722, 802);
            curveVertex(701, 817);
            curveVertex(673, 797);
            curveVertex(684, 762);
            curveVertex(674, 741);
            curveVertex(634, 735);
            curveVertex(628, 703);
            curveVertex(662, 682);
            curveVertex(667, 654);
            curveVertex(641, 626);
            curveVertex(658, 595);
            curveVertex(694, 600);
            curveVertex(722, 573);
            curveVertex(722, 541);
            curveVertex(725, 539);
            endShape();

            fill(backgroundColor);
            ellipse(1140, 560, 270, 270);
            ellipse(854, 399, 160, 160);
            ellipse(795, 685, 210, 210);

            popMatrix();
            popStyle();
        }

        //draws the trophy used to represent the highscores button in the main menu
        public void trophy() {
            scale(.175f);
            translate(-960, -540);
            noStroke();
            fill(0);

            //Black outline of trophy
            beginShape();
            vertex(929, 648);
            vertex(991, 648);
            curveVertex(991, 647);
            curveVertex(1000, 622);
            curveVertex(1019, 591);
            curveVertex(1059, 526);
            curveVertex(1090, 459);
            curveVertex(1109, 367);
            curveVertex(1112, 305);
            vertex(1112, 305);
            vertex(809, 305);
            curveVertex(809, 305);
            curveVertex(810, 370);
            curveVertex(813, 384);
            curveVertex(828, 452);
            curveVertex(848, 502);
            curveVertex(872, 546);
            curveVertex(901, 589);
            curveVertex(920, 622);
            curveVertex(929, 647);
            endShape();

            //inner white portion
            fill(backgroundColor);
            beginShape();
            vertex(844, 340);
            vertex(1078, 340);
            curveVertex(1078, 340);
            curveVertex(1074, 379);
            curveVertex(1058, 447);
            curveVertex(1027, 516);
            curveVertex(990, 573);
            curveVertex(969, 613);
            vertex(967, 613);
            vertex(953, 613);
            curveVertex(953, 613);
            curveVertex(937, 583);
            curveVertex(904, 532);
            curveVertex(876, 481);
            curveVertex(858, 429);
            curveVertex(846, 375);
            curveVertex(844, 340);
            curveVertex(844, 340);
            endShape();

            //Inner balck streak
            fill(0);
            beginShape();
            vertex(1005, 368);
            vertex(1042, 369);
            curveVertex(1042, 369);
            curveVertex(1039, 396);
            curveVertex(1028, 441);
            curveVertex(994, 515);
            curveVertex(962, 557);
            curveVertex(962, 557);
            curveVertex(980, 515);
            curveVertex(997, 454);
            curveVertex(1003, 410);
            curveVertex(1005, 368);
            endShape();

            //base
            beginShape();
            vertex(867, 775);
            vertex(1054, 775);
            vertex(1054, 744);
            curveVertex(1054, 744);
            curveVertex(1027, 742);
            curveVertex(1019, 734);
            curveVertex(1010, 727);
            curveVertex(1001, 714);
            curveVertex(994, 698);
            curveVertex(991, 685);
            curveVertex(990, 676);
            vertex(990, 676);
            vertex(928, 676);
            curveVertex(928, 676);
            curveVertex(927, 689);
            curveVertex(921, 708);
            curveVertex(914, 720);
            curveVertex(900, 734);
            curveVertex(884, 741);
            curveVertex(876, 743);
            curveVertex(866, 743);
            vertex(867, 743);
            endShape();

            for (int i = -1; i < 2; i++) {
                if (i == 0) {
                    continue;
                }
                pushMatrix();
                if (i == -1) {
                    translate(1920, 0);
                }
                // DEBUGGING println(i);
                //bottom leaf
                beginShape();
                vertex(i * 902, 665);
                vertex(i * 896, 656);
                curveVertex(i * 888, 657);
                curveVertex(i * 884, 657);
                curveVertex(i * 880, 655);
                curveVertex(i * 872, 642);
                curveVertex(i * 862, 615);
                curveVertex(i * 844, 594);
                curveVertex(i * 817, 579);
                curveVertex(i * 817, 597);
                curveVertex(i * 827, 619);
                curveVertex(i * 842, 639);
                curveVertex(i * 842, 639);
                vertex(i * 842, 639);
                curveVertex(i * 842, 639);
                curveVertex(i * 842, 639);
                curveVertex(i * 813, 629);
                curveVertex(i * 779, 636);
                curveVertex(i * 797, 653);
                curveVertex(i * 820, 664);
                curveVertex(i * 843, 671);
                curveVertex(i * 877, 670);
                curveVertex(i * 902, 665);
                endShape();

                //second bottom leaf
                beginShape();
                vertex(i * 807, 621);
                vertex(i * 807, 613);
                curveVertex(i * 807, 613);
                curveVertex(i * 799, 608);
                curveVertex(i * 798, 589);
                curveVertex(i * 803, 567);
                curveVertex(i * 793, 539);
                curveVertex(i * 780, 522);
                curveVertex(i * 773, 539);
                curveVertex(i * 770, 560);
                curveVertex(i * 777, 582);
                curveVertex(i * 777, 582);
                vertex(i * 777, 582);
                curveVertex(i * 777, 582);
                curveVertex(i * 777, 582);
                curveVertex(i * 762, 566);
                curveVertex(i * 742, 554);
                curveVertex(i * 726, 551);
                curveVertex(i * 730, 566);
                curveVertex(i * 746, 587);
                curveVertex(i * 762, 601);
                curveVertex(i * 780, 611);
                curveVertex(i * 807, 620);
                endShape();

                //middle leaf
                beginShape();
                vertex(i * 751, 548);
                vertex(i * 759, 545);
                curveVertex(i * 754, 539);
                curveVertex(i * 751, 533);
                curveVertex(i * 755, 524);
                curveVertex(i * 763, 508);
                curveVertex(i * 767, 491);
                curveVertex(i * 766, 480);
                curveVertex(i * 762, 465);
                curveVertex(i * 759, 458);
                curveVertex(i * 749, 468);
                curveVertex(i * 743, 479);
                curveVertex(i * 740, 488);
                curveVertex(i * 739, 507);
                curveVertex(i * 734, 494);
                curveVertex(i * 723, 481);
                curveVertex(i * 712, 472);
                curveVertex(i * 705, 468);
                curveVertex(i * 704, 484);
                curveVertex(i * 711, 506);
                curveVertex(i * 738, 539);
                curveVertex(i * 751, 548);
                endShape();

                //second top leaf
                beginShape();
                vertex(i * 734, 467);
                vertex(i * 726, 467);
                curveVertex(i * 726, 467);
                curveVertex(i * 725, 463);
                curveVertex(i * 720, 451);
                curveVertex(i * 711, 438);
                curveVertex(i * 705, 417);
                curveVertex(i * 703, 411);
                curveVertex(i * 705, 399);
                curveVertex(i * 708, 387);
                curveVertex(i * 718, 397);
                curveVertex(i * 725, 408);
                curveVertex(i * 729, 432);
                curveVertex(i * 729, 432);
                vertex(i * 729, 432);
                curveVertex(i * 729, 432);
                curveVertex(i * 729, 432);
                curveVertex(i * 736, 411);
                curveVertex(i * 748, 398);
                curveVertex(i * 758, 392);
                curveVertex(i * 759, 407);
                curveVertex(i * 754, 428);
                curveVertex(i * 742, 443);
                curveVertex(i * 733, 453);
                curveVertex(i * 732, 460);
                curveVertex(i * 734, 466);
                endShape();

                //top leaf
                beginShape();
                vertex(i * 730, 395);
                curveVertex(i * 730, 395);
                curveVertex(i * 727, 380);
                curveVertex(i * 729, 358);
                curveVertex(i * 738, 339);
                curveVertex(i * 750, 330);
                curveVertex(i * 752, 347);
                curveVertex(i * 749, 366);
                curveVertex(i * 745, 377);
                curveVertex(i * 736, 390);
                endShape();
                popMatrix();
            }
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
            rect(width / 2, height / 2, width, height);
            firstPauseFrame = false;
            fill(backgroundColor);
            stroke(0);
            rect(width / 2, height / 2 - 50, 400, 200);
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
            text("PAUSED", width / 2, height / 2 - 100);
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
            text("QUIT?", width / 2, height / 2 - 100);
            for (TextButton b : confirmationButtons) {
                b.display();
            }
            popMatrix();
            popStyle();
        }
//This tab contains all of the different modes draw loops

        //used to display the main menu and the game shown in the background
        public void mainMenu() {
            background(backgroundColor);
            //Main Gameplay
            if (misses == 3 && !drawn) {
                misses = 0;
                score = 0;
                position = 0;
                alive = true;
                safe = true;
                tools = new boolean[5][6];
                frameCount = 0;
            } else if (misses <= 3) {
                startup = true;
                toolUpdate();
                constrainPos();
                timerCheck();
                difficulty();
                background(backgroundColor);

                // FOR DEBUGGING
    /*pushMatrix();
     textSize(20);
     textAlign(LEFT, BOTTOM);
     text(frameCount+", " + int(frameRate) + ", " + pastFrameCount +", " + difficulty +", " + alive, 25, 25);
     popMatrix();*/

                score();
                misses();
                toolsDraw();
                ground();
                gameB();
                trees();
                rightBuilding();
                rightDoor();
                movement();
                leftBackdrop();
                fill(backgroundColor, 175);
                noStroke();
                rect(0, 0, width, height);
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
                    background(backgroundColor);

                    //FOR DEBUGGING
      /*pushMatrix();
       textSize(20);
       textAlign(LEFT, BOTTOM);
       text(frameCount+", " + int(frameRate) + ", " + pastFrameCount +", " + difficulty +", " + alive, 25, 25);
       popMatrix();*/

                    score();
                    misses();
                    toolsDraw();
                    ground();
                    gameB();
                    trees();
                    rightBuilding();
                    rightDoor();
                    movement();
                    leftBackdrop();
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
                if ((misses * numberOfCircles) + score < ((height / (diameter + circleGap)) * numberOfCircles) && startup) {
                    toolUpdate();
                    constrainPos();
                    timerCheck();
                    difficulty();
                    background(backgroundColor);

                    //FOR DEBUGGING
      /*pushMatrix();
       textSize(20);
       textAlign(LEFT, BOTTOM);
       text(frameCount+", " + int(frameRate) + ", " + pastFrameCount +", " + difficulty +", " + alive, 25, 25);
       popMatrix();*/

                    toolsDraw();
                    ground();
                    gameB();
                    trees();
                    rightBuilding();
                    rightDoor();
                    movement();
                    leftBackdrop();

                    //THE ONE FUNCTION THAT RULES THEM ALL
                    oneFunctionToRuleThemAll(new PVector(circleGap / 2 + diameter / 2, circleGap / 2 + diameter / 2), (circleGap + diameter), (circleGap + diameter), missColor, scoreColor, misses, score);

                    for (PictographicButton b : inGameButtons) {
                        b.display();
                    }
                    //Game Over screen
                } else if ((misses * numberOfCircles) + score >= ((height / (diameter + circleGap)) * numberOfCircles) && startup) {
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
            background(backgroundColor);
            for (TextButton b : settingsButts) {
                b.reset();
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
                rect(width / 2, height / 2 - 50, 450, 200);
                textFont(arialbd);
                textAlign(CENTER, CENTER);
                textSize(64);
                fill(0);
                text("Are you sure?", width / 2, height / 2 - 125);
                textSize(32);
                text("All high scores will be lost.", width / 2, height / 2 - 75);
                for (TextButton b : confirmationButtons) {
                    b.display();
                }
                popMatrix();
                popStyle();
            } else {
                pushStyle();
                pushMatrix();
                fill(0);
                background(backgroundColor);
                textFont(arialbd);
                textSize(60);
                textAlign(CENTER, CENTER);
                text("Leaderboard", width / 2, 28);
                strokeWeight(3);
                for (int i = 1; i <= 3; i++) {
                    line(width / 4 * i, 64, width / 4 * i, 264);
                }
                for (int i = 0; i < 6; i++) {
                    line(width / 4, 64 + 40 * i, 3 * width / 4, 64 + 40 * i);
                }
                for (int i = 0; i < highScores.getRowCount(); i++) {
                    println(i);
                    textFont(arial);

                    textAlign(LEFT, BOTTOM);
                    textSize(30);
                    text(highScores.getString(i, 0), width / 4 + 4, 104 + 40 * i);
                    text(highScores.getString(i, 1), width / 4 * 2 + 4, 104 + 40 * i);
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
            if (row >= 0 && row < highScores.getRowCount() - 1) {
                highScore = true;
                pushStyle();
                pushStyle();
                background(backgroundColor);
                textFont(arialbd);
                textSize(48);
                textAlign(CENTER, TOP);
                fill(0);
                text("NEW HIGH SCORE", width / 2, 0);
                textSize(24);
                text("Enter Name Below:", width / 2, 60);
                text("Press enter to finalise", width / 2, height / 2 + 20);
                line(75, height / 2, 437, height / 2);
                textAlign(CENTER, BOTTOM);
                textSize(50);
                line(width / 2 + textWidth(name) / 2, height / 2 - 5, width / 2 + textWidth(name) / 2, height / 2 - 50);
                text(name, width / 2, height / 2);
                popStyle();
                popStyle();
            }
        }

        //this is the function that determines where and how MR.GW should be drawn
        public void movement() {

            //the following block of code is only executed while in the main menu
            //it is what determies the movement of MR.GW automatically that gives the illusion of the game being played
            int deltaPos = 0;
            if (frameCount % 15 == 0 && z == 0) {           //this only occurs every half second
                //the following stack of if statements determines the probability of MR.GW being moved left, right, or staying the same place
                if (position == 0 || position == 2) {
                    deltaPos = PApplet.parseInt(random(0, 2));
                } else if (open && position == 6) {
                    deltaPos = PApplet.parseInt(random(1, 2));
                } else if (open && (position != 0 || position != 2)) {
                    deltaPos = PApplet.parseInt(random(-1, 2));
                } else if (!open && position == 6) {
                    deltaPos = PApplet.parseInt(random(-2, 0));
                } else if (!open && (position != 6 || position != 0 || position != 2)) {
                    deltaPos = PApplet.parseInt(random(-2, 2));
                }

                //based off of the previous randoms, the following actually turn that into the movenement that is seen
                switch (deltaPos) {
                    case -2:
                    case -1:
                        i = -1;
                        rotation = random(-PI / 8, PI / 8);
                        position--;
                        break;
                    case 0:
                        break;
                    case 1:
                    case 2:
                        i = 1;
                        rotation = random(-PI / 8, PI / 8);
                        position++;
                        break;
                }
                constrainPos();
            }

//this ensures that MR.GW has been drawn, dead, on the screen before pausing all of functions
            if (drawn && startup) {
                if (millis() - lastTime >= 1500) {
                    resetGW();
                    frameCount = deathFrame;
                    drawn = false;
                } else {
                    println(millis() - lastTime);
                    position = deadPosition;
                    rotation = PI / 2;
                    i = -1;
                    pushMatrix();
                    translate(position * 64 - 30, 307);
                    rotate(rotation);
                    MrGW();
                    popMatrix();
                }
            }

            //draws a dead GW in the Center for the startup menu
            if (!alive && !drawn && !startup) {
                rotation = PI / 2;
                i = -1;
                pushMatrix();
                translate(position * 64 - 30, 307);
                rotate(rotation);
                MrGW();
                deadPosition = position;
                popMatrix();
            }

            //draws a dead mr.gw for normal gameplay when he dies
            if (!alive && !drawn && startup) {
                lastTime = millis();
                rotation = PI / 2;
                i = -1;
                pushMatrix();
                translate(position * 64 - 30, 307);
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
                } else if (position < 7) {
                    translate(position * 64 - 30, 250);
                    rotate(rotation);
                    MrGW();
                } else {
                    translate(position * 64 - 10, 250);
                    rotate(rotation);
                    MrGW();
                    if (startup && z != 0) {
                        //doorB.play();
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

        //All of the drawinng related to Mr.GW
//Within this there is an inversion variable (i) that is multiplied by all non-zero x values in both translation and drawing, as well as all roations
//this allows me to flawlessly flip Mr.GW to appear as though he is moving in the direction of the arrow key that is pressed.
        public void MrGW() {
            //Mr.GW's head, was two ellipses, but in order ti show the grass through his dead mouth it was reformed to be a large number of curvevertices,
            //the scaleing is there because I used a seperate program, I wrote, to click around an enlarged version of the head to output the nessecary lines of code.
            pushMatrix();
            scale(.075f);
            pushMatrix();
            noStroke();
            fill(0);
            beginShape();
            translate(-680 * i, -380);
            vertex(850 * i, 438);
            curveVertex(838 * i, 426);
            curveVertex(830 * i, 421);
            curveVertex(820 * i, 415);
            curveVertex(814 * i, 411);
            curveVertex(802 * i, 407);
            curveVertex(792 * i, 403);
            curveVertex(783 * i, 402);
            curveVertex(774 * i, 402);
            curveVertex(765 * i, 403);
            curveVertex(754 * i, 404);
            curveVertex(746 * i, 407);
            curveVertex(738 * i, 409);
            curveVertex(722 * i, 418);
            curveVertex(715 * i, 425);
            curveVertex(709 * i, 433);
            curveVertex(704 * i, 447);
            curveVertex(704 * i, 458);
            curveVertex(709 * i, 475);
            curveVertex(712 * i, 481);
            curveVertex(719 * i, 491);
            curveVertex(727 * i, 500);
            curveVertex(735 * i, 507);
            curveVertex(743 * i, 512);
            vertex(760 * i, 518);
            curveVertex(754 * i, 516);
            curveVertex(740 * i, 526);
            curveVertex(716 * i, 532);
            curveVertex(702 * i, 534);
            curveVertex(663 * i, 533);
            curveVertex(636 * i, 528);
            curveVertex(608 * i, 520);
            curveVertex(586 * i, 510);
            curveVertex(573 * i, 503);
            curveVertex(556 * i, 490);
            curveVertex(548 * i, 483);
            curveVertex(537 * i, 471);
            curveVertex(524 * i, 455);
            curveVertex(517 * i, 442);
            curveVertex(510 * i, 425);
            curveVertex(504 * i, 405);
            curveVertex(502 * i, 383);
            curveVertex(505 * i, 353);
            curveVertex(514 * i, 329);
            curveVertex(524 * i, 311);
            curveVertex(539 * i, 292);
            curveVertex(557 * i, 275);
            curveVertex(583 * i, 258);
            curveVertex(608 * i, 246);
            curveVertex(634 * i, 239);
            curveVertex(648 * i, 236);
            curveVertex(703 * i, 234);
            curveVertex(738 * i, 240);
            curveVertex(774 * i, 254);
            curveVertex(786 * i, 260);
            curveVertex(813 * i, 280);
            curveVertex(825 * i, 291);
            curveVertex(835 * i, 304);
            curveVertex(850 * i, 328);
            curveVertex(856 * i, 343);
            curveVertex(860 * i, 358);
            curveVertex(863 * i, 379);
            curveVertex(863 * i, 393);
            curveVertex(861 * i, 407);
            curveVertex(858 * i, 418);
            curveVertex(854 * i, 429);
            vertex(850 * i, 438);
            endShape();
            popMatrix();
            scale(4);

            //nose
            pushMatrix();
            translate(50 * i, 0);
            rotate(-PI / 6 * i);
            ellipse(0, 0, 30, 20);
            popMatrix();

            //Body
            pushMatrix();
            rotate(PI / 12 * i);
            translate(5 * i, 35);
            bezier(-1 * i, -1, 10 * i, 5, 40 * i, 58, -1 * i, 60);
            bezier(1 * i, 1, -10 * i, 5, -40 * i, 58, 1 * i, 60);
            popMatrix();

            //Hand near mouth
            pushMatrix();
            translate(55 * i, 35);
            ellipse(0, 0, 20, 20);
            popMatrix();

            //Other Hand
            pushMatrix();
            translate(-65 * i, 25);
            ellipse(0, 0, 20, 20);
            popMatrix();

            //Lower Foot
            pushMatrix();
            translate(-3 * i, 130);
            rotate(PI / 4 * i);
            ellipse(0, 0, 30, 20);
            popMatrix();

            //Higher Foot
            pushMatrix();
            translate(-85 * i, 65);
            rotate(PI / 3 * i);
            ellipse(0, 0, 30, 20);
            popMatrix();

            //Mouth hand Arm
            noFill();
            strokeWeight(5);
            stroke(0);
            pushMatrix();
            translate(-5 * i, 50);
            bezier(0, 0, 35 * i, -5, 45 * i, -6, 60 * i, -15);
            popMatrix();

            //Other Arm
            pushMatrix();
            translate(-5 * i, 50);
            bezier(0, 0, -30 * i, -5, -35 * i, -7, -60 * i, -25);
            popMatrix();

            //Lower Leg
            pushMatrix();
            translate(-5 * i, 85);
            bezier(0, 0, 20 * i, 10, 23 * i, 15, -10 * i, 40);
            popMatrix();

            //Higher leg
            pushMatrix();
            translate(-35 * i, 75);
            bezier(0, 0, -20 * i, -5, -21 * i, -4, -40 * i, 0);
            popMatrix();

            //How his helmet is shown if he is alive
            if (alive) {
                pushMatrix();
                translate(0, -10);
                rotate(-PI / 10 * i);
                noStroke();
                fill(0);
                arc(0, 0, 96, 76, PI, 2 * PI);
                stroke(0);
                strokeWeight(10);
                line(-48 * i, 0, i * 60, 0);
                popMatrix();
            }

            //How his helmet and eye are shown when he dies
            if (!alive) {
                pushMatrix();
                strokeWeight(6);
                stroke(backgroundColor);
                translate(4 * i, -12);
                line(-7, -7, 7, 7);
                line(-7, 7, 7, -7);
                popMatrix();
                pushMatrix();
                translate(i * -30, -50);
                rotate(-PI / 5 * i);
                noStroke();
                fill(0);
                arc(0, 0, 96, 76, PI, 2 * PI);
                stroke(0);
                strokeWeight(10);
                line(-48 * i, 0, i * 60, 0);
                popMatrix();
            }
            popMatrix();
        }
//Here is my one function to rule them all

        public void oneFunctionToRuleThemAll(PVector startCOOR, int deltaX, int deltaY, int missC, int scoreC, int i, int s) {
            int loopIterations, row, col;
            int c;
            loopIterations = i * numberOfCircles + s;
            row = col = 0;
            for (int j = 0; j < loopIterations; j++) {
                // this if statement ensures that no cirlce are draw beneath
                // the screen once the maximun number of cirlces has been reached.
                if (j >= ((height / (diameter + circleGap)) * numberOfCircles)) {
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
                gradientCircle(new PVector(col * deltaX + startCOOR.x, row * deltaY + startCOOR.y), diameter, c);
            }
        }

        //the class that I built to create pictogrpahic buttons with overlays
        abstract class PictographicButton {

            PVector centerCoor, upperLeftCoor, lowerRightCoor;
            int buttonWidth, buttonHeight, textSize;
            String overlayText;
            PVector[] buttonCoors;
            int backgroundColour;
            PFont font;

            PVector centerCoorDefault, upperLeftCoorDefault, lowerRightCoorDefault;
            int buttonWidthDefault, buttonHeightDefault, textSizeDefault;
            String overlayTextDefault;
            PVector[] buttonCoorsDefault;
            int backgroundColourDefault;

            PictographicButton(PVector coor, int w, int h, int c) {
                centerCoorDefault = centerCoor = coor;
                upperLeftCoorDefault = upperLeftCoor = new PVector(coor.x - w / 2, coor.y - h / 2);
                lowerRightCoorDefault = lowerRightCoor = new PVector(coor.x + w / 2, coor.y + h / 2);
                buttonWidthDefault = buttonWidth = w;
                buttonHeightDefault = buttonHeight = h;
                buttonCoorsDefault = buttonCoors = new PVector[]{upperLeftCoor, lowerRightCoor};
                backgroundColourDefault = backgroundColour = c;
                font = createFont("arial", 36);
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

            public int backgroundColour() {
                return backgroundColour;
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

            public void backgroundColour(int c) {
                backgroundColour = c;
            }

            public boolean mouseOver() {
                if (mouseX > upperLeftCoor.x && mouseX < lowerRightCoor.x && mouseY > upperLeftCoor.y && mouseY < lowerRightCoor.y) {
                    return true;
                }
                return false;
            }

            public void reset() {
                centerCoor = centerCoorDefault;
                upperLeftCoor = upperLeftCoorDefault;
                lowerRightCoor = lowerRightCoorDefault;
                buttonWidth = buttonWidthDefault;
                buttonHeight = buttonHeightDefault;
                buttonCoors = buttonCoorsDefault;
                backgroundColour = backgroundColourDefault;
            }

            public void display() {
                strokeWeight(3);
                stroke(0);
                fill(backgroundColour);
                rectMode(CENTER);
                rect(centerCoor.x, centerCoor.y, buttonWidth, buttonHeight);
                rectMode(CORNER);
                pictograph();
            }

            //the methods that change with each button
            public abstract void pictograph();

            public abstract void overlay();

            public abstract void action();
        }

        //shows the start up boot
        public void startup() {
            if (frameCount <= 45) {
                misses = 3;
                rotation = startRotation;
                background(backgroundColor);
                ground();
                trees();
                score();
                misses();
                rightBuilding();
                gameB();
                for (int s = 0; s < 8; s++) {
                    alive = true;
                    if (s == 1) {
                        continue;
                    }
                    i *= -1;
                    position = s;
                    movement();
                }
                alive = false;
                position = 4;
                movement();
                rightDoor();
                leftBackdrop();
                for (int x = 0; x < tools.length; x++) {
                    for (int y = 0; y < tools[0].length; y++) {
                        tools[x][y] = true;
                    }
                }
                toolsDraw();
                bellMan();
                if (z == 0) {
                    frameCount = 0;
                }
            } else {
                score = 0;
                position = 0;
                pastFrameCount = 0;
                frameCount = 0;
                i = 1;
                misses = 0;
                groundSaturation = 500;
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
            PVector[] buttonCoors;
            int colour;
            PFont font;

            PVector centerCoorDefault, upperLeftCoorDefault, lowerRightCoorDefault;
            int buttonWidthDefault, buttonHeightDefault, textSizeDefault;
            String buttonTextDefault;
            PVector[] buttonCoorsDefault;
            int colourDefault;

            // first constructor based of defining the button size
            TextButton(PVector coor, int w, int h, String text, int tS, int c) {
                centerCoorDefault = centerCoor = coor;
                upperLeftCoorDefault = upperLeftCoor = new PVector(coor.x - w / 2, coor.y - h / 2);
                lowerRightCoorDefault = lowerRightCoor = new PVector(coor.x + w / 2, coor.y + h / 2);
                buttonWidthDefault = buttonWidth = w;
                buttonHeightDefault = buttonHeight = h;
                buttonTextDefault = buttonText = text;
                textSizeDefault = textSize = tS;
                buttonCoorsDefault = buttonCoors = new PVector[]{upperLeftCoor, lowerRightCoor};
                colourDefault = colour = c;
                font = createFont("arial", 36);
            }

            //second constructor based around defining the size of the butotn off the test size
            TextButton(PVector coor, String text, int tS, int c) {
                buttonTextDefault = buttonText = text;
                textSizeDefault = textSize = tS;
                textSize(tS);
                buttonWidthDefault = buttonWidth = PApplet.parseInt(textWidth(buttonText) + 40);
                buttonHeightDefault = buttonHeight = PApplet.parseInt(textAscent() + textDescent() + 4);
                centerCoorDefault = centerCoor = coor;
                upperLeftCoorDefault = upperLeftCoor = new PVector(coor.x - buttonWidth / 2, coor.y - buttonHeight / 2);
                lowerRightCoorDefault = lowerRightCoor = new PVector(coor.x + buttonWidth / 2, coor.y + buttonHeight / 2);
                buttonCoorsDefault = buttonCoors = new PVector[]{upperLeftCoor, lowerRightCoor};
                colourDefault = colour = c;
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

            public int colour() {
                return colour;
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

            public void colour(int c) {
                colour = c;
            }

            public boolean mouseOver() {
                if (mouseX > upperLeftCoor.x && mouseX < lowerRightCoor.x && mouseY > upperLeftCoor.y && mouseY < lowerRightCoor.y) {
                    return true;
                }
                return false;
            }

            public void reset() {
                centerCoor = centerCoorDefault;
                upperLeftCoor = upperLeftCoorDefault;
                lowerRightCoor = lowerRightCoorDefault;
                buttonWidth = buttonWidthDefault;
                buttonHeight = buttonHeightDefault;
                buttonText = buttonTextDefault;
                textSize = textSizeDefault;
                buttonCoors = buttonCoorsDefault;
                colour = colourDefault;
            }

            public void display() {
                strokeWeight(3);
                stroke(0);
                fill(colour);
                rectMode(CENTER);
                rect(centerCoor.x, centerCoor.y, buttonWidth, buttonHeight);
                rectMode(CORNER);
                fill(0);
                textFont(font);
                textAlign(CENTER, CENTER);
                textSize(textSize);
                text(buttonText, upperLeftCoor.x + buttonWidth / 2, upperLeftCoor.y + 2 * buttonHeight / 5);
            }

            public abstract void action();
        }

        //Shows text that has no relevance to gameplay but is a reference to the original game
        public void gameB() {
            textFont(arialbd);
            textSize(18);
            textAlign(CENTER, BOTTOM);
            fill(0);
            text("GAME B", 400, 330);
            if (!startup) {
                text("GAME A", 75, 330);
                text("AM", 42, 24);
            }
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
            for (int i = 0; i < misses; i++) {
                deadHead(i);
            }
        }

        //determines when and if a tool will enter the screen
        public void toolUpdate() {
            if (alive) {
                for (int j = 0; j < toolStart.length; j++) {
                    if ((frameCount - toolStart[j]) % (45 - difficulty * 2) == 0) {

                        //tests if the player is hit
                        if (tools[j][4] && position == j + 2) {

                            //only for the main menu "play"
                            if (z == 0) {
                                int q = PApplet.parseInt(random(1, 6));
                                if (q % 3 != 0) {
                                    rotation = random(-PI / 8, PI / 8);
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
                                    deathFrame = frameCount;
                                    if (startup && z != 0) {
                                        //deathB.play();
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
                                deathFrame = frameCount;
                                if (startup && z != 0) {
                                    //deathB.play();
                                }
                            }
                        }

                        //moves the tools down
                        for (int y = tools[0].length - 1; y >= 1; y--) {
                            tools[j][y] = tools[j][y - 1];
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
                        int q = PApplet.parseInt(random(0, 10 - spawnDifficulty));
                        if (q <= 1) {
                            tools[j][0] = true;
                        } else {
                            tools[j][0] = false;
                        }

                        //this loop checks if a tools is in a column and if so, to play the sound related to the tool
                        for (int y = 0; y < tools[j].length - 1; y++) {
                            if (tools[j][y] && startup && z != 0) {
                                switch (j) {
                                    case 0:
                                     //   hammerB.play();
                                        break;
                                    case 1:
                                     //   bucketB.play();
                                        break;
                                    case 2:
                                     //   plierB.play();
                                        break;
                                    case 3:
                                        //screwdriverB.play();
                                        break;
                                    case 4:
                                        //spannerB.play();
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
            for (int x = 0; x < tools.length; x++) {
                for (int y = 0; y < tools[0].length - 1; y++) {
                    if (y != 5) {
                        if (tools[x][y]) {
                            pushMatrix();
                            translate((x + 2) * 64 - 30, (y) * 35 + 80);
                            if (x <= 2) {
                                rotate(-(y + 1) * PI / 8);
                            } else {
                                rotate((y + 1) * PI / 8);
                            }
                            switch (x) {
                                case 0:
                                    hammer();
                                    break;
                                case 1:
                                    bucket();
                                    break;
                                case 2:
                                    pliers();
                                    break;
                                case 3:
                                    screwdriver();
                                    break;
                                case 4:
                                    spanner();
                                    break;
                            }
                            popMatrix();
                        }
                    }
                }
            }
        }

        static public void main(String[] passedArgs) {
            String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "studentwork.GameAndWatch" };
            runSketch(appletArgs, new GameAndWatch() );
        }
    }


