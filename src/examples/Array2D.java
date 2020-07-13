// https://processing.org/examples/array2d.html

package examples;

import processing.core.*;

public class Array2D extends mqapp.MQApp {

    public String name(){return "2D Grid Array  - TEST";}
    public String description(){return "Demonstrates the syntax for creating a two-dimensional (2D) array. Values in a 2D array are accessed through\ntwo index values. 2D arrays are useful for storing images. In this example, each dot is colored in relation to its\ndistance from the center of the image.";}

    float[][] distances;
    float maxDistance;
    int spacer;

    public void settings(){
        size(640, 360);
    }

    public void setup() {
        maxDistance = dist(width/2, height/2, width, height);
        distances = new float[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float distance = dist(width/2, height/2, x, y);
                distances[x][y] = distance/maxDistance * 255;
            }
        }
        spacer = 10;
        strokeWeight(6);
        //noLoop();  // Run once and stop
    }

    public void draw() {
        background(0);
        // This embedded loop skips over values in the arrays based on
        // the spacer variable, so there are more values in the array
        // than are drawn here. Change the value of the spacer variable
        // to change the density of the points
        for (int y = 0; y < height; y += spacer) {
            for (int x = 0; x < width; x += spacer) {
                stroke((int)distances[x][y]);
                point(x + spacer/2, y + spacer/2);
            }
        }
    }
}
