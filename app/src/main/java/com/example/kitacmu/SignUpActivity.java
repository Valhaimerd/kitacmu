package com.example.kitacmu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth    auth;
    private FirebaseFirestore db;

    private TextInputEditText
            etFirstName,
            etLastName,
            etEmail,
            etPassword,
            etConfirm,
            etMobile;
    private MaterialButton btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // init Firebase
        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();

        // edge-to-edge inset handling
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(sb.left, sb.top, sb.right, sb.bottom);
                    return insets;
                }
        );

        // bind views
        etFirstName = findViewById(R.id.etFirstName);
        etLastName  = findViewById(R.id.etLastName);
        etEmail     = findViewById(R.id.etEmail);
        etPassword  = findViewById(R.id.etPassword);
        etConfirm   = findViewById(R.id.etConfirm);
        etMobile    = findViewById(R.id.etMobile);
        btnSignUp   = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(v -> attemptSignUp());
    }

    private void attemptSignUp() {
        String first   = etFirstName.getText().toString().trim();
        String last    = etLastName.getText().toString().trim();
        String email   = etEmail.getText().toString().trim();
        String pass    = etPassword.getText().toString();
        String confirm = etConfirm.getText().toString();
        String mobile  = etMobile.getText().toString().trim();

        // validation
        if (TextUtils.isEmpty(first)) {
            etFirstName.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(last)) {
            etLastName.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(pass) || pass.length() < 6) {
            etPassword.setError("Min 6 chars");
            return;
        }
        if (!pass.equals(confirm)) {
            etConfirm.setError("Doesn’t match");
            return;
        }
        if (TextUtils.isEmpty(mobile)) {
            etMobile.setError("Required");
            return;
        }

        // create user
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(
                                this,
                                "Sign-up failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    // registration succeeded
                    FirebaseUser user = auth.getCurrentUser();
                    if (user == null) return;

                    // build profile map
                    Map<String, Object> profile = new HashMap<>();
                    profile.put("firstName", first);
                    profile.put("lastName",  last);
                    profile.put("email",     email);
                    profile.put("mobile",    mobile);

                    // save profile
                    db.collection("users")
                            .document(user.getUid())
                            .set(profile)
                            .addOnSuccessListener(aVoid -> {
                                // now create an empty "skills" document
                                Map<String, Object> skillsMap = new HashMap<>();
                                skillsMap.put("skills", new ArrayList<>());

                                db.collection("users")
                                        .document(user.getUid())
                                        .collection("skills")
                                        .document("default")
                                        .set(skillsMap)
                                        .addOnSuccessListener(a -> onSignupSuccess())
                                        .addOnFailureListener(e -> {
                                            // profile is saved but skills failed
                                            Toast.makeText(
                                                    this,
                                                    "Profile saved, but failed to init skills: " + e.getMessage(),
                                                    Toast.LENGTH_LONG
                                            ).show();
                                            onSignupSuccess();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                // profile write failed
                                Toast.makeText(
                                        this,
                                        "Failed to save profile: " + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                });
    }

    private void onSignupSuccess() {
        Toast.makeText(this, "Registered!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    /** Called by the back-arrow ImageButton via android:onClick */
    public void onBackClicked(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
