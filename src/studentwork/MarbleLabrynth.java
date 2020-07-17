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

public class MarbleLabrynth extends mqapp.MQApp {


    public String name() {return "Marble Labrynth";}
    public String author() { return "Zachary Dingwall"; }
    public String description() { return "Description Pending";}
    Ball ball;
    Board board;
    LevelOne levelOne;
    Camera cam;
    Physics physics;

    float deltaTime;
    int prevMillis;

    boolean bWinScreen = false;
    float totalSec;

    public void setup() {
        size(1280, 1024, P3D);
        smooth(16);
        //noSmooth();

        perspective();

        physics = new Physics();

        cam = new Camera();

        board = new Board();
        levelOne = new LevelOne();
        ball = new Ball(levelOne.spawnPoint);
        board.Populate();
    }

    public void draw() {
        background(0);
        noStroke();

        ambientLight(100, 100, 100, cam.pos.x, cam.pos.y, cam.pos.z);
        directionalLight(100, 100, 100, -1, -1, -1);
        if (!bWinScreen)
        {
            spotLight(255, 255, 255,
                    cam.pos.x, cam.pos.y, cam.pos.z,
                    (ball.pos.x - cam.pos.x) + 5, ball.pos.y - cam.pos.y, (ball.pos.z - cam.pos.z) + 5,
                    PI/20, 500);

            totalSec += deltaTime;
        }
        else {
            directionalLight(100, 100, 100, 0, -1, 0);
        }

        cam.Update();
        ball.Update();
        board.Update();

        ball.Draw();
        board.Draw();
        if (bWinScreen) DrawWinScreen();

        WallCollisionResolution();
        HoleCollisionResolution();
        GoalCollisionResolution();

        //DrawAxis();

        deltaTime = (millis() - prevMillis) / 1000.0f;
        prevMillis = millis();
    }

    public void WallCollisionResolution()
    {
        for (int i = 0; i < board.walls.size(); i++)
        {
            PVector p = physics.sphereBoxCollision(ball, board.walls.get(i));
            ball.pos.x += p.x;
            ball.pos.z += p.z;
        }
    }

    public void HoleCollisionResolution()
    {
        for (int i = 0; i < board.holes.size(); i++)
        {
            if (physics.sphereCircleCollision(ball, board.holes.get(i)))
            {
                ball.Reset(levelOne.spawnPoint);
            }
        }
    }

    public void GoalCollisionResolution()
    {
        if (physics.sphereCircleCollision(ball, board.goal))
        {
            bWinScreen = true;
        }
    }

    public void DrawWinScreen()
    {
        pushMatrix();
        translate(0, cam.pos.y - 150, 0);
        rotateX(-PI / 2.0f);
        fill(255);
        rectMode(CENTER);
        rect(0, 0, 120, 60, 5);

        String s = "You Win!";
        String r = "Press R to restart";

        int min = (int)totalSec / 60;
        int sec = (int)totalSec % 60;

        String t = min + ":" + sec;

        fill(0);
        textMode(SHAPE);
        textAlign(CENTER, CENTER);

        textSize(12);
        text(s, 0, -15, 1);

        textSize(8);
        text(t, 0, 0, 1);

        textSize(8);
        text(r, 0, 20, 1);

        popMatrix();
    }

    public void DrawAxis()
    {
        pushMatrix();
        fill(255);
        translate(0, 0, 0);
        sphere(5);
        popMatrix();

        pushMatrix();
        fill(255, 0, 0);
        translate(50, 0, 0);
        sphere(5);
        popMatrix();

        pushMatrix();
        fill(0, 0, 255);
        translate(0, 50, 0);
        sphere(5);
        popMatrix();

        pushMatrix();
        fill(0, 255, 0);
        translate(0, 0, 50);
        sphere(5);
        popMatrix();
    }
    class Ball {
        PVector pos;
        float rad = 3;
        PVector col;

        PVector vel;
        PVector acc;

        float accelSpeed = 15;

        Ball(PVector spawn)
        {
            col = new PVector(0, 200, 100);
            pos = new PVector(spawn.x, rad, spawn.z);
            vel = new PVector(0,0,0);
            acc = new PVector(0,0,0);
        }


