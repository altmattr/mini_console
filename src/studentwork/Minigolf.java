package studentwork;

import java.util.ArrayList;
import processing.core.*;
import processing.data.*;

public class Minigolf extends mqapp.MQApp {

    public String name() {
        return "Minigolf";
    }

    public String author() {
        return "Mark Saba";
    }

    public String description() {
        return "A game of golf... but mini";
    }

    static public void main(String[] passedArgs) {
        runSketch(appletArgs, new Minigolf());
    }

    float cameraZoom = 100;
    PVector cameraZoomBounds = new PVector(75, 125);
    PVector cameraPos = new PVector(0, 0);
    float moveSpeed = 2;
    float lerpSpeed = 0.1f;
    PVector moveDir = new PVector(0, 0);

    PVector holePos = new PVector(0, 0);
    float ballScale = 0.1f;
    float holeScale = 0.2f;

    float maxPower = 150;
    float hitPower = 5;
    float currPower = 5;
    float hitAngle = 0;
    boolean canHit = true;

    PVector courseCenter = new PVector(0, 0);
    int courseComplexity = 1;
    int maxDifficulty = 10;
    int minDifficulty = 1;

    int hits = 0;
    ArrayList<PVector> coursePoints = new ArrayList<PVector>();
    FloatList courseNormalAngles = new FloatList();

    ArrayList<PVector> staticColliderPoints = new ArrayList<PVector>();
    ArrayList<PVector> colliderPoints = new ArrayList<PVector>();

    PVector invs1;
    PVector invs2;

    int gameState = 0; // 0-Generating, 1-Playing, 2-Sunk, 3-Completed

    // Turn on for debugging lines. Press 'd' to turn on in-game
    boolean debugMode = false;

    public void setup() {
        size(displayWidth, displayHeight);
        // surface.setTitle("Golf");
        // surface.setLocation((displayWidth - width) / 2, (displayHeight - height) / 2
        // - 50); // Center screen
        gameState = 3; // COMPLETED - Show UI
    }

    public void draw() {
        background(15, 195, 215);
        DrawGrid();

        if (gameState == 1) { // PLAYING
            UpdateColliders();
            UpdateCameraPosition();
            DrawCourse();

            CheckCollision();
            DrawBall();

            if (debugMode) { // See if debug mode is active
                DebugNormals();
                DebugColliders();

                // Show the line last collided with
                if (invs1 != null && invs2 != null) {
                    stroke(0, 255, 255);
                    strokeWeight(3);
                    PVector invs1Remap = remapPoint(invs1);
                    PVector invs2Remap = remapPoint(invs2);
                    line(invs1Remap.x, invs1Remap.y, invs2Remap.x, invs2Remap.y);
                }
            }

            DrawFlag();
            DrawClub();
            CheckHole();
            DrawUI();
        }

        if (gameState == 2) { // SUNK
            UpdateCameraPosition();
            DrawCourse();
            DrawBall();
            DrawFlag();
        }
        if (gameState == 3) // COMPLETED
        {
            DrawGameOverUI();
        }
    }

    // Draw an arrow at position
    void DrawArrow(PVector pos, float arrowWidth, float arrowLength, int col) {
        strokeWeight((cameraZoom / 100) * arrowWidth);
        stroke(col);
        fill(col);
        line(pos.x, pos.y, pos.x, pos.y + arrowLength); // Draw arrow line

        strokeJoin(MITER); // Make triangle have pointed stroke
        triangle(pos.x, pos.y, pos.x - arrowWidth, pos.y + (arrowWidth * 2), pos.x + arrowWidth,
                pos.y + (arrowWidth * 2)); // Draw arrow point
    }

    // Update the game state
    void SetGameState(int state) {
        gameState = state;
        println("Set game state to: " + state);
    }

    void NewCourse() {
        GenerateGolfCourse();
        // Generate colliders from course points
        GenerateColliders();
        // Clean up generated colliders
        CleanColliders();
        // Generate collider normals
        GenerateNormals();

        gameState = 1; // PLAYING
    }

    // Generate the main points for the course
    void GenerateGolfCourse() {
        ResetGame();
        gameState = 0; // GENERATING
        coursePoints = new ArrayList<PVector>();

        cameraPos = new PVector(0, 0);

        int prevPointX = (width / 2);
        int prevPointY = (height / 2);
        float prevAngle = Math.round(random(-180, 180));

        coursePoints.add(new PVector(prevPointX, prevPointY));

        for (int n = 0; n < courseComplexity; n++) {

            float angle = random(-45, 45) + prevAngle;
            float pathLength = random(100, 300);

            int pathPointX = Math.round(cos(radians(angle)) * pathLength) + prevPointX;
            int pathPointY = Math.round(sin(radians(angle)) * pathLength) + prevPointY;

            PVector coursePoint = new PVector(pathPointX, pathPointY);

            coursePoints.add(coursePoint);

            prevPointX = pathPointX;
            prevPointY = pathPointY;
            prevAngle = angle;
        }
    }

