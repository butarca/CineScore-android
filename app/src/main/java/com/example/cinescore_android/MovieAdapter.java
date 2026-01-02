package com.example.cinescore_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context context;
    private List<Movie> movieList;
    private OnMovieClickListener listener; // 1. Added listener variable

    // 2. Updated constructor to include the listener
    public MovieAdapter(Context context, List<Movie> movieList, OnMovieClickListener listener) {
        this.context = context;
        this.movieList = movieList;
        this.listener = listener;
    }

    // 3. Define the Interface
    public interface OnMovieClickListener {
        void onMovieClick(int movieId);
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        holder.title.setText(movie.getTitle());
        holder.yearGenre.setText(movie.getYear() + " • " + movie.getGenre());
        holder.description.setText(movie.getDescription());

        Glide.with(context)
                .load(movie.getPosterUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.poster);

        // 4. Set the click listener on the entire card (itemView)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMovieClick(movie.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView title, yearGenre, description;
        ImageView poster;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.movieTitle);
            yearGenre = itemView.findViewById(R.id.movieYearGenre);
            description = itemView.findViewById(R.id.movieDescription);
            poster = itemView.findViewById(R.id.moviePoster);
        }
    }
}