package com.knirirr.beecount;

import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This activity serves to show the actions performed while counting. Mostly, this was to mitigate possible erroneous
 * double-clicks and check the last counts done.
 */
public class CountLogActivity extends AppCompatActivity {

  String projectLogs;
  BeeCountApplication beeCount;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_count_log);

    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      projectLogs = extras.getString("project_logs");
    }
    beeCount = (BeeCountApplication) getApplication();
    ScrollView layout=(ScrollView) findViewById(R.id.countLogLayout);
    layout.setBackground(beeCount.getBackground());

    TextView countLogTextView = (TextView)findViewById(R.id.countLogTextView);
    countLogTextView.setText(projectLogs);
  }
}
