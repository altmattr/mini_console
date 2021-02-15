package unigame;

class Library extends Room {

    static final String path = Globals.assetspath+"Tiles/Library/";
    UniGame p3;
  
    public Library(UniGame p3) {
      super(p3.loadImage(path+"Background.png"), p3);
      this.p3 = p3;
  
      Tile wall = new Tile();
      Tile overlay = new Tile(p3.loadImage(path+"Overlay.png"), true, false, p3);
    
      Tile karen = new Tile(p3.loadImage(Globals.assetspath+"Characters/Karen/karen_up.png"), false, 
        new TurnToFace(
        Globals.assetspath+"Characters/Karen/karen", 
        new Lambda() {
        public void activate() {
          Globals.textManager.printText(new String[]{
            "KAREN: That table at the back would be perfect, go ask if we can have it."
            });
        };
      }
      ), true, p3);
      
      Tile schoolKidOne = new Tile(p3.loadImage(Globals.assetspath+"Characters/SchoolKids/schoolkid1_right.png"), false, 
        new TurnToFace(
        Globals.assetspath+"Characters/SchoolKids/schoolkid1", 
        new Lambda() {
        public void activate() {
          cutsceneSchoolBattleOne();
        };
      })
      , true, p3);
      
      Tile schoolKidTwo = new Tile(p3.loadImage(Globals.assetspath+"Characters/SchoolKids/schoolkid2_left.png"), false, 
        new TurnToFace(
        Globals.assetspath+"Characters/SchoolKids/schoolkid2", 
        new Lambda() {
        public void activate() {
          cutsceneSchoolBattleOne();
        };
      })
      , true, p3);
      
      Tile schoolKidThree = new Tile(p3.loadImage(Globals.assetspath+"Characters/SchoolKids/schoolkid3_right.png"), false, 
        new TurnToFace(
        Globals.assetspath+"Characters/SchoolKids/schoolkid3", 
        new Lambda() {
        public void activate() {
          cutsceneSchoolBattleOne();
        };
      })
      , true, p3);
      
      Tile karenCutscene = new Tile(new LandBehaviour(){
        public void activate(Tile t, UniGame p3){
          cutsceneFivePartOne(); 
        }
      });
      Tile outsideTeleport = new Tile(new LandBehaviour() {
        public void activate(Tile t, UniGame p3) {
          Globals.fadeManager.fade(new Lambda() {
            public void activate() {
              Globals.world.playerX = 10;
              Globals.world.playerY = 12;
              Globals.world.playerDirection = Direction.UP;
              Globals.world.currentRoom = Globals.world.outside;
              Globals.world.adjustOffsets();
            }
          }
          );
        }
      }
      );
      
      Tile npc0 = new Sign(p3.loadImage(Globals.assetspath+"Characters/SchoolKids/schoolkid3_right.png"),"(They look too invested in their study to talk to you)", p3);
      Tile npc1 = new Sign(p3.loadImage(Globals.assetspath+"Characters/SchoolKids/schoolkid3_right.png"),"(They look too invested in their study to talk to you)", p3);
      Tile npc2 = new Sign(p3.loadImage(Globals.assetspath+"Characters/NPCs/npc1_left.png"),"(They’re looking at colourful statistical diagrams.)", p3);
      Tile npc3 = new Sign(p3.loadImage(Globals.assetspath+"Characters/NPCs/npc2_right.png"),"(They’re taking a buzzfeed quiz, \"What kind of procrastination am I?\")", p3);
      
      Tile clrs = new Sign(new String[]{"You browse the bookshelf and find a book that looks interesting.", "Introduction to Algorithms by Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, and Clifford Stein", "...", "Let's put this back."}, p3);
      
        int[][] walls = {{0,1},{1,1},{2,1},{3,1},{4,1},{5,1},{6,1},{7,1},{8,1},{9,1},{10,1},{11,1},
                      {12,1},{13,1},{14,1},{14,2},{0,4},{1,4},{14,2},{0,4},{1,4},{2,4},{3,4},{4,4},
                      {5,4},{0,7},{1,7},{2,7},{3,7},{4,7},{5,7},{11,3},{12,3},{11,4},{12,4},{14,6},
                      {14,7},{11,8},{12,8},{11,9},{12,9},{1,9},{2,9}};
  
      for (int[] arr : walls) {
        tiles[arr[0]][arr[1]] = wall;
      }
  
      tiles[tiles.length-1][tiles[0].length-1] = overlay;
  
      tiles[7][9] = outsideTeleport;
      tiles[8][9] = outsideTeleport;
      
      tiles[7][8] = karenCutscene;
      tiles[8][8] = karen;
      
      tiles[10][4] = schoolKidOne;
      tiles[13][4] = schoolKidTwo;
      tiles[10][3] = schoolKidThree;
      
      tiles[3][9] = npc0;
      tiles[0][9] = npc1;
      tiles[13][9] = npc2;
      tiles[10][8] = npc3;
      
      tiles[0][1] = clrs;
    }

