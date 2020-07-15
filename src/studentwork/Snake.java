package studentwork;

import processing.core.*;
import java.util.*;

public class Snake extends mqapp.MQApp {

    public String name() {
        return "Snake";
    }

    public String author() {
        return "Andrew Kefala";
    }

    public String description() {
        return "No need for a Nokia";
    }

    SnakeItself snake;
    int grid;
    int count;
    PVector food;
    int score = 0;
    int t;
    int dif = 9;
    public PFont uiFont, largeFont;

    public void setup() {
        size(displayWidth, displayHeight);
        grid = height/30;
        snake = new SnakeItself();
        snake.locCheck();

        uiFont = loadFont("shared/Avenir-LightOblique-28.vlw");
        largeFont = loadFont("shared/HiraMaruPro-W4-60.vlw");

    }


    public void draw() {

        count++;
        count = count % (60 / (dif + 1));

        if (count == 0) {
            background(40);

            snake.update();
            snake.show();

            if (snake.eat(snake.food)) {
                snake.locCheck();
                score++;
            }

            fill(250, 0, 0);
            rect(snake.food.x, snake.food.y, grid, grid);

            snake.deathTail();
            snake.deathEdge();

            if (snake.deathTail || snake.deathEdge) {
                score  *= dif;
                background(20);
                fill(200, 0, 0);
                textFont(largeFont);
                textAlign(CENTER, CENTER);
                text("GAME OVER!", width / 2, height / 2);
                textFont(uiFont);
                text("score", width / 2, 11 * height / 16);
                text(score, width / 2, 3 * height / 4);
                text("press space to play again", width/2, 13*height/16);
                noLoop();
            }
        }
    }

    public void keyPressed() {
        if (keyCode == UP || key == 'w' || key == 'W') {
            if (snake.ySpeed != 1) {
                snake.dir(0, -1);
            }
        }
        if (keyCode == DOWN || key == 's' || key == 'S') {
            if (snake.ySpeed != -1) {
                snake.dir(0, 1);
            }
        }
        if (keyCode == RIGHT || key == 'd' || key == 'D') {
            if (snake.xSpeed != -1) {
                snake.dir(1, 0);
            }
        }
        if (keyCode == LEFT || key == 'a' || key == 'A') {
            if (snake.xSpeed != 1) {
                snake.dir(-1, 0);
            }
        }
        if (key == ' ' || keyCode == ENTER) {
            snake.deathTail = false;
            snake.deathEdge = false;
            loop();
            score = 0;
        }
        if (key == 'c' || keyCode == 'C') {
            snake.total++;
            score = 0;
        }
    }

    class SnakeItself {
        float x = floor(random(width / grid)) * grid;
        float y = floor(random(height / grid)) * grid;
        float xSpeed = 0;
        float ySpeed = 0;
        int total = 0;
        ArrayList<PVector> tail = new ArrayList<PVector>();
        boolean deathTail;
        boolean deathEdge;
        PVector food = new PVector();


        SnakeItself() {
        }

        void dir(float x, float y) {
            xSpeed = x;
            ySpeed = y;
        }


        boolean eat(PVector pos) {
            float d = dist(x, y, pos.x, pos.y);
            if (d < 1) {
                total++;
                return true;
            } else {
                return false;
            }
        }

        void deathTail() {
            for (int i = 0; i < tail.size(); i++) {
                PVector pos = tail.get(i);
                float d = dist(x, y, pos.x, pos.y);
                if (d < 1) {
                    tail.clear();
                    total = 0;
                    x = floor(random(width / grid)) * grid;
                    y = floor(random(height / grid)) * grid;
                    xSpeed = 0;
                    ySpeed = 0;
                    deathTail = true;
                }
            }
        }

        void deathEdge() {
            if (y < 0 || y > height - grid || x < 0 || x > width - grid) {
                total = 0;
                tail.clear();
                x = floor(random(width / grid)) * grid;
                y = floor(random(height / grid)) * grid;
                xSpeed = 0;
                ySpeed = 0;
                deathEdge = true;
            }
        }
        void update() {
            if (total > 0) {
                if (total == tail.size() && ! tail.isEmpty()) {
                    tail.remove(0);
                }
                tail.add(new PVector(x, y));
            }

            x = x + xSpeed * grid;
            y = y + ySpeed * grid;
        }

        void show() {
            fill(255);
            for (PVector v : tail) {
                rect(v.x, v.y, grid, grid);
            }
            rect(x, y, grid, grid);
        }

        PVector locCheck() {
            boolean confirm = false;
            int n;
            int esc = 0;

            while (!confirm) {
                esc++;
                int cols = floor(random(1, width/grid -1)) * grid;
                int rows = floor(random(1, height/grid -1)) * grid;
                n = 0;

                for (int i = 0; i < tail.size(); i++) {
                    PVector pos = tail.get(i);
                    if (((int)pos.x == cols && (int)pos.y == rows) || ((int)x == cols && (int)y == rows)) {
                        n++;
                    }
                }
                if (n == 0) {
                    confirm = true;
                    food.x = cols;
                    food.y = rows;
                }
                if (esc >= 10000) {
                    confirm = true;
                    food.x = cols;
                    food.y = rows;
                }
            }
            return food;
        }
    }
}
