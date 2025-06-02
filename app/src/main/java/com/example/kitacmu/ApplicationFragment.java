package com.example.kitacmu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.*;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.example.kitacmu.RecyclerView.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;

public class ApplicationFragment extends Fragment
        implements ApplicationAdapter.OnViewDetailsClickListener {

    private FirebaseAuth     auth;
    private FirebaseFirestore db;
    private RecyclerView     rv;
    private TextView         tvAppCount;
    private ApplicationAdapter adapter;

    @Nullable @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_application, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);

        auth       = FirebaseAuth.getInstance();
        db         = FirebaseFirestore.getInstance();
        tvAppCount = v.findViewById(R.id.tvAppCount);
        rv         = v.findViewById(R.id.rvApplications);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ApplicationAdapter(new ArrayList<>());
        adapter.setOnViewDetailsClickListener(this);
        rv.setAdapter(adapter);

        loadApplications();
    }

    private void loadApplications() {
        String me = auth.getCurrentUser().getUid();

        db.collection("applications")
                .whereEqualTo("ownerUid", me)
                .whereEqualTo("status", "p")
                .orderBy("appliedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> {
                    List<ApplicationModel> list = new ArrayList<>();

                    for (DocumentSnapshot appDoc : snap.getDocuments()) {
                        String appId       = appDoc.getId();
                        String applicantUid= appDoc.getString("applicantUid");
                        long   atMs        = appDoc.getTimestamp("appliedAt")
                                .toDate().getTime();
                        String jobUid      = appDoc.getString("jobUid");
                        String status      = appDoc.getString("status");

                        // fetch applicant's profile
                        db.collection("users").document(applicantUid)
                                .get().addOnSuccessListener(userDoc -> {
                                    String fn = userDoc.getString("firstName");
                                    String ln = userDoc.getString("lastName");
                                    String fullName = fn + " " + ln;
                                    String contact  = userDoc.getString("mobile");
                                    @SuppressWarnings("unchecked")
                                    List<String> skills = (List<String>)userDoc.get("skills");

                                    // fetch job data
                                    db.collection("workjobs").document(jobUid)
                                            .get().addOnSuccessListener(jobDoc -> {
                                                String title    = jobDoc.getString("title");
                                                String location = jobDoc.getString("location");

                                                ApplicationModel m = new ApplicationModel(
                                                        appId,
                                                        applicantUid,
                                                        fullName,
                                                        atMs,
                                                        contact,
                                                        title,
                                                        location,
                                                        status,
                                                        skills
                                                );
                                                list.add(m);

                                                // update RecyclerView
                                                adapter.updateList(list);
                                                tvAppCount.setText(list.size() + " Applicants");
                                            });
                                });
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Failed loading applications: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }

    @Override
    public void onViewDetails(String appDocId) {
        View dlgV = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_applicant_details, null);
        AlertDialog dlg = new AlertDialog.Builder(getContext())
                .setView(dlgV).create();

        TextView tvName   = dlgV.findViewById(R.id.tvDialogName);
        TextView tvEmail  = dlgV.findViewById(R.id.tvDialogEmail);
        TextView tvPhone  = dlgV.findViewById(R.id.tvDialogPhone);
        TextView tvSkills = dlgV.findViewById(R.id.tvDialogSkillsList);
        Button  btnReject = dlgV.findViewById(R.id.btnReject);
        Button  btnApprove= dlgV.findViewById(R.id.btnApprove);

        db.collection("applications").document(appDocId)
                .get().addOnSuccessListener(appDoc -> {
                    String applicantUid = appDoc.getString("applicantUid");
                    String jobUid       = appDoc.getString("jobUid");

                    // re-fetch user for email/phone/skills
                    db.collection("users").document(applicantUid)
                            .get().addOnSuccessListener(userDoc -> {
                                String fn = userDoc.getString("firstName");
                                String ln = userDoc.getString("lastName");
                                String fullName = fn + " " + ln;
                                String email    = userDoc.getString("email");
                                String mobile   = userDoc.getString("mobile");
                                @SuppressWarnings("unchecked")
                                List<String> skills = (List<String>)userDoc.get("skills");

                                tvName  .setText(fullName);
                                tvEmail .setText("Email: " + email);
                                tvPhone .setText("Phone: " + mobile);

                                // NOW load the skills from the sub-collection
                                db.collection("users")
                                        .document(applicantUid)
                                        .collection("skills")
                                        .get()
                                        .addOnSuccessListener(skillsSnap -> {
                                            // Aggregate all the "skills" arrays from each doc
                                            List<String> allSkills = new ArrayList<>();
                                            for (DocumentSnapshot skillDoc : skillsSnap.getDocuments()) {
                                                @SuppressWarnings("unchecked")
                                                List<String> arr = (List<String>) skillDoc.get("skills");
                                                if (arr != null) allSkills.addAll(arr);
                                            }

                                            // Display them (or a fallback)
                                            if (allSkills.isEmpty()) {
                                                tvSkills.setText("No skills listed");
                                            } else {
                                                StringBuilder sb = new StringBuilder();
                                                for (String s : allSkills) {
                                                    sb.append("• ").append(s).append("\n");
                                                }
                                                tvSkills.setText(sb.toString().trim());
                                            }

                                            // — now wire up your Reject / Approve buttons as before —
                                            btnReject.setOnClickListener(v -> {
                                                appDoc.getReference()
                                                        .update("status","r")
                                                        .addOnSuccessListener(__ -> {
                                                            Toast.makeText(getContext(),
                                                                            "Rejected", Toast.LENGTH_SHORT)
                                                                    .show();
                                                            dlg.dismiss();
                                                            loadApplications();
                                                        });
                                            });
                                            btnApprove.setOnClickListener(v -> {
                                                appDoc.getReference()
                                                        .update("status","a")
                                                        .addOnSuccessListener(__ -> {
                                                            db.collection("workjobs")
                                                                    .document(jobUid)
                                                                    .update("hired",
                                                                            FieldValue.arrayUnion(applicantUid))
                                                                    .addOnSuccessListener(___ -> {
                                                                        Toast.makeText(getContext(),
                                                                                        "Approved", Toast.LENGTH_SHORT)
                                                                                .show();
                                                                        dlg.dismiss();
                                                                        loadApplications();
                                                                    });
                                                        });
                                            });
                                        })
                                        .addOnFailureListener(e -> {
                                            // if your “skills” sub-collection is empty or missing, you'll get no docs
                                            // you can still wire up buttons here if you like
                                            tvSkills.setText("No skills listed (failed to load)");
                                        });
                                btnApprove.setOnClickListener(x -> {
                                    appDoc.getReference()
                                            .update("status","a")
                                            .addOnSuccessListener(__ -> {
                                                db.collection("workjobs")
                                                        .document(jobUid)
                                                        .update("hired",
                                                                FieldValue.arrayUnion(applicantUid)
                                                        )
                                                        .addOnSuccessListener(___ -> {
                                                            Toast.makeText(getContext(),
                                                                    "Approved", Toast.LENGTH_SHORT).show();
                                                            dlg.dismiss();
                                                            loadApplications();
                                                        });
                                            });
                                });
                            });
                });

        dlg.show();
    }
}
