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

public class SignupPage extends AppCompatActivity {

    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USERS = "users";
    private static final String KEY_USER_NAMES = "usernames"; // Add this line
    private static final String KEY_LOGGED_IN_USER = "loggedInUser"; // Track logged-in user
    private static final String IS_LOGGED_IN_KEY = "isLoggedIn";

    private boolean passwordVisible = false;
    private boolean verifyPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        TextView sign_in = findViewById(R.id.t20);
        sign_in.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginPage.class);
            startActivity(intent);
        });

        EditText name = findViewById(R.id.t13);
        EditText email = findViewById(R.id.t15);
        EditText n_pass = findViewById(R.id.t17);
        EditText v_pass = findViewById(R.id.t19);
        Button sign_up = findViewById(R.id.sign_up_btn);

        // Toggle password visibility for new password field
        n_pass.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (n_pass.getRight() - n_pass.getCompoundDrawables()[2].getBounds().width())) {
                    passwordVisible = !passwordVisible;
                    if (passwordVisible) {
                        n_pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        n_pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0);
                    } else {
                        n_pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        n_pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_closed, 0);
                    }
                    n_pass.setSelection(n_pass.getText().length());
                    return true;
                }
            }
            return false;
        });

        // Toggle password visibility for verify password field
        v_pass.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (v_pass.getRight() - v_pass.getCompoundDrawables()[2].getBounds().width())) {
                    verifyPasswordVisible = !verifyPasswordVisible;
                    if (verifyPasswordVisible) {
                        v_pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        v_pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0);
                    } else {
                        v_pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        v_pass.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_closed, 0);
                    }
                    v_pass.setSelection(v_pass.getText().length());
                    return true;
                }
            }
            return false;
        });

        sign_up.setOnClickListener(v -> {
            String em = email.getText().toString().trim();
            String nm = name.getText().toString().trim();
            String np = n_pass.getText().toString().trim();
            String vp = v_pass.getText().toString().trim();

            if (nm.isEmpty()) {
                Toast.makeText(this, "Name Required", Toast.LENGTH_SHORT).show();
                return;
            } else if (em.isEmpty()) {
                Toast.makeText(this, "Email Required", Toast.LENGTH_SHORT).show();
                return;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(em).matches()) {
                Toast.makeText(this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
                return;
            } else if (np.isEmpty()) {
                Toast.makeText(this, "Password Required", Toast.LENGTH_SHORT).show();
                return;
            } else if (np.length() < 8) {
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                return;
            } else if (vp.isEmpty()) {
                Toast.makeText(this, "Password required", Toast.LENGTH_SHORT).show();
                return;
            } else if (vp.length() < 8) {
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!np.equals(vp)) {
                Toast.makeText(this, "Enter the same password", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedPreferences.getString(KEY_USERS, null);
            Type type = new TypeToken<HashMap<String, String>>() {}.getType();
            Map<String, String> users = gson.fromJson(json, type);

            String userNamesJson = sharedPreferences.getString(KEY_USER_NAMES, null);
            Map<String, String> userNames = gson.fromJson(userNamesJson, type);

            if (users == null) {
                users = new HashMap<>();
            }

            if (userNames == null) {
                userNames = new HashMap<>();
            }

            if (users.containsKey(em)) {
                Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            users.put(em, np);
            userNames.put(em, nm); // Store user name

            SharedPreferences.Editor editor = sharedPreferences.edit();
            json = gson.toJson(users);
            editor.putString(KEY_USERS, json);
            editor.putString(KEY_USER_NAMES, gson.toJson(userNames));
            editor.putBoolean(IS_LOGGED_IN_KEY, true);
            editor.putString(KEY_LOGGED_IN_USER, em); // Save logged-in user's email
            editor.apply();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
