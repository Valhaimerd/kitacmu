// com/example/kitacmu/RecyclerView/WorkplaceAdapter.java
package com.example.kitacmu.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kitacmu.R;
import java.util.List;

public class WorkplaceAdapter
        extends RecyclerView.Adapter<WorkplaceAdapter.VH> {

    public interface OnJobClickListener {
        void onJobClick(WorkplaceModel job);
    }
    private List<WorkplaceModel> items;
    private OnJobClickListener listener;

    public WorkplaceAdapter(List<WorkplaceModel> items) {
        this.items = items;
    }
    public void setOnJobClickListener(OnJobClickListener l) {
        this.listener = l;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_work, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        WorkplaceModel m = items.get(pos);
        h.tvTitle   .setText(m.getTitle());
        h.tvOwner   .setText(m.getOwner());
        h.tvSalary  .setText(m.getSalary());
        h.tvTime    .setText(m.getTime());
        h.tvLocation.setText(m.getLocation());
        h.tvDuration.setText(m.getDuration());

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onJobClick(m);
        });
    }

    @Override public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void updateList(List<WorkplaceModel> newList) {
        this.items = newList;
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvOwner, tvSalary, tvTime, tvLocation, tvDuration;
        public VH(@NonNull View v) {
            super(v);
            tvTitle    = v.findViewById(R.id.tvJobTitle);
            tvOwner    = v.findViewById(R.id.tvJobOwner);
            tvSalary   = v.findViewById(R.id.tvSalary);
            tvTime     = v.findViewById(R.id.tvTime);
            tvLocation = v.findViewById(R.id.tvLocation);
            tvDuration = v.findViewById(R.id.tvDuration);
        }
    }
}
