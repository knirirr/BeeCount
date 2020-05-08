package com.knirirr.beecount;

import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class CountLog extends AppCompatActivity {

  long project_id;
  BeeCountApplication beeCount;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_count_log);

    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      project_id = extras.getLong("project_id");
    }
    beeCount = (BeeCountApplication) getApplication();
    LinearLayout layout=(LinearLayout)findViewById(R.id.countLogLayout);
    layout.setBackground(beeCount.getBackground());
  }
}
