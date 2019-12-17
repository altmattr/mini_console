package studentwork;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Screen;
import pfx.PVector;
import java.awt.MouseInfo;

public class FarmerBill extends pfx.FXApp {

    public String name(){return "Farmer Bill";}
    public String description(){return "Bounce a ball on Bill's head";}

    //variables
    float circleX = 0;
    float circleY = 0;
    float xSpeed = random(1.15f, 1.2f);
    float ySpeed = random(0.2f, 1.2f);
    float xSpeed2 = random(1.15f, 1.2f);
    float ySpeed2 = random(0.2f, 1.2f);
    float gravity = 0.075f;

    int horseLegOneX = 500;
    int horseLegTwoX = 515;

    int smokeOpacity = 255;
    float smokeSize = 5;
    float smokeLocation = 343f;

    public FarmerBill(GraphicsContext g) {
        super(g);
    }

    public void settings(){
        size(640, 480);
    }

    public void setup(){
        background(100);
        //rectMode(CENTER);
    }

    public void draw(){
        background(82, 202, 255);
        //ground
        fill(0, 255, 80);
        rect(0, 400, 640, 280);
        //horse
            //body
            noStroke();
            fill(144, 91, 34);
            ellipse(480, 320, 120, 50);
            //legs
            stroke(144, 91, 34);
            strokeWeight(5);
            line(500, 340, horseLegOneX, 400);
            line(515, 335, horseLegTwoX, 400);
            line(440, 338, 440, 400);
            line(428, 330, 428, 400);
            noStroke();
            strokeWeight(1);
            //neck
            rect(420, 280, 20, 40);
            //ear
            ellipse(420, 250, 5, 35);
            //head
            ellipse(410, 270, 70, 35);
            //ear
            ellipse(425, 250, 5, 35);
            stroke(0);
            //outer eye
            fill(255);
            ellipse(410, 265, 15, 10);
            //inner eye
            fill(0);
            ellipse(410, 265, 5, 5);
            //tail
            line(537, 315, 547, 315);
            line(537, 317, 547, 317);
            line(537, 319, 547, 319);
            line(547, 316, 552, 370);
            line(547, 318, 550, 370);
            line(547, 320, 554, 370);

            //bill

        //head
        fill(255);
        ellipse(mouseX, 350, 40, 50);
        //body
        fill(0, 80, 250);
        rect(mouseX-7, 375, 15, 50);
        fill(0);
        line(mouseX, 400, mouseX, 425);
        //arms
        fill(200, 0, 0);
        rect(mouseX-12, 375, 5, 20);
        rect(mouseX+8, 375, 5, 20);
        //outer eyes
        fill(255);
        ellipse(mouseX-8, 345, 13, 13);
        ellipse(mouseX+8, 345, 13, 13);
        //inner eyes
        fill(0);
        ellipse(mouseX-8, 345, 8, 8);
        ellipse(mouseX+8, 345, 8, 8);
        //hat
        fill(255, 247, 0);
        rect(mouseX-25, 325, 50, 5);
        rect(mouseX-10, 308, 20, 20);

        //horse kicking
        if (mouseX > 500) {
            horseLegOneX = horseLegOneX + 10;
        }
        else if (mouseX < 500) {
            horseLegOneX = 500;
        }

        if (mouseX > 515) {
            horseLegTwoX = horseLegTwoX + 10;
        }
        else if (mouseX < 515) {
            horseLegTwoX = 515;
        }

        //ball
        fill(255, 0, 0);
        ellipse(circleX, circleY, 30, 30);
        circleX = circleX + xSpeed;
        circleY = circleY +ySpeed;

        if (circleX > width) {
            xSpeed = -1;
        }
        if (circleX < 0) {
            xSpeed = 1;
        }
        //haystack
        fill(240, 213, 44);
        rect(30, 370, 90, 90);

        //bounce on the floor
        if (circleX > mouseX-50 && circleX < mouseX+50  && circleY > 315 && circleY < 320) {
            ySpeed = ySpeed *-0.95f;
        }

        //gravity
        ySpeed = ySpeed + gravity;
        circleY = circleY + ySpeed;

        if (circleY > height-50) {
            ySpeed = ySpeed * -0.95f;
            circleY = height-50;

        }
        //pipe in mouth
        fill(255);
        ellipse(mouseX+2, 360, 5, 5);
        fill(0);
        rect(mouseX+20, 365, 10, 5);
        line(mouseX+3, 360, mouseX+20, 365);

//        //smoke
//        fill(170, 170, 170, smokeOpacity);
//        noStroke();
//        ellipse(mouseX+25, smokeLocation+20, smokeSize, smokeSize);
//        ellipse(mouseX+28, smokeLocation+12, smokeSize, smokeSize);
//        ellipse(mouseX+31, smokeLocation, smokeSize, smokeSize);
//
//        smokeLocation = smokeLocation -0.5f;
//        smokeOpacity = smokeOpacity - 1;
//        if (smokeSize < 70) {
//            smokeSize = smokeSize + 1;
//        }
//        if (smokeLocation < 240) {
//            smokeLocation = smokeLocation +103;
//
//        }
//
//
//        if (smokeOpacity < 0) {
//            smokeOpacity = smokeOpacity + 200;
//
//        }
    }
}