package com.example.javamusic;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Map;

public class FavoriteManager {
    private static FavoriteManager instance;
    private SharedPreferences sharedPreferences;

    private FavoriteManager(Context context) {
        sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
    }

    public static synchronized FavoriteManager getInstance(Context context) {
        if (instance == null) {
            instance = new FavoriteManager(context);
        }
        return instance;
    }

    public boolean isFavorite(AudioModel song) {
        return sharedPreferences.contains(song.getPath());
    }

    public void addFavorite(AudioModel song) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(song.getPath(), song.getTitle() + ";" + song.getDuration());
        editor.apply();
    }

    public void removeFavorite(AudioModel song) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(song.getPath());
        editor.apply();
    }

    public ArrayList<AudioModel> getFavoriteSongs() {
        ArrayList<AudioModel> favoriteSongs = new ArrayList<>();
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String[] songData = ((String) entry.getValue()).split(";");
            if (songData.length == 2) {
                String title = songData[0];
                String duration = songData[1];
                favoriteSongs.add(new AudioModel(entry.getKey(), title, duration));
            }
        }
        return favoriteSongs;
    }
}
