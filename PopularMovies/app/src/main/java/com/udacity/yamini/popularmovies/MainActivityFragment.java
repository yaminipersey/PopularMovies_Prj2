package com.udacity.yamini.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private MoviesAdapter moviesAdapter;
    List<Movies> moviesResults = new ArrayList<>();
    private GridView mMoviesGridView;
    private static final int CURSOR_LOADER_ID = 0;
    private boolean mTwoPane;
    private static final String MOVIEDETAILFRAGMENT_TAG = "MOVIEDFTAG";

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        updateMovies();

        mTwoPane = ((MainActivity)this.getActivity()).mIsTwoPane;

   /*     if (getActivity().findViewById(R.id.movie_detail_container_tablet) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_fragment_tablet, new MovieDetailsActivityFragment(), MOVIEDETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
*/
        mMoviesGridView = (GridView) rootView.findViewById(R.id.movies_grid);

        mMoviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //Get item at position
                Movies movieFromGrid = (Movies) parent.getItemAtPosition(position);

                if (getActivity().findViewById(R.id.movie_detail_container_tablet) != null)
                {
                    Fragment fragment = new MovieDetailsActivityFragment();
                    Bundle arg = new Bundle();
                    arg.putSerializable("id", movieFromGrid.movieId);
                    arg.putSerializable("title", movieFromGrid.movieTitle);
                    arg.putSerializable("image", movieFromGrid.moviePosterPath);
                    arg.putSerializable("releaseDate", movieFromGrid.movieReleaseDate);
                    arg.putSerializable("plot", movieFromGrid.moviePlot);
                    arg.putSerializable("rating", movieFromGrid.movieRating);
                    arg.putSerializable("movieObject", movieFromGrid);

                    fragment.setArguments(arg);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_container_tablet, new MovieDetailsActivityFragment(), MOVIEDETAILFRAGMENT_TAG)
                            .commit();
                }
                else
                {
                    //Pass the image title and url to DetailsActivity
                    Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                    intent.putExtra("id", movieFromGrid.movieId);
                    intent.putExtra("title", movieFromGrid.movieTitle);
                    intent.putExtra("image", movieFromGrid.moviePosterPath);
                    intent.putExtra("releaseDate", movieFromGrid.movieReleaseDate);
                    intent.putExtra("plot", movieFromGrid.moviePlot);
                    intent.putExtra("rating", movieFromGrid.movieRating);
                    intent.putExtra("movieObject", movieFromGrid);


                    //Start details activity
                    startActivity(intent);
                }
            }
        });


        return rootView;
    }

    private void updateMovies() {

        SharedPreferences moviePrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String order = moviePrefs.getString(getString(R.string.pref_movie_prefs_key), getString(R.string.pref_movies_popularity));

        if (order.equals("favorite")) {
            Log.v("Favorites", "order: " + order);
            getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);

        } else {
            FetchMoviesTask moviesTask = new FetchMoviesTask();
            moviesTask.execute();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){

        return new CursorLoader(getActivity(),
                MoviesContract.MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        ArrayList<Movies> favoriteMovies = new ArrayList<Movies>();

        try {

            while (data.moveToNext()) {

                Movies movie = new Movies(data.getInt(data.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID)),
                        data.getString(data.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE)),
                        data.getString(data.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POSTERPATH)),
                        data.getString(data.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE)),
                        data.getString(data.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RATING)),
                        data.getString(data.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_PLOT)));

                favoriteMovies.add(movie);
            }
            data.close();

            moviesAdapter = new MoviesAdapter(getActivity(), R.id.movies_item, favoriteMovies);
            mMoviesGridView.setAdapter(moviesAdapter);
        }
        catch(Exception ex)
        {
            Log.v("Error reading from db",ex.getMessage());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    public class FetchMoviesTask extends AsyncTask<Movies, Void, List<Movies>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected List<Movies> doInBackground(Movies... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            SharedPreferences moviePrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortByPreference = moviePrefs.getString(
                    getString(R.string.pref_movie_prefs_key),
                    getString(R.string.pref_movies_popularity));

            try {

                Uri builtUri = builtURIBYPreference(sortByPreference);
                URL url = new URL(builtUri.toString());
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
                moviesResults = MovieUtility.getMovieDataFromJson(moviesJsonStr);
                return moviesResults;
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        private Uri builtURIBYPreference(String sortByPref)
        {
            final String MOVIES_BASE_URL =
                    "http://api.themoviedb.org/3/discover/movie?";
            final String QUERY_PARAM = "sort_by";
            final String APPID_PARAM = "api_key";
            final String CERTIFICATIONCOUNTRY_PARAM = "certification_country";
            Uri builtUri;

            if (sortByPref.equalsIgnoreCase("popularity")) {
                builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, "popularity.desc")
                        .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIEDB_API_KEY)
                        .build();
            }
            else {

                builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(CERTIFICATIONCOUNTRY_PARAM,"US")
                        .appendQueryParameter(QUERY_PARAM, "vote_average.desc")
                        .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIEDB_API_KEY)
                        .build();
            }
            return builtUri;
        }

        @Override
        protected void onPostExecute(List<Movies> result) {
            //super.onPostExecute(result);

            moviesAdapter = new MoviesAdapter(getActivity(),R.id.movies_item,moviesResults );
            mMoviesGridView.setAdapter(moviesAdapter);

        }

    }
}
