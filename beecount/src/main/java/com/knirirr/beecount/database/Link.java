package com.knirirr.beecount.database;

/**
 * Created by milo on 05/05/2014.
 */
public class Link
{
  private long id;
  private long project_id;
  private long master_id;
  private long slave_id;
  private int increment;
  private int type;

  /*
    Getters
   */
  public long getId()
  {
    return id;
  }

  public long getProjectId()
  {
    return project_id;
  }

  public long getMasterId()
  {
    return master_id;
  }

  public long getSlaveId()
  {
    return slave_id;
  }

  public int getIncrement()
  {
    return increment;
  }

  public int getType()
  {
    return type;
  }

  /*
    Setters
   */
  public void setId(long id)
  {
    this.id = id;
  }

  public void setProjectId(long project_id)
  {
    this.project_id = project_id;
  }

  public void setMasterId(long master_id)
  {
    this.master_id = master_id;
  }

  public void setSlaveId(long slave_id)
  {
    this.slave_id = slave_id;
  }

  public void setIncrement(int increment)
  {
    this.increment = increment;
  }

  public void setType(int type)
  {
    this.type = type;
  }
}