        public void Draw()
        {
            pushMatrix();
            fill(col.x, col.y, col.z);
            translate(pos.x, pos.y, pos.z);
            sphere(rad);
            popMatrix();
        }

        public void Reset(PVector spawn)
        {
            pos = new PVector(spawn.x, rad, spawn.z);
            vel = new PVector(0,0,0);
            acc = new PVector(0,0,0);
            bWinScreen = false;
            totalSec = 0;
        }

        public void Update()
        {
            vel.x += acc.x * deltaTime * accelSpeed * abs(cam.pos.x / cam.maxX);
            vel.y += acc.y * deltaTime * accelSpeed;
            vel.z += acc.z * deltaTime * accelSpeed * abs(cam.pos.z / cam.maxZ);

            pos.x += vel.x * deltaTime;
            pos.y += vel.y * deltaTime;
            pos.z += vel.z * deltaTime;
        }
    }
    class Board {

        // walls are based on a grid
        // will start out on a 13x13 board
        // each cell will be 8 with 2 for a buffer

        // because we want to keep the game using primitives we wont actually use real holes
        // rather we will paint on circles to define an area that the player will lose if the center of the ball enters this area

        PVector size;
        PVector pos;
        PVector col;

        float cellSize = 10;
        float cellBuffer = 2;

        ArrayList<Wall> walls;
        ArrayList<Hole> holes;
        Hole goal;

        Board()
        {
            size = new PVector(130, 30, 130);
            pos = new PVector(0, -size.y / 2.0f, 0);
            col = new PVector(180, 0, 180);
        }

        public void Update()
        {
        }

        public void Draw()
        {
            pushMatrix();
            translate(pos.x, pos.y, pos.z);
            fill(col.x, col.y, col.z);
            box(size.x, size.y, size.z);
            popMatrix();

            for (int i = 0; i < walls.size(); i++)
            {
                walls.get(i).Draw();
            }

            for (int i = 0; i < holes.size(); i++)
            {
                holes.get(i).Draw();
            }

            goal.Draw();
        }

        private void Populate()
        {
            walls = levelOne.walls;
            holes = levelOne.holes;
            goal = levelOne.goal;
        }

    }
    class Camera {

        PVector pos = new PVector(0, 200, 0);
        private float maxX = 100;
        private float maxZ = 100;
        PVector input = new PVector(0, 0, 0);
        private float speed = 500;

        Camera()
        {
            camera(pos.x, pos.y, pos.z, 0, 0, 0, 0, 0, -1);
        }

        public void Update()
        {
            if (input.x == 0)
                pos.x = pos.x/4 * 3;
            if (input.y == 0)
                pos.z = pos.z/4 * 3;

            pos.x += input.x * speed * deltaTime;
            pos.z += input.y * speed * deltaTime;

            if (pos.x < -maxX) pos.x = -maxX;
            else if (pos.x > maxX) pos.x = maxX;
            if (pos.z < -maxZ) pos.z = -maxZ;
            else if (pos.z > maxZ) pos.z = maxZ;

            camera(pos.x, pos.y, pos.z, 0, 0, 0, 0, 0, -1);
        }
    }
    class Hole {

        PVector pos;
        PVector col;
        float rad = 4;
        float a = 0;

        Hole(int gridX, int gridY)
        {
            pos = new PVector(gridX * board.cellSize - (board.size.x / 2.0f) + rad + (board.cellBuffer / 2.0f),
                    1,
                    gridY * board.cellSize - (board.size.z / 2.0f) + rad + (board.cellBuffer / 2.0f));
            col = new PVector(0, 0, 0);
        }

        public void Draw()
        {
            pushMatrix();
            translate(pos.x, pos.y, pos.z);
            rotateX(PI / 2.0f);
            fill(col.x, col.y, col.z);
            ellipse(0, 0, rad*2, rad*2);
            popMatrix();
        }

    }
    public void keyPressed()
    {
        if (key == 'r' || key == 'R')
        {
            bWinScreen = false;
            ball.Reset(levelOne.spawnPoint);
        }

        if (key == 'a' || key == 'A' || keyCode == LEFT)
        {
            cam.input.x += 1;
            ball.acc.x += -1;
        }

        if (key == 'd' || key == 'D' || keyCode == RIGHT)
        {
            cam.input.x += -1;
            ball.acc.x += 1;
        }

        if (key == 'w' || key == 'W' || keyCode == UP)
        {
            cam.input.y += -1;
            ball.acc.z += 1;
        }

        if (key == 's' || key == 'S' || keyCode == DOWN)
        {
            cam.input.y += 1;
            ball.acc.z += -1;
        }
    }

