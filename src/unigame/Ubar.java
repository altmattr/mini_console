package unigame;

class UBar extends Room {

  static final String path = Globals.assetspath+"Tiles/UBar/";
  UniGame p3;
  
  
  public UBar(UniGame p3) {
    super(p3.loadImage(path+"Background.png"), p3);
    this.p3 = p3;
    Tile wall = new Tile();
    Tile overlay = new Tile(p3.loadImage(path+"Overlay.png"), true, false, p3);
    
  
    Tile outsideTeleportOne = new Tile(new LandBehaviour() {
      public void activate(Tile t, UniGame p3) {
        Globals.fadeManager.fade(new Lambda() {
          public void activate() {
            Globals.world.playerX = 15;
            Globals.world.playerY = 8;
            Globals.world.playerDirection = Direction.LEFT;
            Globals.world.currentRoom = Globals.world.outside;
            Globals.world.adjustOffsets();
          }
        }
        );
      }
    }
    );
    
    Tile outsideTeleportTwo = new Tile(new LandBehaviour() {
      public void activate(Tile t, UniGame p3) {
        Globals.fadeManager.fade(new Lambda() {
          public void activate() {
            Globals.world.playerX = 15;
            Globals.world.playerY = 9;
            Globals.world.playerDirection = Direction.LEFT;
            Globals.world.currentRoom = Globals.world.outside;
            Globals.world.adjustOffsets();
          }
        }
        );
      }
    }
    );
  
    Tile cutsceneThree = new Tile(new LandBehaviour() {
      public void activate(Tile t, UniGame p3) {
        cutsceneThreePartOne();
      }
    }
    );
  
    Tile patron = new Tile(p3.loadImage(Globals.assetspath+"Characters/Patron/patron_up.png"), false, 
      new PressBehaviour() {
      public void activate(Tile t, UniGame p3) {
        cutscenePatronBattleOne();
      }
    }
    , true, p3);
  
  
    Tile ubarSign = new Sign("It's the sign for the campus bar", p3);
    Tile poster = new Sign("There are posters for upcoming events at the bar", p3);
  
    Tile npc0 = new Tile(p3.loadImage(Globals.assetspath+"Characters/SchoolKids/schoolkid2_right.png"), false, 
      new TurnToFace(
      Globals.assetspath+"Characters/SchoolKids/schoolkid2", 
      new Lambda() {
      public void activate() {
        Globals.textManager.printText(new String[]{
          "PATRON: I’m coming back next week for the comedy night"
          });
      };
    }
    ), true, p3);
    
    Tile npc1 = new Sign(p3.loadImage(Globals.assetspath+"Characters/NPCs/npc3_up.png"),"(They're enjoying their burger.)", p3);
    Tile npc2 = new Sign(p3.loadImage(Globals.assetspath+"Characters/NPCs/npc2_down.png"),"PATRON: Let’s have a game after the exam!", p3);
    Tile npc3 = new Sign(p3.loadImage(Globals.assetspath+"Characters/NPCs/npc4_left.png"),"(They’re chatting about pool strategies)", p3);
    
    Tile npc4 = new Tile(p3.loadImage(Globals.assetspath+"Characters/NPCs/npc3_up.png"), false, 
      new TurnToFace(
      Globals.assetspath+"Characters/NPCs/npc3", 
      new Lambda() {
      public void activate() {
        Globals.textManager.printText(new String[]{
          "MACS MEMBER: The MACS society is awesome, they have a discord channel too"
          });
      };
    }
    ), true, p3);
    
    Tile npc5 = new Tile(p3.loadImage(Globals.assetspath+"Characters/NPCs/npc4_up.png"), false, 
      new TurnToFace(
      Globals.assetspath+"Characters/NPCs/npc4", 
      new Lambda() {
      public void activate() {
        Globals.textManager.printText(new String[]{
          "MACS MEMBER: I met some really cool friends in this society"
          });
      };
    }
    ), true, p3);
    
    Tile npc6 = new Tile(p3.loadImage(Globals.assetspath+"Characters/NPCs/npc1_down.png"), false, 
      new TurnToFace(
      Globals.assetspath+"Characters/NPCs/npc1", 
      new Lambda() {
      public void activate() {
        Globals.textManager.printText(new String[]{
          "MACS MEMBER: ‘MACS’ stands for the Macquarie Association of Computing Students"
          });
      };
    }
    ), true, p3);
  
    int[][] walls = {{0, 2}, {1, 2}, {2, 2}, {3, 2}, {4, 2}, {5, 2}, {6, 2}, {7, 2}, {8, 2}, {9, 2}, {10, 2}, {11, 2}, {12, 2}, 
      {13, 2}, {14, 2}, {15, 2}, {16, 2}, {17, 2}, {1, 3}, {0, 4}, {1, 5}, {2, 5}, {3, 5}, {4, 5}, {1, 6}, {2, 6}, {3, 6}, 
      {4, 6}, {0, 7}, {1, 7}, {2, 7}, {3, 7}, {4, 7}, {4, 8}, {9, 5}, {10, 5}, {9, 6}, {10, 6}, {9, 7}, {10, 7}, {9, 8}, 
      {10, 8}, {9, 9}, {10, 9}, {8, 9}, {10, 10}, {17, 3}, {17, 4}, {17, 5}, {17, 6}, {17, 7}, {17, 8}, {17, 9}, {17, 10}, 
      {17, 11}, {17, 12}, {17, 13}, {17, 14}, {16, 14}, {15, 14}, {14, 14}, {14, 15}, {13, 16}, {12, 17}, {9, 15}, {10, 15}, 
      {11, 15}, {9, 16}, {10, 16}, {11, 16}, {8, 17}, {7, 17}, {6, 16}, {6, 15}, {5, 15}, {4, 14}, {3, 14}, {2, 14}, {2, 13}, 
      {2, 12}, {1, 12}, {0, 11}, {0, 10}, {16, 5}, {16, 6}, {16, 7}, {16, 8}, {16, 9}, {4, 11}, {5, 11}, {4, 12}, {5, 12}, {6, 5}, 
      {7, 5}, {6, 6}, {7, 6}, {6, 7}, {7, 7}, {12, 5}, {13, 5}, {14, 5}, {12, 6}, {13, 6}, {14, 6}, {12, 9}, {13, 9}, {14, 9}, {12, 8}, 
      {13, 8}, {14, 8}};

    for (int[] arr : walls) {
      tiles[arr[0]][arr[1]] = wall;
    }

    tiles[tiles.length-1][tiles[0].length-1] = overlay;

    tiles[0][8] = outsideTeleportOne;
    tiles[0][9] = outsideTeleportTwo;

    tiles[1][8] = cutsceneThree;
    tiles[1][9] = cutsceneThree;
    
    tiles[7][3] = patron; 
    
    tiles[2][7] = ubarSign;
    tiles[9][9] = poster;
    tiles[3][12] = npc0;
    tiles[5][13] = npc1;
    tiles[6][14] = npc2;
    tiles[7][15] = npc3;
    tiles[13][7] = npc4;
    tiles[14][7] = npc5;
    tiles[12][4] = npc6;
  }

