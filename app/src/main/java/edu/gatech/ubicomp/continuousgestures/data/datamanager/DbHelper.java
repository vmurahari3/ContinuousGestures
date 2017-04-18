package edu.gatech.ubicomp.continuousgestures.data.datamanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import edu.gatech.ubicomp.continuousgestures.common.Constants;
import edu.gatech.ubicomp.continuousgestures.common.Utils;
import edu.gatech.ubicomp.continuousgestures.data.models.SampleFeatureVector;
import edu.gatech.ubicomp.continuousgestures.data.models.SampleSensorData;

/**
 * Created by Aman Parnami on 11/28/14.
 */
public class DbHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String TAG = DbHelper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = Constants.APP_NAME;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(GestureClass.CREATE_TABLE);
        db.execSQL(GestureSample.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + GestureSample.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GestureClass.TABLE_NAME);

        // create new tables
        onCreate(db);
    }


    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    /**
     * get datetime
     * */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static interface CommonColumns extends BaseColumns {
        public static final String CN_CREATED_AT = "createdAt";
        public static final String CN_UPDATED_AT = "updatedAt";
    }

    public static abstract class User implements CommonColumns {
        public static final String TABLE_NAME = "user";
        public static final String CN_LOGIN_ID = "loginId";
        public static final String CN_FIRST_NAME = "firstName";
        public static final String CN_LAST_NAME = "lastName";
        public static final String CN_LOGIN_PASS = "password";
        public static final String CN_TYPE = "type";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "("
                        + _ID + " INTEGER PRIMARY KEY,"
                        + CN_LOGIN_ID + " TEXT NOT NULL,"
                        + CN_FIRST_NAME + " TEXT,"
                        + CN_LAST_NAME + " TEXT,"
                        + CN_LOGIN_PASS + " TEXT,"
                        + CN_TYPE + " TEXT,"
                        + CN_CREATED_AT + " DATETIME,"
                        + CN_UPDATED_AT + " DATETIME"
                        + ")";
    }



    public static abstract class GestureClass implements CommonColumns {
        public static final String TABLE_NAME = "gclass";
        public static final String CN_NAME = "name";
        public static final String CN_DEVICE_TO_BODY_POSITION_MAP = "deviceToBodyPositionMap";
        public static final String CN_DESCRIPTION = "description";
        public static final String CN_GOODNESS = "goodness";
        public static final String CN_IS_ACTIVE = "isActive";
        public static final String CN_VIDEO_URI = "videoURI";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "("
                        + _ID + " INTEGER PRIMARY KEY,"
                        + CN_NAME + " TEXT NOT NULL,"
                        + CN_DESCRIPTION + " TEXT,"
                        + CN_CREATED_AT + " DATETIME,"
                        + CN_UPDATED_AT + " DATETIME"
                        + ")";
    }

    public long createGestureClass(edu.gatech.ubicomp.continuousgestures.data.models.GestureClass gcls)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(GestureClass.CN_NAME, gcls.getName());
        cValues.put(GestureClass.CN_CREATED_AT, getDateTime());

        //insert row
        long gclsId = db.insert(GestureClass.TABLE_NAME, null, cValues);

        return gclsId;
    }

    public edu.gatech.ubicomp.continuousgestures.data.models.GestureClass getGestureClass(long id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery =
                "SELECT * FROM " + GestureClass.TABLE_NAME
                        + " WHERE " + GestureClass._ID + " = " + id;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        edu.gatech.ubicomp.continuousgestures.data.models.GestureClass gc = new edu.gatech.ubicomp.continuousgestures.data.models.GestureClass();
        gc.setId(c.getLong(c.getColumnIndex(GestureClass._ID)));
        gc.setName(c.getString(c.getColumnIndex(GestureClass.CN_NAME)));
        gc.setCreatedAt(c.getString(c.getColumnIndex(GestureClass.CN_CREATED_AT)));

        return gc;
    }

    public ArrayList<edu.gatech.ubicomp.continuousgestures.data.models.GestureClass> getAllGestureClasses()
    {
        ArrayList<edu.gatech.ubicomp.continuousgestures.data.models.GestureClass> gclss = new ArrayList<edu.gatech.ubicomp.continuousgestures.data.models.GestureClass>();
        String selectQuery = "SELECT  * FROM " + GestureClass.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                edu.gatech.ubicomp.continuousgestures.data.models.GestureClass gc = new edu.gatech.ubicomp.continuousgestures.data.models.GestureClass();
                gc.setId(c.getLong(c.getColumnIndex(GestureClass._ID)));
                gc.setName(c.getString(c.getColumnIndex(GestureClass.CN_NAME)));
                gc.setCreatedAt(c.getString(c.getColumnIndex(GestureClass.CN_CREATED_AT)));
                // adding to gesture class list
                gclss.add(gc);
            } while (c.moveToNext());
        }

        return gclss;
    }

    public ArrayList<edu.gatech.ubicomp.continuousgestures.data.models.GestureClass> getAllGestureClasses(String sortBy)
    {
        if (sortBy.equals("")) {
            return getAllGestureClasses();
        }

        ArrayList<edu.gatech.ubicomp.continuousgestures.data.models.GestureClass> gclss = new ArrayList<edu.gatech.ubicomp.continuousgestures.data.models.GestureClass>();

        String selectQuery = "SELECT  * FROM " + GestureClass.TABLE_NAME + " ORDER BY " +  sortBy + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                edu.gatech.ubicomp.continuousgestures.data.models.GestureClass gc = new edu.gatech.ubicomp.continuousgestures.data.models.GestureClass();
                gc.setId(c.getLong(c.getColumnIndex(GestureClass._ID)));
                gc.setName(c.getString(c.getColumnIndex(GestureClass.CN_NAME)));
                gc.setCreatedAt(c.getString(c.getColumnIndex(GestureClass.CN_CREATED_AT)));
                // adding to gesture class list
                gclss.add(gc);
            } while (c.moveToNext());
        }

        return gclss;
    }


    /**
     * Deleting a gesture class along with all the samples it contains.
     */
    public void deleteGestureClass(long gclsId) {
        SQLiteDatabase db = this.getWritableDatabase();

        //Get all the samples a class contains.
        ArrayList<edu.gatech.ubicomp.continuousgestures.data.models.GestureSample> smplsForCls = getAllGestureSamplesByClass(gclsId);

        //Delete all samples
        for(edu.gatech.ubicomp.continuousgestures.data.models.GestureSample gsmpl: smplsForCls)
        {

            //Delete a sample.
            deleteGestureSample(gsmpl.getId());
        }

        //Now delete the class.
        db.delete(GestureClass.TABLE_NAME, GestureClass._ID + " = ?",
                new String[] { String.valueOf(gclsId) });
    }

    /**
     * Updating a class.
     */
    public long updateGestureClass(edu.gatech.ubicomp.continuousgestures.data.models.GestureClass gcls) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cValues = new ContentValues();
        cValues.put(GestureClass.CN_NAME, gcls.getName());

        // updating row
        return db.update(GestureClass.TABLE_NAME, cValues, GestureClass._ID + " = ?",
                new String[] { String.valueOf(gcls.getId()) });
    }

    /**
     * Getting class name by id
     */
    public String getClassNameById(long clsId) {
        String nameQuery = "SELECT name FROM " + GestureClass.TABLE_NAME + " gc "+"WHERE gc."
                + GestureClass._ID + " = " + clsId;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(nameQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst())
            return c.getString(c.getColumnIndex(GestureClass.CN_NAME));

        return null;
    }



    public static abstract class GestureSample implements CommonColumns {
        public static final String TABLE_NAME = "gsample";
        public static final String CN_DATA = "data";
        public static final String CN_CLASS_ID = "classId"; //This is a foreign key
        public static final String CN_FEATURE_VECTORS = "featureVectors";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "("
                        + _ID + " INTEGER PRIMARY KEY,"
                        + CN_CLASS_ID + " INTEGER REFERENCES " + GestureClass.TABLE_NAME + "(" + GestureClass._ID +"),"
                        + CN_CREATED_AT + " DATETIME,"
                        + CN_FEATURE_VECTORS + " TEXT NOT NULL,"
                        + CN_DATA + " TEXT NOT NULL"
                        + ")";
    }

    public long createGestureSample(edu.gatech.ubicomp.continuousgestures.data.models.GestureSample gsmp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();

        cValues.put(GestureSample.CN_DATA, Utils.getJsonString(gsmp.data));
        cValues.put(GestureSample.CN_FEATURE_VECTORS, Utils.getJsonString(gsmp.featureVectors));
        cValues.put(GestureSample.CN_CLASS_ID, gsmp.getClassId());
        cValues.put(GestureSample.CN_CREATED_AT, getDateTime());

        //insert row
        long gsmpId = db.insert(GestureSample.TABLE_NAME, null, cValues);

        return gsmpId;
    }

    public long updateGestureSample(edu.gatech.ubicomp.continuousgestures.data.models.GestureSample gsmp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(GestureSample.CN_DATA, Utils.getJsonString(gsmp.data));
        cValues.put(GestureSample.CN_FEATURE_VECTORS, Utils.getJsonString(gsmp.featureVectors));
        cValues.put(GestureSample.CN_CLASS_ID, gsmp.getClassId());

        return db.update(GestureSample.TABLE_NAME, cValues, GestureSample._ID + " = ?",
                new String[] { String.valueOf(gsmp.getId()) });
    }

    public edu.gatech.ubicomp.continuousgestures.data.models.GestureSample getGestureSample(long id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery =
                "SELECT * FROM " + GestureSample.TABLE_NAME
                        + " WHERE " + GestureSample._ID + " = " + id;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        edu.gatech.ubicomp.continuousgestures.data.models.GestureSample gs = new edu.gatech.ubicomp.continuousgestures.data.models.GestureSample();
        gs.setId(c.getLong(c.getColumnIndex(GestureSample._ID)));
        gs.data.addAll(Utils.getArray(c.getString(c.getColumnIndex(GestureSample.CN_DATA)), SampleSensorData[].class));
        gs.featureVectors.addAll(Utils.getArray(c.getString(c.getColumnIndex(GestureSample.CN_FEATURE_VECTORS)), SampleFeatureVector[].class));
        gs.setClassId(c.getLong(c.getColumnIndex(GestureSample.CN_CLASS_ID)));
        gs.setCreatedAt(c.getString(c.getColumnIndex(GestureSample.CN_CREATED_AT)));
        return gs;
    }


    public ArrayList<edu.gatech.ubicomp.continuousgestures.data.models.GestureSample> getAllGestureSamples()
    {
        ArrayList<edu.gatech.ubicomp.continuousgestures.data.models.GestureSample> gsmps = new ArrayList<edu.gatech.ubicomp.continuousgestures.data.models.GestureSample>();
        String selectQuery = "SELECT  * FROM " + GestureSample.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                edu.gatech.ubicomp.continuousgestures.data.models.GestureSample gs = new edu.gatech.ubicomp.continuousgestures.data.models.GestureSample();
                gs.setId(c.getLong(c.getColumnIndex(GestureSample._ID)));
                gs.data.addAll(Utils.getArray(c.getString(c.getColumnIndex(GestureSample.CN_DATA)), SampleSensorData[].class));
                gs.featureVectors.addAll(Utils.getArray(c.getString(c.getColumnIndex(GestureSample.CN_FEATURE_VECTORS)), SampleFeatureVector[].class));
                gs.setClassId(c.getLong(c.getColumnIndex(GestureSample.CN_CLASS_ID)));
                gs.setCreatedAt(c.getString(c.getColumnIndex(GestureSample.CN_CREATED_AT)));

                // adding to gesture sample list
                gsmps.add(gs);
            } while (c.moveToNext());
        }

        return gsmps;
    }

    public ArrayList<edu.gatech.ubicomp.continuousgestures.data.models.GestureSample> getAllGestureSamplesByClass(long clsId)
    {
        ArrayList<edu.gatech.ubicomp.continuousgestures.data.models.GestureSample> gsmps = new ArrayList<edu.gatech.ubicomp.continuousgestures.data.models.GestureSample>();
        String selectQuery = "SELECT  * FROM " + GestureSample.TABLE_NAME + " gc "+ "WHERE gc."
                + GestureSample.CN_CLASS_ID + " = '" + clsId + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                edu.gatech.ubicomp.continuousgestures.data.models.GestureSample gs = new edu.gatech.ubicomp.continuousgestures.data.models.GestureSample();
                gs.setId(c.getLong(c.getColumnIndex(GestureSample._ID)));
                gs.data.addAll(Utils.getArray(c.getString(c.getColumnIndex(GestureSample.CN_DATA)), SampleSensorData[].class));
                gs.featureVectors.addAll(Utils.getArray(c.getString(c.getColumnIndex(GestureSample.CN_FEATURE_VECTORS)), SampleFeatureVector[].class));
                gs.setClassId(c.getLong(c.getColumnIndex(GestureSample.CN_CLASS_ID)));
                gs.setCreatedAt(c.getString(c.getColumnIndex(GestureSample.CN_CREATED_AT)));


                // adding to gesture sample list
                gsmps.add(gs);
            } while (c.moveToNext());
        }

        return gsmps;
    }


    public ArrayList<SampleFeatureVector> getFeaturesVectorsForGestureSample(long gestureSampleId) {
        edu.gatech.ubicomp.continuousgestures.data.models.GestureSample gs = getGestureSample(gestureSampleId);
        return gs.featureVectors;
    }

    public ArrayList<ArrayList<SampleFeatureVector>> getFeaturesForGestureClass(long gestureClass) {
        ArrayList<ArrayList<SampleFeatureVector>> classFeatureVectors = new ArrayList<>();
        String selectQuery = "SELECT " + GestureSample.CN_FEATURE_VECTORS + " FROM " + GestureSample.TABLE_NAME + " gc "+ "WHERE gc."
                + GestureSample.CN_CLASS_ID + " = '" + gestureClass + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                ArrayList<SampleFeatureVector> sampleFeatureVectors = new ArrayList<>();
                sampleFeatureVectors.addAll(Utils.getArray(c.getString(c.getColumnIndex(GestureSample.CN_FEATURE_VECTORS)), SampleFeatureVector[].class));
                classFeatureVectors.add(sampleFeatureVectors);
            } while (c.moveToNext());
        }

        return classFeatureVectors;
    }


    /**
     * Deleting a gesture sample.
     */
    public void deleteGestureSample(long sampleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(GestureSample.TABLE_NAME, GestureSample._ID + " = ?",
                new String[] { String.valueOf(sampleId) });

        // TODO Delete associated resources such as video
    }


    /**
     * Getting sample count for a class.
     */
    public long getSampleCountByClass(long clsId) {
        String countQuery = "SELECT  * FROM " + GestureSample.TABLE_NAME + " gs "+ "WHERE gs."
                + GestureSample.CN_CLASS_ID + " = '" + clsId + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        long count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }


    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }
}
