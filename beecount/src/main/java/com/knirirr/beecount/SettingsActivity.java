package com.knirirr.beecount;


import android.annotation.SuppressLint;
import android.util.Log;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.content.Intent;
import android.net.Uri;
import android.database.Cursor;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.widget.Toast;


public class SettingsActivity extends PreferenceActivity 
{
  private static String TAG = "BeeCountPreferenceActivity";
  private static final int SELECT_PICTURE = 1;
  String imageFilePath;
  SharedPreferences prefs;
  SharedPreferences.Editor editor;

  @Override
  @SuppressLint("CommitPrefEdits")
  @SuppressWarnings("deprecation")
  public void onCreate(Bundle savedInstanceState) 
  {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);

    Preference button = (Preference) findPreference("button");
    button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() 
    {
      @Override
      public boolean onPreferenceClick(Preference arg0) 
      { 
        getImage();
        return true;
      }
    });
    prefs = PreferenceManager.getDefaultSharedPreferences(this);
    editor = prefs.edit(); // will be committed on pause
  }

  @Override
  protected void onPause()
  {
    super.onPause();
    editor.commit();
  }

  public void getImage()
  {
    Intent pickIntent = new Intent();
    pickIntent.setType("image/*");
    pickIntent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(pickIntent, SELECT_PICTURE);
  }

  @Override
  @SuppressLint("CommitPrefEdits")
  protected void onActivityResult(int requestCode, int resultCode, Intent data) 
  {
    if(requestCode == SELECT_PICTURE && data != null && data.getData() != null)
    {
      Uri _uri = Uri.parse(data.getDataString());

      if (_uri != null) 
      {
        //User did pick an image.
        /*
         * The try is here because this action fails if the user uses a file manager; the gallery
         * seems to work nicely, though.
         */
        Cursor cursor = getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
        try
        {
          cursor.moveToFirst(); // blows up here if file manager used
        }
        catch (Exception e)
        {
          Log.e(TAG, "Failed to select image: " + e.toString());
          Toast.makeText(this, getString(R.string.image_error), Toast.LENGTH_SHORT).show();
          return;
        }

        //Link to the image
        imageFilePath = cursor.getString(0);
        cursor.close();

        // save the image path
        editor.putString("imagePath", imageFilePath);
        //editor.commit();
        try
        {
          Log.i(TAG, "IMAGE (in Settings): " + imageFilePath);
        }
        catch(Exception e)
        {
          Log.e(TAG, "Failed to upload image: " + e.toString());
          Toast.makeText(this, getString(R.string.image_error), Toast.LENGTH_SHORT).show();
        }
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
      case android.R.id.home:
        startActivity(new Intent(this, WelcomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        break;
      default:
        return super.onOptionsItemSelected(item);
    }
    return true;
  }
}
