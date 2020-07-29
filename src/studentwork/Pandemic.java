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
  	public String author(){return "Elizabeth Cappellazzo, Cameron Baker, Quang Minh Pham, Robert Stockton, Nishal Najeeb, and Chhade Alasbar";}
  	public String description(){return "Pandemic simulator";}

  	
	public static final int WIDTH = 600; //width of simulation window (where sims can be)
    public static final int HEIGHT = 600; //height of simulation window (where sims can be)
    public static final int GRAPH_HEIGHT = 50;
    public static final int MAX_MOVE = 300; //max possible radius that sims can move
    public static final int CONT_AFTER = 240; // 4 days - how long after contact does the sim get sick
    public static final int COUNTER_WIDTH = 200; //width of side bar with counters
    
    //Colours of sims
    public static final Color SICK    = new Color(255, 153, 90);
    public static final Color DEAD    = new Color(255, 0, 0);
    public static final Color IMMUNE  = new Color(72, 202, 48);
    public static final Color UNKNOWN = new Color(141, 141, 141);

    //parameters that toggles can change
    public static int SIMS = 1000; //no. of sims
    public static int SIM_SIZE = 6; //size of the sims
    public static int DEATH_RATE = 10;  //self explanatory
    public static int ILL_FOR    = 840; // 14 days - how long the sim is sick for before dying or becoming immune
    public static int TRANS_RATE = 50;  //transmission rate of disease 
    
    public Population p;
	public Simulant selected; //the sim that has been clicked on
	public Color[][] graph; //array of colours
	public int graphX; 
    public Point2D mouse;   
    public int startTimer;
    
    // to determine if the game is running or not
    // 0 = start screen, 1 = game running, 2 = end screen
    public int game_state = 0; 
    public int displayTime = 0; // to display time in end screen
    
    //variables for scaling and translating of sim window
    public float scaleWidth;
    public float scaleHeight;
    public float scale;
    public float xTrans, yTrans;

    static public void main(String[] passedArgs) {
        runSketch(appletArgs, new Pandemic());
    }
    

    public void setup() {
    	//scaling and translation of game window 
        scale = scaleRatio();
        xTrans = translateXRatio()/2;
        yTrans = translateYRatio()/2;
        
    	//noSmooth();
        startTimer = millis();
        size(displayWidth, displayHeight);
    	p = new Population();
    	selected = null;
    	graphX = 0;
    	graph = new Color[(int)(scale * (WIDTH + COUNTER_WIDTH))][(int)(scale * GRAPH_HEIGHT)];
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
        
        //triggers start screen
        if(game_state == 0) {
            mainMenu();
        } 
        
        //game has started
        else if(game_state == 1) {
            runSimulator();
            timer();
        }
        
        //numberSick = 0 when all dead or immune, game should end
        if(p.numberSick() == 0 && game_state != 0) { 
            game_state = 2;
            endScreen();
        } 
    }
	
    
    //Methods that start, run and finish sim
    
    //Main Menu
    public void mainMenu() {
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
        
        //Toggles
        SIMS = toggle(400-25, 350, 50, 25, 1000, 1, SIMS, "Sim Count");
        SIM_SIZE = toggle(80, 350, 50, 1, 20, 1, SIM_SIZE, "Sim Size");
        DEATH_RATE = toggle( 220, 350, 50, 5, 100, 1, DEATH_RATE, "Death Rate");
        TRANS_RATE = toggle( 670, 350, 50, 5, 100, 1, TRANS_RATE, "Infection Rate");
        ILL_FOR = toggle(530, 350, 50, 25, 1000, 240, ILL_FOR, "Time Sick");
    }
    
    //Creates and updates toggle values
    public int toggle(int posX, int posY, int dim, int toggleAmount, int toggleMax, int toggleMin, int toggleValue, String title) {
        //draws toggle
    	stroke(0);
        fill(255);
        rect(posX, posY, dim, 2*dim);
        fill(150);
        triangle(posX, posY+dim, posX+dim/2, posY, posX+dim, posY+dim);
        triangle(posX, posY+dim, posX+dim/2, posY+2*dim, posX+dim, posY+dim);
        
        
       //Updates toggle value
        
        //Top toggle button
        if ((mousePressed) && (mouse.getY() >=posY) && (mouse.getY()<=dim+posY) && (mouse.getX()<=dim+posX) && (mouse.getX()>posX) && (toggleValue<toggleMax)) {
            fill(50); //changes toggle colour when clicked
            triangle(posX, posY+dim, posX+dim/2, posY, posX+dim, posY+dim);
            toggleValue=toggleValue+toggleAmount;
            delay(300); //slows down toggle
        }
        
        //Bottom toggle button
        if ((mousePressed) && (mouse.getY() >=dim+posY) && (mouse.getY()<=2*dim+posY) && (mouse.getX()<=dim+posX) && (mouse.getX()>posX) && (toggleValue>toggleMin+toggleAmount)) {
        	fill(50); //changes toggle colour when clicked
            triangle(posX, posY+dim, posX+dim/2, posY+2*dim, posX+dim, posY+dim);
            toggleValue=toggleValue-toggleAmount;
            delay(300); //slows down toggle
        }
        
        //Displays toggle value and title
        fill(255);
        text(toggleValue, posX+dim/2, posY+dim*3);
        text(title, posX + dim/2, posY - dim);
        
        return toggleValue;
    }
    
    //makes the sim start
    public void runSimulator() {
    	p.update(); //this updates the simulation
        drawCounters();
        fill(255,255,255);
        rect(0,0,WIDTH,HEIGHT);

        //shows circle of motion around selected sim
        if (selected != null){
            stroke(94,86,90);
            noFill();
            ellipse((int)selected.homeLoc.getX(), (int)selected.homeLoc.getY() , (int)selected.mobility*2, (int)selected.mobility*2);
            noStroke();
        }

        //places sims on screen
        for(Simulant s: p.sims){
            Color fillCol = chooseColour(s);
            fill(fillCol.getRed(), fillCol.getGreen(), fillCol.getBlue());
            ellipse((int)s.loc.getX() - Pandemic.SIM_SIZE/2,(int)s.loc.getY() - Pandemic.SIM_SIZE/2, Pandemic.SIM_SIZE, Pandemic.SIM_SIZE);
        }
        
        //faint purple circle around centre of infection
        if (p.numberSick() > 0){
            fill(131, 50, 168, 20);
            int circleRadius = 40;
            ellipse((int)p.averageXOfSick()-SIM_SIZE/2, (int)p.averageYOfSick()-SIM_SIZE/2, circleRadius + p.numberSick()/2, circleRadius + p.numberSick()/2);
        }
        
        fill(255,255,255);
        ArrayList<Simulant> sorted = p.sort(); //Does cause performance issues
        
        //samples a proportion of sorted simulants and puts their colours in an array
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
    
    //draws the side bar with counters
    public void drawCounters() {
        fill(237, 108, 9);
        rect(WIDTH, 0, COUNTER_WIDTH, HEIGHT);

        //Counters
        textAlign(LEFT);
        fill(0);
        textSize(40);
        text("Pandemic", WIDTH + 4, 60);
        textSize(20);
        text("Time: "+timer() + " secs", WIDTH + 25, 110);
        text("Healthy: "+p.numberHealthy(), WIDTH + 25, 140);
        text("Infected: "+p.numberSick(), WIDTH + 25, 170);
        text("Immune: "+p.numberImmune(), WIDTH + 25, 200);
        text("Dead: "+p.numberDead(), WIDTH +  25, 230);
        text("FrameRate: "+(int)frameRate, WIDTH +  25, 260);

        //Sims key
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

        //circles for Sims key
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
    
    //End screen, triggered when last sim dies or becomes immune
    public void endScreen() {
         
    	 //shows total time of simulation
    	if(displayTime == 0) {
                displayTime = timer();
         }
         
         background(0);
         float survivalRate = (float)(p.numberImmune() + p.numberHealthy())/SIMS * 100;
         
         //chooses background colour based on survival rate
         if (survivalRate >= 50) {
        	 fill(99,255,120);
         } else {
             fill(255,99,99);
         }
         
         rect(0, 0, WIDTH+COUNTER_WIDTH,GRAPH_HEIGHT+HEIGHT);
         textAlign(CENTER);
         strokeWeight(3);
         fill(0);
         
         textSize(80);
         text("PANDEMIC", (WIDTH+COUNTER_WIDTH)/2, 150);
         
         //final sim stats
         textSize(20);
         text("Survival rate: " + survivalRate + "%", 400, 200);
         text("Time: "+displayTime, 400, 250);
         text("Healthy: "+p.numberHealthy(), 400, 300);
         text("Infected: "+p.numberSick(), 400, 350);
         text("Immune: "+p.numberImmune(), 400, 400);
         text("Dead: "+p.numberDead(), 400, 450);
         
         text("Press 'B' to return to the Main Menu", 400, 500);
      
    }
    
    
    //Input methods/user interaction methods
    
    public void mouseClicked(){
        if (game_state == 1) {
            selected = p.simAtLocation(mouse); 
        }
    }
    
    public void keyPressed(){
    	if(key == CODED){
        	if (keyCode == RIGHT) { //chooses next sim
        		selected = p.nextAfter(selected);
        	}
    	else if (keyCode == LEFT) { //chooses next sim
		    selected = p.prevBefore(selected);
    	   }
        }
    	
    	//starts game
        if(key == ' ') {
            reset();
            game_state = 1;
        }
        
        //goes to main menu
        if(key == 'b' || key == 'B') {
            game_state = 0;
        }
        
        //restarts the sim
        if (key == 'r' || key == 'R') {
            game_state = 1;
            reset();
        }
        
        //returns toggle values to default
        if ((key == 'd' || key == 'D') && game_state == 0) {
            SIMS = 1000; 
            SIM_SIZE = 6; 
            DEATH_RATE = 10; 
            ILL_FOR    = 840;
            TRANS_RATE = 50; 
        }
    }
    
    //restarts the sim from time = 0 with user set variables
    public void reset() { 
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
    
    
    //Calculation methods

    //calculates number of pixels to translate in x-axis
    public int translateXRatio() {
        if (scaleWidth < scaleHeight) { //x-axis doesn't need scaling
            return 0;
        }
        int xDif = displayWidth - (int)((WIDTH+COUNTER_WIDTH));
        return xDif/2;
    }
    
    //calculates number of pixels to translate in y-axis
    public int translateYRatio() {
        if (scaleHeight < scaleWidth) { //y-axis doesn't need scaling
            return 0;
        }
        int yDif = displayHeight - (int)((HEIGHT+GRAPH_HEIGHT));
        return yDif/2;
    }
    
    //calculates how much screen needs to be scaled
    public float scaleRatio() {
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

    public int timer() {
        int time = (millis()-startTimer)/1000;
        return time;
    }
   
}