package com.example.hackathon.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hackathon.data.models.Task;
import com.example.hackathon_infomatrix.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskCompleted(Task task);
        void onTaskDeleted(Task task);
    }

    public TaskAdapter(List<Task> tasks, OnTaskClickListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);

        holder.taskTitle.setText(task.getTitle());
        holder.taskCategory.setText(task.getCategory());
        holder.taskCheckbox.setChecked(task.isCompleted());

        if (task.isCompleted()) {
            holder.taskTitle.setAlpha(0.5f);
            holder.taskCheckbox.setEnabled(false);
        } else {
            holder.taskTitle.setAlpha(1f);
            holder.taskCheckbox.setEnabled(true);
        }

        holder.taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                task.setCompleted(true);
                task.setCompletedDate(getCurrentDate());
                listener.onTaskCompleted(task);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            listener.onTaskDeleted(task);
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    public void updateTasks(List<Task> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox taskCheckbox;
        TextView taskTitle;
        TextView taskCategory;
        TextView btnDelete;

        TaskViewHolder(View itemView) {
            super(itemView);
            taskCheckbox = itemView.findViewById(R.id.taskCheckbox);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskCategory = itemView.findViewById(R.id.taskCategory);
            btnDelete = itemView.findViewById(R.id.btnDeleteTask);
        }
    }
}