package com.knirirr.beecount;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;


public class WelcomeActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener
{

  private static String TAG = "BeeCountWelcomeActivity";
  BeeCountApplication beeCount;
  SharedPreferences prefs;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);

    beeCount = (BeeCountApplication) getApplication();
    prefs = BeeCountApplication.getPrefs();
    prefs.registerOnSharedPreferenceChangeListener(this);

    /*
    String backgroundPref = prefs.getString("pref_back", "default");
    Boolean fontPref = prefs.getBoolean("pref_font", true);
    String pictPref = prefs.getString("imagePath", "");
    */

    LinearLayout baseLayout = (LinearLayout) findViewById(R.id.baseLayout);
    baseLayout.setBackgroundDrawable(beeCount.setBackground());

  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.welcome, menu);
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
    LinearLayout baseLayout = (LinearLayout) findViewById(R.id.baseLayout);
    baseLayout.setBackgroundDrawable(beeCount.setBackground());
  }

}
