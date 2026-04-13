package com.example.sosapp;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class TrustedContactsActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private EditText etName, etPhone, etRelation;
    private Button btnRegister, btnReset, btnViewAll, btnDeleteAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trusted_contacts);

        dbHelper = new DatabaseHelper(this);
        etName = findViewById(R.id.etContactName);
        etPhone = findViewById(R.id.etContactPhone);
        etRelation = findViewById(R.id.etRelationship);
        btnRegister = findViewById(R.id.btnRegister);
        btnReset = findViewById(R.id.btnReset);
        btnViewAll = findViewById(R.id.btnViewAll);
        btnDeleteAll = findViewById(R.id.btnDeleteAll);

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String relation = etRelation.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || relation.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isInserted = dbHelper.insertData(name, phone, relation);
            if (isInserted) {
                Toast.makeText(this, "Contact Registered Successfully", Toast.LENGTH_SHORT).show();
                resetFields();
            } else {
                Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
            }
        });

        btnReset.setOnClickListener(v -> resetFields());

        btnViewAll.setOnClickListener(v -> viewAll());

        btnDeleteAll.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete all contacts?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteAllData();
                    Toast.makeText(this, "All contacts deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
        });
    }

    private void resetFields() {
        etName.setText("");
        etPhone.setText("");
        etRelation.setText("");
    }

    private void viewAll() {
        Cursor res = dbHelper.getAllData();
        if (res.getCount() == 0) {
            showMessage("Error", "No contacts found");
            return;
        }

        StringBuilder buffer = new StringBuilder();
        while (res.moveToNext()) {
            buffer.append("ID: ").append(res.getString(0)).append("\n");
            buffer.append("Name: ").append(res.getString(1)).append("\n");
            buffer.append("Phone: ").append(res.getString(2)).append("\n");
            buffer.append("Relation: ").append(res.getString(3)).append("\n\n");
        }

        showMessage("Trusted Contacts", buffer.toString());
    }

    private void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}