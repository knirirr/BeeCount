package com.knirirr.beecount.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.knirirr.beecount.R;
import com.knirirr.beecount.database.Alert;
import com.knirirr.beecount.database.Count;
import com.knirirr.beecount.database.Link;

import java.util.List;

/**
 * Created by milo on 25/05/2014.
 */
public class CountingWidget extends RelativeLayout
{
  public static String TAG = "BeeCountCountingWidget";

  private TextView countName;
  private TextView countCount;

  public Count count;
  // are these needed?
  /*
  public List<Alert> alerts;
  public List<Link> links;
  */

  public CountingWidget(Context context, AttributeSet attrs)
  {
    super(context, attrs);

    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.counting_widget, this, true);
    countCount = (TextView) findViewById(R.id.countCount);
    countName = (TextView) findViewById(R.id.countName);

  }

  public void setCount(Count newcount)
  {
    count = newcount;
    countCount.setText(String.valueOf(count.count));
    countName.setText(count.name);
    Button countUpButton = (Button) findViewById(R.id.buttonUp);
    countUpButton.setTag(count.id);
    Button countDownButton = (Button) findViewById(R.id.buttonDown);
    countDownButton.setTag(count.id);
    Button editButton = (Button) findViewById(R.id.buttonEdit);
    editButton.setTag(count.id);
  }

  public void countUp()
  {
    count.increase();
    countCount.setText(String.valueOf(count.count));
  }

  public void countDown()
  {
    count.decrease();
    countCount.setText(String.valueOf(count.count));
  }

  public void resetZero()
  {
    count.count = 0;
    countCount.setText(String.valueOf(count.count));
  }

  /*
   * Saving the counts should perhaps go in this widget?
   */




}
