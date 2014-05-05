package com.knirirr.beecount.database;

/**
 * Created by milo on 05/05/2014.
 */
public class Project
{
  private long id;
  private int created_at;
  private String name;
  private String notes;

  /*
   * getters
   */
  public long getId()
  {
    return id;
  }

  public int getCreatedAt()
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

  /*
  * setters
  */
  public void setId(long id)
  {
    this.id = id;
  }

  public void setCreatedAt(int created_at)
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
