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

    public MovieAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the "item_movie.xml" you created earlier
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        holder.title.setText(movie.getTitle());
        holder.yearGenre.setText(movie.getYear() + " • " + movie.getGenre());
        holder.description.setText(movie.getDescription());

        // Use Glide to load the image URL into the ImageView
        Glide.with(context)
                .load(movie.getPosterUrl())
                .placeholder(android.R.drawable.ic_menu_gallery) // Default icon if loading fails
                .into(holder.poster);
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
