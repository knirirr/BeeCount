package com.knirirr.beecount.database;

/**
 * Created by milo on 05/05/2014.
 */
public class Count
{
  public long id;
  public long project_id;
  public int count;
  public String name;
  public int auto_reset;
  public int reset_level;

  public int increase()
  {
    count = count + 1;
    return count;
  }

  public int decrease()
  {
    if (count > 0)
    {
      count = count - 1;
    }
    return count;
  }

}