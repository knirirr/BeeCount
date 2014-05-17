package com.knirirr.beecount.database;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by milo on 05/05/2014.
 */
public class Project
{
  private long id;
  private long created_at;
  private String name;
  private String notes;

  /*
   * getters
   */
  public long getId()
  {
    return id;
  }

  public long getCreatedAt()
  {
    return created_at;
  }

  public String getName()
  {
    return name;
  }

  public String getNotes()
  {
    return notes;
  }

  public String getDate()
  {
    Date date = new Date(created_at);
    DateFormat df = SimpleDateFormat.getDateInstance();
    return df.format(date);
  }

  /*
  * setters
  */
  public void setId(long id)
  {
    this.id = id;
  }

  public void setCreatedAt(long created_at)
  {
    this.created_at = created_at;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void setNotes(String notes)
  {
    this.notes = notes;
  }
}
