package com.knirirr.beecount.widgets;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knirirr.beecount.CountingActivity;
import com.knirirr.beecount.R;
import com.knirirr.beecount.database.Count;

/**
 * Created by milo on 25/05/2014.
 */
public class CountingWidget extends RelativeLayout
{
  public static String TAG = "BeeCountCountingWidget";

  private TextView countName;
  private TextView countCount;

  public Count count;

  public CountingWidget(Context context, AttributeSet attrs)
  {
    super(context, attrs);

    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.widget_counting, this, true);
    countCount = (TextView) findViewById(R.id.countCount);
    countName = (TextView) findViewById(R.id.countName);

  }

  public void setCount(Count newcount)
  {
    count = newcount;
    countCount.setText(String.valueOf(count.count));
    setFont();
    countName.setText(count.name);
    ImageButton countUpButton = (ImageButton) findViewById(R.id.buttonUp);
    countUpButton.setTag(count.id);
    ImageButton countDownButton = (ImageButton) findViewById(R.id.buttonDown);
    countDownButton.setTag(count.id);
    ImageButton editButton = (ImageButton) findViewById(R.id.buttonEdit);
    editButton.setTag(count.id);
  }

  public void countUp()
  {
    count.increase();
    countCount.setText(String.valueOf(count.count));
    setFont();
  }

  public void countDown()
  {
    if (((CountingActivity) getContext()).getNegPref())
    {
      count.decrease();
    }
    else
    {
      count.safe_decrease();
    }
    countCount.setText(String.valueOf(count.count));
    setFont();
  }

  // sets to the reset level
  public void resetZero()
  {
    count.count = count.reset_level;
    countCount.setText(String.valueOf(count.count));
    setFont();
  }

  /*
   * The purpose of this function is to flip out and change the font size for the count box depending on how many
   * digits are in it, in order that the count doesn't wrap when going over 100.
   */
  public void setFont()
  {
    String currCount = countCount.getText().toString();
    if (currCount.length() <= 2)
    {
      countCount.setTextSize(38);
    }
    else if (currCount.length() == 3)
    {
      countCount.setTextSize(30);
    }
    else if (currCount.length() >= 4)
    {
      countCount.setTextSize(22);
    }
  }

  /*
   * Saving the counts should perhaps go in this widget?
   */




}
