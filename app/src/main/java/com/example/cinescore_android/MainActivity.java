package com.example.cinescore_android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
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

    // Pagination Variables
    private int currentPage = 1;
    private String currentCategory = "top-rated"; // Default category
    private TextView tvPageIndicator;

    // Base URL components
    private static final String API_BASE = "https://cinescore-webapp-arhuerfndwewhte9.germanywestcentral-01.azurewebsites.net/api/v1/tmdb/";
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
        Button btnPrev = findViewById(R.id.btnPrev); // You'll need to add these to XML
        Button btnNext = findViewById(R.id.btnNext);
        tvPageIndicator = findViewById(R.id.tvPageIndicator);

        // 2. Initialize Data Components
        movieList = new ArrayList<>();
        adapter = new MovieAdapter(this, movieList);
        recyclerView.setAdapter(adapter);
        queue = Volley.newRequestQueue(this);

        // 3. Initial Load
        fetchData();

        // 4. Category Listeners
        btnAll.setOnClickListener(v -> {
            currentCategory = "top-rated";
            currentPage = 1;
            fetchData();
        });

        btnTrending.setOnClickListener(v -> {
            currentCategory = "popular";
            currentPage = 1;
            fetchData();
        });

        // 5. Pagination Listeners
        btnNext.setOnClickListener(v -> {
            currentPage++;
            fetchData();
        });

        btnPrev.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                fetchData();
            } else {
                Toast.makeText(this, "You are on the first page", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchData() {
        // Construct the URL dynamically based on category and page
        String targetUrl = API_BASE + currentCategory + "?page=" + currentPage;

        // Update UI indicator
        tvPageIndicator.setText("Page: " + currentPage);

        movieList.clear();
        adapter.notifyDataSetChanged();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                targetUrl,
                null,
                response -> {
                    try {
                        JSONArray moviesArray = response.getJSONArray("movies");
                        for (int i = 0; i < moviesArray.length(); i++) {
                            JSONObject movieObj = moviesArray.getJSONObject(i);
                            movieList.add(new Movie(
                                    movieObj.optInt("id", -1),
                                    movieObj.optString("title", "Unknown"),
                                    movieObj.optString("genre", "N/A"),
                                    movieObj.optInt("year", 0),
                                    movieObj.optString("description", ""),
                                    movieObj.optString("posterUrl", "")
                            ));
                        }
                        adapter.notifyDataSetChanged();
                        // Scroll back to top after page change
                        recyclerView.scrollToPosition(0);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("ApiKey", API_KEY);
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }
}