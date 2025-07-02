package ir.khu.gasht;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.view.View;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin); // اتصال به فایل XML

        // مقداردهی اولیه المان‌ها
        emailField = findViewById(R.id.cardgmail);
        passwordField = findViewById(R.id.cardpass);
        signInButton = findViewById(R.id.SignInButton);
        noAccountText = findViewById(R.id.noaccount);

        // تنظیم کلیک روی دکمه ورود
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SigninActivity.this, "لطفا تمامی فیلدها را پر کنید!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SigninActivity.this, "ورود با موفقیت انجام شد!", Toast.LENGTH_SHORT).show();
                    // در اینجا می‌توان ورود به حساب کاربری را مدیریت کرد.
                }
            }
        });

        // تنظیم کلیک روی متن "آیا اکانت ندارید؟"
        noAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SigninActivity.this, "لطفا ثبت‌نام کنید!", Toast.LENGTH_SHORT).show();
                // می‌توان کاربر را به صفحه ثبت نام هدایت کرد.
            }
        });
    }
}