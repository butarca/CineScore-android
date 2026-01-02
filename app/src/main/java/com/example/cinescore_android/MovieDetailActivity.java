package com.example.cinescore_android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MovieDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvGenre, tvYear, tvDescription;
    private RequestQueue queue;

    private LinearLayout commentsContainer;

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
        commentsContainer = findViewById(R.id.commentsContainer);

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
                        // 1. Parse standard movie details
                        String title = response.optString("title", "Unknown Title");
                        String genre = response.optString("genre", "N/A");
                        int year = response.optInt("year", 0);
                        String desc = response.optString("description", "No description available.");

                        tvTitle.setText(title);
                        tvGenre.setText("Genre: " + genre);
                        tvYear.setText("Year: " + year);
                        tvDescription.setText(desc);

                        // 2. Locate the comments container and clear it (prevents duplicates)
                        LinearLayout commentsContainer = findViewById(R.id.commentsContainer);
                        commentsContainer.removeAllViews();

                        // 3. Parse the comments array
                        JSONArray commentsArray = response.optJSONArray("comments");
                        commentsContainer.removeAllViews();

                        if (commentsArray != null && commentsArray.length() > 0) {
                            // Get the inflater service
                            android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);

                            for (int i = 0; i < commentsArray.length(); i++) {
                                JSONObject commentObj = commentsArray.getJSONObject(i);

                                // 1. Inflate the reusable comment layout
                                android.view.View commentView = inflater.inflate(R.layout.item_comment, commentsContainer, false);

                                // 2. Find the views inside that inflated layout
                                TextView tvUser = commentView.findViewById(R.id.tvCommentUser);
                                TextView tvText = commentView.findViewById(R.id.tvCommentText);
                                TextView tvDate = commentView.findViewById(R.id.tvCommentDate);

                                // 3. Extract data from JSON
                                String content = commentObj.optString("text", "");
                                String date = commentObj.optString("createdAt", "").split("T")[0]; // Just get the date part

                                String userName = "Anonymous";
                                if (commentObj.has("user") && !commentObj.isNull("user")) {
                                    userName = commentObj.getJSONObject("user").optString("userName", "Anonymous");
                                }

                                // 4. Set the data
                                tvUser.setText(userName);
                                tvText.setText(content);
                                tvDate.setText(date);

                                // 5. Add the complete view to the container
                                commentsContainer.addView(commentView);
                            }
                        } else {
                            // Handle no comments case
                            TextView noComments = new TextView(this);
                            noComments.setText("No comments yet.");
                            commentsContainer.addView(noComments);
                        }

                    } catch (JSONException e) {
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
