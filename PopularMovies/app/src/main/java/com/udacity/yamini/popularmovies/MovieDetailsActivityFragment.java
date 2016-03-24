package com.udacity.yamini.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.yamini.popularmovies.data.MoviesContract;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsActivityFragment extends Fragment {

    private Intent intent;
    List<MovieReviews> movieReviewResults = new ArrayList<>();
    List<MovieTrailers> movieTrailerResults = new ArrayList<>();
    private MovieReviewAdapter movieReviewAdapter;
    private MovieTrailersAdapter movieTrailersAdapter;
    private LinearLayout mReviewLayout, mTrailerLayout;
    private Button favoriteButton;
    private static final int CURSOR_LOADER_ID = 0;
    private Movies movie;

    public MovieDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        intent = getActivity().getIntent();

        String title;
        String imagePath;
        String releaseDate;
        String userRating;
        String plot;
        final String movieId;
        Bundle args = getArguments();

        if(args != null) {

            title = args.getString("title");
            imagePath = args.getString("image");
            String str[] = args.getString("releaseDate").split("-");
            releaseDate = str[0];
            userRating = args.getString("rating") + "/10";
            plot = args.getString("plot");
            movieId = Integer.toString(args.getInt("id", 0));
            movie = (Movies) args.getSerializable("movieObject");
        }
        else
        {
            title = intent.getStringExtra("title");
            imagePath = intent.getStringExtra("image");
            String str[] = intent.getStringExtra("releaseDate").split("-");
            releaseDate = str[0];
            userRating = intent.getStringExtra("rating") + "/10";
            plot = intent.getStringExtra("plot");
            movieId = Integer.toString(intent.getIntExtra("id", 0));
            movie = (Movies) intent.getExtras().getSerializable("movieObject");
        }

        mTrailerLayout = (LinearLayout)rootView.findViewById(R.id.trailers_list);
        mReviewLayout = (LinearLayout) rootView.findViewById(R.id.reviews_list);
        favoriteButton = (Button)rootView.findViewById(R.id.favorite_button);

        favoriteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                insertFavoriteMovieData();}
        });

        updateMovieDetails(movieId);

        final String IMAGE_BASE_URL =
                "http://image.tmdb.org/t/p/";
        final String IMAGE_SIZE = "w185";

        String imageUrl = IMAGE_BASE_URL+IMAGE_SIZE+imagePath;

        final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
        String apiKey=BuildConfig.MOVIEDB_API_KEY;

        TextView titleTextView = (TextView) rootView.findViewById(R.id.grid_movie_title);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.grid_movie_image);
        TextView  releaseDateTextView = (TextView) rootView.findViewById(R.id.grid_movie_release_date);
        TextView ratingTextView = (TextView) rootView.findViewById(R.id.grid_movie_rating);
        TextView plotTextView = (TextView) rootView.findViewById(R.id.grid_movie_plot);

        titleTextView.setText(title);
        Picasso.with(getContext()).load(imageUrl).into(imageView);
        releaseDateTextView.setText(releaseDate);
        ratingTextView.setText(userRating);
        plotTextView.setText(plot);
        return rootView ;
    }

    public void updateMovieDetails(String movieId) {

        FetchMovieTrailersTask movieTrailersTask = new FetchMovieTrailersTask();
        movieTrailersTask.execute(movieId);

        FetchMovieReviewsTask movieReviewsTask = new FetchMovieReviewsTask();
        movieReviewsTask.execute(movieId);

    }

    public class FetchMovieReviewsTask extends AsyncTask<String, Void, List<MovieReviews>> {

        private final String LOG_TAG = FetchMovieReviewsTask.class.getSimpleName();

        @Override
        protected List<MovieReviews> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieId;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {

                movieId=params[0];
                final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
                String apiKey=BuildConfig.MOVIEDB_API_KEY;
                String movieReviewURL =  MOVIE_BASE_URL+movieId+"/reviews?api_key="+apiKey;

                URL url = new URL(movieReviewURL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                movieReviewResults = MovieUtility.getMovieReviewDataFromJson(moviesJsonStr);
                return movieReviewResults;
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<MovieReviews> result) {
            super.onPostExecute(result);

            movieReviewAdapter = new MovieReviewAdapter(getActivity(),R.id.moviereview_item,movieReviewResults);
            final int adapterCount = movieReviewAdapter.getCount();

            for (int i = 0; i < adapterCount; i++) {
                View item = movieReviewAdapter.getView(i, null, null);
                mReviewLayout.addView(item);
            }
        }

    }

    public class FetchMovieTrailersTask extends AsyncTask<String, Void, List<MovieTrailers>> {

        private final String LOG_TAG = FetchMovieTrailersTask.class.getSimpleName();

        @Override
        protected List<MovieTrailers> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieId;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {

                movieId=params[0];
                final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
                String apiKey=BuildConfig.MOVIEDB_API_KEY;
                String movieReviewURL =  MOVIE_BASE_URL+movieId+"/videos?api_key="+apiKey;

                URL url = new URL(movieReviewURL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                movieTrailerResults = MovieUtility.getMovieTrailerDataFromJson(moviesJsonStr);
                return movieTrailerResults;
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<MovieTrailers> result) {
            super.onPostExecute(result);

            movieTrailersAdapter = new MovieTrailersAdapter(getActivity(),R.id.movietrailer_item,movieTrailerResults);
            final int adapterCount = movieTrailersAdapter.getCount();

            for (int i = 0; i < adapterCount; i++) {
                View item = movieTrailersAdapter.getView(i, null, null);
                mTrailerLayout.addView(item);
            }
        }

    }

    public void insertFavoriteMovieData(){
        ContentValues favoriteMovie = new ContentValues();

        favoriteMovie.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID,movie.movieId);
        favoriteMovie.put(MoviesContract.MoviesEntry.COLUMN_TITLE,movie.movieTitle);
        favoriteMovie.put(MoviesContract.MoviesEntry.COLUMN_POSTERPATH,movie.moviePosterPath);
        favoriteMovie.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE,movie.movieReleaseDate);
        favoriteMovie.put(MoviesContract.MoviesEntry.COLUMN_RATING,movie.movieRating);
        favoriteMovie.put(MoviesContract.MoviesEntry.COLUMN_PLOT,movie.moviePlot);

        // bulkInsert our ContentValues
        getActivity().getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI,
                favoriteMovie);
    }

}
