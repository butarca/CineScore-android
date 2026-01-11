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

    private static final String API_URL = "https://cinescore-webapp-arhuerfndwewhte9.germanywestcentral-01.azurewebsites.net/api/v1/movies/";
    private static final String API_KEY = "ChangeMeApiKey123!";

    // Metoda se pokliče ob ustvarjanju aktivnosti; nastavi uporabniški vmesnik, poveže gumbe, inicializira vrsto za omrežne zahtevke in prebere ID filma iz namena (Intent).
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
        });

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvGenre = findViewById(R.id.tvDetailGenre);
        tvYear = findViewById(R.id.tvDetailYear);
        tvDescription = findViewById(R.id.tvDetailDescription);
        commentsContainer = findViewById(R.id.commentsContainer);

        queue = Volley.newRequestQueue(this);

        int movieId = getIntent().getIntExtra("MOVIE_ID", -1);

        if (movieId != -1) {
            fetchMovieDetails(movieId);
        } else {
            Toast.makeText(this, "Error: Invalid Movie ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // Metoda pridobi podrobne podatke o filmu in komentarje s strežnika, izračuna povprečno oceno ter dinamično doda poglede za komentarje v vsebnik na zaslonu.
    private void fetchMovieDetails(int id) {
        String url = API_URL + id;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        String title = response.optString("title", "Unknown Title");
                        String genre = response.optString("genre", "N/A");
                        int year = response.optInt("year", 0);
                        String desc = response.optString("description", "No description available.");

                        tvTitle.setText(title);
                        tvGenre.setText("Genre: " + genre);
                        tvYear.setText("Year: " + year);
                        tvDescription.setText(desc);

                        LinearLayout commentsContainer = findViewById(R.id.commentsContainer);
                        TextView tvAverageScore = findViewById(R.id.tvAverageScore);
                        TextView tvReviewCount = findViewById(R.id.tvReviewCount);

                        commentsContainer.removeAllViews();

                        JSONArray commentsArray = response.optJSONArray("comments");

                        if (commentsArray != null && commentsArray.length() > 0) {
                            android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);
                            double totalScore = 0;
                            int count = commentsArray.length();

                            for (int i = 0; i < count; i++) {
                                JSONObject commentObj = commentsArray.getJSONObject(i);
                                android.view.View commentView = inflater.inflate(R.layout.item_comment, commentsContainer, false);

                                TextView tvUser = commentView.findViewById(R.id.tvCommentUser);
                                TextView tvText = commentView.findViewById(R.id.tvCommentText);
                                TextView tvDate = commentView.findViewById(R.id.tvCommentDate);
                                TextView tvRating = commentView.findViewById(R.id.tvCommentRating);

                                String content = commentObj.optString("text", "");
                                String date = commentObj.optString("createdAt", "").split("T")[0];
                                int ratingValue = commentObj.optInt("rating", 0);

                                totalScore += ratingValue;

                                String userName = "Anonymous";
                                if (commentObj.has("user") && !commentObj.isNull("user")) {
                                    userName = commentObj.getJSONObject("user").optString("userName", "Anonymous");
                                }

                                tvUser.setText(userName);
                                tvText.setText(content);
                                tvDate.setText(date);
                                tvRating.setText("⭐ " + ratingValue + "/5");

                                commentsContainer.addView(commentView);
                            }

                            double average = totalScore / count;
                            tvAverageScore.setText(String.format("%.1f", average));
                            tvReviewCount.setText("Based on " + count + " reviews");

                        } else {
                            tvAverageScore.setText("N/A");
                            tvReviewCount.setText("No reviews yet");

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
            // Metoda pregazi privzete glave zahtevka in doda avtentikacijski API ključ, ki je potreben za dostop do zalednega sistema.
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