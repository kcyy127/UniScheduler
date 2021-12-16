package com.example.unischeduler.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.example.unischeduler.AdapterToActivity;
import com.example.unischeduler.Adapters.MonthAdapter;
import com.example.unischeduler.CenterSmoothScroller;
import com.example.unischeduler.R;
import com.example.unischeduler.databinding.ActivityMainBinding;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterToActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    public MutableLiveData<LocalDate> selectedDate;

    private DateTimeFormatter dateTimeFormatter;
    private LinearLayoutManager linearLayoutManager;
    private MonthAdapter monthAdapter;
    private ArrayList<LocalDate> months;
    private int oldAdapterPosition;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        oldAdapterPosition = 0;

        createCalendar();

        setListeners();
    }

    public void createCalendar() {
        dateTimeFormatter = DateTimeFormat.forPattern("E, MMM d");

//        LocalDate selectedDate = LocalDate.now();
//        binding.tvDate.setText(dateTimeFormatter.print(selectedDate).substring(0, 4).toUpperCase() +
//                dateTimeFormatter.print(selectedDate).substring(4));

        months = new ArrayList<>();
        LocalDate startDate = new LocalDate(LocalDate.now().getYear(), LocalDate.now().getMonthOfYear(), 1);
        for (int i = -6; i <= 6; i++) {
            months.add(startDate.plusMonths(i));
        }

        monthAdapter = new MonthAdapter(this, months, this);
        linearLayoutManager = new LinearLayoutManager(this);
        binding.rvCalendar.setLayoutManager(linearLayoutManager);
        binding.rvCalendar.setAdapter(monthAdapter);

        selectedDate = new MutableLiveData<>();
        selectedDate.observe(this, new Observer<LocalDate>() {
            @Override
            public void onChanged(LocalDate localDate) {
                binding.tvDate.setText(dateTimeFormatter.print(selectedDate.getValue()).substring(0, 4).toUpperCase() +
                        dateTimeFormatter.print(selectedDate.getValue()).substring(4));
                monthAdapter.changeSelectedDateFromActivity(selectedDate.getValue());
                //old value position
                monthAdapter.notifyItemChanged(oldAdapterPosition);
                int presentPosition = months.indexOf(new LocalDate(selectedDate.getValue().getYear(), selectedDate.getValue().getMonthOfYear(), 1));
                monthAdapter.notifyItemChanged(presentPosition);
                oldAdapterPosition = presentPosition;
            }
        });
        today();

    }

    private void setListeners() {
        binding.tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                today();
                Log.i(TAG, "tvDate clicked");
            }
        });

        binding.cbShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.i(TAG, "is checked!");
                    binding.rvCalendar.setNestedScrollingEnabled(false);
                } else {
                    Log.i(TAG, "not checked!");
                }
            }
        });
    }

    public void today() {
        selectedDate.setValue(LocalDate.now());
        RecyclerView.SmoothScroller smoothScroller = new CenterSmoothScroller(binding.rvCalendar.getContext());
        smoothScroller.setTargetPosition(months.indexOf(
                new LocalDate(selectedDate.getValue().getYear(), selectedDate.getValue().getMonthOfYear(), 1)
        ));
        linearLayoutManager.startSmoothScroll(smoothScroller);
    }

    @Override
    public void changeSelectedDate(LocalDate selectedDate) {
//        this.selectedDate = selectedDate;
        this.selectedDate.setValue(selectedDate);
        this.oldAdapterPosition = months.indexOf(new LocalDate(selectedDate.getYear(), selectedDate.getMonthOfYear(), 1));
    }

}