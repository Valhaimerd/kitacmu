// com/example/kitacmu/RecyclerView/JobAdapter.java
package com.example.kitacmu.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kitacmu.R;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(JobModel job);
    }

    private List<JobModel> jobs;
    private OnItemClickListener listener;

    public JobAdapter(List<JobModel> jobs, OnItemClickListener listener) {
        this.jobs     = jobs;
        this.listener = listener;
    }

    @NonNull @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder h, int pos) {
        JobModel job = jobs.get(pos);
        h.tvJobTitle .setText(job.getTitle());
        h.tvJobOwner .setText(job.getOwnerName());
        h.tvSalary   .setText("₱ " + job.getSalary());
        h.tvTime     .setText(job.getTime());
        h.tvLocation .setText(job.getLocation());
        h.tvDuration .setText(job.getDuration());
        // card‐click => callback
        h.itemView.setOnClickListener(v -> listener.onItemClick(job));
    }

    @Override public int getItemCount() {
        return jobs == null ? 0 : jobs.size();
    }

    /** swap in a new list and refresh */
    public void updateList(List<JobModel> newList) {
        this.jobs = newList;
        notifyDataSetChanged();
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView tvJobTitle, tvJobOwner, tvSalary, tvTime, tvLocation, tvDuration;
        ImageView ivSalaryIcon, ivLocationIcon, ivDurationIcon;
        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobTitle     = itemView.findViewById(R.id.tvJobTitle);
            tvJobOwner     = itemView.findViewById(R.id.tvJobOwner);
            tvSalary       = itemView.findViewById(R.id.tvSalary);
            tvTime         = itemView.findViewById(R.id.tvTime);
            tvLocation     = itemView.findViewById(R.id.tvLocation);
            tvDuration     = itemView.findViewById(R.id.tvDuration);
            ivSalaryIcon   = itemView.findViewById(R.id.ivSalaryIcon);
            ivDurationIcon = itemView.findViewById(R.id.ivDurationIcon);
        }
    }
}
