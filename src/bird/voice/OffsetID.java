package bird.voice;

/*
* Class name: OffsetID.java
*
* Rev 1
*
* 21/12/2012
*
* @author Monika Czaja, x11114568
*
*/

public class OffsetID implements Comparable<OffsetID> {
	
	int trackID;
	int offset;
	
	
	//empty constructor
	public OffsetID(){
		
	}

	//constructor
	public OffsetID(int trackID, int offset){
		this.trackID=trackID;
		this.offset=offset;
	}

	//getters and setters
	
	public int getTrackID() {
		return trackID;
	}

	public void setTrackID(int trackID) {
		this.trackID = trackID;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	//overrides java standard 'equals' method
	public boolean equals(Object o){
		if(!(o instanceof OffsetID )){
			return false;
		}
		OffsetID ofs = (OffsetID) o;
		return (ofs.trackID==trackID && ofs.offset==offset);
	}
	
	//methods to compare OffsetID objects
	
	public boolean equalID(Object o){
		if(!(o instanceof OffsetID )){
			return false;
		}
		OffsetID ofs = (OffsetID) o;
		return (ofs.trackID==trackID);
	}
	
	public int compareTo(OffsetID ofs){
		if(this.trackID < ofs.trackID){
			return -1;
		}
		else if(this.trackID > ofs.trackID){
			return 1;
		}
		else{
			if(this.offset < ofs.offset){
				return -1;
			}
			else if(this.offset > ofs.offset){
				return 1;
			}
			else{
				return 0;
			}
		}
	}
	

}









