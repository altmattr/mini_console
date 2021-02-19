package unigame;

//Appearance is image
//MAXHP determines max HP
//CurrentHP determines current HP
//Speed determines who goes first, with some minor RNG involvement
//Attack determines damage
//Defense determines how much is blocked
//To calculate damage, multiply attack by (100-defense) and divide by 100
//Basically, 30 defense will block 30% of damage and 99 defense will block 99% of damage
//Accuracy determines hit percentage chance
//Dodge determines dodge percentage chance
//Both are /100. If a move has 100 accuracy, it gets through the hit phase 100% of the time
//If a character has 15 dodge, they can dodge the move that got through the hit phase 15% of the time

//Statuses: 
//Tired -> dodge goes to 0%
//Resting -> Can't attack until out of shock, max 3 turns
//Drunk -> Attack triples, accuracy is halved
//Caffinated -> Speed triples
//Guarding -> Defense changed to 80+(previousDefense/5)
//Focused -> All your attacks do double damage
//we can think of others I guess
class BattleUnits{
  public BattleUnit playerBattleUnit;
  public BattleUnit hungryIbis;
  public BattleUnit hatIbis;
  public BattleUnit patron;
  public BattleUnit schoolKids;
  public BattleUnit teacher;

  public BattleUnits(UniGame p3){
    playerBattleUnit = new BattleUnit(Globals.player.name, Globals.player.battleImage, Globals.attacks.powerNap, Globals.attacks.coffeeShot,
                                      Globals.attacks.selfStudy, Globals.attacks.persuade, 100, 100, 0, 5, p3);
                                      
    hungryIbis = new BattleUnit("Hungry Ibis", p3.loadImage(Globals.assetspath+"Characters/Battle/ibis_battle.png"), 
    Globals.attacks.wingWave, Globals.attacks.screechingSquawk, Globals.attacks.rummage, Globals.attacks.snackSteal, 
    100, 100, 0, 0, p3);
  
    angryTurkey = new BattleUnit("Angry Turkey", p3.loadImage(Globals.assetspath+"Characters/Battle/turkey_battle.png"), 
    Globals.attacks.wingWave, Globals.attacks.screechingSquawk, Globals.attacks.rummage, Globals.attacks.snackSteal, 
    100, 100, 0, 0, p3);

    cockyCockatoo = new BattleUnit("Cocky Cockatoo", p3.loadImage(Globals.assetspath+"Characters/Battle/cockatoo_battle.png"), 
    Globals.attacks.wingWave, Globals.attacks.screechingSquawk, Globals.attacks.rummage, Globals.attacks.snackSteal, 
    100, 100, 0, 0, p3);

    hatIbis = new BattleUnit("Howdy Ibis", p3.loadImage(Globals.assetspath+"Characters/Battle/ibis_hat_battle.png"), 
    Globals.attacks.wingWave, Globals.attacks.screechingSquawk, Globals.attacks.hatTip, Globals.attacks.snackSteal, 
    100, 100, 0, 0, p3);

    patron = new BattleUnit("UBar Patron", p3.loadImage(Globals.assetspath+"Characters/Battle/ubarpatron_battle.png"),
    Globals.attacks.cheers, Globals.attacks.recruitmentAttempt, Globals.attacks.smallTalk, Globals.attacks.clumsyDance, 120, 100, 0, 10, p3);
    
    schoolKids = new BattleUnit("HSC Student", p3.loadImage(Globals.assetspath+"Characters/Battle/schoolkids_battle.png"),
    Globals.attacks.heavyBookThrow, Globals.attacks.portableCharger, Globals.attacks.zoomerMeme, Globals.attacks.gossip, 150,100,0,10, p3);
    
    teacher = new BattleUnit("Teacher", p3.loadImage(Globals.assetspath+"Characters/Battle/teacher_battle.png"),
    Globals.attacks.rulesReminder, Globals.attacks.hardHittingTruth, Globals.attacks.armedAccusation, Globals.attacks.disappointedSigh, 200,100,0,20, p3);
  }
}
