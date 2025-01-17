package studentwork;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Pong extends mqapp.MQApp {


    public String name() {return "Pong";}
    public String author() { return "Beau Williams"; }
    public String description() { return "Pong - Need I Say More";}

	boolean gameover= false, right = false, left = false, d = false, a = false;
	int topscore=0;
	int bottomscore=0;
	float changespeed=0;
	Paddle bottom;
	Ball pongball;
	Paddle top;
	public void setup()
	{
	  noStroke();
	  pongball= new Ball();
	  bottom=new Paddle();
	  top=new Paddle();
	  top.y=0;
	  size(displayWidth, displayHeight);
	}
	public void keyPressed()
	{
	  if (keyCode == LEFT)
	  {
	    left = true;
	  }
	  if (keyCode == RIGHT)
	  {
	    right = true;
	  }
	  if (key == 'a')
	  {
	    a=true;
	  }
	  if (key == 'd' )
	  {
	    d=true;
	  }
	}
	public void keyReleased()
	{
	  if (keyCode == LEFT)
	  {
	    left = false;
	  }
	  if (keyCode==RIGHT)
	  {
	    right = false;
	  }
	  if (key=='a')
	  {
	    a=false;
	  }
	  if (key=='d')
	  {
	    d=false;
	  }
	}
	public void draw()
	{
	  if (gameover==false)
	  {
	    background(0);
	    bottom.show();
	    top.show();
	    if (left==true)
	    {
	      bottom.moveleft();
	    }
	    if (right==true)
	    {
	      bottom.moveright();
	    }
	    if (a==true)
	    {
	      top.moveleft();
	    }
	    if (d==true)
	    {
	      top.moveright();
	    }
	    pongball.move();
	    pongball.bounce();
	    pongball.show();
	    if (pongball.y<-8)
	    {
	      gameover=true;
	      bottomscore++;
	    }
	    if (pongball.y>width + 8)
	    {
	      gameover=true;
	      topscore++;
	    }
	  }
	  else //gameover==true
	  {
	    background(0);
	    fill(255, 0, 0);
	    changespeed=0;
	    textSize(18);
	    text("Top Player's Score: "+topscore, 15, 290);
	    text("Bottom Player's Score: "+bottomscore, 15, 330);
	    textSize(36);
	    text("Game over! Click to restart.", 15, 250);
	    if (mousePressed==true)
	    {
	      pongball.x=(int) random(200, 301);
	      pongball.y=(int) random(200, 301);
	      int xdirection=(int) random(2);
	      int ydirection=(int) random(2);
	      if (xdirection==0)
	      {
	        pongball.right=true;
	      }
	      else //xidrection==1
	      {
	        pongball.right=false;
	      }
	      if (ydirection==0)
	      {
	        pongball.up=true;
	      }
	      else //ydirection==1
	      {
	        pongball.up=false;
	      }
	      gameover=false;
	    }
	  }
	}
	class Paddle
	{
	  int x, y;
	  Paddle()
	  {
	    x=250;
	    y=height-4;
	  }
	  void show()
	  {
	    fill(255, 0, 0);
	    rect(x, y, 60, 4);
	  }
	  void moveleft()
	  {
	    if (x>=0)
	    {
	      x-=5;
	    }
	  }
	  void moveright()
	  {
	    if (x<=width)
	    {
	      x+=5;
	    }
	  }
	}
	class Ball
	{
	  int x, y;
	  boolean up, right;
	  Ball()
	  {
	    x=16;
	    y=484;
	    up=true;
	    right=true;
	  }
	  void move()
	  {
	    if (up==true)
	    {
	      y=(int) (y-2-changespeed/2);
	    }
	    else  //up==false
	    {
	      y=(int)(y+2+changespeed/2);
	    }
	    if (right==true)
	    {
	      x=(int) (x+1+changespeed);
	    }
	    else  //right==false
	    {
	      x=(int) (x-1-changespeed);
	    }
	  }
	  void bounce()
	  {
	    if (get((x)-8, (y))!=color(0))
	    {
	      right=true;
	    }
	    if (get((x)+8, (y))!=color(0))
	    {
	      right=false;
	    }
	    if (get((x), (y)-8)==color(255, 0, 0))
	    {
	      up=false;
	    }
	    if (get((x), (y)+8)==color(255, 0, 0))
	    {
	      up=true;
	      changespeed+=1.0/4;
	    }
	  }
	  void show()
	  {
	    fill(247, 226, 47);
	    ellipse(x, y, 16, 16);
	  }
	}
}
