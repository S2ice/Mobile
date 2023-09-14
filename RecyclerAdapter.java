package com.example.planningmeeting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Meeting> meetingList;

    public RecyclerAdapter(Context context, ArrayList<Meeting> meetingList) {
        this.context = context;
        this.meetingList = meetingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Meeting meeting = meetingList.get(position);

        holder.textViewDate.setText(meeting.getDate());
        holder.textViewDescription.setText(meeting.getDescription());
        holder.textViewAttendees.setText(meeting.getAttendees());
        holder.textViewTime.setText(meeting.getTime());
    }

    @Override
    public int getItemCount() {
        return meetingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewDate;
        public TextView textViewMonth;
        public TextView textViewYear;
        public TextView textViewDescription;
        public TextView textViewAttendees;
        public TextView textViewTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewAttendees = itemView.findViewById(R.id.textViewAttendees);
            textViewTime = itemView.findViewById(R.id.textViewTime);
        }
    }
}
