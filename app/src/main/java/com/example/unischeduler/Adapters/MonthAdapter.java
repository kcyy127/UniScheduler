package com.example.unischeduler.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unischeduler.MonthToHomeInterface;
import com.example.unischeduler.UniSchedulerApplication;
import com.example.unischeduler.databinding.ItemMonthBinding;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.MonthViewHolder> {

    private static final String TAG = "CalendarAdapter";

    private Context context;
    private List<LocalDate> months;
    private LocalDate selectedDate;
    private MonthToHomeInterface mthInterface;
    private int oldAdapterPosition;

    public MonthAdapter(Context context, List<LocalDate> months, MonthToHomeInterface adapterToActivity) {
        this.context = context;
        this.months = months;
        selectedDate = UniSchedulerApplication.getInstance().getSelectedDate();
        this.mthInterface = adapterToActivity;
        this.oldAdapterPosition = 0;
    }

    @NonNull
    @Override
    public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemMonthBinding itemMonthBinding = ItemMonthBinding.inflate(layoutInflater, parent, false);
        MonthViewHolder viewHolder = new MonthViewHolder(itemMonthBinding);
        return viewHolder;
    }

    public void changeSelectedDateFromActivity(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }

    @Override
    public void onBindViewHolder(@NonNull MonthViewHolder holder, int position) {
        LocalDate localDate = months.get(position);
        holder.bind(localDate);
    }

    @Override
    public int getItemCount() {
        return months.size();
    }

    public interface DateToMonthInterface {
        LocalDate dateClick(LocalDate adapterPosition, int date);
        void dateLongClick();
    }

    public class MonthViewHolder extends RecyclerView.ViewHolder implements DateToMonthInterface {
        private ItemMonthBinding binding;
        private ArrayList<Integer> dates;

        public MonthViewHolder(@NonNull ItemMonthBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(LocalDate localDate) {
            binding.tvMonth.setText(DateTimeFormat.forPattern("MMMM").print(localDate));
            dates = new ArrayList<>();
            for (int i = 1; i <= localDate.dayOfMonth().getMaximumValue(); i++) {
                dates.add(i);
            }
            for (int i = 0; i < localDate.getDayOfWeek() % 7; i++) {
                dates.add(0, null);
            }

            binding.rvDates.setLayoutManager(new GridLayoutManager(context, 7));
            DateAdapterTwo dateAdapterTwo = new DateAdapterTwo(context, dates, selectedDate, months.get(getAdapterPosition()), this);
            binding.rvDates.setAdapter(dateAdapterTwo);

        }

        @Override
        public LocalDate dateClick(LocalDate monthDate, int date) {

            selectedDate = new LocalDate(monthDate.getYear(), monthDate.getMonthOfYear(), date);
            notifyItemChanged(oldAdapterPosition);
            oldAdapterPosition = months.indexOf(monthDate);
            notifyItemChanged(months.indexOf(monthDate));
            mthInterface.changeSelectedDate(selectedDate);
            return selectedDate;
        }

        @Override
        public void dateLongClick() {
            mthInterface.startScheduleActivity();
        }
    }
}
