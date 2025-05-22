package com.example.sportkardi.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportkardi.Model.TodaysAppointment;
import com.example.sportkardi.R;

import java.util.ArrayList;

public class TodaysAppointmentListAdapter extends RecyclerView.Adapter<TodaysAppointmentListAdapter.Viewholder>{
    private ArrayList<TodaysAppointment> items;

    public TodaysAppointmentListAdapter(ArrayList<TodaysAppointment> items) {
        this.items = items;
    }

    public TodaysAppointmentListAdapter() {
    }

    public TodaysAppointment getItem(int position) {
        return items.get(position);
    }

    public void updateAppointments(ArrayList<TodaysAppointment> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        notifyDataSetChanged(); // Értesíti az adaptert, hogy minden adat változott
    }

    @NonNull
    @Override
    public com.example.sportkardi.Adapter.TodaysAppointmentListAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_todays_appointments, parent, false);
        return new com.example.sportkardi.Adapter.TodaysAppointmentListAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.sportkardi.Adapter.TodaysAppointmentListAdapter.Viewholder holder, int position) {
        TodaysAppointment appointment = items.get(position);
        holder.timeTextView.setText(appointment.getTime());
        holder.dayTextView.setText(appointment.getPersonalId());
    }


    public class Viewholder extends RecyclerView.ViewHolder {
        TextView timeTextView;
        TextView dayTextView;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

