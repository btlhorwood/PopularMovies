package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private TmdbImageArrayAdapter mAdapter;
    private String mBaseImageUrl = "";

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridView);

        mAdapter = new TmdbImageArrayAdapter(
                getActivity(),
                R.layout.movie_item,
                R.id.emptyTextView,
                new ArrayList<MovieItem>());

        gridView.setAdapter(mAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieItem item = mAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra("movie", item);
                intent.putExtra("image_url", mBaseImageUrl);
                getActivity().startActivity(intent);
            }
        });
        return rootView;
    }

    private String getSortMethod() {
        Resources resources = getResources();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return sharedPref.getString(
                resources.getString(R.string.pref_sort_key),
                resources.getString(R.string.popular));
    }


    @Override
    public void onResume() {
        super.onResume();
        new FetchMoviesTask().execute(getSortMethod());
    }

    public class TmdbImageArrayAdapter extends ArrayAdapter<MovieItem> {

        public TmdbImageArrayAdapter(Context context, int resource, int textViewResourceId, List<MovieItem> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int SIZE_INDEX = 1;
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view;
            if (convertView == null) {
                view = inflater.inflate(R.layout.movie_item, parent, false);
            } else {
                view = convertView;
            }

            MovieItem item = getItem(position);
            ImageView imageView = (ImageView)view.findViewById(R.id.imageView);

            Resources resources = getResources();
            Uri imageUri = Uri.parse(mBaseImageUrl+MainActivity.IMAGE_SIZE_STR+item.getPosterPath()).buildUpon().build();
            Picasso.with(getActivity()).load(imageUri).into(imageView);
            imageView.setAdjustViewBounds(true);
            return view;
        }
    }


    public class FetchMoviesTask extends AsyncTask<String, Void, MovieItem[]> {
        @Override
        protected void onPostExecute(MovieItem[] movieItems) {
            mAdapter.clear();
            mAdapter.addAll(Arrays.asList(movieItems));
            super.onPostExecute(movieItems);
        }

        @Override
        protected MovieItem[] doInBackground(String... params) {

            if (params.length != 1)
                return null;

            if (mBaseImageUrl.isEmpty())
                setConfigurationVariables();

            Resources resources = getResources();

            String sortOrder = params[0].equals(resources.getString(R.string.popular)) ? "popular" : "top_rated";

            String baseRequest = "http://api.themoviedb.org/3/movie/"+sortOrder;
            Uri.Builder builder = Uri.parse(baseRequest).buildUpon();
            builder.appendQueryParameter("api_key", resources.getString(R.string.tmdb_api_key));
            MovieItem[] movieItems = null;
            try {
                URL url = new URL(builder.build().toString());
                String reply = getHttpReply(url);
                movieItems = parseMovies(reply);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
            return movieItems;
        }

        private void setConfigurationVariables() {
            Resources resources = getResources();
            String baseRequest = "http://api.themoviedb.org/3/configuration";
            Uri.Builder builder = Uri.parse(baseRequest).buildUpon();
            builder.appendQueryParameter("api_key", resources.getString(R.string.tmdb_api_key));
            try {
                URL url = new URL(builder.build().toString());
                String reply = getHttpReply(url);
                if (reply != null) {
                    JSONObject jsonObject = new JSONObject(reply);
                    JSONObject jsonImages = jsonObject.getJSONObject("images");
                    mBaseImageUrl = jsonImages.getString("base_url");
                }
            } catch (MalformedURLException e) {
                Log.e(getClass().getName(), "MalformedUrlException", e);
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e(getClass().getName(), "JSONException", e);
            }
        }

        private MovieItem[] parseMovies(String jsonReply) {
            List<MovieItem> results = new ArrayList<MovieItem>();
            try {
                JSONObject json = new JSONObject(jsonReply);
                JSONArray resultArray = json.getJSONArray("results");
                for (int i = 0; i < resultArray.length(); i++) {
                    MovieItem movieItem = new MovieItem(resultArray.getJSONObject(i));
                    results.add(movieItem);
                }

            } catch (JSONException jse) {
                jse.printStackTrace();
            }
            MovieItem[] resultArray = new MovieItem[results.size()];
            return results.toArray(resultArray);
        }

        /**
         * Most of this code is based on the code from the sunshine app
         * @param url The url representing the api call to tmdb
         * @return string reply from tmdb
         */
        private String getHttpReply(URL url) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            StringBuffer buffer = null;

            try {
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
               buffer = new StringBuffer();
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
                    return "";
                }
            } catch(IOException e) {
                Log.e(getClass().getName(), "IOException", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(getClass().getName(), "Error closing stream", e);
                    }
                }
            }
            return (buffer == null) ? "" : buffer.toString();
        }
    }

}
