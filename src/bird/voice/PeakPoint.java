package bird.voice;

/*
* Class name: PeakPoint.java
*
* Rev 1
*
* 21/12/2012
*
* @author Monika Czaja, x11114568
*
*/

public class PeakPoint {
	
	int t;
	int f;
	
	//empty constructor
	public PeakPoint(){	
	}
	
	//constructor
	public PeakPoint(int t, int f){
		this.t=t;
		this.f=f;
		
	}

	public int getT() {
		return t;
	}

	public void setT(int t) {
		this.t = t;
	}

	public int getF() {
		return f;
	}

	public void setF(int f) {
		this.f = f;
	}
	
	

}
