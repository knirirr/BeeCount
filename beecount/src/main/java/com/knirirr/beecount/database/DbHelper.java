package com.knirirr.beecount.database;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.knirirr.beecount.R;


/**
 * Created by milo on 05/05/2014.
 */
public class DbHelper extends SQLiteOpenHelper
{
  static final String TAG = "BeeCount DB";
  static final String DATABASE = "beecount.db";
  static final int VERSION = 11;
  static final String PROJ_TABLE = "projects";
  static final String COUNT_TABLE = "counts";
  static final String LINK_TABLE = "links";
  static final String ALERT_TABLE = "alerts";
  public static final String P_ID = "_id";
  public static final String P_CREATED_AT = "created_at";
  public static final String P_NAME = "name";
  public static final String P_NOTES = "notes";
  public static final String C_ID = "_id";
  public static final String C_PROJECT_ID = "project_id";
  public static final String C_COUNT = "count";
  public static final String C_NAME = "name";
  public static final String C_AUTO_RESET = "auto_reset";
  public static final String C_RESET_LEVEL = "reset_level";
  public static final String C_ALERT = "alert";
  public static final String C_ALERT_TEXT = "alert_text";
  public static final String C_NOTES = "notes";
  public static final String C_MULTIPLIER = "multiplier";
  public static final String L_ID = "_id";
  public static final String L_PROJECT_ID = "project_id";
  public static final String L_MASTER_ID = "master_id";
  public static final String L_SLAVE_ID = "slave_id";
  public static final String L_MASTER = "master"; // deprecated
  public static final String L_SLAVE = "slave"; // deprecated
  public static final String L_INCREMENT = "increment";
  public static final String L_TYPE = "type";
  public static final String A_ID = "_id";
  public static final String A_COUNT_ID = "count_id";
  public static final String A_ALERT = "alert";
  public static final String A_ALERT_TEXT = "alert_text";

  private Context mContext;

  // constructor
  public DbHelper(Context context)
  {
    super(context, DATABASE, null, VERSION);
    this.mContext = context;
  }

  // called once on database creation
  @Override
  public void onCreate(SQLiteDatabase db)
  {
    Log.i(TAG, "Creating database: " + DATABASE);
    String sql = "create table " + PROJ_TABLE + " (" + P_ID + " integer primary key, " +
        P_CREATED_AT + " int, " + P_NAME + " text, " + P_NOTES + " text)";
    db.execSQL(sql);
    sql = "create table " + COUNT_TABLE + " (" + C_ID + " integer primary key, " + C_PROJECT_ID +
        " int, " + C_COUNT + " int, " + C_NAME + " text, " + C_AUTO_RESET + " int, " +
        C_RESET_LEVEL + " int default 0, " + C_NOTES + " text default NULL, " + C_MULTIPLIER + " int default 1)";
    db.execSQL(sql);
    sql = "create table " + LINK_TABLE + " (" + L_ID + " integer primary key, " + L_PROJECT_ID +
        " int, " + L_MASTER_ID + " int, " + L_SLAVE_ID + " int, " + L_INCREMENT + " int, " +
        L_TYPE + " int)"; // type: 0 = reset, 1 = increment
    db.execSQL(sql);
    sql = "create table " + ALERT_TABLE + " (" + A_ID + " integer primary key, " + A_COUNT_ID +
        " int, " + A_ALERT + " int, " + A_ALERT_TEXT + " text)";
    db.execSQL(sql);
    Log.i(TAG, "Success!");
  }

