package unigame;

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import static javax.swing.JOptionPane.*;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

class Player {
  PImage[] overworldLeft;
  PImage[] overworldRight;  
  PImage[] overworldUp; 
  PImage[] overworldDown;
  PImage battleImage;
  String playerType;
  String name = "Macky"; 

   static final String path = Globals.assetspath+"Characters/Player/";
  static final String battlepath = Globals.assetspath+"Characters/Battle/";

  public Player(UniGame p3) {
    this.name = showInputDialog("Enter your name please:"); //Ask user for their name
    this.playerType = showInputDialog("What is your hair colour: red, blue, yellow, green?"); //Ask user for their hair colour
    System.out.println(this.playerType);
    
    battleImage = p3.loadImage(battlepath+"p_"+playerType+"_battle.png"); //battleUnit constructor will automatically upscale this
    PImage[] tOverworldLeft = {p3.loadImage(path+"p_"+playerType+"_left_still.png"), p3.loadImage(path+"p_"+playerType+"_left_left.png"), p3.loadImage(path+"p_"+playerType+"_left_still.png"), p3.loadImage(path+"p_"+playerType+"_left_right.png")};   
    overworldLeft = tOverworldLeft;
    PImage[] tOverworldRight = {p3.loadImage(path+"p_"+playerType+"_right_still.png"), p3.loadImage(path+"p_"+playerType+"_right_left.png"), p3.loadImage(path+"p_"+playerType+"_right_still.png"), p3.loadImage(path+"p_"+playerType+"_right_right.png")};   
    overworldRight = tOverworldRight;
    PImage[] tOverworldUp = {p3.loadImage(path+"p_"+playerType+"_up_still.png"), p3.loadImage(path+"p_"+playerType+"_up_left.png"), p3.loadImage(path+"p_"+playerType+"_up_still.png"), p3.loadImage(path+"p_"+playerType+"_up_right.png")};   
    overworldUp = tOverworldUp;
    PImage[] tOverworldDown = {p3.loadImage(path+"p_"+playerType+"_down_still.png"), p3.loadImage(path+"p_"+playerType+"_down_left.png"), p3.loadImage(path+"p_"+playerType+"_down_still.png"), p3.loadImage(path+"p_"+playerType+"_down_right.png")};   
    overworldDown = tOverworldDown;
    
    for(int i = 0; i < overworldLeft.length; ++i){
      overworldLeft[i] = p3.upscale(overworldLeft[i]);
      overworldRight[i] = p3.upscale(overworldRight[i]);
      overworldUp[i] = p3.upscale(overworldUp[i]);
      overworldDown[i] = p3.upscale(overworldDown[i]);
    }
  }
}
