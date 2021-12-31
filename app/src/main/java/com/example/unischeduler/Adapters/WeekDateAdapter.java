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
import com.example.unischeduler.WeekToScheduleInterface;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;

public class WeekDateAdapter extends RecyclerView.Adapter<WeekDateAdapter.WeekDateViewHolder> {

    public static final String TAG = "WeekDateAdapter";

    private Context context;
    private ArrayList<LocalDate> dates;
    private LocalDate selectedDate;
    private WeekToScheduleInterface wtsInterface;

    public WeekDateAdapter(Context context, ArrayList<LocalDate> dates, LocalDate selectedDate,
                           WeekToScheduleInterface wtsInterface) {
        this.context = context;
        this.dates = dates;
        this.selectedDate = selectedDate;
        this.wtsInterface = wtsInterface;
    }

    @NonNull
    @Override
    public WeekDateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_weekdate, parent, false);
        WeekDateViewHolder viewHolder = new WeekDateViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WeekDateViewHolder holder, int position) {
        LocalDate date = dates.get(position);
        holder.bind(date);
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public class WeekDateViewHolder extends RecyclerView.ViewHolder {
        private TextView tvWeek, tvDate;

        public WeekDateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWeek = itemView.findViewById(R.id.tvWeek);
            tvDate = itemView.findViewById(R.id.tvDate);
        }

        public void bind(LocalDate date) {
            tvWeek.setText(DateTimeFormat.forPattern("E").print(date).substring(0, 1));
            tvDate.setText(DateTimeFormat.forPattern("d").print(date));

            if (date.isEqual(selectedDate)) {
                tvDate.setBackgroundResource(R.drawable.background_date_selected);
                tvDate.setTextColor(context.getResources().getColor(R.color.my_dark_background));
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View itemView) {
                        wtsInterface.startHomeActivity();
                        return true;
                    }
                });
            } else {
                tvDate.setBackgroundResource(0);
                tvDate.setTextColor(context.getResources().getColor(R.color.my_white));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "date clicked!");
                    wtsInterface.changeDate(date);
                    selectedDate = date;
                }
            });
        }
    }
}
