package com.knirirr.beecount.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by milo on 05/05/2014.
 */
public class DbHelper extends SQLiteOpenHelper
{
  static final String TAG = "BeeCount DB";
  static final String DATABASE = "beecount.db";
  static final int VERSION = 8;
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
  public static final String L_ID = "_id";
  public static final String L_PROJECT_ID = "project_id";
  public static final String L_MASTER = "master";
  public static final String L_SLAVE = "slave";
  public static final String L_INCREMENT = "increment";
  public static final String L_TYPE = "type";
  public static final String A_ID = "_id";
  public static final String A_COUNT_ID = "count_id";
  public static final String A_ALERT = "alert";
  public static final String A_ALERT_TEXT = "alert_text";

  // constructor
  public DbHelper(Context context)
  {
    super(context, DATABASE, null, VERSION);
  }

  // called once on database creation
  @Override
  public void onCreate(SQLiteDatabase db)
  {
    Log.i(TAG, "Creating database: " + DATABASE);
    String sql = "create table " + PROJ_TABLE + " (" + P_ID + " integer primary key, " + P_CREATED_AT + " int, " + P_NAME + " text, " + P_NOTES + " text)";
    db.execSQL(sql);
    sql = "create table " + COUNT_TABLE + " (" + C_ID + " integer primary key, " + C_PROJECT_ID + " int, " + C_COUNT + " int, " + C_NAME + " text, " + C_AUTO_RESET + " int, " + C_RESET_LEVEL + " int default 0)";
    db.execSQL(sql);
    sql = "create table " + LINK_TABLE + " (" + L_ID + " integer primary key, " + L_PROJECT_ID + " int, " + L_MASTER + " text, " + L_SLAVE + " text, " + L_INCREMENT + " int, " + L_TYPE + " int)"; // type: 0 = reset, 1 = increment
    db.execSQL(sql);
    sql = "create table " + ALERT_TABLE + " (" + A_ID + " integer primary key, " + A_COUNT_ID + " int, " + A_ALERT + " int, " + A_ALERT_TEXT + " text)";
    db.execSQL(sql);
    Log.i(TAG, "Success!");
  }

  // called if newVersion != oldVersion
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    if (oldVersion == 7)
    {
      version_8(db,oldVersion,newVersion);
    }
    if (oldVersion == 6)
    {
      version_7(db,oldVersion,newVersion);
      version_8(db,oldVersion,newVersion);
    }
    if (oldVersion == 5)
    {
      version_6(db,oldVersion,newVersion);
      version_7(db,oldVersion,newVersion);
      version_8(db,oldVersion,newVersion);
    }
    if (oldVersion == 4)
    {
      version_5(db,oldVersion,newVersion);
      version_6(db,oldVersion,newVersion);
      version_7(db,oldVersion,newVersion);
      version_8(db,oldVersion,newVersion);
    }
    else if (oldVersion == 3)
    {
      version_4(db,oldVersion,newVersion);
      version_5(db,oldVersion,newVersion);
      version_6(db,oldVersion,newVersion);
      version_7(db,oldVersion,newVersion);
      version_8(db,oldVersion,newVersion);
    }
    else if (oldVersion == 2)
    {
      version_3(db,oldVersion,newVersion);
      version_4(db,oldVersion,newVersion);
      version_5(db,oldVersion,newVersion);
      version_6(db,oldVersion,newVersion);
      version_7(db,oldVersion,newVersion);
      version_8(db,oldVersion,newVersion);
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
    }
  }

  public void version_2(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    String sql = "create table " + LINK_TABLE + " (" + L_ID + " integer primary key, " + L_PROJECT_ID + " int, " + L_MASTER + " text, " + L_SLAVE + " text, " + L_INCREMENT + " int)";
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
    String sql = "create table " + ALERT_TABLE + " (" + A_ID + " integer primary key, " + A_COUNT_ID + " int, " + A_ALERT + " int, " + A_ALERT_TEXT + " text)";
    db.execSQL(sql);
    sql = "create table count_backup (" + C_ID + " integer primary key, " + C_PROJECT_ID + " int, " + C_COUNT + " int, " + C_NAME + " text, " + C_AUTO_RESET + " int, " + C_RESET_LEVEL + " int default 0)";
    db.execSQL(sql);
    sql = "INSERT INTO count_backup SELECT " + C_ID + "," + C_PROJECT_ID + "," + C_COUNT + "," + C_NAME + "," + C_AUTO_RESET + "," + C_RESET_LEVEL + " FROM " + COUNT_TABLE;
    db.execSQL(sql);
    sql = "DROP TABLE " + COUNT_TABLE;
    db.execSQL(sql);
    sql = "create table " + COUNT_TABLE + "(" + C_ID + " integer primary key, " + C_PROJECT_ID + " int, " + C_COUNT + " int, " + C_NAME + " text, " + C_AUTO_RESET + " int, " + C_RESET_LEVEL + " int default 0)";    Log.i(TAG, "Upgraded database to version 8!");
    db.execSQL(sql);
    sql = "INSERT INTO " + COUNT_TABLE + " SELECT " + C_ID + "," + C_PROJECT_ID + "," + C_COUNT + "," + C_NAME + "," + C_AUTO_RESET + "," + C_RESET_LEVEL + " FROM count_backup";
    db.execSQL(sql);
    sql = "DROP TABLE count_backup";
    db.execSQL(sql);
    Log.i(TAG, "Upgraded database to version 8!");
  }
}
