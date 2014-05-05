package com.knirirr.beecount.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
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

  public Project createProject(String name, String notes)
  {
    Date date = new Date();
    long timeMilliseconds = date.getTime();
    ContentValues values = new ContentValues();
    values.put(DbHelper.P_NAME, name);
    values.put(DbHelper.P_NOTES, notes);
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
    project.setId(cursor.getLong(cursor.getColumnIndex(DbHelper.P_ID)));
    project.setCreatedAt(cursor.getInt(cursor.getColumnIndex(DbHelper.P_CREATED_AT)));
    project.setName(cursor.getString(cursor.getColumnIndex(DbHelper.P_NAME)));
    project.setNotes(cursor.getString(cursor.getColumnIndex(DbHelper.P_NOTES)));
    return project;
  }

  public void deleteProject(Project project)
  {
    long id = project.getId();
    System.out.println("Project deleted with id: " + id);
    database.delete(DbHelper.PROJ_TABLE, DbHelper.P_ID + " = " + id, null);

    /*
    TODO
    Get the id of all associated counts here; use this to delete all alerts.
    Then, proceed as below to delete links and counts.
     */

    // delete associated links and counts
    database.delete(DbHelper.LINK_TABLE, DbHelper.L_PROJECT_ID  + " = " + id, null);
    database.delete(DbHelper.COUNT_TABLE, DbHelper.C_PROJECT_ID  + " = " + id, null);
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
}
