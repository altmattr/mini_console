package examples;// https://processing.org/examples/bouncingball.html

import processing.core.*;

//import javafx.scene.canvas.GraphicsContext;

public class BouncingBall extends mqapp.MQApp {
    PVector location;
    PVector velocity;
    PVector gravity;

    public String name(){return "Bouncing Ball - TEST";}
    public String description(){return "Demonstration of using vectors to control motion of body.";}

    public void settings(){
        size(1200,800);
        location = new PVector(100f,100f); // TODO: deal with these f's that should not be here.
        velocity = new PVector(1.5f, 2.1f);
        gravity = new PVector(0f, 0.2f);
    }

    public void draw(){
        background(0);
        location.add(velocity);
        velocity.add(gravity);

        if (location.x > width || location.x < 0){
            velocity.x = velocity.x *-1;
        }

        if (location.y > height){
            velocity.y = velocity.y*-0.95f;
            location.y = height;
        }

        stroke(255);
        strokeWeight(2);
        fill(127);
        rect(1190,790,10,10);
//        System.out.println(width);
//        System.out.println(height);
        ellipse(location.x, location.y, 48, 48);
    }
}
