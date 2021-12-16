package com.example.unischeduler.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.unischeduler.R;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;

public class DateAdapter extends BaseAdapter {
    private static final String TAG = "DateAdapter";

    private Context context;
    private ArrayList<Integer> dates;
    private LayoutInflater layoutInflater;
    private int adapterPosition;
    private MonthAdapter.DateToMonthInterface dateToMonthInterface;
    private LocalDate monthDate;
    private LocalDate selectedDate;

    public DateAdapter(Context context, ArrayList<Integer> dates, int adapterPosition, MonthAdapter.DateToMonthInterface dateToMonthInterface,
                       LocalDate monthDate, org.joda.time.LocalDate selectedDate) {
        this.context = context;
        this.dates = dates;
        this.adapterPosition = adapterPosition;
        this.dateToMonthInterface = dateToMonthInterface;
        layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.monthDate = monthDate;
        this.selectedDate = selectedDate;
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int datePosition, View convertView, ViewGroup parent) {
        if (convertView == null) {
            Log.i(TAG, "recreating date items");
            convertView = layoutInflater.inflate(com.example.unischeduler.R.layout.item_date, null);
            // listener here
            if (dates.get(datePosition) != null) {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        selectedDate = dateToMonthInterface.customClick(adapterPosition, dates.get(datePosition));
                        Log.i(TAG, DateTimeFormat.forPattern("YYYY MMM dd").print(selectedDate));
                        notifyDataSetChanged();
                    }
                });
            }
        }
        TextView tvDate = (TextView) convertView.findViewById(com.example.unischeduler.R.id.tvDate);


        if (dates.get(datePosition) == null) {
            tvDate.setText("");
        } else {
            int date = dates.get(datePosition);
            tvDate.setText(String.valueOf(date));

//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.i(TAG, String.valueOf(adapterPosition) + " " + String.valueOf(date) + " " + datePosition);
//                    selectedDate = dateToMonthInterface.customClick(adapterPosition, date);
//                    Log.i(TAG, DateTimeFormat.forPattern("YYYY MMM dd").print(selectedDate));
//                    notifyDataSetChanged();
//                }
//            });

            if (selectedDate.isEqual(new LocalDate(monthDate.getYear(), monthDate.getMonthOfYear(), dates.get(datePosition)))) {
                convertView.setBackgroundResource(R.drawable.date_selected);
                tvDate.setTextColor(context.getResources().getColor(R.color.my_dark_background));
            } else {
                convertView.setBackgroundResource(0);
                tvDate.setTextColor(context.getResources().getColor(R.color.my_white));
            }
        }

        return convertView;
    }
}
