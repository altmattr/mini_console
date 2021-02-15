package unigame;

class PCLab extends Room {
    static final String path = Globals.assetspath+"Tiles/PCLab/";
  
    public PCLab(UniGame p3) {
      super(p3.loadImage(path+"Background.png"), p3);

      Tile wall = new Tile();
  
      Tile teacher = new Tile(p3.loadImage(Globals.assetspath+"Characters/Teacher/teacher_down.png"), false, 
        new TurnToFace(
        Globals.assetspath+"Characters/Teacher/teacher", 
        new Lambda() {
        public void activate() {
          Globals.textManager.printText(new String[]{
            "TEACHER: Don’t forget to hand the assignment in when it’s finished"
            });
        };
      }
      ), true, p3);
    
      Tile karen = new Tile(p3.loadImage(Globals.assetspath+"Characters/Karen/karen_left.png"), false, 
        new PressBehaviour() {
        public void activate(Tile t, UniGame p3) {
          Globals.textManager.printText(new String[]{
            "KAREN: Stupid program isnt saving… I’ll catch up with you later"
            });
        }
      }
      , true, p3);
    
      Tile dev = new Tile(p3.loadImage(Globals.assetspath+"Characters/Dev/dev_left.png"), false, 
        new TurnToFace(
        Globals.assetspath+"Characters/Dev/dev", 
        new Lambda() {
        public void activate() {
          Globals.textManager.printText(new String[]{
            "DEV: I’m really struggling, I should have asked for help earlier…"
            });
        };
      }
      ), true, p3);
      
      Tile bin = new Sign(new String[]{"It's just trash"}, p3);
      Tile projector = new Sign(new String[]{"So many buttons, but none of them will solve my assignment for me"}, p3);
      Tile karenspc = new Sign(new String[]{"She forgot to log out..."}, p3);
      Tile cutsceneTwo = new Tile(new LandBehaviour(){
        public void activate(Tile t, UniGame p3){
          cutsceneTwoPartOne(p3);  
        }
      });
      
      Tile npc3 = new Sign(p3.loadImage(Globals.assetspath+"Characters/NPCs/npc3_right.png"),"(Looks like they're busy)", p3);
      Tile npc4 = new Sign(p3.loadImage(Globals.assetspath+"Characters/NPCs/npc4_right.png"),"(Looks like they're busy)", p3);
      
      Tile outsideTeleport = new Tile(new LandBehaviour(){
        public void activate(Tile t, UniGame p3){
           Globals.fadeManager.fade(new Lambda(){
             public void activate(){
               Globals.world.playerX = 10;
               Globals.world.playerY = 4;
               Globals.world.playerDirection = Direction.DOWN;
               Globals.world.currentRoom = Globals.world.outside;
               Globals.world.adjustOffsets();
             }
           });
        }
      });
  
      //Setting up all the walls so you can't walk on tables
      int[][] wallLocations = {{0, 2}, {1, 2}, {2, 2}, {3, 2}, {4, 2}, {5, 2}, {6, 2}, {7, 2}, 
        {9, 2}, {10, 2}, {11, 2}, {12, 3}, {12, 5}, {1, 5}, {1, 6}, {1, 7}, {1, 8}, 
        {2, 5}, {2, 6}, {2, 7}, {2, 8}, {6, 5}, {6, 6}, {6, 7}, {6, 8}, {7, 5}, 
        {7, 6}, {7, 7}, {7, 8}, {11, 5}, {11, 6}, {11, 7}, {11, 8}};
      for (int[] arr : wallLocations) {
        tiles[arr[0]][arr[1]] = wall;
      }
      
      tiles[3][7] = karen;
      tiles[6][3] = teacher;
      tiles[8][6] = dev;
  
      tiles[8][2] = projector;
      tiles[11][3] = bin;
      tiles[2][7] = karenspc;
      tiles[11][4] = cutsceneTwo;
      tiles[12][4] = outsideTeleport;
      
      tiles[0][5] = npc3;
      tiles[10][8] = npc4;
    }

    public void cutsceneTwoPartOne(UniGame p3) {
        Globals.fadeManager.fade(new Lambda() {
          public void activate() {
            Globals.world.pcLab.tiles[3][7] = null;
            Globals.world.pcLab.tiles[10][4] = new Tile(p3.loadImage(Globals.assetspath+"Characters/Karen/karen_right.png"), false, false, p3);
            Globals.world.playerDirection = Direction.LEFT;
          }
        }
        , 
          new Lambda() {
          public void activate() {
            cutsceneTwoPartTwo();
          }
        }
        );
      }
      
      public void cutsceneTwoPartTwo() {
        Globals.textManager.printText(
          new String[]{
          "KAREN: Hey "+Globals.player.name+"! You haven't started either - perfect! Let’s go get some lunch while we figure out how to do this assignment. With the two of us working together we’ll get it done in no time at all! Plus, I haven’t seen any of the lectures yet, I’m stumped. I’ll meet you at Ubar!"
          }, 
          new Lambda(){
            public void activate(){
              cutsceneTwoPartThree();
      }
      });
      }
      
      public void cutsceneTwoPartThree() {
        Globals.fadeManager.fade(new Lambda() {
          public void activate() {
            Globals.world.pcLab.tiles[10][4] = null;
          }
        }
        , 
          new Lambda() {
          public void activate() {
            cutsceneTwoPartFour();
          }
        }
        );
      }
      
      public void cutsceneTwoPartFour() {
        Globals.textManager.printText(
          new String[]{
          "(She dashed off before I had the chance to answer, typical Karen…)", 
          "(I am hungry though so I should head that way anyway)"
          }, 
          new Lambda(){
            public void activate(){
              Globals.world.pcLab.tiles[11][4] = null;
        Globals.gameStateManager.setState(State.WORLD);
      }
      });
      }
      
      
  }
  