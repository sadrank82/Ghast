package ir.khu.gasht;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

import ir.khu.gasht.adapters.TaskAdapter;

public class CalenderActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView itemList;
    private TaskAdapter adapter;
    private DatabaseManager databaseManager;
    private long startOfDay, endOfDay;
    private LinearLayout emptyTask;
    private FloatingActionButton addItemButton;
    private Calendar startCal = Calendar.getInstance();
    private Calendar endCal = Calendar.getInstance();
    private ImageView imageBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calender);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        calendarView = findViewById(R.id.calendarView);
        itemList = findViewById(R.id.itemList);
        emptyTask = findViewById(R.id.emptyTask);
        addItemButton = findViewById(R.id.addItemButton);
        imageBack = findViewById(R.id.imageBack);

        itemList.setLayoutManager(new LinearLayoutManager(this));

        databaseManager = new DatabaseManager(this);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {

            // ساخت شی Calendar برای شروع روز
            startCal.set(year, month, dayOfMonth, 0, 0, 0);
            startCal.set(Calendar.MILLISECOND, 0);
            startOfDay = startCal.getTimeInMillis();

            // ساخت شی Calendar برای پایان روز
            endCal.set(year, month, dayOfMonth, 23, 59, 59);
            endCal.set(Calendar.MILLISECOND, 999);
            endOfDay = endCal.getTimeInMillis();

            loadList();
        });

        addItemButton.setOnClickListener(view -> {
            // ارسال حالت فعالیت به صفحه مدیریت فعالیت‌ها
            Intent intent = new Intent(this, TaskManageActivity.class);
            intent.putExtra("isNewTask", true);

            Calendar calendar = (Calendar) startCal.clone();
            calendar.add(Calendar.HOUR_OF_DAY, 12);
            intent.putExtra("alarmDate", calendar.getTimeInMillis());

            startActivity(intent);
        });

        // دکمه بازگشت
        imageBack.setOnClickListener(view -> finish());

        // مقداردهی اولیه به startOfDay و endOfDay با تاریخ امروز
        Calendar today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH);
        int day = today.get(Calendar.DAY_OF_MONTH);

        // تنظیم calendarView روی تاریخ امروز
        calendarView.setDate(today.getTimeInMillis(), false, true);

        // تنظیم startCal و endCal با تاریخ امروز
        startCal.set(year, month, day, 0, 0, 0);
        startCal.set(Calendar.MILLISECOND, 0);
        startOfDay = startCal.getTimeInMillis();

        endCal.set(year, month, day, 23, 59, 59);
        endCal.set(Calendar.MILLISECOND, 999);
        endOfDay = endCal.getTimeInMillis();

        loadList();
    }

    private void loadList() {
        adapter = new TaskAdapter(this, databaseManager.searchTasksByDate(startOfDay, endOfDay));
        itemList.setAdapter(adapter);
        checkIsEmptyList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.setTasks(databaseManager.searchTasksByDate(startOfDay, endOfDay));
            adapter.notifyDataSetChanged();
        } else {
            loadList();
        }
        checkIsEmptyList();
    }

    private void checkIsEmptyList() {
        if (adapter.getItemCount() == 0) {
            emptyTask.setAlpha(0f);
            emptyTask.setVisibility(VISIBLE);
            emptyTask.animate().alpha(1f).setDuration(300).start();
            itemList.setVisibility(GONE);
        } else {
            itemList.setVisibility(VISIBLE);
            emptyTask.setVisibility(GONE);
        }
    }
}