    public void cutsceneSchoolBattleOne() {
        Globals.textManager.printText(new String[]{
          "The high schoolers glare at you sleepily", 
          }, new Lambda(){
            public void activate(){
              cutsceneSchoolBattleTwo();
      }
      });
      }
      
      
      public void cutsceneSchoolBattleTwo() {
        Globals.battleManager.battle(Globals.battleUnits.playerBattleUnit, Globals.battleUnits.schoolKids, 
          new Lambda() {
          public void activate() {
            cutsceneSchoolBattleThree();
          }
        }
        );
      }
      
      public void cutsceneSchoolBattleThree() {
        if (Globals.battleManager.playerUnit.currentHP == 0) {
          Globals.textManager.printText(
            new String[]{
            "HIGHSCHOOLER: Heh, we’re heading off anyway"}, 
            new Lambda(){
              public void activate(){
                cutsceneSchoolBattleFour();
        }
      }
      );
      }  else {
        Globals.textManager.printText(
          new String[]{
          "HIGHSCHOOLER: Whatever, we were heading off anyway"}, 
          new Lambda(){
            public void activate(){
              cutsceneSchoolBattleFour();
      }
      });
      }
      }
      
      public void cutsceneSchoolBattleFour() {
        Globals.fadeManager.fade(new Lambda() {
          public void activate() {
            Globals.world.library.tiles[10][4] = null;
            Globals.world.library.tiles[13][4] = null;
            Globals.world.library.tiles[10][3] = null;
            Globals.world.library.tiles[8][8] = null;
      
            Globals.world.playerX = 10;
            Globals.world.playerY = 4;
            Globals.world.playerDirection = Direction.RIGHT;
            Globals.world.adjustOffsets();
      
            Globals.world.library.tiles[10][3] = new Tile(p3.loadImage(Globals.assetspath+"Characters/Karen/karen_left.png"), false, true, p3);
          }
        }
        , new Lambda() {
          public void activate() {
            cutsceneSchoolBattleFive();
          }
        }
        );
      }
      
      public void cutsceneSchoolBattleFive() {
        Globals.textManager.printText(new String[]{
          "KAREN: This is the worst - I have no idea what to do, any ideas?" 
          }, new Lambda(){
            public void activate(){
              cutsceneSchoolBattleSix();
      }
      });
      }
      
      public void cutsceneSchoolBattleSix() {
        Globals.world.library.tiles[10][3].setAppearance(p3.loadImage(Globals.assetspath+"Characters/Karen/karen_down.png"), p3);
        Globals.world.drawOverworld();
        Globals.world.sleepWorld(15, new Lambda() {
          public void activate() {
            cutsceneSchoolBattleSeven();
          }
        }
        );
      }
      
      public void cutsceneSchoolBattleSeven() {
        Globals.textManager.printText(new String[]{
          "KAREN: I mean you DID go to the lectures, what’s the harm in sharing what you have so far?" 
          }, new Lambda(){
            public void activate(){
              cutsceneSchoolBattleEight();
      }
      });
      }
      
      public void cutsceneSchoolBattleEight() {
        Globals.world.playerDirection = Direction.UP;
        Globals.world.drawOverworld();
        Globals.world.sleepWorld(15, new Lambda() {
          public void activate() {
            cutsceneSchoolBattleNine();
          }
        }
        );
      }
      
      public void cutsceneSchoolBattleNine() {
        Globals.textManager.printText(new String[]{
          "...", 
          "KAREN: Hey, I was busy with my friends so of course I didn’t have time to start it before today.", 
          "Argh! Just work on your own then I’ll figure something out."
          }, new Lambda(){
            public void activate(){
              cutsceneSchoolBattleTen();
      }
      });
      }
      
      public void cutsceneSchoolBattleTen() {
        Globals.world.playerDirection = Direction.RIGHT;
        Globals.world.library.tiles[10][3].setAppearance(p3.loadImage(Globals.assetspath+"Characters/Karen/karen_right.png"), p3);
        Globals.world.drawOverworld();
        Globals.world.sleepWorld(15, new Lambda() {
          public void activate() {
            cutsceneSchoolBattleEleven();
          }
        }
        );
      }
      
      public void cutsceneSchoolBattleEleven() {
        Globals.textManager.printText(new String[]{
          "(As you type up your answers you can’t help but get the feeling that Karen isn’t really looking at her own screen)" 
          }, new Lambda(){
            public void activate(){
              cutsceneSchoolBattleTwelve();
      }
      });
      }
      
      public void cutsceneSchoolBattleTwelve() {
        Globals.fadeManager.fade(new Lambda() {
          public void activate() {
          };
        }
        , new Lambda() {
          public void activate() {
            cutsceneSchoolBattleThirteen();
          }
        }
        );
      }
      
