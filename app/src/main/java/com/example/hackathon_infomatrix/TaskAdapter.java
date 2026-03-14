package com.example.hackathon_infomatrix;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class TaskAdapter extends BaseAdapter {

    private List<TaskModel> tasks;
    private MainActivity activity;
    private int selectedPosition = -1;

    public TaskAdapter(MainActivity activity, List<TaskModel> tasks) {
        this.activity = activity;
        this.tasks = tasks;
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_task, parent, false);
            holder = new ViewHolder();

            // Ініціалізуємо всі view без приведення до FrameLayout
            holder.checkBox = convertView.findViewById(R.id.taskCheckbox);
            holder.title = convertView.findViewById(R.id.taskTitle);
            holder.swipeBackground = convertView.findViewById(R.id.swipeBackground);
            holder.leftSwipeButtons = convertView.findViewById(R.id.leftSwipeButtons);
            holder.btnDelete = convertView.findViewById(R.id.btnDelete);
            holder.btnGenerate = convertView.findViewById(R.id.btnGenerate);
            holder.taskContainer = convertView.findViewById(R.id.taskContainer);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TaskModel task = tasks.get(position);

        setupTaskTitle(holder, task);
        setupCheckbox(holder, task, position);
        setupButtons(holder, task, position);
        setupSwipeListeners(convertView, holder, position);

        return convertView;
    }

    private void setupTaskTitle(ViewHolder holder, TaskModel task) {
        if (holder.title != null) {
            holder.title.setText(task.getTitle());
            if (task.isCompleted()) {
                holder.title.setPaintFlags(holder.title.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                holder.title.setAlpha(0.6f);
            } else {
                holder.title.setPaintFlags(holder.title.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
                holder.title.setAlpha(1.0f);
            }
        }
    }

    private void setupCheckbox(ViewHolder holder, TaskModel task, int position) {
        if (holder.checkBox != null) {
            holder.checkBox.setChecked(task.isCompleted());
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                task.setCompleted(isChecked);
                activity.updateTaskStatus(task, isChecked);
            });
        }
    }

    private void setupButtons(ViewHolder holder, TaskModel task, int position) {
        // Кнопка AI
        if (holder.btnGenerate != null) {
            holder.btnGenerate.setOnClickListener(v -> {
                // TODO: Розкоментувати пізніше
                // Intent intent = new Intent(activity, GenerateActivity.class);
                // intent.putExtra("task_id", task.getId());
                // intent.putExtra("task_title", task.getTitle());
                // activity.startActivity(intent);
                Toast.makeText(activity, "🤖 AI генерація для: " + task.getTitle(), Toast.LENGTH_SHORT).show();
            });
        }

        // Кнопка видалення
        if (holder.btnDelete != null) {
            holder.btnDelete.setOnClickListener(v -> {
                activity.deleteTask(task);
                holder.leftSwipeButtons.setVisibility(View.GONE);
                if (holder.taskContainer != null) {
                    holder.taskContainer.animate().translationX(0).setDuration(200).start();
                }
            });
        }
    }

    private void setupSwipeListeners(View itemView, ViewHolder holder, int position) {
        itemView.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            private boolean isSwiping = false;
            private static final float SWIPE_THRESHOLD = 50;
            private static final float SWIPE_RESISTANCE = 0.3f;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Перевіряємо, чи всі необхідні view існують
                if (holder.leftSwipeButtons == null || holder.taskContainer == null) {
                    return false;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        isSwiping = false;

                        // Ховаємо кнопки для всіх інших позицій
                        if (selectedPosition != -1 && selectedPosition != position) {
                            View parent = (View) v.getParent();
                            if (parent instanceof ListView) {
                                ListView listView = (ListView) parent;
                                int firstVisible = listView.getFirstVisiblePosition();
                                int lastVisible = listView.getLastVisiblePosition();

                                if (selectedPosition >= firstVisible && selectedPosition <= lastVisible) {
                                    View prevView = listView.getChildAt(selectedPosition - firstVisible);
                                    if (prevView != null) {
                                        ViewHolder prevHolder = (ViewHolder) prevView.getTag();
                                        if (prevHolder != null && prevHolder.leftSwipeButtons != null && prevHolder.taskContainer != null) {
                                            prevHolder.leftSwipeButtons.setVisibility(View.GONE);
                                            prevHolder.taskContainer.animate().translationX(0).setDuration(200).start();
                                        }
                                    }
                                }
                            }
                        }
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float deltaX = event.getX() - startX;

                        if (deltaX < 0) {
                            float translationX = Math.max(deltaX * SWIPE_RESISTANCE, -holder.leftSwipeButtons.getWidth());
                            holder.taskContainer.setTranslationX(translationX);

                            if (Math.abs(deltaX) > SWIPE_THRESHOLD) {
                                isSwiping = true;
                            }
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (isSwiping && holder.taskContainer.getTranslationX() < -holder.leftSwipeButtons.getWidth() / 2) {
                            holder.leftSwipeButtons.setVisibility(View.VISIBLE);
                            holder.taskContainer.animate().translationX(-holder.leftSwipeButtons.getWidth()).setDuration(200).start();
                            selectedPosition = position;
                        } else {
                            holder.leftSwipeButtons.setVisibility(View.GONE);
                            holder.taskContainer.animate().translationX(0).setDuration(200).start();
                            selectedPosition = -1;

                            if (!isSwiping) {
                                v.performClick();
                            }
                        }
                        return true;
                }
                return false;
            }
        });
    }

    public void updateTasks(List<TaskModel> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        CheckBox checkBox;
        TextView title;
        View swipeBackground;
        LinearLayout leftSwipeButtons;
        ImageView btnDelete;
        ImageView btnGenerate;
        LinearLayout taskContainer;
    }
}