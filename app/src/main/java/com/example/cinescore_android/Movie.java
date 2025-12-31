package com.example.cinescore_android;

public class Movie {
    private int id; // Added ID
    private String title;
    private String genre;
    private int year;
    private String description;
    private String posterUrl;

    public Movie(int id, String title, String genre, int year, String description, String posterUrl) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.description = description;
        this.posterUrl = posterUrl;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public int getYear() { return year; }
    public String getDescription() { return description; }
    public String getPosterUrl() { return posterUrl; }
}
