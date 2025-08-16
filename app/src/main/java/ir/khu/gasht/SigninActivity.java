package ir.khu.gasht;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class SigninActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private MaterialButton signInButton;
    private TextView noAccountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin); // اتصال به فایل XML

        // مقداردهی اولیه المان‌ها
        emailField = findViewById(R.id.gmail);
        passwordField = findViewById(R.id.pass);
        signInButton = findViewById(R.id.SignInButton);
        noAccountText = findViewById(R.id.noaccount);

        SharedPreferences sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);

        // بررسی "آیا کاربر ثبت نام کرده؟"
       // if (sharedPreferences.getBoolean("isRegistered", false)) {
         //   startActivity(new Intent(this, MainActivity.class));
            //finish();
        //}

        // تنظیم کلیک روی دکمه ورود
        signInButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SigninActivity.this, "لطفا تمامی فیلدها را پر کنید!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SigninActivity.this, "ورود با موفقیت انجام شد!", Toast.LENGTH_SHORT).show();
                // در اینجا می‌توان ورود به حساب کاربری را مدیریت کرد.

                // ذخیره اطلاعات وارد شده
                sharedPreferences.edit()
                        .putString("email", email)
                        .putString("password", password)
                        .putBoolean("isRegistered", true)
                        .apply();

                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        // تنظیم کلیک روی متن "آیا اکانت ندارید؟"
        noAccountText.setOnClickListener(v -> {
            // می‌توان کاربر را به صفحه ثبت نام هدایت کرد.
            startActivity(new Intent(this, SignupActivity.class));
        });
    }
}