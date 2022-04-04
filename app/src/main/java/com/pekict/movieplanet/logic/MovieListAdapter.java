package com.pekict.movieplanet.logic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pekict.movieplanet.R;
import com.pekict.movieplanet.domain.movie.Movie;
import com.pekict.movieplanet.presentation.MainActivity;
import com.pekict.movieplanet.presentation.MovieViewActivity;
import com.squareup.picasso.Picasso;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {
    private static final String TAG_NAME = MovieListAdapter.class.getSimpleName();

    private final Movie[] movies;
    private final LayoutInflater mInflater;
    private final MainActivity mainActivity;

    public MovieListAdapter(Context context, Movie[] movies, MainActivity mainActivity) {
        mInflater = LayoutInflater.from(context);
        this.movies = movies;

        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie mCurrent = movies[position];

        // Listener to open the MovieViewActivity passing the clicked Movie with it
        holder.mMovieImage.setOnClickListener(view -> {
            // Todo: Create detail activity
            Bundle bundle = new Bundle();
            bundle.putParcelable("movieObj", mCurrent);

            Intent intent = new Intent(mainActivity, MovieViewActivity.class);
            intent.putExtra("bundle", bundle);

            mainActivity.startActivity(intent);
        });

        // Declaring the needed Movie information
        String backdropPath = mCurrent.getSmallImageURL();
        String releaseYear = mCurrent.getReleaseYear();
        String title = mCurrent.getTitle();
        String genres = mCurrent.getGenresAsString();

        // Setting the items UI elements to the Movies values

        // Displaying a placeholder Image when the Movie doesn't contain a valid image Url
        if (backdropPath.equals("https://image.tmdb.org/t/p/w500null")) {
            holder.mMovieImage.setImageResource(R.drawable.placeholder);
        } else {
            Picasso.get().load(mCurrent.getSmallImageURL()).into(holder.mMovieImage);
        }
        // Removing the releaseYearText when the Movie doesn't contain a release date, otherwise displaying the year
        if (releaseYear == null) {
            holder.mMovieReleaseYearText.setVisibility(View.GONE);
        } else {
            holder.mMovieReleaseYearText.setText(releaseYear);
        }
        holder.mMovieTitleText.setText(title);
        holder.mMovieGenreText.setText(genres);
    }

    @Override
    public int getItemCount() {
        return movies.length;
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        final MovieListAdapter mAdapter;
        private final ImageView mMovieImage;
        private final TextView mMovieReleaseYearText;
        private final TextView mMovieTitleText;
        private final TextView mMovieGenreText;

        public MovieViewHolder(View itemView, MovieListAdapter mAdapter) {
            super(itemView);

            mMovieImage = itemView.findViewById(R.id.iv_rv_movie_image);
            mMovieReleaseYearText = itemView.findViewById(R.id.tv_rv_release_year);
            mMovieTitleText = itemView.findViewById(R.id.tv_rv_movie_title);
            mMovieGenreText = itemView.findViewById(R.id.tv_rv_movie_genres);

            this.mAdapter = mAdapter;
        }
    }
}
