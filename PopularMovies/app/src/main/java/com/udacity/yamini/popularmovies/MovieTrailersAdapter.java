package com.udacity.yamini.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yamini on 3/4/2016.
 */

public class MovieTrailersAdapter extends ArrayAdapter<MovieTrailers> {

    private static final String LOG_TAG = MovieTrailersAdapter.class.getSimpleName();
    private List<MovieTrailers> movieTrailerResults = new ArrayList<MovieTrailers>();


    public MovieTrailersAdapter(Activity context,int layoutResourceId, List<MovieTrailers> moviesList) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, layoutResourceId, moviesList);

        movieTrailerResults.addAll(moviesList);

    }

    @Override
    public int getCount() {

        return (movieTrailerResults != null) ? movieTrailerResults.size() : 0;
    }

    @Override
    public MovieTrailers getItem(int position) {

        return movieTrailerResults.get(position);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     *                    (search online for "android view recycling" to learn more)
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        MovieTrailers movieTrailer = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.

        Context context = getContext();


        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.list_trailer_item, parent, false);
        }

        final String trailerUrl="https://www.youtube.com/watch?v="+movieTrailer.movieTrailerYoutubeKey;

        ImageView trailerPlay = (ImageView)convertView.findViewById(R.id.playButton);

        trailerPlay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
                getContext().startActivity(intent);}
        });

        TextView trailerName = (TextView)convertView.findViewById(R.id.text_view_trailerName);
        trailerName.setText(movieTrailer.movieTrailerName);

        return convertView;
    }
}
