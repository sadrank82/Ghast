package ir.khu.gasht;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import ir.khu.gasht.models.Task;

public class TaskManageActivity extends AppCompatActivity {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
    final private String[] types = {"Clothes", "Gift", "Health", "IDcard", "Tools"};
    private Calendar currentDate = Calendar.getInstance();
    private EditText titleInput, dateInput;
    private ImageView showPicker, deleteDate, imageBack, deleteType;
    private FloatingActionButton deleteFab, saveFab;
    private DatabaseManager databaseManager;
    private Task task;
    private int id;
    private Intent intent;
    private TextInputEditText spinnerEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_manage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // مقداردهی اولیه المان‌ها
        titleInput = findViewById(R.id.titleInput);
        dateInput = findViewById(R.id.dateInput);
        showPicker = findViewById(R.id.showPicker);
        imageBack = findViewById(R.id.imageBack);
        deleteFab = findViewById(R.id.deleteFab);
        saveFab = findViewById(R.id.saveFab);
        deleteDate = findViewById(R.id.deleteDate);
        spinnerEditText = findViewById(R.id.spinnerEditText);
        deleteType = findViewById(R.id.deleteType);

        // تنظیمات مربوط به اسپینر
        ListPopupWindow listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, types));
        listPopupWindow.setAnchorView(spinnerEditText);
        listPopupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
        listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
        listPopupWindow.setModal(true);

        spinnerEditText.setOnClickListener(v -> listPopupWindow.show());

        listPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
            spinnerEditText.setText(types[position]);
            listPopupWindow.dismiss();
            deleteType.setVisibility(VISIBLE);
        });

        // دریافت اطلاعات ورودی و بررسی حالت جدید یا ویرایش
        databaseManager = new DatabaseManager(this);
        intent = getIntent();

        if (intent.getLongExtra("alarmDate", -1) != -1)
            currentDate.setTimeInMillis(intent.getLongExtra("alarmDate", -1));

        if (!intent.getBooleanExtra("isNewTask", true)) {
            // حالت ویرایش
            task = (Task) intent.getSerializableExtra("task");
            if (task == null) {
                Toast.makeText(this, "تسکی برای ویرایش پیدا نشد.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            titleInput.setText(task.getTitle());
            spinnerEditText.setText(task.getType());

            if (!task.getType().isEmpty())
                deleteType.setVisibility(VISIBLE);

            if (task.hasAlert()) {
                currentDate.setTimeInMillis(task.getAlertDate());
                dateInput.setText(sdf.format(currentDate.getTime()));
                deleteDate.setVisibility(VISIBLE);
                if (task.isAlertInPast()) dateInput.setTextColor(Color.RED);
            }
        } else {
            // حالت ساخت تسک جدید
            deleteFab.setVisibility(GONE);

            String type = intent.getStringExtra("type");
            if (type != null && !type.isEmpty()) {
                spinnerEditText.setText(type);
                deleteType.setVisibility(VISIBLE);
            }

            long alarmDateMillis = intent.getLongExtra("alarmDate", -1);
            if (alarmDateMillis != -1) {
                currentDate.setTimeInMillis(alarmDateMillis);
                dateInput.setText(sdf.format(currentDate.getTime()));
                deleteDate.setVisibility(VISIBLE);

                if (currentDate.before(Calendar.getInstance())) {
                    dateInput.setTextColor(Color.RED);
                } else {
                    dateInput.setTextColor(Color.BLACK);
                }
            }
        }

        // نمایش دیالوگ انتخاب تاریخ و زمان
        showPicker.setOnClickListener(view -> showDateTimePicker());
        dateInput.setOnClickListener(view -> showDateTimePicker());

        // دکمه بازگشت
        imageBack.setOnClickListener(view -> finish());

        // تنظیم کلیک روی دکمه ذخیره (ساخت یا ویرایش تسک)
        saveFab.setOnClickListener(view -> {
            String title = titleInput.getText().toString().trim();
            String type = spinnerEditText.getText() != null ? spinnerEditText.getText().toString().trim() : "";

            if (title.isEmpty()) {
                Toast.makeText(this, "فعالیتی بنویسید.", Toast.LENGTH_SHORT).show();
                return;
            }

            Task taskToSave = new Task();
            taskToSave.setTitle(title);
            taskToSave.setType(type);

            if (!dateInput.getText().toString().isEmpty()) {
                taskToSave.setAlertDate(currentDate.getTimeInMillis());
            }

            boolean isNewTask = intent.getBooleanExtra("isNewTask", true);

            if (isNewTask) {
                // ایجاد تسک جدید
                id = databaseManager.insertTask(taskToSave);
                taskToSave.setId(id);
            } else {
                // بروزرسانی تسک موجود
                taskToSave.setId(task.getId());

                // جلوگیری از ذخیره در صورت عدم تغییر
                if (Objects.equals(taskToSave.getTitle(), task.getTitle()) &&
                        Objects.equals(taskToSave.getType(), task.getType()) &&
                        taskToSave.hasAlert() == task.hasAlert() &&
                        (!taskToSave.hasAlert() || taskToSave.getAlertDate() == task.getAlertDate())) {
                    finish();
                    return;
                }

                databaseManager.updateTask(taskToSave);
            }

            // تنظیم آلارم (در صورت وجود و نبودن در گذشته)
            if (taskToSave.hasAlert() && !taskToSave.isAlertInPast()) {
                setAlarm(taskToSave.getId(), taskToSave.getAlertDate(), "\"" + taskToSave.getTitle() + "\"" + " رو انجام دادی؟");
            }

            finish();
        });

        // حذف تسک
        deleteFab.setOnClickListener(view -> {
            if (task != null) deleteTask(this, task);
        });

        // حذف تاریخ انتخاب ‌شده
        deleteDate.setOnClickListener(view1 -> {
            dateInput.setText(null);
            deleteDate.setVisibility(GONE);
            dateInput.setTextColor(Color.BLACK);
        });

        // حذف دسته بندی انتخاب ‌شده
        deleteType.setOnClickListener(view -> {
            spinnerEditText.setText(null);
            deleteType.setVisibility(GONE);
        });
    }

    // نمایش دیالوگ انتخاب تاریخ و ساعت
    private void showDateTimePicker() {
        Calendar tempCalendar = (Calendar) currentDate.clone();

        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            tempCalendar.set(Calendar.YEAR, year);
            tempCalendar.set(Calendar.MONTH, month);
            tempCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                tempCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                tempCalendar.set(Calendar.MINUTE, minute);
                tempCalendar.set(Calendar.SECOND, 0);
                tempCalendar.set(Calendar.MILLISECOND, 0);

                currentDate = (Calendar) tempCalendar.clone();

                dateInput.setText(sdf.format(currentDate.getTime()));
                deleteDate.setVisibility(VISIBLE);

                if (currentDate.before(Calendar.getInstance())) {
                    dateInput.setTextColor(Color.RED);
                } else {
                    dateInput.setTextColor(Color.BLACK);
                }

            }, tempCalendar.get(Calendar.HOUR_OF_DAY), tempCalendar.get(Calendar.MINUTE), true).show();

        }, tempCalendar.get(Calendar.YEAR), tempCalendar.get(Calendar.MONTH), tempCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    // تنظیم آلارم دقیق با بررسی مجوزها
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    private void setAlarm(int taskId, long timeInMillis, String message) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        // بررسی اجازه دقیق آلارم در اندروید ۱۲+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(this, "اجازه‌ی آلارم داده نشده. لطفاً از تنظیمات فعال کنید.", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            return;
        }

        Intent intent2 = new Intent(this, AlarmReceiver.class);
        intent2.putExtra("alarm_message", message);
        intent2.putExtra("alarm_id", taskId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                taskId,
                intent2,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.cancel(pendingIntent); // حذف آلارم قبلی

        try {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
            );
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "خطا در تنظیم آلارم. مجوز لازم داده نشده.", Toast.LENGTH_SHORT).show();
        }
    }

    // حذف تسک به همراه نمایش دیالوگ تأیید
    public void deleteTask(Activity activity, Task task) {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_layout_2);

        MaterialButton dismissButton = dialog.findViewById(R.id.dismissButton);
        MaterialButton deleteButton = dialog.findViewById(R.id.deleteButton);

        deleteButton.setOnClickListener(view -> {
            AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
            Intent intent2 = new Intent(activity, AlarmReceiver.class);
            intent2.putExtra("alarm_id", task.getId());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    activity,
                    task.getId(),
                    intent2,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (alarmManager != null) alarmManager.cancel(pendingIntent);

            new DatabaseManager(activity).deleteTask(task);
            dialog.dismiss();

            if (activity instanceof TaskManageActivity) {
                activity.finish();
            }
        });

        dismissButton.setOnClickListener(view -> dialog.dismiss());

        dialog.setOnDismissListener(dialogInterface -> {
            if (activity instanceof TaskManageActivity) {
                activity.finish();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}