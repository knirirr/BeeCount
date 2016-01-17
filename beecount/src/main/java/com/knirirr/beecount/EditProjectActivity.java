package com.knirirr.beecount;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

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
import com.knirirr.beecount.widgets.ExistingLinkWidget;
import com.knirirr.beecount.widgets.LinkEditWidget;
import com.knirirr.beecount.widgets.NotesWidget;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class EditProjectActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener
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
  LinearLayout existing_links_area;
  EditTitleWidget etw;
  EditTitleWidget enw;
  private View markedForDelete;
  private long idToDelete;
  private AlertDialog.Builder are_you_sure;
  private int too_many;
  public ArrayList<String> countNames;
  public ArrayList<Long> countIds;
  public ArrayList<LinkEditWidget> savedLinks;
  public ArrayList<CountEditWidget> savedCounts;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_project);

    countNames = new ArrayList<String>();
    countIds = new ArrayList<Long>();
    savedLinks = new ArrayList<LinkEditWidget>();
    savedCounts = new ArrayList<CountEditWidget>();

    notes_area = (LinearLayout) findViewById(R.id.editingNotesLayout);
    counts_area = (LinearLayout) findViewById(R.id.editingCountsLayout);
    links_area = (LinearLayout) findViewById(R.id.editingLinksLayout);
    existing_links_area = (LinearLayout) findViewById(R.id.existingLinksLayout);

    too_many = 10; // used for catching link loops

    Bundle extras = getIntent().getExtras();
    if(extras !=null)
    {
      project_id = extras.getLong("project_id");
    }

    /*
     * Restore any edit widgets the user has added previously
     */
    if (savedInstanceState != null)
    {
      if (savedInstanceState.getSerializable("savedLinks") != null)
      {
        savedLinks = (ArrayList<LinkEditWidget>) savedInstanceState.getSerializable("savedLinks");
      }
      if (savedInstanceState.getSerializable("savedCounts") != null)
      {
        savedCounts = (ArrayList<CountEditWidget>) savedInstanceState.getSerializable("savedCounts");
      }
    }

    beeCount = (BeeCountApplication) getApplication();
    //project_id = beeCount.project_id;
    prefs = BeeCountApplication.getPrefs();
    prefs.registerOnSharedPreferenceChangeListener(this);

    ScrollView counting_screen = (ScrollView) findViewById(R.id.editingScreen);
    counting_screen.setBackgroundDrawable(beeCount.getBackground());
  }

  @Override
  protected void onSaveInstanceState(Bundle outState)
  {
    /*
     * Before these widgets can be serialised they must be removed from their parent, or else
     * trying to add them to a new parent causes a crash because they've already got one.
     */
    super.onSaveInstanceState(outState);
    for (LinkEditWidget lew : savedLinks)
    {
      ((ViewGroup) lew.getParent()).removeView(lew);
    }
    outState.putSerializable("savedLinks", savedLinks);
    for (CountEditWidget cew : savedCounts)
    {
      ((ViewGroup)cew.getParent()).removeView(cew);
    }
    outState.putSerializable("savedCounts", savedCounts);
  }

  @Override
  protected void onResume()
  {
    super.onResume();

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
    getSupportActionBar().setTitle(project.name);

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
    enw.setHint(getString(R.string.notesHint));
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
    for (CountEditWidget cew: savedCounts)
    {
      counts_area.addView(cew);
    }

    // these are needed to fill the spinners for the link widgets
    getCountNames();

    // links
    links = linkDataSource.getAllLinksForProject(project_id);

    // display all the links
    // add to existing_links_area
    for (Link link : links)
    {
      ExistingLinkWidget elw = new ExistingLinkWidget(this,null);
      elw.setLinkId(link.id);
      elw.masterId = link.master_id;
      elw.slaveId = link.slave_id;
      elw.setInfo(countDataSource.getCountById(link.master_id).name,
          countDataSource.getCountById(link.slave_id).name,
          link.type,
          link.increment);
      existing_links_area.addView(elw);
    }
    for (LinkEditWidget lew: savedLinks)
    {
      links_area.addView(lew);
    }
  }

  @Override
  protected void onPause()
  {
    super.onPause();

    // close the data sources
    projectDataSource.close();
    countDataSource.close();
    alertDataSource.close();
    linkDataSource.close();

  }

  public void getCountNames()
  {
    /*
     * My plan here is that both the names and ids arrays contain the entries in the same
     * order, so I can link a count name to its id by knowing the index.
     */
    countNames.clear();
    countIds.clear();
    int childcount = counts_area.getChildCount();
    for (int i=0; i < childcount; i++)
    {
      CountEditWidget cew = (CountEditWidget) counts_area.getChildAt(i);
      String name = cew.getCountName();
      // ignore count widgets where the user has filled nothing in. Id will be 0
      // if this is a new count.
      if (StringUtils.isNotEmpty(name))
      {
        countNames.add(name);
        countIds.add(cew.countId);
      }
    }
    childcount = links_area.getChildCount();
    for (int i=0; i < childcount; i++)
    {
      LinkEditWidget lew = (LinkEditWidget) links_area.getChildAt(i);
      lew.setCountNames(countNames);
    }
  }

  public void saveAndExit(View view)
  {
    if (saveData())
      // empty the list of linkEditWidgets in case it hangs around to create a nuisance later
      savedCounts.clear();
      savedLinks.clear();
      super.finish();
  }

  public boolean saveData()
  {
    // first of all deal with the links, as some links will contain new counts which must be created
    String message = getString(R.string.failedToSave);
    ArrayList<LinkEditWidget> linkEditWidgets = new ArrayList<LinkEditWidget>();
    boolean stop = false;
    int childcount = links_area.getChildCount();
    for (int i=0; i < childcount; i++)
    {
      LinkEditWidget lew = (LinkEditWidget) links_area.getChildAt(i);
      linkEditWidgets.add(lew);
      //Log.i(TAG, "Master: " + lew.getMasterId() + ", Slave: " + lew.getSlaveId());
      Long mid = lew.getMasterId();
      Long sid = lew.getSlaveId();
      if (mid == sid)
      {
        if (mid == 0 && sid == 0) // both are new counts
        {
          if (lew.getMasterName().equals(lew.getSlaveName())) // check matching names
          {
            stop = true;
            message = getString(R.string.mismatch);
          }
          // if they don't match and are new counts then all is well
        }
        else
        {
          stop = true;
          message = getString(R.string.mismatch);
        }
      }
      if (lew.getLinkIncrement() <= 0)
      {
        stop = true;
        message = getString(R.string.zero);
      }
      /*
       * This fails to work and so has been hidden for now, in case it can be brought out later.
       */
      /*
      if (link_loop())
      {
        // add a message here
        message = "FRC, your links are broken!";
        stop = true;
      }
      */
    }
    if (stop)
    {
      Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
      return false;
    }

    // if we've got this far then the links in the link edit widgets should be OK, and links can be created from them
    ArrayList<String> newlyCreatedCounts = new ArrayList<String>();
    for (LinkEditWidget lew : linkEditWidgets)
    {
      long masterId = lew.getMasterId();
      long slaveId = lew.getSlaveId();
      if (masterId == 0) // new count
      {
        masterId = countDataSource.createCount(project_id,lew.getMasterName()).id;
        newlyCreatedCounts.add(lew.getMasterName());
      }
      if (slaveId == 0) // new count
      {
        slaveId = countDataSource.createCount(project_id,lew.getSlaveName()).id;
        newlyCreatedCounts.add(lew.getSlaveName());
      }
      linkDataSource.createLink(project_id,masterId,slaveId,lew.getLinkIncrement(),lew.getChoice());
    }

    // save title and notes only if they have changed
    boolean saveproject = false;
    String newtitle = etw.getProjectName();
    if (StringUtils.isNotEmpty(newtitle))
    {
      project.name = newtitle;
      saveproject = true;
    }
    String newnotes = enw.getProjectName();
    // Always add notes if the user has written some...
    if (StringUtils.isNotEmpty(newnotes))
    {
      project.notes = newnotes;
      saveproject = true;
    }
    //...if they haven't, only save if the current notes have a value (i.e.
    // they are trying to delete what's there.
    else
    {
      if (StringUtils.isNotEmpty(project.notes))
      {
        project.notes = newnotes;
        saveproject = true;
      }
    }
    if (saveproject)
    {
      projectDataSource.saveProject(project);
    }

    // save counts
    childcount = counts_area.getChildCount();
    for (int i=0; i < childcount; i++)
    {
      //Log.i(TAG, "Childcount: " + String.valueOf(childcount));
      CountEditWidget cew = (CountEditWidget) counts_area.getChildAt(i);
      if (StringUtils.isNotEmpty(cew.getCountName()))
      {
        //Log.i(TAG, "CEW: " + String.valueOf(cew.countId) + ", " + cew.getCountName());
        // save or create
        if (cew.countId == 0)
        {
          // make sure a count is not created twice if it's already been done in a link, above
          if (newlyCreatedCounts.contains(cew.getCountName()))
            continue;
          //Log.i(TAG, "Creating!");
          countDataSource.createCount(project_id,cew.getCountName());
        }
        else
        {
          //Log.i(TAG, "Updating!");
          countDataSource.updateCountName(cew.countId,cew.getCountName());
        }
      }
      else
      {
        Log.i(TAG, "Failed to save count: " + cew.countId);
      }
    }
    Toast.makeText(EditProjectActivity.this, getString(R.string.projSaving) + " " + project.name + "!", Toast.LENGTH_SHORT).show();


    return true;
  }

  /*
   * These are the methods for adding new widgets to the relevant section of the screen.
   */

  public void newLink(View view)
  {
    getCountNames(); // a new count may have been added
    LinkEditWidget lew = new LinkEditWidget(this,null);
    lew.setCountNames(countNames);
    lew.setCountIds(countIds);
    links_area.addView(lew);
    savedLinks.add(lew);
  }

  public void newCount(View view)
  {
    CountEditWidget cew = new CountEditWidget(this,null);
    counts_area.addView(cew);
    savedCounts.add(cew);
  }

  /*
   * These are required for purging counts (with associated alerts) and links (with associated counts and alerts)
   */

  public void deleteLink(View view)
  {
    markedForDelete = view;
    idToDelete = (Long) view.getTag();
    if (idToDelete == 0)
    {
      // the actual LinkEditWidget is three levels up from the button in which it is embedded
      links_area.removeView((LinkEditWidget) view.getParent().getParent().getParent());
    }
    else
    {
      // this link will be in the existing_links_area if it doesn't have an id of 0
      are_you_sure = new AlertDialog.Builder(this);
      are_you_sure.setTitle(getString(R.string.deleteLink));
      are_you_sure.setMessage(getString(R.string.reallyDeleteLink));
      are_you_sure.setPositiveButton(R.string.yesDeleteIt, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface dialog, int whichButton)
        {
          // go ahead with the delete
          linkDataSource.deleteLinkById(idToDelete);
          existing_links_area.removeView((ExistingLinkWidget) markedForDelete.getParent().getParent());
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
          // there may be links which reference this count, and they too must be deleted
          int childcount = existing_links_area.getChildCount();
          for (int i=0; i < childcount; i++)
          {
            ExistingLinkWidget elw = (ExistingLinkWidget) existing_links_area.getChildAt(i);
            if (elw.masterId == idToDelete || elw.slaveId == idToDelete)
            {
              linkDataSource.deleteLinkById(elw.linkId);
              existing_links_area.removeView(elw);
            }
          }
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
    getCountNames();

  }

  /*
   * The purpose of this function is to flip out and loop through all the linked counts, returning
   * false if it looks like there are too many nested links.
   */
  /*
  private boolean link_loop()
  {
    too_many = 0;
    counts = countDataSource.getAllCountsForProject(project_id);
    for (Count count : counts)
    {
      if (check_count(count) == false)
      {
        return false;
      }
    }
    return true;
  }

  private boolean check_count(Count count)
  {
    Log.i(TAG, "COUNT: " + String.valueOf(count.id) + ", " + count.name);
    Link link = linkDataSource.getLinkByMasterId(count.id);
    if (link != null)
    {
      Log.i(TAG, "LINK: " + String.valueOf(link.master_id) + ", " + String.valueOf(link.slave_id));
      Count new_count = countDataSource.getCountById(link.slave_id);
      if (new_count != null)
      {
        Log.i(TAG, "NEW COUNT: " + String.valueOf(new_count.id) + ", " + new_count.name);
        too_many = too_many + 1;
        Log.i(TAG,"TOO MANY: " + String.valueOf(too_many));
        if (too_many > 5)
        {
          return false;
        }
        check_count(new_count);
      }
      return true;
    }
    return true;
  }
  */


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
    }
    else if (id == R.id.home)
    {
      Intent intent = NavUtils.getParentActivityIntent(this);
      intent.putExtra("project_id",project_id);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
      NavUtils.navigateUpTo(this, intent);
    }
    else if (id == R.id.menuCalculate)
    {
      Intent intent = new Intent(EditProjectActivity.this, CalculateActivity.class);
      intent.putExtra("project_id",project_id);
      startActivity(intent);
    }
    else if (id == R.id.menuSaveExit)
    {
      if (saveData())
        super.finish();
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
