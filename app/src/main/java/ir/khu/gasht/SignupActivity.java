package ir.khu.gasht;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class SignupActivity extends AppCompatActivity {

    private EditText nameField, emailField, passwordField;
    private MaterialButton signupButton;
    private TextView backButton, withAccountText;
    private ImageView imageBack;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
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
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // دکمه "قبلاً حساب دارید؟"
        withAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // دکمه ثبت‌نام
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameField.getText().toString().trim();
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "لطفاً تمامی فیلدها را پر کنید!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignupActivity.this, "ثبت‌نام موفقیت‌آمیز بود!", Toast.LENGTH_SHORT).show();
                    // اینجا می‌توان فرآیند ثبت‌نام واقعی را اضافه کرد.
                }
            }
        });
    }
}