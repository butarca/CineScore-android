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
    private OnMovieClickListener listener;

    // Konstruktor razreda: Inicializira adapter s potrebnim kontekstom, seznamom podatkov o filmih in poslušalcem za klike.
    public MovieAdapter(Context context, List<Movie> movieList, OnMovieClickListener listener) {
        this.context = context;
        this.movieList = movieList;
        this.listener = listener;
    }

    // Vmesnik (Interface), ki definira metodo, ki se bo poklicala, ko uporabnik klikne na določen film v seznamu.
    public interface OnMovieClickListener {
        void onMovieClick(int movieId);
    }

    // Ta metoda ustvari nov "pogled" (View) za posamezno vrstico v seznamu tako, da "napihne" (inflate) XML postavitev (item_movie.xml).
    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    // Ta metoda poveže podatke posameznega filma z UI elementi v vrstici (nastavi naslov, opis in naloži sliko s knjižnico Glide) ter nastavi odziv na klik.
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

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMovieClick(movie.getId());
            }
        });
    }

    // Vrne skupno število filmov v seznamu, kar pove RecyclerView-ju, koliko vrstic mora izrisati.
    @Override
    public int getItemCount() {
        return movieList.size();
    }

    // Notranji razred, ki hrani reference na UI komponente (TextView, ImageView) posamezne vrstice, da jih ni treba vedno znova iskati (caching).
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