package studentwork;

public class KuruCountry extends mqapp.MQApp { //important

    public String name(){return "Kuru Country";}
    public String author(){return "Chris Felix";}
    public String description(){return "Find all the rupees";}

    //public KuruCountry(GraphicsContext g){super(g);} - found to be unnecessary so far

    int ydirection=3;//variable required to bounce of wall
    int xdirection=3;
    int y=130;//y cordinate of fizzy
    int x=55;//x cordinate of fizzy
    int rot=270;//direction at which fizzy is pointed
    int[] xRupees = {110, 110, 110, 200, 200, 290, 290, 380, 380, 470, 470, 560, 560, 650, 650, 650 };
    int [] yRupees = {200, 290, 380, 110, 470, 110, 560, 200, 650, 110, 560, 110, 470, 200, 290, 380 };
    boolean [] status = new boolean[16];//rupees

    boolean [] baddieStatus= new boolean[7];
    int [] baddieX= new int[7];
    int [] baddieY= new int[7];
    int [] baddieSpeedX= new int[7];
    int [] baddieSpeedY= new int[7];
    int [] baddieChange = {135,225,315,405,495,585,675};
    int [] baddieXdirection= new int[7];
    int [] baddieYdirection= new int[7];
    int [] baddierot=new int[7];
    int count=0; //variable to introduce new baddies

    int lives=3;
    int colour=70;
    int change=1;
    int size=0;
    int changeSize=7;
    int sizeOfHole=0;//blackhole size
    int timer=3000;
    boolean detectInstruction=false;
    boolean detectInput=false; //variable to switch from main screen
    boolean detectWin= false;
    boolean gameEnd=false;//variable to check if fizzy uncovered all rupees

    //variable for user input
    boolean right=false;
    boolean left=false;
    boolean top=false;
    boolean down=false;

    static public void main(String[] passedArgs) {
        runSketch(appletArgs, new KuruCountry());
    }


    public void setup()
    {
        size(810,810);

        background(0,102,51);
        //sets all status values to false
        for (int i = 0; i < 16; i++) {
            status[i] = false;
        }
        for (int i = 0; i < 7; i++){
            baddieStatus[i] = false;
            baddieX[i] = 315;
            baddieY[i] = 315;
            baddieSpeedX[i]=0;
            baddieSpeedY[i]=0;
            baddieXdirection[i]=1;
            baddieYdirection[i]=1;
            baddierot[i]=0;
        }
    }

    public void draw()
    {
        if (lives<=0){
            gameEnd();
        }
        else if (x>290 && x<340 && y>290 && y<340){ //coordinates of the blackhole
            x=55;
            y=130;
            rot=270;
            lives--;
        }
        else if (detectInput){//checks for user input
            startGame();
        }
        else if (detectInstruction){//checks for user input
            instruction();
        }
        else if (detectWin){//checks for user input
            winScreen(timer,lives);
        }
        else {
            mainScreen();
        }

        if (gameEnd==true){
            gameEnd();
        }
    }

    public void startGame(){
        background(0);
        //border rectangle and background editing of the game
        strokeWeight(1);
        stroke(0);
        fill(107,142,35);
        stroke(255);
        strokeWeight(6);
        rect(30,70, 740,670,10);//green rect
        stroke(0,0,100);
        fill(255);
        rect(500,0,160,60);// rect for score
        rect(100,0,90,60);//rect for time

        switch(lives)
        { //draws static fizzy on screen depending on lives left
            case 3:
                drawLives(530, 40);
                drawLives(580, 40);
                drawLives(630, 40);
                break;
            case 2:
                drawLives(530, 40);
                drawLives(580, 40);
                break;
            case 1:
                drawLives(530, 40);
                break;
        }

        //draws the rupees
        checkBaddieCollision();
        drawRupee();
        checkRupeeStatus();//checks to see if all rupees are uncovered
        checkBoundaryCollision();//checks screen boundaries
        drawPoles();
        drawBlackhole();
        checkPoleCollision();
        drawFizzy(x,y,rot);

        if (timer==0){
            gameEnd=true;
        }
        else if (timer>0) {
            timer--;
        }

        textSize(20);
        text(timer, 120,50);

        if (timer >0) {
            if (timer%400==0) {
                baddieStatus[count]= true;
                count++;
            }
        }
        for (int i = 0; i < 7; i++){
            if (baddieStatus[i] == true)
                createBaddie(i);
        }
    }

