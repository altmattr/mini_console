package studentwork;


import processing.data.*;
import processing.core.*;

public class FarmerBill extends mqapp.MQApp {

    public String name(){return "Farmer Bill";}
    public String author(){return "created by Georgie Curtis";}
    public String description(){return "Bounce a ball on Bill's head";}

    PVector [] ball = new PVector [5];
    PVector [] speed = new PVector[5];
    float gravity = 0.075f;

    int horseLegOneX = 500;
    int horseLegTwoX = 515;

    float smokeOpacity = 255;
    float smokeSize = 5;
    float smokeLocation = 343;

    int b = 50;
    int counter = 0;

    //scaling variables
    float originalWidth = 640;
    float originalHeight = 480;
    float Scale = 1;


    public void setup()
    {
        ball = new PVector[5];
        speed = new PVector[5];
        size(displayWidth, displayHeight);
        frameRate(75);
        start();
    }



    public void draw()
    {


        float ratioX = displayWidth/originalWidth;
        float ratioY = displayHeight/originalHeight;
        if(ratioX <= ratioY){
            Scale = ratioX;
        } else{
            Scale = ratioY;
        }
        scale(Scale);
//translating
        translate(0, 0);


//gradientLoop();
        b = 70;
        for(int i = 0; i < height ; i+=50)
        {
            fill(0, 0, b);
            rect(0, i, width, 50);
            b = b + 20;
        }
        ground();
        horse();
        bill();
        horseKick();
        pipe();
        smoking();
        haystack();
        basket();
        ball();
    }



//functions

    public void start()
    {
//initialising the PVectors
        for(int i = 0; i < ball.length; i++){
            ball[i] = new PVector();
            ball[i].set(random(0,1), 0);
            speed[i] = new PVector();
            speed[i].set(random(1, 1.15f), random(0.2f, 0.5f));
        }
    }


    void horse()
    {
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
    }


    void horseKick()
    {
        if (mouseX > 500) {
            horseLegOneX = horseLegOneX + 10;
        }  else if (mouseX < 500){
            horseLegOneX = 500;
        }

        if (mouseX > 515) {
            horseLegTwoX = horseLegTwoX + 10;
        } else if (mouseX < 515) {
            horseLegTwoX = 515;
        }
    }


    void bill()
    {
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
        rect(mouseX-35, 325, 70, 5);
        rect(mouseX-10, 308, 20, 20);
    }


    void pipe()
    {
        fill(255);
        ellipse(mouseX+2, 360, 5, 5);
        fill(0);
        rect(mouseX+20, 365, 10, 5);
        line(mouseX+3, 360, mouseX+20, 365);
    }


    void smoking()
    {
        fill(170, 170, 170, smokeOpacity);
        noStroke();
        ellipse(mouseX+25, smokeLocation+20, smokeSize, smokeSize);
        ellipse(mouseX+28, smokeLocation+12, smokeSize, smokeSize);
        ellipse(mouseX+31, smokeLocation, smokeSize, smokeSize);

        smokeLocation = smokeLocation -0.5f;
        smokeOpacity = smokeOpacity - 1;

        if (smokeSize < 70) {
            smokeSize = smokeSize +1;
        }

        if (smokeLocation < 240) {
            smokeLocation = smokeLocation +103;
        }

        if (smokeOpacity < 0) {
            smokeOpacity = smokeOpacity + 200;
        }
    }


    void ground()
    {
        fill(0, 255, 80);
        rect(0, 400, 640, 480);
    }


    void ball()
    {
        fill(255, 0, 0, random(200, 255));

        for(int i = 0; i < ball.length; i++){
            ellipse(ball[i].x, ball[i].y, 30, 30);

            ball[i].x = ball[i].x + speed[i].x;
            ball[i].y = ball[i].y +speed[i].y;

            if (ball[i].x > width){
                speed[i].x = -1;
            }
            if (ball[i].y < 0){
                speed[i].x = 1;
            }

            speed[i].y = speed[i].y + gravity;
            ball[i].y = ball[i].y + speed[i].y;

            if (ball[i].y > height-50) {
                speed[i].y = speed[i].y * -0.95f;
                ball[i].y = height-50;
            }

            //ball bouncing on hat
            if (ball[i].x > mouseX-45 && ball[i].x < mouseX+45  && ball[i].y > 315 && ball[i].y < 325){
                speed[i].y = speed[i].y *-0.95f;
                ball[i].y = ball[i].y - 1;
            }

//making the game end when a ball lands on the floor
            if(ball[i].y >= 400 && ball[i].x <= 550){
                counter = 0;
                fill(255, 0, 0);
                rect(0, 0, 640, 480);
                textSize(50);
                fill(0);
                text("GAME OVER", 170, 240);
                textSize(25);
                text("press any key to play again", 150, 300);
                noLoop();
            }

//ball lands in bucket, counter goes up
            if(ball[i].y >= 400 && ball[i].x > 550 && ball[i].x < 640){
                ball[i].set(random(0, 1), 0);
                speed[i].set(random(1, 1.15f), random(0.2f, 0.5f));
                counter = counter + 6;
            }
        }
        textSize(30);
        text("$", 550, 50);
        text(counter, 570, 50);
    }


    public void keyPressed()
    {
        //restarting the game when a key is pressed
        loop();
        for(int i = 0; i <ball.length; i++){
            ball[i].set(random(0, 1), 0);
            speed[i].set(random(1, 1.15f), random(0.2f, 0.5f));
        }
    }


    void haystack()
    {
        fill(240, 213, 44);
        rect(30, 370, 90, 90);
    }


    void basket()
    {
        fill(180, 114, 84);
        rect(550, 360, 90, 90);
    }
}