package bird.voice;

/*
* Class name: Spectrogram.java
*
* Rev 1
*
* 21/12/2012
*
* @reference http://www.digiphd.com/spectrogram-speech-short-time-fouier-transform-libgdx/
*
*/


import android.os.AsyncTask;
import android.util.Log;

import com.badlogic.gdx.audio.analysis.FFT;



public class Spectrogram {
	
	float[] buff;
	float[] buff_audio;
	float[] new_sig;
	int tshift = 4; //frame shift in ms
	int tlen = 32; //frame length in ms
	float [] audioBuf; //audio data input
	
	public Spectrogram(float[] audioBuf){
		this.audioBuf = audioBuf;
	}
		
	

	/**
	 * Calculates the spectrogram or log spectrum of the
	 * audio signal
	 * @param data
	 * @param nsegs
	 * @param nshift
	 * @param seglen
	 */
	public void specGram(float [] data, float nsegs, int nshift, int seglen){
	
		spec = new float[seglen][(int)nsegs];
		array2 = new float[seglen];
		seg_len = seglen;
		n_segs = nsegs;
		n_shift = nshift;
		time_array = new float[data.length];
		time_array = data;

		framed = new float [seg_len][(int)n_segs];
		framed = FrameSig();
		minmax(framed,seg_len,(int)n_segs);
		meansig((int)n_segs);
		
		array = new float[seg_len*2];
	
		
		 res=new float[seg_len];
		 fmag = new float[seg_len];
		 flogmag = new float[seg_len];
		
		 mod_spec =new float[seg_len];
		 real_mod = new float[seg_len];
		 imag_mod = new float[seg_len];
		 real = new double[seg_len];
		 imag= new double[seg_len];
		 mag = new double[seg_len];
		 phase = new double[seg_len];
		 logmag = new double[seg_len];
		 nmag = new double[seg_len];
		 for (int i = 0;i<seg_len*2;i++){
				array[i] = 0;
			}
		 
		
	for (int j=0;j<nsegs; j++){
		FFT fft = new FFT(seg_len*2, 8000);
		for (int i = 0;i<seg_len;i++){
			array[i] = framed [i][j];
		}
       fft.forward(array);
       fft_cpx=fft.getSpectrum();
       tmpi = fft.getImaginaryPart();
       tmpr = fft.getRealPart();
       
           
    	   	for(int i=0;i<seg_len;i++)
    	   	{
    	  
    	   real[i] = (double) tmpr[i];
    	   imag[i] = (double) tmpi[i];
           
    	   mag[i] = Math.sqrt((real[i]*real[i]) + (imag[i]*imag[i]));
           mag[i] = Math.abs(mag[i]/seg_len);
    
          
           logmag[i] = 20*Math.log10(mag[i]);
    	   phase[i]=Math.atan2(imag[i],real[i]);
    	 
    	   /****Reconstruction****/    	   
    	   //real_mod[i] = (float) (mag[i] * Math.cos(phase[i]));
    	   //imag_mod[i] = (float) (mag[i] * Math.sin(phase[i]));
    	   spec[(seg_len-1)-i][j] = (float) logmag[i];
    	   
    	   //Log.d("SpecGram","log= "+logmag[i]);
    	   	}
		}
		minmaxspec(spec,seg_len,(int)nsegs);
		meanspec((int)nsegs);
       //fft.inverse(real_mod,imag_mod,res);
       
       }
	/**
	 * Calculates the mean of the fft magnitude spectrum
	 * @param nsegs
	 */
	private void meanspec(int nsegs) {
		float sum = 0;
		 for (int j=1; j<(int)nsegs; j++) {
		    	for (int i = 0;i<seg_len;i++){
					
		    	sum += spec[i][j];
		        }
		    	}
		 
		  
	sum = sum/(nsegs*seg_len);
	mux = sum;		   
		
	}
	/**
	 * Calculates the min and max of the fft magnitude
	 * spectrum
	 * @param spec
	 * @param seglen
	 * @param nsegs
	 * @return
	 */
	public static float minmaxspec(float[][] spec, int seglen, int nsegs) {

		   smin = (float) 1e35;
		   smax = (float) -1e35;
		    for (int j=1; j<nsegs; j++) {
		    	for (int i = 0;i<seglen;i++){
					
		    	if (smax < spec[i][j]) {
		  	         smax =  spec[i][j];  // new maximum
		  	     }else if(smin > spec[i][j]) {
		           smin=spec[i][j];   // new maximum
		        }
		    	}
		    }
		    return smax;
		}
	/**
	 * Calculates the min and max value of the framed signal
	 * @param spec
	 * @param seglen
	 * @param nsegs
	 * @return
	 */
	public static float minmax(float[][] spec, int seglen, int nsegs) {

		   min = (float) 1e35;
		   max = (float) -1e35;
		    for (int j=1; j<nsegs; j++) {
		    	for (int i = 0;i<seglen;i++){
					
		    	if (max < spec[i][j]) {
		  	         max =  spec[i][j];  // new maximum
		  	     }else if(min > spec[i][j]) {
		           min=spec[i][j];   // new maximum
		        }
		    	}
		    }
		    return max;
		}
	
