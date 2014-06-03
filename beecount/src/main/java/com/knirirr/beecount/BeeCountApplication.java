package com.knirirr.beecount;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by milo on 14/05/2014.
 */
public class BeeCountApplication extends Application
{
  private static String TAG = "BeeCount";
  public BitmapDrawable ob;
  private Bitmap bMap;
  public Long project_id;
  private static SharedPreferences prefs;


  @Override
  public void onCreate()
  {
    super.onCreate();
    ob = null;
    project_id = null;
    bMap = null;
    try
    {
      prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }
    catch (Exception e)
    {
      Log.e(TAG, e.toString());
    }
  }

  /*
   * The idea here is to keep ob around as a pre-prepared bitmap, only setting it up
   * when the user's settings change or when the application starts up.
   */

  public BitmapDrawable getBackground()
  {
    if (ob == null)
    {
      return setBackground();
    }
    else
    {
      return ob;
    }
  }

  public BitmapDrawable setBackground()
  {
    String backgroundPref = prefs.getString("pref_back", "default");
    String pictPref = prefs.getString("imagePath", "");

    WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    int width = size.x;
    int height = size.y;


    if (backgroundPref.equals("none"))
    {
      // boring black screen
      bMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
      bMap.eraseColor(Color.BLACK);
    }
    else if (backgroundPref.equals("custom"))
    {
      if (!(pictPref.equals("")))
      {
        if (new File(pictPref).isFile())
        {
          bMap = BitmapFactory.decodeFile(pictPref);
        }
        else
        {
          Toast.makeText(this, getString(R.string.customMissing), Toast.LENGTH_SHORT).show();
          bMap = decodeBitmap(R.drawable.beecount_knitting, width, height);
        }
      }
      else
      {
        Toast.makeText(this,getString(R.string.customNotDefined),Toast.LENGTH_SHORT).show();
        bMap = decodeBitmap(R.drawable.beecount_knitting, width, height);
      }
    }
    else if (backgroundPref.equals("default"))
    {
      //bMap = BitmapFactory.decodeResource(getResources(), R.drawable.beecount_knitting);
      bMap = decodeBitmap(R.drawable.beecount_knitting, width, height);
    }

    ob = new BitmapDrawable(bMap);
    bMap = null;
    return ob;
  }

  public static SharedPreferences getPrefs()
  {
    return prefs;
  }

  public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
  {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

      final int halfHeight = height / 2;
      final int halfWidth = width / 2;

      // Calculate the largest inSampleSize value that is a power of 2 and keeps both
      // height and width larger than the requested height and width.
      while ((halfHeight / inSampleSize) > reqHeight
          && (halfWidth / inSampleSize) > reqWidth) {
        inSampleSize *= 2;
      }
    }

    return inSampleSize;
  }

  public Bitmap decodeBitmap(int resId, int reqWidth, int reqHeight)
  {
    // First decode with inJustDecodeBounds=true to check dimensions
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeResource(getResources(), resId, options);

    // Calculate inSampleSize
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeResource(getResources(), resId, options);
  }

}
