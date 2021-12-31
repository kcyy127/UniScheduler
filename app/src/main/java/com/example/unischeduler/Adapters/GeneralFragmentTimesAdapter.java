package com.example.unischeduler.Adapters;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unischeduler.Models.ScheduleEvent;
import com.example.unischeduler.Models.Section;
import com.example.unischeduler.databinding.ItemGeneralFragmentTimesBinding;

import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeneralFragmentTimesAdapter extends RecyclerView.Adapter<GeneralFragmentTimesAdapter.GeneralFragmentTimesViewHolder> {

    private static final String TAG = "GeneralFragmentTimesAdapter";

    private Context context;
    private Section section;
    ArrayList<String[]> display;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public GeneralFragmentTimesAdapter(Context context, Section section) {
        this.context = context;
        this.section = section;
        display = new ArrayList<>();
        getDisplayData();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getDisplayData() {
        ArrayList<ScheduleEvent> events = section.getSchedules();
        DateTimeFormatter formatter = DateTimeFormat.forPattern("k:mm");

        while (events.size() != 0) {
            ScheduleEvent event = events.get(0);
            List<ScheduleEvent> samesies = events.stream().filter(s -> s.getTime() == event.getTime() && s.getDuration() == event.getDuration()
                    && s.getName().equals(event.getName()) && s.getLocation().equals(event.getLocation())).collect(Collectors.toList());
            String type = event.getName().substring(event.getName().lastIndexOf(" ") + 1);
            LocalTime startTime = new LocalTime((int) event.getTime() / 3600, (int) event.getTime() % 3600);
            String time = formatter.print(startTime) + " - " + formatter.print(startTime.plusMinutes((int) event.getDuration() / 60));

            ArrayList<Integer> dows = new ArrayList<>();
            for (ScheduleEvent same : samesies) {
                dows.add(getDowFromUnix(same.getDates().get(0)));
            }

            String dow = dowToString(dows);

            String location = event.getLocation();

            display.add(new String[]{type, dow, time, location});
            Log.i(TAG, type + dow + time + location);

            events.removeAll(samesies);
        }
    }

    private int getDowFromUnix(long unix) {
        Instant instant = Instant.ofEpochSecond(unix);
        return instant.toDateTime().toLocalDateTime().getDayOfWeek();
    }

    @NonNull
    @Override
    public GeneralFragmentTimesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemGeneralFragmentTimesBinding binding = ItemGeneralFragmentTimesBinding.inflate(inflater, parent, false);
        return new GeneralFragmentTimesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GeneralFragmentTimesViewHolder holder, int position) {
        holder.bind(display.get(position));
    }

    @Override
    public int getItemCount() {
        return display.size();
    }

    private String dowToString(ArrayList<Integer> nums) {
        ArrayList<String> dows = new ArrayList<>(Arrays.asList("M", "Tu", "W", "Th", "F", "Sa", "Su"));
        Collections.sort(nums);
        StringBuilder builder = new StringBuilder();
        for (int num : nums) {
            builder.append(dows.get(num - 1));
        }
        return builder.toString();
    }

    public class GeneralFragmentTimesViewHolder extends RecyclerView.ViewHolder {

        ItemGeneralFragmentTimesBinding binding;

        public GeneralFragmentTimesViewHolder(@NonNull ItemGeneralFragmentTimesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String[] strings) {
            binding.tvType.setText(strings[0]);
            binding.tvTime.setText(strings[1] + " " + strings[2]);
            binding.tvLocation.setText(strings[3]);
        }
    }
}
