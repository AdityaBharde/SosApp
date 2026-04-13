package com.example.sosapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QuickActionsActivity extends AppCompatActivity {
    private TextView tvWelcome, tvLogCount;
    private EditText etNoteContent;
    private static final String FILE_NAME = "emergency_logs.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_actions);

        tvWelcome = findViewById(R.id.tvWelcomeUser);
        tvLogCount = findViewById(R.id.tvLogCount);
        etNoteContent = findViewById(R.id.etNoteContent);

        String userName = getIntent().getStringExtra("USER_NAME");
        tvWelcome.setText("User: " + (userName != null ? userName : "User"));

        findViewById(R.id.btnCall).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:112"));
            startActivity(intent);
        });

        findViewById(R.id.btnEmail).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "SOS Alert from " + userName);
            intent.putExtra(Intent.EXTRA_TEXT, "I need assistance. Please contact me immediately.");
            startActivity(intent);
        });

        findViewById(R.id.btnShare).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Emergency! " + userName + " needs help. Shared via SosApp.");
            startActivity(Intent.createChooser(intent, "Share via"));
        });

        findViewById(R.id.btnSaveLog).setOnClickListener(v -> saveLog());
        findViewById(R.id.btnViewLogs).setOnClickListener(v -> viewLogs());

        updateLogCounts();
    }

    private void saveLog() {
        String note = etNoteContent.getText().toString().trim();
        if (note.isEmpty()) {
            Toast.makeText(this, "Please enter a note", Toast.LENGTH_SHORT).show();
            return;
        }

        String time = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(new Date());
        String logEntry = "[" + time + "] " + note + "\n--------------------\n";

        try (FileOutputStream fos = openFileOutput(FILE_NAME, MODE_APPEND)) {
            fos.write(logEntry.getBytes());
            etNoteContent.setText("");
            Toast.makeText(this, "Note Saved to Internal Storage", Toast.LENGTH_SHORT).show();
            updateLogCounts();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving note", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewLogs() {
        StringBuilder content = new StringBuilder();
        try (FileInputStream fis = openFileInput(FILE_NAME);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (Exception e) {
            content.append("No notes found in internal storage.");
        }

        new AlertDialog.Builder(this)
            .setTitle("Saved Internal Notes")
            .setMessage(content.length() > 0 ? content.toString() : "No notes saved yet.")
            .setPositiveButton("OK", null)
            .setNeutralButton("Clear All", (dialog, which) -> clearLogs())
            .show();
    }

    private void clearLogs() {
        if (deleteFile(FILE_NAME)) {
            Toast.makeText(this, "All notes deleted", Toast.LENGTH_SHORT).show();
            updateLogCounts();
        }
    }

    private void updateLogCounts() {
        int count = 0;
        try (FileInputStream fis = openFileInput(FILE_NAME);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
            while (reader.readLine() != null) {
                count++;
            }
        } catch (Exception e) { /* File doesn't exist */ }
        
        // Approx count based on lines (divider + note)
        tvLogCount.setText("Total Entries: " + (count / 2));
    }
}