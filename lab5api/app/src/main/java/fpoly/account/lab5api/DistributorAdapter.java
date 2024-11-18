package fpoly.account.lab5api;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DistributorAdapter extends RecyclerView.Adapter<DistributorAdapter.DistributorViewHolder> {

    private ArrayList<Distributor> distributors;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Distributor distributor);
        void onDeleteClick(Distributor distributor);
    }

    public DistributorAdapter(ArrayList<Distributor> distributors, OnItemClickListener listener) {
        this.distributors = distributors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DistributorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_distributor, parent, false);
        return new DistributorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DistributorViewHolder holder, int position) {
        Distributor distributor = distributors.get(position);
        holder.tvSTT.setText(String.valueOf(position + 1)); // Hiển thị số thứ tự
        holder.tvName.setText(distributor.getName());

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onDeleteClick(distributor);

                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(distributor);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return distributors.size();
    }

    public static class DistributorViewHolder extends RecyclerView.ViewHolder {
        public TextView tvSTT, tvName;
        public ImageView ivDelete;

        public DistributorViewHolder(View itemView) {
            super(itemView);
            tvSTT = itemView.findViewById(R.id.tvSTT);
            tvName = itemView.findViewById(R.id.tvName);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
