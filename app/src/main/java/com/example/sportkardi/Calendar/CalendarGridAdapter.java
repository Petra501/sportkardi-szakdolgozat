package com.example.sportkardi.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sportkardi.R;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarGridAdapter extends ArrayAdapter {
    private List<Date> dates;
    private Calendar currentDate;
    private LayoutInflater inflater;
    private Map<String, Integer> dayToAppointmentsCount;
    private Map<String, String> specialDays;
    private List<Date> recurringDates = new ArrayList<>();
    private List<Date> recurringDatesYes = new ArrayList<>();
    private Date selectedDate;
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", new Locale("hu", "HU"));
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy. MMMM dd.", new Locale("hu", "HU"));
    private SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");


    public CalendarGridAdapter(@NonNull Context context, List<Date> dates, Calendar currentDate, Map<String, Integer> dayToAppointmentsCount, Date selectedDate, Map<String, String> specialDays) {
        super(context, R.layout.calendar_cell_layout);

        this.dates = dates;
        this.currentDate = currentDate;
        this.dayToAppointmentsCount = (dayToAppointmentsCount != null) ? dayToAppointmentsCount : new HashMap<>();
        this.selectedDate = selectedDate != null ? selectedDate : new Date();
        this.specialDays = specialDays;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Date monthDate = dates.get(position);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);

        String selectedYearS = yearFormat.format(selectedDate);
        String selectedMonthS = monthFormat.format(selectedDate);
        String selectedDayS = dayFormat.format(selectedDate);

        List<String> months = Arrays.asList(DateFormatSymbols.getInstance(Locale.forLanguageTag("hu")).getMonths());

        int selectedDay = Integer.parseInt(selectedDayS);
        int selectedMonth = months.indexOf(selectedMonthS) + 1;
        int selectedYear = Integer.parseInt(selectedYearS);

        int dayNumber = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalendar.get(Calendar.MONTH) + 1;
        int displayYear = dateCalendar.get(Calendar.YEAR);

        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentYear = currentDate.get(Calendar.YEAR);

        Calendar actualCurrentDate = Calendar.getInstance();
        int actualCurrentDay = actualCurrentDate.get(Calendar.DAY_OF_MONTH);
        int actualCurrentMonth = actualCurrentDate.get(Calendar.MONTH) + 1;
        int actualCurrentYear = actualCurrentDate.get(Calendar.YEAR);

        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.calendar_cell_layout, parent, false);
        }

        int gridWidth = parent.getWidth() / 8;
        view.setLayoutParams(new ViewGroup.LayoutParams(gridWidth, gridWidth));

        TextView dayTextView = view.findViewById(R.id.dayTextView);
        dayTextView.setText(String.valueOf(dayNumber));

        // hétvége ellenőrzés
        int dayOfWeek = dateCalendar.get(Calendar.DAY_OF_WEEK);
        boolean isWeekend = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);

        // speciális nap ellenőrzés
        boolean isSpecialDay = false;

        for (Date recurringDate : recurringDates) {
            if (monthDate.getDay() == recurringDate.getDay()) {
                if (monthDate.after(recurringDate)) {
                    if (!isWeekend){
                        isSpecialDay = true;
                    } else {
                        isSpecialDay = false;
                    }
                }
            }
        }

        for (Date recurringDate : recurringDatesYes) {
            if (monthDate.getDay() == recurringDate.getDay()) {
                if (monthDate.after(recurringDate)) {
                    if (!isWeekend){
                        isSpecialDay = false;
                    } else {
                        isWeekend = false;
                    }
                }
            }
        }

        for (String key : specialDays.keySet()) {
            try {
                // String -> Date átalakítás
                Date keyDate = dateFormat.parse(key);
                String rule = specialDays.get(key);

                if (rule.equals("recurring-no")) {
                    recurringDates.add(keyDate);
                } else if (rule.equals("recurring-yes")) {
                    recurringDatesYes.add(keyDate);
                }

                // Csak az év, hónap, nap összehasonlításához
                if (isSameDay(keyDate, monthDate)) {

                    if (rule.equals("no-appointment")) {
                        isSpecialDay = true;
                    } else if (rule.equals("allow-only")) {
                        isWeekend = false;
                        isSpecialDay = false;
                    }

                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        
        // pirosbetűs ünnepek listája
        List<String> publicHolidays = Arrays.asList(
                "1-1",  // Újév
                "3-15", // Nemzeti ünnep
                "5-1", // Munka ünnepe
                "8-20", // Államalapítás
                "10-23", // Forradalom napja
                "11-1", // Mindenszentek
                "12-25", // Karácsony
                "12-26"  // Karácsony másnapja
        );

        String currentDateKey = displayMonth + "-" + dayNumber;
        boolean isPublicHoliday = publicHolidays.contains(currentDateKey);

        String[] MONTH_NAMES_HU = {
                "Január", "Február", "Március", "Április", "Május", "Június",
                "Július", "Augusztus", "Szeptember", "Október", "November", "December"
        };

        String monthName = "";

        if (displayMonth >= 1 && displayMonth <= 12) {
            monthName = MONTH_NAMES_HU[displayMonth - 1];
        }

        String dateKey = displayYear + ". " + monthName + " " + dayNumber + ".";

        if (displayMonth == currentMonth && displayYear == currentYear) {
            if (dayNumber == actualCurrentDay && displayMonth == actualCurrentMonth && displayYear == actualCurrentYear) {
                dayTextView.setTextColor(getContext().getResources().getColor(R.color.calendarToday));
            } else {
                TypedValue typedValue = new TypedValue();
                getContext().getTheme().resolveAttribute(android.R.attr.textColor, typedValue, true);
                dayTextView.setTextColor(typedValue.data);
            }


            if (dayToAppointmentsCount != null && dayToAppointmentsCount.containsKey(dateKey)) {
                int count = dayToAppointmentsCount.get(dateKey);

                if (displayMonth == selectedMonth && displayYear == selectedYear && dayNumber == selectedDay) {
                    if (count == 1 || count == 2) {
                        dayTextView.setBackgroundResource(R.drawable.calendar_2_day_today);
                    } else if (count >= 3 && count <= 5) {
                        dayTextView.setBackgroundResource(R.drawable.calendar_4_day_today);
                    } else if (count == 6 || count == 7) {
                        dayTextView.setBackgroundResource(R.drawable.calendar_6_day_today);
                    } else if (count >= 8) {
                        dayTextView.setBackgroundResource(R.drawable.calendar_8_day_today);
                    }
                } else {
                    if (count == 1 || count == 2) {
                        dayTextView.setBackgroundResource(R.drawable.calendar_2_day);
                    } else if (count >= 3 && count <= 5) {
                        dayTextView.setBackgroundResource(R.drawable.calendar_4_day);
                    } else if (count == 6 || count == 7) {
                        dayTextView.setBackgroundResource(R.drawable.calendar_6_day);
                    } else if (count >= 8) {
                        dayTextView.setBackgroundResource(R.drawable.calendar_8_day);
                    }
                }
            } else {
                if (displayMonth == selectedMonth && displayYear == selectedYear && dayNumber == selectedDay) {
                    dayTextView.setBackgroundResource(R.drawable.calendar_0_day_today);
                } else {
                    dayTextView.setBackgroundResource(R.drawable.calendar_0_day);
                }
            }

            if (isWeekend || isPublicHoliday || isSpecialDay) {
                if (displayMonth == selectedMonth && displayYear == selectedYear && dayNumber == selectedDay) {
                    dayTextView.setBackgroundResource(R.drawable.calendar_special_day_today);
                } else {
                    dayTextView.setBackgroundResource(R.drawable.calendar_special_day);
                }
            }

        } else {
            dayTextView.setTextColor(Color.parseColor("#acacac"));
            dayTextView.setBackground(null);
        }

        return view;
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.setTime(date1);
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    public Map<String, Integer> getDayToAppointmentsCount() {
        return dayToAppointmentsCount;
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return dates.indexOf(item);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }
}