    public void keyReleased()
    {
        if (key == 'a' || key == 'A' || keyCode == LEFT)
        {
            cam.input.x += -1;
            ball.acc.x += 1;
        }

        if (key == 'd' || key == 'D' || keyCode == RIGHT)
        {
            cam.input.x += 1;
            ball.acc.x += -1;
        }

        if (key == 'w' || key == 'W' || keyCode == UP)
        {
            cam.input.y += 1;
            ball.acc.z += -1;
        }

        if (key == 's' || key == 'S' || keyCode == DOWN)
        {
            cam.input.y += -1;
            ball.acc.z += 1;
        }
    }
    class LevelOne{

        ArrayList<Wall> walls;
        ArrayList<Hole> holes;

        Hole goal;

        PVector spawnPoint = new PVector(10, 0, 60);

        LevelOne()
        {
            AddWalls();
            AddHoles();
            goal = new Hole(12, 6);
            goal.col = new PVector(0, 255, 0);
        }

        public void AddWalls()
        {
            walls = new ArrayList<Wall>();

            // out walls
            walls.add( new Wall(false, 13, 0, 0));
            walls.add( new Wall(false, 13, 0, 13));
            walls.add( new Wall(true, 13, 0, 0));
            walls.add( new Wall(true, 13, 13, 0));

            walls.add( new Wall(false, 1, 8, 1));

            walls.add( new Wall(false, 1, 5, 3));

            walls.add( new Wall(false, 2, 1, 4));
            walls.add( new Wall(false, 1, 9, 4));
            walls.add( new Wall(false, 1, 6, 4));

            walls.add( new Wall(false, 1, 12, 6));

            walls.add( new Wall(false, 1, 9, 7));

            walls.add( new Wall(false, 2, 1, 8));

            walls.add( new Wall(false, 1, 5, 9));
            walls.add( new Wall(false, 2, 8, 9));
            walls.add( new Wall(false, 2, 0, 9));

            walls.add( new Wall(false, 1, 1, 11));
            walls.add( new Wall(false, 1, 8, 11));
            walls.add( new Wall(false, 1, 10, 11));

            walls.add( new Wall(false, 2, 0, 12));
            walls.add( new Wall(false, 3, 5, 12));


            walls.add( new Wall(true, 1, 4, 0));

            walls.add( new Wall(true, 3, 3, 1));
            walls.add( new Wall(true, 2, 5, 1));
            walls.add( new Wall(true, 1, 6, 1));
            walls.add( new Wall(true, 2, 7, 1));
            walls.add( new Wall(true, 3, 10, 1));
            walls.add( new Wall(true, 1, 11, 1));

            walls.add( new Wall(true, 1, 4, 2));

            walls.add( new Wall(true, 3, 11, 3));
            walls.add( new Wall(true, 3, 8, 3));

            walls.add( new Wall(true, 4, 6, 4));
            walls.add( new Wall(true, 2, 9, 4));

            walls.add( new Wall(true, 2, 1, 5));
            walls.add( new Wall(true, 2, 3, 5));
            walls.add( new Wall(true, 2, 4, 5));
            walls.add( new Wall(true, 2, 5, 5));
            walls.add( new Wall(true, 1, 8, 5));
            walls.add( new Wall(true, 3, 10, 5));


            walls.add( new Wall(true, 2, 12, 6));
            walls.add( new Wall(true, 1, 7, 6));

            walls.add( new Wall(true, 6, 8, 7));
            walls.add( new Wall(true, 4, 11, 7));

            walls.add( new Wall(true, 2, 6, 9));
            walls.add( new Wall(true, 2, 3, 8));
            walls.add( new Wall(true, 1, 4, 8));
            walls.add( new Wall(true, 1, 7, 8));


            walls.add( new Wall(true, 1, 12, 10));

            walls.add( new Wall(true, 1, 10, 10));

            walls.add( new Wall(true, 1, 4, 10));
            walls.add( new Wall(true, 2, 5, 10));
            walls.add( new Wall(true, 1, 7, 10));

            walls.add( new Wall(true, 1, 3, 11));

            walls.add( new Wall(true, 1, 4, 12));
            walls.add( new Wall(true, 1, 11, 12));
            walls.add( new Wall(true, 1, 12, 12));
        }

