package studentwork;

//'public' needs to be added at the beginning of all functions and

public class MrGW extends mqapp.MQApp{

    public String name(){return "Mr GW - Cameron Aume";}
    public String description(){return "Cameron Aume's Game";}

    final int moveSpeed = 64;
    final int unitSize = 64;
    final int minDoorTime = 3000;
    final int maxDoorTime = 8000;
    final int minToolTime = 1000;
    final int maxToolTime = 1500;
    final int throughDoorScore = 3;
    final int deathScore = -6;
    final int toolDropScore = 1;
    final int[] toolPoses = {unitSize * 1, unitSize * 2, unitSize * 3, unitSize * 4, unitSize * 5, unitSize * 6};

   // PImage gwImage;
    int deltaX = 0;
    int doorTime, toolTime;
    boolean doorOpen = true;
    boolean prevDoorOpen = false;
    boolean isTool = false;
    boolean prevKeyPressed = false;
    boolean isNumericalScore = false;
    int score = 0;
    int deaths = 0;
    int toolPosI = 0;
    int toolID = 0;

    public void setup() {
        size(512, 348);
        background(150, 150, 255);
        //gwImage = loadImage("MRGW.png"); // Image taken from http://cdn.atomix.vg/wp-content/uploads/2014/10/super-smash-bros-mr-game-and-watch.png
        randomizeDoorTime();
        resetTool();
    }

    public void draw() {
        background(150, 150, 255); // Reset screen
        doDoor(); // Draw the doors and time them appropriately
        doTool(); // Draw and manipulate the tool
       // image(gwImage, deltaX, height - unitSize, unitSize, unitSize); // draw GW
        displayScore(); // Displays the score either with coloured circles or numbers
        autoScoreReset(); // Automatically reset the score when it's too high
    }

    public void keyPressed() {
        if (!prevKeyPressed && keyCode == 37 && deltaX > 64) {
            deltaX -= moveSpeed;
        }
        if (!prevKeyPressed && keyCode == 39 && ((doorOpen && deltaX + unitSize < width) || (!doorOpen && deltaX + 2*unitSize < width))) {
            deltaX += moveSpeed;
        }

        if (keyCode == 67) {
            isNumericalScore = false;
        }
        if (keyCode == 78) {
            isNumericalScore = true;
        }
        prevKeyPressed = true;
    }

    public void keyReleased() {
        prevKeyPressed = false;
    }

    int toolX() {
        return toolPoses[toolPosI];
    }
    int toolY() {
        return height - (1 - (int)(millis() - toolTime)/1000)*unitSize;
    }

    void randomizeDoorTime() {
        doorTime = (int)random(millis() + minDoorTime, millis() + maxDoorTime);
    }

    void randomizeToolX() {
        toolPosI = (int)random(0, toolPoses.length);
    }

    void randomizeToolTime() {
        toolTime = (int)random(millis() + minToolTime, millis() + maxToolTime);
    }

    void randomizeToolID() {
        toolID = 1-(int)random(0, 2);
    }

    void resetTool() {
        randomizeToolX();
        randomizeToolID();
        randomizeToolTime();
    }

    void doDoor() {
        if (doorTime <= millis()) { // if door should change states
            randomizeDoorTime();
            doorOpen = !doorOpen;
        }
        if (!doorOpen) { // if door is closed
            drawDoor(width - unitSize, height - unitSize);
        } else { // if door is open
            stroke(0);
            noFill();
            rect(width - unitSize, height - unitSize, unitSize-1, unitSize-1);
        }

        if (deltaX >= width - unitSize) { // Make it through the door
            deltaX = 0;
            score(throughDoorScore);
        }

        stroke(0);
        line(0, height-unitSize, unitSize, height-unitSize); // Draw roofs
        //line(0, height-unitSize * 1.5, unitSize, height-unitSize);
        //line(width-unitSize, height-unitSize, width, height-unitSize*1.5);

        if (deltaX > 0) { // If player is no longer in the left doorway
            drawDoor(0, height - unitSize); // draw left door
        }
        prevDoorOpen = doorOpen;
    }

    void doTool() {
        if (toolTime <= millis() && isTool) { // if tool needs to reset because it has 'expired' (at the bottom of the screen)
            score(toolDropScore);
            isTool = !isTool;
            resetTool();
        } else if (toolTime <= millis() && !isTool) { // if tool exists and is above ground
            toolTime = millis() + 5000;
            isTool = !isTool;
        }
        if (isTool) { // if tool exists
            drawTools(toolX(), toolY());
        }
        if (deltaX == toolX() && isTool && toolY() == 284) { // if tool hits player
            deltaX = 0;
            isTool = false;
            score(deathScore);
            resetTool();
        }
    }

