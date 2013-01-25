package bird.voice;

/*
* Class name: MainActivity.java
*
* Rev 3
*
* 19/01/2013
*
* @author Monika Czaja, x11114568
*
*/




import java.util.concurrent.ExecutionException;


import bird.voice.WaveTools;
import bird.voice.PeaksMap;


import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	
	TextView myText;
	TextView lowerText;
	float[] audioInput;//audio file input buffer for spectrogram
	float[] audioCapture;//captured mic buffer for spectrogram
	float [][] specArray;//spectrogram array
	float [][] captSpecArray;//spectrogram array for captured sound	
	int [][] filePeaksArray;//spectrogram peaks array calculated from file input
	int [][] captPeaksArray;//spectrogram peaks array calculated from captured sound
	
//values to create known calls database
	static String inputPath = "";
	int trackID = 8;
	String speciesName = "";
	
//	"Skylark", "Sparrowhawk", "Blackbird", "Little Grebe", "Great Tit", "Common Gull", "Goldfinch"
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
    }
        
    
  //starts processing data from wave file after touching the button
    
    public void start_file_processing(View view) {
    	
    	if(inputPath.equals("")){
            myText = (TextView) findViewById(R.id.file_processing_output);
            myText.setText("No valid input path typed");
    	}else{
    	    	
    	//getting audio buffer from file using WaveTools class
          audioInput = getAudio();
        
        /* creates new spectrogram object from Spectrogram outer class
        *  use 'audioInput' for creating database entry or 'audioCapture' for normal app operation 
        *  as input variable
        */
        Spectrogram outerSpectr = new Spectrogram(audioInput);
       
        //instantiate calcSpec inner class
        Spectrogram.calcSpec innerSpec = outerSpectr.new calcSpec();
        
        /*	Calculate Log Spectrogram data,
    	 * 	done with an AsynTask
    	 *  to avoid consuming UI thread resources
    	 */
        String dummy = "test";
        innerSpec.execute(dummy);
        
        
        try {
			specArray = innerSpec.get();
		} catch (InterruptedException e) {
				e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
        
        //run spectrogram peaks search method from PeaksMap class
        filePeaksArray = PeaksMap.createPeaksMap(specArray);
        
        //methods to create database of known fingerprints
        int count = PeaksMap.getPeaksCount();//number of hashes to be placed in database        
        Fingerprint trackFingerprint = new Fingerprint(filePeaksArray,count,trackID);//create fingerprint object
        DBtools db = new DBtools(this);//create instance of database handler

	    //	Add data to database, done with an AsynTask to avoid consuming UI thread resources
	    	
        db.addSpeciesTrack(new SpeciesTrack(trackID,speciesName));//add track record and species name into table
        
        DBtools.AddFingerprintAT addFingerprintAT = db.new AddFingerprintAT();//instantiate AddFingerprintAT inner class
        	
        addFingerprintAT.execute(trackFingerprint);//execute AsyncTask method

 
        
        myText = (TextView) findViewById(R.id.file_processing_output);
        myText.setText("Database entries for "+speciesName+" were added.");
 
    	}
    }
 
    
   
    
    
    /*
     * getting audio buffer by capture sound using microphone after
     * pressing the button this method use SoundCapt class
     * and calculates spectrogram from captured data
    */
    
    public void start_recording(View view){
 
//        lowerText = (TextView) findViewById(R.id.results_output);        
//        lowerText.getEditableText().clear();
  	
 //runs audio capturing and saves data to float array
 
     	SoundCapt recorder = new SoundCapt(12);
    	
    	 try {
         	Thread thread = new Thread(recorder);
              thread.start();
              thread.join();
         } catch (InterruptedException e) {
 			e.printStackTrace();
         }
    	
    	 audioCapture=recorder.getBuffer();
    	
//generate spectrogram from captured sound data
    	Spectrogram capturedSpectr = new Spectrogram(audioCapture);
        
        //instantiate calcSpec inner class
        Spectrogram.calcSpec innerCaptSpec = capturedSpectr.new calcSpec();
        
        /*	Calculate Log Spectrogram data,
    	 * 	done with an AsynTask
    	 *  to avoid consuming UI thread resources
    	 */
        String dummy = "test";
        innerCaptSpec.execute(dummy);
        
        
        try {
			captSpecArray = innerCaptSpec.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}    	

//scan spectrogram to find peaks
        captPeaksArray = PeaksMap.createPeaksMap(captSpecArray);        
   
//create hashes from unknown bird calls
        int count = PeaksMap.getPeaksCount();//number of hashes     
        Fingerprint capturedFingerprint = new Fingerprint(captPeaksArray,count);//create fingerprint object
        DBtools db = new DBtools(this);//create instance of database handler    
        
//run search methods
        
        Search hashSearch = new Search(db, capturedFingerprint);//create Search object
        
        
        //run fingerprint match search in separate thread
        try {
        	Thread thread = new Thread(hashSearch);
             thread.start();
             thread.join();
        } catch (InterruptedException e) {
			e.printStackTrace();
        }	
        int foundTrackID = hashSearch.getResultTrackID();
        
        Log.d("MainActivity","Found trackID: "+foundTrackID);
        
        SearchSpiecesName nameSearch = new SearchSpiecesName(db, foundTrackID);
        
        //run name search in separate thread
        try {
        Thread nameThread = new Thread(nameSearch);
        nameThread.start();
        nameThread.join(); 
        } catch (InterruptedException e) {
			e.printStackTrace();
        }
//      Log.d("MainActivity","Is nameThread alive: "+nameThread.isAlive());
        String results = nameSearch.getResult();
        
        lowerText = (TextView) findViewById(R.id.results_output);        
        lowerText.setText("Result: "+results);

    }
  
    public void clear_output(View view){
    	 
      lowerText = (TextView) findViewById(R.id.results_output);        
      lowerText.setText("");  
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
       
    }
    
    
   
    
  //getting audio buffer using WaveTools class (from wave file)
    private float[] getAudio() {
    	float [] audioBuf = null;
		// Acquire input audio file
		try{
			audioBuf = WaveTools.wavread(inputPath, this);
		}catch(Exception e){
			Log.d("SpecGram2","Exception= "+e);
		}
		return audioBuf;
        }
    
}
