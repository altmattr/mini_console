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

	public static final int WIDTH      = 400;
    public static final int HEIGHT     = 300;
    public static final int GRAPH_HEIGHT = 50;
    public static final int SIMS       = 500;
    public static final int SIM_SIZE   = 6;
    public static final int MAX_MOVE   = 100;
    public static final int CONT_AFTER = 240; // 4 days
    public static final int DEATH_RATE = 10;   
    public static final int ILL_FOR    = 840; // 14 days
    public static final int TRANS_RATE = 50;  

    public static final Color SICK    = new Color(255, 153, 90);
    public static final Color DEAD    = new Color(255, 0, 0);
    public static final Color IMMUNE  = new Color(72, 202, 48);
    public static final Color UNKNOWN = new Color(141, 141, 141);
	
    public Population p;
	public Simulant selected;
	public Color[][] graph;
	public int graphX;

    public void setup() {
	    	 
	        
            size(Pandemic.WIDTH, Pandemic.HEIGHT+Pandemic.GRAPH_HEIGHT);
        	p = new Population();
        	selected = null;
        	graphX = 0;
        	graph = new Color[Pandemic.WIDTH][Pandemic.GRAPH_HEIGHT];
        	for(int x = 0; x < graph.length; x++){
                for(int y = 0; y < graph[x].length; y++){
                    graph[x][y]= new Color(255, 255, 255);
                }
        }
}
	    
	    public void draw() {
	    	
	    		p.update();

	            fill(255,255,255); //change to processing
	            rect(0,0,Pandemic.WIDTH,Pandemic.HEIGHT); //processing

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
	                fill(255, 253, 90, 20);
	                ellipse((int)p.averageXOfSick(), (int)p.averageYOfSick(), 40, 40);
	            }

	            fill(255,255,255);
	            ArrayList<Simulant> sorted = p.sort();
	            for(int y = 0; y < Pandemic.GRAPH_HEIGHT; y++){
	                graph[graphX][y] = chooseColour(sorted.get(y*Pandemic.SIMS/Pandemic.GRAPH_HEIGHT));
	            }
	            graphX = (graphX + 1) % Pandemic.WIDTH;

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
    }


	

