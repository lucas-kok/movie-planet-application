package com.pekict.movieplanet.logic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pekict.movieplanet.presentation.MovieViewActivity;
import com.pekict.movieplanet.R;
import com.pekict.movieplanet.domain.Movie;
import com.pekict.movieplanet.presentation.MainActivity;
import com.squareup.picasso.Picasso;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {
    private static final String TAG_NAME = MovieListAdapter.class.getSimpleName();

    private final Movie[] movies;
    private final LayoutInflater mInflater;
    private final MainActivity mainActivity;

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mMovieImage;
        private final TextView mMovieTitleText;

        final MovieListAdapter mAdapter;

        public MovieViewHolder(View itemView, MovieListAdapter mAdapter) {
            super(itemView);

            mMovieImage = itemView.findViewById(R.id.iv_movie);
            mMovieTitleText = itemView.findViewById(R.id.tv_movie_title);

            this.mAdapter = mAdapter;
        }
    }

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

        // onClickListener to open the MovieViewActivity passing the clicked Movie with it
        holder.mMovieImage.setOnClickListener(view -> {
            // Todo: Create detail activity
            Intent intent = new Intent(mainActivity, MovieViewActivity.class);

            Bundle bundle = new Bundle();
            bundle.putParcelable("movieObj", mCurrent);

            intent.putExtra("bundle", bundle);

            mainActivity.startActivity(intent);
        });

        // Setting the items UI elements to the Movies values
        Picasso.get().load(mCurrent.getSmallImageURL()).into(holder.mMovieImage);
        holder.mMovieTitleText.setText(mCurrent.getTitle());
    }

    @Override
    public int getItemCount() {
        return movies.length;
    }
}