    // Generate the line colliders around the edge of the course based off the main
    // course points
    void GenerateColliders() {
        // Foreach drawn point calc sides and curves to lines
        // Store line points
        // Check collisions on lines

        staticColliderPoints.clear();
        float hZoom = cameraZoom / 2;
        float acc = 8;

        PVector prevLP1 = null;
        PVector prevRP1 = null;

        PVector prevLP2 = null;
        PVector prevRP2 = null;

        ArrayList<PVector> tempColliderPoints = new ArrayList<PVector>();
        for (int n = 0; n < coursePoints.size() - 1; n++) {
            PVector p1 = remapPoint(coursePoints.get(n));
            PVector p2 = remapPoint(coursePoints.get(n + 1));

            float lineAngle = degrees(getAngle(p2, p1));
            PVector leftP1 = new PVector(cos(radians(lineAngle - 90)) * hZoom + p1.x,
                    sin(radians(lineAngle - 90)) * hZoom + p1.y);
            PVector rightP1 = new PVector(cos(radians(lineAngle + 90)) * hZoom + p1.x,
                    sin(radians(lineAngle + 90)) * hZoom + p1.y);

            PVector leftP2 = new PVector(cos(radians(lineAngle - 90)) * hZoom + p2.x,
                    sin(radians(lineAngle - 90)) * hZoom + p2.y);
            PVector rightP2 = new PVector(cos(radians(lineAngle + 90)) * hZoom + p2.x,
                    sin(radians(lineAngle + 90)) * hZoom + p2.y);

            if (prevLP1 != null && prevRP1 != null && prevLP2 != null && prevRP2 != null) {
                // Check if previous line intersects with connecting one
                if (lineIntersect(prevLP1, prevLP2, leftP1, leftP2) == null) {
                    // AddColliderLine(prevLP2, leftP1);

                    tempColliderPoints.add(prevLP2);
                    tempColliderPoints.add(leftP1);
                }
                if (lineIntersect(prevRP2, prevRP1, rightP2, rightP1) == null) {
                    // AddColliderLine(prevRP2, rightP1);

                    tempColliderPoints.add(prevRP2);
                    tempColliderPoints.add(rightP1);
                }
            }

            tempColliderPoints.add(leftP1);
            tempColliderPoints.add(leftP2);

            tempColliderPoints.add(rightP1);
            tempColliderPoints.add(rightP2);

            prevLP1 = leftP1;
            prevRP1 = rightP1;

            prevLP2 = leftP2;
            prevRP2 = rightP2;
        }

        for (int n = 0; n < tempColliderPoints.size(); n += 2) {
            PVector leftP1 = tempColliderPoints.get(n);
            PVector rightP1 = tempColliderPoints.get(n + 1);
            AddColliderLine(leftP1, rightP1);
        }

        // Generate end caps
        AddEndCap(remapPoint(coursePoints.get(0)), remapPoint(coursePoints.get(1)), hZoom, acc);
        AddEndCap(remapPoint(coursePoints.get(coursePoints.size() - 1)),
                remapPoint(coursePoints.get(coursePoints.size() - 2)), hZoom, acc);
    }

    // Draw a line collider semi circle between 2 points
    void AddEndCap(PVector cP1, PVector cP2, float hZoom, float acc) {
        float lineAngle = degrees(getAngle(cP2, cP1));
        PVector left = new PVector(cos(radians(lineAngle - 90)) * hZoom + cP1.x,
                sin(radians(lineAngle - 90)) * hZoom + cP1.y);
        PVector right = new PVector(cos(radians(lineAngle + 90)) * hZoom + cP1.x,
                sin(radians(lineAngle + 90)) * hZoom + cP1.y);

        float arcAngle = getAngle(cP1, left);
        float endAngle = arcAngle;

        if (cP1.x > cP2.x) {
            endAngle = getAngle(cP1, right);
        } else {
            endAngle = getAngle(right, cP1) + PI;
        }

        PVector mid = new PVector((right.x + left.x) / 2, (right.y + left.y) / 2);
        ArrayList<PVector> arcPoints = new ArrayList<PVector>();
        for (float a = arcAngle; a < endAngle - ((PI / acc) / 2) + (PI / acc); a += (PI / acc)) {
            float arcX = cos(a) * hZoom + mid.x;
            float arcY = sin(a) * hZoom + mid.y;

            PVector arcPos = new PVector(arcX, arcY);
            arcPoints.add(arcPos);
        }

        for (int g = 0; g < arcPoints.size() - 1; g++) {
            AddColliderLine(arcPoints.get(g), arcPoints.get(g + 1));
        }
    }

