package com.knirirr.beecount.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.knirirr.beecount.R;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by milo on 27/05/2014.
 */
public class OptionsWidget extends LinearLayout
{
  private TextView instructions;
  private TextView number;

  public OptionsWidget(Context context, AttributeSet attrs)
  {
    super(context, attrs);

    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.widget_options, this, true);
    instructions = (TextView) findViewById(R.id.help_text);
    number = (EditText) findViewById(R.id.count_parameter_edit);
  }

  public void setInstructions(String i)
  {
    instructions.setText(i);
  }

  public void setParameterValue(int i)
  {
    number.setText(String.valueOf(i));
  }

  // this is set to return 0 if it can't parse a value from the box in order
  // that BeeCount doesn't crash
  public int getParameterValue()
  {
    String text = number.getText().toString();
    if (StringUtils.isEmpty(text))
    {
      return Integer.valueOf(0);
    }
    else
    {
      return Integer.parseInt(text);
    }
  }

}
