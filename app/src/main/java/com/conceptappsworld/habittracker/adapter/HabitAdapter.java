package com.conceptappsworld.habittracker.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.conceptappsworld.habittracker.EditorActivity;
import com.conceptappsworld.habittracker.R;
import com.conceptappsworld.habittracker.data.HabitTrackerContract;
import com.conceptappsworld.habittracker.model.Habit;
import com.conceptappsworld.habittracker.util.ConstantUtil;

import java.util.ArrayList;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private ArrayList<Habit> listData;
    private Context context;

    public HabitAdapter(ArrayList<Habit> alHabit, Context _context) {
        this.listData = alHabit;
        this.context = _context;
    }

    @Override
    public HabitAdapter.HabitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        HabitViewHolder mViewHolder = new HabitViewHolder(view);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(HabitViewHolder holder, int position) {

        Habit habit = listData.get(position);

        holder.tvId.setText(String.valueOf(habit.getHabitId()));
        holder.tvName.setText(habit.getPersonName());
        holder.tvHabit.setText(habit.getHabitName());
        holder.tvGender.setText(getGenderStr(habit.getPersonGender()));
        holder.tvFrequency.setText(String.valueOf(habit.getHabitFrequency()));

    }

    private String getGenderStr(int personGender) {
        String strGender = context.getResources().getString(R.string.gender_unknown);
        switch (personGender) {
            case HabitTrackerContract.HabitEntry.GENDER_UNKNOWN:
                strGender = context.getResources().getString(R.string.gender_unknown);
                break;
            case HabitTrackerContract.HabitEntry.GENDER_MALE:
                strGender = context.getResources().getString(R.string.gender_male);
                break;
            case HabitTrackerContract.HabitEntry.GENDER_FEMALE:
                strGender = context.getResources().getString(R.string.gender_female);
                break;
            default:
                strGender = context.getResources().getString(R.string.gender_unknown);
                break;

        }
        return strGender;
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class HabitViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvId;
        TextView tvName;
        TextView tvHabit;
        TextView tvGender;
        TextView tvFrequency;

        public HabitViewHolder(View itemView) {
            super(itemView);

            tvId = (TextView) itemView.findViewById(R.id.tv_person_id);
            tvName = (TextView) itemView.findViewById(R.id.tv_person_name);
            tvHabit = (TextView) itemView.findViewById(R.id.tv_person_habit);
            tvGender = (TextView) itemView.findViewById(R.id.tv_gender);
            tvFrequency = (TextView) itemView.findViewById(R.id.tv_freq);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int habitId = listData.get(getAdapterPosition()).getHabitId();
            Intent intentEditor = new Intent(context, EditorActivity.class);
            intentEditor.putExtra(ConstantUtil.INTENT_EXTRA_HABIT_ID, habitId);
            context.startActivity(intentEditor);
        }
    }
}
