package com.knirirr.beecount;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.knirirr.beecount.database.Project;
import com.knirirr.beecount.database.ProjectDataSource;
import com.knirirr.beecount.widgets.OptionsWidget;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by milo on 25/08/2014.
 */
public class CalculateActivity extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
  public static String TAG = "BeeCountCalculateActivity";
  ProjectDataSource projectDataSource;
  BeeCountApplication beeCount;
  SharedPreferences prefs;
  Long project_id;
  Project project;

  OptionsWidget spr_widget; // stitches per row
  OptionsWidget sti_widget; // stitches to increase
  LinearLayout calc_area;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_calculate);

    Bundle extras = getIntent().getExtras();
    if(extras !=null)
    {
      project_id = extras.getLong("project_id");
    }

    projectDataSource = new ProjectDataSource(this);
    projectDataSource.open();
    project = projectDataSource.getProject(project_id);

    beeCount = (BeeCountApplication) getApplication();
    //project_id = beeCount.project_id;
    prefs = BeeCountApplication.getPrefs();
    prefs.registerOnSharedPreferenceChangeListener(this);

    ScrollView counting_screen = (ScrollView) findViewById(R.id.calcScreen);
    counting_screen.setBackgroundDrawable(beeCount.getBackground());

    calc_area = (LinearLayout) findViewById(R.id.calc_area);

    spr_widget = new OptionsWidget(this,null);
    spr_widget.setInstructions(getString(R.string.perRow));
    spr_widget.setParameterValue(0);
    spr_widget.setSize(18);
    calc_area.addView(spr_widget);

    sti_widget = new OptionsWidget(this,null);
    sti_widget.setInstructions(getString(R.string.toIncrease));
    sti_widget.setParameterValue(0);
    sti_widget.setSize(18);
    calc_area.addView(sti_widget);

  }

  /*
    These methods respond do the four calculation buttons and insert the appropriate string into
    the results display box.
   */
  public void performCalculation(View view)
  {
    TextView results = (TextView) findViewById(R.id.calcResults);
    int spr = spr_widget.getParameterValue(); // stitches
    int sti = sti_widget.getParameterValue(); // decStitches
    results.setText("");
    String result_string = "";

    // if the calculation has succeeded...
    int id = view.getId();
    if (id == R.id.increaseBalanced)
    {
      result_string = "Increase Balanced:\n";
    }
    else if (id == R.id.decreaseBalanced)
    {
      result_string = "Decrease Balanced:\n";
    }
    else if (id == R.id.increaseUnbalanced)
    {
      // knitBet1
      // times1_1
      // knitEnd1
      // times1_1=decStitches;
      // p.times1_1.value=times1_1; // => spr
      // knitBet1 = Math.floor(divide(stitches,decStitches));
      // p.knitBet1.value=knitBet1-1;
      // knitEnd1 = stitches-(knitBet1)*decStitches;
      // p.knitEnd1.value=knitEnd1
      int knitBet1 = (int) java.lang.Math.floor((double) spr / (double) sti);
      int finalStitches = spr + sti; // work this out
      result_string = "Increase Unbalanced:\n";
      result_string += String.format(getString(R.string.increase_unbalanced),knitBet1 - 1,sti,spr - (knitBet1*sti));
      result_string += "\n" + String.format(getString(R.string.number_on_needle),finalStitches);

    }
    else if (id == R.id.decreaseUnbalanced)
    {
      result_string = "Decrease Unbalanced:\n";
    }

    // this should be set no matter what
    results.setText(result_string);

  }

  // copy the contents of the results box to the project notes and exit this activity
  public void saveAndExit(View view)
  {
    TextView results = (TextView) findViewById(R.id.calcResults);
    String result_text = results.getText().toString();
    if (StringUtils.isNotBlank(result_text))
    {
      if (StringUtils.isNotBlank(project.notes))
      {
        project.notes = project.notes + "\n\n" + result_text;
      }
      else
      {
        project.notes = result_text;
      }
    }
    projectDataSource.saveProject(project);
    Toast.makeText(this,getString(R.string.updating_with_calc),Toast.LENGTH_SHORT).show();
    super.finish();
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    if (projectDataSource == null)
    {
      projectDataSource.open();
    }

  }

  @Override
  protected void onPause()
  {
    super.onPause();
    projectDataSource.close();
  }

  public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
  {
    ScrollView calc_screen = (ScrollView) findViewById(R.id.calcScreen);
    calc_screen.setBackgroundDrawable(null);
    calc_screen.setBackgroundDrawable(beeCount.setBackground());
  }
}
