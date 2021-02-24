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

    Tile bushTurkey = new Tile(p3.loadImage(Globals.assetspath+"Characters/BushTurkey/bushTurkey.png"),true,false,p3);

    Tile turkeyBattle = new Tile(new LandBehaviour() {
      public void activate(Tile t, UniGame p3) {
        cutsceneTurkeyBattleOne();
      }
    }
    );

    Tile Cockatoo = new Tile(p3.loadImage(Globals.assetspath+"Characters/Cockatoo/Cockatoo.png"),true,false,p3);

    Tile cockatooBattle = new Tile(new LandBehaviour() {
      public void activate(Tile t, UniGame p3) {
        cutsceneCockatooBattleOne();
      }
    }
    );

    Tile ibisHat = new Tile(p3.loadImage(Globals.assetspath+"Characters/Ibis/ibis_hat_right.png"), true, false, p3);

    Tile ibisHatBattle = new Tile(new LandBehaviour() {
      public void activate(Tile t, UniGame p3) {
        cutsceneIbisHatBattleOne();
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

    tiles[8][12] = bushTurkey;
    tiles[7][12] = turkeyBattle;
    tiles[8][11] = turkeyBattle;
    tiles[8][13] = turkeyBattle;
    tiles[9][12] = turkeyBattle;
    tiles[9][13] = turkeyBattle;

    tiles[7][5] = Cockatoo;
    tiles[6][5] = cockatooBattle;
    tiles[7][4] = cockatooBattle;
    tiles[7][6] = cockatooBattle;
    tiles[8][5] = cockatooBattle;
    tiles[8][6] = cockatooBattle;

    tiles[4][8] = ibisHat;
    tiles[3][8] = ibisHatBattle;
    tiles[4][7] = ibisHatBattle;
    tiles[4][9] = ibisHatBattle;
    tiles[5][8] = ibisHatBattle;
    tiles[5][9] = ibisHatBattle;
    
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


  //-------------------------HAT IBIS CUT SCENES-------------------------


  public void cutsceneIbisHatBattleOne() {
    Globals.textManager.printText(
      new String[]{
      "IBIS: squarrrkkkk", 
      "(He looks very stylish)"}, 
      new Lambda(){
        public void activate(){
          cutsceneIbisHatBattleTwo();
  }
  });
  }
  
  public void cutsceneIbisHatBattleTwo() {
    Globals.battleManager.battle(Globals.battleUnits.playerBattleUnit, Globals.battleUnits.hatIbis, 
      new Lambda() {
      public void activate() {
        cutsceneIbisHatBattleThree();
      }
    }
    );
  }
  
  public void cutsceneIbisHatBattleThree() {
    //if player lost
    if (Globals.battleManager.playerUnit.currentHP == 0) {
      Globals.textManager.printText(
        new String[]{
        "IBIS: squarrrkkkk squarrrkkkk", 
        "(It seems happy with itself..)"}, 
        new Lambda(){
          public void activate(){
            cutsceneIbisHatBattleFour();
    }
  }
  );
  }         else {
    Globals.textManager.printText(
      new String[]{
      "IBIS: squarrrkkk", 
      "(It seems dejected..)"}, 
      new Lambda(){
        public void activate(){
          cutsceneIbisHatBattleFour();
  }
  });
  }
  }
  
  public void cutsceneIbisHatBattleFour() {
    Globals.fadeManager.fade(new Lambda() {
      public void activate() {
        tiles[4][8] = null;
        tiles[3][8] = null;
        tiles[4][7] = null;
        tiles[4][9] = null;
        tiles[5][8] = null;
        tiles[5][9] = null;
      }
    }
    , 
      new Lambda() {
      public void activate() {
        cutsceneIbisHatBattleFive();
      }
    }
    );
  }
  
  
  public void cutsceneIbisHatBattleFive() {
    Globals.textManager.printText(new String[]{
      "(The ibis ran off)" 
      });
  }
  

//----------BUSH TURKEY CUT SCENES----------
public void cutsceneTurkeyBattleOne() {
    Globals.textManager.printText(
      new String[]{
      "TURKEY: cAWWW!", 
      "(He looks very angry)"}, 
      new Lambda(){
        public void activate(){
          cutsceneTurkeyBattleTwo();
  }
  });
  }
  
  public void cutsceneTurkeyBattleTwo() {
    Globals.battleManager.battle(Globals.battleUnits.playerBattleUnit, Globals.battleUnits.angryTurkey, 
      new Lambda() {
      public void activate() {
        cutsceneTurkeyBattleThree();
      }
    }
    );
  }
  
  public void cutsceneTurkeyBattleThree() {
    //if player lost
    if (Globals.battleManager.playerUnit.currentHP == 0) {
      Globals.textManager.printText(
        new String[]{
        "TURKEY: cacAWWWW, cacaAWWW", 
        "(It seems alright with itself..)"}, 
        new Lambda(){
          public void activate(){
            cutsceneTurkeyBattleFour();
    }
  }
  );
  }         else {
    Globals.textManager.printText(
      new String[]{
      "TURKEY: cacawww", 
      "(It seems annoyed..)"}, 
      new Lambda(){
        public void activate(){
          cutsceneTurkeyBattleFour();
  }
  });
  }
  }

  public void cutsceneTurkeyBattleFour() {
    Globals.fadeManager.fade(new Lambda() {
      public void activate() {
        Globals.world.outside.tiles[8][12] = null;
        Globals.world.outside.tiles[7][12] = null;
        Globals.world.outside.tiles[8][11] = null;
        Globals.world.outside.tiles[8][13] = null;
        Globals.world.outside.tiles[9][12] = null;
        Globals.world.outside.tiles[9][13] = null;
      }
    }
    , 
      new Lambda() {
      public void activate() {
        cutsceneTurkeyBattleFive();
      }
    }
    );
  }
  
  
  public void cutsceneTurkeyBattleFive() {
    Globals.textManager.printText(new String[]{
      "(The turkey ran off)" 
      });
  }

//----------------------COCKATOO CUT SCENES------------------
  public void cutsceneCockatooBattleOne() {
    Globals.textManager.printText(
      new String[]{
      "COCKATOO: ?", 
      "(He looks very cocky)"}, 
      new Lambda(){
        public void activate(){
          cutsceneCockatooBattleTwo();
  }
  });
  }
  
  public void cutsceneCockatooBattleTwo() {
    Globals.battleManager.battle(Globals.battleUnits.playerBattleUnit, Globals.battleUnits.cockyCockatoo, 
      new Lambda() {
      public void activate() {
        cutsceneCockatooBattleThree();
      }
    }
    );
  }
  
  public void cutsceneCockatooBattleThree() {
    //if player lost
    if (Globals.battleManager.playerUnit.currentHP == 0) {
      Globals.textManager.printText(
        new String[]{
        "COCKATOO: screeeech", 
        "(It seems confused with itself..)"}, 
        new Lambda(){
          public void activate(){
            cutsceneCockatooBattleFour();
    }
  }
  );
  }         else {
    Globals.textManager.printText(
      new String[]{
      "COCKATOO: Hello?", 
      "(Did it just talk to me?)"}, 
      new Lambda(){
        public void activate(){
          cutsceneCockatooBattleFour();
  }
  });
  }
  }

  public void cutsceneCockatooBattleFour() {
    Globals.fadeManager.fade(new Lambda() {
      public void activate() {
        Globals.world.outside.tiles[7][5] = null;
        Globals.world.outside.tiles[6][5] = null;
        Globals.world.outside.tiles[7][4] = null;
        Globals.world.outside.tiles[7][6] = null;
        Globals.world.outside.tiles[8][5] = null;
        Globals.world.outside.tiles[8][6] = null;
      }
    }
    , 
      new Lambda() {
      public void activate() {
        cutsceneCockatooBattleFive();
      }
    }
    );
  }
  
  
  public void cutsceneCockatooBattleFive() {
    Globals.textManager.printText(new String[]{
      "(The cockatoo ran off)" 
      });
  }
}

