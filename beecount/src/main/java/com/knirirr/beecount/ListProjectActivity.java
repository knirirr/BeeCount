package com.knirirr.beecount;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.knirirr.beecount.database.Project;
import com.knirirr.beecount.database.ProjectDataSource;

import java.util.ArrayList;
import java.util.List;


public class ListProjectActivity extends ListActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
  private ProjectDataSource projectDataSource;
  private static String TAG = "BeeCountListProjectActivity";
  BeeCountApplication beeCount;
  SharedPreferences prefs;
  ArrayAdapter<String> adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_project);

    beeCount = (BeeCountApplication) getApplication();
    prefs = BeeCountApplication.getPrefs();
    prefs.registerOnSharedPreferenceChangeListener(this);

    LinearLayout list_view = (LinearLayout) findViewById(R.id.list_view);
    list_view.setBackgroundDrawable(beeCount.setBackground());
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    showData();
  }

  @Override
  protected void onPause()
  {
    super.onPause();
    projectDataSource.close();
  }

  private void showData()
  {
    projectDataSource = new ProjectDataSource(this);
    projectDataSource.open();

    List<Project> values = projectDataSource.getAllProjects();

    ProjectListAdapter adapter = new ProjectListAdapter(this,
        R.layout.listview_project_row, values);
    setListAdapter(adapter);

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
      startActivity(new Intent(this, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
  {
    LinearLayout list_view = (LinearLayout) findViewById(R.id.list_view);
    list_view.setBackgroundDrawable(beeCount.setBackground());
  }
}
