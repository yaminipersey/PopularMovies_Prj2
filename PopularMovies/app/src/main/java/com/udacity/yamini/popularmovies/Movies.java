package com.udacity.yamini.popularmovies;

import java.io.Serializable;

/**
 * Created by Yamini on 11/23/2015.
 */
public class Movies implements Serializable {

    String movieTitle;
    int movieId;
    String moviePosterPath; // drawable reference id
    String movieReleaseDate;
    String movieRating;
    String moviePlot;

    public Movies(int movieId, String movieTitle, String moviePosterPath,String movieReleaseDate,String movieRating,String moviePlot )
    {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.moviePosterPath = moviePosterPath;
        this.movieReleaseDate= movieReleaseDate;
        this.movieRating = movieRating;
        this.moviePlot = moviePlot;
    }
}
