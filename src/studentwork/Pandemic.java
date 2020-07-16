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

	public static final int WIDTH = 600;
    public static final int HEIGHT = 600;
    public static final int GRAPH_HEIGHT = 50;
    public static int SIMS = 1000; //no. of sims
    public static int SIM_SIZE = 6; //size of the sims
    public static final int MAX_MOVE = 300; //max possible radius that sims can move
    public static final int CONT_AFTER = 240; // 4 days - how long after contact does the sim get sick
    public static int DEATH_RATE = 10;  //self explanatory
    public static int ILL_FOR    = 840; // 14 days - how long the sim is sick for before dying or becoming immune
    public static int TRANS_RATE = 50;  //transmission rate of disease 
    public static final int COUNTER_WIDTH = 200;
    public static int DISPLAY_WIDTH;
    public static int DISPLAY_HEIGHT;

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
    public int a, b; //VITAL DO NOT REMOVE
    public float scaleWidth;
    public float scaleHeight;
    public float scale;
    public float xTrans, yTrans;
    public boolean pressedMouse;
    public Point2D mouse;

    public void setup() {
        //fun scale translation stuff 
        scale = scaleRatio();
        xTrans = translateXRatio()/2;
        yTrans = translateYRatio()/2;
       


        a = displayWidth;
        b = displayHeight;
        DISPLAY_WIDTH = a;
        DISPLAY_HEIGHT = b;
    	noSmooth();
        startTimer = millis();
        //size(WIDTH+COUNTER_WIDTH, HEIGHT+GRAPH_HEIGHT);
        size(displayWidth, displayHeight);
    	p = new Population();
    	selected = null;
    	graphX = 0;
    	graph = new Color[(int)(scaleRatio() * (WIDTH + COUNTER_WIDTH))][(int)(scaleRatio() * GRAPH_HEIGHT)];
    	for(int x = 0; x < graph.length; x++){
            for(int y = 0; y < graph[x].length; y++){
                graph[x][y]= new Color(255, 255, 255);
            }
        }
    }
	    
    public void draw() {
        background(100);
        translate(xTrans, yTrans);
        scale(scale);
        mouse = new Point2D.Float((mouseX-xTrans)/scale,  (mouseY - yTrans)/scale);
        if(game_state == 0) { //start screen
            preGame();
        } else if(game_state == 1) {
            
            runSimulator();
            timer();
        }  
        if( p.numberSick() == 0 && game_state != 0) {
            game_state = 2;
            // end screen
           endScreen();
        } 
    	

    }
	    
    public int translateXRatio()
    {
        if (scaleWidth < scaleHeight) {
            return 0;
        }
        int xDif = displayWidth - (int)((WIDTH+COUNTER_WIDTH));
        return xDif/2;
    }
    public int translateYRatio()
    {
        if (scaleHeight < scaleWidth) {
            return 0;
        }
        int yDif = displayHeight - (int)((HEIGHT+GRAPH_HEIGHT));
        return yDif/2;
    }
    

    public float scaleRatio()
    {
        scaleWidth = (float)((1.0*displayWidth)/(WIDTH+COUNTER_WIDTH));
        scaleHeight = (float)((1.0*displayHeight)/(HEIGHT+GRAPH_HEIGHT));
        if (scaleWidth > scaleHeight){
            return scaleHeight;
        }
        else{
            return scaleWidth;
        }
    }


    private Color chooseColour(Simulant s){
        if(s.sick > 0){
            return SICK;
        } else if (s.sick < 0){
            return DEAD;
        } else if (s.immune){
            return IMMUNE;
        } else {
            return UNKNOWN;
        }
    }

        
    public void mouseClicked(){
        if (game_state == 1) {
            selected = p.simAtLocation(mouse); 
        }

    }

    public void keyPressed(){
    	if(key == CODED){
        	if (keyCode == RIGHT) {
        		selected = p.nextAfter(selected);
        	}
    	else if (keyCode == LEFT) {
		    selected = p.prevBefore(selected);
    	   }
        }
        if(key == ' ') {
            reset();
            game_state = 1;
        }
        if(key == 'b' || key == 'B') {
            game_state = 0;
        }
        if (key == 'r' || key == 'R') {
            game_state = 1;
            reset();
        }
        if ((key == 'd' || key == 'D') && game_state == 0) {
            SIMS = 1000; 
            SIM_SIZE = 6; 
            DEATH_RATE = 10; 
            ILL_FOR    = 840;
            TRANS_RATE = 50; 
        }
    }

    public void reset(){
        p = new Population();
            selected = null;
            startTimer = millis();
            graphX = 0;
          

            for(int x = 0; x < graph.length; x++){
                for(int y = 0; y < graph[x].length; y++){
                    graph[x][y]= new Color(255, 255, 255);
                }
            }
    }

    public void runSimulator() {

        p.update(); //this updates the simulation
        drawCounters();
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
        for(int y = 0; y < (int)(scale * Pandemic.GRAPH_HEIGHT); y++){
            graph[graphX][y] = chooseColour(sorted.get(y*Pandemic.SIMS/(int)(scale *Pandemic.GRAPH_HEIGHT)));
        }
        graphX = (graphX + 1) % ((int)(scaleRatio() * (WIDTH + COUNTER_WIDTH)));

        // draw graph
        for(int x = 0; x < graph.length; x++){
            for(int y = 0; y < graph[x].length; y++){
                Color fillCol = graph[x][y];
                int c = color(fillCol.getRed(), fillCol.getGreen(), fillCol.getBlue());
                set(x + (int)xTrans,(int)(scale *(HEIGHT + GRAPH_HEIGHT + (int)yTrans)) - y, c);
                noStroke();
            }
        }
            
    }

    public void drawCounters()
    {
        //infected, immune, dead, uninfected, timer
        //uninfected
        fill(237, 108, 9);
        rect(WIDTH, 0, COUNTER_WIDTH, HEIGHT);
        textSize(40);
        textAlign(LEFT);
        fill(0);

        fill(0);
        text("Pandemic", WIDTH + 4, 60);
        fill(0);
        textSize(20);
        text("Time: "+timer() + " secs", WIDTH + 25, 110);
        text("Healthy: "+p.numberUninfected(), WIDTH + 25, 140);
        text("Infected: "+p.numberSick(), WIDTH + 25, 170);
        text("Immune: "+p.numberImmune(), WIDTH + 25, 200);
        text("Dead: "+p.numberDead(), WIDTH +  25, 230);
        text("FrameRate: "+(int)frameRate, WIDTH +  25, 260);

        //Sims
        textSize(30);
        textAlign(CENTER);
        text("Sims", WIDTH + 100, 330);
        textSize(20);
        textAlign(LEFT);
        text("Healthy", WIDTH + 70, 367);
        text("Infected", WIDTH + 70, 397);
        text("Immune", WIDTH + 70, 427);
        text("Dead", WIDTH + 70, 457);

        textAlign(CENTER);
        text("Press 'b' for", WIDTH + 100, 500);
        text("Main Menu", WIDTH + 100, 520);
        text("Press 'r' to", WIDTH + 100, 550);
        text("restart Sim", WIDTH + 100, 570);

        //circles
        stroke(0);
        strokeWeight(2);
        //Healthy
        fill(141, 141, 141);
        ellipse(WIDTH+40, 360, 20,20);
        //Infected
        fill(255, 153, 90);
        ellipse(WIDTH+40, 390, 20,20);
        //Immune
        fill(72, 202, 48);
        ellipse(WIDTH+40, 420, 20,20);
        //Dead
        fill(255, 0, 0);
        ellipse(WIDTH+40, 450, 20,20);
        noStroke();
    }

    public int timer() {
        int time = (millis()-startTimer)/1000;
        return time;
    }
    
    public void preGame() {
    	background(0);
        fill(237, 108, 9);
        rect(0, 0, WIDTH+COUNTER_WIDTH,GRAPH_HEIGHT+HEIGHT);
        textAlign(CENTER);
        strokeWeight(3);
        fill(0);
        textSize(80);
        
        text("PANDEMIC", (WIDTH+COUNTER_WIDTH)/2, 150);
        textSize(20);
        
        text("PRESS SPACE BAR TO START THE GAME", (WIDTH+COUNTER_WIDTH)/2, 600);
        text("PRESS 'R' TO RESTART THE GAME", (WIDTH+COUNTER_WIDTH)/2, 200);
        text("PRESS 'D' TO RESET THE VALUES", (WIDTH+COUNTER_WIDTH)/2, 240);
        SIMS = toggle(400-25, 350, 50, 25, 1000, 1, SIMS, "Sim Count");
        SIM_SIZE = toggle(80, 350, 50, 1, 20, 1, SIM_SIZE, "Sim Size");
        DEATH_RATE = toggle( 220, 350, 50, 5, 100, 1, DEATH_RATE, "Death Rate");
        TRANS_RATE = toggle( 670, 350, 50, 5, 100, 1, TRANS_RATE, "Infection Rate");
        ILL_FOR = toggle(530, 350, 50, 25, 1000, 200, ILL_FOR, "Time Sick");
    }
    
    public void endScreen() {
         if(displayTime == 0) {
                displayTime = timer();
            }
            background(0);
            float survivalRate = (float)(p.numberImmune() + p.numberUninfected())/SIMS * 100;
            if (survivalRate >= 50) {
                fill(99,255,120);
            }
            else {
                fill(255,99,99);
            }
            rect(0, 0, WIDTH+COUNTER_WIDTH,GRAPH_HEIGHT+HEIGHT);
            textAlign(CENTER);
            strokeWeight(3);
            fill(0);
            textSize(20);
            text("Survival rate: " + survivalRate + "%", 400, 200);
            text("Time: "+displayTime, 400, 250);
            text("Healthy: "+p.numberUninfected(), 400, 300);
            text("Infected: "+p.numberSick(), 400, 350);
            text("Immune: "+p.numberImmune(), 400, 400);
            text("Dead: "+p.numberDead(), 400, 450);
            text("Press 'B' to return to the Main Menu", 400, 500);

            textSize(80);
            text("PANDEMIC", (WIDTH+COUNTER_WIDTH)/2, 150);
    }

    public int toggle(int posX, int posY, int dim, int toggleAmount, int toggleMax, int toggleMin, int toggleValue, String title) {
        stroke(0);
        fill(255);
        rect(posX, posY, dim, 2*dim);
        fill(150);
        triangle(posX, posY+dim, posX+dim/2, posY, posX+dim, posY+dim);
        triangle(posX, posY+dim, posX+dim/2, posY+2*dim, posX+dim, posY+dim);
        if ((mousePressed) && (mouse.getY() >=posY) && (mouse.getY()<=dim+posY) && (mouse.getX()<=dim+posX) && (mouse.getX()>posX) && (toggleValue<toggleMax)) {
            fill(50);
            triangle(posX, posY+dim, posX+dim/2, posY, posX+dim, posY+dim);
            toggleValue=toggleValue+toggleAmount;
            delay(300);
        }

        if ((mousePressed) && (mouse.getY() >=dim+posY) && (mouse.getY()<=2*dim+posY) && (mouse.getX()<=dim+posX) && (mouse.getX()>posX) && (toggleValue>toggleMin+toggleAmount)) {
            fill(50);
            triangle(posX, posY+dim, posX+dim/2, posY+2*dim, posX+dim, posY+dim);
            toggleValue=toggleValue-toggleAmount;
            delay(300);
        }
        fill(255);
        text(toggleValue, posX+dim/2, posY+dim*3);
        text(title, posX + dim/2, posY - dim);
        float mX = (mouseX-xTrans)/scale, mY = (mouseY - yTrans)/scale;
        
        //pressedMouse = false;
        return toggleValue;
    }
}
    
