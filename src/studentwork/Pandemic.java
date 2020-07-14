package studentwork;

import java.util.ArrayList;
import processing.core.*; 

public class Pandemic extends mqapp.MQApp {
	
	public String name(){return "Pandemic";}
  	public String author(){return "Elizabeth Cappellazzo";}
  	public String description(){return "Pandemic simulator";}

	public static final int WIDTH      = 400;
    public static final int HEIGHT     = 300;
    public static final int GRAPH_HEIGHT = 50;
    public static final int SIMS       = 500;
    public static final int SIM_SIZE   = 6;
    public static final int MAX_MOVE   = 1000;
    public static final int CONT_AFTER = 240; // 4 days
    public static final int DEATH_RATE = 10;   
    public static final int ILL_FOR    = 840; // 14 days
    public static final int TRANS_RATE = 50;  

    public static final color SICK    = color(255, 153, 90);
    public static final color DEAD    = color(255, 0, 0);
    public static final color IMMUNE  = color(72, 202, 48);
    public static final color UNKNOWN = color(141, 141, 141);
	
    public Population p;
	public Simulant selected;
	public color[][] graph;
	public int graphX;

    public void setup() {
	    	 
	        
            size(Main.WIDTH, Main.HEIGHT+Main.GRAPH_HEIGHT);
        	p = new Population();
        	selected = null;
        	graphX = 0;
        	graph = new color[Main.WIDTH][Main.GRAPH_HEIGHT];
        	for(int x = 0; x < graph.length; x++){
                for(int y = 0; y < graph[x].length; y++){
                    graph[x][y]= color(255, 255, 255);
                }
        }
}
	    
	    public void draw() {
	    	
	            fill(255,255,255); //change to processing
	            rect(0,0,Main.WIDTH,Main.HEIGHT); //processing

	            if (selected != null){ //shows circle of motion around selected sim
	                stroke(94,86,90);
	                noFill();
	                ellipse((int)selected.homeLoc.getX() - (int)selected.mobility, (int)selected.homeLoc.getY() - (int)selected.mobility, (int)selected.mobility*2, (int)selected.mobility*2);
	            	noStroke();
	            }

	            for(Simulant s: p.sims){ //places sims on screen
	                fill(chooseColour(s));
	                ellipse((int)s.loc.getX() - Main.SIM_SIZE/2,(int)s.loc.getY() - Main.SIM_SIZE/2, Main.SIM_SIZE, Main.SIM_SIZE);
	            }
	            if (p.numberSick() > 0){ //faint yellow circle
	                fill(255, 253, 90, 20);
	                ellipse((int)p.averageXOfSick() - 20, (int)p.averageYOfSick() - 20, 40, 40);
	            }

	            fill(255,255,255);
	            ArrayList<Simulant> sorted = p.sort();
	            for(int y = 0; y < Main.GRAPH_HEIGHT; y++){
	                graph[graphX][y] = chooseColour(sorted.get(y*Main.SIMS/Main.GRAPH_HEIGHT));
	            }
	            graphX = (graphX + 1) % Main.WIDTH;

	            // draw graph
	            for(int x = 0; x < graph.length; x++){
	                for(int y = 0; y < graph[x].length; y++){
	                    fill(graph[x][y]);
	                    line(x, Main.HEIGHT + Main.GRAPH_HEIGHT - y, x, Main.HEIGHT + Main.GRAPH_HEIGHT -y);
	                }
	            }
	    }
	    
	    private color chooseColour(Simulant s){
            if(s.sick > 0){
                    return Main.SICK;
            } else if (s.sick < 0){
                    return Main.DEAD;
            } else if (s.immune){
                    return Main.IMMUNE;
            } else {
                    return Main.UNKNOWN;
            }
        }

        
        public void mouseClicked()
        {
        	Point2D mouse = new Point2D(mouseX, mouseY);
        	selected = p.simAtLocation(mouse); //simAtLocation needs to be updated for mouseX mouseY
        }
        public void keyPressed()
        {
        	if (keyCode == RIGHT) {
        		selected = p.nextAfter(selected);
        	}
        	else if (keyCode == LEFT) {
        		selected = p.prevBefore(selected);
        	}
        }
    }
}

	

