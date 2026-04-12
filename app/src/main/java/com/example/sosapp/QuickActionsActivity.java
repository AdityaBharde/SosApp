package com.example.sosapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class QuickActionsActivity extends AppCompatActivity {
    private TextView tvWelcome, tvLogCount;
    private EditText etNoteContent;
    private RadioButton rbMedical;
    private static final String FILE_NAME = "emergency_logs.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_actions);

        tvWelcome = findViewById(R.id.tvWelcomeUser);
        tvLogCount = findViewById(R.id.tvLogCount);
        etNoteContent = findViewById(R.id.etNoteContent);
        rbMedical = findViewById(R.id.rbMedical);

        String userName = getIntent().getStringExtra("USER_NAME");
        tvWelcome.setText("User: " + (userName != null ? userName : "Unknown"));

        findViewById(R.id.btnCall).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:112"));
            startActivity(intent);
        });

        findViewById(R.id.btnEmail).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Emergency Alert from " + userName);
            intent.putExtra(Intent.EXTRA_TEXT, "I am in an emergency situation. Please help.");
            startActivity(intent);
        });

        findViewById(R.id.btnShare).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Emergency Alert! User: " + userName + " needs assistance.");
            startActivity(Intent.createChooser(intent, "Share via"));
        });

        findViewById(R.id.btnSaveLog).setOnClickListener(v -> saveLog());

        updateLogCounts();
    }

    private void saveLog() {
        String note = etNoteContent.getText().toString();
        if (note.isEmpty()) {
            Toast.makeText(this, "Please enter a note", Toast.LENGTH_SHORT).show();
            return;
        }

        String type = rbMedical.isChecked() ? "Medical" : "SafeZone";
        String logEntry = type + ":" + note + "\n";

        try (FileOutputStream fos = openFileOutput(FILE_NAME, MODE_APPEND)) {
            fos.write(logEntry.getBytes());
            etNoteContent.setText("");
            Toast.makeText(this, "Log Saved", Toast.LENGTH_SHORT).show();
            updateLogCounts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLogCounts() {
        int medicalCount = 0;
        int safeZoneCount = 0;

        try (FileInputStream fis = openFileInput(FILE_NAME);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Medical:")) medicalCount++;
                else if (line.startsWith("SafeZone:")) safeZoneCount++;
            }
        } catch (Exception e) {
            // File might not exist yet
        }

        tvLogCount.setText("Counts: Medical (" + medicalCount + "), Safe Zone (" + safeZoneCount + ")");
    }
}