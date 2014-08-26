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

import java.lang.Math;

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
    //spr_widget.setParameterValue(0);
    spr_widget.setSize(18);
    calc_area.addView(spr_widget);

    sti_widget = new OptionsWidget(this,null);
    sti_widget.setInstructions(getString(R.string.toIncrease));
    //sti_widget.setParameterValue(0);
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
    int stitches = spr_widget.getParameterValue();
    int decStitches = sti_widget.getParameterValue();
    int finalStitches = stitches + decStitches; // work this out
    results.setText("");
    String result_string = "";

    if (stitches == 0 || decStitches == 0)
    {
      Toast.makeText(this,getString(R.string.dont_set_zero),Toast.LENGTH_SHORT).show();
      return;
    }

    // if the calculation has succeeded...
    int id = view.getId();
    if (id == R.id.increaseBalanced)
    {
      /*
      This code is directly plagiarised from this:
      http://www.thedietdiary.com/knittingfiend/tools/IncreaseEvenlySpace.html
      With permission, I hasten to add.
      */
      int knitBet2_1;
      int times2_1;
      int knitBet2_2;
      int times2_2;
      int knitBet2_3;
      int times2_3;
      int knitBeg2;
      int knitEnd2;
      int highTimes = stitches % decStitches;
      int lowTimes = decStitches - highTimes;
      double nonRoundBet = (double) stitches / (double) decStitches;
      int lowBet =  (int) Math.floor(nonRoundBet);
      int highBet = (int) Math.ceil(nonRoundBet);

      if(highTimes%2 == 1) // high repeated an odd number of times so in center.
      {
        times2_2 = highTimes;
        knitBet2_2 = highBet;
        times2_1= (int) Math.ceil(((double) lowTimes-1)/2);
        knitBet2_1 = lowBet;
        times2_3 = (int) Math.floor(((double) lowTimes-1)/2);
        knitBet2_3 = lowBet;
        knitEnd2 = (int) Math.floor((double) lowBet / 2);
        knitBeg2 = (int) Math.ceil((double) lowBet / 2);
      }
      else
      {
        times2_2 = lowTimes-1;
        knitBet2_2 = lowBet;
        times2_1 = (int) Math.ceil((double) highTimes/2);
        knitBet2_1 = highBet;
        times2_3 = (int) Math.floor((double) highTimes/2);
        knitBet2_3 = highBet;
        knitEnd2 = (int) Math.floor((double) lowBet/2);
        knitBeg2 = (int) Math.ceil((double) lowBet/2);

      }
      result_string = "Increase Balanced:\n";
      result_string += String.format(getString(R.string.increase_balanced),knitEnd2,
                                                                           knitBet2_1,
                                                                           times2_1,
                                                                           knitBet2_2,
                                                                           times2_2,
                                                                           knitBet2_3,
                                                                           times2_3,
                                                                           knitBeg2);
      result_string += "\n" + String.format(getString(R.string.number_on_needle),finalStitches);
    }
    else if (id == R.id.decreaseBalanced)
    {
      result_string = "Decrease Balanced:\n";
    }
    else if (id == R.id.increaseUnbalanced)
    {
      /*
       This code is also taken from the IncreaseEvenlySpace.html file, but as it was a bit simpler
       I was able to fiddle with the variable names without losing my place.
       */
      int knitBet1 = (int) Math.floor((double) stitches / (double) decStitches);
      result_string = "Increase Unbalanced:\n";
      result_string += String.format(getString(R.string.increase_unbalanced),knitBet1 - 1,decStitches,stitches - (knitBet1*decStitches));
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
        project.notes = project.notes + "\n" + result_text;
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
