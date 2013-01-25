package bird.voice;

/*
* Class name: SpeciesTrack.java
*
* Rev 1
*
* 21/12/2012
*
* @author Monika Czaja, x11114568
*
*/

public class SpeciesTrack {
	
	int trackID;
	String species;
	
	//constructor - main use
	public SpeciesTrack(int trackID, String species) {
		this.trackID = trackID;
		this.species = species;
	}
	
	//empty constructor
	public SpeciesTrack() {}
	
	
	
	//getters and setters
	public int getTrackID() {
		return trackID;
	}

	public void setTrackID(int trackID) {
		this.trackID = trackID;
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}
	

}
