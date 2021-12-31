package com.example.unischeduler.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unischeduler.R;

import java.util.ArrayList;

public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder> {

    Context context;
    ArrayList<String> hours;

    public HourAdapter(Context context, ArrayList<String> hours) {
        this.context = context;
        this.hours = hours;
    }

    @NonNull
    @Override
    public HourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_hour, parent, false);
        HourViewHolder holder = new HourViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HourViewHolder holder, int position) {
        String hour = hours.get(position);
        holder.bind(hour);
    }

    @Override
    public int getItemCount() {
        return hours.size();
    }

    public class HourViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        private TextView tvHour;

        public HourViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.tvHour = itemView.findViewById(R.id.tvHour);
        }

        public void bind(String hour) {
            tvHour.setText(hour);
        }
    }
}
