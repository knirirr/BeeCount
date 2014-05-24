package com.knirirr.beecount.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by milo on 05/05/2014.
 */
public class AlertDataSource
{
  // Database fields
  private SQLiteDatabase database;
  private DbHelper dbHelper;
  private String[] allColumns = {
      DbHelper.A_ID,
      DbHelper.A_COUNT_ID,
      DbHelper.A_ALERT,
      DbHelper.A_ALERT_TEXT
  };

  public AlertDataSource(Context context)
  {
    dbHelper = new DbHelper(context);
  }

  public void open() throws SQLException
  {
    database = dbHelper.getWritableDatabase();
  }

  public void close()
  {
    dbHelper.close();
  }

  public Alert createAlert(long count_id)
  {
    ContentValues values = new ContentValues();
    values.put(DbHelper.A_COUNT_ID, count_id);
    values.put(DbHelper.A_ALERT, 0);
    values.put(DbHelper.A_ALERT_TEXT, "");

    long insertId = database.insert(DbHelper.ALERT_TABLE, null, values);
    Cursor cursor = database.query(DbHelper.ALERT_TABLE,
        allColumns, DbHelper.A_ID + " = " + insertId, null,
        null, null, null);
    cursor.moveToFirst();
    Alert newAlert = cursorToAlert(cursor);
    cursor.close();
    return newAlert;
  }

  private Alert cursorToAlert(Cursor cursor)
  {
    Alert newalert = new Alert();
    newalert.id = cursor.getLong(cursor.getColumnIndex(DbHelper.A_ID));
    newalert.count_id = cursor.getLong(cursor.getColumnIndex(DbHelper.A_COUNT_ID));
    newalert.alert = cursor.getInt(cursor.getColumnIndex(DbHelper.A_ALERT));
    newalert.alert_text = cursor.getString(cursor.getColumnIndex(DbHelper.A_ALERT_TEXT));
    return newalert;
  }

  public void deleteAlert(Alert alert)
  {
    long id = alert.id;
    System.out.println("Alert deleted with id: " + id);
    database.delete(DbHelper.ALERT_TABLE, DbHelper.A_ID + " = " + id, null);
  }

  public List<Alert> getAllAlertsForCount(long count_id)
  {
    List<Alert> alerts = new ArrayList<Alert>();

    Cursor cursor = database.query(DbHelper.ALERT_TABLE, allColumns,
        DbHelper.A_COUNT_ID + " = " + count_id, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast())
    {
      Alert alert = cursorToAlert(cursor);
      alerts.add(alert);
      cursor.moveToNext();
    }
    // Make sure to close the cursor
    cursor.close();
    return alerts;
  }

}
