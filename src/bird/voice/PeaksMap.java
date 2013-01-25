package bird.voice;

/*
* Class name: PeaksMap.java
*
* Rev 2
*
* 29/12/2012
*
* @author Monika Czaja, x11114568
*
*/


import android.util.Log;

public class PeaksMap {
	
	private static final String TAG = "PeaksMap";
	private static int peaksCount;
	
	//create peaks map from spectrogram array and return it as an array
	public static int [][] createPeaksMap(float [][] spec){
		
		peaksCount=0;
		
		float avg;
		
		int fs = 16, ts = 24; //size of the subarray squares for which local peaks are calculated
		int nf = (int) Math.floor(spec.length/fs);
		int nt = (int) Math.floor(spec[0].length/ts);
		
		int [] localMaxCoordinates=new int[2];//indexes of local max, fm first and tm second value
		int fm, tm; //indexes of local max in subarray box, row(frequency) and column(time)
		
		int [][] peaks = new int[nf*fs][nt*ts];
		
		//fill peak array with 0 values
		
		for(int i = 0;i<peaks.length;i++){
			for(int j=0;j<peaks[i].length;j++){
				peaks[i][j]=0;
			}
		}

		Log.d(TAG, "spectrogram rows: "+spec.length+" columns: "+spec[0].length);		
		

/*	calculate average value of the spectrogram
 * 	
		int numberOfValues = 0;
		float total = 0;
		for(int i = 0;i<spec.length;i++){
			for(int j=0;j<spec[i].length;j++){
				total += spec[i][j];
				numberOfValues++;
			}
		}
		avg = total/numberOfValues;
*/
		
		//low value of average - to include local peaks even for low amplitudes in spectrogram
		avg = (float) -100;
		
//		Log.d(TAG, "average: "+total+"/"+numberOfValues);
		Log.d(TAG, "array of subarray rows: "+nf+" columns: "+nt);
		
		//loop through sq_elem array (array of subarrays) and calculate local maximum for each
		//subarray square
		
		float[][] square = new float[fs][ts];
		
		
		for(int i=0;i<nf;i++){
			for(int j=0;j<nt;j++){
				
				for(int k=0;k<fs;k++){
					for(int n=0;n<ts;n++){
						square[k][n]=spec[k+i*fs][n+j*ts]; //fills subarray
					}
				}
				
				localMaxCoordinates = getLocalMax(square, avg);
				if(localMaxCoordinates != null){
					fm=localMaxCoordinates[0];
					tm=localMaxCoordinates[1];
					
					//adds value of one to peaks array where local maximum
					//bigger than average of spectrogram was found
					
					peaks[fm+i*fs][tm+j*ts]=1; 
					
					peaksCount++;
					
				}
				
			}
			
		}
		
		Log.d(TAG, "spectrogram average: "+avg+", number of peaks found: "+peaksCount);
		
		return peaks;
		
	}
	
	
	//search for maximum value coordinates(indexes) in subarray squares
	
	private static int[] getLocalMax(float[][] square, float avg){
		
		int [] maxCoordinates=new int[2];
		
		float localMax = square[0][0];
		
		for(int i = 0; i<square.length; i++){
			for(int j = 0; j<square[i].length;j++){
				
				if(localMax<square[i][j]){
					localMax = square[i][j];
						maxCoordinates[0]=i;//row index ( coordinate on frequency axis)
						maxCoordinates[1]=j;//column index ( coordinate on time axis)
						
				}
				
			}
		}
			
		if(localMax>avg){	
		return maxCoordinates;
		}
		else{
			return null;
		}
	}
	
	public static int getPeaksCount(){
		return peaksCount;
	}

}
