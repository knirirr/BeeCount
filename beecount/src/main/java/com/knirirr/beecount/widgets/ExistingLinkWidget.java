package com.knirirr.beecount.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.knirirr.beecount.R;

/**
 * Created by milo on 06/06/2014.
 */
public class ExistingLinkWidget extends LinearLayout
{
  public long masterId;
  public long slaveId;
  public long linkId;

  TextView linkDescription;
  ImageButton deleteLinkButton;

  public ExistingLinkWidget(Context context, AttributeSet attrs)
  {
    super(context, attrs);
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.widget_existing_link, this, true);
    linkDescription = (TextView) findViewById(R.id.linkDescription);
    deleteLinkButton = (ImageButton) findViewById(R.id.deleteLinkButton);
    deleteLinkButton.setTag(Long.valueOf(0));
  }

  public void setLinkId(long id)
  {
    linkId = id;
    deleteLinkButton.setTag(id);
  }

  public void setInfo(String master, String slave, int type, int increment)
  {
    String typestring = "";
    if (type == 0)
    {
      typestring = getContext().getString(R.string.incr_reset);
    }
    else if (type == 1)
    {
      typestring = getContext().getString(R.string.incr_up);
    }
    else if (type == 2)
    {
      typestring = getContext().getString(R.string.incr_down);
    }
    linkDescription.setText(String.format(getContext().getString(R.string.xWillYZ),master,typestring,slave,increment));
  }

}
