package studentwork;

public class HardHat2 extends mqapp.MQApp {

    // COMP115 Assignment 2 - "Hardhat 2"
    // Author: Brendan McSweeney
    // Date: 27/05/2018
    // Original Game, Concept and Character ©1981 Nintendo

    // Customisable variables: the initial values of these can be safely altered to customise the play experience.
    int toolCount = 5; // Set number of tools
    int playerStepSize = 60; // Set the size of player steps and the space between tools.
    int toolSteps = 6; // The number of steps down a tool can make before it hurts the player/returns to the top.
    int toolStepSize = 50; // How far down a tool falls per step.
    int lives = 3; // Number of lives for Game A.
    int gameBRowCount = 4; // Number of rows of circles in Game B. Also the maximum number of lives in Game B.
    int gameBColumnCount = 6; // Number of columns of circles in Game B.
    int timePenalty = 6; // The seconds added as penalty for getting hit in Game B. Preferably equal to gameBColumnCount.
    int gameSpeed = 30; // The speed of the game in frames per second.

    // Important variables: actively used and altered by the game, should not be altered.
    boolean gameMode = true; // true = Game A, false = Game B
    int playerState = 0; // 0 to value of toolCount is position (left to right), toolCount+1 is in door, toolCount+2 is hurt
    int[] toolStates = new int[toolCount]; // < 0 is off screen, 1 to 5 is position (top to bottom), 6 is damage
    int[] toolSpeeds = new int[toolCount]; // Speed of falling tools, in frames per step.
    int doorSpeed; // Speed of door open / close
    boolean doorOpen = false;
    int doorFlipFrame; // Keeps track of when the door last opened/closed, in order to determine when it should next open/close.
    int framesPassed; // The amount of frames since the current game started, different from FrameCount.
    int frameNote; // Keeps track of when the player got hurt / entered the door, in order to calculate when to reset the player's position.
    int score = 0;
    int missCount = 0;
    boolean isPaused = true;
    boolean isMenu = true;
    boolean isGameOver = false;
    int gameBMaxCircles = gameBColumnCount*gameBRowCount; // Calculate the total number of circles in Game B that can appear on screen before Game Over.

   public void setup() {
        size(512, 348); // Set size of window
        frameRate(gameSpeed); // Set frame rate
        setupGame();
    }

    void setupGame() {
        isMenu = true; // Set menu to appear
        isPaused = true; // Pause before displaying menu
        isGameOver = false;
        playerState = 0;
        doorOpen = false;
        score = 0;
        missCount = 0;
        for (int toolID = 0; toolID < toolCount; toolID++) {
            toolStates[toolID] = toolReset(true); // Place the tools somewhere above the screen
        }
        setDoorSpeed(); // Set inital door speed
        framesPassed = 0;
        setToolSpeeds();
    }

    void setToolSpeeds() {
        // Set random speed values (at the start of each round (each time entering the door))
        for (int toolID = 0; toolID < toolCount; toolID++) {
            toolSpeeds[toolID] = (int)random(15, 45);
        }
    }

    void setDoorSpeed() {
        doorSpeed = (int)random(90, 240);
    }

    public void draw() {
        if (isPaused == false) gameStep(); // Don't run game's main code if game is paused.
        background(255);
        rectMode(CORNER);
        drawBackground();
        drawPlayer();
        drawTools();
        drawHUD();
    }

    void gameStep() {
        if ((framesPassed > doorFlipFrame + doorSpeed) && playerState != toolCount+1){ // Check if door should flip (time since last flip & player is in door)
            flipDoor();
        }

        for (int toolID = 0; toolID < toolCount; toolID++) { // Loop for each tool
            toolStates[toolID] = toolFall(toolID, toolStates[toolID]);
        }

        if (playerState > toolCount){ // Check if player is in a "frozen" state (hurt or in door)
            playerReset();
        }

        if (gameMode == false) { // Check if Game Over condition for Game B is satisfied
            if ((missCount*gameBColumnCount) + score >= gameBMaxCircles){
                gameOver();
            }
        }

        framesPassed++; // Increment how long the game's code has been running, excluding time paused.
    }

    void flipDoor() {
        doorOpen =! doorOpen; // Open close door
        doorFlipFrame = framesPassed; // Record the time the door flipped
        setDoorSpeed(); // Set new door speed
    }

    int toolFall(int toolID, int toolState) {
        if ((framesPassed % toolSpeeds[toolID]) == 0){
            toolState++; // Fall one step
            if (toolState == toolSteps){ // Check if tool is in position to hurt player
                if (playerState == toolID + 1){ // Check if player is standing under tool
                    // Player hurt
                    frameNote = framesPassed; // Record the time the player got hurt (in frames passed)
                    playerState = toolCount+2; // Hurt animation
                }
                else{
                    if (gameMode == false) {
                        score++; // Add point in Game B for not getting hit.
                    }
                }
                toolState = toolReset(false); // Put tool back at top
            }
        }
        return toolState;
    }

