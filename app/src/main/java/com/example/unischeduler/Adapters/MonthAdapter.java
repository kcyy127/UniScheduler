package com.example.unischeduler.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unischeduler.AdapterToActivity;
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
    private AdapterToActivity adapterToActivity;
    private int oldAdapterPosition;

    public MonthAdapter(Context context, List<LocalDate> months, AdapterToActivity adapterToActivity) {
        this.context = context;
        this.months = months;
        selectedDate = LocalDate.now();
        this.adapterToActivity = adapterToActivity;
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
        public LocalDate customClick(LocalDate adapterPosition, int date);
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

//        @Override
//        public LocalDate customClick(LocalDate monthDate, int date) {
////            Log.i(TAG, "We've been custom clicked!");
//            selectedDate = new LocalDate(months.get(adapterPosition).getYear(),
//                    months.get(adapterPosition).getMonthOfYear(), date);
//            Log.i(TAG, DateTimeFormat.forPattern("YYYY, MMM dd").print(selectedDate));
//            notifyItemChanged(oldAdapterPosition);
//            oldAdapterPosition = adapterPosition;
//            notifyItemChanged(adapterPosition);
//            adapterToActivity.changeSelectedDate(selectedDate);
//            return selectedDate;
//        }

        @Override
        public LocalDate customClick(LocalDate monthDate, int date) {

            selectedDate = new LocalDate(monthDate.getYear(), monthDate.getMonthOfYear(), date);
            notifyItemChanged(oldAdapterPosition);
            oldAdapterPosition = months.indexOf(monthDate);
            notifyItemChanged(months.indexOf(monthDate));
            adapterToActivity.changeSelectedDate(selectedDate);
            return selectedDate;
        }
    }
}
