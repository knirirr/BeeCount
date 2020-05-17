package com.knirirr.beecount;

import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

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
