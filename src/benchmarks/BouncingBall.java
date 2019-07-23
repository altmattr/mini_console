package benchmarks;

import pfx.*;

import javafx.scene.canvas.GraphicsContext;

public class BouncingBall extends pfx.SizedFXApp {
    PVector[] location;
    PVector[] velocity;
    PVector[] gravity;

    int balls;

    public BouncingBall(GraphicsContext g) {
        super(g);
    }
    public String name(){return "Bouncing_Balls_30";}
    public String description(){return "Each step is 30 balls.";}

    public void settings(){
        size(1200,800);
        balls = SIZE*100;
        location = new PVector[balls];
        velocity = new PVector[balls];
        gravity = new PVector[balls];
        for(int i = 0; i < balls; i++) {
            location[i] = new PVector(100f + random(0,4), 100f+random(0,4)); // TODO: deal with these f's that should not be here
            velocity[i] = new PVector(1.5f+random(0,1), 2.1f+random(0,1));
            gravity[i] = new PVector(0f, 0.2f);
        }
    }

    public void draw(){
        background(0);
        for(int i = 0; i < balls; i++) {
            location[i].add(velocity[i]);
            velocity[i].add(gravity[i]);

            if (location[i].x > width || location[i].x < 0) {
                velocity[i].x = velocity[i].x * -1;
            }

            if (location[i].y > height) {
                velocity[i].y = velocity[i].y * -0.95f;
                location[i].y = height;
            }

            stroke(255);
            strokeWeight(2);
            fill(127);
            ellipse(location[i].x, location[i].y, 48, 48);
        }
    }
}