  // called if newVersion != oldVersion
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {

    if (oldVersion == 10)
    {
      version_11(db,oldVersion,newVersion);
    }
    if (oldVersion == 9)
    {
      version_10(db,oldVersion,newVersion);
      version_11(db,oldVersion,newVersion);
    }
    if (oldVersion == 8)
    {
      version_9(db,oldVersion,newVersion);
      version_10(db,oldVersion,newVersion);
      version_11(db,oldVersion,newVersion);
    }
    if (oldVersion == 7)
    {
      version_8(db,oldVersion,newVersion);
      version_9(db,oldVersion,newVersion);
      version_10(db,oldVersion,newVersion);
      version_11(db,oldVersion,newVersion);
    }
    if (oldVersion == 6)
    {
      version_7(db,oldVersion,newVersion);
      version_8(db,oldVersion,newVersion);
      version_9(db,oldVersion,newVersion);
      version_10(db,oldVersion,newVersion);
      version_11(db,oldVersion,newVersion);
    }
    if (oldVersion == 5)
    {
      version_6(db,oldVersion,newVersion);
      version_7(db,oldVersion,newVersion);
      version_8(db,oldVersion,newVersion);
      version_9(db,oldVersion,newVersion);
      version_10(db,oldVersion,newVersion);
      version_11(db,oldVersion,newVersion);
    }
    if (oldVersion == 4)
    {
      version_5(db,oldVersion,newVersion);
      version_6(db,oldVersion,newVersion);
      version_7(db,oldVersion,newVersion);
      version_8(db,oldVersion,newVersion);
      version_9(db,oldVersion,newVersion);
      version_10(db,oldVersion,newVersion);
      version_11(db,oldVersion,newVersion);
    }
    else if (oldVersion == 3)
    {
      version_4(db,oldVersion,newVersion);
      version_5(db,oldVersion,newVersion);
      version_6(db,oldVersion,newVersion);
      version_7(db,oldVersion,newVersion);
      version_8(db,oldVersion,newVersion);
      version_9(db,oldVersion,newVersion);
      version_10(db,oldVersion,newVersion);
      version_11(db,oldVersion,newVersion);
    }
    else if (oldVersion == 2)
    {
      version_3(db,oldVersion,newVersion);
      version_4(db,oldVersion,newVersion);
      version_5(db,oldVersion,newVersion);
      version_6(db,oldVersion,newVersion);
      version_7(db,oldVersion,newVersion);
      version_8(db,oldVersion,newVersion);
      version_9(db,oldVersion,newVersion);
      version_10(db,oldVersion,newVersion);
      version_11(db,oldVersion,newVersion);
    }
    else if (oldVersion == 1)
    {
      version_2(db,oldVersion,newVersion);
      version_3(db,oldVersion,newVersion);
      version_4(db,oldVersion,newVersion);
      version_5(db,oldVersion,newVersion);
      version_6(db,oldVersion,newVersion);
      version_7(db,oldVersion,newVersion);
      version_8(db,oldVersion,newVersion);
      version_9(db,oldVersion,newVersion);
      version_10(db,oldVersion,newVersion);
      version_11(db,oldVersion,newVersion);
    }
  }

  public void version_2(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    String sql = "create table " + LINK_TABLE + " (" + L_ID + " integer primary key, " +
        L_PROJECT_ID + " int, " + L_MASTER + " text, " + L_SLAVE + " text, " + L_INCREMENT + " int)";
    db.execSQL(sql);
    Log.i(TAG, "Upgraded database to version 2!");
  }

  public void version_3(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    String sql = "alter table " + LINK_TABLE + " add column " + L_TYPE + " int";
    db.execSQL(sql);
    sql = "update " + LINK_TABLE + " set " + L_TYPE + " = 1 where " + L_TYPE + " is NULL";
    db.execSQL(sql);
    Log.i(TAG, "Upgraded database to version 3!");
  }

  public void version_4(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    String sql = "alter table " + COUNT_TABLE + " add column " + C_AUTO_RESET + " int";
    db.execSQL(sql);
    sql = "alter table " + COUNT_TABLE + " add column " + C_ALERT + " int";
    db.execSQL(sql);
    sql = "alter table " + COUNT_TABLE + " add column " + C_ALERT_TEXT + " text";
    db.execSQL(sql);
    Log.i(TAG, "Upgraded database to version 4!");
  }

