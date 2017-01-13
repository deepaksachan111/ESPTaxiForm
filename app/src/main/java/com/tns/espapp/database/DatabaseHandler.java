package com.tns.espapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
    private static final String TABLE_PICTURE = "add_picture";
    private static final String TABLE_LATLONG ="latlong";

    // Contacts Table Columns names


    // Contacts Table Columns names for add post data student


    private static final String KEY_INCRI_ID = "incri_id";
    private static final String KEY_SELECTDATE = "selectdate";
    private static final String KEY_SETFORMNO = "formno";
    private static final String KEY_PROJECTTYPE = "projecttype";
    private static final String KEY_VEHICLENO = "vehicleno";
    private static final String KEY_STARTKM = "startkm";
    private static final String KEY_STARTKM_IMAGE = "startkm_image";
    private static final String KEY_ENDKM = "endkm";
    private static final String KEY_ENDKM_IMAGE = "endkmimage";
    private static final String KEY_FLAG = "flag";

    // Table coloum name for save picture
    private static final String PICTURE_INCRI_ID = "incri_pic_id";
    private static final String PICTURE_COURSE = "pic_course";
    private static final String PICTURE_DATE = "pic_date";
    private static final String PICTURE_PHOTO = "pic_photo";

// Table LatLong Columns name

    private static final String KEY_LATLONG_INCRIID ="incri_latlongid";
    private static final String KEY_LATLONG_SETFORMNO="formno";
    private static final String KEY_LATLONG_DATE="lat_longdate";
    private static final String KEY_LATLONG_LAT ="lat";
    private static final String KEY_LATLONG_LONG ="long";
    private static final String KEY_LATLONG_FLAG ="latlong_flag";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        Log.v(TAG, "Databaser object created");
    }

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {




        String  CREATE_TABLE_ADD_POST_DATA = "CREATE TABLE " + TABLE_TAXIFOM_DATA + "(" + KEY_INCRI_ID + " integer primary key autoincrement,"
                + KEY_SELECTDATE + " TEXT," + KEY_SETFORMNO + " TEXT," + KEY_PROJECTTYPE + " TEXT," + KEY_VEHICLENO + " TEXT,"
                + KEY_STARTKM + " TEXT," + KEY_STARTKM_IMAGE + " TEXT," + KEY_ENDKM + " TEXT,"  + KEY_ENDKM_IMAGE + " TEXT,"  + KEY_FLAG + " integer" +")";


        String CREATE_EMPLOYEES_TABLE = "create table "
                + TABLE_PICTURE + " (" + PICTURE_INCRI_ID
                + " integer primary key autoincrement," + PICTURE_COURSE
                + " text," + PICTURE_DATE + " text,"
                + PICTURE_PHOTO + " text" +  ");";

        String CREATE_TABLE_LATlONGDATA = "create table "
                + TABLE_LATLONG + " (" + KEY_LATLONG_INCRIID
                + " integer primary key autoincrement," + KEY_LATLONG_SETFORMNO
                + " text,"    + KEY_LATLONG_DATE
                + " text,"+ KEY_LATLONG_LAT + " text,"
                + KEY_LATLONG_LONG + " text," + KEY_LATLONG_FLAG+ " integer"    +");";




        db.execSQL(CREATE_TABLE_ADD_POST_DATA);
        db.execSQL(CREATE_EMPLOYEES_TABLE);
        db.execSQL(CREATE_TABLE_LATlONGDATA);

        Log.v(TAG, "Database table created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {



        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAXIFOM_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PICTURE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LATLONG);
        // Create tables again
        onCreate(db);
    }




    // code to add the new addBus


    public void addTaxiformData(TaxiFormData taxiFormData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
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
        values.put(KEY_LATLONG_FLAG, latLongData.getLatlong_flag());






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

    public void deleteSingleRow(String value)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_TAXIFOM_DATA + " WHERE " + KEY_INCRI_ID + "='" + value + "'");
        db.close();
    }

    public void deleteSingleRow()
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
                data.setSelectdate(cursor.getString(1));
                data.setFormno(cursor.getString(2));
                data.setProjecttype(cursor.getString(3));
                data.setVechicleno(cursor.getString(4));
                data.setStartkm(cursor.getString(5));
                data.setStartkm_image(cursor.getString(6));
                data.setEndkm(cursor.getString(7));
                data.setEndkmimage(cursor.getString(8));
                data.setFlag(cursor.getInt(9));
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
  /*      List<LatLongData> List = new ArrayList<LatLongData>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_LATLONG;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                LatLongData data = new LatLongData();
                //contact.setID(Integer.parseInt(cursor.getString(0)));
                data.setId(cursor.getInt(0));
                data.setFormno(cursor.getString(1));
                data.setDate(cursor.getString(2));
                data.setLat(cursor.getString(3));
                data.setLongi(cursor.getString(4));
                data.setLatlong_flag(cursor.getInt(5));
                // Adding contact to list
                List.add(data);
            } while
                    (cursor.moveToNext());
        }

     finally {
        try { cursor.close(); } catch (Exception ignore) {}
    }

} finally {
        try { db.close(); } catch (Exception ignore) {}
        }

        return list;
        }*/




    // Getting AddBusData Count
    public int getAddBusCount() {
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


    public boolean updateLatLong(String form_no,double lat,double longi,int flag )
    {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues args = new ContentValues();

        args.put(KEY_LATLONG_LAT, lat);
        args.put(KEY_LATLONG_LONG, longi);
        args.put(KEY_LATLONG_FLAG,flag);


        int i =  db.update(TABLE_LATLONG, args, KEY_LATLONG_SETFORMNO + "=" + form_no, null);
        return i > 0;
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

    public void addPicture(Picture picture) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PICTURE_COURSE, picture.getCourse());
        values.put(PICTURE_DATE, picture.getDate());
        values.put(PICTURE_PHOTO, picture.getBmp());


        // Inserting Row
        db.insert(TABLE_PICTURE, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public ArrayList<Picture> getAllPicture() {
        ArrayList<Picture> contactArrayList = new ArrayList();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_PICTURE, null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            contactArrayList.add(new Picture(
                    cursor.getString(cursor.getColumnIndex(PICTURE_COURSE)),
                    cursor.getString(cursor.getColumnIndex(PICTURE_DATE)),
                    cursor.getString(cursor.getColumnIndex(PICTURE_PHOTO))
            ));


            cursor.moveToNext();
        }
        return contactArrayList;
    }

}