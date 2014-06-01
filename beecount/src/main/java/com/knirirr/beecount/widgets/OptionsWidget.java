package com.knirirr.beecount.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.knirirr.beecount.R;

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
    inflater.inflate(R.layout.options_widget, this, true);
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

  public int getParameterValue()
  {
    return Integer.parseInt(number.getText().toString());
  }

}