    public void drawFizzy(int x, int y, int rot)
    {      //outline of fizzy
        stroke(0);
        rectMode(CORNER);
        ellipseMode(CENTER);
        fill(255, 0, 0);
        ellipse(x,y, 40,40);

        switch(rot)
        {
            case 90:
                fill(255);
                ellipse(x+8,y-8, 10,10);//eye1
                ellipse(x-8,y-8,10,10);//eye2
                fill(0);
                ellipse(x+8,y-8, 3,3);//pupil eye1
                ellipse(x-8,y-8, 3,3);//pupil eye2
                rect(x-16,y+20,30,20);//top hat
                rect(x-21,y+10,40,10);//bottom hat
                break;
            case 180:
                fill(255);
                ellipse(x-8,y+8, 10,10);
                ellipse(x-8,y-8,10,10);
                fill(0);
                ellipse(x-8,y+8, 3,3);
                ellipse(x-8,y-8, 3,3);
                //hat
                rect(x+20,y-15,20,30);
                rect(x+10,y-20,10,40);
                break;
            case 270:
                fill(255);
                ellipse(x+8,y+8, 10,10);
                ellipse(x-8,y+8,10,10);
                fill(0);
                ellipse(x+8,y+8, 3,3);
                ellipse(x-8,y+8, 3,3);
                rect(x-16,y-40,30,20);
                rect(x-21,y-20,40,10);
                break;
            case 0:
                fill(255);
                ellipse(x+8,y+8, 10,10);//eye1
                ellipse(x+8,y-8,10,10);//eye2
                fill(0);
                ellipse(x+8,y+8, 3,3);//pupil eye1
                ellipse(x+8,y-8, 3,3);//pupil eye2
                rect(x-40,y-15,20,30);//top hat
                rect(x-20,y-20,10,40);//bottom hat
                break;
        }
    }

    public void drawPoles()
    {
        strokeWeight(1);
        stroke(0);
        for(int polesx=90; polesx <810; polesx +=90)
        {
            for (int polesY=90; polesY <810; polesY +=90)
            {
                ellipseMode(CENTER);
                fill(255);
                ellipse(polesx, polesY, 20,20);
                fill(51,25,0);
                ellipse(polesx, polesY, 10,10);
            }
        }
    }

    public void drawRupee(){
        for (int i = 0; i < xRupees.length; i++)
        {
            if (x>=xRupees[i] && x <=xRupees[i]+50 && y >=yRupees[i]&& y <= yRupees[i]+50)//checks if fizzy has walked over the cards
            {
                status[i] = true;
            }
            if (status[i]==true)
            {
                fill(0, 255, 0);
                stroke(0);
                strokeWeight(1);
                rect(xRupees[i], yRupees[i], 40, 40);//draws card if fizzy has walked over it
            }
        }
    }

