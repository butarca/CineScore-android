package com.example.cinescore_android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest; // Changed to JsonObjectRequest
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private List<Movie> movieList;
    private RequestQueue queue;

    // Endpoints
    private static final String BASE_URL = "https://cinescore-webapp-arhuerfndwewhte9.germanywestcentral-01.azurewebsites.net/api/v1/tmdb/top-rated?page=1";
    private static final String TRENDING_URL = "https://cinescore-webapp-arhuerfndwewhte9.germanywestcentral-01.azurewebsites.net/api/v1/tmdb/popular?page=1";
    private static final String API_KEY = "ChangeMeApiKey123!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialize UI
        recyclerView = findViewById(R.id.recyclerViewMovies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button btnAll = findViewById(R.id.btnAllMovies);
        Button btnTrending = findViewById(R.id.btnTrending);

        // 2. Initialize Data Components
        movieList = new ArrayList<>();
        adapter = new MovieAdapter(this, movieList);
        recyclerView.setAdapter(adapter);

        queue = Volley.newRequestQueue(this);

        // 3. Initial Load
        fetchData(BASE_URL);

        // 4. Button Listeners
        btnAll.setOnClickListener(v -> fetchData(BASE_URL));
        btnTrending.setOnClickListener(v -> fetchData(TRENDING_URL));
    }

    private void fetchData(String targetUrl) {
        // Clear list and refresh UI immediately to show a "loading" state (blank screen)
        movieList.clear();
        adapter.notifyDataSetChanged();

        // Use JsonObjectRequest because the JSON root is now { "movies": [...] }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                targetUrl,
                null,
                response -> {
                    try {
                        // Access the "movies" array inside the root object
                        JSONArray moviesArray = response.getJSONArray("movies");

                        for (int i = 0; i < moviesArray.length(); i++) {
                            JSONObject movieObj = moviesArray.getJSONObject(i);

                            // Parse individual movie data
                            // Using optString/optInt is safer if fields are sometimes missing
                            int id = movieObj.optInt("id", -1);
                            String title = movieObj.optString("title", "Unknown Title");
                            String genre = movieObj.optString("genre", "N/A");
                            int year = movieObj.optInt("year", 0);
                            String description = movieObj.optString("description", "No description available.");
                            String posterUrl = movieObj.optString("posterUrl", "");

                            // Add to list (Ensure your Movie constructor accepts 'id' now)
                            movieList.add(new Movie(id, title, genre, year, description, posterUrl));
                        }

                        // Notify the adapter that data has changed
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Data Parsing Error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(MainActivity.this, "Network Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("ApiKey", API_KEY);
                return headers;
            }
        };

        // Add to Volley Queue
        queue.add(jsonObjectRequest);
    }
}