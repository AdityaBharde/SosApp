package com.example.sosapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class DispatchResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_result);

        TextView tvServices = findViewById(R.id.tvSelectedServices);
        TextView tvPriority = findViewById(R.id.tvPriority);

        ArrayList<String> services = getIntent().getStringArrayListExtra("SERVICES");
        int priority = getIntent().getIntExtra("PRIORITY", 0);

        if (services != null && !services.isEmpty()) {
            StringBuilder sb = new StringBuilder("Selected Services:\n");
            for (String s : services) {
                sb.append("- ").append(s).append("\n");
            }
            tvServices.setText(sb.toString());
        } else {
            tvServices.setText("No services selected.");
        }

        tvPriority.setText("Dispatch Priority Score: " + priority);
    }
}