    void score(int deltaScore) {
        if (deltaScore == deathScore) { // if score is a death
            deaths++;
        } else {
            score += deltaScore;
        }
    }

    void autoScoreReset() {
        if ((score + deaths *6) >= 24) { // If the score and (deaths * 6) add up to 24, reset score and deaths
            score = 0;
            deaths = 0;
        }
    }

    void displayScore() {
        if (isNumericalScore) {
            textSize(30);
            fill(0);
            text("Score: " + score + "    Deaths: " + deaths, 25, 25);
            textSize(15);
            text("Press \"C\" to display circle score", 70, height-10);
        } else {
            textSize(15);
            fill(0);
            text("Press \"N\" to display numerical score", 70, height-10);

            int redCircles = deaths*6;
            noStroke();
            for (int i = 0; i < (score + deaths * 6); i++) {
                int y = i/6;
                int x = i%6;
                for (int fill = 0; fill <= 15; fill++) {
                    int alpha = fill*fill;
                    if (redCircles > 0) {
                        fill(255, 0, 0, alpha);
                    } else {
                        fill(0, 0, 0, alpha);
                    }
                    ellipse(85/2 + (85*x), 85/2 + (85*y), 85 - fill*3, 85 - fill*3);
                }
                redCircles--;
            }
        }
    }

    void drawDoor(int x, int y) {
        stroke(0);
        strokeWeight(1);
        fill(140, 70, 40);
        rect(x, y, unitSize/4, unitSize - 1);
        fill(180, 90, 50);
        rect(x + unitSize/4, y, unitSize/4, unitSize - 1);
        fill(140, 70, 40);
        rect(x + unitSize/2, y, unitSize/4, unitSize - 1);
        fill(180, 90, 50);
        rect(x + 3*unitSize/4, y, unitSize/4 - 1, unitSize - 1);
        rect(x + 5, y + unitSize/2 - 8, 6, 16);
        fill(200);
        ellipse(x + 8, y + unitSize/2, 8, 8);
        fill(200);
        rect(x + unitSize/4 - 3, y + unitSize/8 - 3, unitSize/2 + 6, unitSize/4 + 6);
        fill(255);
        rect(x + unitSize/4, y + unitSize/8, unitSize/4, unitSize/8);
        rect(x + unitSize/2, y + unitSize/8, unitSize/4, unitSize/8);
        rect(x + unitSize/4, y + unitSize/4, unitSize/4, unitSize/8);
        rect(x + unitSize/2, y + unitSize/4, unitSize/4, unitSize/8);
    }

    void drawTools(int x, int y) {
        if (toolID == 0) drawBucket(x, y);
        else drawHammer(x, y);
    }

    void drawBucket(int x, int y) {
        fill(150);
        arc(x + 32, y + 50, 40, 10, 0, PI);
        noStroke();
        triangle(x + 32 - 20, y + 50, x + 6, y + 20, x + 32 - 20, y + 20);
        triangle(x + 32 + 20, y + 50, x + 32 + 26, y + 20, x + 32 + 20, y + 20);
        rect(x + 32 - 20, y + 20, 40, 30);
        stroke(0);
        line(x + 32 - 20, y + 50, x + 6, y + 20);
        line(x + 32 + 20, y + 50, x + 32 + 26, y + 20);
        ellipse(x + 32, y + 20, 52, 10);
        noFill();
        arc(x + 32, y + 20, 52, 40, PI, 2*PI);
        arc(x + 32, y + 35, 46, 10, 0, PI);
    }

    void drawHammer(int x, int y) {
        fill(185, 100, 50);
        beginShape();
        vertex(x + 5, y + 10);
        vertex(x + 10, y + 5);
        vertex(x + 50, y + 45);
        vertex(x + 45, y + 50);
        endShape();
        line(x + 45, y + 50, x + 5, y + 10);
        fill(160);
        beginShape();
        vertex(x + 56, y + 36);
        vertex(x + 42, y + 50);
        vertex(x + 45, y + 53);
        vertex(x + 59, y + 39);
        endShape();
        beginShape();
        vertex(x + 60, y + 40);
        vertex(x + 55, y + 35);
        vertex(x + 52, y + 38);
        vertex(x + 57, y + 43);
        endShape();
        line(x + 57, y + 43, x + 60, y + 40);
        triangle(x + 40, y + 49, x + 43, y + 52, x + 33, y + 49);
    }
}
