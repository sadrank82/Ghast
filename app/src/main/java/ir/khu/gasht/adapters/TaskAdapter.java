package ir.khu.gasht.adapters;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ir.khu.gasht.AlarmReceiver;
import ir.khu.gasht.DatabaseManager;
import ir.khu.gasht.R;
import ir.khu.gasht.TaskManageActivity;
import ir.khu.gasht.models.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    private final Activity activity;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
    private List<Task> itemList;

    public TaskAdapter(Activity activity, List<Task> itemList) {
        this.activity = activity;
        this.itemList = itemList;
    }

    public void setTasks(List<Task> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // اتصال به فایل XML آیتم
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // دریافت آیتم فعلی
        Task item = itemList.get(position);

        DatabaseManager databaseManager = new DatabaseManager(activity);

        holder.checkBox.setOnCheckedChangeListener(null);

        // تنظیم داده‌های نمایش داده شده
        holder.title.setText(item.getTitle());
        holder.checkBox.setChecked(item.getIsChecked() == 1);

        // نمایش تاریخ هشدار (در صورت وجود)
        if (item.hasAlert()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(item.getAlertDate());
            holder.alarmDate.setVisibility(VISIBLE);
            holder.alarmDate.setText(sdf.format(calendar.getTime()));
            holder.alarmDate.setTextColor(item.isAlertInPast() ? Color.RED : Color.BLACK);
        } else {
            holder.alarmDate.setVisibility(GONE);
        }

        // خط زدن متن در صورت انجام شدن تسک
        if (item.getIsChecked() == 1) {
            holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.title.setPaintFlags(holder.title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // تنظیم Listener
        holder.checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            Task task = itemList.get(adapterPosition);
            task.setIsChecked(isChecked ? 1 : 0);
            databaseManager.updateTask(task);

            if (isChecked) {
                holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                // لغو آلارم اگر وجود داشته باشد
                if (task.hasAlert() && !task.isAlertInPast()) {
                    cancelAlarm(task);
                }

            } else {
                holder.title.setPaintFlags(holder.title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

                // فعال‌سازی مجدد آلارم در صورت وجود و معتبر بودن
                if (task.hasAlert() && !task.isAlertInPast()) {
                    setAlarm(task);
                }
            }
        });

        // کلیک روی آیتم: باز کردن صفحه ویرایش
        holder.layout.setOnClickListener(view -> {
            Intent intent = new Intent(activity, TaskManageActivity.class);
            intent.putExtra("task", item);
            intent.putExtra("isNewTask", false);
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        // تعداد آیتم‌ها
        return itemList != null ? itemList.size() : 0;
    }

    // متد لغو آلارم
    private void cancelAlarm(Task task) {
        Intent intent = new Intent(activity, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                activity,
                task.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    // متد فعال‌سازی آلارم
    private void setAlarm(Task task) {
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(activity, AlarmReceiver.class);
        intent.putExtra("alarm_id", 0);
        intent.putExtra("alarm_message", "\"" + task.getTitle() + "\"" + " رو انجام دادی؟");
        intent.putExtra("alarm_id", task.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                activity,
                task.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                return;
            }
        }

        try {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    task.getAlertDate(),
                    pendingIntent
            );
        } catch (SecurityException ignored) {}
    }

    // ViewHolder کلاس داخلی برای نگهداری ویوهای آیتم
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, alarmDate;
        CheckBox checkBox;
        MaterialCardView layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // اتصال ویوها
            title = itemView.findViewById(R.id.title);
            alarmDate = itemView.findViewById(R.id.alarmDate);
            checkBox = itemView.findViewById(R.id.checkBox);
            layout = itemView.findViewById(R.id.layout);
        }
    }
}