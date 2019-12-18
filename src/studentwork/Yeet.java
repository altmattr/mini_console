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

public class Yeet extends mqapp.MQApp {

    public String name() {
        return "Yeet";
    }


// attempt 3
//with projectile motion

   // mountain variables
    float mtBase = 432;
    float mtHeight = 288;



    //cloud variables
    float circleX = -200;
    float lc = 180; //lower cloud
    float uc =130; //upper cloud
    float diac = 24; //diamater cloud


    //ball variables
    float velocityX =random(2, 6);
    float velocityY = 0.5f;
    float gravity = 0.2f;
    float x = 0;
    float y =100;

    public void setup() {
        size(640, 480);

    }
    public void draw() {
        background(0, 200, 255);



        //mountains
//    beginShape(TRIANGLE);
        fill(0, 255, 0);
        triangle(128, mtHeight, 0, mtBase, 128, mtBase);
        fill(0, 0, 255);
        triangle(128, mtHeight, 128, mtBase, 256, mtBase);
        fill(0, 255, 0);
        triangle(320, mtHeight, 192, mtBase, 320, mtBase);
        fill(0, 0, 255);
        triangle(320, mtHeight, 320, mtBase, 448, mtBase);
        fill(0, 255, 0);
        triangle(512, mtHeight, 384, mtBase, 567, mtBase);
        fill(0, 0, 255);
        triangle(512, mtHeight, 512, mtBase, 640, mtBase);


        //clouds
        noStroke();
        fill(255);
        ellipse(circleX, lc-10, 40, diac);
        ellipse(circleX+20, lc, 50, diac);
        ellipse(circleX-20, lc, 50, diac);
        ellipse(circleX, lc+10, 40, diac);
        //upper cloud
        ellipse(circleX+60, uc-10, 40, diac);
        ellipse(circleX+80, uc, 50, diac);
        ellipse(circleX+40, uc, 50, diac);
        ellipse(circleX+60, uc+10, 40, diac);

        circleX = circleX + 0.5f;
        if (circleX == width+60) {
            circleX=-100;
        }



        //grass
        fill(0, 200, 0);
        noStroke();
        rect(0, 480, 1600, 100);

        ellipseMode(CENTER);
        rectMode(CENTER);

        //bill
        stroke(200, 0, 200);
        strokeWeight(4);
        noFill();
        ellipse(mouseX, 380, 40, 40);
        line(mouseX, 400, mouseX, 440);
        line(mouseX, 440, mouseX+10, 460); //right leg
        line(mouseX, 440, mouseX-10, 460); //left leg
        line(mouseX, 400, mouseX+10, 420); //right arm
        line(mouseX, 400, mouseX-10, 420); //left arm
        fill(100, 100, 100);

        ellipse(mouseX, 360, 50, 20);
        rect(mouseX, 350, 30, 20);

        noStroke();
        fill(255, 0, 0);
        ellipse(x, y, 30, 30);
        x = x + velocityX;
        y = y + velocityY;

        velocityY = velocityY + gravity;

        if (x > width-15) { //bounce RHS
            velocityX = -(velocityX);
        }
        if (x < 15) {//bounce LHS
            velocityX = (velocityX);
        }

        if (y > height-30) {  // bounce off floor
            velocityY = -(velocityY)+gravity;
        }
        if (y > height) {
            x =0;
            y = 100;
        }

        if (x > mouseX-20  && x < mouseX +20 && y >350) {
            velocityY = -(velocityY)+gravity ;
        }
        velocityY = velocityY+ gravity;
    }

public void keyPressed(){
  if (key == 'd'){
    System.out.println("disposed");
    dispose();
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "studentwork.BlackHole" };
    runSketch(appletArgs, new BlackHole());
  }
}

static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "studentwork.Yeet" };
    runSketch(appletArgs, new Yeet());
  }

}
