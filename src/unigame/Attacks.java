package unigame;

class Attacks{
  //Attacks go: Name, Damage, Accuracy, Self-Damage/Healing, Effect (status and length), effectTargetSelf and alwaysHit
  Attack powerNap = new Attack("Power Nap",0,100,-50,new Effect(StatusEffect.RESTING,1),true,true);
  Attack coffeeShot = new Attack("Coffee Shot",15,100,0,new Effect(StatusEffect.CAFFEINATED,4),true,false);
  Attack selfStudy = new Attack("Self Study",0,100,0,new Effect(StatusEffect.FOCUSED,2),true,true);
  Attack persuade = new Attack("Persuade",25,80,0,new Effect(),false,false);
  
  //Ibis attacks
  Attack wingWave = new Attack("Wing Wave",10,100,0,new Effect(),false,false);
  Attack screechingSquawk = new Attack("Screeching Squawk",10,100,0,new Effect(),false,false);
  Attack rummage = new Attack("Rummage",0,70,-20,new Effect(),false,false);
  Attack snackSteal = new Attack("Snack Steal",20,80,-5,new Effect(),false,false);
  
  //Patron attacks
  Attack cheers = new Attack("Cheers",0,100,-10,new Effect(StatusEffect.DRUNK,5),true,true);
  Attack recruitmentAttempt = new Attack("MACS Recruitment Attempt",20,100,0,new Effect(),false,false);
  Attack smallTalk = new Attack("Small Talk",10,100,0,new Effect(StatusEffect.AWKWARD,1),false,false);
  Attack clumsyDance = new Attack("Clumsy Dance Move",25,80,10,new Effect(StatusEffect.RESTING,1),true,false);
  
  //HSC attacks
  Attack heavyBookThrow = new Attack("Heavy Book Throw",10,100,0,new Effect(),false,false);
  Attack portableCharger = new Attack("Portable Charger",0,100,-5,new Effect(StatusEffect.FOCUSED,1),true,true);
  Attack zoomerMeme = new Attack("Abstract Meme", 20,90,0,new Effect(),false,false);
  Attack gossip = new Attack("Gossip About "+Globals.player.name, 0, 100, 0, new Effect(StatusEffect.VULNERABLE,2),false,false);
  
  //Teacher attacks
  Attack rulesReminder = new Attack("Rules Reminder",10,100,0,new Effect(StatusEffect.AWKWARD,2),false,false);
  Attack hardHittingTruth = new Attack("Hard Hitting Truth",15,100,0,new Effect(),false,false);
  Attack armedAccusation = new Attack("Armed Accusation",30,90,0,new Effect(),false,false);
  Attack disappointedSigh = new Attack("Disappointed Sigh",10,100,-20,new Effect(),false,false);
  
  public Attacks(){};
}