    // Add collider lines to collider array
    void AddColliderLine(PVector p1, PVector p2) {
        boolean containsLine = false;
        for (int n = 0; n < staticColliderPoints.size(); n += 2) {
            PVector cp1 = staticColliderPoints.get(n);
            PVector cp2 = staticColliderPoints.get(n + 1);

            if (cp1 == p1 && cp2 == p2) {
                containsLine = true;
            }
        }

        if (!containsLine && p1 != p2) {
            staticColliderPoints.add(p1);
            staticColliderPoints.add(p2);
        }
    }

    // Fix any issues created when generating colliders such as overlaps
    void CleanColliders() {
        // Make sure points are connected
        ArrayList<PVector> removePoints = new ArrayList<PVector>();

        for (int n = 0; n < staticColliderPoints.size() - 1; n += 2) {
            PVector l1p1 = staticColliderPoints.get(n);
            PVector l1p2 = staticColliderPoints.get(n + 1);

            for (int i = 0; i < staticColliderPoints.size() - 1; i += 2) {
                if (i != n) {
                    PVector l2p1 = staticColliderPoints.get(i);
                    PVector l2p2 = staticColliderPoints.get(i + 1);

                    PVector intersectP = lineIntersect(l1p1, l1p2, l2p1, l2p2);
                    if (intersectP != null) {
                        // get closest points from each line
                        // set to intersect point
                        float l1p1D = dist(l1p1.x, l1p1.y, intersectP.x, intersectP.y);
                        float l1p2D = dist(l1p2.x, l1p2.y, intersectP.x, intersectP.y);

                        float l2p1D = dist(l2p1.x, l2p1.y, intersectP.x, intersectP.y);
                        float l2p2D = dist(l2p2.x, l2p2.y, intersectP.x, intersectP.y);

                        PVector cP1 = l1p1D <= l1p2D ? l1p1 : l1p2;
                        PVector cP2 = l2p1D <= l2p2D ? l2p1 : l2p2;
                        float pDist = dist(cP1.x, cP1.y, cP2.x, cP2.y);

                        if (pDist > 1) { // Stops gittering
                            if (l1p1D <= l1p2D) {
                                staticColliderPoints.set(n, intersectP);
                            } else {
                                staticColliderPoints.set(n + 1, intersectP);
                            }

                            if (l2p1D <= l2p2D) {
                                staticColliderPoints.set(i, intersectP);
                            } else {
                                staticColliderPoints.set(i + 1, intersectP);
                            }
                        }
                    }
                }
            }
        }

        for (PVector p : removePoints) {
            staticColliderPoints.remove(p);
        }
    }

    // Update the colliders and their positions based on the camera
    void UpdateColliders() {
        colliderPoints.clear();
        for (PVector p : staticColliderPoints) {
            colliderPoints.add(remapPoint(p));
        }
    }

    // Show all the colliders
    void DebugColliders() {
        strokeWeight((cameraZoom / 100) * 2);
        stroke(255, 0, 0);
        noFill();

        for (int n = 0; n < colliderPoints.size(); n += 2) {
            PVector p1 = colliderPoints.get(n);
            PVector p2 = colliderPoints.get(n + 1);

            stroke(0, 0, 255);
            circle(p1.x, p1.y, 5);
            circle(p2.x, p2.y, 5);
            stroke(255, 0, 0);
            line(p1.x, p1.y, p2.x, p2.y);
        }
    }

