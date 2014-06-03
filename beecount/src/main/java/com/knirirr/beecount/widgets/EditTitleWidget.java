package com.knirirr.beecount.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.knirirr.beecount.R;

/**
 * Created by milo on 03/06/2014.
 */
public class EditTitleWidget extends LinearLayout
{
  TextView widget_title;
  EditText project_name;

  public EditTitleWidget(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.widget_edit_title, this, true);
    widget_title = (TextView) findViewById(R.id.widgetTitle);
    project_name = (EditText) findViewById(R.id.projectName);
  }

  public void setProjectName(String name)
  {
    project_name.setText(name);
  }

  public String getProjectName()
  {
    return project_name.getText().toString();
  }

  public void setWidgetTitle(String title)
  {
    widget_title.setText(title);
  }

}
