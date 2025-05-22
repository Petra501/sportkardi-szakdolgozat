package com.example.sportkardi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sportkardi.Model.Athlete;
import com.example.sportkardi.R;

import java.util.ArrayList;

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Athlete> athletes;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Athlete athlete);
    }

    public PatientListAdapter(Context context, ArrayList<Athlete> athletes, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.athletes = athletes;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_patients_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Athlete athlete = athletes.get(position);
        holder.bind(athlete, onItemClickListener);
    }

    public void setFilteredList(ArrayList<Athlete> filteredList){
        this.athletes = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return athletes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView personalIdTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            personalIdTextView = itemView.findViewById(R.id.personalIdTextView);
        }

        public void bind(Athlete athlete, OnItemClickListener onItemClickListener) {
            nameTextView.setText(athlete.getName());
            personalIdTextView.setText(athlete.getPersonalId());

            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(athlete));
        }
    }
}
