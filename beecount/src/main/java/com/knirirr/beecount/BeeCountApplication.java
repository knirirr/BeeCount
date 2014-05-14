package com.knirirr.beecount;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;

/**
 * Created by milo on 14/05/2014.
 */
public class BeeCountApplication extends Application
{
  private static String TAG = "BeeCount";

  private static SharedPreferences prefs;

  @Override
  public void onCreate()
  {
    super.onCreate();

    try
    {
      prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }
    catch (Exception e)
    {
      Log.e(TAG, e.toString());
    }
  }

  public BitmapDrawable setBackground()
  {
    // set background

    String backgroundPref = prefs.getString("pref_back", "default");
    String pictPref = prefs.getString("imagePath", "");
    Bitmap bMap = null;

    if (backgroundPref.equals("none"))
    {
      // boring black screen
      WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
      Display display = wm.getDefaultDisplay();
      Point size = new Point();
      display.getSize(size);
      int width = size.x;
      int height = size.y;
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
          bMap = BitmapFactory.decodeResource(getResources(), R.drawable.beecount_knitting);
        }
      }
      else
      {
        Toast.makeText(this,getString(R.string.customNotDefined),Toast.LENGTH_SHORT).show();
        bMap = BitmapFactory.decodeResource(getResources(), R.drawable.beecount_knitting);
      }
    }
    else if (backgroundPref.equals("default"))
    {
      bMap = BitmapFactory.decodeResource(getResources(), R.drawable.beecount_knitting);
    }

    BitmapDrawable ob = new BitmapDrawable(bMap);
    return ob;
  }

  public static SharedPreferences getPrefs()
  {
    return prefs;
  }

}
