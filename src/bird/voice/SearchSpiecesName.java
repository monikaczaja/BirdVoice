package bird.voice;

/*
* Class name: SearchSpiecesName.java
*
* Rev 1
*
* 21/12/2012
*
* @author Monika Czaja, x11114568
*
*/

public class SearchSpiecesName implements Runnable{

	DBtools db;
	int trackID;
	private volatile String result;
	
	
	public SearchSpiecesName(DBtools db, int trackID) {
		this.db = db;
		this.trackID = trackID;
	}

	public void run(){
		getSpiecesName();
	}
	
	
	//get result
		public String getResult(){
			return result;
		}
		//set result
		public void setResult(String result){
			this.result = result;
		}
	
	//search database for spieces name depending on trackID
		private void getSpiecesName(){
	
			if(trackID == 0){
				this.setResult("Match not found");
			}
			else{
			//	this.setResult("aliens attack!!!");
				this.setResult(db.getSpeciesName(trackID));
			}
		}
	
}
