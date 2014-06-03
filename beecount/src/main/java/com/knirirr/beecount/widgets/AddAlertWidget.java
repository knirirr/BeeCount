package com.knirirr.beecount.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.knirirr.beecount.R;

/**
 * Created by milo on 01/06/2014.
 */
public class AddAlertWidget extends LinearLayout
{
  private TextView textView;

  public AddAlertWidget(Context context, AttributeSet attrs)
  {
    super(context, attrs);

    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.widget_add_alert, this, true);
    textView = (TextView) findViewById(R.id.add_alert_text);
  }

}