    public void mainScreen()
    {
        background(120,100,colour);
        for (int x=0; x<width;x+=70)
        {
            stroke(0);
            strokeWeight(3);
            line(x,0, width,height-x);
            line(0,x, width-x,height);
            line(width,x, x,height);
            line(width-x,0, 0,height-x);

        }
        stroke(0);
        fill(255,0,0);
        ellipse(width/2, height/2, 400,400);
        fill(0);
        rect(width/5+30, height/3,420,50);
        rect(width/3-30, height/5-30,310,180);
        fill(255);
        ellipse(width/3+40, height/2+80, 110,110);
        ellipse(width/2+90, height/2+80, 110,110);
        fill(0);
        ellipse(width/3+40, height/2+80, 50,80);
        ellipse(width/2+90, height/2+80,50,80);
        textSize(36);
        fill(255);
        text("Fizz Fizz Land", width/3+10, height/4);
        textSize(22);
        rect(width/3+40, height/4+30, 180,50,10);
        rect(width/3+40, height/3+30, 180,50,10);
        fill(0);
        text("START", width/2-40, height/4+60);
        text("INSTRUCTIONS", width/2-80, height/3+65);
        if (colour>255|| size>=110)
        {
            change*=-1;
            changeSize*=-1;

        }
        else if (colour<40 || size<=-50)
        {
            change*=-1;
            changeSize*=-1;
        }
        colour= colour+change;
        if (dist(mouseX,mouseY, width/2,height/2)<200)
        {
            fill(255,0,0);
            noStroke();
            rect(width/2,height/3+150,150,size,20);
            rect(width/3-23,height/3+150,150,size,20);
            size=changeSize+size;
        }
    }

    public void mouseClicked(){
        //activates menu screen START and INSTRUCTION buttons
        if ((mouseX>=width/3+40&& mouseX<490 && mouseY>=height/4+30 && mouseY<=565/2) || (mouseX>=width/3+40 && mouseX<490 && mouseY>=height/1.5f+40 && mouseY<=height/1.5f+90) )
        {
            detectInstruction=false;
            detectInput=true;
        }
        else if (mouseX>=width/3+40&& mouseX<490 && mouseY>=height/3+30 && mouseY<=350)
        {
            detectInstruction=true;
        }
    }



    public void createBaddie(int i)
    {
        strokeWeight(0);
        fill(30,144,255);
        ellipse(baddieX[i], baddieY[i], 30, 30);
        fill(0,0,0);
        switch(baddierot[i])
        {
            case (90):
                ellipse(baddieX[i] - 50/8, baddieY[i] -4, 10, 15);
                ellipse(baddieX[i] + 50/8, baddieY[i] - 4, 10, 15);
                fill(255,255,255);
                ellipse(baddieX[i] - 50/8, baddieY[i] - 6, 5, 10);
                ellipse(baddieX[i] + 50/8, baddieY[i] - 6, 5, 10);
                stroke(30,144,255);
                strokeWeight(4);
                line(baddieX[i],baddieY[i]+20,baddieX[i],baddieY[i]-10);
                break;

            case(270):
                ellipse(baddieX[i] - 50/8, baddieY[i] + 50/8, 10, 15);
                ellipse(baddieX[i] + 50/8, baddieY[i] + 50/8, 10, 15);
                fill(255,255,255);
                ellipse(baddieX[i] - 50/8, baddieY[i]-5 + 50/4, 5, 10);
                ellipse(baddieX[i] + 50/8, baddieY[i]-5 + 50/4, 5, 10);
                stroke(30,144,255);
                strokeWeight(4);
                line(baddieX[i],baddieY[i]-2*10,baddieX[i],baddieY[i]+2*5);
                break;

            case(180):
                ellipse(baddieX[i]-5, baddieY[i] - 50/8, 15, 10);
                ellipse(baddieX[i]-5, baddieY[i] + 50/8, 15, 10);
                fill(255,255,255);
                ellipse(baddieX[i]-5, baddieY[i]-6, 10, 5);
                ellipse(baddieX[i]-5, baddieY[i]-5 + 50/4, 10, 5);
                stroke(30,144,255);
                strokeWeight(4);
                line(baddieX[i]+20,baddieY[i],baddieX[i]-10,baddieY[i]);
                break;

            case(0):
                ellipse(baddieX[i]+5, baddieY[i] - 50/8, 15, 10);
                ellipse(baddieX[i]+5, baddieY[i] + 50/8, 15, 10);
                fill(255,255,255);
                ellipse(baddieX[i]+5, baddieY[i]-6, 10, 5);
                ellipse(baddieX[i]+5, baddieY[i]-5 + 50/4, 10, 5);
                stroke(30,144,255);
                strokeWeight(4);
                line(baddieX[i]-20,baddieY[i],baddieX[i]+10,baddieY[i]);
                break;
        }
        baddieX[i]= baddieX[i]+ (baddieSpeedX[i]* baddieXdirection[i]);
        baddieY[i]= baddieY[i]+ (baddieSpeedY[i]* baddieYdirection[i]);
        baddieDirectionChange(i);
        //keeps the urchins within the boundaries
        if ((baddieX[i]) >= width-80)
        {
            baddierot[i]=180;
            baddieXdirection[i]*=-1;
        }
        if ((baddieX[i]) <=80)
        {
            baddierot[i]=0;
            baddieXdirection[i]*=-1;
        }

        if ((baddieY[i]) > height-80)
        {
            baddierot[i]=90;
            baddieYdirection[i]*=-1;
        }
        if ((baddieY[i]) <80)
        {
            baddierot[i]=270;
            baddieYdirection[i]*=-1 ;
        }
    }