    void GenerateNormals() {
        ArrayList<PVector> normalPoints = new ArrayList<PVector>();
        courseNormalAngles.clear();

        for (int n = 0; n < coursePoints.size() - 1; n++) {
            PVector c = remapPoint(coursePoints.get(n));
            PVector c2 = remapPoint(coursePoints.get(n + 1));

            normalPoints.add(c);
            normalPoints.add(c2);
            normalPoints.add(new PVector((c.x + c2.x) / 2, (c.y + c2.y) / 2));
        }

        for (int i = 0; i < staticColliderPoints.size(); i += 2) {
            PVector p1 = staticColliderPoints.get(i);
            PVector p2 = staticColliderPoints.get(i + 1);

            PVector cP = new PVector((p2.x + p1.x) / 2, (p2.y + p1.y) / 2);

            PVector nP = normalPoints.get(0);
            float closestDist = dist(nP.x, nP.y, cP.x, cP.y);
            PVector closestPoint = normalPoints.get(0);

            for (int n = 0; n < normalPoints.size(); n++) {
                nP = normalPoints.get(n);
                float dist = dist(nP.x, nP.y, cP.x, cP.y);

                if (dist < closestDist) {
                    closestDist = dist;
                    closestPoint = nP;
                }
            }

            float a = getAngle(closestPoint, cP);
            courseNormalAngles.append(a);
        }
        println(courseNormalAngles.size());
    }

    void DebugNormals() {
        for (int i = 0; i < colliderPoints.size(); i += 2) {
            PVector p1 = colliderPoints.get(i);
            PVector p2 = colliderPoints.get(i + 1);

            PVector cP = new PVector((p2.x + p1.x) / 2, (p2.y + p1.y) / 2);

            // println(courseNormalAngles.size());
            float a = courseNormalAngles.get(Math.round(i / 2));

            PVector nP = new PVector(cos(a) * 25 + cP.x, sin(a) * 25 + cP.y);

            stroke(255, 0, 255);
            strokeWeight((cameraZoom / 100) * 2);
            line(cP.x, cP.y, nP.x, nP.y);
        }

        for (int n = 0; n < coursePoints.size() - 1; n++) {
            fill(255, 255, 0);
            PVector c = remapPoint(coursePoints.get(n));
            PVector c2 = remapPoint(coursePoints.get(n + 1));
            circle((c.x + c2.x) / 2, (c.y + c2.y) / 2, 10);

            fill(255, 127, 0);
            circle(c.x, c.y, 5);
            circle(c2.x, c2.y, 5);
            circle((c.x + c2.x) / 2, (c.y + c2.y) / 2, 5);

            stroke(255, 255, 0);
            strokeWeight((cameraZoom / 100) * 2);
            line(c.x, c.y, (c.x + c2.x) / 2, (c.y + c2.y) / 2);
            line(c2.x, c2.y, (c.x + c2.x) / 2, (c.y + c2.y) / 2);
        }
    }

    float invWiggleAngle = 0;
    float wiggleAngle = 0;
    float wiggleMax = 15;
    float wiggleMin = -15;

    boolean wiggleUp = false;

    void DrawGrid() {
        boolean yCol = false;
        for (float n = 0; n < width / cameraZoom; n++) {
            for (float i = 0; i < height / cameraZoom; i++) {
                float x = n * (cameraZoom * 2);
                float y = i * (cameraZoom * 2);

                wiggleAngle += wiggleUp ? 0.001f : -0.001f;
                if (wiggleAngle > wiggleMax) {
                    wiggleUp = false;
                }
                if (wiggleAngle < wiggleMin) {
                    wiggleUp = true;
                }

                invWiggleAngle += wiggleUp ? -0.001f : 0.001f;
                if (invWiggleAngle > wiggleMax) {
                    wiggleUp = false;
                }
                if (invWiggleAngle < wiggleMin) {
                    wiggleUp = true;
                }

                float wAngle = 0;
                if (n % 2 == 0) {
                    if (i % 2 == 0) {
                        wAngle = wiggleAngle;
                    } else {
                        wAngle = invWiggleAngle;
                    }
                } else {
                    if (i % 2 == 0) {
                        wAngle = invWiggleAngle;
                    } else {
                        wAngle = wiggleAngle;
                    }
                }

                int fillCol = color(15, 210, 225);

                PVector TL = new PVector(cos(radians(wAngle - 90)) * cameraZoom + x,
                        sin(radians(wAngle - 90)) * cameraZoom + y);
                PVector TR = new PVector(cos(radians(wAngle)) * cameraZoom + x, sin(radians(wAngle)) * cameraZoom + y);
                PVector BL = new PVector(cos(radians(wAngle + 90)) * cameraZoom + x,
                        sin(radians(wAngle + 90)) * cameraZoom + y);
                PVector BR = new PVector(cos(radians(wAngle + 180)) * cameraZoom + x,
                        sin(radians(wAngle + 180)) * cameraZoom + y);

                noStroke();
                fill(fillCol);
                quad(TR.x, TR.y, TL.x, TL.y, BR.x, BR.y, BL.x, BL.y);

                yCol = !yCol;
            }
        }
    }

