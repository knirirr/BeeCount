package com.knirirr.beecount;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.knirirr.beecount.database.Alert;
import com.knirirr.beecount.database.AlertDataSource;
import com.knirirr.beecount.database.Count;
import com.knirirr.beecount.database.CountDataSource;
import com.knirirr.beecount.database.Link;
import com.knirirr.beecount.database.LinkDataSource;
import com.knirirr.beecount.database.Project;
import com.knirirr.beecount.database.ProjectDataSource;
import com.knirirr.beecount.widgets.CountingWidget;

import java.util.ArrayList;
import java.util.List;


public class CountingActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener
{
  private static String TAG = "BeeCountCountingActivity";
  BeeCountApplication beeCount;
  SharedPreferences prefs;
  long project_id;
  LinearLayout count_area;

  // the actual data
  Project project;
  List<Count> counts;
  List<Alert> alerts;
  List<Link> links;

  List<CountingWidget> countingWidgets;

  private ProjectDataSource projectDataSource;
  private CountDataSource countDataSource;
  private AlertDataSource alertDataSource;
  private LinkDataSource linkDataSource;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_counting);

    beeCount = (BeeCountApplication) getApplication();
    prefs = BeeCountApplication.getPrefs();
    prefs.registerOnSharedPreferenceChangeListener(this);

    LinearLayout counting_screen = (LinearLayout) findViewById(R.id.countingScreen);
    counting_screen.setBackgroundDrawable(beeCount.setBackground());

    count_area = (LinearLayout) findViewById(R.id.countCountLayout);

    /*
     * Everything should be obtainable from the project_id.
     * It should not be possible to start this activity without a project_id having
     * been supplied, hence the error below not being caught.
     */
    Bundle extras = getIntent().getExtras();
    if(extras !=null)
    {
      project_id = extras.getLong("project_id");
    }
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    // setup the data sources
    projectDataSource = new ProjectDataSource(this);
    projectDataSource.open();

    countDataSource = new CountDataSource(this);
    countDataSource.open();
    alertDataSource = new AlertDataSource(this);
    alertDataSource.open();
    linkDataSource = new LinkDataSource(this);
    linkDataSource.open();

    // load the data
    // projects
    project = projectDataSource.getProject(project_id);
    Log.i(TAG, "Got project: " + project.name);
    getActionBar().setTitle(project.name);

    // counts
    countingWidgets = new ArrayList<CountingWidget>();
    counts = countDataSource.getAllCountsForProject(project.id);

    // display all the counts by adding them to countCountLayout
    for (Count count : counts)
    {
      CountingWidget widget = new CountingWidget(this,null);
      widget.setCount(count);
      countingWidgets.add(widget);
      count_area.addView(widget);
    }

  }

  @Override
  protected void onPause()
  {
    super.onPause();

    // save the data
    saveData();

    // close the data sources
    projectDataSource.close();
    countDataSource.close();
    alertDataSource.close();
    linkDataSource.close();

  }

  private void saveData()
  {
    Toast.makeText(CountingActivity.this, getString(R.string.projSaving) + " " + project.name + "!", Toast.LENGTH_SHORT).show();
    for (Count count : counts)
    {
      countDataSource.saveCount(count);
    }
  }

  public void saveAndExit(View view)
  {
    saveData();
    super.finish();
  }

  //**************************************

  /*
   * The next three methods are called from a counting widget.
   * It may well be the case that this means of identifying which widget to increase/decreaes
   * by tagging the buttons with the relevant count ID is a bit crap. Suggestions welcome if so.
   */
  public void countUp(View view)
  {
    Log.i(TAG, "View clicked: " + view.toString());
    Log.i(TAG, "View tag: " + view.getTag().toString());
    CountingWidget widget = getCountFromId(Long.valueOf(view.getTag().toString()));
    if (widget != null)
    {
      widget.countUp();
    }
  }

  public void countDown(View view)
  {
    Log.i(TAG, "View clicked: " + view.toString());
    Log.i(TAG, "View tag: " + view.getTag().toString());
    CountingWidget widget = getCountFromId(Long.valueOf(view.getTag().toString()));
    if (widget != null)
    {
      widget.countDown();
    }

  }

  public void edit(View view)
  {
    Log.i(TAG, "Would edit this count: " + view.toString());
  }

  public CountingWidget getCountFromId(long id)
  {
    for (CountingWidget widget : countingWidgets)
    {
      if (widget.count.id == id)
      {
        return widget;
      }
    }
    return null;
  }

  //**************************************

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.counting, menu);
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
    LinearLayout counting_screen = (LinearLayout) findViewById(R.id.countingScreen);
    counting_screen.setBackgroundDrawable(beeCount.setBackground());
  }
}
