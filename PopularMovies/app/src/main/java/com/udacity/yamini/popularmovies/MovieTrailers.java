package com.udacity.yamini.popularmovies;

/**
 * Created by Yamini on 2/15/2016.
 */
public class MovieTrailers {

    final String trailerId;
    final String movieTrailerName;
    final String movieTrailerYoutubeKey; // drawable reference id

    public MovieTrailers( String trailerId, String movieTrailerName,String movieTrailerYoutubeKey )
    {
        this.trailerId = trailerId;
        this.movieTrailerName = movieTrailerName;
        this.movieTrailerYoutubeKey = movieTrailerYoutubeKey;
    }
}