    public void keyPressed()
    {
        switch(key)
        {
            case 'w':
                top=true;
                break;
            case 's':
                down=true;
                break;
            case 'a':
                left=true;
                break;
            case 'd':
                right=true;
                break;
        }
    }

    public void changeDirection(){
        if (top){
            rot=90;
            ydirection=-3;
            top=false;
        }
        else if(down){
            rot=270;
            ydirection=3;
            down=false;
        }
        else if(right){
            rot=0;
            xdirection=3;
            right=false;
        }
        else if(left){
            rot=180;
            xdirection=-3;
            left=false;
        }
    }



    public void drawBlackhole()
    {
        rectMode(CENTER);
        fill(13,2,64);
        rect(315, 315, 55, 55, 15);
        fill(255);
        rect(315,315, sizeOfHole,sizeOfHole, 7);
        fill(0);
        rect(315,315, sizeOfHole-10,sizeOfHole-10, 7);
        sizeOfHole= (sizeOfHole+1) % 55;
    }

    public void gameEnd()
    {

        background(100);
        for (int x=0; x<width;x+=70)
        {
            stroke(0);
            strokeWeight(3);
            line(x,0, width,height-x);
            line(0,x, width-x,height);
            line(width,x, x,height);
            line(width-x,0, 0,height-x);

        }
        fill(0);
        rect(0, height/3, width,100);
        textSize(36);
        fill(255,0,0);
        text("YOU DIED", width/3+30, height/3+50);

    }

    public void drawLives(int livesx, int livesy)
    {
        stroke(0);
        strokeWeight(1);
        rectMode(CORNER);
        ellipseMode(CENTER);
        fill(255, 0, 0);
        ellipse(livesx,livesy, 30,30);
        fill(255);
        ellipse(livesx+7,livesy+6, 8,8);
        ellipse(livesx-7,livesy+6,8,8);
        fill(0);
        ellipse(livesx+7,livesy+6, 3,3);
        ellipse(livesx-7,livesy+6, 3,3);
        rect(livesx-17,23,33,10);
        rect(livesx-13,10,25,15);
    }

    public void instruction(){
        background(200);
        for (int x=0; x<width;x+=70)
        {
            stroke(0);
            strokeWeight(3);
            line(x,0, width,height-x);
            line(0,x, width-x,height);
            line(width,x, x,height);
            line(width-x,0, 0,height-x);

        }
        fill(255);
        textSize(22);
        rect(width/3+40, height/1.5f+40, 180,50,10);
        fill(0);
        text("START", width/2-40, height/1.5f+70);
        fill(255);
        rect(width/10+40, height/10-50,575,500);
        fill(0);
        textSize(40);
        text("INSTRUCTIONS", width/3, height/10);
        textSize(30);
        text("AVOID the BLACKHOLES", width/4+30, height/10+100);
        text("AVOID the URCHINS", width/4+30, height/10+170);
        text("Uncover all RUPPEES", width/4+30, height/10+240);
        text("Keep track of your lives!", width/4+30, height/10+310);
        text("Do it all in the TIME LIMIT", width/4+30, height/10+380);
    }

