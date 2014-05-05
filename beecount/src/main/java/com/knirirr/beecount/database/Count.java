package com.knirirr.beecount.database;

/**
 * Created by milo on 05/05/2014.
 */
public class Count
{
  private long id;
  private long project_id;
  private int count;
  private String name;
  private int auto_reset;
  private int reset_level;

  /*
   * getters
   */
  public long getId()
  {
    return id;
  }

  public long getProjectId()
  {
    return project_id;
  }

  public int getCount()
  {
    return count;
  }

  public String getName()
  {
    return name;
  }

  public int getAutoReset()
  {
    return auto_reset;
  }

  public int getResetLevel()
  {
    return reset_level;
  }

  /*
   * setters
   */
  public void setId(long id)
  {
    this.id = id;
  }

  public void setProjectId(long project_id)
  {
    this.project_id = project_id;
  }

  public void setCount(int count)
  {
    this.count = count;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void setAutoReset(int auto_reset)
  {
    this.auto_reset = auto_reset;
  }

  public void setResetLevel(int reset_level)
  {
    this.reset_level = reset_level;
  }
}
