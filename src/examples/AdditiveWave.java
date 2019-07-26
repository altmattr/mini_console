// https://processing.org/examples/additivewave.html

package examples;

import javafx.scene.canvas.GraphicsContext;

public class AdditiveWave extends pfx.FXApp {
    public AdditiveWave(GraphicsContext g) {
        super(g);
    }

    public String name(){return "Additive Wave - TEST";}
    public String description(){return "Create a more complex wave by adding two waves together.";}

    int xspacing = 8;   // How far apart should each horizontal location be spaced
    int w;              // Width of entire wave
    int maxwaves = 4;   // total # of waves to add together

    float theta = 0.0f;
    float[] amplitude = new float[maxwaves];   // Height of wave
    float[] dx = new float[maxwaves];          // Value for incrementing X, to be calculated as a function of period and xspacing
    float[] yvalues;                           // Using an array to store height values for the wave (not entirely necessary)

    public void settings() {
        size(640, 360);
    }

    public void setup() {

        //frameRate(30);
        //colorMode(RGB, 255, 255, 255, 100);
        w = width + 16;

        for (int i = 0; i < maxwaves; i++) {
            amplitude[i] = random(10, 30);
            float period = random(100, 300); // How many pixels before the wave repeats
            dx[i] = ((float) Math.PI * 2 / period) * xspacing;
        }

        yvalues = new float[w / xspacing];
    }

    public void draw() {
        background(0);
        calcWave();
        renderWave();
    }

    private void calcWave() {
        // Increment theta (try different values for 'angular velocity' here
        theta += 0.02;

        // Set all height values to zero
        for (int i = 0; i < yvalues.length; i++) {
            yvalues[i] = 0;
        }

        // Accumulate wave height values
        for (int j = 0; j < maxwaves; j++) {
            float x = theta;
            for (int i = 0; i < yvalues.length; i++) {
                // Every other wave is cosine instead of sine
                if (j % 2 == 0) yvalues[i] += sin(x) * amplitude[j];
                else yvalues[i] += cos(x) * amplitude[j];
                x += dx[j];
            }
        }
    }

    private void renderWave() {
        // A simple way to draw the wave with an ellipse at each location
        noStroke();
        fill(255, 50);
        ellipseMode(CENTER);
        for (int x = 0; x < yvalues.length; x++) {
            ellipse(x * xspacing, height / 2 + yvalues[x], 16, 16);
        }
    }

}
