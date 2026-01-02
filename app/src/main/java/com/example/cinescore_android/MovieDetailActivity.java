package com.example.cinescore_android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class MovieDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvGenre, tvYear, tvDescription;
    private RequestQueue queue;

    // Base URL for single movie details
    private static final String API_URL = "https://cinescore-webapp-arhuerfndwewhte9.germanywestcentral-01.azurewebsites.net/api/v1/movies/";
    private static final String API_KEY = "ChangeMeApiKey123!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish(); // This simply closes the current page
        });

        // 1. Initialize UI
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvGenre = findViewById(R.id.tvDetailGenre);
        tvYear = findViewById(R.id.tvDetailYear);
        tvDescription = findViewById(R.id.tvDetailDescription);

        queue = Volley.newRequestQueue(this);

        // 2. Get the Movie ID passed from MainActivity
        int movieId = getIntent().getIntExtra("MOVIE_ID", -1);

        if (movieId != -1) {
            fetchMovieDetails(movieId);
        } else {
            Toast.makeText(this, "Error: Invalid Movie ID", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if no ID
        }
    }

    private void fetchMovieDetails(int id) {
        String url = API_URL + id;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        // Assuming the API returns the movie object directly
                        // Adjust these keys based on your exact API response for a single movie
                        String title = response.optString("title", "Unknown Title");
                        String genre = response.optString("genre", "N/A");
                        int year = response.optInt("year", 0);
                        String desc = response.optString("description", "No description available.");

                        tvTitle.setText(title);
                        tvGenre.setText("Genre: " + genre);
                        tvYear.setText("Year: " + year);
                        tvDescription.setText(desc);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parsing Error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("ApiKey", API_KEY);
                return headers;
            }
        };

        queue.add(request);
    }
}