    int toolReset(boolean isFirst) {
        int low;
        if (isFirst == true) { // Check if this is the initial setup or a single tool restarting its cycle
            low = -5; // Set tool higher than usual for initial setup
        }
        else {
            low = -2;
        }
        return (int) random(low, 0);
    }

    void playerReset() {
        if (framesPassed > frameNote + frameRate){ // Check if 1 second has passed since becoming frozen.
            if (playerState == toolCount+1){
                if (gameMode == true) {
                    score++; // Add to score
                }
                else {
                    score+=3;
                }
            }
            else if (playerState == toolCount+2){
                missCount++; // Add to misses
                if ((missCount == lives) && (gameMode == true)){
                    gameOver();
                    return;
                }
            }
            // Reset
            setToolSpeeds(); // Refresh speeds
            playerState = 0; // Put player at start
        }
    }

    void gameOver() {
        isGameOver = true;
        isPaused = true;
    }

   public void keyPressed() {
        if (isPaused == false) {
            if (playerState <= toolCount){ // Check if player is in a "frozen" state (hurt or in door), input will be ignored if so
                if (keyCode == RIGHT){
                    playerState++; // Move right
                    if (playerState == toolCount+1){ // Check if player is trying to enter door
                        if (doorOpen == true){ // Check if door is open
                            frameNote = framesPassed; // Record the time the player entered the door (in frames passed)
                        }
                        else {
                            playerState = toolCount; // Keep player outside
                        }
                    }
                }
                else if (keyCode == LEFT){
                    playerState--; // Move left
                }
                playerState = constrain(playerState, 0, toolCount+1); // Prevent player from going out of bounds
            }
        }
        else {
            if (isMenu == true) { // Check if game menu is open
                if ((keyCode == UP) || (keyCode == DOWN)) {
                    gameMode =! gameMode; // Change game mode
                }
            }
        }
        if (keyCode == ENTER) {
            if (isGameOver == true) {
                setupGame();
                return;
            }
            else {
                isPaused =! isPaused; // Pause or unpause
                if (isMenu == true) isMenu = false; // Start game, prevent menu from appearing when the game is paused next.
            }
        }
    }

    void drawBackground() {
        // Building
        noStroke();
        fill(104, 80, 56);
        rect(0, 24, 9, 201); // Wall
        rect(9, 24, 45, 12); // Top
        for (int n = 0; n <= 1; n++) {
            rect(9, 78 + (90 * n), 42, 6); // Handrail
            rect(9, 114 + (90 * n), 45, 12); // Landing
            for (int n2 = 0; n2 <= 3; n2++) {
                rect(15 + (9 * n2), 84 + (90 * n), 3, 30); // Banister
            }
        }

        // Left Door
        stroke(0);
        strokeWeight(3);
        fill(0);
        rect(7, 259, 25, 19);
        ellipse(37, 292, 4, 4);
        noFill();
        rect(-2, 247, 48, 87);
        // Right Door
        // Frame
        rect(417, 247, 48, 87);
        if (doorOpen == true){ // Open
            line(468, 247, 501, 232);
            line(501, 232, 501, 349);
            line(468, 334, 501, 349);
        }
        else{ //Closed
            fill(0);
            rect(429, 259, 25, 19);
            ellipse(426, 292, 4, 4);
        }
        // House
        line(405, 199, 405, 331); // Wall
        strokeWeight(6);
        line(398, 198, 484, 156); // Roof
        line(570, 198, 484, 156);
    }

    void drawPlayer() {
        // Player
        noStroke();
        fill(0);
        if (playerState != toolCount+2) { // Normal animation
            ellipse(76 + (playerState * playerStepSize), 280, 30, 30); // Head
            ellipse(90 + (playerState * playerStepSize), 276, 16, 10); // Nose
            ellipse(52 + (playerState * playerStepSize), 295, 10, 10); // Left Hand
            ellipse(95 + (playerState * playerStepSize), 305, 10, 10); // Right Hand
            ellipse(50 + (playerState * playerStepSize), 315, 10, 15); // Left Foot
            ellipse(85 + (playerState * playerStepSize), 330, 15, 10); // Right Foot
            ellipse(72 + (playerState * playerStepSize), 307, 18, 25); // Torso
            stroke(0);
            strokeWeight(4);
            curve(72 + (playerState * playerStepSize), 320, 72 + (playerState * playerStepSize), 300, 52 + (playerState * playerStepSize), 295, 52 + (playerState * playerStepSize), 315); // Left Arm
            curve(72 + (playerState * playerStepSize), 320, 72 + (playerState * playerStepSize), 300, 95 + (playerState * playerStepSize), 305, 95 + (playerState * playerStepSize), 325); // Right Arm
            curve(68 + (playerState * playerStepSize), 292, 66 + (playerState * playerStepSize), 315, 50 + (playerState * playerStepSize), 310, 50 + (playerState * playerStepSize), 292); // Left Leg
            curve(55 + (playerState * playerStepSize), 315, 72 + (playerState * playerStepSize), 315, 82 + (playerState * playerStepSize), 330, 82 + (playerState * playerStepSize), 370); // Right Leg
            fill(255);
            noStroke();
            ellipse(86 + (playerState * playerStepSize), 287, 15, 10); // Mouth
        }
        else { // Hurt animation
            ellipse(76 + (2.5f * playerStepSize), 340, 30, 30); // Head
            ellipse(90 + (2.5f * playerStepSize), 336, 16, 10); // Nose
            fill(255);
            ellipse(86 + (2.5f * playerStepSize), 347, 15, 10); // Mouth
        }
    }

