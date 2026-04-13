package com.example.sosapp;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import java.util.ArrayList;

public class DispatchCenterActivity extends AppCompatActivity {
    private CheckBox cbPolice, cbAmbulance, cbFire;
    private Button btnSubmit;
    private static final String CHANNEL_ID = "emergency_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_center);

        cbPolice = findViewById(R.id.cbPolice);
        cbAmbulance = findViewById(R.id.cbAmbulance);
        cbFire = findViewById(R.id.cbFire);
        btnSubmit = findViewById(R.id.btnSubmitDispatch);

        createNotificationChannel();

        btnSubmit.setOnClickListener(v -> showConfirmationDialog());
    }

    private void showConfirmationDialog() {
        if (!cbPolice.isChecked() && !cbAmbulance.isChecked() && !cbFire.isChecked()) {
            Toast.makeText(this, "Please select at least one service", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Request");
        builder.setMessage("Are you sure you want to dispatch selected services?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            ArrayList<String> selectedServices = getSelectedServices();
            int priorityScore = (int) (Math.random() * 100);

            sendNotification(selectedServices, priorityScore);

            // Directly navigate to results
            Intent intent = new Intent(this, DispatchResultActivity.class);
            intent.putStringArrayListExtra("SERVICES", selectedServices);
            intent.putExtra("PRIORITY", priorityScore);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private ArrayList<String> getSelectedServices() {
        ArrayList<String> selectedServices = new ArrayList<>();
        if (cbPolice.isChecked()) selectedServices.add("Police");
        if (cbAmbulance.isChecked()) selectedServices.add("Ambulance");
        if (cbFire.isChecked()) selectedServices.add("Fire Brigade");
        return selectedServices;
    }

    private void sendNotification(ArrayList<String> services, int priority) {
        Intent intent = new Intent(this, DispatchResultActivity.class);
        intent.putStringArrayListExtra("SERVICES", services);
        intent.putExtra("PRIORITY", priority);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Emergency Dispatch")
                .setContentText("Emergency Dispatch is ready")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Emergency", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}