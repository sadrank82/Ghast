package ir.khu.gasht;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class SettingActivity extends AppCompatActivity {

    private MaterialCardView redButton, blueButton, greenButton, defaultButton;
    private TextView usernameView, passwordView;
    private MaterialButton editButton;
    private String username, password;
    private ImageView imageBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // مقداردهی اولیه المان‌ها
        redButton = findViewById(R.id.redButton);
        blueButton = findViewById(R.id.blueButton);
        greenButton = findViewById(R.id.greenButton);
        defaultButton = findViewById(R.id.defaultButton);

        usernameView = findViewById(R.id.username);
        passwordView = findViewById(R.id.password);

        editButton = findViewById(R.id.editButton);
        imageBack = findViewById(R.id.imageBack);

        // دریافت اطلاعات و جایگذاری آنها
        SharedPreferences sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
        username = sharedPreferences.getString("name", "");
        password = sharedPreferences.getString("password", "");
        usernameView.setText(username);
        passwordView.setText(password);

        // تنظیم کلیک روی دکمه ویرایش
        editButton.setOnClickListener(view -> editAccount());

        // دکمه بازگشت
        imageBack.setOnClickListener(view -> finish());

        // تنظیم کلیک روی دکمه های تم
        redButton.setOnClickListener(view -> {
            ThemeUtils.saveTheme(this, ThemeUtils.THEME_RED);
            ThemeUtils.restartApp(this);
        });
        blueButton.setOnClickListener(view -> {
            ThemeUtils.saveTheme(this, ThemeUtils.THEME_BLUE);
            ThemeUtils.restartApp(this);
        });
        greenButton.setOnClickListener(view -> {
            ThemeUtils.saveTheme(this, ThemeUtils.THEME_GREEN);
            ThemeUtils.restartApp(this);
        });
        defaultButton.setOnClickListener(view -> {
            ThemeUtils.saveTheme(this, ThemeUtils.THEME_DEFAULT);
            ThemeUtils.restartApp(this);
        });
    }

    private void editAccount() {
        // نمایش دیالوگ
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_layout_3);

        //مقدار دهی اجزای دیالوگ
        EditText usernameInput = dialog.findViewById(R.id.usernameInput);
        EditText passwordInput = dialog.findViewById(R.id.passwordInput);
        MaterialButton dismissButton = dialog.findViewById(R.id.dismissButton);
        MaterialButton editButton = dialog.findViewById(R.id.editButton);

        // چایگذاری اطلاعات
        usernameInput.setText(username);
        passwordInput.setText(password);

        // تنظیم کلیک روی دکمه ویرایش
        editButton.setOnClickListener(view -> {

            // دریافت اطلاعات دیالوگ
            username = usernameInput.getText().toString().trim();
            password = passwordInput.getText().toString().trim();

            // ذخیره اطلاعات جدید حساب کاربری
            saveAccount(username, password);

            // جایگذاری اطلاعات جدید
            usernameView.setText(username);
            passwordView.setText(password);

            // لفو نمایش دیالوگ
            dialog.dismiss();
        });

        // تنظیم کلیک روی دکمه لغو
        dismissButton.setOnClickListener(view -> dialog.dismiss());

        // تنظیم پس زمینه شفاف صفحه و نمایش دیالوگ
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        // تنظیم عرض لایه دیالوگ
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    // ذخیره اطلاعات حساب کاربری
    private void saveAccount(String username, String password) {
        SharedPreferences.Editor editor = getSharedPreferences("appData", MODE_PRIVATE).edit();
        editor.putString("name", username);
        editor.putString("password", password);
        editor.apply();
    }
}