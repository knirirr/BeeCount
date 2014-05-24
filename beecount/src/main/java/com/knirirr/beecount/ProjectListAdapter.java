package com.knirirr.beecount;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.knirirr.beecount.database.Project;

public class ProjectListAdapter extends ArrayAdapter<Project>
{
  private static final String TAG = "BeeCountProjectListAdapater";
  Context context;
  int layoutResourceId;
  List<Project> projects = null;

  // Constructor
  public ProjectListAdapter(Context context, int layoutResourceId, List<Project> projects)
  {
    super(context, layoutResourceId, projects);
    this.layoutResourceId = layoutResourceId;
    this.context = context;
    this.projects = projects;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {
    View row = convertView;
    ProjectHolder holder = null;

    if(row == null)
    {
      LayoutInflater inflater = ((Activity)context).getLayoutInflater();
      row = inflater.inflate(layoutResourceId, parent, false);

      holder = new ProjectHolder();
      holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);
      holder.txtDate  = (TextView) row.findViewById(R.id.txtDate);

      row.setTag(holder);
    }
    else
    {
      holder = (ProjectHolder)row.getTag();
    }

    Project project = projects.get(position);
    holder.txtTitle.setText(project.name);
    holder.txtDate.setText(project.getDate());

    return row;
  }

  static class ProjectHolder
  {
    TextView txtTitle;
    TextView txtDate;
  }

}