      public void cutsceneSchoolBattleThirteen() {
        Globals.textManager.printText(new String[]{
          "KAREN: Aha! We finished at the same time - Yay! I’m going to go hand this in right now.", 
          "Yeah sorry I can’t stick around, gotta run!"
          }, new Lambda(){
            public void activate(){
              cutsceneSchoolBattleFourteen();
      }
      });
      }
      
      public void cutsceneSchoolBattleFourteen() {
        Globals.fadeManager.fade(new Lambda() {
          public void activate() {
            Globals.world.library.tiles[10][3] = null;
          }
        }
        , new Lambda() {
          public void activate() {
            Globals.world.pcLab.tiles[6][3] = new Tile(p3.loadImage(Globals.assetspath+"Characters/Teacher/teacher_down.png"), false, 
              new TurnToFace(
              Globals.assetspath+"Characters/Teacher/teacher", 
              new Lambda() {
              public void activate() {
                cutsceneTeacherBattleOne();
              };
            }
            ), true, p3);
            Globals.textManager.printText(new String[]{
              "(I should go back to the classroom to hand this in)"
              });
          }
        }
        );
      }
      
      public void cutsceneTeacherBattleOne() {
        Globals.textManager.printText(new String[]{
          "TEACHER: All finished with the assignment? Good work."
          }, new Lambda(){
            public void activate(){
              cutsceneTeacherBattleTwo();
            }
        });
      }
      
      public void cutsceneTeacherBattleTwo() {
        Globals.fadeManager.fade(new Lambda() {
          public void activate() {
            Globals.world.pcLab.tiles[8][6] = null;
            Globals.world.pcLab.tiles[7][4] = new Tile(p3.loadImage(Globals.assetspath+"Characters/Dev/dev_up.png"), false, true, p3);
          }
        }
        , new Lambda() {
          public void activate() {
            cutsceneTeacherBattleThree();
          }
        }
        );
      }
      
      public void cutsceneTeacherBattleThree() {
        Globals.textManager.printText(new String[]{
          "DEV: I’m finally done, I couldn’t do every task but I’m proud of myself for powering through.", 
          "TEACHER: Well done! I’ll have your feedback uploaded by the end of the week which will help you find which areas need more work.", 
          "DEV: Cheers, see you next week."
          }, new Lambda(){
            public void activate(){
              cutsceneTeacherBattleFour();
          }
          });
      }
      
      public void cutsceneTeacherBattleFour() {
        Globals.fadeManager.fade(new Lambda() {
          public void activate() {
            Globals.world.pcLab.tiles[7][4] = null;
          }
        }
        , new Lambda() {
          public void activate() {
            cutsceneTeacherBattleFive();
          }
        }
        );
      }
      
      public void cutsceneTeacherBattleFive() {
        Globals.textManager.printText(new String[]{
          "TEACHER: Hm, okay "+Globals.player.name+", let’s put this in the system.", 
          "...", 
          "TEACHER: Oh dear... 90% plagiarism.", 
          "…"+Globals.player.name+" this is not good, not only will you not receive any marks but you also will need to be reported to the disciplinary committee. Please explain yourself"
          }, new Lambda(){
            public void activate(){
              cutsceneTeacherBattleSix();
            }
          }
      );
      }
      
      public void cutsceneTeacherBattleSix() {
        Globals.battleManager.battle(Globals.battleUnits.playerBattleUnit, Globals.battleUnits.teacher, 
          new Lambda() {
          public void activate() {
            cutsceneTeacherBattleSeven();
          }
        }
        );
      }
      
      public void cutsceneTeacherBattleSeven() {
        Globals.textManager.printText(new String[]{
          "TEACHER: Okay, I see… So Karen said she would work on her own, but then used your work without acknowledgement. And now it seems you are just as surprised as me. Your honesty is appreciated. I can’t give you any marks - you need to learn to be more careful in the future. But, I won’t need to report you to the higher ups . The point of this small assignment was so that I could give you feedback that will help in the next assessment. That feedback will still be valuable to you as you did the work but Karen wont have the benefit of that. I’ll see you in class next week." 
          }, new Lambda(){
            public void activate(){
              cutsceneTeacherBattleEight();
      }
      });
      }
      
      public void cutsceneTeacherBattleEight() {
        Globals.world.pcLab.tiles[6][3].pb = new TurnToFace(
          Globals.assetspath+"Characters/Teacher/teacher", 
            new Lambda() {
              public void activate() {
                Globals.textManager.printText(new String[]{
                  "TEACHER: See you next week" 
                 });
              }
             }
           );
        Globals.gameStateManager.setState(State.WORLD);
      }
      
      public void cutsceneFivePartOne() {
        Globals.textManager.printText(new String[]{
          "KAREN: Hey "+Globals.player.name+" I can’t find a seat anywhere! Looks like high school students are coming here to hang out with their friends. That table at the back would be perfect, go ask if we can have it."
          }, new Lambda(){
            public void activate(){
              Globals.world.library.tiles[7][8] = null;
        Globals.gameStateManager.setState(State.WORLD);
      }
      });
      }      
}
  