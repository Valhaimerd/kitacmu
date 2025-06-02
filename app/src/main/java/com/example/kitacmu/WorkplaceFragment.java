// com/example/kitacmu/WorkplaceFragment.java
package com.example.kitacmu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.*;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.example.kitacmu.RecyclerView.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;

public class WorkplaceFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private RecyclerView rv;
    private WorkplaceAdapter adapter;
    private List<WorkplaceModel> jobs = new ArrayList<>();
    private String myName;

    @Nullable @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_workplace, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        super.onViewCreated(v,b);

        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();
        rv   = v.findViewById(R.id.rvApplications);

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new WorkplaceAdapter(jobs);
        adapter.setOnJobClickListener(this::showManageJobDialog);
        rv.setAdapter(adapter);

        // load current user's name, then their jobs
        String me = auth.getCurrentUser().getUid();
        db.collection("users")
                .document(me)
                .get()
                .addOnSuccessListener(doc -> {
                    String f = doc.getString("firstName");
                    String l = doc.getString("lastName");
                    myName = (f==null?"":f) + " " + (l==null?"":l);
                    loadMyJobs(me);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Failed to load profile: "+e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void loadMyJobs(String myUid) {
        db.collection("workjobs")
                .whereEqualTo("userUid", myUid)
                .orderBy("dateTime", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snap -> {
                    jobs.clear();
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        String id       = d.getId();
                        String title    = d.getString("title");
                        String salary   = "₱ " + d.getString("salary");
                        String time     = d.getString("dateTime");
                        String loc      = d.getString("location");
                        String dur      = d.getString("duration");
                        jobs.add(new WorkplaceModel(
                                id, title, myName, salary, time, loc, dur
                        ));
                    }
                    adapter.updateList(jobs);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Failed loading jobs: "+e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void showManageJobDialog(WorkplaceModel job) {
        View dlgView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_manage_job, null);
        TextView tvTitle    = dlgView.findViewById(R.id.tvDialogJobTitle);
        LinearLayout llApps = dlgView.findViewById(R.id.llApplicants);
//        Button   btnEdit    = dlgView.findViewById(R.id.btnEditJob);
        Button   btnDelete  = dlgView.findViewById(R.id.btnDeleteJob);

        tvTitle.setText(job.getTitle());

        AlertDialog dlg = new AlertDialog.Builder(getContext())
                .setView(dlgView)
                .create();

        // 1) load the hired array
        db.collection("workjobs")
                .document(job.getId())
                .get()
                .addOnSuccessListener(doc -> {
                    @SuppressWarnings("unchecked")
                    List<String> hired = (List<String>)doc.get("hired");
                    llApps.removeAllViews();
                    if (hired!=null && !hired.isEmpty()) {
                        for (String uid : hired) {
                            // for each UID fetch the user's name
                            db.collection("users")
                                    .document(uid)
                                    .get()
                                    .addOnSuccessListener(uDoc -> {
                                        String fn = uDoc.getString("firstName");
                                        String ln = uDoc.getString("lastName");
                                        String fullName = (fn==null?"":fn) + " " + (ln==null?"":ln);

                                        // build a row: [ Name • REMOVE ]
                                        LinearLayout row = new LinearLayout(getContext());
                                        row.setOrientation(LinearLayout.HORIZONTAL);
                                        row.setPadding(0,8,0,8);

                                        TextView tv = new TextView(getContext());
                                        tv.setText(fullName);
                                        tv.setLayoutParams(new LinearLayout.LayoutParams(0,
                                                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                                        Button btnRemove = new Button(getContext());
                                        btnRemove.setText("Remove");
                                        btnRemove.setOnClickListener(x -> {
                                            db.collection("workjobs")
                                                    .document(job.getId())
                                                    .update("hired", FieldValue.arrayRemove(uid))
                                                    .addOnSuccessListener(__ -> {
                                                        llApps.removeView(row);
                                                        Toast.makeText(getContext(),
                                                                "Removed "+fullName, Toast.LENGTH_SHORT).show();
                                                    });
                                        });

                                        row.addView(tv);
                                        row.addView(btnRemove);
                                        llApps.addView(row);
                                    });
                        }
                    } else {
                        TextView none = new TextView(getContext());
                        none.setText("No applicants yet.");
                        none.setPadding(0,16,0,16);
                        llApps.addView(none);
                    }
                });

        // Edit button → launch your edit activity/fragment
//        btnEdit.setOnClickListener(x -> {
//            dlg.dismiss();
//            // e.g. startActivity(EditJobActivity.intentFor(this, job.getId()));
//        });

        // Delete button → delete the job document
        btnDelete.setOnClickListener(x -> {
            db.collection("workjobs")
                    .document(job.getId())
                    .delete()
                    .addOnSuccessListener(__ -> {
                        Toast.makeText(getContext(),
                                "Job deleted", Toast.LENGTH_SHORT).show();
                        dlg.dismiss();
                        loadMyJobs(auth.getCurrentUser().getUid());
                    });
        });

        dlg.show();
    }
}