        public void AddHoles()
        {
            holes = new ArrayList<Hole>();

            holes.add( new Hole(4,0));
            holes.add( new Hole(8,0));
            holes.add( new Hole(11,0));

            holes.add( new Hole(1,1));
            holes.add( new Hole(5,1));
            holes.add( new Hole(8,1));

            holes.add( new Hole(0,2));
            holes.add( new Hole(3,2));
            holes.add( new Hole(7,2));
            holes.add( new Hole(12,2));

            holes.add( new Hole(2,3));
            holes.add( new Hole(5,3));
            holes.add( new Hole(10,3));
            holes.add( new Hole(11,3));

            holes.add( new Hole(3,4));
            holes.add( new Hole(8,4));

            holes.add( new Hole(0,5));
            holes.add( new Hole(12,5));

            holes.add( new Hole(4,6));
            holes.add( new Hole(7,6));

            holes.add( new Hole(2,7));
            holes.add( new Hole(9,7));
            holes.add( new Hole(10,7));
            holes.add( new Hole(11,7));

            holes.add( new Hole(4,8));
            holes.add( new Hole(5,8));
            holes.add( new Hole(6,8));
            holes.add( new Hole(8,8));

            holes.add( new Hole(8,9));

            holes.add( new Hole(2,10));
            holes.add( new Hole(3,10));
            holes.add( new Hole(4,10));
            holes.add( new Hole(10,10));

            holes.add( new Hole(7,11));
            holes.add( new Hole(12, 11));

            holes.add( new Hole(0,12));
            holes.add( new Hole(8,12));
            holes.add( new Hole(10,12));
            holes.add( new Hole(11,12));
        }

    }
    class Physics{

        float dampening = 0.25f;

        Physics(){
        }

