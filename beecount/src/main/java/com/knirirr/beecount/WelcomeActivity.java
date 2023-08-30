package com.knirirr.beecount;

import static java.security.AccessController.getContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sheetrock.panda.changelog.ChangeLog;


public class WelcomeActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{

  private static String TAG = "BeeCountWelcomeActivity";
  BeeCountApplication beeCount;
  SharedPreferences prefs;
  ChangeLog cl;
  final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

  // import/export stuff
  File infile;
  File outfile;
  boolean mExternalStorageAvailable = false;
  boolean mExternalStorageWriteable = false;
  String state = Environment.getExternalStorageState();
  AlertDialog alert;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);

    beeCount = (BeeCountApplication) getApplication();
    prefs = BeeCountApplication.getPrefs();
    prefs.registerOnSharedPreferenceChangeListener(this);

    //LinearLayout baseLayout = (LinearLayout) findViewById(R.id.baseLayout);
    ScrollView baseLayout = (ScrollView) findViewById(R.id.baseLayout);
    baseLayout.setBackgroundDrawable(beeCount.getBackground());

    // a title isn't necessary on this welcome screen as it appears below
    getSupportActionBar().setTitle("");

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
    baseLayout.setBackgroundDrawable(null);
    baseLayout.setBackgroundDrawable(beeCount.setBackground());
  }

  /*
   * The three activities below are for exporting and importing the database. They've been put here because
   * no database should be open at this point.
   */


  private static final int CREATE_FILE = 1;

  @SuppressLint("SdCardPath")
  public void exportDb()
  {
    // if API level > 23
    // Need to do this to write the database file.
    /*
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
    {
      int hasWriteStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
      if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED)
      {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
      }
    }
    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;
    String state = Environment.getExternalStorageState();
    File outfile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"beecount.db");
    File infile = getApplicationContext().getDatabasePath("beecount.db");

    if (Environment.MEDIA_MOUNTED.equals(state))
    {
      // We can read and write the media
      mExternalStorageAvailable = mExternalStorageWriteable = true;
    }
    else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
    {
      // We can only read the media
      mExternalStorageAvailable = true;
      mExternalStorageWriteable = false;
    }
    else
    {
      // Something else is wrong. It may be one of many other states, but all we need
      //  to know is we can neither read nor write
      mExternalStorageAvailable = mExternalStorageWriteable = false;
    }

    if ((mExternalStorageAvailable == false) || ( mExternalStorageWriteable == false))
    {
      Log.e(TAG,"No sdcard access");
      Toast.makeText(this, getString(R.string.noCard), Toast.LENGTH_SHORT).show();
      return;
    }
    else
    {
      // export the db
      try
      {
        copy(infile, outfile);
        Toast.makeText(this,getString(R.string.saveWin),Toast.LENGTH_LONG).show();
        return;
      }
      catch (IOException e)
      {
        Log.e(TAG,"Failed to copy database: " + e.toString());
        Toast.makeText(this,getString(R.string.saveFail) + " " + e.toString(),Toast.LENGTH_SHORT).show();
        return;
      }
    }
     */
    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    intent.setType("application/octet-stream");
    intent.putExtra(Intent.EXTRA_TITLE, "beecount.db");

    // Optionally, specify a URI for the directory that should be opened in
    // the system file picker when your app creates the document.
    Uri uri = Uri.parse("file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
    //Toast.makeText(this, "Trying to export..." + getApplicationContext().getDatabasePath("beecount.db")  ,Toast.LENGTH_SHORT).show();
    intent.putExtra(Environment.DIRECTORY_DOWNLOADS, uri);

    startActivityForResult(intent, CREATE_FILE);

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (resultCode != RESULT_OK || data == null || data.getData() == null) {
      Log.e(TAG, "Failed to copy database.");
      Toast.makeText(this, getString(R.string.saveFail), Toast.LENGTH_SHORT).show();
    }

    if (requestCode == CREATE_FILE) {
      // Somehow work out how to get location and destination from the intent
      Uri uri = data.getData();
      InputStream inputStream = null;
      try {
        inputStream = getContentResolver().openInputStream(uri);
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
      Toast.makeText(this, String.valueOf(uri) ,Toast.LENGTH_LONG).show();
      Log.e(TAG,"AARS: " + String.valueOf(uri));
      File infile = new File("/data/data/com.knirirr.beecount/databases/beecount.db");
      try
      {
        copy(infile, inputStream);
        Toast.makeText(this,getString(R.string.saveWin),Toast.LENGTH_LONG).show();
      }
      catch (IOException e)
      {
        Log.e(TAG,"Failed to copy database: " + e.toString());
        Toast.makeText(this,getString(R.string.saveFail) + " " + e.toString(),Toast.LENGTH_LONG).show();
      }

    }

  }

  @SuppressLint("SdCardPath")
  public void importDb()
  {
    // permission to read db
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
    {
      int hasReadStoragePermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
      if (hasReadStoragePermission != PackageManager.PERMISSION_GRANTED)
      {
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
      }
    }
    infile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/beecount.db");
    File outfile = getApplicationContext().getDatabasePath("beecount.db");
    String extStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
    if(!(infile.exists()))
    {
      Toast.makeText(this,getString(R.string.noDb) + extStorage, Toast.LENGTH_LONG).show();
      return;
    }

    // a confirm dialogue before anything else takes place
    // http://developer.android.com/guide/topics/ui/dialogs.html#AlertDialog
    // could make the dialog central in the popup - to do later
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setIcon(android.R.drawable.ic_dialog_alert);
    builder.setMessage(R.string.confirmImport).setCancelable(false).setPositiveButton(R.string.importButton, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface dialog, int id)
      {
        // START
        // replace this with another function rather than this lazy c&p
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
          // We can read and write the media
          mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
        {
          // We can only read the media
          mExternalStorageAvailable = true;
          mExternalStorageWriteable = false;
        } else
        {
          // Something else is wrong. It may be one of many other states, but all we need
          //  to know is we can neither read nor write
          mExternalStorageAvailable = mExternalStorageWriteable = false;
        }


        if ((mExternalStorageAvailable == false) || (mExternalStorageWriteable == false))
        {
          Log.e(TAG, "No sdcard access");
          Toast.makeText(getApplicationContext(), getString(R.string.noCard), Toast.LENGTH_SHORT).show();
          return;
        } else
        {
          // TODO: Fix all this...
          //try
          //{

            //copy(infile, outfile);
            Toast.makeText(getApplicationContext(), getString(R.string.importWin), Toast.LENGTH_SHORT).show();
          //}
          /*
          catch (IOException e)
          {
            Log.e(TAG, "Failed to import database");
            Toast.makeText(getApplicationContext(), getString(R.string.importFail), Toast.LENGTH_SHORT).show();
            return;
          }
           */
        }

        // END
      }
    }).setNegativeButton(R.string.importCancelButton, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface dialog, int id)
      {
        dialog.cancel();
      }
    });
    alert = builder.create();
    alert.show();
  }

  // http://stackoverflow.com/questions/9292954/how-to-make-a-copy-of-a-file-in-android
  public void copy(File src, InputStream dst) throws IOException
  {
    FileInputStream in = new FileInputStream(src);
    FileOutputStream out = new FileOutputStream(dst.toString());

    // Transfer bytes from in to out
    byte[] buf = new byte[1024];
    int len;
    while ((len = in.read(buf)) > 0)
    {
      out.write(buf, 0, len);
    }
    in.close();
    out.close();
  }

}
