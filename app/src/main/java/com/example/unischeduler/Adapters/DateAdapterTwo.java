package com.example.unischeduler.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unischeduler.R;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;

public class DateAdapterTwo extends RecyclerView.Adapter<DateAdapterTwo.DateViewHolder> {
    private static final String TAG = "DateAdapterTwo";

    private static final int VIEW_TYPE_EMPTY = 1;
    private static final int VIEW_TYPE_DATE = 0;

    private Context context;
    private ArrayList<Integer> dates;
    private LocalDate selectedDate;
    private LocalDate monthDate;
    private MonthAdapter.DateToMonthInterface dtmInterface;

    public DateAdapterTwo(Context context, ArrayList<Integer> dates, org.joda.time.LocalDate selectedDate,
                          LocalDate monthDate, MonthAdapter.DateToMonthInterface dtmInterface) {
        this.context = context;
        this.dates = dates;
        this.selectedDate = selectedDate;
        this.monthDate = monthDate;
        this.dtmInterface = dtmInterface;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View dateView = layoutInflater.inflate(R.layout.item_date, parent,false);
        DateViewHolder dateViewHolder = new DateViewHolder(dateView);

        if (viewType == VIEW_TYPE_DATE) {
            dateView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "individual date clicked");
                    if (dateViewHolder.getAdapterPosition() == RecyclerView.NO_POSITION) {
                        Log.i(TAG, "No Position");
                    } else {
                        selectedDate = dtmInterface.customClick(monthDate, dates.get(dateViewHolder.getAdapterPosition()));
                        Log.i(TAG, "New selected date is " + DateTimeFormat.forPattern("YYYY MMM dd").print(selectedDate));
                    }

                }
            });
        }
        return dateViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_DATE) {
            holder.bind(dates.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (dates.get(position) == null) {
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_DATE;
        }
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public class DateViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public TextView tvDate;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        }

        public void bind(int date) {
            tvDate.setText(String.valueOf(date));
            boolean isSelectedDate = new LocalDate(monthDate.getYear(), monthDate.getMonthOfYear(), date).isEqual(selectedDate);
            changeDisplay(isSelectedDate);
        }

        public void changeDisplay(boolean isSelected) {
            if (isSelected) {
                itemView.setBackgroundResource(R.drawable.date_selected);
                tvDate.setTextColor(context.getResources().getColor(R.color.my_dark_background));
            } else {
                itemView.setBackgroundResource(0);
                tvDate.setTextColor(context.getResources().getColor(R.color.my_white));
            }
        }
    }
}