    void DrawCourse() {
        ArrayList<PVector> drawPoints = new ArrayList<PVector>();
        PVector[] arrayPoints = new PVector[coursePoints.size()]; // Convert from arrayList to array
        courseCenter = centroid(coursePoints.toArray(arrayPoints)); // Get center point

        for (int n = 0; n < coursePoints.size(); n++) {
            PVector p = coursePoints.get(n); // Get point at array index
            PVector newPos = remapPoint(p); // Remap the point to fit camera
            drawPoints.add(newPos); // Add new point to draw point arraylist
        }

        strokeJoin(ROUND); // Draw rounded lines
        noFill();

        strokeWeight(cameraZoom * 1.25f);
        stroke(230, 220, 210);

        beginShape();
        for (int n = 0; n < drawPoints.size(); n++) {
            PVector p1 = drawPoints.get(n);
            vertex(p1.x, p1.y);
        }
        endShape();

        strokeWeight(cameraZoom);
        stroke(130, 195, 72);

        beginShape();
        for (int n = 0; n < drawPoints.size(); n++) {
            PVector p1 = drawPoints.get(n);
            vertex(p1.x, p1.y);
        }
        endShape();

        // Draw hole at the last drawn point
        noStroke();
        fill(127);
        PVector lPoint = drawPoints.get(drawPoints.size() - 1);
        holePos = lPoint;
        circle(lPoint.x, lPoint.y, cameraZoom * holeScale);
    }

    PVector hasCollided = null;

    void CheckCollision() {
        // See where player exists rect or circle. bounce them in opposite direction
        PVector mPos = new PVector(width / 2, height / 2);

        boolean noCollision = true;
        for (int n = 0; n < colliderPoints.size(); n += 2) {
            PVector p1 = colliderPoints.get(n);
            PVector p2 = colliderPoints.get(n + 1);

            boolean colliding = circleCollidingLine(mPos, cameraZoom * ballScale, p1, p2);
            PVector nextPos = new PVector(mPos.x + (moveDir.x * -moveSpeed), mPos.y + (moveDir.y * -moveSpeed));
            boolean goingToCollide = circleCollidingLine(nextPos, cameraZoom * ballScale, p1, p2);

            if (colliding || goingToCollide) {
                noCollision = false;
                if (hasCollided == null) {
                    println("COLLIDING");

                    float normalAngle = courseNormalAngles.get(Math.round(n / 2));

                    PVector normalDir = new PVector(-cos(normalAngle), -sin(normalAngle));
                    normalDir.normalize();

                    moveDir = normalDir;

                    movePercent = (movePercent * 1.1f); // Remove speed on bounce

                    invs1 = inverseRemapPoint(p1);
                    invs2 = inverseRemapPoint(p2);
                }
            }
            if (noCollision) {
                hasCollided = null;
            }
        }
    }

    float movePercent = 0;

    void UpdateCameraPosition() {
        if (!canHit) {
            movePercent += lerpSpeed / 100;
            PVector m = moveDir;
            m.lerp(new PVector(0, 0), movePercent);
            cameraPos = new PVector(cameraPos.x + (m.x * moveSpeed), cameraPos.y + (m.y * moveSpeed));
        }

        currPower -= 1;
        currPower = currPower < 0 ? 0 : currPower;

        if (moveDir.x < 0.01f && moveDir.x > -0.01f) {
            if (moveDir.y < 0.01f && moveDir.y > -0.01f) {
                movePercent = 0;
                moveDir = new PVector(0, 0);
                canHit = true;
            }
        }
    }

    float ballAlpha = 255;
    float fadeSpeed = 10;

    void DrawBall() {
        if (gameState == 2) { // SUNK
            if (ballAlpha > 0) {
                ballAlpha -= fadeSpeed;
            } else {
                SetGameState(3); // COMPLETED
            }
        }

        noStroke();
        fill(255, 255, 255, ballAlpha);
        circle(width / 2, height / 2, cameraZoom * ballScale);
    }

    void CheckHole() {
        float dist = dist(width / 2, height / 2, holePos.x, holePos.y);
        if (dist < (cameraZoom * (holeScale / 2))) {
            println("Sunk");
            println(holePos + " : " + cameraPos);

            gameState = 2; // SUNK

            movePercent = 0;
            canHit = false;
            moveDir = new PVector(0, 0);
        }
    }

