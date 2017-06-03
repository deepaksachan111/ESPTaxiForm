package com.tns.espapp.database;

import android.app.Notification;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.tns.espapp.AttachmentData;
import com.tns.espapp.CaptureData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TNS on 12/22/2016.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHandler.class.getSimpleName();
    // Database Version
    private static final int DATABASE_VERSION = 12;

    // Database Name
    private static final String DATABASE_NAME = "my";
    private static final String TABLE_TAXIFOM_DATA = "add_texiformaata";
    private static final String TABLE_LATLONG ="latlong";
    private static final String TABLE_FEEDBACK_RECORD ="feedback_record";
    private static final String TABLE_FEEDBACK_ATTACHMENT ="feedback_attachment";
    private static final String TABLE_FEEDBACK_CAPTURE ="feedback_capture";
    private static final String TABLE_NOTIFICATION ="notification";

    // Contacts Table Columns names


    // Contacts Table Columns names for add post data student


    private static final String KEY_INCRI_ID = "incri_id";
    private static final String KEY_ID = "key_id";
    private static final String KEY_SELECTDATE = "selectdate";
    private static final String KEY_SETFORMNO = "formno";
    private static final String KEY_PROJECTTYPE = "projecttype";
    private static final String KEY_VEHICLENO = "vehicleno";
    private static final String KEY_STARTKM = "startkm";
    private static final String KEY_STARTKM_IMAGE = "startkm_image";
    private static final String KEY_ENDKM = "endkm";
    private static final String KEY_ENDKM_IMAGE = "endkmimage";
    private static final String KEY_FLAG = "flag";



