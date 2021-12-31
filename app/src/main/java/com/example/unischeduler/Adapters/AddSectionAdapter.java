package com.example.unischeduler.Adapters;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unischeduler.Models.Section;
import com.example.unischeduler.databinding.ItemSectionBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AddSectionAdapter extends RecyclerView.Adapter<AddSectionAdapter.AddSectionViewHolder> {

    // TODO: search bar
    // TODO: Create course activity
    // TODO: Add Course button
    private static final String TAG = "AddSectionAdapter";

    private Context context;
    private ArrayList<Section> sections;

    int selectedPosition = RecyclerView.NO_POSITION;

    public AddSectionAdapter(Context context, ArrayList<Section> sections) {
        this.context = context;
        this.sections = sections;
    }

    @NonNull
    @Override
    public AddSectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemSectionBinding binding = ItemSectionBinding.inflate(inflater, parent, false);
        return new AddSectionViewHolder(binding);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull AddSectionViewHolder holder, int position) {
        Section section = sections.get(position);
        holder.bind(section);
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public class AddSectionViewHolder extends RecyclerView.ViewHolder {

        ItemSectionBinding binding;

        public AddSectionViewHolder(ItemSectionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void bind(Section section) {
            binding.tvProf.setText(section.getInstructor().getLast_name() + ", " + section.getInstructor().getFirst_name());
            binding.tvSection.setText(String.valueOf(getAdapterPosition()));

            String sectionTimes = readableToString(section.scheduleToHashMap());
            binding.tvTimes.setText(sectionTimes);

            binding.cvContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "being clicked + " + selectedPosition);
                    notifyItemChanged(selectedPosition);
                    selectedPosition = getAdapterPosition();
                    notifyItemChanged(selectedPosition);
                }
            });

            if (getAdapterPosition() == selectedPosition) {
                binding.cvContainer.setStrokeWidth(8);
            } else {
                binding.cvContainer.setStrokeWidth(0);
            }
        }

        // type : {  hours : {  DoW (continue to append)  }  }
        @RequiresApi(api = Build.VERSION_CODES.O)
        private String readableToString(HashMap<String, HashMap<String, ArrayList<Integer>>> readable) {
            ArrayList<String> lines = new ArrayList<>();

            for (Map.Entry type : readable.entrySet()) {

                for (Map.Entry hours : ((HashMap<String, ArrayList<Integer>>) type.getValue()).entrySet()) {
                    String dowString = dowToString((ArrayList<Integer>) hours.getValue());
                    lines.add(type.getKey().toString() + " " + dowString + " " + hours.getKey().toString());
                }
            }

            return String.join("\n", lines);

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
    }
}
