package com.example.hackathon_infomatrix;

import android.animation.ObjectAnimator;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
    private MainActivity mainActivity;
    private float startX;
    private float startY;
    private float currentTranslation = 0;
    private static final int SWIPE_THRESHOLD = 250;
    private static final int MAX_SWIPE = 350;
    private static final float BUTTONS_WIDTH = 450f;
    private static final int SCROLL_THRESHOLD = 10;

    private View selectedView = null;
    private int selectedPosition = -1;
    private boolean isSwiping = false;
    private boolean isScrolling = false;
    private ListView listView;

    public TaskAdapter(MainActivity context, List<TaskItem> taskList, MainActivity mainActivity) {
        this.taskList = taskList;
        this.inflater = LayoutInflater.from(context);
        this.db = FirebaseFirestore.getInstance();
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.mainActivity = mainActivity;

        // Отримуємо ListView з MainActivity
        this.listView = context.findViewById(R.id.tasklist);
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
            holder.expBadge = convertView.findViewById(R.id.expBadge);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TaskItem task = taskList.get(position);
        holder.title.setText(task.getTitle());

        // Застосування закреслення якщо завдання виконане
        if (task.isCompleted()) {
            holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.title.setAlpha(0.6f);
        } else {
            holder.title.setPaintFlags(holder.title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.title.setAlpha(1.0f);
        }

        // Показуємо досвід за завдання
        holder.expBadge.setText("+" + task.getExpReward() + " exp");

        // Спочатку ховаємо свайп-кнопки
        holder.swipeBackground.setVisibility(View.GONE);
        holder.leftSwipeButtons.setVisibility(View.GONE);

        // Скидаємо трансформацію при повторному використанні
        if (selectedView != convertView) {
            holder.taskContainer.setTranslationX(0);
            holder.swipeBackground.setVisibility(View.GONE);
            holder.leftSwipeButtons.setVisibility(View.GONE);
        } else {
            // Якщо це вибраний вид, відновлюємо його стан
            holder.taskContainer.setTranslationX(currentTranslation);
            if (currentTranslation < -SWIPE_THRESHOLD) {
                holder.swipeBackground.setVisibility(View.VISIBLE);
                holder.leftSwipeButtons.setVisibility(View.VISIBLE);
            }
        }

        // ВИПРАВЛЕНО: Обробка свайпу з підтримкою скролу
        holder.taskContainer.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    startY = event.getY();
                    currentTranslation = v.getTranslationX();
                    isSwiping = false;
                    isScrolling = false;

                    // Запобігаємо скролу під час свайпу
                    listView.requestDisallowInterceptTouchEvent(true);
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float deltaX = event.getX() - startX;
                    float deltaY = event.getY() - startY;

                    // Визначаємо, чи це свайп чи скрол
                    if (!isSwiping && !isScrolling) {
                        if (Math.abs(deltaX) > SCROLL_THRESHOLD && Math.abs(deltaX) > Math.abs(deltaY)) {
                            // Це горизонтальний свайп
                            isSwiping = true;
                            listView.requestDisallowInterceptTouchEvent(true);
                        } else if (Math.abs(deltaY) > SCROLL_THRESHOLD && Math.abs(deltaY) > Math.abs(deltaX)) {
                            // Це вертикальний скрол
                            isScrolling = true;
                            listView.requestDisallowInterceptTouchEvent(false);
                            return false;
                        }
                    }

                    if (isSwiping) {
                        // Обробляємо горизонтальний свайп
                        float newTranslation = currentTranslation + deltaX;

                        // Обмежуємо свайп вліво (негативні значення) до MAX_SWIPE
                        if (newTranslation < 0) {
                            if (newTranslation < -MAX_SWIPE) {
                                newTranslation = -MAX_SWIPE;
                            }
                            v.setTranslationX(newTranslation);

                            // Показуємо фон пропорційно до свайпу
                            float progress = Math.abs(newTranslation) / BUTTONS_WIDTH;
                            if (progress > 0.3f) {
                                holder.swipeBackground.setVisibility(View.VISIBLE);
                                holder.leftSwipeButtons.setVisibility(View.VISIBLE);
                                holder.leftSwipeButtons.setAlpha(progress);
                            } else {
                                holder.swipeBackground.setVisibility(View.GONE);
                                holder.leftSwipeButtons.setVisibility(View.GONE);
                            }
                        }
                        return true;
                    }
                    return false;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (isSwiping) {
                        float finalTranslation = v.getTranslationX();

                        if (finalTranslation < -SWIPE_THRESHOLD) {
                            // Відкриваємо кнопки повністю
                            openSwipe(v, holder);
                        } else {
                            // Закриваємо назад
                            closeSwipe(v, holder);
                        }
                    }

                    // Дозволяємо скрол знову
                    listView.requestDisallowInterceptTouchEvent(false);
                    return true;
            }
            return false;
        });

        // Встановлюємо слухач для чекбокса
        holder.checkbox.setOnCheckedChangeListener(null);
        holder.checkbox.setChecked(task.isCompleted());

        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            private boolean isUpdating = false;

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isUpdating) return;
                isUpdating = true;

                try {
                    if (isChecked && !task.isCompleted()) {
                        // Завдання виконано
                        task.setCompleted(true);
                        task.setWasCompleted(true);
                        updateTaskInFirestore(task);

                        // Переміщуємо в кінець списку
                        moveTaskToEnd(position);

                        // Нараховуємо досвід гравцю
                        mainActivity.onTaskCompleted(task);

                        Toast.makeText(inflater.getContext(),
                                "Завдання виконано! +" + task.getExpReward() + " досвіду",
                                Toast.LENGTH_SHORT).show();

                    } else if (!isChecked && task.isCompleted()) {
                        // Завдання скасовано
                        task.setCompleted(false);
                        task.setWasCompleted(false);
                        updateTaskInFirestore(task);

                        mainActivity.onTaskUncompleted(task);

                        notifyDataSetChanged();
                    }

                    mainActivity.updateFlameCount();

                } finally {
                    isUpdating = false;
                }
            }
        });

        // Обробка видалення (смітник)
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Закриваємо свайп перед видаленням
                if (selectedView != null) {
                    closeSwipe(selectedView, holder);
                }

                // Показуємо діалог підтвердження
                new androidx.appcompat.app.AlertDialog.Builder(inflater.getContext())
                        .setTitle("Видалити завдання")
                        .setMessage("Ви впевнені, що хочете видалити \"" + task.getTitle() + "\"?")
                        .setPositiveButton("Так", (dialog, which) -> {
                            deleteTask(task, position);
                        })
                        .setNegativeButton("Ні", null)
                        .show();
            }
        });

        // Обробка кнопки AI
        holder.btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Закриваємо свайп
                if (selectedView != null) {
                    closeSwipe(selectedView, holder);
                }

                // Показуємо, що AI працює
                Toast.makeText(inflater.getContext(),
                        "🤖 AI генерує підзавдання для: " + task.getTitle(),
                        Toast.LENGTH_SHORT).show();

                // Тут буде логіка AI генерації
            }
        });

        // Закриваємо свайп при кліку на основному контейнері
        holder.taskContainer.setOnClickListener(v -> {
            if (holder.leftSwipeButtons.getVisibility() == View.VISIBLE) {
                closeSwipe(v, holder);
            }
        });

        return convertView;
    }

    private void openSwipe(View v, ViewHolder holder) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "translationX", -BUTTONS_WIDTH);
        animator.setDuration(250);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();

        holder.swipeBackground.setVisibility(View.VISIBLE);
        holder.leftSwipeButtons.setVisibility(View.VISIBLE);
        holder.leftSwipeButtons.setAlpha(1.0f);
        currentTranslation = -BUTTONS_WIDTH;

        selectedView = v;
    }

    private void closeSwipe(View v, ViewHolder holder) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "translationX", 0);
        animator.setDuration(200);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();

        holder.swipeBackground.setVisibility(View.GONE);
        holder.leftSwipeButtons.setVisibility(View.GONE);
        currentTranslation = 0;

        if (selectedView == v) {
            selectedView = null;
            selectedPosition = -1;
        }
    }

    private void moveTaskToEnd(int position) {
        if (position >= 0 && position < taskList.size() && position != taskList.size() - 1) {
            TaskItem task = taskList.get(position);
            taskList.remove(position);
            taskList.add(task);
            notifyDataSetChanged();
        }
    }

    private void updateTaskInFirestore(TaskItem task) {
        db.collection("users").document(userId)
                .collection("tasks").document(task.getId())
                .update("completed", task.isCompleted(),
                        "wasCompleted", task.wasCompleted())
                .addOnFailureListener(e -> {
                    Toast.makeText(inflater.getContext(),
                            "Помилка оновлення: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteTask(TaskItem task, int position) {
        db.collection("users").document(userId)
                .collection("tasks").document(task.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    taskList.remove(position);
                    notifyDataSetChanged();
                    mainActivity.updateFlameCount();
                    Toast.makeText(inflater.getContext(),
                            "🗑 Завдання видалено",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(inflater.getContext(),
                            "Помилка видалення: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private static class ViewHolder {
        CheckBox checkbox;
        TextView title;
        TextView expBadge;
        ImageView btnDelete, btnGenerate;
        View swipeBackground;
        LinearLayout leftSwipeButtons;
        LinearLayout taskContainer;
    }
}