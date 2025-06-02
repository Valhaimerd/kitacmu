// com/example/kitacmu/JobFragment.java
package com.example.kitacmu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitacmu.RecyclerView.JobAdapter;
import com.example.kitacmu.RecyclerView.JobModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class JobFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private RecyclerView rvJobs;
    private JobAdapter adapter;
    private List<JobModel> fullList = new ArrayList<>();
    private EditText etSearch;
    private TextView tvJobCount;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             ViewGroup container,
                             Bundle saved) {
        return inf.inflate(R.layout.fragment_job, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        super.onViewCreated(v,b);
        auth       = FirebaseAuth.getInstance();
        db         = FirebaseFirestore.getInstance();

        rvJobs     = v.findViewById(R.id.rvApplications);
        etSearch   = v.findViewById(R.id.etSearch);
        tvJobCount = v.findViewById(R.id.tvAppCount);

        // set up adapter with click‐listener
        adapter = new JobAdapter(fullList, this::askApplyDialog);
        rvJobs.setLayoutManager(new LinearLayoutManager(getContext()));
        rvJobs.setAdapter(adapter);

        loadJobs();
        setupSearchFilter();
    }

    private void loadJobs() {
        String me = Optional.ofNullable(auth.getCurrentUser())
                .map(u -> u.getUid())
                .orElse("");
        db.collection("workjobs")
                .whereNotEqualTo("userUid", me)
                .orderBy("userUid", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snap -> {
                    fullList.clear();
                    // for each job doc fetch poster’s name then add to list
                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        String jobUid   = doc.getId();
                        String title    = doc.getString("title");
                        String salary   = doc.getString("salary");
                        String dateTime = doc.getString("dateTime");
                        String location = doc.getString("location");
                        String duration = doc.getString("duration");
                        String ownerUid = doc.getString("userUid");

                        // look up that user’s name
                        db.collection("users")
                                .document(ownerUid)
                                .get()
                                .addOnSuccessListener(userDoc -> {
                                    String fn = userDoc.getString("firstName");
                                    String ln = userDoc.getString("lastName");
                                    String ownerName = (fn==null?"":fn) + " " + (ln==null?"":ln);

                                    fullList.add(new JobModel(
                                            jobUid,
                                            title,
                                            ownerName,
                                            ownerUid,
                                            salary,
                                            dateTime,
                                            location,
                                            duration
                                    ));
                                    adapter.updateList(fullList);
                                    tvJobCount.setText(fullList.size()+" Jobs");
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(getContext(),
                                                "Failed owner lookup: "+e.getMessage(),
                                                Toast.LENGTH_SHORT
                                        ).show());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Failed loading jobs: "+e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show());
    }

    private void setupSearchFilter() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            @Override public void afterTextChanged(Editable e){}
            @Override
            public void onTextChanged(CharSequence s,int a,int b,int c){
                String term = s.toString().toLowerCase().trim();
                if (term.isEmpty()) {
                    adapter.updateList(fullList);
                    tvJobCount.setText(fullList.size()+" Jobs");
                    return;
                }
                List<JobModel> filtered = new ArrayList<>();
                for (JobModel jm : fullList) {
                    if (jm.getTitle().toLowerCase().contains(term)) {
                        filtered.add(jm);
                    }
                }
                adapter.updateList(filtered);
                tvJobCount.setText(filtered.size()+" Jobs");
            }
        });
    }

    private void askApplyDialog(JobModel job) {
        AlertDialog dlg = new AlertDialog.Builder(getContext())
                .setTitle("Apply for “" + job.getTitle() + "”?")
                .setMessage("Do you want to submit an application?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", (di, which) -> submitApplication(job))
                .create();

        dlg.show();
        // style the “Yes” button green on teal background:
        dlg.getButton(DialogInterface.BUTTON_POSITIVE)
                .setBackgroundColor(requireContext()
                        .getColor(R.color.teal_700));
        dlg.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(Color.WHITE);
    }

    private void submitApplication(JobModel job) {
        String me = auth.getCurrentUser().getUid();
        Map<String,Object> data = new HashMap<>();
        data.put("applicantUid", me);
        data.put("ownerUid",     job.getOwnerUid());
        data.put("jobUid",       job.getJobUid());
        data.put("status",       "p");
        data.put("appliedAt", FieldValue.serverTimestamp());

        db.collection("applications")
                .add(data)
                .addOnSuccessListener(ref ->
                        Toast.makeText(getContext(),
                                "Application submitted!",Toast.LENGTH_SHORT
                        ).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Failed to apply: "+e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show());
    }
}
