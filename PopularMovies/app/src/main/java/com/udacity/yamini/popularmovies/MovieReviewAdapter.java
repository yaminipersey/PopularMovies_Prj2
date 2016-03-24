package com.udacity.yamini.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yamini on 3/1/2016.
 */
public class MovieReviewAdapter extends ArrayAdapter<MovieReviews> {

    private static final String LOG_TAG = MovieReviewAdapter.class.getSimpleName();
    private List<MovieReviews> movieReviewResults = new ArrayList<MovieReviews>();


    public MovieReviewAdapter(Activity context,int layoutResourceId, List<MovieReviews> moviesList) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, layoutResourceId, moviesList);

        movieReviewResults.addAll(moviesList);

    }

    @Override
    public int getCount() {

        return (movieReviewResults != null) ? movieReviewResults.size() : 0;
    }

    @Override
    public MovieReviews getItem(int position) {

        return movieReviewResults.get(position);
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
        MovieReviews movieReview = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.

        Context context = getContext();


        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.list_review_item, parent, false);
        }

        TextView author = (TextView)convertView.findViewById(R.id.text_view_author);
        TextView content = (TextView)convertView.findViewById(R.id.text_view_content);

        author.setText(movieReview.author);
        content.setText(movieReview.content);

        return convertView;
    }
}
