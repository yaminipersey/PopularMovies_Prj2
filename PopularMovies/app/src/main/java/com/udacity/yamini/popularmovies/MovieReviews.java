package com.udacity.yamini.popularmovies;

/**
 * Created by Yamini on 2/15/2016.
 */
public class MovieReviews {


    final String reviewId;
    final String author;
    final String content;
    final String movieReviewPath; // drawable reference id

    public MovieReviews(String reviewId,String author,String content, String movieReviewPath )
    {
        this.reviewId = reviewId;
        this.author = author;
        this.content = content;
        this.movieReviewPath = movieReviewPath;
    }
}