  /*
   * this is here because it seems that I cocked up earlier and somehow some
   * previous versions didn't update properly.
   */
  public void version_5(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    String sql;
    try
    {
      sql = "alter table " + COUNT_TABLE + " add column " + C_AUTO_RESET + " int";
      db.execSQL(sql);
      Log.i(TAG, "Missing auto_reset column added to counts!");
    }
    catch (Exception e)
    {
      Log.i(TAG, "Column already present: " + e.toString());
    }
    try
    {
      sql = "alter table " + COUNT_TABLE + " add column " + C_ALERT + " int";
      db.execSQL(sql);
      Log.i(TAG, "Missing alert column added to counts!");
    }
    catch (Exception e)
    {
      Log.i(TAG, "Column already present: " + e.toString());
    }
    try
    {
      sql = "alter table " + COUNT_TABLE + " add column " + C_ALERT_TEXT + " int";
      db.execSQL(sql);
      Log.i(TAG, "Missing alert_text column added to counts!");
    }
    catch (Exception e)
    {
      Log.i(TAG, "Column already present: " + e.toString());
    }
    try
    {
      sql = "alter table " + LINK_TABLE + " add column " + L_TYPE + " int";
      db.execSQL(sql);
      sql = "update " + LINK_TABLE + " set " + L_TYPE + " = 1 where " + L_TYPE + " is NULL";
      db.execSQL(sql);
      Log.i(TAG, "Missing type column added to links!");
    }
    catch (Exception e)
    {
      Log.i(TAG, "Column already present: " + e.toString());
    }
    Log.i(TAG, "Upgraded database to version 5!");
  }

  public void version_6(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    String sql = "alter table " + COUNT_TABLE + " add column " + C_RESET_LEVEL + " int default 0";
    db.execSQL(sql);
    Log.i(TAG, "Upgraded database to version 6!");
  }

  public void version_7(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    String sql = "alter table " + PROJ_TABLE + " add column " + P_NOTES + " text";
    db.execSQL(sql);
    Log.i(TAG, "Upgraded database to version 7!");
  }

  public void version_8(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    String sql = "create table " + ALERT_TABLE + " (" + A_ID + " integer primary key, " +
        A_COUNT_ID + " int, " + A_ALERT + " int, " + A_ALERT_TEXT + " text)";
    db.execSQL(sql);
    sql = "create table count_backup (" + C_ID + " integer primary key, " + C_PROJECT_ID +
        " int, " + C_COUNT + " int, " + C_NAME + " text, " + C_AUTO_RESET + " int, " +
        C_RESET_LEVEL + " int default 0)";
    db.execSQL(sql);
    sql = "INSERT INTO count_backup SELECT " + C_ID + "," + C_PROJECT_ID + "," + C_COUNT + "," +
        C_NAME + "," + C_AUTO_RESET + "," + C_RESET_LEVEL + " FROM " + COUNT_TABLE;
    db.execSQL(sql);
    sql = "DROP TABLE " + COUNT_TABLE;
    db.execSQL(sql);
    sql = "create table " + COUNT_TABLE + "(" + C_ID + " integer primary key, " + C_PROJECT_ID +
        " int, " + C_COUNT + " int, " + C_NAME + " text, " + C_AUTO_RESET + " int, " +
        C_RESET_LEVEL + " int default 0)";
    db.execSQL(sql);
    sql = "INSERT INTO " + COUNT_TABLE + " SELECT " + C_ID + "," + C_PROJECT_ID + "," + C_COUNT +
        "," + C_NAME + "," + C_AUTO_RESET + "," + C_RESET_LEVEL + " FROM count_backup";
    db.execSQL(sql);
    sql = "DROP TABLE count_backup";
    db.execSQL(sql);
    Log.i(TAG, "Upgraded database to version 8!");
  }

