package studentwork;

import java.util.ArrayList;
import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;




public class Pandemic extends mqapp.MQApp {
	
	public String name(){return "Pandemic";}
  	public String author(){return "Elizabeth Cappellazzo";}
  	public String description(){return "Pandemic simulator";}

	public static final int WIDTH      = 600;
    public static final int HEIGHT     = 600;
    public static final int GRAPH_HEIGHT = 50;
    public static final int SIMS       = 1000; //no. of sims
    public static final int SIM_SIZE   = 6; //size of the sims
    public static final int MAX_MOVE   = 500; //max possible radius that sims can move
    public static final int CONT_AFTER = 240; // 4 days - how long after contact does the sim get sick
    public static final int DEATH_RATE = 10;  //self explanatory
    public static final int ILL_FOR    = 840; // 14 days - how long the sim is sick for before dying or becoming immune
    public static final int TRANS_RATE = 50;  //transmission rate of disease 
    public static final int COUNTER_WIDTH = 200;


    public static final Color SICK    = new Color(255, 153, 90);
    public static final Color DEAD    = new Color(255, 0, 0);
    public static final Color IMMUNE  = new Color(72, 202, 48);
    public static final Color UNKNOWN = new Color(141, 141, 141);
	public int startTimer;
    public Population p;
	public Simulant selected; //the sim that has been clicked on
	public Color[][] graph; //array of colours
	public int graphX;
    public int game_state = 0;      // to determine if the game is running or not
    public int displayTime = 0;    // to display time in end screen

    public void setup() {
	    	//noSmooth();
	        startTimer = millis();
            size(Pandemic.WIDTH+ Pandemic.COUNTER_WIDTH, Pandemic.HEIGHT+Pandemic.GRAPH_HEIGHT);
        	p = new Population();
        	selected = null;
        	graphX = 0;
        	graph = new Color[Pandemic.WIDTH + COUNTER_WIDTH][Pandemic.GRAPH_HEIGHT];
        	for(int x = 0; x < graph.length; x++){
                for(int y = 0; y < graph[x].length; y++){
                    graph[x][y]= new Color(255, 255, 255);
                }
        }
}
	    
	    public void draw() {
            if(game_state == 0) { //start screen
                background(0);
                textAlign(CENTER);
                strokeWeight(3);
                fill(255);
                textSize(20);
                text("PANDEMIC", 200, 100);
                text("PRESS SPACE BAR TO START THE GAME", 200, 150);
                text("PRESS 'b' TO RE-SET THE GAME", 200, 250);
            } else if(game_state == 1) {
                runSimulator();
                timer();
            }  
            if( p.numberSick() == 0 ) {
                // end screen
               endScreen();
            } 
	    	
	    }
	    
