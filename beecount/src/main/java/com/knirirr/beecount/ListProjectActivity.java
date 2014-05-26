package com.knirirr.beecount;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
  private ProjectListAdapter adapter;
  private static String TAG = "BeeCountListProjectActivity";
  BeeCountApplication beeCount;
  SharedPreferences prefs;
  List<Project> projects;
  ListView list;
  private Project p; // a project selected from the list

  /*
   * NEEDS LONG PRESS TO DELETE PROJECTS
   */

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_project);

    beeCount = (BeeCountApplication) getApplication();
    prefs = BeeCountApplication.getPrefs();
    prefs.registerOnSharedPreferenceChangeListener(this);

    LinearLayout list_view = (LinearLayout) findViewById(R.id.list_view);
    list_view.setBackgroundDrawable(beeCount.getBackground());
    list = (ListView) findViewById(android.R.id.list);

    // single tap selects an item for viewing
    list.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      @Override
      public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
      {
        //Take action here.
        p = projects.get(position);
        Intent intent = new Intent(ListProjectActivity.this, CountingActivity.class);
        intent.putExtra("project_id",p.id);
        startActivity(intent);
      }
    });

    // long press to delete a project
    list.setOnItemLongClickListener (new AdapterView.OnItemLongClickListener() {
      public boolean onItemLongClick(AdapterView parent, View view, final int position, long id) {
        p = projects.get(position);
        // http://developer.android.com/guide/topics/ui/dialogs.html#AlertDialog
        // could make the dialog central in the popup - to do later
        AlertDialog.Builder builder = new AlertDialog.Builder(ListProjectActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage(p.name + ": " + getString(R.string.confirmDelete)).setCancelable(false).setPositiveButton(R.string.deleteButton, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface dialog, int id)
          {
            // perform the deleting here
            projectDataSource.deleteProject(p);
            projects.remove(position);
            showData();
            list.invalidate();

          }
        }).setNegativeButton(R.string.cancelButton, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface dialog, int id)
          {
            dialog.cancel();
          }
        });
        AlertDialog alert = builder.create();
        alert.show();
        return true;
      }
    });
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    projectDataSource = new ProjectDataSource(this);
    projectDataSource.open();
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
    projects = projectDataSource.getAllProjects();
    adapter = new ProjectListAdapter(this, R.layout.listview_project_row, projects);
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
