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
public class CountDataSource
{
  // Database fields
  private SQLiteDatabase database;
  private DbHelper dbHelper;
  private String[] allColumns = {
      DbHelper.C_ID,
      DbHelper.C_PROJECT_ID,
      DbHelper.C_COUNT,
      DbHelper.C_NAME,
      DbHelper.C_AUTO_RESET,
      DbHelper.C_RESET_LEVEL
  };

  public CountDataSource(Context context)
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

  public Count createCount(long count_id, String name)
  {
    ContentValues values = new ContentValues();
    values.put(DbHelper.C_NAME, name);
    values.put(DbHelper.C_PROJECT_ID, count_id);
    values.put(DbHelper.C_COUNT, 0);
    values.put(DbHelper.C_AUTO_RESET, 0);
    values.put(DbHelper.C_RESET_LEVEL, 0);

    long insertId = database.insert(DbHelper.COUNT_TABLE, null, values);
    Cursor cursor = database.query(DbHelper.COUNT_TABLE,
        allColumns, DbHelper.C_ID + " = " + insertId, null, null, null, null);
    cursor.moveToFirst();
    Count newCount = cursorToCount(cursor);
    cursor.close();
    return newCount;
  }

  private Count cursorToCount(Cursor cursor)
  {
    Count newcount = new Count();
    newcount.setId(cursor.getLong(cursor.getColumnIndex(DbHelper.C_ID)));
    newcount.setName(cursor.getString(cursor.getColumnIndex(DbHelper.C_NAME)));
    newcount.setProjectId(cursor.getLong(cursor.getColumnIndex(DbHelper.C_PROJECT_ID)));
    newcount.setCount(cursor.getInt(cursor.getColumnIndex(DbHelper.C_COUNT)));
    newcount.setAutoReset(cursor.getInt(cursor.getColumnIndex(DbHelper.C_AUTO_RESET)));
    newcount.setResetLevel(cursor.getInt(cursor.getColumnIndex(DbHelper.C_RESET_LEVEL)));
    return newcount;
  }

  public void deleteCount(Count count)
  {
    long id = count.getId();
    System.out.println("Count deleted with id: " + id);
    database.delete(DbHelper.COUNT_TABLE, DbHelper.C_ID + " = " + id, null);

    // delete associated alerts
    database.delete(DbHelper.ALERT_TABLE, DbHelper.A_COUNT_ID  + " = " + id, null);
  }

  public List<Count> getAllCountsForProject(long project_id)
  {
    List<Count> counts = new ArrayList<Count>();

    Cursor cursor = database.query(DbHelper.COUNT_TABLE, allColumns,
        DbHelper.C_PROJECT_ID + " = " + project_id, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast())
    {
      Count count = cursorToCount(cursor);
      counts.add(count);
      cursor.moveToNext();
    }
    // Make sure to close the cursor
    cursor.close();
    return counts;
  }
}
