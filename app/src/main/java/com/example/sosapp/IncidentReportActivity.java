package com.example.sosapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class IncidentReportActivity extends AppCompatActivity {
    private EditText etName, etAge, etMobile, etDesc, etLocation;
    private Button btnSendAlert, btnViewPast;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_report);

        dbHelper = new DatabaseHelper(this);
        etName = findViewById(R.id.etReportName);
        etAge = findViewById(R.id.etReportAge);
        etMobile = findViewById(R.id.etReportMobile);
        etLocation = findViewById(R.id.etIncidentLocation);
        etDesc = findViewById(R.id.etIncidentDesc);
        btnSendAlert = findViewById(R.id.btnSendAlert);
        btnViewPast = findViewById(R.id.btnViewPastReports);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateForm();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        etName.addTextChangedListener(watcher);
        etAge.addTextChangedListener(watcher);
        etMobile.addTextChangedListener(watcher);
        etLocation.addTextChangedListener(watcher);
        etDesc.addTextChangedListener(watcher);

        btnSendAlert.setOnClickListener(v -> submitReport());
        btnViewPast.setOnClickListener(v -> viewPastReports());

        validateForm();
    }

    private void submitReport() {
        String name = etName.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String time = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date());

        boolean isInserted = dbHelper.insertIncident(name, mobile, desc, location, time);

        if (isInserted) {
            Toast.makeText(this, "Incident Reported & Saved Successfully!", Toast.LENGTH_LONG).show();
            shareIncident(name, desc, location, time);
            finish();
        } else {
            Toast.makeText(this, "Failed to save report. Try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareIncident(String name, String desc, String location, String time) {
        String shareBody = "Emergency Incident Report\n\n" + "Reported by: " + name + "\n" + "Time: " + time + "\n" + "Location: " + location + "\n" + "Description: " + desc + "\n\n" + "Sent via SosApp";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Incident Report - " + name);
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(intent, "Share Incident Report via"));
    }

    private void viewPastReports() {
        Cursor res = dbHelper.getAllIncidents();
        if (res.getCount() == 0) {
            new AlertDialog.Builder(this).setTitle("No Records").setMessage("You haven't submitted any incident reports yet.").setPositiveButton("OK", null).show();
            return;
        }

        StringBuilder buffer = new StringBuilder();
        while (res.moveToNext()) {
            buffer.append("ID: ").append(res.getString(0)).append("\n");
            buffer.append("Time: ").append(res.getString(5)).append("\n");
            buffer.append("Location: ").append(res.getString(4)).append("\n");
            buffer.append("Description: ").append(res.getString(3)).append("\n");
            buffer.append("----------------------------\n\n");
        }

        new AlertDialog.Builder(this).setTitle("Past Incident Reports").setMessage(buffer.toString()).setPositiveButton("Close", null).setNeutralButton("Share Latest", (dialog, which) -> {
            if (res.moveToFirst()) {
                shareIncident(res.getString(1), res.getString(3), res.getString(4), res.getString(5));
            }
        }).show();
    }

    private void validateForm() {
        boolean isValid = !etName.getText().toString().trim().isEmpty() && !etAge.getText().toString().trim().isEmpty() && !etMobile.getText().toString().trim().isEmpty() && !etLocation.getText().toString().trim().isEmpty() && !etDesc.getText().toString().trim().isEmpty();
        btnSendAlert.setEnabled(isValid);
    }
}