// Table LatLong Columns name

    private static final String KEY_LATLONG_INCRIID ="incri_latlongid";
    private static final String KEY_LATLONG_SETFORMNO="formno";
    private static final String KEY_LATLONG_DATE="lat_longdate";
    private static final String KEY_LATLONG_LAT ="lat";
    private static final String KEY_LATLONG_LONG ="long";
    private static final String KEY_LATLONG_FLAG ="latlong_flag";
    private static final String KEY_LATLONG_TOTALDIST="dist_sum";
    private static final String KEY_LATLONG_TIME="latlong_time";
    private static final String KEY_LATLONG_SPEED="latlong_speed";



    // Table FEEDBACK_RECORD Columns name

    private static final String KEY_FEEDBACK_RECORD_INCRIID ="incri_feedback_record_id";
    private static final String KEY_FEEDBACK_RECORD_UNITNAME="unitname";
    private static final String KEY_FEEDBACK_RECORD_REFERENCENO="referenceno";
    private static final String KEY_FEEDBACK_RECORD_DATE ="date";
    private static final String KEY_FEEDBACK_RECORD_BREIF ="brief";
    private static final String KEY_FEEDBACK_RECORD_FLAG ="feedbackrecord_flag";
    private static final String KEY_FEEDBACK_RECORD_LAT ="feedbackrecord_lat";
    private static final String KEY_FEEDBACK_RECORD_LOG ="feedbackrecord_log";



    // Table FEEDBACK_ATTACHMENTDATA Columns name
    private static final String KEY_FEEDBACK_ATT_INCRIID ="incri_feedback_att_id";
   // private static final String KEY_FEEDBACK_ATT_FNID="fnId";
    private static final String KEY_FEEDBACK_ATT_REFNO="att_refno";
    private static final String KEY_FEEDBACK_ATT_IMAGENAME="imagename";
    private static final String KEY_FEEDBACK_ATT_IMAGE_FILE="imagename_file";
    private static final String KEY_FEEDBACK_ATT_FLAG ="feedback_att_flag";

    // Table FEEDBACK_CAPTUREDATA Columns name
    private static final String KEY_FEEDBACK_CAPTURE_INCRIID ="incri_feedback_capture_id";
  //  private static final String KEY_FEEDBACK_CAPTURE_FNID="fnId";
  private static final String KEY_FEEDBACK_CAPTURE_REFNO="image_refno";
    private static final String KEY_FEEDBACK_CAPTURE_IMAGENAME="imagename";
    private static final String KEY_FEEDBACK_CAPTURE_IMAGE_FILE="imagename_file";
    private static final String KEY_FEEDBACK_CAPTURE_FLAG ="feedback_capture_flag";


    // Table Notification Columns name
    private static final String KEY_NOTIFICATION_INCRIID ="incri_notification";
    private static final String KEY_NOTIFICTION_TITTLE="notification_tittle";
    private static final String KEY_NOTIFICATION_MESSAGE="notification_message";
    private static final String KEY_NOTIFICATION_IMAGE="notification_image";
    private static final String KEY_NOTIFICATION_FLAG="notification_flag";





    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        Log.v(TAG, "Databaser object created");
    }

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {




        String  CREATE_TABLE_ADD_POST_DATA = "CREATE TABLE " + TABLE_TAXIFOM_DATA + "(" + KEY_INCRI_ID + " integer primary key autoincrement," +KEY_ID+ " integer,"
                + KEY_SELECTDATE + " TEXT," + KEY_SETFORMNO + " TEXT," + KEY_PROJECTTYPE + " TEXT," + KEY_VEHICLENO + " TEXT,"
                + KEY_STARTKM + " TEXT," + KEY_STARTKM_IMAGE + " TEXT," + KEY_ENDKM + " TEXT,"  + KEY_ENDKM_IMAGE + " TEXT,"  + KEY_FLAG + " integer" +")";



        String CREATE_TABLE_LATlONGDATA = "create table "
                + TABLE_LATLONG + " (" + KEY_LATLONG_INCRIID
                + " integer primary key autoincrement," + KEY_LATLONG_SETFORMNO
                + " text,"    + KEY_LATLONG_DATE
                + " text,"+ KEY_LATLONG_LAT + " text,"
                + KEY_LATLONG_LONG + " text," + KEY_LATLONG_FLAG+ " integer," +KEY_LATLONG_TOTALDIST + " text," +KEY_LATLONG_TIME + " text,"  + KEY_LATLONG_SPEED + " text"    +");";

        String CREATE_TABLE_FEEDBACK_RECORD = "create table "
                + TABLE_FEEDBACK_RECORD + " (" + KEY_FEEDBACK_RECORD_INCRIID
                + " integer primary key autoincrement," + KEY_FEEDBACK_RECORD_UNITNAME
                + " text,"    + KEY_FEEDBACK_RECORD_REFERENCENO
                + " text,"+ KEY_FEEDBACK_RECORD_DATE + " text,"
                + KEY_FEEDBACK_RECORD_BREIF + " text," + KEY_FEEDBACK_RECORD_FLAG+ " integer,"  + KEY_FEEDBACK_RECORD_LAT + " text,"   + KEY_FEEDBACK_RECORD_LOG + " text"   +");";

        String CREATE_TABLE_FEEDBACK_ATTACHMENT = "create table "
                + TABLE_FEEDBACK_ATTACHMENT + " (" + KEY_FEEDBACK_ATT_INCRIID + " integer primary key autoincrement,"+KEY_FEEDBACK_ATT_REFNO + " text,"
                + KEY_FEEDBACK_ATT_IMAGENAME + " text,"   + KEY_FEEDBACK_ATT_IMAGE_FILE + " text," + KEY_FEEDBACK_ATT_FLAG+ " integer"    +");";


        String CREATE_TABLE_FEEDBACK_CAPTURE = "create table "
                + TABLE_FEEDBACK_CAPTURE + " (" + KEY_FEEDBACK_CAPTURE_INCRIID + " integer primary key autoincrement,"+ KEY_FEEDBACK_CAPTURE_REFNO + " text,"
                + KEY_FEEDBACK_CAPTURE_IMAGENAME + " text,"+ KEY_FEEDBACK_CAPTURE_IMAGE_FILE+ " text," + KEY_FEEDBACK_CAPTURE_FLAG+ " integer"    +");";

        String CREATE_TABLE_NOTIFICATION = "create table "
                + TABLE_NOTIFICATION + " (" + KEY_NOTIFICATION_INCRIID + " integer primary key autoincrement,"+ KEY_NOTIFICTION_TITTLE + " text,"
                + KEY_NOTIFICATION_MESSAGE + " text,"+ KEY_NOTIFICATION_IMAGE+ " text," + KEY_NOTIFICATION_FLAG+ " integer"   +");";




        db.execSQL(CREATE_TABLE_ADD_POST_DATA);
        db.execSQL(CREATE_TABLE_LATlONGDATA);
        db.execSQL(CREATE_TABLE_FEEDBACK_RECORD);
        db.execSQL(CREATE_TABLE_FEEDBACK_ATTACHMENT);
        db.execSQL(CREATE_TABLE_FEEDBACK_CAPTURE);
        db.execSQL(CREATE_TABLE_NOTIFICATION);

        Log.v(TAG, "Database table created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {



        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAXIFOM_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LATLONG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDBACK_RECORD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDBACK_ATTACHMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDBACK_CAPTURE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);

        // Create tables again
        onCreate(db);
    }




    // code to add the new addBus


    public void addTaxiformData(TaxiFormData taxiFormData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, taxiFormData.getKeyid());
        values.put(KEY_SELECTDATE, taxiFormData.getSelectdate());
        values.put(KEY_SETFORMNO,taxiFormData.getFormno());
        values.put(KEY_PROJECTTYPE, taxiFormData.getProjecttype());
        values.put(KEY_VEHICLENO, taxiFormData.getVechicleno());

        values.put(KEY_STARTKM, taxiFormData.getStartkm());
        values.put(KEY_STARTKM_IMAGE, taxiFormData.getStartkm_image());
        values.put(KEY_ENDKM, taxiFormData.getEndkm());
        values.put(KEY_ENDKM_IMAGE, taxiFormData.getEndkmimage());
        values.put(KEY_FLAG, taxiFormData.getFlag());




        // Inserting Row
        db.insert(TABLE_TAXIFOM_DATA, null, values);
        Log.v(TAG, "Databaser insert taxidata table");
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public void addTaxiformLatLong(LatLongData latLongData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LATLONG_SETFORMNO, latLongData.getFormno());
        values.put(KEY_LATLONG_DATE, latLongData.getDate());
        values.put(KEY_LATLONG_LAT, latLongData.getLat());
        values.put(KEY_LATLONG_LONG, latLongData.getLongi());
        values.put(KEY_LATLONG_TOTALDIST, latLongData.getTotaldis());
        values.put(KEY_LATLONG_FLAG, latLongData.getLatlong_flag());
        values.put(KEY_LATLONG_TIME, latLongData.getCurrent_time_str());
        values.put(KEY_LATLONG_SPEED, latLongData.getSpeed());
       // values.put(KEY_LATLONG_TOTALDIST, latLongData.getTotaldis());







        // Inserting Row
        db.insert(TABLE_LATLONG, null, values);
        Log.v(TAG, "Databaser insert taxi Lat Long data table");
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }





    // code to get the single AddBus

    public void deleteSingleRow_LatLong()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_LATLONG + " ;");
        db.close();
    }

    public void deleteSingleRowTaxiformData(String value)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_TAXIFOM_DATA + " WHERE " + KEY_SETFORMNO + "='" + value + "'");
        db.close();
    }

    public void delete_TaxiFormRecord()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //  db.execSQL("DELETE FROM " + TABLE_DASHBOARD + " WHERE " + KEY_EMAIL + "='" + value + "'");
        db.execSQL("DELETE FROM " + TABLE_TAXIFOM_DATA + " ;");
        db.close();
    }


    public List<String> showData(String u,String p)
    {
        SQLiteDatabase db =this.getReadableDatabase();

        List<String> list = new ArrayList<String>();

        String str="No Data Found";
        String query = "SELECT * FROM "+ TABLE_TAXIFOM_DATA +" WHERE selectdate=? and projecttype=?";
        Cursor cursor = db.rawQuery(query, new String[]{u, p});

        int c = cursor.getCount();

        if(c > 0)
        {
            while(cursor.moveToNext())
            {
                str = ""+cursor.getString(0)+":"+cursor.getString(1)+":"+cursor.getString(2)+"";
                list.add(str);
            }
        }
        return list;
    }



    public List<TaxiFormData> getAllTaxiformData() {
        List<TaxiFormData> List = new ArrayList<TaxiFormData>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TAXIFOM_DATA;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TaxiFormData data = new TaxiFormData();
                //contact.setID(Integer.parseInt(cursor.getString(0)));
                data.setId(cursor.getInt(0));
                data.setKeyid(cursor.getInt(1));
                data.setSelectdate(cursor.getString(2));
                data.setFormno(cursor.getString(3));
                data.setProjecttype(cursor.getString(4));
                data.setVechicleno(cursor.getString(5));
                data.setStartkm(cursor.getString(6));
                data.setStartkm_image(cursor.getString(7));
                data.setEndkm(cursor.getString(8));
                data.setEndkmimage(cursor.getString(9));
                data.setFlag(cursor.getInt(10));
                // Adding contact to list
                List.add(data);
            } while (cursor.moveToNext());
        }
        // return contact list
        return List;
    }


    public List<LatLongData> getAllLatLong() {


        ArrayList<LatLongData> list = new ArrayList<LatLongData>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_LATLONG;

        SQLiteDatabase db = this.getReadableDatabase();
        try {

            Cursor cursor = db.rawQuery(selectQuery, null);
            try {

                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        LatLongData data = new LatLongData();
                        //only one column
                        data.setId(cursor.getInt(0));
                        data.setFormno(cursor.getString(1));
                        data.setDate(cursor.getString(2));
                        data.setLat(cursor.getString(3));
                        data.setLongi(cursor.getString(4));
                        data.setLatlong_flag(cursor.getInt(5));
                        data.setTotaldis(cursor.getString(6));
                        data.setCurrent_time_str(cursor.getString(7));
                        data.setSpeed(cursor.getString(8));


                        //you could add additional columns here..

                        list.add(data);
                    } while (cursor.moveToNext());
                }

            } finally {
                try { cursor.close();

                }
                catch (Exception ignore) {}
            }

        } finally {
            try {
                db.close(); }
            catch (Exception ignore) {

            }
        }

        return list;
    }

    public List<LatLongData> getAllLatLongORDerBy() {

        ArrayList<LatLongData> list = new ArrayList<LatLongData>();
        // Select All Query
        // SELECT * FROM members ORDER BY date_of_birth DESC;
        String selectQuery = "SELECT  * FROM " + TABLE_LATLONG +" ORDER BY "+KEY_LATLONG_INCRIID+" ASC;";
        SQLiteDatabase db = this.getReadableDatabase();
        try {

            Cursor cursor = db.rawQuery(selectQuery, null);
            try {
                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        LatLongData data = new LatLongData();
                        //only one column
                        data.setId(cursor.getInt(0));
                        data.setFormno(cursor.getString(1));
                        data.setDate(cursor.getString(2));
                        data.setLat(cursor.getString(3));
                        data.setLongi(cursor.getString(4));
                        data.setLatlong_flag(cursor.getInt(5));
                        data.setTotaldis(cursor.getString(6));
                        data.setCurrent_time_str(cursor.getString(7));
                        data.setSpeed(cursor.getString(8));

                        //you could add additional columns here..

                        list.add(data);
                    } while (cursor.moveToNext());
                }

            } finally {
                try { cursor.close();

                }
                catch (Exception ignore) {}
            }

        } finally {
            try {
                db.close(); }
            catch (Exception ignore) {

            }
        }

        return list;
    }

    public List<LatLongData> getAllLatLongStatus() {

        ArrayList<LatLongData> list = new ArrayList<LatLongData>();
        // Select All Query
        // SELECT * FROM members ORDER BY date_of_birth DESC;
        String selectQuery = "SELECT  * FROM " + TABLE_LATLONG +" WHERE "+KEY_LATLONG_FLAG +" = 0" +" ORDER BY "+KEY_LATLONG_INCRIID +" ASC LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        try {

            Cursor cursor = db.rawQuery(selectQuery, null);
            try {
                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        LatLongData data = new LatLongData();
                        //only one column
                        data.setId(cursor.getInt(0));
                        data.setFormno(cursor.getString(1));
                        data.setDate(cursor.getString(2));
                        data.setLat(cursor.getString(3));
                        data.setLongi(cursor.getString(4));
                        data.setLatlong_flag(cursor.getInt(5));
                        data.setTotaldis(cursor.getString(6));
                        data.setCurrent_time_str(cursor.getString(7));
                        data.setSpeed(cursor.getString(8));

                        //you could add additional columns here..

                        list.add(data);
                    } while (cursor.moveToNext());
                }

            } finally {
                try { cursor.close();

                }
                catch (Exception ignore) {}
            }

        } finally {
            try {
                db.close(); }
            catch (Exception ignore) {

            }
        }

        return list;
    }




    public  List<LatLongData> getLatLongbyFormNo(String formno){
        ArrayList<LatLongData> list = new ArrayList<LatLongData>();
        // Select All Query
        // SELECT * FROM members ORDER BY date_of_birth DESC;
       // String selectQuery = "SELECT  * FROM " + TABLE_LATLONG +" ORDER BY "+KEY_LATLONG_INCRIID+" ASC LIMIT 1;";
      //  String selectQuery = "SELECT  * FROM " + TABLE_LATLONG + " WHERE " +KEY_LATLONG_SETFORMNO +" = ?" + " ORDER BY "+KEY_LATLONG_INCRIID+  " ASC LIMIT 1" ;

        String selectQuery = "SELECT  * FROM " + TABLE_LATLONG + " WHERE " +KEY_LATLONG_SETFORMNO +" = ?"  ;


        SQLiteDatabase db = this.getReadableDatabase();
        try {

            Cursor cursor = db.rawQuery(selectQuery, new String[]{formno});
            try {
                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        LatLongData data = new LatLongData();
                        //only one column
                        data.setId(cursor.getInt(0));
                        data.setFormno(cursor.getString(1));
                        data.setDate(cursor.getString(2));
                        data.setLat(cursor.getString(3));
                        data.setLongi(cursor.getString(4));
                        data.setLatlong_flag(cursor.getInt(5));
                        data.setTotaldis(cursor.getString(6));
                        data.setCurrent_time_str(cursor.getString(7));
                        data.setSpeed(cursor.getString(8));

                        //you could add additional columns here..

                        list.add(data);
                    } while (cursor.moveToNext());
                }

            } finally {
                try { cursor.close();

                }
                catch (Exception ignore) {}
            }

        } finally {
            try {
                db.close(); }
            catch (Exception ignore) {

            }
        }

        return list;

    }

    public  List<LatLongData> getLastLatLong(String formno){
        ArrayList<LatLongData> list = new ArrayList<LatLongData>();
        // Select All Query
        // SELECT * FROM members ORDER BY date_of_birth DESC;
        //String selectQuery = "SELECT  * FROM " + TABLE_LATLONG +" ORDER BY "+KEY_LATLONG_INCRIID+" DESC LIMIT 1;";

        String selectQuery = "SELECT  * FROM " + TABLE_LATLONG + " WHERE " +KEY_LATLONG_SETFORMNO +" = ?" + " ORDER BY "+KEY_LATLONG_INCRIID+  " DESC LIMIT 1" ;

        SQLiteDatabase db = this.getReadableDatabase();
        try {

            Cursor cursor = db.rawQuery(selectQuery, new String[]{formno});
            try {
                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        LatLongData data = new LatLongData();
                        //only one column
                        data.setId(cursor.getInt(0));
                        data.setFormno(cursor.getString(1));
                        data.setDate(cursor.getString(2));
                        data.setLat(cursor.getString(3));
                        data.setLongi(cursor.getString(4));
                        data.setLatlong_flag(cursor.getInt(5));
                        data.setTotaldis(cursor.getString(6));
                        data.setCurrent_time_str(cursor.getString(7));
                        data.setSpeed(cursor.getString(8));
                        //you could add additional columns here..

                        list.add(data);
                    } while (cursor.moveToNext());
                }

            } finally {
                try { cursor.close();

                }
                catch (Exception ignore) {}
            }

        } finally {
            try {
                db.close(); }
            catch (Exception ignore) {

            }
        }

        return list;

    }


    // Getting AddBusData Count
    public int getCountTaxiform() {
        String countQuery = "SELECT  * FROM " + TABLE_TAXIFOM_DATA;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }



    public boolean updatedetails(int rowId,String date,String formno,String projectType,String vehicleno, String stkm,String stkm_image,String endkm,String endkmImage,int flag )
    {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues args = new ContentValues();

        args.put(KEY_SELECTDATE, date);
        args.put(KEY_SETFORMNO, formno);
        args.put(KEY_PROJECTTYPE, projectType);
        args.put(KEY_VEHICLENO,vehicleno);
        args.put(KEY_STARTKM, stkm);
        args.put(KEY_STARTKM_IMAGE, stkm_image);
        args.put(KEY_ENDKM, endkm);
        args.put(KEY_ENDKM_IMAGE, endkmImage);
        args.put(KEY_FLAG, flag);


        int i =  db.update(TABLE_TAXIFOM_DATA, args, KEY_INCRI_ID + "=" + rowId, null);
        return i > 0;
    }


    public boolean updateLatLong( int incriid,String form_no,String date,String lat,String longi,int flag )
    {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues args = new ContentValues();
        args.put(KEY_LATLONG_SETFORMNO, form_no);
        args.put(KEY_LATLONG_DATE, date);
        args.put(KEY_LATLONG_LAT, lat);
        args.put(KEY_LATLONG_LONG, longi);
        args.put(KEY_LATLONG_FLAG,flag);
      //  args.put(KEY_LATLONG_TOTALDIST,dis);


        int i =  db.update(TABLE_LATLONG, args, KEY_LATLONG_INCRIID + "=" + incriid, null);
        return i > 0;
    }


    public void deleteSomeRow_Taxiform()
    {
        SQLiteDatabase db = this.getWritableDatabase();
       // db.execSQL("delete from "+ TABLE_TAXIFOM_DATA+" where " +KEY_INCRI_ID+ " not in ( select " +KEY_INCRI_ID+" from "+ TABLE_TAXIFOM_DATA+" order by "+KEY_SELECTDATE +" desc limit 100)");
        db.execSQL("DELETE FROM " + TABLE_TAXIFOM_DATA + " ;");

        db.close();
    }

    public void deleteSomeRow_LatLong()
    {
        SQLiteDatabase db = this.getWritableDatabase();
       // db.execSQL("delete from "+ TABLE_LATLONG+" where " +KEY_LATLONG_INCRIID+ " not in ( select " +KEY_LATLONG_INCRIID+" from "+ TABLE_LATLONG+" order by "+KEY_LATLONG_INCRIID +" desc limit 1000)");
        db.execSQL("DELETE FROM " + TABLE_LATLONG + " ;");
        db.close();
    }



    public void insert_feedbackREcordData(FeedbackRecordData feedbackRecordData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
       // values.put(KEY_ID, feedbackRecordData.getKeyid());
        values.put(KEY_FEEDBACK_RECORD_UNITNAME, feedbackRecordData.getfEEDBACK_RECORD_UNITNAME());
        values.put(KEY_FEEDBACK_RECORD_REFERENCENO, feedbackRecordData.getfEEDBACK_RECORD_REFERENCENO());
        values.put(KEY_FEEDBACK_RECORD_DATE,feedbackRecordData.getfEEDBACK_RECORD_DATE());
        values.put(KEY_FEEDBACK_RECORD_BREIF, feedbackRecordData.getfEEDBACK_RECORD_BREIF());
        values.put(KEY_FEEDBACK_RECORD_FLAG, feedbackRecordData.getfEEDBACK_RECORD_FLAG());
        values.put(KEY_FEEDBACK_RECORD_LAT, feedbackRecordData.getfEED_LAT());
        values.put(KEY_FEEDBACK_RECORD_LOG, feedbackRecordData.getfEED_LONG());





        // Inserting Row
        db.insert(TABLE_FEEDBACK_RECORD, null, values);
        Log.v(TAG, "Databaser insert feedbackRecord table");
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public List<FeedbackRecordData> getAllFeedbackRecord() {

        ArrayList<FeedbackRecordData> list = new ArrayList<FeedbackRecordData>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FEEDBACK_RECORD;
        SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        FeedbackRecordData data = new FeedbackRecordData();
                        //only one column
                        data.setfEEDBACK_RECORD_INCRIID(cursor.getInt(0));
                        data.setfEEDBACK_RECORD_UNITNAME(cursor.getString(1));
                        data.setfEEDBACK_RECORD_REFERENCENO(cursor.getString(2));
                        data.setfEEDBACK_RECORD_DATE(cursor.getString(3));
                        data.setfEEDBACK_RECORD_BREIF(cursor.getString(4));
                        data.setfEEDBACK_RECORD_FLAG(cursor.getInt(5));
                        data.setfEED_LAT(cursor.getString(6));
                        data.setfEED_LONG(cursor.getString(7));

                        //you could add additional columns here..

                        list.add(data);
                    } while (cursor.moveToNext());
                }


        return list;
    }


    public boolean updateFeedbackRecord( int incriid,int flag )
    {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues args = new ContentValues();
        args.put(KEY_FEEDBACK_RECORD_FLAG,flag);

        int i =  db.update(TABLE_FEEDBACK_RECORD, args, KEY_FEEDBACK_RECORD_INCRIID + "=" + incriid, null);
        return i > 0;
    }

    public void deleteFeedbackRecordTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_FEEDBACK_RECORD + " ;");
        db.close();
    }



    public void insertfeedbackAttachment(AttachmentData attachmentData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // values.put(KEY_ID, feedbackRecordData.getKeyid());
        values.put(KEY_FEEDBACK_ATT_REFNO, attachmentData.getRefNo());
        values.put(KEY_FEEDBACK_ATT_IMAGENAME, attachmentData.getAttachshow());
        values.put(KEY_FEEDBACK_ATT_IMAGE_FILE, attachmentData.getAttachsend());
        values.put(KEY_FEEDBACK_ATT_FLAG,attachmentData.getFlag());
        // Inserting Row
        db.insert(TABLE_FEEDBACK_ATTACHMENT, null, values);
        Log.v(TAG, "Databaser insert feedbackAttachmenttable");
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public List<AttachmentData> getAllFeedbackAttachment() {

        ArrayList<AttachmentData> list = new ArrayList<AttachmentData>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FEEDBACK_ATTACHMENT;
        SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.rawQuery(selectQuery, null);

                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        AttachmentData data = new AttachmentData();
                        //only one column
                        data.setIncriID(cursor.getInt(0));
                        data.setRefNo(cursor.getString(1));
                        data.setAttachshow(cursor.getString(2));
                        data.setAttachsend(cursor.getString(3));
                        data.setFlag(cursor.getInt(4));

                        //you could add additional columns here..

                        list.add(data);
                    } while (cursor.moveToNext());
                }



        return list;
    }


    public List<AttachmentData> getAllFeedbackAttachment_BY_Refno(String refno) {

        ArrayList<AttachmentData> list = new ArrayList<AttachmentData>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FEEDBACK_ATTACHMENT + " WHERE " +KEY_FEEDBACK_ATT_REFNO +" = ?" ;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, new String[]{refno});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AttachmentData data = new AttachmentData();
                //only one column
                data.setIncriID(cursor.getInt(0));
                data.setRefNo(cursor.getString(1));
                data.setAttachshow(cursor.getString(2));
                data.setAttachsend(cursor.getString(3));
                data.setFlag(cursor.getInt(4));

                //you could add additional columns here..

                list.add(data);
            } while (cursor.moveToNext());
        }



        return list;
    }


    public boolean updateFeedbackAttachment( int incriid,int flag )
    {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues args = new ContentValues();
        args.put(KEY_FEEDBACK_ATT_FLAG,flag);
        int i =  db.update(TABLE_FEEDBACK_ATTACHMENT, args, KEY_FEEDBACK_ATT_INCRIID + "=" + incriid, null);
        return i > 0;
    }

    public void deleteFeedbackAttachmentTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_FEEDBACK_ATTACHMENT + " ;");
        db.close();
    }

    public void insertfeedbackCapture(CaptureData captureData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // values.put(KEY_ID, feedbackRecordData.getKeyid());
        //values.put(KEY_FEEDBACK_CAPTURE_FNID, captureData.getFnID());

        values.put(KEY_FEEDBACK_CAPTURE_REFNO, captureData.getRefNo());
        values.put(KEY_FEEDBACK_CAPTURE_IMAGENAME, captureData.getCaptureImageshow());
        values.put(KEY_FEEDBACK_CAPTURE_IMAGE_FILE, captureData.getCaptureFilesend());
        values.put(KEY_FEEDBACK_CAPTURE_FLAG,captureData.getFlag());


        // Inserting Row
        db.insert(TABLE_FEEDBACK_CAPTURE, null, values);
        Log.v(TAG, "Databaser insert feedbackCapturetable");
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public List<CaptureData> getAllFeedbackCaputre() {


        ArrayList<CaptureData> list = new ArrayList<CaptureData>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FEEDBACK_CAPTURE;

        SQLiteDatabase db = this.getReadableDatabase();
        try {

            Cursor cursor = db.rawQuery(selectQuery, null);
            try {

                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        CaptureData data = new CaptureData();
                        //only one column
                        data.setIncri_id(cursor.getInt(0));
                       // data.setFnID(cursor.getInt(1));
                        data.setRefNo(cursor.getString(1));
                        data.setCaptureImageshow(cursor.getString(2));
                        data.setCaptureFilesend(cursor.getString(3));
                        data.setFlag(cursor.getInt(4));

                        //you could add additional columns here..

                        list.add(data);
                    } while (cursor.moveToNext());
                }

            } finally {
                try { cursor.close();

                }
                catch (Exception ignore) {}
            }

        } finally {
            try {
                db.close(); }
            catch (Exception ignore) {

            }
        }

        return list;
    }

    public List<CaptureData> getAllFeedbackCapture_BY_Refno(String refno) {

        ArrayList<CaptureData> list = new ArrayList<CaptureData>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FEEDBACK_CAPTURE + " WHERE " +KEY_FEEDBACK_CAPTURE_REFNO +" = ?" ;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, new String[]{refno});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CaptureData data = new CaptureData();
                //only one column
                data.setIncri_id(cursor.getInt(0));
                // data.setFnID(cursor.getInt(1));
                data.setRefNo(cursor.getString(1));
                data.setCaptureImageshow(cursor.getString(2));
                data.setCaptureFilesend(cursor.getString(3));
                data.setFlag(cursor.getInt(4));

                //you could add additional columns here..

                list.add(data);
            } while (cursor.moveToNext());
        }

        return list;
    }



    public boolean updateFeedbackCapture( int incriid,int flag  )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(KEY_FEEDBACK_CAPTURE_FLAG,flag);

        int i =  db.update(TABLE_FEEDBACK_CAPTURE, args, KEY_FEEDBACK_CAPTURE_INCRIID + "=" + incriid, null);

        return i > 0;

    }

    public void deleteFeedbackCaptureTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_FEEDBACK_CAPTURE + " ;");
        db.close();
    }

