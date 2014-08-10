package com.knirirr.beecount.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.knirirr.beecount.R;

/**
 * Created by milo on 26/05/2014.
 */
public class NotesWidget extends LinearLayout
{
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
      textView.setTextSize(20);
    }
    else
    {
      textView.setTextSize(12);
    }
  }

}
