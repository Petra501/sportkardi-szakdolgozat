package com.example.sportkardi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sportkardi.Model.BloodPressure;
import com.example.sportkardi.R;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PrevoiusBloodPressureListAdapter extends RecyclerView.Adapter<PrevoiusBloodPressureListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<BloodPressure> bloodPressures;
    private PrevoiusBloodPressureListAdapter.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(BloodPressure bloodPressure);
    }

    public PrevoiusBloodPressureListAdapter(Context context, ArrayList<BloodPressure> bloodPressures, PrevoiusBloodPressureListAdapter.OnItemClickListener onItemClickListener) {
        this.context = context;
        this.bloodPressures = bloodPressures;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public PrevoiusBloodPressureListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_blood_pressure_item, parent, false);
        return new PrevoiusBloodPressureListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PrevoiusBloodPressureListAdapter.ViewHolder holder, int position) {
        BloodPressure bloodPressure = bloodPressures.get(position);
        holder.bind(bloodPressure, onItemClickListener);
    }

    public void setFilteredList(ArrayList<BloodPressure> filteredList) {
        this.bloodPressures = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return bloodPressures.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView timeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
        
        public void bind(BloodPressure bloodPressures, PrevoiusBloodPressureListAdapter.OnItemClickListener onItemClickListener) {

            Timestamp timestamp = bloodPressures.getTime();
            if (timestamp != null) {
                Date date = timestamp.toDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy. MMMM dd.", new Locale("hu"));
                timeTextView.setText(sdf.format(date));
            } else {
                timeTextView.setText("No timestamp available");
            }

            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(bloodPressures));
        }
    }
}
