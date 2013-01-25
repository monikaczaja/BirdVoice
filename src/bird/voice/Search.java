package bird.voice;

/*
* Class name: Search.java
*
* Rev 2
*
* 29/12/2012
*
* @author Monika Czaja, x11114568
*
*/

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.util.Log;

public class Search implements Runnable{

	private static final String TAG = "Search";
	
	private DBtools db;
	private Fingerprint fingerprint;
	private int resultTrackID;
	private int threshold = 6; //Experimentally derived from incorrectly-matched tracks
	@SuppressLint("UseValueOf")
	Integer maximumMatchNumber = new Integer(0);
	
	//constructor
	public Search(DBtools db, Fingerprint fingerprint){
		this.db = db;
		this.fingerprint = fingerprint;
		}
	
	//methods to be run in separate thread
	public void run(){
		this.findSpecies();
	}
	
	//set matching trackID
	public void setTrackID(int trackID){
		this.resultTrackID=trackID;
	}
	//get matching trackID
	public int getResultTrackID(){
		return resultTrackID;
	}
	
	
	//searching method for matching tracks - species
	public void findSpecies(){
		
		List<OffsetID> offsetList = db.getOffsetList(fingerprint);
		ArrayList<Integer> temporaryBin = new ArrayList<Integer>();
		int currentMaximumMatchNumber=0;
		
		Log.d(TAG,"Offset lists length: "+offsetList.size());
		
		if(offsetList.size() != 0){
		
		Collections.sort(offsetList);
		
		temporaryBin.add(offsetList.get(0).getOffset());
		int currentTrackId = offsetList.get(0).getTrackID();
		
		for(int i=0; i < (offsetList.size()-1) ;i++){
				currentTrackId = offsetList.get(i).getTrackID();		
				if(offsetList.get(i).equalID(offsetList.get(i+1))){
				temporaryBin.add(offsetList.get(i+1).getOffset());
			}
			else if(this.scan(temporaryBin) && currentMaximumMatchNumber<maximumMatchNumber.intValue()){
				this.setTrackID(currentTrackId);
				currentMaximumMatchNumber=maximumMatchNumber.intValue();
				temporaryBin.clear();
			}
			else{
				temporaryBin.clear();
				temporaryBin.add(offsetList.get(i).getOffset());
			}
		}
		if(this.scan(temporaryBin) && currentMaximumMatchNumber<maximumMatchNumber.intValue()){
			this.setTrackID(currentTrackId);
			}
		}
		else{
			this.setTrackID(0);
		}
	}
	
	//scanning for number of similar offsets in each bin(sorted arrayList)
	private boolean scan(ArrayList<Integer> tempBin){

// 		Log.d(TAG,"temporary bin size: "+tempBin.size());
		
		int marker = 0;//index in array of value to which next values are compared
		Integer valueAtMarker = tempBin.get(marker);
// 		Log.d(TAG,"first marker: "+valueAtMarker);
		Integer counter = 1;
		ArrayList<Integer> sameOffsetCount = new ArrayList<Integer>();
		
		for(int i = 1; i<tempBin.size(); i++){
			if(valueAtMarker.intValue() == tempBin.get(i).intValue()){
				counter++;
				}
			else{
				sameOffsetCount.add(counter);
				marker = marker+counter;
				valueAtMarker = tempBin.get(marker);
				counter = 1;
			}
			if(i == (tempBin.size()-1)){
				sameOffsetCount.add(counter);
			}
		}
		
//		Log.d(TAG,"array of same values: " + sameOffsetCount.toString());
		
		Collections.sort(sameOffsetCount);
		maximumMatchNumber = sameOffsetCount.get(sameOffsetCount.size()-1);
		Log.d(TAG,"maximumMatchNumber: " + maximumMatchNumber);
		
		return(maximumMatchNumber>threshold);
	}
	
}
