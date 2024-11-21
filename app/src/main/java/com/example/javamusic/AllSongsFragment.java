// AllSongsFragment.java
package com.example.javamusic;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class AllSongsFragment extends Fragment {
    private static final int REQUEST_CODE = 123;

    RecyclerView recyclerView;
    TextView noMusicTextView;
    ArrayList<AudioModel> songsList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_songs, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        noMusicTextView = view.findViewById(R.id.no_songs_text);

        if (!checkPermission()) {
            requestPermission();
        } else {
            loadMusicFiles();
        }

        return view;
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            int result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_AUDIO);
            return result == PackageManager.PERMISSION_GRANTED;
        } else {
            int result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_MEDIA_AUDIO}, REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadMusicFiles();
            } else {
                Toast.makeText(requireContext(), "Permission denied. Cannot load music files.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onPermissionResult(boolean granted) {
        if (granted) {
            loadMusicFiles();
        } else {
            Toast.makeText(requireContext(), "Permission denied. Cannot load music files.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMusicFiles() {
        new Thread(() -> {
            String[] projection = {
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DURATION
            };

            String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

            try (Cursor cursor = requireActivity().getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null,
                    null)) {

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        AudioModel songData = new AudioModel(
                                cursor.getString(1),
                                cursor.getString(0),
                                cursor.getString(2)
                        );
                        if (new File(songData.getPath()).exists()) {
                            songsList.add(songData);
                        }
                    } while (cursor.moveToNext());
                }
            }

            requireActivity().runOnUiThread(() -> {
                if (songsList.isEmpty()) {
                    noMusicTextView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    recyclerView.setAdapter(new MusicListAdapter(songsList, requireContext()));
                }
            });
        }).start();
    }


}