	    private Color chooseColour(Simulant s){
            if(s.sick > 0){
                    return Pandemic.SICK;
            } else if (s.sick < 0){
                    return Pandemic.DEAD;
            } else if (s.immune){
                    return Pandemic.IMMUNE;
            } else {
                    return Pandemic.UNKNOWN;
            }
        }

        
        public void mouseClicked()
        {
            if (game_state == 1) {
                Point2D mouse = new Point2D.Float(mouseX, mouseY);
                selected = p.simAtLocation(mouse); //simAtLocation needs to be updated for mouseX mouseY
            }

        }
        public void keyPressed()
        {
        	if(key == CODED){
        	if (keyCode == RIGHT) {
        		selected = p.nextAfter(selected);
        	}
        	else if (keyCode == LEFT) {
        		selected = p.prevBefore(selected);
        	}
        }
            if(key == ' ') {
                game_state = 1;
            }
            if (key == 'b' || key == 'B') {
                p = new Population();
                selected = null;
                startTimer = millis();
                graphX = 0;
                graph = new Color[Pandemic.WIDTH + COUNTER_WIDTH][Pandemic.GRAPH_HEIGHT];

                for(int x = 0; x < graph.length; x++){
                    for(int y = 0; y < graph[x].length; y++){
                        graph[x][y]= new Color(255, 255, 255);
                    }
            }
            }

        }
        public void runSimulator() {
           p.update(); //this updates the simulation
                drawCounters();
                drawLegend();
                fill(255,255,255);
                rect(0,0,Pandemic.WIDTH,Pandemic.HEIGHT);

                if (selected != null){ //shows circle of motion around selected sim
                    stroke(94,86,90);
                    noFill();
                    ellipse((int)selected.homeLoc.getX(), (int)selected.homeLoc.getY() , (int)selected.mobility*2, (int)selected.mobility*2);
                    noStroke();
                }

                for(Simulant s: p.sims){ //places sims on screen
                    Color fillCol = chooseColour(s);
                    fill(fillCol.getRed(), fillCol.getGreen(), fillCol.getBlue());
                    ellipse((int)s.loc.getX() - Pandemic.SIM_SIZE/2,(int)s.loc.getY() - Pandemic.SIM_SIZE/2, Pandemic.SIM_SIZE, Pandemic.SIM_SIZE);
                }
                if (p.numberSick() > 0){ //faint purple circle
                    fill(131, 50, 168, 20);
                    int circleRadius = 40;
                    ellipse((int)p.averageXOfSick(), (int)p.averageYOfSick(), circleRadius + p.numberSick()/2, circleRadius + p.numberSick()/2);
                }

                
                fill(255,255,255);
                ArrayList<Simulant> sorted = p.sort();
                for(int y = 0; y < Pandemic.GRAPH_HEIGHT; y++){
                    graph[graphX][y] = chooseColour(sorted.get(y*Pandemic.SIMS/Pandemic.GRAPH_HEIGHT));
                }
                graphX = (graphX + 1) % (Pandemic.WIDTH + COUNTER_WIDTH);

                // draw graph
                for(int x = 0; x < graph.length; x++){
                    for(int y = 0; y < graph[x].length; y++){
                        Color fillCol = graph[x][y];
                        int c = color(fillCol.getRed(), fillCol.getGreen(), fillCol.getBlue());
                        set(x,Pandemic.HEIGHT + Pandemic.GRAPH_HEIGHT - y, c);
                        noStroke();
                    }
                }
                
        }

        public void drawCounters()
        {
            //infected, immune, dead, uninfected, timer
            //uninfected
            fill(200);
            rect(WIDTH, 0, COUNTER_WIDTH, HEIGHT);
            textSize(40);
            textAlign(LEFT);
            fill(0);

            fill(237, 108, 9);
            text("Pandemic", WIDTH + 4, 45);
            fill(0);
            textSize(20);
            text("Time: "+timer() + " secs", WIDTH + 5, 80);
            text("Healthy: "+p.numberUninfected(), WIDTH + 5, 110);
            text("Infected: "+p.numberSick(), WIDTH + 5, 140);
            text("Immune: "+p.numberImmune(), WIDTH + 5, 170);
            text("Dead: "+p.numberDead(), WIDTH +  5, 200);
            text("FrameRate: "+(int)frameRate, WIDTH +  5, 230);

            //Sims
            textSize(30);
            textAlign(CENTER);
            text("Sims", WIDTH + 100, 330);
            textSize(20);
            text("Time", WIDTH + 100, 360);
            text("Healthy", WIDTH + 100, 390);
            text("Infected", WIDTH + 100, 420);
            text("Immune", WIDTH + 100, 450);
            text("Dead", WIDTH + 100, 480);
            text("FrameRate", WIDTH + 100, 510);

        }

        public int timer()
        {
            int time = (millis()-startTimer)/1000;
            return time;

        }

        public void drawLegend()
        {
            int offsetY = 100;
            int offsetX = 180;
            fill(0,0,255, 20);
            rect(WIDTH-offsetX, HEIGHT-offsetY, offsetX, offsetY);
        }
        public void endScreen() {
             if(displayTime == 0) {
                    displayTime = timer();
                }
                background(0);
                textAlign(CENTER);
                strokeWeight(3);
                fill(255);
                textSize(20);
                text("Time: "+displayTime, 400, 200);
                text("Healthy: "+p.numberUninfected(), 400, 250);
                text("Infected: "+p.numberSick(), 400, 300);
                text("Immune: "+p.numberImmune(), 400, 350);
                text("Dead: "+p.numberDead(), 400, 400);   
        }
    }
