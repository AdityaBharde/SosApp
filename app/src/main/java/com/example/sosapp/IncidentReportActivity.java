package com.example.sosapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class IncidentReportActivity extends AppCompatActivity {
    private EditText etName, etAge, etMobile, etDesc;
    private Button btnSendAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_report);

        etName = findViewById(R.id.etReportName);
        etAge = findViewById(R.id.etReportAge);
        etMobile = findViewById(R.id.etReportMobile);
        etDesc = findViewById(R.id.etIncidentDesc);
        btnSendAlert = findViewById(R.id.btnSendAlert);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateForm();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        etName.addTextChangedListener(watcher);
        etAge.addTextChangedListener(watcher);
        etMobile.addTextChangedListener(watcher);
        etDesc.addTextChangedListener(watcher);

        btnSendAlert.setOnClickListener(v -> {
            String summary = "Alert Sent!\nName: " + etName.getText().toString() + 
                             "\nMobile: " + etMobile.getText().toString();
            Toast.makeText(this, summary, Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private void validateForm() {
        boolean isValid = !etName.getText().toString().trim().isEmpty() &&
                         !etAge.getText().toString().trim().isEmpty() &&
                         !etMobile.getText().toString().trim().isEmpty() &&
                         !etDesc.getText().toString().trim().isEmpty();
        btnSendAlert.setEnabled(isValid);
    }
}