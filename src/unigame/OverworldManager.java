package unigame;

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

class OverworldManager {
  public boolean locked = false;

  public Room pcLab;
  public Room outside;
  public Room ubar;
  public Room library;
  public Room currentRoom;
  public Direction playerDirection = Direction.RIGHT;
  public TextManager text;

  UniGame p3;
  
  public Lambda afterSleep;
  


  //Player starts the game facing right, at [5][6]
  //Rendering magic below
  public int playerX = 5;
  public int playerY = 6;
  public int playerDrawX = (playerX)*16; 
  public int playerDrawY = (playerY-1)*16;

  //player render offset based on the sprite resolution
  final int offsetX = 240/2-8;
  final int offsetY = 160/2-24;

  int worldSleep = 0;
  private PGraphics scene;
  

  public OverworldManager(UniGame p3) {
    this.p3 = p3;
    pcLab  = new PCLab(p3);
    outside = new Outside(p3);
    text = new TextManager(p3);
    ubar = new UBar(p3);
    library = new Library(p3);
    scene = p3.createGraphics(240*Globals.scale,160*Globals.scale);
    currentRoom = pcLab;
    drawOverworld();
  };

 
  int animationState = 0;

  public void process() {
    if(worldSleep == 1){
     worldSleep = 0;
     afterSleep.activate();
     return;
    }else if(worldSleep > 0){
     --worldSleep; 
    }else if (animationState%8 != 0) {
      movePlayer();
    } else {
      processInput();
    }
    drawOverworld();
  }

  public PGraphics getScene(){
    return scene;
  }
  
  public void setScene(PGraphics scene){
    if(scene != null){
      this.scene = scene; 
    }
  }

  public void drawOverworld() {
    //draw a black background
    //scene = createGraphics(240, 160);
    scene.beginDraw();
    scene.background(0);

    //draw the current background plate, offset by the player location 
    scene.image(currentRoom.background, (offsetX-playerDrawX)*Globals.scale, (offsetY-playerDrawY)*Globals.scale);

    //iterate through active elements, drawing the tiles below the player
    for (int i = 0; i < currentRoom.tiles[0].length; i++) {
      for (int j = 0; j < currentRoom.tiles.length; ++j) {
        if (currentRoom.tiles[j][i] != null && !currentRoom.tiles[j][i].displayOver) {
          drawTile(currentRoom.tiles[j][i], j, i);
        }
        if(i == playerY && j == playerX) drawPlayer();
      }
    }
    //then draw the tiles that should display above the player
    for (int i = 0; i < currentRoom.tiles[0].length; i++) {
      for (int j = 0; j < currentRoom.tiles.length; ++j) {
        if (currentRoom.tiles[j][i] != null && currentRoom.tiles[j][i].displayOver) {
          drawTile(currentRoom.tiles[j][i], j, i);
        }
      }
    }
    scene.endDraw();
    p3.image(scene, 0, 0);
  }


  public void drawTile(Tile t, int x, int y) {
    int tileXPos = offsetX-playerDrawX-t.offsetX+x*16;
    int tileYPos = offsetY-playerDrawY-t.offsetY+y*16;
    scene.image(t.appearance, tileXPos*Globals.scale, tileYPos*Globals.scale);
  }

  public void drawPlayer() {
    switch(playerDirection) {
    case UP: 
      scene.image(Globals.player.overworldUp[animationState/4], offsetX*Globals.scale, offsetY*Globals.scale); 
      break;
    case DOWN: 
      scene.image(Globals.player.overworldDown[animationState/4], offsetX*Globals.scale, offsetY*Globals.scale); 
      break;
    case LEFT: 
      scene.image(Globals.player.overworldLeft[animationState/4], offsetX*Globals.scale, offsetY*Globals.scale); 
      break;
    case RIGHT: 
      scene.image(Globals.player.overworldRight[animationState/4], offsetX*Globals.scale, offsetY*Globals.scale); 
      break;
    }
  }

  public void movePlayer() {
    ++animationState; 
    animationState%=16;
    switch(playerDirection) {
    case UP:
      playerDrawY-=2; 
      break;
    case DOWN:
      playerDrawY+=2; 
      break;
    case LEFT:
      playerDrawX-=2; 
      break;
    case RIGHT:
      playerDrawX+=2; 
      break;
    }
    if (animationState%8 == 0) {
      currentRoom.processLanding(playerX, playerY);
    }
  }

  public void processInput() {
    switch(Globals.keyPressManager.getKey()) {
    case UP:
      playerDirection = Direction.UP;
      if (validDirection()) {     
        --playerY;
        movePlayer();
      } 
      break;
    case DOWN:
      playerDirection = Direction.DOWN; 
      if (validDirection()) {
        ++playerY;
        movePlayer();
      } 
      break;
    case LEFT:
      playerDirection = Direction.LEFT;
      if (validDirection()) {
        --playerX;
        movePlayer();
      } 
      break;
    case RIGHT:
      playerDirection = Direction.RIGHT;
      if (validDirection()) { 
        ++playerX;
        movePlayer();
      } 
      break;
    case A:
      processInteraction(); 
      break;
    default:
      break;
    }
  }

  public boolean validDirection() {
    int candX = playerX;
    int candY = playerY;
    switch(playerDirection) {
    case UP:
      --candY; 
      break;
    case DOWN:
      ++candY; 
      break;
    case LEFT:
      --candX; 
      break;
    case RIGHT:
      ++candX; 
      break;
    }
    return currentRoom.validDirection(candX, candY);
  }

  public void processInteraction() {
    int candX = playerX;
    int candY = playerY;
    switch(playerDirection) {
    case UP:
      --candY; 
      break;
    case DOWN:
      ++candY; 
      break;
    case LEFT:
      --candX; 
      break;
    case RIGHT:
      ++candX; 
      break;
    }
    currentRoom.processInteraction(candX, candY);
  }
  
  public void sleepWorld(int worldSleep){
    Globals.gameStateManager.setState(State.WORLD);
    this.worldSleep = worldSleep;
    this.afterSleep = new Lambda(){ public void activate(){}};
  }
  
  public void sleepWorld(int worldSleep, Lambda afterSleep){
    Globals.gameStateManager.setState(State.WORLD);
    this.worldSleep = worldSleep;
    this.afterSleep = afterSleep;
  }
  
  public void adjustOffsets(){
    playerDrawX = (playerX)*16; 
    playerDrawY = (playerY-1)*16; 
  }
  
}
