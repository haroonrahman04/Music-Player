package com.example.javamusic;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class LoginPage extends AppCompatActivity {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USERS = "users";

    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        EditText email = findViewById(R.id.t2);
        EditText password = findViewById(R.id.t3);
        Button login = findViewById(R.id.t4);
        TextView signup = findViewById(R.id.t5);

        // Toggle password visibility for password field
        password.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[2].getBounds().width())) {
                    passwordVisible = !passwordVisible;
                    if (passwordVisible) {
                        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0);
                    } else {
                        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_closed, 0);
                    }
                    password.setSelection(password.getText().length());
                    return true;
                }
            }
            return false;
        });

        login.setOnClickListener(v -> {
            String em = email.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (em.isEmpty()) {
                Toast.makeText(this, "Email Required", Toast.LENGTH_SHORT).show();
                return;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(em).matches()) {
                Toast.makeText(this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
                return;
            } else if (pass.isEmpty()) {
                Toast.makeText(this, "Password Required", Toast.LENGTH_SHORT).show();
                return;
            } else if (pass.length() < 8) {
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedPreferences.getString(KEY_USERS, null);
            Type type = new TypeToken<HashMap<String, String>>() {}.getType();
            Map<String, String> users = gson.fromJson(json, type);

            if (users != null && users.containsKey(em) && users.get(em).equals(pass)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.apply();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        });

        signup.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignupPage.class);
            startActivity(intent);
        });
    }
}
