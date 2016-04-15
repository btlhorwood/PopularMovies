package com.example.android.popularmovies;

import android.graphics.Movie;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsActivityFragment extends Fragment {

    public MovieDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);

        MovieItem movie = getActivity().getIntent().getExtras().getParcelable("movie");

        TextView titleView = (TextView)view.findViewById(R.id.textView_title);
        TextView summaryView = (TextView)view.findViewById(R.id.textView_overview);
        ImageView thumbnailView = (ImageView)view.findViewById(R.id.imageView_thumbnail);

        titleView.setText(movie.getTitle());
        summaryView.setText(movie.getOverview());

        String imageUrl = getActivity().getIntent().getExtras().getString("image_url");
        Uri imageUri = Uri.parse(imageUrl+MainActivity.IMAGE_SIZE_STR+movie.getPosterPath()).buildUpon().build();
        Picasso.with(getActivity()).load(imageUri).into(thumbnailView);

        return view;
    }
}
