package com.example.kitacmu;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileEditFragment extends Fragment {
    private EditText etEmail, etContact, etSkills;
    private ImageButton btnConfirm;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(
                R.layout.activity_profile_edit_fragment,  // you can rename this to fragment_profile_edit.xml
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        // edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(
                view.findViewById(R.id.profile_root),
                (v, insets) -> {
                    Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(sb.left, sb.top, sb.right, sb.bottom);
                    return insets;
                }
        );

        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();

        etEmail   = view.findViewById(R.id.tvPersonalEmail);
        etContact = view.findViewById(R.id.tvPersonalContact);
        etSkills  = view.findViewById(R.id.tvSkillsList);
        btnConfirm= view.findViewById(R.id.btnConfirmProfile);

        loadCurrentData();
        btnConfirm.setOnClickListener(v -> saveChanges());
    }

    private void loadCurrentData() {
        FirebaseUser u = auth.getCurrentUser();
        if (u == null) return;
        String uid = u.getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;
                    etEmail.setText(doc.getString("email"));
                    etContact.setText(doc.getString("mobile"));
                });

        db.collection("users")
                .document(uid)
                .collection("skills")
                .document("default")
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;
                    List<String> list = (List<String>)doc.get("skills");
                    if (list != null) {
                        StringBuilder sb = new StringBuilder();
                        for (String s:list) sb.append("• ").append(s).append("\n");
                        etSkills.setText(sb.toString().trim());
                    }
                });
    }

    private void saveChanges() {
        String email   = etEmail.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String raw     = etSkills.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Required"); return;
        }
        if (TextUtils.isEmpty(contact)) {
            etContact.setError("Required"); return;
        }

        List<String> skills = new ArrayList<>();
        for (String line: raw.split("\\r?\\n")) {
            String s = line.replace("•","").trim();
            if (!s.isEmpty()) skills.add(s);
        }

        FirebaseUser u = auth.getCurrentUser();
        if (u == null) return;
        String uid = u.getUid();

        Map<String,Object> upd = new HashMap<>();
        upd.put("email",  email);
        upd.put("mobile", contact);

        db.collection("users")
                .document(uid)
                .update(upd);

        Map<String,Object> sk = new HashMap<>();
        sk.put("skills", skills);
        db.collection("users")
                .document(uid)
                .collection("skills")
                .document("default")
                .set(sk)
                .addOnSuccessListener(a -> {
                    Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
                    // pop back to ProfileFragment
                    requireActivity().getSupportFragmentManager().popBackStack();
                });
    }
}