  public void version_9(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    /*
    Convert master and slave in the links from text to int;
    */
    Log.i(TAG, "Upgrading to version 9...");
    String sql = "create table temp_link_table (" + L_ID + " integer primary key, " + L_PROJECT_ID +
        " int, " + L_MASTER_ID + " int, " + L_SLAVE_ID + " int, " + L_INCREMENT + " int, " +
        L_TYPE + " int)"; // type: 0 = reset, 1 = increment
    db.execSQL(sql);
    Cursor cursor = db.query(DbHelper.LINK_TABLE, new String[]{DbHelper.L_ID,
            DbHelper.L_PROJECT_ID, DbHelper.L_MASTER, DbHelper.L_SLAVE, DbHelper.L_INCREMENT,
            DbHelper.L_TYPE},  null, null, null, null, null );
    boolean upgrade_fail = false;
    while (cursor.moveToNext())
    {
      try
      {
        long projId = cursor.getLong(cursor.getColumnIndex(DbHelper.L_PROJECT_ID));
        long linkId = cursor.getLong(cursor.getColumnIndex(DbHelper.L_ID));
        int increment = cursor.getInt(cursor.getColumnIndex(DbHelper.L_INCREMENT));
        int type = cursor.getInt(cursor.getColumnIndex(DbHelper.L_TYPE));
        String masterName = cursor.getString(cursor.getColumnIndex(DbHelper.L_MASTER));
        String slaveName = cursor.getString(cursor.getColumnIndex(DbHelper.L_SLAVE));
        Log.i(TAG, "Master, slave: " + masterName + ", " + slaveName);

        Cursor master_cursor = db.query(DbHelper.COUNT_TABLE, new String[]{DbHelper.C_ID,
                DbHelper.C_NAME}, DbHelper.C_PROJECT_ID + " =? and " + DbHelper.C_NAME + " =? ",
            new String[]{String.valueOf(projId), masterName}, null, null, null
        );
        master_cursor.moveToFirst();
        long masterId = master_cursor.getLong(master_cursor.getColumnIndex(DbHelper.C_ID));
        //Log.i(TAG, "Master cursor: " + String.valueOf(masterId));
        master_cursor.close();

        Cursor slave_cursor = db.query(DbHelper.COUNT_TABLE, new String[]{DbHelper.C_ID,
                DbHelper.C_NAME}, DbHelper.C_PROJECT_ID + " =? and " + DbHelper.C_NAME + " =? ",
            new String[]{String.valueOf(projId), slaveName}, null, null, null
        );
        slave_cursor.moveToFirst();
        long slaveId = slave_cursor.getLong(slave_cursor.getColumnIndex(DbHelper.C_ID));
        //Log.i(TAG, "Slave cursor: " + String.valueOf(slaveId));
        slave_cursor.close();

        ContentValues values = new ContentValues();
        values.put(DbHelper.L_ID, linkId);
        values.put(DbHelper.L_PROJECT_ID, projId);
        values.put(DbHelper.L_MASTER_ID, masterId);
        values.put(DbHelper.L_SLAVE_ID, slaveId);
        values.put(DbHelper.L_INCREMENT, increment);
        values.put(DbHelper.L_TYPE, type);
        long insertId = db.insert("temp_link_table", null, values);
        //Log.i(TAG, "Inserted: " + String.valueOf(insertId));
      }
      catch (Exception e) // generic exception because I don't know if it's just CursorOutOfBounds which is occurring here
      {
        upgrade_fail = true;
      }
    }

    // could not copy all links
    if (upgrade_fail)
    {
      Toast.makeText(this.mContext, this.mContext.getString(R.string.upgradeFail), Toast.LENGTH_LONG).show();
    }

    // move the table
    sql = "DROP TABLE " + DbHelper.LINK_TABLE;
    db.execSQL(sql);
    sql = "create table " + LINK_TABLE + " (" + L_ID + " integer primary key, " + L_PROJECT_ID +
          " int, " + L_MASTER_ID + " int, " + L_SLAVE_ID + " int, " + L_INCREMENT + " int, " +
          L_TYPE + " int)"; // type: 0 = reset, 1 = increment
    db.execSQL(sql);
    sql = "INSERT INTO " + DbHelper.LINK_TABLE + " SELECT * FROM temp_link_table";
    db.execSQL(sql);
    sql = "DROP TABLE temp_link_table";
    db.execSQL(sql);
    Log.i(TAG, "Upgraded database to version 9!");
    cursor.close();
  }

  public void version_10(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    String sql = "alter table " + COUNT_TABLE + " add column " + C_NOTES + " text default NULL";
    db.execSQL(sql);
    Log.i(TAG, "Upgraded database to version 10!");
  }

  public void version_11(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    String sql = "alter table " + COUNT_TABLE + " add column " + C_MULTIPLIER + " int default 1";
    db.execSQL(sql);
    Log.i(TAG, "Upgraded database to version 11!");
  }


}
