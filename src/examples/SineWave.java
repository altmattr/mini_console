package examples;// https://processing.org/examples/sinewave.html

import javafx.scene.canvas.GraphicsContext;

public class SineWave extends pfx.FXApp {

    int xspacing = 16;   // How far apart should each horizontal location be spaced
    int w;              // Width of entire wave

    double theta = 0.0;  // Start angle at 0
    double amplitude = 75.0;  // Height of wave
    double period = 500.0;  // How many pixels before the wave repeats
    double dx;  // Value for incrementing X, a function of period and xspacing
    double[] yvalues;  // Using an array to store height values for the wave

    public SineWave(GraphicsContext g) {
        super(g);
    }
    public String name(){return "Sine Wave by Daniel Shiffman (https://processing.org/examples/sinewave.html)";}
    public String description(){return "Render a simple sine wave.";}

    public void settings(){
        size(640, 360);
    }

    public void setup() {
        w = width+16;
        dx = (Math.PI*2 / period) * xspacing;
        yvalues = new double[w/xspacing];
    }

    public void draw() {
        background(0);
        calcWave();
        renderWave();
    }

    private void calcWave() {
        // Increment theta (try different values for 'angular velocity' here
        theta += 0.02;

        // For every x value, calculate a y value with sine function
        double x = theta;
        for (int i = 0; i < yvalues.length; i++) {
            yvalues[i] = sin((float)x)*amplitude;
            x+=dx;
        }
    }

    private void renderWave() {
        noStroke();
        fill(255);
        // A simple way to draw the wave with an ellipse at each location
        for (int x = 0; x < yvalues.length; x++) {
            ellipse(x*xspacing, (float)(height/2+yvalues[x]), 16, 16);
        }
    }
}
