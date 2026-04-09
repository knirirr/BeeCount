package com.knirirr.beecount;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import org.woheller69.freeDroidWarn.FreeDroidWarn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import sheetrock.panda.changelog.ChangeLog;


public class WelcomeActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{

  private static String TAG = "BeeCountWelcomeActivity";
  BeeCountApplication beeCount;
  SharedPreferences prefs;
  ChangeLog cl;
  AlertDialog alert;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);

    FreeDroidWarn.showWarningOnUpgrade(this, BuildConfig.VERSION_CODE);

    beeCount = (BeeCountApplication) getApplication();
    prefs = BeeCountApplication.getPrefs();
    prefs.registerOnSharedPreferenceChangeListener(this);
    boolean tastePref = prefs.getBoolean("pref_dark_theme", false);
    if (tastePref) {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }
    else {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    //LinearLayout baseLayout = (LinearLayout) findViewById(R.id.baseLayout);
    ScrollView baseLayout = (ScrollView) findViewById(R.id.baseLayout);
    // TODO: Properly clear up references to this
    //baseLayout.setBackgroundDrawable(beeCount.getBackground());

    // a title isn't necessary on this welcome screen as it appears below
    //getSupportActionBar().setTitle("");

    cl = new ChangeLog(this);
    if (cl.firstRun())
      cl.getLogDialog().show();
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
    else if (id == R.id.exportMenu)
    {
      exportDb();
      return true;
    }
    else if (id == R.id.importMenu)
    {
      importDb();
      return true;
    }
    else if (id == R.id.changeLog)
    {
      cl.getFullLogDialog().show();
      return true;
    }
    else if (id == R.id.newProject)
    {
      startActivity(new Intent(this, NewProjectActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
      return true;
    }
    else if (id == R.id.viewProjects)
    {
      startActivity(new Intent(this, ListProjectActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
      return true;
    }


    return super.onOptionsItemSelected(item);
  }

  public void newProject(View view)
  {
    startActivity(new Intent(this, NewProjectActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
  }

  public void viewProjects(View view)
  {
    startActivity(new Intent(this, ListProjectActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
  }

  public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
  {
    //LinearLayout baseLayout = (LinearLayout) findViewById(R.id.baseLayout);
    ScrollView baseLayout = (ScrollView) findViewById(R.id.baseLayout);
    //baseLayout.setBackgroundDrawable(null);
    //baseLayout.setBackgroundDrawable(beeCount.setBackground());
    boolean tastePref = prefs.getBoolean("pref_dark_theme", false);
    if (tastePref) {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }
    else {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
  }

  /*
   * The three activities below are for exporting and importing the database. They've been put here because
   * no database should be open at this point.
   */
  private final ActivityResultLauncher<Intent> exportFileLauncher = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
      if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
        Uri uri = result.getData().getData();
        File infile = getApplicationContext().getDatabasePath("beecount.db");

        try (FileOutputStream out = (FileOutputStream) getContentResolver().openOutputStream(uri);
             FileInputStream in = new FileInputStream(infile)) {
          if (out == null) {
            throw new IOException("OutputStream is null");
          }

          byte[] buf = new byte[1024];
          int len;
          while ((len = in.read(buf)) > 0) {
              out.write(buf, 0, len);
          }
          Toast.makeText(getApplicationContext(), getString(R.string.saveWin), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
          Log.e(TAG, "Failed to export database: " + e);
          Toast.makeText(getApplicationContext(), getString(R.string.saveFail), Toast.LENGTH_SHORT).show();
        }
      }
    }
  );

  @SuppressLint("SdCardPath")
  public void exportDb()
  {
    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    intent.setType("application/octet-stream");
    intent.putExtra(Intent.EXTRA_TITLE, "beecount.db");
    exportFileLauncher.launch(intent);
  }

  private final ActivityResultLauncher<Intent> importFileLauncher = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
      if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
        Uri uri = result.getData().getData();
        File outfile = getApplicationContext().getDatabasePath("beecount.db");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage(R.string.confirmImport).setCancelable(false)
                .setPositiveButton(R.string.importButton, (dialog, id) -> {
                  try (InputStream in = getContentResolver().openInputStream(uri);
                       FileOutputStream out = new FileOutputStream(outfile)) {

                    if (in == null) {
                      throw new IOException("InputStream is null");
                    }

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                      out.write(buf, 0, len);
                    }
                    Toast.makeText(getApplicationContext(), getString(R.string.importWin), Toast.LENGTH_SHORT).show();
                  } catch (IOException e) {
                    Log.e(TAG, "Failed to import database: " + e);
                    Toast.makeText(getApplicationContext(), getString(R.string.importFail), Toast.LENGTH_SHORT).show();
                  }
                })
                .setNegativeButton(R.string.importCancelButton, (dialog, id) -> dialog.cancel());
        alert = builder.create();
        alert.show();
      }
    }
  );

  @SuppressLint("SdCardPath")
  public void importDb() {
    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    intent.setType("application/octet-stream");
    importFileLauncher.launch(intent);
  }
}
