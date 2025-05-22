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

import com.example.sportkardi.Model.Appointment;
import com.example.sportkardi.R;

import java.util.ArrayList;
import java.util.Objects;

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.Viewholder> {
    private ArrayList<Appointment> items;

    // Interfész a kattintáskezeléshez
    public interface OnAppointmentClickListener {
        void onAppointmentClick(Appointment appointment);
    }

    // Interfész a törlés kezeléséhez
    public interface OnDeletetmentClickListener {
        void onDeleteAppointmentClick(Appointment appointment);
    }

    public AppointmentListAdapter() {
    }

    private OnAppointmentClickListener listener;
    private OnDeletetmentClickListener deleteListener;

    // Konstruktor a listener beállításához
    public AppointmentListAdapter(ArrayList<Appointment> items) {
        this.items = items;
    }

    public void setOnAppointmentClickListener(OnAppointmentClickListener listener) {
        this.listener = listener;
    }
    public void setDeletetmentClickListener(OnDeletetmentClickListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public Appointment getItem(int position) {
        return items.get(position);
    }

    public void updateAppointments(ArrayList<Appointment> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        notifyDataSetChanged(); // Értesíti az adaptert, hogy minden adat változott
    }

    @NonNull
    @Override
    public AppointmentListAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_all_appointment, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentListAdapter.Viewholder holder, int position) {
        Appointment appointment = items.get(position);
        holder.timeTextView.setText(appointment.getTimeRange());
        holder.nameTextView.setText(appointment.getPersonalId());

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

        if (Objects.equals(appointment.getPersonalId(), "-")){
            holder.deleteAppointmentConstraintLayout.setVisibility(View.GONE);
        } else {
            holder.deleteAppointmentConstraintLayout.setVisibility(View.VISIBLE);
        }

        // Kattintáskezelő az egész itemView-ra
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && Objects.equals(appointment.getPersonalId(), "-")) {
                listener.onAppointmentClick(appointment);
            }
        });

        holder.deleteAppointmentConstraintLayout.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteAppointmentClick(appointment);
            }
        });
    }

    private void showPopup(Context context, String message) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_message, null);
        Button doneButton = view.findViewById(R.id.doneButton);
        TextView messageTextView = view.findViewById(R.id.messageTextView);

        messageTextView.setText(message);

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
        TextView nameTextView;
        ConstraintLayout messageConstraintLayout;
        ConstraintLayout deleteAppointmentConstraintLayout;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            messageConstraintLayout = itemView.findViewById(R.id.messageConstraintLayout);
            deleteAppointmentConstraintLayout = itemView.findViewById(R.id.deleteAppointmentConstraintLayout);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

