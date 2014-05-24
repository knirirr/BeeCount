package com.knirirr.beecount.database;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by milo on 05/05/2014.
 */
public class Project
{
  public long id;
  public long created_at;
  public String name;
  public String notes;

  public String getDate()
  {
    Date date = new Date(created_at);
    DateFormat df = SimpleDateFormat.getDateInstance();
    return df.format(date);
  }

}