        public PVector sphereBoxCollision(Ball ball, Wall wall)
        {
            float xDistance = ball.pos.x - (wall.pos.x + wall.size.x / 2.0f) + 1;
            float zDistance = ball.pos.z - (wall.pos.z + wall.size.z / 2.0f) + 1;

            // 1---2
            // |   |
            // 3---4

            PVector cornVecOne;
            PVector cornVecTwo;
            PVector cornVecThree;
            PVector cornVecFour;

            if (wall.vert)
            {

                cornVecOne = new PVector (ball.pos.x - (wall.pos.x - wall.size.x / 2.0f), 0,
                        ball.pos.z - (wall.pos.z + wall.size.z));

                cornVecTwo = new PVector (ball.pos.x - (wall.pos.x + wall.size.x / 2.0f), 0,
                        ball.pos.z - (wall.pos.z + wall.size.z));

                cornVecThree = new PVector (ball.pos.x - (wall.pos.x - wall.size.x / 2.0f), 0,
                        ball.pos.z - (wall.pos.z));

                cornVecFour = new PVector (ball.pos.x - (wall.pos.x + wall.size.x / 2.0f), 0,
                        ball.pos.z - (wall.pos.z));
            }
            else
            {
                cornVecOne = new PVector (ball.pos.x - (wall.pos.x), 0,
                        ball.pos.z - (wall.pos.z + wall.size.z / 2.0f));

                cornVecTwo = new PVector (ball.pos.x - (wall.pos.x + wall.size.x), 0,
                        ball.pos.z - (wall.pos.z + wall.size.z / 2.0f));

                cornVecThree = new PVector (ball.pos.x - (wall.pos.x), 0,
                        ball.pos.z - (wall.pos.z - wall.size.z / 2.0f));

                cornVecFour = new PVector (ball.pos.x - (wall.pos.x + wall.size.x), 0,
                        ball.pos.z - (wall.pos.z - wall.size.z / 2.0f));
            }

            float magnitudeOne = sqrt(sq(cornVecOne.x) + sq(cornVecOne.z));
            float magnitudeTwo = sqrt(sq(cornVecTwo.x) + sq(cornVecTwo.z));
            float magnitudeThree = sqrt(sq(cornVecThree.x) + sq(cornVecThree.z));
            float magnitudeFour = sqrt(sq(cornVecFour.x) + sq(cornVecFour.z));

            if (magnitudeOne < ball.rad)
            {
                ball.vel.x += -ball.vel.x * dampening;
                ball.vel.z += -ball.vel.z * dampening;
                return new PVector((cornVecOne.x / magnitudeOne) * 0.2f, 0, (cornVecOne.z / magnitudeOne) * 0.2f);
            }
            if (magnitudeTwo < ball.rad)
            {
                ball.vel.x += -ball.vel.x * dampening;
                ball.vel.z += -ball.vel.z * dampening;
                return new PVector(cornVecTwo.x / magnitudeTwo * 0.2f, 0, cornVecTwo.z / magnitudeTwo * 0.2f);
            }
            if (magnitudeThree < ball.rad)
            {
                ball.vel.x += -ball.vel.x * dampening;
                ball.vel.z += -ball.vel.z * dampening;
                return new PVector(cornVecThree.x / magnitudeThree * 0.2f, 0, cornVecThree.z / magnitudeThree * 0.2f);
            }
            if (magnitudeFour < ball.rad)
            {
                ball.vel.x += -ball.vel.x * dampening;
                ball.vel.z += -ball.vel.z * dampening;
                return new PVector(cornVecFour.x / magnitudeFour * 0.2f, 0, cornVecFour.z / magnitudeFour * 0.2f);
            }


            if (abs(xDistance) < (wall.size.x / 2.0f) + ball.rad &&
                    ball.pos.z >= wall.pos.z &&
                    ball.pos.z <= wall.pos.z + wall.size.z)
            {
                ball.vel.x = -ball.vel.x * dampening;

                PVector p;

                if (xDistance < 0)
                {
                    p = new PVector(-((wall.size.x / 2.0f) + ball.rad - abs(xDistance)), 0, 0);
                }
                else p = new PVector((wall.size.x / 2.0f) + ball.rad - abs(xDistance), 0, 0);

                return p;
            }
            else if (abs(zDistance) < (wall.size.z / 2.0f) + ball.rad &&
                    ball.pos.x >= wall.pos.x &&
                    ball.pos.x <= wall.pos.x + wall.size.x)
            {
                ball.vel.z = -ball.vel.z * dampening;

                PVector p;

                if (zDistance < 0)
                {
                    p = new PVector(0, 0, -((wall.size.z / 2.0f) + ball.rad - abs(zDistance)));
                }
                else p = new PVector(0, 0, ((wall.size.z / 2.0f) + ball.rad) - abs(zDistance));

                return p;
            }


            return new PVector(0, 0, 0);
        }

        public boolean sphereCircleCollision(Ball ball, Hole hole)
        {
            float distance = sqrt( sq(ball.pos.x - hole.pos.x) + sq(ball.pos.z - hole.pos.z));

            return distance < hole.rad;
        }
    }
    class Wall {

        boolean vert;
        float len;
        PVector col;
        PVector size;

        private PVector pos;

        Wall(boolean v, float l, int gridX, int gridY) {
            vert = v;
            len = l;
            col = new PVector(255, 255, 255);
            pos = new PVector(gridX * board.cellSize - (board.size.x / 2.0f), 0, gridY * board.cellSize - (board.size.z / 2.0f));
            if (vert) size = new PVector(board.cellBuffer, 20, (float) len * board.cellSize);
            else size = new PVector((float) len * board.cellSize, 20, board.cellBuffer);
        }

        public void Draw() {
            pushMatrix();
            fill(col.x, col.y, col.z);
            if (vert) {
                translate(pos.x, pos.y, pos.z + (size.z / 2.0f));
                box(size.x, 20, size.z);
            } else {
                translate(pos.x + (size.x / 2.0f), pos.y, pos.z);
                box(size.x, 20, size.z);
            }
            popMatrix();
        }
    }
}
