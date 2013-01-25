package bird.voice;

/*
* Class name: Fingerprint.java
*
* Rev 2
*
* 28/12/2012
*
* @author Monika Czaja, x11114568
*
*/

import java.util.ArrayList;
import java.util.List;

import android.util.Log;


public class Fingerprint {

	private static final String TAG = "Fingerprint";	
	
	int key;
	int [][] peaksMap;	//peaks map array - input data
	int peaksCount;
	int trackID;
	int hashCount;
	int fanSize = 5; //number of hashes for one peak point
	
	//empty constructor
	public Fingerprint(){}

	//file input constructor 
	public Fingerprint(int[][] peaksMap, int peaksCount, int trackID) {
		this.peaksMap = peaksMap;
		this.peaksCount = peaksCount;
		this.trackID = trackID;		
	}
	
	//captured input constructor 
	public Fingerprint(int[][] peaksMap, int peaksCount) {
		this.peaksMap = peaksMap;
		this.peaksCount = peaksCount;
	}

	//overloaded constructor
	public Fingerprint(int key, int[][] peaksMap, int peaksCount, int trackID) {
		this.key = key;
		this.peaksMap = peaksMap;
		this.peaksCount = peaksCount;
		this.trackID = trackID;
	}
	
	
	
	
	//getters and setters
	
	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public int[][] getPeaksMap() {
		return peaksMap;
	}

	public void setPeaksMap(int[][] peaksMap) {
		this.peaksMap = peaksMap;
	}
	
	public int getPeaksCount() {
		return peaksCount;
	}

	public void setPeaksCount(int peaksCount) {
		this.peaksCount = peaksCount;
	}

	public int getTrackID() {
		return trackID;
	}

	public void setTrackID(int trackID) {
		this.trackID = trackID;
	}

	
	
	//produce hashes from peaks map and return array which holds database rows.
	//this mathod creates database from files
	
	public int[][] getDbRows(){
		int[][] dbRows;		//array which holds database rows. Each array row contains hash, time_offset, trackID.
		List<PeakPoint> peakList = new ArrayList<PeakPoint>();
		
		int row = 0;
		
		int faHash;
		int ta;
		int ftHash;
		int tt;
	
	for(int t=0;t<peaksMap[0].length;t++){
		for(int f = 0; f<peaksMap.length;f++){
			if(peaksMap[f][t] !=0){
				peakList.add(new PeakPoint(t,f));
			}
		}
	}
	
	int r = fanSize*fanSize - countDownSum(fanSize-1);
	hashCount=peakList.size()*fanSize-r; //number of hashes based for given fan size
	
	Log.d(TAG, "Hash number is: "+hashCount);
	dbRows = new int[hashCount][3];
	
	
	while(peakList.isEmpty() == false){
		for(int i =0; i<peakList.size();i++){
			faHash = peakList.get(0).getF();
			faHash=faHash<<23;
			ta = peakList.get(0).getT();
			
			for(int j=1;j<=fanSize && j<peakList.size();j++){  //creates "fanSize" number of hashes for each anchor point
				ftHash = peakList.get(j).getF();
				ftHash = ftHash<<13;
				tt = peakList.get(j).getT();
				
				int hash = faHash + ftHash + (tt-ta)*7;
				
				dbRows[row][0]=hash;
				dbRows[row][1]=ta;
				dbRows[row][2]=trackID;
				row++;
			}
			peakList.remove(0);
		}
	}
	
	peakList = null;
	
	return dbRows;
	
	}

	//produce hashes from peaks map and return array for searching database matches.
	//this mathod creates hashes from microphone input
	
	public int[][] getCapturedSoundHashes(){
		int[][] hashTimeTable;	//array which holds captured sound. Each array row contains hash and time_offset.		
		List<PeakPoint> peakList = new ArrayList<PeakPoint>();
		
		int row = 0;
		
		int faHash;
		int ta;
		int ftHash;
		int tt;
	
	for(int t=0;t<peaksMap[0].length;t++){
		for(int f = 0; f<peaksMap.length;f++){
			if(peaksMap[f][t] !=0){
				peakList.add(new PeakPoint(t,f));
			}
		}
	}
	int r = fanSize*fanSize - countDownSum(fanSize-1);
	hashCount=peakList.size()*fanSize-r;
	
	Log.d(TAG, "Hash number is: "+hashCount);
	
	hashTimeTable = new int[hashCount][2];
	
	
	while(peakList.isEmpty() == false){
		for(int i =0; i<peakList.size();i++){
			faHash = peakList.get(0).getF();
			faHash=faHash<<23;
			ta = peakList.get(0).getT();
			
			for(int j=1;j<=fanSize && j<peakList.size();j++){ //creates "fanSize" number of hashes for each anchor point
				ftHash = peakList.get(j).getF();
				ftHash = ftHash<<13;
				tt = peakList.get(j).getT();
				
				int hash = faHash + ftHash + (tt-ta)*7;
				
				hashTimeTable[row][0]=hash;
				hashTimeTable[row][1]=ta;

				row++;
			}
			peakList.remove(0);
		}
	}
	
	peakList = null;
	return hashTimeTable;
	
	}
	
//calculate sum of the decreasing integers
 
    private static int countDownSum(int n){
        int sum = n;
        
        if(n == 0){
            return 0;
        }
        else{
            return sum + countDownSum(n-1);
        }
        
     }
}
