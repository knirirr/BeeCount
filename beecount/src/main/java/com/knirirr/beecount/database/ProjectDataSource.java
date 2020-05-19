package com.knirirr.beecount.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


/**
 * Created by milo on 05/05/2014.
 */
public class ProjectDataSource
{
  // Database fields
  private SQLiteDatabase database;
  private DbHelper dbHelper;
  private String[] allColumns = {
      DbHelper.P_ID,
      DbHelper.P_CREATED_AT,
      DbHelper.P_LOGS,
      DbHelper.P_NAME,
      DbHelper.P_NOTES
  };

  public ProjectDataSource(Context context)
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

  public Project createProject(String name)
  {
    Date date = new Date();
    long timeMilliseconds = date.getTime();
    ContentValues values = new ContentValues();
    values.put(DbHelper.P_NAME, name);
    values.put(DbHelper.P_CREATED_AT, timeMilliseconds);

    long insertId = database.insert(DbHelper.PROJ_TABLE, null, values);
    Cursor cursor = database.query(DbHelper.PROJ_TABLE,
        allColumns, DbHelper.P_ID + " = " + insertId, null,
        null, null, null);
    cursor.moveToFirst();
    Project newProject = cursorToProject(cursor);
    cursor.close();
    return newProject;
  }

  private Project cursorToProject(Cursor cursor)
  {
    Project project = new Project();
    project.id = cursor.getLong(cursor.getColumnIndex(DbHelper.P_ID));
    project.created_at = cursor.getLong(cursor.getColumnIndex(DbHelper.P_CREATED_AT));
    project.name = cursor.getString(cursor.getColumnIndex(DbHelper.P_NAME));
    project.notes = cursor.getString(cursor.getColumnIndex(DbHelper.P_NOTES));
    project.logs = cursor.getString(cursor.getColumnIndex(DbHelper.P_LOGS));
    return project;
  }

  public void deleteProject(Project project)
  {
    long id = project.id;
    System.out.println("Project deleted with id: " + id);
    database.delete(DbHelper.PROJ_TABLE, DbHelper.P_ID + " = " + id, null);

    /*
    Get the id of all associated counts here; alerts are the only things which can't
    be removed directly as the project_id is not stored in them. A join is therefore required.
     */
    // delete associated links and counts
    String sql = "DELETE FROM " + DbHelper.ALERT_TABLE + " WHERE " + DbHelper.A_COUNT_ID + " IN "
        + "(SELECT " + DbHelper.C_ID + " FROM " + DbHelper.COUNT_TABLE + " WHERE "
        + DbHelper.C_PROJECT_ID + " = " + id + ")";
    database.execSQL(sql);
    database.delete(DbHelper.LINK_TABLE, DbHelper.L_PROJECT_ID  + " = " + id, null);
    database.delete(DbHelper.COUNT_TABLE, DbHelper.C_PROJECT_ID  + " = " + id, null);

  }

  public void saveProject(Project project)
  {
    ContentValues dataToInsert = new ContentValues();
    dataToInsert.put(DbHelper.P_NAME, project.name);
    dataToInsert.put(DbHelper.P_NOTES, project.notes);
    dataToInsert.put(DbHelper.P_LOGS, project.logs);
    String where = DbHelper.P_ID + " = ?";
    String[] whereArgs = {String.valueOf(project.id)};
    database.update(DbHelper.PROJ_TABLE, dataToInsert, where, whereArgs);
  }

  public List<Project> getAllProjects(SharedPreferences prefs)
  {
    List<Project> projects = new ArrayList<Project>();

    String orderBy =  DbHelper.P_CREATED_AT + " DESC";
    String sortString = prefs.getString("pref_sort", "date_desc");
    if (sortString.equals("date_asc"))
    {
      orderBy =  DbHelper.P_CREATED_AT + " ASC";
    }
    else if (sortString.equals("name_asc"))
    {
      orderBy =  DbHelper.P_NAME + " ASC";
    }
    else if (sortString.equals("name_desc"))
    {
      orderBy =  DbHelper.P_NAME + " DESC";
    }
    Cursor cursor = database.query(DbHelper.PROJ_TABLE, allColumns, null, null, null, null, orderBy);

    cursor.moveToFirst();
    while (!cursor.isAfterLast())
    {
      Project project = cursorToProject(cursor);
      projects.add(project);
      cursor.moveToNext();
    }
    // Make sure to close the cursor
    cursor.close();
    return projects;
  }

  public List<Project> getAllProjects()
  {
    List<Project> projects = new ArrayList<Project>();

    Cursor cursor = database.query(DbHelper.PROJ_TABLE, allColumns, null, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast())
    {
      Project project = cursorToProject(cursor);
      projects.add(project);
      cursor.moveToNext();
    }
    // Make sure to close the cursor
    cursor.close();
    return projects;
  }

  public Project getProject(long project_id)
  {
    Project project = null;
    Cursor cursor = database.query(DbHelper.PROJ_TABLE, allColumns, DbHelper.P_ID + " = ?", new String[] { String.valueOf(project_id) }, null, null, null);
    cursor.moveToFirst();
    project = cursorToProject(cursor);
    // Make sure to close the cursor
    cursor.close();
    return project;
  }
}
