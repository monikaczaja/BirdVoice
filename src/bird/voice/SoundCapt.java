package bird.voice;

/*
* Class name: SoundCapt.java
*
* Rev 2
*
* 19/01/2013
*
* @author Monika Czaja, x11114568
*
*/

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;


/**
 * @author monika
 *
 */
public class SoundCapt implements Runnable{

	private static final String TAG = "SoundCapturing";

	private static final int RECORDER_SAMPLERATE = 8000;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	
	private int bufferSize = 0;
	private AudioRecord recorder = null;
    private boolean hasRun = false;
    
    private float [] buffer = null;
    private int time = 0;
    
    
    //constructor
    public SoundCapt(int time){
    	this.time=time;
    }
	
    
    //method to return a buffer array
    public float [] getBuffer(){
    	return buffer;
    }
    
  //capturing sound and write data to buffer array - on separate thread
    
    public void run(){
    	this.micread();
    }
    
	public void micread() { //time: sets recording time
		
		int numberOfReadings = ((RECORDER_SAMPLERATE*1*16)/16)*time;

        bufferSize = 2*AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING);
			
		//creates an instance of AudioRecord
		recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
							RECORDER_SAMPLERATE, RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING, bufferSize);
		
		//starts recording from the AudioRecord instance
		
		int recState = recorder.getState();
		Log.d(TAG, "recorder state: "+recState);
		
		if(recorder.getState()==1){
		recorder.startRecording();
		
		
		
		//write sound data to the buffer
	        
			buffer=writeDataToBuffer(numberOfReadings);
			
	    }	

		
		
		//stops recording from the AudioRecord instance
			if((hasRun==true) && (recorder != null)){
			recorder.stop();
			recorder.release();	
			recorder = null;
			}
	
		
	}
	
	
	private float[] writeDataToBuffer(int readingsNumber){
				
		float [] data = null;
		byte [] tmpInt = new byte [2];
		ByteBuffer bb = ByteBuffer.allocateDirect(2);
		data=new float[readingsNumber];
		int read = 0;
		
	 	bb.order(ByteOrder.LITTLE_ENDIAN);

		if(recorder.getRecordingState()==3){
			Log.d(TAG, "sound is being recorded");
			
		for(int i=0;i<readingsNumber;i++){
			read = recorder.read(tmpInt, 0, 2);//reads two bytes from AudioRecord buffer (for 16 bits sample)
			
			if(AudioRecord.ERROR_INVALID_OPERATION != read){
				bb.position(0);			
				bb.put(tmpInt[0]);
				bb.put(tmpInt[1]);
				data[i]=(float)bb.getShort(0); //writes two bytes as a float
				hasRun=true;
				
				}
			}
		}

		return data;
	}
	
	public static int getFs(){
		return RECORDER_SAMPLERATE;
	}
	
}
