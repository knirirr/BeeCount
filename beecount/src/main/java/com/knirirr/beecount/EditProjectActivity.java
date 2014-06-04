package com.knirirr.beecount;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.knirirr.beecount.database.Alert;
import com.knirirr.beecount.database.AlertDataSource;
import com.knirirr.beecount.database.Count;
import com.knirirr.beecount.database.CountDataSource;
import com.knirirr.beecount.database.Link;
import com.knirirr.beecount.database.LinkDataSource;
import com.knirirr.beecount.database.Project;
import com.knirirr.beecount.database.ProjectDataSource;
import com.knirirr.beecount.widgets.AlertCreateWidget;
import com.knirirr.beecount.widgets.CountEditWidget;
import com.knirirr.beecount.widgets.CountingWidget;
import com.knirirr.beecount.widgets.EditTitleWidget;
import com.knirirr.beecount.widgets.NotesWidget;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class EditProjectActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener
{
  BeeCountApplication beeCount;
  SharedPreferences prefs;
  public static String TAG = "BeeCountEditProjectActivity";

  // the actual data
  Project project;
  List<Count> counts;
  List<Link> links;

  private ProjectDataSource projectDataSource;
  private CountDataSource countDataSource;
  private AlertDataSource alertDataSource;
  private LinkDataSource linkDataSource;

  long project_id;
  LinearLayout counts_area;
  LinearLayout notes_area;
  LinearLayout links_area;
  EditTitleWidget etw;
  EditTitleWidget enw;
  private View markedForDelete;
  private long idToDelete;
  private AlertDialog.Builder are_you_sure;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_project);

    beeCount = (BeeCountApplication) getApplication();
    prefs = BeeCountApplication.getPrefs();
    prefs.registerOnSharedPreferenceChangeListener(this);

    ScrollView counting_screen = (ScrollView) findViewById(R.id.editingScreen);
    counting_screen.setBackgroundDrawable(beeCount.getBackground());

    notes_area = (LinearLayout) findViewById(R.id.editingNotesLayout);
    counts_area = (LinearLayout) findViewById(R.id.editingCountsLayout);
    links_area = (LinearLayout) findViewById(R.id.editingLinksLayout);
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    project_id = beeCount.project_id;

    // clear any existing views
    counts_area.removeAllViews();
    notes_area.removeAllViews();
    links_area.removeAllViews();

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
    getActionBar().setTitle(project.name);

    // display an editable project title
    etw = new EditTitleWidget(this,null);
    etw.setProjectName(project.name);
    etw.setWidgetTitle(getString(R.string.titleEdit));
    notes_area.addView(etw);

    // display editable project notes; the same class
    // is being used for both due to being lazy
    enw = new EditTitleWidget(this,null);
    enw.setProjectName(project.notes);
    enw.setWidgetTitle(getString(R.string.notesHere));
    notes_area.addView(enw);

    // counts
    counts = countDataSource.getAllCountsForProject(project.id);

    // display all the counts by adding them to countCountLayout
    for (Count count : counts)
    {
      // widget
      CountEditWidget cew = new CountEditWidget(this,null);
      cew.setCountName(count.name);
      cew.setCountId(count.id);
      counts_area.addView(cew);
    }

    // links
    links = linkDataSource.getAllLinksForProject(project_id);

    // display all the links
    for (Link link : links)
    {
      // widget

    }


  }

  @Override
  protected void onPause()
  {
    super.onPause();

    // save the data
    beeCount.project_id = project_id;

    // close the data sources
    projectDataSource.close();
    countDataSource.close();
    alertDataSource.close();
    linkDataSource.close();

  }

  public void saveAndExit(View view)
  {
    saveData();
    super.finish();
  }

  public void saveData()
  {
    // save title and notes
    boolean saveproject = false;
    String newtitle = etw.getProjectName();
    if (StringUtils.isNotEmpty(newtitle))
    {
      project.name = newtitle;
      saveproject = true;
    }
    String newnotes = enw.getProjectName();
    if (StringUtils.isNotEmpty(newnotes))
    {
      project.notes = newnotes;
      saveproject = true;
    }
    if (saveproject)
    {
      projectDataSource.saveProject(project);
    }

    // save counts
    int childcount = counts_area.getChildCount();
    for (int i=0; i < childcount; i++)
    {
      Log.i(TAG, "Childcount: " + String.valueOf(childcount));
      CountEditWidget cew = (CountEditWidget) counts_area.getChildAt(i);
      if (StringUtils.isNotEmpty(cew.getCountName()))
      {
        Log.i(TAG, "CEW: " + String.valueOf(cew.countId) + ", " + cew.getCountName());
        // save or create
        if (cew.countId == 0)
        {
          Log.i(TAG, "Creating!");
          countDataSource.createCount(project_id,cew.getCountName());
        }
        else
        {
          Log.i(TAG, "Updating!");
          countDataSource.updateCountName(cew.countId,cew.getCountName());
        }
      }
      else
      {
        Log.i(TAG, "Failed to save count: " + cew.countId);
      }
    }

    // save/create links
  }

  /*
   * These are the methods for adding new widgets to the relevant section of the screen.
   */

  public void newLink(View view)
  {

  }

  public void newCount(View view)
  {
    CountEditWidget cew = new CountEditWidget(this,null);
    counts_area.addView(cew);
  }

  /*
   * These are required for purging counts (with associated alerts) and links (with associated counts and alerts)
   */

  public void deleteLink(View view)
  {
    // find counts and delete them, deleting their alerts first

    // then delete link and remove widget from screen

  }

  public void deleteCount(View view)
  {

    /*
     * These global variables keep a track of the view containing an alert to be deleted and also the id
     * of the alert itself, to make sure that they're available inside the code for the alert dialog by
     * which they will be deleted.
     */
    markedForDelete = view;
    idToDelete = (Long) view.getTag();
    if (idToDelete == 0)
    {
      // the actual CountEditWidget is two levels up from the button in which it is embedded
      counts_area.removeView((CountEditWidget) view.getParent().getParent());
    }
    else
    {
      //Log.i(TAG, "(2) View tag was " + String.valueOf(deleteAnAlert));
      // before removing this widget it is necessary to do the following:
      // (1) Check the user is sure they want to delete it and, if so...
      // (2) Delete the associated alert from the database.
      are_you_sure = new AlertDialog.Builder(this);
      are_you_sure.setTitle(getString(R.string.deleteCount));
      are_you_sure.setMessage(getString(R.string.reallyDeleteCount));
      are_you_sure.setPositiveButton(R.string.yesDeleteIt, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface dialog, int whichButton)
        {
          // go ahead for the delete
          countDataSource.deleteCountById(idToDelete);
          counts_area.removeView((CountEditWidget) markedForDelete.getParent().getParent());
        }
      });
      are_you_sure.setNegativeButton(R.string.noCancel, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface dialog, int whichButton)
        {
          // Cancelled.
        }
      });
      are_you_sure.show();
    }

  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.edit_project, menu);
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
    ScrollView counting_screen = (ScrollView) findViewById(R.id.editingScreen);
    counting_screen.setBackgroundDrawable(null);
    counting_screen.setBackgroundDrawable(beeCount.setBackground());
  }
}
