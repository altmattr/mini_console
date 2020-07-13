package examples;

import processing.core.*;

public class ScalingChecker extends mqapp.MQApp {

    public String name(){return "Scaling Checker - Matt";}
    public String description(){return "A new age test pattern";}

    public void settings(){
        size(960,555);
    }

    public void draw(){
        background(0);
        fill(100);
        stroke(255);
        rect(5,5,950,545);
    }
}
