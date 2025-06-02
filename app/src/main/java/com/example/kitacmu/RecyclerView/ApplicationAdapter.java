package com.example.kitacmu.RecyclerView;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kitacmu.R;
import java.util.ArrayList;
import java.util.List;

public class ApplicationAdapter
        extends RecyclerView.Adapter<ApplicationAdapter.AppViewHolder> {

    public interface OnViewDetailsClickListener {
        void onViewDetails(String applicationDocId);
    }

    private List<ApplicationModel>         items = new ArrayList<>();
    private OnViewDetailsClickListener listener;

    public ApplicationAdapter(List<ApplicationModel> initial) {
        if (initial != null) this.items = initial;
    }

    /** Swap in a fresh list and redraw */
    public void updateList(List<ApplicationModel> newList) {
        this.items = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    /** Called by Fragment to receive clicks */
    public void setOnViewDetailsClickListener(OnViewDetailsClickListener l) {
        this.listener = l;
    }

    @NonNull @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_application, parent, false);
        return new AppViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder h, int pos) {
        ApplicationModel m = items.get(pos);

        // name
        h.tvApplicantID.setText(m.getApplicantName());

        // relative time
        CharSequence ago = DateUtils.getRelativeTimeSpanString(
                m.getDateAppliedMs(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS
        );
        h.tvDateApply.setText(ago);

        // contact, job, location
        h.tvContact .setText(m.getContact());
        h.tvJob     .setText(m.getJobTitle());
        h.tvLocation.setText(m.getLocation());

        // click → Fragment callback
        h.tvViewDetails.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewDetails(m.getDocumentId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        TextView tvApplicantID, tvDateApply, tvContact,
                tvJob, tvLocation, tvViewDetails;

        AppViewHolder(@NonNull View itemView) {
            super(itemView);
            tvApplicantID = itemView.findViewById(R.id.tvApplicantID);
            tvDateApply   = itemView.findViewById(R.id.tvDateApply);
            tvContact     = itemView.findViewById(R.id.tvContact);
            tvJob         = itemView.findViewById(R.id.tvJob);
            tvLocation    = itemView.findViewById(R.id.tvLocation);
            tvViewDetails = itemView.findViewById(R.id.tvViewDetails);
        }
    }
}
