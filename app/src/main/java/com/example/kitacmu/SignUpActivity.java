package com.example.kitacmu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // enable edge-to-edge layout
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // apply system-bars inset padding to the root view (id="main")
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom);
            return insets;
        });
    }

    /** Called by the back-arrow ImageButton via android:onClick */
    public void onBackClicked(View view) {
        // go back to login
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        // finish this so it's removed from the back stack
        finish();
    }
}
