package com.udacity.yamini.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yamini on 3/1/2016.
 */
public class MovieUtility {

    public static List<Movies> getMovieDataFromJson(String forecastJsonStr)
            throws JSONException {
        List<Movies> moviesResults = new ArrayList<>();
        // These are the names of the JSON objects that need to be extracted.
        final String MOVIE_RESULTS = "results";
        final String MOVIE_ID = "id";
        final String MOVIE_POSTERPATH = "poster_path";
        final String MOVIE_TITLE = "title";
        final String MOVIE_RELEASEDATE = "release_date";
        final String MOVIE_RATING = "vote_average";
        final String MOVIE_PLOT = "overview";


        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray movieArray = forecastJson.getJSONArray(MOVIE_RESULTS);

        int arraySize = movieArray.length();
        if (moviesResults.size() > 0) {
            moviesResults.clear();
        }

        for (int i = 0; i < arraySize; ++i) {
            // For now, using the format "Day, description, hi/low"

            // Get the JSON object representing the day
            JSONObject movieObject = movieArray.getJSONObject(i);

            int movieId = Integer.parseInt(movieObject.getString(MOVIE_ID));
            String movieTitle = movieObject.getString(MOVIE_TITLE);
            String moviePosterPath = movieObject.getString(MOVIE_POSTERPATH);
            String movieReleaseDate = movieObject.getString(MOVIE_RELEASEDATE);
            String movieRating = movieObject.getString(MOVIE_RATING);
            String moviePlot = movieObject.getString(MOVIE_PLOT);

            moviesResults.add(new Movies(movieId,movieTitle, moviePosterPath, movieReleaseDate, movieRating, moviePlot));
        }
        return moviesResults;
    }

    public static List<MovieTrailers> getMovieTrailerDataFromJson(String forecastJsonStr)
            throws JSONException {
        List<MovieTrailers> movieTrailerResults = new ArrayList<>();
        // These are the names of the JSON objects that need to be extracted.
        final String TRAILER_RESULTS = "results";
        final String TRAILER_ID = "id";
        final String TRAILER_NAME = "name";
        final String TRAILER_YOUTUBEKEY = "key";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray movieArray = forecastJson.getJSONArray(TRAILER_RESULTS);

        int arraySize = movieArray.length();
        if (movieTrailerResults.size() > 0) {
            movieTrailerResults.clear();
        }

        for (int i = 0; i < arraySize; ++i) {

            JSONObject movieObject = movieArray.getJSONObject(i);

            String trailerId = movieObject.getString(TRAILER_ID);
            String trailerName = movieObject.getString(TRAILER_NAME);
            String trailerYoutubeKey = movieObject.getString(TRAILER_YOUTUBEKEY);

            movieTrailerResults.add(new MovieTrailers(trailerId,trailerName,trailerYoutubeKey));
        }
        return movieTrailerResults;
    }

    public static List<MovieReviews> getMovieReviewDataFromJson(String forecastJsonStr)
            throws JSONException {
        List<MovieReviews> movieReviewResults = new ArrayList<>();
        // These are the names of the JSON objects that need to be extracted.
        final String REVIEW_RESULTS = "results";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_ID = "id";
        final String REVIEW_CONTENT = "content";
        final String REVIEW_URL = "url";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray movieArray = forecastJson.getJSONArray(REVIEW_RESULTS);

        int arraySize = movieArray.length();
        if (movieReviewResults.size() > 0) {
            movieReviewResults.clear();
        }

        for (int i = 0; i < arraySize; ++i) {

            JSONObject movieObject = movieArray.getJSONObject(i);

            String reviewId = movieObject.getString(REVIEW_ID);
            String reviewAuthor = movieObject.getString(REVIEW_AUTHOR);
            String reviewContent = movieObject.getString(REVIEW_CONTENT);
            String reviewUrl = movieObject.getString(REVIEW_URL);

            movieReviewResults.add(new MovieReviews(reviewId,reviewAuthor,reviewContent,reviewUrl));
        }
        return movieReviewResults;
    }
}
