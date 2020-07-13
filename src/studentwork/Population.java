package studentwork;
import java.util.*;
import java.awt.geom.*;

class Population {
	public ArrayList<Simulant> sims = new ArrayList<Simulant>();
	public Random rand = new Random();

	public Population(){
		for(int i = 0; i < Main.SIMS; i++){
            sims.add(new Simulant());
        }
		
        // make the first one sick
        sims.get(0).sick = 1;
	}

	/** update an entire population
	  * for each simulant update their location and illness
	  * then check each pair of simulants to see if they are in 
	  * touch with one another.  If they are, they might get sick
	  **/
	public void update(){
		for(Simulant s: sims){
            s.updateLoc();
            s.updateIllness();
        }
        // check every pair of sims to see if they are in contact, if so, pass on any sickness
        for(Simulant s1: sims){
            for(Simulant s2: sims){
                if (s1.loc.distance(s2.loc) < Simulant.SIZE){
                    s1.sick = s1.sick == 0 && s2.sick > Main.CONT_AFTER && !s1.immune && rand.nextInt(100) < Main.TRANS_RATE ? 1 : s1.sick;
                    s2.sick = s2.sick == 0 && s1.sick > Main.CONT_AFTER && !s2.immune && rand.nextInt(100) < Main.TRANS_RATE ? 1 : s2.sick;
                }
            }
        }
	}

	/** 
	  * @return  the average of the x locations of all sick simulants
	  *          answer is zero if no sims are sick
	  **/
	public double averageXOfSick(){
		double xLocs = 0.0;
		double count = 0.0;
		
		
		for (int i = 0; i < sims.size(); i++) {
			if (sims.get(i).sick >= 1) {
				count++;
				xLocs = xLocs + sims.get(i).loc.getX();
			}
		}
		if (count == 0) {
			return 0.0;
		}
		
		double averageXLoc = xLocs/count;
		return averageXLoc;
	}

	/** 
	  * @return  the average of the y locations of all sick simulants
	  *          answer is zero if no sims are sick
	  **/
	public double averageYOfSick(){
		double yLocs = 0.0;
		double count = 0.0;
		
		
		for (int i = 0; i < sims.size(); i++) {
			if (sims.get(i).sick >= 1) {
				count++;
				yLocs = yLocs + sims.get(i).loc.getY();
			}
		}
		if (count == 0) {
			return 0.0;
		}
		
		double averageYLoc = yLocs/count;
		return averageYLoc;
	}

	/**
	  * @return  the first sim found who's is over the given point
	  *          you should use the whole area the simulant is drawn
	  *          on (i.e. center plus radius) to determine if the sim
	  *          is at this location.  Return null if no sim found.
	  **/
	public Simulant simAtLocation(Point2D loc){
		if (loc == null) {
			return null;
		}
		
		if (loc.getY() < 0 || loc.getX() < 0) {
			return null;
		}
		
		for (int i = 0; i < sims.size(); i++) {
			double xCentre = sims.get(i).loc.getX();
			double yCentre = sims.get(i).loc.getY();
			
			int radius = Main.SIM_SIZE;
			
			if (loc.getX() >= (xCentre - radius) && loc.getX() <= (xCentre + radius)) {
				if (loc.getY() >= (yCentre-radius) && loc.getY() <= (yCentre + radius)) {
					return sims.get(i);
				}
			}
		}
		return null;
	}

	/**
	  * @param sim the simulant to find in the population
	  * @return  the simulant one index greater than
	  *          the given simulant. Return null if `sim`
	  *          not found or there is no simulant after the
	  *          found one.
	  **/
	public Simulant nextAfter(Simulant sim){
		if (sim == null) {
			return null;
		}
		
		for (int i = 0; i < sims.size()-1; i++) {
			if (sims.get(i).homeLoc == sim.homeLoc) {
				return sims.get(i+1);
			}
		}
		
		return null;
	}
        
	/**
	  * @param sim the simulant to find in the population
	  * @return  the simulant one index less than
	  *          the given simulant. Return null if `sim`
	  *          not found or there is no simulant before the
	  *          found one.
	  **/
	public Simulant prevBefore(Simulant sim){
		if (sim == null) {
			return null;
		}
		
		for (int i = 1; i < sims.size(); i++) {
			if (sims.get(i).homeLoc == sim.homeLoc) {
				return sims.get(i-1);
			}
		}
		
		return null;
		
		
	}

	/**
	  * @return the total number of sick simulants
	  **/
	public int numberSick(){
		int count = 0;
		
		for (int i = 0; i < sims.size(); i++) {
			if (sims.get(i).sick > 0) {
				count++;
			}
		}
		return count;
	}

	/**
	  * @return An array list of all the simulants
	  *         sorted by "sickness".  Sorting order is 
	  *         determined by the `compareTo` method on
	  *         the simulants.
	  **/
	public ArrayList<Simulant> sort(){
		for (int i = 0; i < sims.size(); i++) {
			for (int k = i; k < sims.size(); k++) {
				if (sims.get(i).compareTo(sims.get(k)) == 1) {
					
					Simulant tempSim = new Simulant();
					tempSim = sims.get(i);
					
					sims.set(i, sims.get(k));
					sims.set(k, tempSim);
					
				}
			}
		}
		return sims;
	}

}