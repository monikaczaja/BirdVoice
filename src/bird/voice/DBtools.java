package bird.voice;

/*
* Class name: DBtools.java
*
* Rev 4
*
* 19/01/2013
*
* @author Monika Czaja, x11114568
*
*/


import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
//import android.util.Log;


//create a subclass of 'SQLiteOpenHelper' helper class to manage database creation and version management
public class DBtools extends SQLiteOpenHelper {
	
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "File-15s";
	//"Captured-BV1"
	private static final String TABLE_CALLS_FINGERPRINTS = "CallsFingerprints"; //table name
	private static final String TABLE_SPECIES_TRACKS = "Species_Tracks"; //table name
//	private static final String TAG = "DBtools";
	
	//CallsFingerprints table column names:
	private static final String KEY_HASH_ID="hash_id";
	private static final String KEY_HASH="hash";
	private static final String KEY_TIME_OFFSET="time_offset";
	private static final String KEY_TRACK_ID="track_id";
	
	//SpeciesTracks table column names:
	private static final String KEY_SPECIES_TRACK_ID="track_id";
	private static final String KEY_SPECIES_NAME="species_name";
	
	//constructor
	public DBtools(Context context){
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	
	//Create table
	
	String CREATE_CALLS_FINGERPRINTS_TABLE = "CREATE TABLE "+ TABLE_CALLS_FINGERPRINTS + "("+KEY_HASH_ID+
			" INTEGER PRIMARY KEY, "+KEY_HASH+" INTEGER, "+KEY_TIME_OFFSET+" INTEGER, "+KEY_TRACK_ID
			+" INTEGER )";	
	String CREATE_SPECIES_TRACKS_TABLE = "CREATE TABLE "+ TABLE_SPECIES_TRACKS + "("+KEY_SPECIES_TRACK_ID+
			" INTEGER PRIMARY KEY, "+KEY_SPECIES_NAME+" TEXT )";
	
	@Override
	public void onCreate(SQLiteDatabase db){

		db.execSQL(CREATE_CALLS_FINGERPRINTS_TABLE);
		db.execSQL(CREATE_SPECIES_TRACKS_TABLE);
	}
	
	//Upgrade database = drop tables and create them again
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS "+ TABLE_CALLS_FINGERPRINTS);
		db.execSQL("DROP TABLE IF EXISTS "+ CREATE_SPECIES_TRACKS_TABLE);
		onCreate(db);
	}
	
	
	
	
	
	//Methods for database operation ie: Create, Read, Update and Delete (aka CRUD)
	
	// Adding new Fingerprint
	public void addFingerprint(Fingerprint fingerprint) {
		
		int[][] dbArray = fingerprint.getDbRows();
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		//add fingerprint's values
		ContentValues values = new ContentValues();
		for(int i = 0; i < dbArray.length; i++){
		values.put(KEY_HASH, dbArray[i][0]);
		values.put(KEY_TIME_OFFSET, dbArray[i][1]);
		values.put(KEY_TRACK_ID, dbArray[i][2]);
		
		//insert row with above values 
		db.insert(TABLE_CALLS_FINGERPRINTS, null, values);
		values.clear();
		}
  		db.close();
		
	}
	
	//Add new fingerprint using AsyncTask
	class AddFingerprintAT extends AsyncTask<Fingerprint, Void, Void>{
		
		@Override
		protected Void doInBackground(Fingerprint... fingerprints){
						
			addFingerprint(fingerprints[0]);
			
			return null;
		}
		
	}
	
	
	
	//Add new species trackID
	public void addSpeciesTrack(SpeciesTrack track) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		//add values to table
		ContentValues values = new ContentValues();
		values.put(KEY_SPECIES_TRACK_ID, track.getTrackID());
		values.put(KEY_SPECIES_NAME, track.getSpecies());
		
		//insert row with above values 
		db.insert(TABLE_SPECIES_TRACKS, null, values);
		values.clear();
				