  public void cutsceneThreePartOne() {
    Globals.fadeManager.fade(new Lambda() {
      public void activate() {
        Globals.world.ubar.tiles[Globals.world.playerX+1][Globals.world.playerY] = new Tile(p3.loadImage(Globals.assetspath+"Characters/Karen/karen_left.png"), true, true, p3);
      }
    }
    , 
      new Lambda() {
      public void activate() {
        cutsceneThreePartTwo();
      }
    }
    );
  }
  
  public void cutsceneThreePartTwo() {
    Globals.textManager.printText(new String[]{
      "KAREN: What took you so long? I'm so hungry. Let's go find a table." 
      }, new Lambda(){
        public void activate(){
          cutsceneThreePartThree();
  }
  }
  );
  }
  
  public void cutsceneThreePartThree() {  
    Globals.fadeManager.fade(new Lambda() {
      public void activate() {
        Globals.world.ubar.tiles[1][8] = null;
        Globals.world.ubar.tiles[1][9] = null;
        Globals.world.ubar.tiles[Globals.world.playerX+1][Globals.world.playerY] = null;
  
        Globals.world.ubar.tiles[13][10] = new Tile(p3.loadImage(Globals.assetspath+"Characters/Karen/karen_left.png"), false, 
          new TurnToFace(
          Globals.assetspath+"Characters/Karen/karen", 
          new Lambda() {
          public void activate() {
            Globals.textManager.printText(new String[]{
              "KAREN: Go get us some pizza, you can order at the bar"
              });
          };
        }
        ), true, p3);
  
        Globals.world.playerX = 12;
        Globals.world.playerY = 10;
        Globals.world.playerDirection = Direction.RIGHT;
        Globals.world.adjustOffsets();
      }
    }
    , new Lambda() {
      public void activate() {
        Globals.textManager.printText(new String[]{
          "KAREN: Looks like theres some kind of society meetup, but the sooner we get food, the sooner we can get on to doing my assignment- uh I mean working together. I’ll mind the seats, go get us some pizza."
          });
      }
    }
    );
  }
  public void cutscenePatronBattleOne() {
    Globals.textManager.printText(
      new String[]{
      "PATRON: Soooo many options. How will I ever decide?", 
      "(The bartender looks annoyed with the disorderly ubar patron holding up the queue)", 
      "PATRON: I’ll get back to the meetup when I’ve made up my mind, don’t rush me."}, 
  
      new Lambda(){
        public void activate(){
          cutscenePatronBattleTwo();
  }
  });
  }
  