	/**
	 * Calculates the mean of the framed signal
	 * @param nsegs
	 */	
	private void meansig(int nsegs) { 
		float sum = 0;
		 for (int j=1; j<(int)nsegs; j++) {
		    	for (int i = 0;i<seg_len;i++){
					
		    	sum += framed[i][j];
		        }
		    	}
		 
		  
	sum = sum/(nsegs*seg_len);
	smux = sum;
		   
		
	}
	
	/**
	 * Frames up input audio 
	 * @return
	 */
	
	public float[][] FrameSig(){
		float [][] temp = new float [seg_len][(int)n_segs];
		float [][] frame = new float [seg_len][(int)n_segs];
		float padlen = (n_segs-1)*n_shift+seg_len;
		Log.d("DEBUG10","padlen = "+padlen);
		Log.d("DEBUG10","len = "+array2.length);
		
		 wn = hamming(seg_len);
		for (int i = 0; i < n_segs;i++){
		
			for (int j = 0;j<seg_len;j++){
		
				temp[j][i] = time_array[i*n_shift+j];//*wn[i];
			
			}
		}
		for (int i = 0; i < n_segs;i++){			// Windowing
			
			for (int j = 0;j<seg_len;j++){
		
					frame[j][i] = temp[j][i]*wn[j];
							
			}
		}
		return frame;
		
	}
	/**
	 * Calculates a hamming window to reduce
	 * spectral leakage
	 * @param len
	 * @return
	 */
	public float[] hamming(int len){
		float [] win = new float [len];
		for (int i = 0; i<len; i++){
			win[i] = (float) (0.54-0.46*Math.cos((2*Math.PI*i)/(len-1)));
		}
		return win;
	}
	
	class calcSpec extends AsyncTask<String, Integer, float [][]> {
		int fs = 0; // Sampling frequency
		int nshift = 0;// Initialise frame shift
		int nlen = 0;// Initialise frame length 
		float nsegs = 0 ; //Initialise the total number of frames		
		@Override
		protected float [][] doInBackground(String... params) {
			fs = WaveTools.getFs();
			if(fs == 0){
				fs=SoundCapt.getFs();
			}
			nshift = (int) Math.floor(tshift*fs/1000); // frame shift in samples
			nlen = (int) Math.floor(tlen*fs/1000);	// frame length in samples
			nsegs = 1+(float) (Math.ceil((audioBuf.length-(nlen))/(nshift)));
			specGram(audioBuf,nsegs,nshift,nlen);
			
			Log.d("Spectogram", "nsegs=" +nsegs+",nshift="+nshift+",nlen="+nlen);
			
			return spec;
			
		}
						
	}
	
	float[] array_hat,res=null;
	float[] fmag = null;
	float[] flogmag = null;
	float[] fft_cpx,tmpr,tmpi;
	float[] mod_spec =null;
	float[] real_mod = null;
	float[] imag_mod = null;
	double[] real =null;
	double[] imag= null;
	double[] mag =null;
	double[] phase = null;
	double[] logmag = null;
	static float [][] framed;
	static int n, seg_len,n_shift;
	static float n_segs;
	float [] time_array;
	float [] array;
	float [] wn;
	double[] nmag;
	static float [][] spec; //spectrogram array
	float [] array2;
	static float max;
	static float min;
	static float smax;
	static float smin;
	static float mux;
	static float smux;

	
	

}
