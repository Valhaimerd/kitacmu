package com.example.kitacmu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ImageButton btnDrawer;
    private ImageButton btnHome, btnNotif, btnJob, btnCV, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 1) Drawer toggle
        drawerLayout = findViewById(R.id.drawer_layout);
        btnDrawer    = findViewById(R.id.btnDrawer);
        btnDrawer.setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START)
        );

        // 2) Hook up “Sign Out” from nav_header.xml
        NavigationView navView = findViewById(R.id.nav_drawer);
        View header = navView.getHeaderView(0);
        TextView tvSignOut = header.findViewById(R.id.tvSignOut);
        tvSignOut.setOnClickListener(v -> {
            // Sign out via FirebaseAuth
            FirebaseAuth.getInstance().signOut();
            // Back to LoginActivity
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });

        // 3) Bottom‐nav buttons
        btnHome    = findViewById(R.id.find_job);    // Jobs
        btnNotif   = findViewById(R.id.workplace);   // Workplace
        btnJob     = findViewById(R.id.create_jobs); // Create Job
        btnCV      = findViewById(R.id.applicants);  // Applicants
        btnProfile = findViewById(R.id.profile);     // Profile

        btnHome.setOnClickListener(v -> loadFragment(new JobFragment()));
        btnNotif.setOnClickListener(v -> loadFragment(new WorkplaceFragment()));
        btnJob.setOnClickListener(v -> loadFragment(new CreateFragment()));
        btnCV.setOnClickListener(v -> loadFragment(new ApplicationFragment()));
        btnProfile.setOnClickListener(v -> loadFragment(new ProfileFragment()));

        // 4) Load default fragment on first launch
        if (savedInstanceState == null) {
            loadFragment(new JobFragment());
        }
    }

    /** Helper that swaps container with given fragment */
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        // Close drawer if open, otherwise normal back
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
