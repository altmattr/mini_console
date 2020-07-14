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

	public static final int WIDTH      = 1280;
    public static final int HEIGHT     = 720;
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
	
    public Population p;
	public Simulant selected; //the sim that has been clicked on
	public Color[][] graph; //array of colours
	public int graphX;

    public void setup() {
	    	 
	        
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
	            if (p.numberSick() > 0){ //faint yellow circle
	                fill(131, 50, 168, 20);
                    int circleRadius = 40;
                    float scale = (p.numberSick() / p.sims.size()) * 1000;
	                ellipse((int)p.averageXOfSick(), (int)p.averageYOfSick(), circleRadius + p.numberSick(), circleRadius + p.numberSick());
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
	                    stroke(fillCol.getRed(), fillCol.getGreen(), fillCol.getBlue());
	                    line(x, Pandemic.HEIGHT + Pandemic.GRAPH_HEIGHT - y, x, Pandemic.HEIGHT + Pandemic.GRAPH_HEIGHT -y);
	                    noStroke();
	                }
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
        	Point2D mouse = new Point2D.Float(mouseX, mouseY);
        	selected = p.simAtLocation(mouse); //simAtLocation needs to be updated for mouseX mouseY
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
        }

        public void drawCounters()
        {
            //infected, immune, dead, uninfected, timer
            //uninfected
            fill(200);
            rect(WIDTH, 0, COUNTER_WIDTH, HEIGHT);
            textSize(20);
            fill(0);
            text("Time: "+(millis()/1000), WIDTH + 10, 20);
            text("Healthy: "+p.numberUninfected(), WIDTH + 10, 50);
            text("Infected: "+p.numberSick(), WIDTH + 10, 80);
            text("Immune: "+p.numberImmune(), WIDTH + 10, 110);
            text("Dead: "+p.numberDead(), WIDTH +  10, 140);
        }

        public void drawLegend()
        {
            int offsetY = 100;
            int offsetX = 180;
            fill(0,0,255, 20);
            rect(WIDTH-offsetX, HEIGHT-offsetY, offsetX, offsetY);
        }
    }