package com.example.javamusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Menu;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private static final String USER_PREFS = "UserPrefs";
    private static final String IS_LOGGED_IN_KEY = "isLoggedIn";
    private static final String KEY_USER_NAMES = "usernames";
    private static final String KEY_LOGGED_IN_USER = "loggedInUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();
        setupViewPagerAndTabLayout();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        String loggedInUserEmail = sharedPreferences.getString(KEY_LOGGED_IN_USER, "");
        String userNamesJson = sharedPreferences.getString(KEY_USER_NAMES, "");
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, String>>() {}.getType();
        Map<String, String> userNames = gson.fromJson(userNamesJson, type);

        if (userNames != null && userNames.containsKey(loggedInUserEmail)) {
            String userName = userNames.get(loggedInUserEmail);
            if (userName != null && !userName.isEmpty()) {
                getSupportActionBar().setTitle(userName); // Set user name as the toolbar title
            }
        }
    }

    private void setupViewPagerAndTabLayout() {
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new AllSongsFragment();
                    case 1:
                        return new FavoriteSongsFragment();
                    default:
                        return new AllSongsFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("All Songs");
                            tab.setContentDescription("All Songs Tab");
                            break;
                        case 1:
                            tab.setText("Favorites");
                            tab.setContentDescription("Favorites Tab");
                            break;
                    }
                }).attach();


        tabLayout.setTabTextColors(
                ContextCompat.getColor(this, R.color.white), // Normal text color
                ContextCompat.getColor(this, R.color.textcolor) // Selected text color
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sign_out) {
            signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_LOGGED_IN_KEY, false);
        editor.putString(KEY_LOGGED_IN_USER, ""); // Clear logged-in user email
        editor.apply();

        Intent intent = new Intent(MainActivity.this, LoginPage.class);
        startActivity(intent);
        finish();
    }
}

