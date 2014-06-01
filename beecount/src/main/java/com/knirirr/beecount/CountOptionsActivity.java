package com.knirirr.beecount;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.knirirr.beecount.database.AlertDataSource;
import com.knirirr.beecount.database.Count;
import com.knirirr.beecount.database.CountDataSource;
import com.knirirr.beecount.widgets.AddAlertWidget;
import com.knirirr.beecount.widgets.OptionsWidget;


public class CountOptionsActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener
{
  private static String TAG = "BeeCountCountOptionsActivity";
  BeeCountApplication beeCountApplication;
  SharedPreferences prefs;

  private Count count;
  private long count_id;
  private CountDataSource countDataSource;
  private AlertDataSource alertDataSource;

  LinearLayout static_widget_area;
  LinearLayout dynamic_widget_area;
  OptionsWidget ar_value_widget;
  OptionsWidget ar_level_widget;
  OptionsWidget curr_val_widget;
  AddAlertWidget aa_widget;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_count_options);

    beeCountApplication = (BeeCountApplication) getApplication();
    prefs = BeeCountApplication.getPrefs();
    prefs.registerOnSharedPreferenceChangeListener(this);

    ScrollView counting_screen = (ScrollView) findViewById(R.id.count_options);
    counting_screen.setBackgroundDrawable(beeCountApplication.setBackground());

    static_widget_area = (LinearLayout) findViewById(R.id.static_widget_area);
    dynamic_widget_area = (LinearLayout) findViewById(R.id.dynamic_widget_area);

    Bundle extras = getIntent().getExtras();
    if(extras !=null)
    {
      count_id = extras.getLong("count_id");
    }

  }

  @Override
  protected void onResume()
  {
    super.onResume();

    // clear any existing views
    static_widget_area.removeAllViews();
    dynamic_widget_area.removeAllViews();

    // get the data sources
    countDataSource = new CountDataSource(this);
    countDataSource.open();
    alertDataSource = new AlertDataSource(this);
    alertDataSource.open();

    count = countDataSource.getCountById(count_id);
    getActionBar().setTitle(count.name);

    // setup the static widgets in the following order
    // 1. Auto reset value (value at which reset is triggered)
    // 2. Auto reset level (value to which count rests)
    // 3. Current count value
    // 4. Alert add/remove
    ar_value_widget = new OptionsWidget(this,null);
    ar_value_widget.setInstructions(getString(R.string.setResetValue));
    ar_value_widget.setParameterValue(count.auto_reset);
    static_widget_area.addView(ar_value_widget);

    ar_level_widget = new OptionsWidget(this,null);
    ar_level_widget.setInstructions(String.format(getString(R.string.setResetLevel), count.name));
    ar_level_widget.setParameterValue(count.reset_level);
    static_widget_area.addView(ar_level_widget);

    curr_val_widget = new OptionsWidget(this,null);
    curr_val_widget.setInstructions(String.format(getString(R.string.editCountValue), count.name, count.count));
    curr_val_widget.setParameterValue(count.count);
    static_widget_area.addView(curr_val_widget);

    aa_widget = new AddAlertWidget(this,null);
    static_widget_area.addView(aa_widget);

    /*
     * There should be a method to add all counts in order to re-draw when one is deleted.
     */



  }

  @Override
  protected void onPause()
  {
    super.onPause();
    Toast.makeText(CountOptionsActivity.this, getString(R.string.projSaving) + " " + count.name + "!", Toast.LENGTH_SHORT).show();
    count.auto_reset = ar_value_widget.getParameterValue();
    count.reset_level = ar_level_widget.getParameterValue();
    count.count = curr_val_widget.getParameterValue();
    countDataSource.saveCount(count);

    /*
     * Get all the alerts from the dynamic_widget_area and save each one.
     */


    countDataSource.close();
    alertDataSource.close();

  }


  public void addAnAlert(View view)
  {
    Log.i(TAG, "Adding an alert!");
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.count_options, menu);
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
    ScrollView counting_screen = (ScrollView) findViewById(R.id.count_options);
    counting_screen.setBackgroundDrawable(beeCountApplication.setBackground());
  }

}
