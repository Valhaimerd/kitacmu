package com.example.kitacmu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // enable edge-to-edge
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // inset padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom);
            return insets;
        });

        // FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        // bind views
        etEmail    = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin   = findViewById(R.id.btnLogin);

        // back arrow
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // sign-up link
        findViewById(R.id.tvSignUp).setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class))
        );

        // login button
        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String pass  = etPassword.getText().toString();

        // basic validation
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email required");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            etPassword.setError("Password required");
            return;
        }

        // disable button to prevent double-tap
        btnLogin.setEnabled(false);

        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    btnLogin.setEnabled(true);
                    if (task.isSuccessful()) {
                        // success → go to HomeActivity
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        // failure → show error
                        Toast.makeText(
                                LoginActivity.this,
                                "Login failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}
