package studentwork;

import java.awt.geom.*;
import java.util.*;

class Simulant implements Comparable<Simulant> {
	public static final int SIZE = 5;
	public Point2D loc;
	public int sick;
	public boolean immune;

	private double dir;
	private double speed;
	public Point2D homeLoc;
	public double mobility;
	private Random rand;

	public Simulant(){
		rand = new Random();
		homeLoc = new Point2D.Double(rand.nextInt(Pandemic.WIDTH), rand.nextInt(Pandemic.HEIGHT));
		loc = new Point2D.Double(homeLoc.getX(),homeLoc.getY());
		sick = 0;
		immune = false;
		dir = Math.random()*2*Math.PI;
		speed = Math.random()*10;
		mobility = rand.nextInt(Pandemic.MAX_MOVE);
	}

	/**
	 ** updates the location of the simulant on each animation frame according to
	 ** it's movement settings
	 **/
	public void updateLoc(){
		if (sick >=0){
			Point2D nLoc = (Point2D)loc.clone();
			nLoc.setLocation( nLoc.getX() + speed * Math.cos(dir)
				            , nLoc.getY() + speed * Math.sin(dir));

			if (nLoc.distance(homeLoc) > mobility || nLoc.getX() < 0 || nLoc.getY() < 0 || nLoc.getX() > Pandemic.WIDTH || nLoc.getY() > Pandemic.HEIGHT){
				dir = Math.random()*2*Math.PI;
			} else {
				loc = nLoc;
			}
		}
	}

	/** 
	 ** adjusts the simulant's illness status according to the rules
	 ** of the illness.  Is asymptomatic for 240 frames, then is ill
	 ** with a 3% chance of dying and then is immune if they survive
	 ** to 240 frames
	 **/
	public void updateIllness(){
		if (sick > Pandemic.CONT_AFTER){
			if (rand.nextInt((Pandemic.ILL_FOR - Pandemic.CONT_AFTER)*100) < Pandemic.DEATH_RATE){
				sick = -1;
			}
		}
		if (sick > 0){
			sick++;
		}
		if (sick > Pandemic.ILL_FOR){
			sick = 0;
			immune = true;
		}

	}

	/** compares simulants using sickness.
	  * A simulant who is earlier in their sickness is "less than" one later in their
	  * sickness.  A simulant who is dead is "less than" any other.  A simulant who is
	  * immune is "greater than" any that is sick.  A simulant that has never been 
	  * sick is "less than" one that has been sick.
	  * @return 0 if this simulant is equal to the other, less than zero if it is less than and
	  *         greater than zero if it is greater than
	  **/
	public int compareTo(Simulant other){ //done
		if (other == null) {
			return 1;
		}
	
		
		//Dead scenarios:
		
		//this dead, other not dead
		if (this.sick == -1 && other.sick != -1) {
			return -1;
		}
		
		//other dead, this not dead
		if (other.sick == -1 && this.sick != -1) {
			return 1;
		}
		
		//both dead
		if (this.sick == -1 && other.sick == -1) {
			return 0;
		}
		
		//Immune scenarios:
		
		//this is immune, other not immune
		if (this.immune == true && other.immune == false) {
			return 1;
		}
		
		//other is immune, this is not
		if (this.immune == false && other.immune == true) {
			return -1;
		}
		
		//both immune
		if (this.immune == true && other.immune == true) {
			return 0;
		}
		
		//Never been sick scenarios:
		
		//this never been sick, other has been sick or is sick or dead
		if (this.sick == 0 && this.immune == false && (other.sick != 0 || other.immune == true)) {
			return -1;
		}
		
		//other never been sick, this has been sick, is sick or dead
		if ((this.sick != 0 || this.immune) == true && other.sick == 0 && other.immune == false) {
			return 1;
		}
		
		//both never been sick
		if (this.sick == 0 && this.immune == false && other.sick == 0 && other.immune == false) {
			return 0;
		}
		
		//Relative sickness scenarios
		if (this.sick < other.sick) {
			return -1;
		}
		if (this.sick > other.sick) {
			return 1;
		}
		
		return 0;
	}

}