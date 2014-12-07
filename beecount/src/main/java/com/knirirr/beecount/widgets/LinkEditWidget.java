package com.knirirr.beecount.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.knirirr.beecount.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by milo on 04/06/2014.
 */
public class LinkEditWidget extends LinearLayout implements Serializable
{
  Spinner masterSpinner;
  Spinner slaveSpinner;
  Spinner choiceSpinner;
  EditText linkIncrement;
  ImageButton deleteLink;

  public long linkId;
  public ArrayList<String> countNames;
  public ArrayList<Long> countIds;


  public LinkEditWidget(Context context, AttributeSet attrs)
  {
    super(context, attrs);

    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.widget_link_edit, this, true);
    masterSpinner = (Spinner) findViewById(R.id.masterSpinner);
    slaveSpinner = (Spinner) findViewById(R.id.slaveSpinner);
    choiceSpinner = (Spinner) findViewById(R.id.choiceSpinner);
    linkIncrement = (EditText) findViewById(R.id.linkIncrement);
    deleteLink = (ImageButton) findViewById(R.id.deleteLink);
    deleteLink.setTag(Long.valueOf(0));

    /*
     * Some sort of static array would be better here, and should be added when a cunning idea for how
     * to do it springs to mind.
     */
    ArrayList<String> choices = new ArrayList<String>();
    choices.add(getResources().getString(R.string.incr_reset));
    choices.add(getResources().getString(R.string.incr_up));
    choices.add(getResources().getString(R.string.incr_down));

    setSpinnerAdapter("choice", choices);

    ArrayList<String> countNames = new ArrayList<String>();
    ArrayList<Long> countIds = new ArrayList<Long>();

  }

  public void setLinkId(long id)
  {
    linkId = id;
    deleteLink.setTag(id);
  }

  public void setCountNames(ArrayList<String> names)
  {
    countNames = names;
    setSpinnerAdapter("master", names);
    setSpinnerAdapter("slave", names);
  }

  public void setCountIds(ArrayList<Long> ids)
  {
    countIds = ids;
  }

  private void setSpinnerAdapter(String spinner, ArrayList<String> array)
  {
    int master_position = masterSpinner.getSelectedItemPosition();
    int slave_position = slaveSpinner.getSelectedItemPosition();
    int choice_position = choiceSpinner.getSelectedItemPosition();
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.custom_spinner, array)
    {
      public View getView(int position, View convertView, ViewGroup parent)
      {
        View v = super.getView(position, convertView, parent);
        ((TextView) v).setTextColor(Color.WHITE);
        return v;
      }

      public View getDropDownView(int position, View convertView, ViewGroup parent)
      {
        View v = super.getDropDownView(position, convertView, parent);
        ((TextView) v).setTextColor(Color.WHITE);
        return v;
      }
    };
    int maxlen = adapter.getCount();
    if (spinner.equals("master"))
    {
      masterSpinner.setAdapter(adapter);
      if (master_position < maxlen)
      {
        masterSpinner.setSelection(master_position);
      }
    } else if (spinner.equals("slave"))
    {
      slaveSpinner.setAdapter(adapter);
      if (slave_position < maxlen)
      {
        slaveSpinner.setSelection(slave_position);
      }
    } else if (spinner.equals("choice"))
    {
      choiceSpinner.setAdapter(adapter);
      if (choice_position < maxlen)
      {
        choiceSpinner.setSelection(choice_position);
      }
    }

  }

  public long getMasterId()
  {
    return countIds.get(masterSpinner.getSelectedItemPosition());
  }

  public long getSlaveId()
  {
    return countIds.get(slaveSpinner.getSelectedItemPosition());
  }

  public int getChoice()
  {
    return choiceSpinner.getSelectedItemPosition();
  }

  public int getLinkIncrement()
  {
    try
    {
      return Integer.valueOf(linkIncrement.getText().toString());
    }
    catch (NumberFormatException e)
    {
      return 0;
    }
  }

  public String getMasterName()
  {
    return masterSpinner.getSelectedItem().toString();
  }

  public String getSlaveName()
  {
    return slaveSpinner.getSelectedItem().toString();
  }

}
