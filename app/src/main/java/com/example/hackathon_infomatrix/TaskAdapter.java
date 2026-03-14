package com.example.hackathon_infomatrix;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TaskAdapter extends BaseAdapter {

    private List<TaskItem> taskList;
    private LayoutInflater inflater;
    private FirebaseFirestore db;
    private String userId;

    public TaskAdapter(MainActivity context, List<TaskItem> taskList) {
        this.taskList = taskList;
        this.inflater = LayoutInflater.from(context);
        this.db = FirebaseFirestore.getInstance();
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_task, parent, false);
            holder = new ViewHolder();
            holder.checkbox = convertView.findViewById(R.id.taskCheckbox);
            holder.title = convertView.findViewById(R.id.taskTitle);
            holder.btnDelete = convertView.findViewById(R.id.btnDelete);
            holder.btnGenerate = convertView.findViewById(R.id.btnGenerate);
            holder.swipeBackground = convertView.findViewById(R.id.swipeBackground);
            holder.leftSwipeButtons = convertView.findViewById(R.id.leftSwipeButtons);
            holder.taskContainer = convertView.findViewById(R.id.taskContainer);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TaskItem task = taskList.get(position);
        holder.title.setText(task.getTitle());
        holder.checkbox.setChecked(task.isCompleted());

        // Спочатку ховаємо свайп-кнопки
        holder.swipeBackground.setVisibility(View.GONE);
        holder.leftSwipeButtons.setVisibility(View.GONE);

        // Обробка чекбокса
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                task.setCompleted(isChecked);
                updateTaskInFirestore(task);
            }
        });

        // Обробка видалення
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask(task, position);
            }
        });

        // Обробка кнопки AI
        holder.btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(inflater.getContext(), "AI генерація для: " + task.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        // Додаємо можливість свайпу (спрощено - просто показуємо кнопки при кліку на контейнер)
        holder.taskContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                toggleSwipeButtons(holder);
                return true;
            }
        });

        return convertView;
    }

    private void toggleSwipeButtons(ViewHolder holder) {
        if (holder.leftSwipeButtons.getVisibility() == View.VISIBLE) {
            holder.swipeBackground.setVisibility(View.GONE);
            holder.leftSwipeButtons.setVisibility(View.GONE);
        } else {
            holder.swipeBackground.setVisibility(View.VISIBLE);
            holder.leftSwipeButtons.setVisibility(View.VISIBLE);
        }
    }

    private void updateTaskInFirestore(TaskItem task) {
        db.collection("users").document(userId)
                .collection("tasks").document(task.getId())
                .update("completed", task.isCompleted())
                .addOnFailureListener(e -> {
                    Toast.makeText(inflater.getContext(), "Помилка оновлення", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteTask(TaskItem task, int position) {
        db.collection("users").document(userId)
                .collection("tasks").document(task.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    taskList.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(inflater.getContext(), "Завдання видалено", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(inflater.getContext(), "Помилка видалення", Toast.LENGTH_SHORT).show();
                });
    }

    private static class ViewHolder {
        CheckBox checkbox;
        TextView title;
        ImageView btnDelete, btnGenerate;
        View swipeBackground;
        LinearLayout leftSwipeButtons;
        LinearLayout taskContainer;
    }
}