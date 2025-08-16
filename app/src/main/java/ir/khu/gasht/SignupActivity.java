package ir.khu.gasht;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class SignupActivity extends AppCompatActivity {

    private EditText nameField, emailField, passwordField;
    private MaterialButton signupButton;
    private TextView withAccountText;
    private ImageView imageBack;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // مقداردهی اولیه المان‌ها
        nameField = findViewById(R.id.FName_input);
        emailField = findViewById(R.id.emailInput);
        passwordField = findViewById(R.id.passwordInput);
        signupButton = findViewById(R.id.SignUpButton);
        imageBack = findViewById(R.id.imageBack);
        withAccountText = findViewById(R.id.WithAccountText);

        // دکمه بازگشت
        imageBack.setOnClickListener(v -> finish());

        // دکمه "قبلاً حساب دارید؟"
        withAccountText.setOnClickListener(v -> finish());

        // دکمه ثبت‌نام
        signupButton.setOnClickListener(v -> {
            String name = nameField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignupActivity.this, "لطفاً تمامی فیلدها را پر کنید!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SignupActivity.this, "ثبت‌نام موفقیت‌آمیز بود!", Toast.LENGTH_SHORT).show();
                // اینجا می‌توان فرآیند ثبت‌نام واقعی را اضافه کرد.

                // ذخیره اطلاعات وارد شده
                getSharedPreferences("userData", MODE_PRIVATE).edit()
                        .putString("name", name)
                        .putString("email", email)
                        .putString("password", password)
                        .putBoolean("isRegistered", true)
                        .apply();

                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });
    }
}