    void drawTools() {
        fill(0);
        stroke(0);
        strokeCap(SQUARE);
        for (int n = 0; n < toolCount; n++) {
            strokeWeight(10);
            stroke(104, 80, 56);
            line(121 + (playerStepSize * n), -10 + (toolStepSize * toolStates[n]), 151 + (playerStepSize * n), -40 + (toolStepSize * toolStates[n])); // Handle
            strokeWeight(20);
            stroke(0);
            line(141 + (playerStepSize * n), -10 + (toolStepSize * toolStates[n]), 121 + (playerStepSize * n), -30 + (toolStepSize * toolStates[n])); // Head
        }
        strokeCap(ROUND); // Reset strokeCap
    }

    void drawHUD() {
        noStroke();
        if (gameMode == true) { // Game A / original style HUD
            drawHUDGameA();
        }
        else { // Game B / new style HUD
            drawHUDGameB();
        }

        if (isPaused) {
            fill(0);
            textSize(32);
            textAlign(CENTER);
            if (isMenu) {
                drawHUDMenu();
            }
            else if (isGameOver) {
                drawHUDGameOver();
            }
            else {
                text("PAUSED", width/2, height/2);
            }
        }
    }

    void drawHUDGameA() {
        if (missCount > 0){
            fill(200,0,0);
            textSize(12);
            textAlign(CENTER);
            text("MISS", 460, 100); // Display text if the player has been hit at least once
            for (int n = 0; n < missCount; n++) {
                ellipse(485 - (25 * n), 75, 20, 20); // Draw one symbol for each time the player has been hit
            }
        }
        fill(0);
        textSize(32);
        textAlign(RIGHT);
        if (score > 0) text(score, 500, 30); // Score number
    }

    void drawHUDGameB() {
        //text(score, 500, 30); // Score number (debug)
        int undrawnScore = score; // Keeps track of how many points have yet to be drawn to the screen.
        int circleSizeX = width/gameBColumnCount;
        int circleSizeY = height/gameBRowCount;   // Determine size of circles based on screen size and number to be drawn per row/column.
        boolean isBadCircle = true;
        fill(255,0,0);               // Draw red circles first.
        //for (int row = gameBRowCount - 1; row >= 0; row--) { // Bottom up variant
        for (int row = 0; row < gameBRowCount; row++) {
            //if (missCount < gameBRowCount - row) // Bottom up variant
            if (missCount <= row) { // Check if all necessary red rows have been drawn.
                isBadCircle = false; // Draw remaining circles as black.
            }
            for (int column = 0; column < gameBColumnCount && (undrawnScore > 0 || isBadCircle == true); column++) {
                drawGradientEllipse((int)(circleSizeX*column+circleSizeX*0.5),(int)(circleSizeY*row+circleSizeX*0.5),circleSizeX,circleSizeY, isBadCircle, 15);
                if (isBadCircle == false) {
                    undrawnScore--; // Determines the number of points left to draw.
                }
            }
        }
        if (isMenu == false) {
            fill(0);
            textSize(24);
            textAlign(RIGHT);
           // text(int((framesPassed / frameRate)+(missCount*timePenalty)), 500, 30); // Time passed
        }
    }

    void drawGradientEllipse(int posX, int posY, int sizeX, int sizeY, boolean isBadCircle, int gradientLayers) {
        int alpha = 0; // Set transparency to 100 percent
        int red = 255; // Color of red channel
        if (isBadCircle == false) {
            red = 0; // Set color of red channel to black
        }
        for (int layer = 0; layer < gradientLayers; layer++) {
            fill(red,0,0,alpha);
            ellipse(posX,posY,sizeX - (sizeX/gradientLayers * layer),sizeY - (sizeY/gradientLayers * layer));
            alpha = alpha + (255 / gradientLayers); // Decrease transparency for next circle
        }
    }

    void drawHUDMenu() {
        text("HARDHAT", width/2, height/3);
        textSize(24);
        text("GAME A", width/2, height/3 * 2.25f);
        text("GAME B", width/2, height/3 * 2.25f + 40);
        fill(0,200,0);
        if (gameMode == true) { // Draw selection indicator next to menu options
            ellipse(200, height/3 * 2.25f - 8, 10, 10);
        }
        else {
            ellipse(200, height/3 * 2.25f + 32, 10, 10);
        }
    }

    void drawHUDGameOver() {
        text("GAME OVER", width/2, height/3);
    }
}