		db.close();
		
	}
	
	
	
	// Getting time offsets for captured sound hashes - method called from Search class
	public List<OffsetID> getOffsetList(Fingerprint fingerprint) {
		
		List<OffsetID> offsetList = new ArrayList<OffsetID>();
		int[][] capturedHashes = fingerprint.getCapturedSoundHashes();
	   	int hash; 
		int timeDB;
		int hashDB;
		int timeOffset;
		int trackID;
		Cursor cursor;
		int numberOfArguments=200;
		int pointer=0;
		
		//testing hash values
//		int[][] testHashes = {{974716935,1},{1679466496,2},{1485561863,4},{797720576,5}};//values from File-BV1 DB
//		capturedHashes = testHashes;
		
//		Log.d(TAG, "Hash array size: "+capturedHashes.length+" by "+ capturedHashes[0].length);
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		while(pointer < (capturedHashes.length-numberOfArguments)){
		
	    String[] selectionArgs = new String[numberOfArguments];
	    
	    for(int i=0;i<numberOfArguments;i++){
	    	hash=capturedHashes[pointer+i][0];
	    	selectionArgs[i]=String.valueOf(hash);
	    }
	
		
        StringBuilder inClause = new StringBuilder();
        inClause.append(" IN (");
        for(int i=0; i<selectionArgs.length-1;i++){
          inClause.append("?,");  
        }
        inClause.append("?)");
        
        String inString=inClause.toString();
	
	    cursor = db.query(TABLE_CALLS_FINGERPRINTS, 
				new String[] {KEY_HASH, KEY_TIME_OFFSET, KEY_TRACK_ID}, KEY_HASH + inString,
				selectionArgs, null, null, null);

	//		Log.d(TAG, "Number of resulting rows for a hash is: "+cursor.getCount());

			if(cursor != null && cursor.moveToFirst()){
			cursor.moveToFirst();
 				while (!cursor.isAfterLast()){	
					hashDB = cursor.getInt(0);
 					timeDB = cursor.getInt(1);
					trackID=cursor.getInt(2);
					
				    for(int i=0;i<numberOfArguments;i++){
				    	int currentHash=capturedHashes[pointer+i][0];
				    if(hashDB==currentHash){	
					timeOffset=capturedHashes[pointer+i][1]-timeDB;
					offsetList.add(new OffsetID(trackID,timeOffset));
				    	}
				    }
					cursor.moveToNext();
				}
			}
			
			pointer=pointer+numberOfArguments;
			cursor.close();
		}

	    db.close();
	    return offsetList;
	}
	


	
	// Getting species name for given key - trackID
	public String getSpeciesName(int trackID){
		
		String result;
		SQLiteDatabase db = this.getReadableDatabase();
	    Cursor cursor = db.query(TABLE_SPECIES_TRACKS, 
				new String[] {KEY_SPECIES_NAME}, KEY_SPECIES_TRACK_ID +"=?",
				new String[]{String.valueOf(trackID)}, null, null, null);
		
			if(cursor != null){
				cursor.moveToFirst();
			}
			
			result = cursor.getString(0);
			db.close();
			return result;
	}
	
	

	
	// Updating single Fingerprint for given trackID (for future use - never tested)
	public void updateFingerprint(Fingerprint fingerprint) {
		SQLiteDatabase db = this.getWritableDatabase();
		int[][] dbArray = fingerprint.getDbRows();
		int trackID=fingerprint.getTrackID();
		
		//delete DB rows for a given trackID
		db.delete(TABLE_CALLS_FINGERPRINTS, KEY_HASH_ID + " = ?",
	            new String[] { String.valueOf(trackID) });
		
		//add fingerprint's values
		ContentValues values = new ContentValues();
		for(int i = 0; i < dbArray.length; i++){
		values.put(KEY_HASH, dbArray[i][0]);
		values.put(KEY_TIME_OFFSET, dbArray[i][1]);
		values.put(KEY_TRACK_ID, dbArray[i][2]);
		
		//insert row with above values 
		db.insert(TABLE_CALLS_FINGERPRINTS, null, values);
		values.clear();
		}
		db.close();		
	 
	}
	
	// Updating single speciesTrack (for future use - never tested)
	public int updateSpeciesTrack(SpeciesTrack track) {
	    		
		SQLiteDatabase db = this.getWritableDatabase();
		int rowsAffected = 0;
	 
	    ContentValues values = new ContentValues();
	    values.put(KEY_SPECIES_NAME, track.getSpecies());
	 
	    // updating row
	    rowsAffected = db.update(TABLE_SPECIES_TRACKS, values, KEY_SPECIES_TRACK_ID + " = ?",
	            new String[] { String.valueOf(track.getTrackID()) });
	    db.close();
	    return rowsAffected;
	}
	
	// Deleting single Fingerprint for a given trackID (for future use - never tested)
	public void deleteFingerprint(int trackID) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_CALLS_FINGERPRINTS, KEY_HASH_ID + " = ?",
	            new String[] { String.valueOf(trackID) });
		
		db.close();
	}
	
	//delete single entry in Species_Track table (for future use - never tested)
	public void deleteSpeciesTrack(int trackID) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.delete(TABLE_SPECIES_TRACKS, KEY_SPECIES_TRACK_ID + " = ?", 
				new String[] { String.valueOf(trackID) });
		
		db.close();
	}
	
}
