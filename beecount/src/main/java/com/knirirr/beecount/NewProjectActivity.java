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
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.knirirr.beecount.database.Count;
import com.knirirr.beecount.database.Project;
import com.knirirr.beecount.database.CountDataSource;
import com.knirirr.beecount.database.ProjectDataSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;


public class NewProjectActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener
{
  private static String TAG = "BeeCountNewProjectActivity";
  BeeCountApplication beeCount;
  SharedPreferences prefs;
  int newBox;
  ViewGroup layout;
  private ArrayList<EditText> myTexts;
  private ArrayList<String> countNames;
  EditText newprojName;
  ProjectDataSource projectDataSource;
  CountDataSource countDataSource;


  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_project);

    beeCount = (BeeCountApplication) getApplication();
    prefs = BeeCountApplication.getPrefs();
    prefs.registerOnSharedPreferenceChangeListener(this);

    LinearLayout baseLayout = (LinearLayout) findViewById(R.id.newprojScreen);
    baseLayout.setBackgroundDrawable(beeCount.setBackground());

    // data access using CrowTrack method
    projectDataSource = new ProjectDataSource(this);
    countDataSource = new CountDataSource(this);

    // setup from previous version
    newBox = 1;
    layout = (ViewGroup) findViewById(R.id.newCountLayout);
    myTexts = new ArrayList<EditText>();
    newprojName = (EditText) findViewById(R.id.newprojName);
    countNames = new ArrayList<String>();

  }

  // the required pause and resume stuff
  @Override
  protected void onResume()
  {
    projectDataSource.open();
    countDataSource.open();
    super.onResume();
  }

  @Override
  protected void onPause() {
    projectDataSource.close();
    countDataSource.close();
    super.onPause();
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.new_project, menu);
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

  public void newCount(View view)
  {
    // attempt to add a new EditText to an array thereof
    //Log.i(TAG,"Adding new count!");
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT);
    params.setMargins(5, 5, 5, 5);
    EditText c = new EditText(this);
    c.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    c.setHint(this.getString(R.string.boxFill) + " " + newBox);
    c.setBackgroundResource(R.drawable.rounded_corner);
    c.setPadding(5,5,5,5);
    c.setTextSize(24);

    layout.addView(c, params);
    myTexts.add(c);
    newBox++;
  }

  public void clearCount(View view)
  {
    if (myTexts.isEmpty())
      return;
    int count_number = myTexts.size();
    ViewGroup layout = (ViewGroup) findViewById(R.id.newCountLayout);
    layout.removeView(myTexts.get(count_number -1));
    myTexts.remove(count_number -1);
    newBox--;
  }

  public void saveProject(View view)
  {
    // first, the project name
    String proj_name = newprojName.getText().toString();
    String count_name;

    if (myTexts.isEmpty())
    {
      Toast.makeText(this, getString(R.string.noCounts), Toast.LENGTH_SHORT).show();
      return;
    }

    // check that the boxes are filled in
    int carryon = 1;
    if (StringUtils.isBlank(proj_name))
    {
      carryon = 0;
    }
    for (EditText c : myTexts)
    {
      count_name = c.getText().toString();
      if (StringUtils.isBlank(count_name))
      {
        carryon = 0;
        break;
      }
    }
    if (carryon == 0)
    {
      Toast.makeText(this,getString(R.string.emptyBox),Toast.LENGTH_SHORT).show();
      return;
    }

    // check for unique names
    countNames.clear();
    for (EditText c : myTexts)
    {
      count_name = c.getText().toString();
      if (countNames.contains(count_name))
      {
        Toast.makeText(this,getString(R.string.duplicate),Toast.LENGTH_SHORT).show();
        return;
      }
      else
      {
        countNames.add(count_name);
      }
    }

    /*
     * Commence saving the project and its associated counts.
     */

    Project newProject = projectDataSource.createProject(proj_name); // might need to escape the name
    for (EditText c : myTexts)
    {
      count_name = c.getText().toString();
      Count newCount = countDataSource.createCount(newProject.id,count_name);
    }

    // Huzzah!
    Toast.makeText(this,getString(R.string.projectSaved),Toast.LENGTH_SHORT).show();
    //startActivity(new Intent(this, WelcomeActivity.class)); // later, go directly to a view
    super.finish();
  }

  // check for duplicates of counts
  // http://stackoverflow.com/questions/562894/java-detect-duplicates-in-arraylist
  public static <T> boolean hasDuplicate(Collection<T> list)
  {
    Set<T> set = new HashSet<T>();
    // Set#add returns false if the set does not change, which
    // indicates that a duplicate element has been added.
    for (T each: list) if (!set.add(each)) return true;
    return false;
  }

  public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
  {
    LinearLayout baseLayout = (LinearLayout) findViewById(R.id.newprojScreen);
    baseLayout.setBackgroundDrawable(beeCount.setBackground());
  }

}