    public void winScreen(int finalTime, int finalLife)
    {
        background(255,0,0);
        for (int x=0; x<width;x+=70)
        {
            stroke(0);
            strokeWeight(3);
            line(x,0, width,height-x);
            line(0,x, width-x,height);
            line(width,x, x,height);
            line(width-x,0, 0,height-x);

        }
        fill(255);
        rect(width/3-30, height/3, 310,200);
        textSize(36);
        fill(0);
        text("YAY! YOU WIN!", width/3, height/2-40);
        textSize(22);
        text("Time taken: " + timer, width/3, height/2+40);
        text("Lives left: " + lives, width/3, height/2+5);

    }
    public void checkBaddieCollision(){
        for (int i = 0; i < baddieX.length; i++)
        {
            if (baddieStatus[i])
            {
                if (dist(x, y, baddieX[i], baddieY[i])<=35)//checks if fizzy has walked over the cards
                {
                    x=130;
                    y=130;
                    lives--;
                }
            }
        }
    }
    public void checkBoundaryCollision(){
        //conditional statements to bounce off walls
        //ensure fizzy doesnt go off screen
        if (x<=0 ) {
            x=x+10;
        }
        else if ( x>=width ) {
            x-=10;
        }
        else if ( y<=0 ) {
            y=y+10;
        }
        else if (y>= height) {
            y-=10;
        }

        if (rot==90 || rot==270) {
            y=y+ ydirection;
            if (y >= height){
                ydirection =-3;
                rot=90;
            }
            else if(y <=2){
                ydirection=3;
                rot=270;
            }
        }
        else if (rot==0 || rot==180){
            x= x + xdirection;
            if (x >= width-30){
                xdirection= -3;
                rot=180;
            }
            else if (x <=0) {
                xdirection=3;
                rot = 0;
            }
        }
    }

    public void checkRupeeStatus(){
        for (int i = 0; i < 16; i++)
        {
            if (!status[i])
            {
                break;
            }
            else if (status[i])
            {
                if (i==15)
                {
                    detectWin=true;
                    detectInput=false;
                }
            }
        }
    }

    public void checkPoleCollision(){
        if (y>55 && y<125|| y>145 && y<225|| y>235 && y<305|| y>325 && y<395|| y>415 && y<485|| y>505 && y<575|| y>595 && y<665|| y>685 && y<755)
        {
            drawFizzy(x,y,rot);
        }
        else if  (x>55 && x<125|| x>145 && x<225|| x>235 && x<305|| x>325 && x<395|| x>415 && x<485|| x>505 && x<575|| x>595 && x<665|| x>685 && x<755)
        {
            drawFizzy(x,y,rot);
        }
        else{
            changeDirection();
        }
    }

    public void baddieDirectionChange(int i){

        if (baddieX[i]== baddieChange[(int)random(6)])//changes direction of urchins at a random spot in between the poles
        {
            if (baddieYdirection[i]<1)//to make sure rotation syncs with movement
            {
                baddierot[i]=90;
            }
            else {
                baddierot[i]=270;
            }
            baddieSpeedX[i]=0;
            baddieSpeedY[i]=2;
        }
        if (baddieY[i]== baddieChange[(int)random(6)])//changes direction of urchins at a random spot in between the poles
        {
            if (baddieXdirection[i]<1)//to make sure rotation syncs with movement
            {
                baddierot[i]=180;
            }
            else {
                baddierot[i]=0;
            }
            baddieSpeedX[i]=2;
            baddieSpeedY[i]=0;
        }
    }
}

