package studentwork;
import processing.core.*;
import processing.data.*;


public class Grapher extends mqapp.MQApp {




    public String name(){return "Grapher";}
    public String author() {return "created by Sepehr Torfeh Nejad";}
    public String description(){return "Make your own interactive graph";}

    final int N_PARTITIONS = 10;
    int current;
    int mxIndex;
    int mnIndex;
    int draggingPoint;
    float sum;
    float[] totalDistPoint;
    PVector[] points;
    PVector[] gridLocations;
    boolean nodePressed;

    public void setup() {
        size(displayWidth, displayHeight);
        current = 0;
        sum = 0;
        draggingPoint = 0;
        nodePressed = false;

        points = new PVector[100];
        for (int i=0; i < points.length; i++) {
            points[i] = new PVector(-10, -10);
        }

        gridLocations = new PVector[(N_PARTITIONS+1)*(N_PARTITIONS+1)];
        for (int i=0; i < gridLocations.length; i++) {
            gridLocations[i] = new PVector(0, 0);
        }
        gridLocationsSetup();

        totalDistPoint = new float[100];
    }

    public void draw() {
        background(255);

        fill(255);
        stroke(200);
        drawGrid();

        stroke(0);
        drawEdges();

        noStroke();
        totalDist();

        drawPoints();
    }

    public void mouseClicked() {
        PVector mouse = new PVector(mouseX, mouseY);

        //getting the location of the point when the user clickes

        points[current].x = minDis(mouse, gridLocations).x;
        points[current].y = minDis(mouse, gridLocations).y;

        current++;

        if (current > points.length-1) {
            current = 0;
        }
    }

    public void mousePressed() {
        if (current > 0) {
            //it finds out if we want to drag a node, if so which node it is
            for (int i = 0; i < current; i++) {
                if (((mouseX >= ((points[i].x)-5)) && (mouseX <= ((points[i].x)+5))) && ((mouseY >= ((points[i].y)-5)) && (mouseY <= ((points[i].y)+5)))) {
                    draggingPoint = i;
                    nodePressed = true;
                }
            }
        }
    }

    public void mouseDragged() {
        if (nodePressed) {
            //the node that we are dragging
            points[draggingPoint].x = mouseX;
            points[draggingPoint].y = mouseY;
        }
    }

    public void mouseReleased() {
        //after we dragged the node it finds the nearest intersection and puts it there
        points[draggingPoint].x = minDis(points[draggingPoint], gridLocations).x;
        points[draggingPoint].y = minDis(points[draggingPoint], gridLocations).y;
        nodePressed = false;
    }

    public void keyPressed() {
        //movement of nodes and edges
        if ( key == CODED ) {

            if ( keyCode == UP) {

                for (int i=0; i <= current; i++) {
                    points[i].y -= height/N_PARTITIONS;
                }
            }
            if ( keyCode == DOWN) {

                for (int i=0; i <= current; i++) {
                    points[i].y += height/N_PARTITIONS;
                }
            }
            if ( keyCode == LEFT) {

                for (int i=0; i <= current; i++) {
                    points[i].x -= width/N_PARTITIONS;
                }
            }
            if ( keyCode == RIGHT) {

                for (int i=0; i <= current; i++) {
                    points[i].x += width/N_PARTITIONS;
                }
            }
        }
    }

    public void drawGrid() { //draws the grid with squares
        for (float j= 0; j <= height; j += height/(float)N_PARTITIONS) {
            for (float i= 0; i <= width; i += width/(float)N_PARTITIONS) {
                rect(i, j, width/(float)N_PARTITIONS, height/(float)N_PARTITIONS);
            }
        }
    }

    public void drawPoints() { //draws the nodes
        if (current > 0) {
            for (int i=0; i < current; i++) {
                fillSetup();
                //setting the colour of the node based on the distance
                if (i == mnIndex) {
                    fill(255, 0, 0);
                } else if (i == mxIndex) {
                    fill(0, 0, 255);
                } else {
                    fill(0);
                }
                ellipse(points[i].x, points[i].y, 10, 10);
            }
            //reseting the totalDistpoint
            for (int i = 0; i < totalDistPoint.length; i++) {
                totalDistPoint[i] = 0;
            }
        }
    }

    public void drawEdges() { //draws the edges
        if (current > 0) {
            for (int i=0; i < current-1; i++) {
                line(points[i].x, points[i].y, points[i+1].x, points[i+1].y);
            }
            line(points[0].x, points[0].y, points[current-1].x, points[current-1].y);
        }
    }

    public void gridLocationsSetup() {// store all the location of the intersections of the grid
        int rowCounterY = 0;
        int k=0;

        for (float j= 0; j <= height; j += height/(float)N_PARTITIONS) {
            for (int a = 0; a <= N_PARTITIONS; a++) {
                gridLocations[(a+rowCounterY)+(N_PARTITIONS*rowCounterY)].y = j;
            }
            rowCounterY++;
            for (float i= 0; i <= width; i += width/(float)N_PARTITIONS) {
                gridLocations[k].x = i;
                k++;
            }
        }
    }

    public float distance(PVector x1, PVector x2) {//finds the distance
        float X = x1.x - x2.x;
        float Y = x1.y - x2.y;
        return pow( pow(X, 2)+ pow(Y, 2), 0.5f);
    }

    public PVector minDis(PVector mouse, PVector[] gridLocations) {
        //finds the smallest distance from current point to the nearest grid intersection location

        PVector res = new PVector(gridLocations[0].x, gridLocations[0].y);

        for (int i = 0; i < gridLocations.length; i++) {
            if ( distance(mouse, gridLocations[i]) < distance(mouse, res)) {
                res = gridLocations[i];
            }
        }
        return res;
    }

    public void totalDist() {
        // stores all the cumulative distance for each point
        if (current > 0) {
            for (int i=0; i < current; i++) {
                for (int j=0; j < current; j++) {
                    float total = distance(points[i], points[j]);
                    sum = sum + total;
                }
                totalDistPoint[i] = sum;
                sum = 0;
            }
        }
    }

    public void fillSetup() {
        //finds the index number which has the maxdistance and mindistance in it
        mxIndex = 0;
        mnIndex = 0;

        if (current > 0) {
            float[] cop = new float[current];
            for (int j=0; j < cop.length; j++) {
                cop[j] = totalDistPoint[j];
            }
            float mn = min(cop);
            float mx = max(cop);

            for (int k = cop.length-1; k >= 0; k--) {
                if (cop[k] == mn) {
                    mnIndex = k;
                }
                if (cop[k] == mx) {
                    mxIndex = k;
                }
            }
        }
    }

    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "studentwork.Grapher" };
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }
}