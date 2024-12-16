package com.knirirr.beecount;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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


public class NewProjectActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
  private static String TAG = "BeeCountNewProjectActivity";
  BeeCountApplication beeCount;
  SharedPreferences prefs;
  int newBox;
  private boolean dupPref;
  ViewGroup layout;
  private ArrayList<NewCount> myTexts;
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
    dupPref = prefs.getBoolean("pref_duplicate", true);

    LinearLayout baseLayout = (LinearLayout) findViewById(R.id.newprojScreen);
    //baseLayout.setBackgroundDrawable(beeCount.getBackground());

    // data access using CrowTrack method
    projectDataSource = new ProjectDataSource(this);
    countDataSource = new CountDataSource(this);

    // setup from previous version
    newBox = 1;
    layout = (ViewGroup) findViewById(R.id.newCountLayout);
    myTexts = new ArrayList<NewCount>();
    newprojName = (EditText) findViewById(R.id.newprojName);
    newprojName.setTextColor(Color.WHITE);
    countNames = new ArrayList<String>();

    if (savedInstanceState != null)
    {
      if (savedInstanceState.getSerializable("savedTexts") != null)
      {
        myTexts = (ArrayList<NewCount>) savedInstanceState.getSerializable("savedTexts");
        for (NewCount c : myTexts)
        {
          LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
              LinearLayout.LayoutParams.WRAP_CONTENT);
          params.setMargins(5, 5, 5, 5);
          c.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
          c.setBackgroundResource(R.drawable.rounded_corner);
          c.setPadding(5,5,5,5);
          c.setTextSize(24);
          c.setTextColor(Color.WHITE);
          layout.addView(c, params);
          newBox++;
        }
      }
    }

  }

  private SharedPreferences spGen;

  private boolean isSubmit;

  // the required pause and resume stuff
  @Override
  protected void onResume()
  {
    projectDataSource.open();
    countDataSource.open();
    super.onResume();
    isSubmit = false;
    spGen = getSharedPreferences("NewProjectActivity", MODE_PRIVATE);
    newprojName.setText(spGen.getString("editName", ""));
  }

  @Override
  protected void onSaveInstanceState(Bundle outState)
  {
    /*
     * Before these widgets can be serialised they must be removed from their parent, or else
     * trying to add them to a new parent causes a crash because they've already got one.
     */
    super.onSaveInstanceState(outState);
    for (NewCount c : myTexts)
    {
      try
      {
        ((ViewGroup) c.getParent()).removeView(c);
      }
      catch (java.lang.NullPointerException e)
      {
        Log.e(TAG, "Empty text box.");
      }
    }
    outState.putSerializable("savedTexts", myTexts);
  }

  @Override
  protected void onPause() {
    SharedPreferences.Editor  spEditor = spGen.edit();
    if(isSubmit){
      spEditor.putString("editName", "");
    }else{
      spEditor.putString("editName", newprojName.getText().toString());
    }
    spEditor.commit();
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
    NewCount c = new NewCount(this);
    c.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    c.setBackgroundResource(R.drawable.rounded_corner);
    c.setPadding(5,5,5,5);
    c.setTextSize(24);
    c.setTextColor(Color.WHITE);

    layout.addView(c, params);
    c.requestFocus();
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
    for (NewCount c : myTexts)
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
    if (dupPref)
    {
      countNames.clear();
      for (NewCount c : myTexts)
      {
        count_name = c.getText().toString();
        if (countNames.contains(count_name))
        {
          Toast.makeText(this, getString(R.string.duplicate), Toast.LENGTH_SHORT).show();
          return;
        }
        else
        {
          countNames.add(count_name);
        }
      }
    }

    /*
     * Commence saving the project and its associated counts.
     */

    Project newProject = projectDataSource.createProject(proj_name); // might need to escape the name
    for (NewCount c : myTexts)
    {
      count_name = c.getText().toString();
      Count newCount = countDataSource.createCount(newProject.id,count_name);
    }

    // Huzzah!
    Toast.makeText(this,getString(R.string.projectSaved),Toast.LENGTH_SHORT).show();
    isSubmit = true;
    // Instead of returning to the welcome screen, show the new project.
    //super.finish();
    Intent intent = new Intent(NewProjectActivity.this, ListProjectActivity.class);
    //intent.putExtra("project_id",newProject.id);
    startActivity(intent);
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
    baseLayout.setBackgroundDrawable(null);
    baseLayout.setBackgroundDrawable(beeCount.setBackground());
    dupPref = prefs.getBoolean("duplicate_counts", true);
  }

}
