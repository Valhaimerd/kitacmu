package com.example.kitacmu;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;


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

        // 2) Bottom nav buttons
        btnHome    = findViewById(R.id.find_job); // Home
        btnNotif   = findViewById(R.id.notifications); // Notif
        btnJob     = findViewById(R.id.create_jobs);  // Job
        btnCV      = findViewById(R.id.applicants); // CV/Create
        btnProfile = findViewById(R.id.profile); // Profile

        btnHome.setOnClickListener(v -> loadFragment(new JobFragment()));
        btnNotif.setOnClickListener(v -> loadFragment(new NotificationFragment()));
        btnJob.setOnClickListener(v -> loadFragment(new CreateFragment()));
        btnCV.setOnClickListener(v -> loadFragment(new ApplicationFragment()));
        btnProfile.setOnClickListener(v -> loadFragment(new ProfileFragment()));

        // 3) Load default fragment (Home) on first launch
        if (savedInstanceState == null) {
            loadFragment(new JobFragment());
        }
    }

    /** Helper that swaps the container with any Fragment */
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        // If drawer is open, close it first
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
