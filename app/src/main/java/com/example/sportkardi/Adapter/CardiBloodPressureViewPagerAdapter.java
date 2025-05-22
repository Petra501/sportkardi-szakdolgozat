package com.example.sportkardi.Adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.sportkardi.Fragment.PatientDataFragment;
import com.example.sportkardi.Fragment.PreviousBloodPressureFragment;
import com.example.sportkardi.Fragment.StatisticsBloodPressureFragment;
import com.example.sportkardi.Fragment.TodayBloodPressureFragment;

public class CardiBloodPressureViewPagerAdapter extends FragmentStateAdapter {
    private String personalId;
    private boolean isEditable;

    public CardiBloodPressureViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, String personalId, Boolean isEditable) {
        super(fragmentActivity);
        this.personalId = personalId;
        this.isEditable = isEditable;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new PatientDataFragment();
                break;
            case 1:
                fragment = new TodayBloodPressureFragment();
                break;
            case 2:
                fragment = new PreviousBloodPressureFragment();
                break;
            case 3:
                fragment = new StatisticsBloodPressureFragment();
                break;
            default:
                fragment = new PatientDataFragment();
                break;
        }

        Bundle args = new Bundle();
        args.putString("personalId", personalId);
        args.putBoolean("isEditable", isEditable);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public int getItemCount() {
        return 4;
    }
}
