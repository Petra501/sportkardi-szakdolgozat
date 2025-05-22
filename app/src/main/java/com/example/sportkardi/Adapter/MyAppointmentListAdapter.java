package com.example.sportkardi.Adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportkardi.Model.MyAppointment;
import com.example.sportkardi.R;

import java.util.ArrayList;

public class MyAppointmentListAdapter extends RecyclerView.Adapter<MyAppointmentListAdapter.Viewholder>{
    private ArrayList<MyAppointment> items;

    public MyAppointmentListAdapter(ArrayList<MyAppointment> items) {
        this.items = items;
    }

    public MyAppointmentListAdapter() {
    }

    public MyAppointment getItem(int position) {
        return items.get(position);
    }

    public void updateAppointments(ArrayList<MyAppointment> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        notifyDataSetChanged(); // Értesíti az adaptert, hogy minden adat változott
    }

    @NonNull
    @Override
    public MyAppointmentListAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_my_appointments, parent, false);
        return new MyAppointmentListAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAppointmentListAdapter.Viewholder holder, int position) {
        MyAppointment appointment = items.get(position);
        holder.timeTextView.setText(appointment.getTime());
        holder.dayTextView.setText(appointment.getDay());

        // Message láthatóságának kezelése
        String message = appointment.getMessage();
        if (message != null && !message.equals("Nincs")) {
            holder.messageConstraintLayout.setVisibility(View.VISIBLE);

            // Kattintáskezelő beállítása
            holder.messageConstraintLayout.setOnClickListener(v -> {
                // Popup megjelenítése az üzenettel
                showPopup(holder.itemView.getContext(), message);
            });
        } else {
            holder.messageConstraintLayout.setVisibility(View.GONE);
        }
    }

    private void showPopup(Context context, String message) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_message, null);
        Button doneButton = view.findViewById(R.id.doneButton);
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView messageTextView = view.findViewById(R.id.messageTextView);

        messageTextView.setText(message);
        titleTextView.setText("Megjegyzésem");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        doneButton.findViewById(R.id.doneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView timeTextView;
        TextView dayTextView;
        ConstraintLayout messageConstraintLayout;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
            messageConstraintLayout = itemView.findViewById(R.id.messageConstraintLayout);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
