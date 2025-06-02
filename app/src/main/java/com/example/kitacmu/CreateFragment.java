package com.example.kitacmu;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateFragment extends Fragment {
    private TextInputEditText etJobTitle,
            etDescription,
            etLocation,
            etDateTime,
            etDuration,
            etSalary;
    private MaterialButton btnSubmit;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Firebase
        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();

        // bind views
        etJobTitle    = view.findViewById(R.id.etJobTitle);
        etDescription = view.findViewById(R.id.etDescription);
        etLocation    = view.findViewById(R.id.etLocation);
        etDateTime    = view.findViewById(R.id.etDateTime);
        etDuration    = view.findViewById(R.id.etDuration);
        etSalary      = view.findViewById(R.id.etSalary);
        btnSubmit     = view.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> submitJob());
    }

    private void submitJob() {
        String title       = etJobTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String location    = etLocation.getText().toString().trim();
        String dateTime    = etDateTime.getText().toString().trim();
        String duration    = etDuration.getText().toString().trim();
        String salary      = etSalary.getText().toString().trim();

        // simple validation
        if (TextUtils.isEmpty(title)) {
            etJobTitle.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(description)) {
            etDescription.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(location)) {
            etLocation.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(dateTime)) {
            etDateTime.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(duration)) {
            etDuration.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(salary)) {
            etSalary.setError("Required");
            return;
        }

        String uid = auth.getCurrentUser() != null
                ? auth.getCurrentUser().getUid()
                : "";

        // build map
        Map<String,Object> job = new HashMap<>();
        job.put("title",       title);
        job.put("description", description);
        job.put("location",    location);
        job.put("dateTime",    dateTime);
        job.put("duration",    duration);
        job.put("salary",      salary);
        job.put("userUid",     uid);
        // initialize "hired" as an empty list of UIDs
        job.put("hired",       new ArrayList<String>());

        // write to Firestore
        db.collection("workjobs")
                .add(job)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(getContext(),
                            "Job created successfully!",
                            Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void clearFields() {
        etJobTitle.setText("");
        etDescription.setText("");
        etLocation.setText("");
        etDateTime.setText("");
        etDuration.setText("");
        etSalary.setText("");
    }
}
