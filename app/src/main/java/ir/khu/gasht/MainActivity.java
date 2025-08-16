package ir.khu.gasht;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import ir.khu.gasht.adapters.TaskAdapter;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton addItemButton;
    private MaterialButton tab1, tab2, tab3, tab4, tab5;
    private RecyclerView taskList;
    private DatabaseManager databaseManager;
    private TaskAdapter adapter;
    private LinearLayout emptyTask;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private LinearLayout menuButton;
    private LinearLayout icon1, icon2, icon4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // مقداردهی اولیه المان‌ها
        addItemButton = findViewById(R.id.fab1);
        taskList = findViewById(R.id.taskList);
        emptyTask = findViewById(R.id.emptyTask);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        menuButton = findViewById(R.id.menu);

        tab1 = findViewById(R.id.tab1);
        tab2 = findViewById(R.id.tab2);
        tab3 = findViewById(R.id.tab3);
        tab4 = findViewById(R.id.tab4);
        tab5 = findViewById(R.id.tab5);

        icon1 = findViewById(R.id.icon1);
        icon2 = findViewById(R.id.icon2);
        icon4 = findViewById(R.id.icon4);

        databaseManager = new DatabaseManager(this);

        setupRecyclerView();
        setupDrawer();
        setupTabs();
        setupBottomNavItems();

        // تنظیم کلیک روی دکمه افزودن آیتم
        addItemButton.setOnClickListener(view -> {

            // ارسال تیپ و جالت فعالیت به صفحه مدیریت قغالیت ها
            Intent intent = new Intent(this, TaskManageActivity.class);
            intent.putExtra("isNewTask", true);
            startActivity(intent);
        });
    }

    private void setupDrawer() {

        // تنظیم کلیک روی دکمه منو
        menuButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.END));

        // تنظیم کلیک روی آیتم های منو
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_settings)
                startActivity(new Intent(this, SettingActivity.class));

            // بستن منو
            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });

    }

    // تنظیم کلیک روی آیتم های باتم نویگیشن
    private void setupBottomNavItems() {
        icon1.setOnClickListener(view -> startActivity(new Intent(this, UserActivity.class)));
        icon2.setOnClickListener(view -> startActivity(new Intent(this, MapActivity.class)));
        icon4.setOnClickListener(view -> startActivity(new Intent(this, CalenderActivity.class)));
    }

    // تنظیم اطلاعات لیست
    private void setupRecyclerView() {
        taskList.setLayoutManager(new LinearLayoutManager(this));
        loadList();
    }

    // بارگذاری اطلاعات لیست
    private void loadList() {
        adapter = new TaskAdapter(this, databaseManager.getAllTasks());
        taskList.setAdapter(adapter);

        // بررسی خالی بودن لیست
        checkIsEmptyList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.setTasks(databaseManager.getAllTasks());
            adapter.notifyDataSetChanged();
        } else {
            loadList();
        }
        checkIsEmptyList();
    }

    private void checkIsEmptyList() {
        if (adapter.getItemCount() == 0) {
            taskList.setVisibility(GONE);
            emptyTask.setVisibility(VISIBLE);
        } else {
            taskList.setVisibility(VISIBLE);
            emptyTask.setVisibility(GONE);
        }
    }

    // تنظیم کلیک روی تب
    private void setupTabs() {
        tab2.setOnClickListener(view -> {
            startActivity(new Intent(this, IDcardActivity.class));
            finish();
        });
        tab3.setOnClickListener(view -> {
            startActivity(new Intent(this, ClothesActivity.class));
            finish();
        });
        tab4.setOnClickListener(view -> {
            startActivity(new Intent(this, ToolsActivity.class));
            finish();
        });
        tab5.setOnClickListener(view -> {
            startActivity(new Intent(this, HealthActivity.class));
            finish();
        });
    }
}