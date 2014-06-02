package com.knirirr.beecount;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
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
import com.knirirr.beecount.widgets.NotesWidget;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class CountingActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener
{
  private static String TAG = "BeeCountCountingActivity";
  private AlertDialog.Builder row_alert;
  BeeCountApplication beeCount;
  SharedPreferences prefs;
  long project_id;
  LinearLayout count_area;
  LinearLayout notes_area;

  // preferences
  private boolean toastPref;

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
    getPrefs();

    ScrollView counting_screen = (ScrollView) findViewById(R.id.countingScreen);
    counting_screen.setBackgroundDrawable(beeCount.getBackground());

    count_area = (LinearLayout) findViewById(R.id.countCountLayout);
    notes_area = (LinearLayout) findViewById(R.id.countNotesLayout);

    /*
     * Everything should be obtainable from the project_id.
     * It should not be possible to start this activity without a project_id having
     * been supplied, hence the error below not being caught.
     */
    /*
    Bundle bundle = getIntent().getExtras();
    if(bundle !=null)
    {
      project_id = bundle.getLong("project_id");
    }
    */

  }

  /*
   * So preferences can be loaded at the start, and also when a change is detected.
   */
  private void getPrefs()
  {
    toastPref = prefs.getBoolean("toast_away", false);
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    project_id = beeCount.project_id;

    // clear any existing views
    count_area.removeAllViews();
    notes_area.removeAllViews();

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
    Log.i(TAG,"Project ID: " + String.valueOf(project_id));
    project = projectDataSource.getProject(project_id);
    Log.i(TAG, "Got project: " + project.name);
    getActionBar().setTitle(project.name);
    List<String> extras = new ArrayList<String>();

    // counts
    countingWidgets = new ArrayList<CountingWidget>();
    counts = countDataSource.getAllCountsForProject(project.id);

    // display all the counts by adding them to countCountLayout
    alerts = new ArrayList<Alert>();
    for (Count count : counts)
    {
      CountingWidget widget = new CountingWidget(this,null);
      widget.setCount(count);
      countingWidgets.add(widget);
      count_area.addView(widget);
      if (count.auto_reset > 0)
      {
        extras.add(String.format(getString(R.string.willReset), count.name, count.reset_level, count.auto_reset));
      }

      // get add all alerts for this project
      List<Alert> tmpAlerts = alertDataSource.getAllAlertsForCount(count.id);
      for (Alert a : tmpAlerts)
      {
        alerts.add(a);
        extras.add(String.format(getString(R.string.willAlert), count.name, a.alert));
      }
    }
    links = linkDataSource.getAllLinksForProject(project_id);

    // display project notes
    if (project.notes != null)
    {
      if (!project.notes.isEmpty())
      {
        NotesWidget project_notes = new NotesWidget(this, null);
        project_notes.setNotes(project.notes);
        notes_area.addView(project_notes);
      }
    }

    // display summary of links; resets and alerts should already have
    // been dealt with during setup, above
    for (Link l : links)
    {
      String master = getCountFromId(l.master_id).count.name;
      String slave = getCountFromId(l.slave_id).count.name;
      if (l.type == 0)
      {
        extras.add(String.format(getString(R.string.willLinkReset), master, slave, l.increment));
      }
      else if (l.type == 1)
      {
        extras.add(String.format(getString(R.string.willLinkIncrease), master, slave, l.increment));
      }
      else if (l.type == 2)
      {
        extras.add(String.format(getString(R.string.willLinkDecrease), master, slave, l.increment));
      }
    }
    if (!extras.isEmpty())
    {
      NotesWidget extra_notes = new NotesWidget(this,null);
      extra_notes.setNotes(StringUtils.join(extras,"\n"));
      notes_area.addView(extra_notes);
    }
  }

  @Override
  protected void onPause()
  {
    super.onPause();

    // save the data
    saveData();
    beeCount.project_id = project_id;

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

  public void editProject(View view)
  {
    // some stuff to go here
  }

  //**************************************

  /*
   * The next few methods are called from a counting widget or from within one of the methods here,
   * to make sure that many nested counts &c. all work on each other.
   * It may well be the case that this means of identifying which widget to increase/decrease
   * by tagging the buttons with the relevant count ID is a bit crap. Suggestions welcome if so.
   * The countUp and countDown methods are overloaded so they can be called by a view tagged with
   * the id of the count to count, or by supplying that ID directly.
   */
  public void countUp(long count_id)
  {
    CountingWidget widget = getCountFromId(count_id);
    if (widget != null)
    {
      widget.countUp();
    }
    checkAlert(widget.count.id,widget.count.count);
    checkLink(widget.count.id, widget.count.count, true);
  }

  public void countDown(long count_id)
  {
    CountingWidget widget = getCountFromId(count_id);
    if (widget != null)
    {
      widget.countDown();
    }
    checkAlert(widget.count.id,widget.count.count);
    checkLink(widget.count.id,widget.count.count,false);
  }

  public void countUp(View view)
  {
    //Log.i(TAG, "View clicked: " + view.toString());
    //Log.i(TAG, "View tag: " + view.getTag().toString());
    long count_id = Long.valueOf(view.getTag().toString());
    CountingWidget widget = getCountFromId(count_id);
    if (widget != null)
    {
      widget.countUp();
    }
    checkAlert(widget.count.id,widget.count.count);
    checkLink(widget.count.id, widget.count.count, true);
    if (widget.count.auto_reset > 0)
      checkReset(widget.count);
  }

  public void countDown(View view)
  {
    //Log.i(TAG, "View clicked: " + view.toString());
    //Log.i(TAG, "View tag: " + view.getTag().toString());
    long count_id = Long.valueOf(view.getTag().toString());
    CountingWidget widget = getCountFromId(count_id);
    if (widget != null)
    {
      widget.countDown();
    }
    checkAlert(widget.count.id,widget.count.count);
    checkLink(widget.count.id,widget.count.count,false);
    if (widget.count.auto_reset > 0)
      checkReset(widget.count);
  }

  public void resetCount(long count_id)
  {
    CountingWidget widget = getCountFromId(count_id);
    widget.resetZero();
  }

  public void edit(View view)
  {
    long count_id = Long.valueOf(view.getTag().toString());
    Intent intent = new Intent(CountingActivity.this, CountOptionsActivity.class);
    intent.putExtra("count_id",count_id);
    startActivity(intent);
  }

  /*
   * This is the lookup to get a counting widget (with references to the
   * associated count) from the list of widgets.
   */
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

  /*
   * Link and alert checking...
   */
  public void checkAlert(long count_id, int count_value)
  {
    for (Alert a : alerts)
    {
      if (a.count_id == count_id && a.alert == count_value)
      {
        row_alert = new AlertDialog.Builder(this);
        row_alert.setTitle(getString(R.string.alertTitle));
        row_alert.setMessage(a.alert_text);
        row_alert.setNegativeButton("OK", new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface dialog, int whichButton)
          {
            // Cancelled.
          }
        });
        row_alert.show();
        break;
      }
    }
  }

  public void checkLink(long count_id, int count_value, boolean up)
  {
    for (Link l : links)
    {
      if (l.master_id == count_id && (count_value % l.increment == 0) && up)
      {
        if (l.type == 0) // reset
        {
          resetCount(l.slave_id);
          hasReset(l.master_id,l.slave_id);
        }
        else if (l.type == 1) // increase
        {
          countUp(l.slave_id);
          hasIncreased(l.master_id,l.slave_id);
        }
        else if (l.type == 2) // decrease
        {
          countDown(l.slave_id);
          hasDecreased(l.slave_id,l.master_id);
        }
      }
      else if (l.master_id == count_id && ((count_value + 1) % l.increment == 0) && !up)
      {
        if (l.type == 0) // reset
        {
          resetCount(l.slave_id);
          hasReset(l.master_id,l.slave_id);
        }
        else if (l.type == 1) // increment
        {
          countDown(l.slave_id);
          hasIncreased(l.master_id, l.slave_id);
        }
        else if (l.type == 2) // decrease
        {
          countUp(l.slave_id);
          hasDecreased(l.master_id, l.slave_id);
        }
      }
    }
  }

  // resetting might as well call the
  public void checkReset(Count count)
  {
    if (count.auto_reset == count.count)
    {
      resetCount(count.id);
      if (toastPref == false)
      {
        Toast.makeText(CountingActivity.this, String.format(getString(R.string.hasAutoReset),count.name), Toast.LENGTH_SHORT).show();
      }
    }
  }

  /*
   * Pop up various exciting messages if the user has not bothered to turn them off in the
   * settings...
   */

  private void hasIncreased(long master_id, long slave_id)
  {
    if (toastPref == false)
    {
      String master = getCountFromId(master_id).count.name;
      String slave = getCountFromId(slave_id).count.name;
      Toast.makeText(CountingActivity.this, String.format(getString(R.string.postIncr),master,slave), Toast.LENGTH_SHORT).show();
    }
  }

  private void hasDecreased(long master_id, long slave_id)
  {
    if (toastPref == false)
    {
      String master = getCountFromId(master_id).count.name;
      String slave = getCountFromId(slave_id).count.name;
      Toast.makeText(CountingActivity.this, String.format(getString(R.string.postDecr),master,slave), Toast.LENGTH_SHORT).show();
    }
  }

  private void hasReset(long master_id, long slave_id)
  {
    if (toastPref == false)
    {
      String master = getCountFromId(master_id).count.name;
      String slave = getCountFromId(slave_id).count.name;
      Toast.makeText(CountingActivity.this, String.format(getString(R.string.postReset),master,slave), Toast.LENGTH_SHORT).show();
    }
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
    ScrollView counting_screen = (ScrollView) findViewById(R.id.countingScreen);
    counting_screen.setBackgroundDrawable(beeCount.setBackground());
    getPrefs();
  }
}
