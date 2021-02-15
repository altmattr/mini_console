package unigame;

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

class Room {
  PImage background;
  int roomWidth; 
  int roomHeight;
  Tile[][] tiles;
  UniGame p3;
  public Room(PImage background, UniGame p3){
    this.p3 = p3;
    this.background = p3.upscale(background);
    roomWidth = background.width/16;
    roomHeight = background.height/16;
    tiles = new Tile[roomWidth][roomHeight];
  }
  
  public void processLanding(int x, int y){
    if(tiles[x][y] != null){
      tiles[x][y].land(p3); 
    }
  }
  
  public void processInteraction(int x, int y){
    if(x > -1 && x < tiles.length && y > -1 && y < tiles[0].length){
      if(tiles[x][y] != null) tiles[x][y].press(p3); 
    }
  }
  
  public boolean validDirection(int x, int y){
    if(x > -1 && x < tiles.length && y > -1 && y < tiles[0].length){
      if(tiles[x][y] == null || !tiles[x][y].isSolid) return true; 
    }
    return false;
  }
  
  
}
