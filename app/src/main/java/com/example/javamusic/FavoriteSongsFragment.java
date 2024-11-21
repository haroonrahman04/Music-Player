package com.example.javamusic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavoriteSongsFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<AudioModel> favoriteSongsList = new ArrayList<>();
    MusicListAdapter adapter;
    TextView noFavoritesText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_songs, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_favorites);
        noFavoritesText = view.findViewById(R.id.no_favorites_text);

        loadFavoriteSongs();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavoriteSongs();
    }

    private void loadFavoriteSongs() {
        FavoriteManager favoriteManager = FavoriteManager.getInstance(requireContext());
        favoriteSongsList = favoriteManager.getFavoriteSongs();

        if (favoriteSongsList.isEmpty()) {
            noFavoritesText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noFavoritesText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new MusicListAdapter(favoriteSongsList, requireContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerView.setAdapter(adapter);
        }
    }
}