  public void cutscenePatronBattleTwo() {
    Globals.battleManager.battle(Globals.battleUnits.playerBattleUnit, Globals.battleUnits.patron, 
      new Lambda() {
      public void activate() {
        cutscenePatronBattleThree();
      }
    }
    );
  }
  
  public void cutscenePatronBattleThree() {
    switch(Globals.world.playerDirection) {
    case UP:
      Globals.world.ubar.tiles[7][3].setAppearance(p3.loadImage(Globals.assetspath+"Characters/Patron/patron_down.png"), p3);
      break;
    case LEFT:
      Globals.world.ubar.tiles[7][3].setAppearance(p3.loadImage(Globals.assetspath+"Characters/Patron/patron_right.png"), p3);
      break;    
    case RIGHT:
      Globals.world.ubar.tiles[7][3].setAppearance(p3.loadImage(Globals.assetspath+"Characters/Patron/patron_left.png"), p3);
      break;    
    case DOWN:
      break;
    }
    if (Globals.battleManager.playerUnit.currentHP == 0) {
      Globals.textManager.printText(
        new String[]{
        "PATRON: That was fun, I’ll think about what I want for a bit and come back"}, 
        new Lambda(){
          public void activate(){
            cutscenePatronBattleFour();
    }
  }
  );
  }  else {
    Globals.textManager.printText(
      new String[]{
      "PATRON: Fine, I’ll come back when I figure out what I want"}, 
      new Lambda(){
        public void activate(){
          cutscenePatronBattleFour();
  }
  });
  }
  }
  
  public void cutscenePatronBattleFour() {
    Globals.fadeManager.fade(new Lambda() {
      public void activate() {
        Globals.world.ubar.tiles[7][3] = null;
        Globals.world.ubar.tiles[13][4] = new Tile(p3.loadImage(Globals.assetspath+"Characters/Patron/patron_down.png"), false, 
          new TurnToFace(
          Globals.assetspath+"Characters/Patron/patron", 
          new Lambda() {
          public void activate() {
            Globals.textManager.printText(new String[]{
              "PATRON: Maybe I should get the vegetarian pizza… but the margarita looks so good…"
              });
          };
        }
        ), true, p3);
  
        //Making it so that talking to karen triggers the next cutscene
        Globals.world.ubar.tiles[13][10].pb = new TurnToFace(
          Globals.assetspath+"Characters/Karen/karen", 
          new Lambda() {
          public void activate() {
            cutsceneFourPartOne();
          }
        }
        );
  
        Globals.world.playerX = 7;
        Globals.world.playerY = 3;
        Globals.world.playerDirection = Direction.UP;
        Globals.world.adjustOffsets();
      }
    }
    , new Lambda() {
      public void activate() {
        cutscenePatronBattleFive();
      }
    }
    );
  }
  
  public void cutscenePatronBattleFive() {
    Globals.textManager.printText(new String[]{
      "BARTENDER: Thanks for helping move her along, here’s your food", 
      "(I should head back to Karen)"
      });
  }

  public void cutsceneFourPartOne() {
    Globals.textManager.printText(new String[]{
      "KAREN: Nice work, let’s eat!", 
      "Mmmhmm delicious.", 
      "…", 
      "Okay, let’s go somewhere more quiet, I’ll meet you at the library - don’t keep me waiting again."
      }, new Lambda(){
        public void activate(){
          cutsceneFourPartTwo();
  }
  });
  }
  
  public void cutsceneFourPartTwo() {
    Globals.fadeManager.fade(new Lambda() {
      public void activate() {
        Globals.world.ubar.tiles[13][10] = null;
        Tile outsideToLibrary = new Tile(new LandBehaviour() {
          public void activate(Tile t, UniGame p3) {
            Globals.fadeManager.fade(new Lambda() {
              public void activate() {
                Globals.world.playerX = 7;
                Globals.world.playerY = 9;
                Globals.world.playerDirection = Direction.UP;
                Globals.world.currentRoom = Globals.world.library;
                Globals.world.adjustOffsets();
              }
            }
            );
          }
        }
        );
        Globals.world.outside.tiles[10][13] = outsideToLibrary;
        Globals.world.outside.tiles[11][13] = outsideToLibrary;
      }
    }
    );
  }
  
}
