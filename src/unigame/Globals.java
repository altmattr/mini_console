package unigame;

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

public class Globals{
    public static Player player;
    public static Attacks attacks;
    public static final String assetspath = "unigame/";
    //edit this to change the scale the game runs at. 1 is 260*160 and each increase causes pixels to become that size
    //(i.e at scale 2 each pixel is rendered as 2x2, at scale 3 each pixel is rendered at 3x3, etc.)
    public static final int scale = 7;
  
}