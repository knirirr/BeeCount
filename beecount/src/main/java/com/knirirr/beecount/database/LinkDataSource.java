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
public class LinkDataSource
{
  private SQLiteDatabase database;
  private DbHelper dbHelper;
  private String[] allColumns = {
      DbHelper.L_ID,
      DbHelper.L_PROJECT_ID,
      DbHelper.L_MASTER_ID,
      DbHelper.L_SLAVE_ID,
      DbHelper.L_INCREMENT,
      DbHelper.L_TYPE
  };

  public LinkDataSource(Context context)
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

  public Link createLink(long project_id, long master_id, long slave_id, int increment, int type)
  {
    ContentValues values = new ContentValues();
    values.put(DbHelper.L_PROJECT_ID, project_id);
    values.put(DbHelper.L_MASTER_ID, master_id);
    values.put(DbHelper.L_SLAVE_ID, slave_id);
    values.put(DbHelper.L_INCREMENT, increment);
    values.put(DbHelper.L_TYPE, type);

    long insertId = database.insert(DbHelper.LINK_TABLE, null, values);
    Cursor cursor = database.query(DbHelper.LINK_TABLE,
        allColumns, DbHelper.L_ID + " = " + insertId, null, null, null, null);
    cursor.moveToFirst();
    Link newLink = cursorToLink(cursor);
    cursor.close();
    return newLink;
  }

  private Link cursorToLink(Cursor cursor)
  {
    Link newlink = new Link();
    newlink.id = cursor.getLong(cursor.getColumnIndex(DbHelper.L_ID));
    newlink.project_id = cursor.getLong(cursor.getColumnIndex(DbHelper.L_PROJECT_ID));
    newlink.master_id = cursor.getLong(cursor.getColumnIndex(DbHelper.L_MASTER_ID));
    newlink.slave_id = cursor.getLong(cursor.getColumnIndex(DbHelper.L_SLAVE_ID));
    newlink.increment = cursor.getInt(cursor.getColumnIndex(DbHelper.L_INCREMENT));
    newlink.type = cursor.getInt(cursor.getColumnIndex(DbHelper.L_TYPE));
    return newlink;
  }


  public void deleteLink(Link link)
  {
    long id = link.id;
    database.delete(DbHelper.LINK_TABLE, DbHelper.L_ID + " = " + id, null);
    System.out.println("Link deleted with id: " + id);
  }

  public void deleteLinkById(long id)
  {
    database.delete(DbHelper.LINK_TABLE, DbHelper.L_ID + " = " + id, null);
    System.out.println("Link deleted with id: " + id);
  }

  public List<Link> getAllLinksForProject(long project_id)
  {
    List<Link> links = new ArrayList<Link>();

    Cursor cursor = database.query(DbHelper.LINK_TABLE, allColumns,
        DbHelper.L_PROJECT_ID + " = " + project_id, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast())
    {
      Link link = cursorToLink(cursor);
      links.add(link);
      cursor.moveToNext();
    }
    // Make sure to close the cursor
    cursor.close();
    return links;
  }

}
