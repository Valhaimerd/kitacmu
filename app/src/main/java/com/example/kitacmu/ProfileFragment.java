package com.example.kitacmu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ProfileFragment extends Fragment {
    private FirebaseAuth    auth;
    private FirebaseFirestore db;

    private TextView tvName,
            tvEmailTop,
            tvPersonalEmail,
            tvPersonalContact,
            tvSkillsList;
    private ImageButton btnEdit;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();

        tvName            = view.findViewById(R.id.tvName);
        tvEmailTop        = view.findViewById(R.id.tvEmail);
        tvPersonalEmail   = view.findViewById(R.id.tvPersonalEmail);
        tvPersonalContact = view.findViewById(R.id.tvPersonalContact);
        tvSkillsList      = view.findViewById(R.id.tvSkillsList);
        btnEdit           = view.findViewById(R.id.btnEditProfile);

        btnEdit.setOnClickListener(v -> openEditFragment());

        loadProfile();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfile();  // reload after user comes back
    }

    private void openEditFragment() {
        // 1) Create your edit fragment
        ProfileEditFragment edit = new ProfileEditFragment();

        // 2) Swap it into your container
        //    Replace R.id.nav_host_fragment with whatever your Fragment container ID is
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.main_content, edit)
                .addToBackStack(null)
                .commit();
    }

    private void loadProfile() {
        FirebaseUser u = auth.getCurrentUser();
        if (u == null) {
            Toast.makeText(getContext(), "Not signed in", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = u.getUid();

        // load main profile doc
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(this::applyProfile)
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Profile load failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );

        // load skills sub-doc
        db.collection("users")
                .document(uid)
                .collection("skills")
                .document("default")
                .get()
                .addOnSuccessListener(this::applySkills)
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Skills load failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }

    private void applyProfile(DocumentSnapshot doc) {
        if (!doc.exists()) return;
        String first  = doc.getString("firstName");
        String last   = doc.getString("lastName");
        String email  = doc.getString("email");
        String mobile = doc.getString("mobile");

        tvName.setText(first + " " + last);
        tvEmailTop.setText(email);
        tvPersonalEmail.setText("Email: " + email);
        tvPersonalContact.setText("Contact No.: " + mobile);
    }

    @SuppressWarnings("unchecked")
    private void applySkills(DocumentSnapshot doc) {
        if (!doc.exists()) {
            tvSkillsList.setText("No skills added");
            return;
        }
        List<String> skills = (List<String>) doc.get("skills");
        if (skills == null || skills.isEmpty()) {
            tvSkillsList.setText("No skills added");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String s : skills) {
            sb.append("• ").append(s).append("\n");
        }
        tvSkillsList.setText(sb.toString().trim());
    }
}
