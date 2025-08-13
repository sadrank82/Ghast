package ir.khu.gasht;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserActivity extends AppCompatActivity {

    private TextView userName, email;
    private ImageView imageBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // مقداردهی اولیه المان‌ها
        userName = findViewById(R.id.userName);
        email = findViewById(R.id.email);
        imageBack = findViewById(R.id.imageBack);

        // دکمه بازگشت
        imageBack.setOnClickListener(view -> finish());

        // دریافت اطلاعات و جایگذاری آنها
        SharedPreferences sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
        userName.setText(sharedPreferences.getString("name", ""));
        email.setText(sharedPreferences.getString("email", ""));
    }
}