    void DrawClub() {
        if (clickedBall) {
            PVector mouse = new PVector(mouseX, mouseY);
            PVector hWH = new PVector(width / 2, height / 2);

            float powerDist = dist(hWH.x, hWH.y, mouse.x, mouse.y);
            powerDist = powerDist > maxPower ? maxPower : powerDist;

            float angle = getAngle(mouse, hWH);
            float powerPercent = (powerDist / maxPower);
            int powerColor = lerpHSBColor(color(0, 255, 0), color(255, 0, 0), powerPercent);

            stroke(255);
            noFill();
            strokeWeight(ballScale * cameraZoom * 0.2f);
            circle(hWH.x, hWH.y, cameraZoom * ballScale * 1.5f); // Draw circle around ball

            DrawArrow(hWH, 20, angle, Math.round(powerDist), powerColor); // Draw arrow to show ball direction and power
        }
    }

    float flagY = 0;

    void DrawFlag() {
        // Draw flag at hole
        float dist = dist(width / 2, height / 2, holePos.x, holePos.y);

        float distPercent = dist / 100;
        flagY = dist < 100 ? 25 - (25 * distPercent) : 0;

        PVector flagPos = new PVector(holePos.x, holePos.y - flagY);
        Flag(flagPos, 100, 25);
    }

    boolean clickedBall = false;

    public void mousePressed(processing.event.MouseEvent event) {
        if (gameState == 1) { // PLAYING
            if (mouseButton == LEFT) {
                if (canHit) {
                    float mouseDist = dist(mouseX, mouseY, width / 2, height / 2);
                    if (mouseDist < cameraZoom * ballScale) {
                        clickedBall = true;
                    }
                }
            }
        }
    }

    public void mouseReleased() {
        if (gameState == 1) { // PLAYING
            if (clickedBall) {
                canHit = false;
                hits++;
                clickedBall = false;

                PVector mouse = new PVector(mouseX, mouseY);
                PVector hWH = new PVector(width / 2, height / 2);

                float powerDist = dist(hWH.x, hWH.y, mouse.x, mouse.y);
                powerDist = powerDist > maxPower ? maxPower : powerDist;
                powerDist *= hitPower;

                float angle = getAngle(mouse, hWH);
                hitAngle = angle;

                println("Hit with power " + powerDist + " at angle " + degrees(angle));
                currPower = powerDist;

                float moveX = cos(angle) * (powerDist / maxPower);
                float moveY = sin(angle) * (powerDist / maxPower);

                moveDir = new PVector(moveX, moveY);
            }
        }
    }

    float sliderHandleX = 0;

    public void mouseClicked() {
        if (gameState == 3) { // COMPLETED
            // Next course button
            if (pointInRect(new PVector(mouseX, mouseY), new PVector(width / 2, height * 0.55f),
                    new PVector(125, 25))) {
                NewCourse();
            }

            // Difficulty slider handle
            if (pointInRect(new PVector(mouseX, mouseY), new PVector(width / 2, height / 2 * 0.5f),
                    new PVector(150, 50))) {
                sliderHandleX = mouseX;
            }
        }
    }

    public void mouseDragged() {
        if (gameState == 3) { // COMPLETED
            if (pointInRect(new PVector(mouseX, mouseY), new PVector(width / 2, height / 2),
                    new PVector(width * 0.2f, 10))) {
                sliderHandleX = mouseX;
            }
        }
    }

    // Zoom in and out
    // void mouseWheel(MouseEvent event) {
    // float e = event.getCount();
    // cameraZoom = cameraZoom + e > cameraZoomBounds.x && cameraZoom + e <
    // cameraZoomBounds.y ? cameraZoom + e : cameraZoom;as
    // }

    public void keyPressed() {
        if (key == 'd') {
            debugMode = !debugMode;
        }

        // Exit current game
        if (key == ESC) {
            gameState = 3;
            SetGameState(3);
        }
    }

    void DrawUI() {
        // Display how many hits the player has made
        fill(255);
        textSize(18);
        text("Hits: " + hits, 50, 25);
    }

