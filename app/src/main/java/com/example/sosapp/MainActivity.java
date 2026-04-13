package com.example.sosapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivityLifecycle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate called");

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String name = prefs.getString("userName", "User");
        Toast.makeText(this, "Welcome, " + name, Toast.LENGTH_SHORT).show();

        findViewById(R.id.btnIncidentReport).setOnClickListener(v ->
                startActivity(new Intent(this, IncidentReportActivity.class)));

        findViewById(R.id.btnTrustedContacts).setOnClickListener(v ->
                startActivity(new Intent(this, TrustedContactsActivity.class)));

        findViewById(R.id.btnQuickActions).setOnClickListener(v -> {
            Intent intent = new Intent(this, QuickActionsActivity.class);
            intent.putExtra("USER_NAME", name);
            startActivity(intent);
        });

        findViewById(R.id.btnLiveLocation).setOnClickListener(v -> sendLiveLocation());
    }

    private void sendLiveLocation() {
        if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            androidx.core.app.ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.SEND_SMS}, 1);
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        android.database.Cursor cursor = dbHelper.getAllData();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No trusted contacts found to send location!", Toast.LENGTH_LONG).show();
            return;
        }

        String mockLocation = "Emergency! I need help. My current location is: https://www.google.com/maps?q=28.6139,77.2090";
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        int sentCount = 0;

        while (cursor.moveToNext()) {
            String phone = cursor.getString(2);
            try {
                smsManager.sendTextMessage(phone, null, mockLocation, null, null);
                sentCount++;
            } catch (Exception e) {
                Log.e(TAG, "Failed to send SMS to " + phone, e);
            }
        }

        if (sentCount > 0) {
            Toast.makeText(this, "Emergency Alert & Live Location sent to " + sentCount + " contacts!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Failed to send SMS alerts. Check your balance/network.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                sendLiveLocation();
            } else {
                Toast.makeText(this, "SMS Permission is required to alert contacts!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart called");
    }
}