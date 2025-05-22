package com.example.sportkardi.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.sportkardi.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarView extends LinearLayout {
    private ImageButton previousButton, nextButton;
    private TextView currentDateTextView;
    private GridView daysGridView;
    private static final int MAX_CALENDAR_DAYS = 42;
    private Calendar calendar = Calendar.getInstance(new Locale("hu", "HU"));
    private Context context;
    private Date selectedDate;
    private List<Date> dates = new ArrayList<>();
    private Map<String, String> specialDays = new HashMap<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy. MMMM", new Locale("hu", "HU"));
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", new Locale("hu", "HU"));
    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", new Locale("hu", "HU"));
    private AlertDialog alertDialog;
    private CalendarGridAdapter calendarGridAdapter;
    private OnDateChangeListener dateChangeListener;
    private Map<String, Integer> dayToAppointmentsCount;


    public CalendarView(Context context) {
        super(context);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        InitializeLayout();
        SetupCalendar();

        previousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                SetupCalendar();
            }
        });

        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                SetupCalendar();
            }
        });

        daysGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedDate = dates.get(position);
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.setTime(selectedDate);

                int year = selectedCalendar.get(Calendar.YEAR);
                int month = selectedCalendar.get(Calendar.MONTH);
                int dayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH);

                notifyDateChange(year, month, dayOfMonth);

                SetupCalendar();
            }
        });

    }

    public void setSpecialDays(Map<String, String> specialDays) {
        this.specialDays.clear();
        this.specialDays.putAll(specialDays);
        invalidate(); // Újrarajzolja a nézetet
    }

    public Map<String, String> getSpecialDays() {
        return specialDays;
    }

    public void setOnDateChangeListener(OnDateChangeListener listener) {
        this.dateChangeListener = listener;
    }

    private void notifyDateChange(int year, int month, int dayOfMonth) {
        if (dateChangeListener != null) {
            dateChangeListener.onDateChanged(year, month, dayOfMonth);
        }
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void InitializeLayout(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_layout, this);
        nextButton = view.findViewById(R.id.nextButton);
        previousButton = view.findViewById(R.id.previousButton);
        currentDateTextView = view.findViewById(R.id.currentDateTextView);
        daysGridView = view.findViewById(R.id.daysGridView);
    }

    public void ColorCells(Map<String, Integer> dayToAppointmentsCount) {
        this.dayToAppointmentsCount = dayToAppointmentsCount;
        SetupCalendar();
    }

    private void SetupCalendar() {
        String currentDate = dateFormat.format(calendar.getTime());
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        currentDateTextView.setText(currentDate);
        dates.clear();

        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;

        if (firstDayOfMonth < 0) {
            firstDayOfMonth += 7;
        }

        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);

        while (dates.size() < MAX_CALENDAR_DAYS) {
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Ha a map null, hozzunk létre egy üres map-et
        if (dayToAppointmentsCount == null) {
            dayToAppointmentsCount = new HashMap<>();
        }

        // Mindig a Map-et fogadó konstruktort használjuk
        calendarGridAdapter = new CalendarGridAdapter(context, dates, calendar, dayToAppointmentsCount, selectedDate, specialDays);

        daysGridView.setAdapter(calendarGridAdapter);
    }

}