    void DrawGameOverUI() {
        // Overlay background
        stroke(0);
        strokeWeight(5);
        fill(255);
        rectMode(CENTER);
        rect(width / 2, height / 2, width * 0.3f, height * 0.2f, 21);

        // Generate course button
        noStroke();
        fill(color(130, 195, 72));
        rect(width / 2, height * 0.55f, 125, 25, 14);

        // Draw difficulty slider
        float sliderXMinBound = width / 2 - width * 0.1f;
        float sliderXMaxBound = width / 2 + width * 0.1f;

        strokeWeight(2);
        stroke(0);
        line(sliderXMinBound, height * 0.5f, sliderXMaxBound, height * 0.5f);

        // Draw slider handle
        fill(0);
        float diffSliderX = sliderHandleX;
        float sliderLength = sliderXMaxBound - sliderXMinBound;

        float roundTo = (sliderLength) / (10); // Max complexity of 10. Create ratio to slider length
        float sliderX = Math.round((diffSliderX + (roundTo / 2)) / roundTo) * roundTo; // Round handle x pos to nearest
                                                                                       // node

        // Clamp sliderX to min and max bounds
        sliderX = sliderX < sliderXMinBound ? sliderXMinBound : sliderX;
        sliderX = sliderX > sliderXMaxBound ? sliderXMaxBound : sliderX;

        circle(sliderX, height * 0.5f, 10); // Draw handle on slider

        float sliderPercent = (sliderX - sliderXMinBound) / sliderLength; // Get slider percent
        int newDifficulty = ceil(sliderPercent * (maxDifficulty - minDifficulty) + (minDifficulty)); // Calc difficulty
                                                                                                     // based on slider
                                                                                                     // pos
        courseComplexity = newDifficulty; // Set new course difficult

        // Generate course button text
        textSize(16);
        text("NEW COURSE", width / 2, height * 0.555f);

        // Difficulty text
        text("Difficulty - " + courseComplexity, width / 2, height * 0.475f);

        // Title text
        fill(0);
        textAlign(CENTER);
        textSize(32);
        String hitText = "HOLE IN " + hits;
        String titleText = hits > 0 ? hitText : "GOLF";
        text(titleText, width / 2, height * 0.45f); // Show title text
    }

    void DrawArrow(PVector pos, float offsetDist, float rotation, int arrowLength, int col) {
        // Get rotated point with offset from position
        float offsetX = cos(rotation) * offsetDist + pos.x;
        float offsetY = sin(rotation) * offsetDist + pos.y;

        // Get back point of arrow with arrowlength from offset position
        float rotatedX = cos(rotation) * (arrowLength - offsetDist) + offsetX;
        float rotatedY = sin(rotation) * (arrowLength - offsetDist) + offsetY;

        strokeWeight((cameraZoom / 100) * 5);
        stroke(col);
        fill(col);
        line(offsetX, offsetY, rotatedX, rotatedY); // Draw arrow line

        // Triangle point
        float tx1 = offsetX;
        float ty1 = offsetY;

        // triangle left side
        float sideAngleOffset = radians(25);
        float tx2 = cos(rotation - sideAngleOffset) * (offsetDist) + offsetX;
        float ty2 = sin(rotation - sideAngleOffset) * (offsetDist) + offsetY;

        // triangle right side
        float tx3 = cos(rotation + sideAngleOffset) * (offsetDist) + offsetX;
        float ty3 = sin(rotation + sideAngleOffset) * (offsetDist) + offsetY;
        strokeJoin(MITER); // Make triangle have pointed stroke
        triangle(tx1, ty1, tx2, ty2, tx3, ty3); // Draw arrow point
    }

    void Flag(PVector pos, int flagLength, int flagSize) {
        strokeWeight((cameraZoom / 100) * 5);
        stroke(255);
        line(pos.x, pos.y, pos.x, pos.y - flagLength);

        noStroke();
        strokeJoin(ROUND);
        fill(color(255, 0, 0));
        triangle(pos.x, pos.y - flagLength, pos.x, pos.y - (flagLength - flagSize), pos.x - flagSize,
                pos.y - (flagLength - (flagSize / 2)));
    }

    void ResetGame() {
        ballAlpha = 255;
        clickedBall = false;
        movePercent = 0;
        hits = 0;

        cameraPos = new PVector(0, 0);
        movePercent = 1;
        moveDir = new PVector(0, 0);
        cameraZoom = 100;
    }

    int lerpHSBColor(int from, int to, float percent) {
        colorMode(HSB);
        int lerpedColor = lerpColor(from, to, percent);
        colorMode(RGB);
        return lerpedColor;
    }

    PVector centroid(PVector[] points) {
        PVector center = new PVector(0, 0);
        for (int n = 0; n < points.length; n++) {
            center = new PVector(center.x + points[n].x, center.y + points[n].y); // Add each point to center
        }
        center = new PVector(center.x / points.length, center.y / points.length); // Divide center by amount of points

        return center; // Return average point
    }

    PVector remapPoint(PVector p) {
        PVector re = new PVector(p.x + cameraPos.x, p.y + cameraPos.y);
        return re; // Return new point pos
    }

    PVector inverseRemapPoint(PVector p) {
        PVector invRe = new PVector(p.x - cameraPos.x, p.y - cameraPos.y);
        return invRe;
    }