/*
    public boolean insertcontacts(String name, String status, String from, String image) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("status", status);
        contentValues.put("`from`", from);
        contentValues.put("image", image);
        db.insert("contacts", null, contentValues);
        return true;
    }*/

/*
    public void insertAllContacts() {
        for (Picture contact : Contact.getContacts()) {
            insertcontacts(contact.getName(), contact.getStatus(), contact.getFrom(), contact.getImage());
        }
    }
*/

    //....................................Notification...............................................

    public void add_DB_Notification(NotificationData notificationData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTIFICTION_TITTLE, notificationData.getNoti_tittle());
        values.put(KEY_NOTIFICATION_MESSAGE,notificationData.getNoti_message());
        values.put(KEY_NOTIFICATION_IMAGE, notificationData.getNoti_image());





        // Inserting Row
        db.insert(TABLE_NOTIFICATION, null, values);
        Log.v(TAG, "Databaser insert notification table");
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public List<NotificationData> getAllNotification() {

        ArrayList<NotificationData> list = new ArrayList<NotificationData>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NOTIFICATION;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                NotificationData data = new NotificationData();
                //only one column
                data.setNoti_id(cursor.getInt(0));
                data.setNoti_tittle(cursor.getString(1));
                data.setNoti_message(cursor.getString(2));
                data.setNoti_tittle(cursor.getString(3));
               data.setFlag(cursor.getInt(4));

                //you could add additional columns here..

                list.add(data);
            } while (cursor.moveToNext());
        }



        return list;
    }

    public void deleteSingleRow(String value)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NOTIFICATION + " WHERE " + KEY_NOTIFICATION_MESSAGE + "='" + value + "'");
        db.close();
    }


}