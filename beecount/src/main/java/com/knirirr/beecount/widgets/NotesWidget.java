package com.knirirr.beecount.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.knirirr.beecount.R;

/**
 * Created by milo on 26/05/2014.
 */
public class NotesWidget extends LinearLayout
{
  public static String TAG = "Beecount NotesWidget";
  public String project_notes;
  private TextView textView;

  public NotesWidget(Context context, AttributeSet attrs)
  {
    super(context, attrs);

    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.widget_notes, this, true);
    textView = (TextView) findViewById(R.id.notes_text);
  }

  public void setNotes(String notes)
  {
    project_notes = notes;
    textView.setText(project_notes);
  }

  public void setFont(Boolean large)
  {
    if (large)
    {
      Log.i(TAG, "Setting LARGE text size.");
      textView.setTextSize(22);
    }
    else
    {
      Log.i(TAG, "Setting small text size.");
      textView.setTextSize(14);
    }
  }

}

