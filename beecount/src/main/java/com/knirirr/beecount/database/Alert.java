package com.knirirr.beecount.database;



/**
 * Created by milo on 05/05/2014.
 */
public class Alert
{
  private long id;
  private long count_id;
  private int alert;
  private String alert_text;

  /*
 * getters
 */
  public long getId()
  {
    return id;
  }

  public long getCountId()
  {
    return count_id;
  }

  public int getAlert()
  {
    return alert;
  }

  public String getAlertText()
  {
    return alert_text;
  }

  /*
   * setters
   */
  public void setId(long id)
  {
    this.id = id;
  }

  public void setCountId(long count_id)
  {
    this.count_id = count_id;
  }

  public void setAlert(int alert)
  {
    this.alert = alert;
  }

  public void setAlertText(String alert_text)
  {
    this.alert_text = alert_text;
  }

}