    float getAngle(PVector p) {
        return atan2(p.y, p.x);
    }

    float getAngle(PVector of, PVector from) {
        PVector p1 = of;
        PVector p2 = from;

        float dX = p1.x - p2.x;
        float dY = p1.y - p2.y;

        float angle = atan2(dY, dX);

        return angle;
    }

    float getAngle(PVector of, PVector center, PVector from) {
        float atanOfCenter = atan2(center.y - of.y, center.x - of.x);
        float atanOfFrom = atan2(from.y - of.y, from.x - of.x);

        float angle = atanOfFrom - atanOfCenter;
        return angle;
    }

    boolean circleCollidingLine(PVector cPos, float cRad, PVector lP1, PVector lP2) {
        float dist = dist(cPos.x, cPos.y, lP1.x, lP1.y);
        float angle = getAngle(lP1, lP2);

        PVector closestPoint = new PVector((cos(angle) * -dist) + lP1.x, (sin(angle) * -dist) + lP1.y);
        float cDist = dist(cPos.x, cPos.y, closestPoint.x, closestPoint.y);

        PVector rectPos = new PVector((lP1.x + lP2.x) / 2, (lP1.y + lP2.y) / 2);
        PVector size = new PVector(lP2.x - lP1.x, lP2.y - lP1.y);
        size = new PVector(abs(size.x), abs(size.y));

        boolean colliding = false;
        if (cDist < cRad) {
            if (pointInRect(cPos, rectPos, size)) {
                colliding = true;
            }
        }

        float topY = (lP1.y < lP2.y ? lP1.y : lP2.y);
        float bottomY = (topY == lP1.y ? lP2.y : lP1.y);
        if (cPos.y >= topY) {
            if (cPos.y <= bottomY) {
                if (abs(lP2.x - lP1.x) < cRad) {
                    if (abs(cPos.x - lP1.x) < cRad) {
                        colliding = true;
                    }
                }
            }
        }

        float leftX = (lP1.x < lP2.x ? lP1.x : lP2.x);
        float rightX = (leftX == lP1.x ? lP2.x : lP1.x);
        if (cPos.x >= leftX) {
            if (cPos.x <= rightX) {
                if (abs(lP2.y - lP1.y) < cRad) {
                    if (abs(cPos.y - lP1.y) < cRad) {
                        colliding = true;
                    }
                }
            }
        }

        return colliding;
    }

    // https://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
    // Gavin's answer - modified
    PVector lineIntersect(PVector p0, PVector p1, PVector p2, PVector p3) {
        PVector p = null;
        PVector s1 = new PVector(p1.x - p0.x, p1.y - p0.y);
        PVector s2 = new PVector(p3.x - p2.x, p3.y - p2.y);

        float s, t;
        s = (-s1.y * (p0.x - p2.x) + s1.x * (p0.y - p2.y)) / (-s2.x * s1.y + s1.x * s2.y);
        t = (s2.x * (p0.y - p2.y) - s2.y * (p0.x - p2.x)) / (-s2.x * s1.y + s1.x * s2.y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
            // Collision detected
            p = new PVector(p0.x + (t * s1.x), p0.y + (t * s1.y));
            return p;
        }

        return null; // No collision
    }
    // End reference

    boolean pointInRect(PVector point, PVector rectPos, PVector rectScale) {
        boolean colliding = false;

        if (point.x < rectPos.x + (rectScale.x / 2)) {
            if (point.x > rectPos.x - (rectScale.x / 2)) {
                if (point.y < rectPos.y + (rectScale.y / 2)) {
                    if (point.y > rectPos.y - (rectScale.y / 2)) {
                        colliding = true;
                    }
                }
            }
        }
        return colliding;
    }

    boolean pointInSquare(PVector point, PVector squarePos, float size) {
        boolean colliding = false;
        if (point.x < squarePos.x + size) {
            if (point.x > squarePos.x - size) {
                if (point.y < squarePos.y + size) {
                    if (point.y > squarePos.y - size) {
                        colliding = true;
                    }
                }
            }
        }
        return colliding;
    }

    boolean rectIntersection(PVector pos1, PVector size1, PVector pos2, PVector size2) {
        boolean colliding = false;

        if (pos1.x < pos2.x - size2.x) {
            if (pos1.x + size1.x > pos2.x) {
                if (pos1.y > pos2.y - size2.y) {
                    if (pos1.y < pos2.y + size2.y) {
                        colliding = true;
                    }
                }
            }
        }
        return colliding;
    }
}