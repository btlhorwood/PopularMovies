package com.example.android.popularmovies;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 15/04/2016.
 */
public class MovieItem {

    private Integer id;
    private String posterPath;
    private String overview;
    private String releaseDate;
    private final List<Integer> genreList = new ArrayList<Integer>();
    private String originalTitle;
    private String originalLanguage;
    private String title;
    private String backdropPath;
    private Double popularity;
    private Integer voteCount;
    private Boolean video;
    private Boolean adult;
    private Double voteAverage;

    public MovieItem(JSONObject jsonObj) {
        try {
            id = jsonObj.getInt("id");
            posterPath = jsonObj.getString("poster_path");
            adult = jsonObj.getBoolean("adult");
            overview = jsonObj.getString("overview");
            releaseDate = jsonObj.getString("release_date");
            genreList.addAll(parseGenreIds(jsonObj.getJSONArray("genre_ids")));
            originalTitle = jsonObj.getString("original_title");
            originalLanguage = jsonObj.getString("original_language");
            title = jsonObj.getString("title");
            backdropPath = jsonObj.getString("backdrop_path");
            popularity = jsonObj.getDouble("popularity");
            voteCount = jsonObj.getInt("vote_count");
            video = jsonObj.getBoolean("video");
            voteAverage = jsonObj.getDouble("vote_average");

        } catch (JSONException e) {
            Log.e(getClass().getName(), "Error creating MovieItem", e);
        }
    }

    private List<Integer> parseGenreIds(JSONArray jsonArray) {
        List<Integer> ids = new  ArrayList<Integer>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                ids.add(jsonArray.getInt(i));
            } catch (JSONException e) {
                Log.e(getClass().getName(), "Error parsing Genre Ids", e);
            }
        }
        return ids;
    }

    public Integer getId() {
        return id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public List<Integer> getGenreList() {
        List<Integer> result = new ArrayList<Integer>();
        result.addAll(genreList);
        return result;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public Double getPopularity() {
        return popularity;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public Boolean getVideo() {
        return video;
    }

    public Boolean getAdult() {
        return adult;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }



}