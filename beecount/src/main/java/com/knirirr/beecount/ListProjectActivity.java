package com.knirirr.beecount;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.knirirr.beecount.database.Project;
import com.knirirr.beecount.database.ProjectDataSource;

import java.util.List;


public class ListProjectActivity extends Activity
{
  private ProjectDataSource projectDataSource;
  private static String TAG = "BeeCountListProjectActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_project);

    projectDataSource  = new ProjectDataSource(this);
    projectDataSource.open();
  }

  @Override
  protected void onResume()
  {
    // return if there are no deliveries with which to deal
    List<Project> projects = projectDataSource.getAllProjects();
    for (Project p : projects)
    {
      Log.i(TAG, "PROJECT: " + p.toString());
      return;
    }
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.list_project, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_settings)
    {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
