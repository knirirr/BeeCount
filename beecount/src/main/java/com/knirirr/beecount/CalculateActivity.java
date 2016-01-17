package com.knirirr.beecount;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Locale;

/**
 * Created by milo on 25/08/2014.
 */
public class CalculateActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener
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
    spr_widget.setInstructions(getString(R.string.stitchTotal));
    spr_widget.setSize(18);
    calc_area.addView(spr_widget);

    sti_widget = new OptionsWidget(this,null);
    sti_widget.setInstructions(getString(R.string.toIncrease));
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
    int rows = sti_widget.getParameterValue();
    results.setText("");
    String result_string = "";

    if (stitches == 0 || rows == 0)
    {
      Toast.makeText(this,getString(R.string.dont_set_zero),Toast.LENGTH_SHORT).show();
      return;
    }

    // if the calculation has succeeded...
    try
    {
      int times = stitches / rows;
      int remainder = stitches % rows;
      int bottom_no = rows - remainder;
      int top_no = times + 1;

      result_string += String.format(getString(R.string.increaseOutput),ordinal(times),bottom_no,ordinal(top_no),remainder);
      //"Increase every #{times}th stitch #{bottom_no} times, and every #{top_no}th stitch #{remainder} times."


    }
    catch (Exception e)
    {
      Log.e(TAG, "Calculation exception: " + e.toString());
      result_string = getString(R.string.calcError);
    }

    // this should be set no matter what
    results.setText(result_string);

  }

  // from http://stackoverflow.com/questions/6810336/is-there-a-library-or-utility-in-java-to-convert-an-integer-to-its-ordinal
  public static String ordinal(int i)
  {
    String language = Locale.getDefault().getLanguage();
    if (language == "fr")
    {
      return String.valueOf(i) + "e";
    }
    else
    {
      return i % 100 == 11 || i % 100 == 12 || i % 100 == 13 ? i + "th" : i + new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"}[i % 10];
    }
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
