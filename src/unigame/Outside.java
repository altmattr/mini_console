package unigame;

class Outside extends Room {

  static final String path = Globals.assetspath+"Tiles/Outside/";


  public Outside(UniGame p3) {
    super(p3.loadImage(path+"Background.png"), p3);
    Tile wall = new Tile();
    Tile overlay = new Tile(p3.loadImage(path+"Overlay.png"), true, false, p3);
  
    Tile ibis = new Tile(p3.loadImage(Globals.assetspath+"Characters/Ibis/ibis_left.png"), true, false, p3);
  
    Tile ibisBattle = new Tile(new LandBehaviour() {
      public void activate(Tile t, UniGame p3) {
        cutsceneIbisBattleOne();
      }
    }
    );
  
    Tile pcLabTeleport = new Tile(new LandBehaviour() {
      public void activate(Tile t, UniGame p3) {
        Globals.fadeManager.fade(new Lambda() {
          public void activate() {
            Globals.world.playerX = 12;
            Globals.world.playerY = 4;
            Globals.world.playerDirection = Direction.LEFT;
            Globals.world.currentRoom = Globals.world.pcLab;
            Globals.world.adjustOffsets();
          }
        }
        );
      }
    }
    );
  
    Tile ubarTeleportOne = new Tile(new LandBehaviour() {
      public void activate(Tile t, UniGame p3) {
        Globals.fadeManager.fade(new Lambda() {
          public void activate() {
            Globals.world.playerX = 0;
            Globals.world.playerY = 8;
            Globals.world.playerDirection = Direction.RIGHT;
            Globals.world.currentRoom = Globals.world.ubar;
            Globals.world.adjustOffsets();
          }
        }
        );
      }
    }
    );
    Tile ubarTeleportTwo = new Tile(new LandBehaviour() {
      public void activate(Tile t, UniGame p3) {
        Globals.fadeManager.fade(new Lambda() {
          public void activate() {
            Globals.world.playerX = 0;
            Globals.world.playerY = 9;
            Globals.world.playerDirection = Direction.RIGHT;
            Globals.world.currentRoom = Globals.world.ubar;
            Globals.world.adjustOffsets();
          }
        }
        );
      }
    }
    );
    
    Tile noLibraryYet = new Tile(new LandBehaviour() {
      public void activate(Tile t, UniGame p3) {
        Globals.fadeManager.fade(new Lambda() {
          public void activate() {
            Globals.world.playerY = Globals.world.playerY -1;
            Globals.world.playerDirection = Direction.UP;
            Globals.world.adjustOffsets();
          }
        }
        , 
          new Lambda() {
          public void activate() {
            Globals.textManager.printText(new String[]{"(I should eat before going the the library, I'm hungry)"});
          }
        }
        );
      }
    }
    );
  
    int[][] walls = {{2, 4}, {3, 4}, {4, 4}, {5, 4}, {6, 4}, {7, 4}, {8, 4}, {9, 4}, 
      {12, 4}, {13, 4}, {14, 5}, {15, 6}, {15, 7}, {16, 8}, {16, 9}, 
      {16, 10}, {15, 11}, {14, 12}, {13, 13}, {12, 14}, {2, 14}, {3, 14}, 
      {4, 14}, {5, 14}, {6, 14}, {7, 14}, {8, 14}, {9, 14}, {2, 5}, {2, 6}, 
      {2, 7}, {2, 8}, {2, 9}, {2, 10}, {2, 11}, {2, 12}, {2, 13}, {10, 3}, {11, 3}, {15, 10}};
    for (int[] arr : walls) {
      tiles[arr[0]][arr[1]] = wall;
    }

    tiles[tiles.length-1][tiles[0].length-1] = overlay;

    tiles[10][4] = pcLabTeleport;
    tiles[11][4] = pcLabTeleport;

    tiles[13][8] = ibis;
    tiles[12][8] = ibisBattle;
    tiles[13][7] = ibisBattle;
    tiles[13][9] = ibisBattle;
    tiles[14][8] = ibisBattle;
    tiles[14][9] = ibisBattle;
    
    tiles[10][13] = noLibraryYet;
    tiles[11][13] = noLibraryYet;
    
    tiles[15][8] = ubarTeleportOne;
    tiles[15][9] = ubarTeleportTwo;
  }

  public void cutsceneIbisBattleOne() {
    Globals.textManager.printText(
      new String[]{
      "IBIS: squawwwkkkk", 
      "(He looks very hungry)"}, 
      new Lambda(){
        public void activate(){
          cutsceneIbisBattleTwo();
  }
  });
  }
  
  public void cutsceneIbisBattleTwo() {
    Globals.battleManager.battle(Globals.battleUnits.playerBattleUnit, Globals.battleUnits.hungryIbis, 
      new Lambda() {
      public void activate() {
        cutsceneIbisBattleThree();
      }
    }
    );
  }
  
  public void cutsceneIbisBattleThree() {
    //if player lost
    if (Globals.battleManager.playerUnit.currentHP == 0) {
      Globals.textManager.printText(
        new String[]{
        "IBIS: squawwwkkkk squawwwkkkk", 
        "(It seems happy with itself..)"}, 
        new Lambda(){
          public void activate(){
            cutsceneIbisBattleFour();
    }
  }
  );
  }         else {
    Globals.textManager.printText(
      new String[]{
      "IBIS: squawwkkk", 
      "(It seems dejected..)"}, 
      new Lambda(){
        public void activate(){
          cutsceneIbisBattleFour();
  }
  });
  }
  }
  
  public void cutsceneIbisBattleFour() {
    Globals.fadeManager.fade(new Lambda() {
      public void activate() {
        Globals.world.outside.tiles[12][8] = null;
        Globals.world.outside.tiles[13][7] = null;
        Globals.world.outside.tiles[13][8] = null;
        Globals.world.outside.tiles[13][9] = null;
        Globals.world.outside.tiles[14][8] = null;
        Globals.world.outside.tiles[14][9] = null;
      }
    }
    , 
      new Lambda() {
      public void activate() {
        cutsceneIbisBattleFive();
      }
    }
    );
  }
  
  
  public void cutsceneIbisBattleFive() {
    Globals.textManager.printText(new String[]{
      "(The ibis ran off)" 
      });
  }
  
}