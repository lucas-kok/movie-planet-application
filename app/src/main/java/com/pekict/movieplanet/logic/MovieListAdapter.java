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
        private final CardView mMovieCard;
        private final ImageView mMovieImage;

        final MovieListAdapter mAdapter;

        public MovieViewHolder(View itemView, MovieListAdapter mAdapter) {
            super(itemView);

            mMovieCard = itemView.findViewById(R.id.card_movie);
            mMovieImage = itemView.findViewById(R.id.iv_movie);

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

        // onClickListener to open the MealViewActivity passing the clicked Meal with it
        holder.mMovieCard.setOnClickListener(view -> {
            // Todo: Create detail activity
//            Intent intent = new Intent(mainActivity, MealViewActivity.class);
//
//            Bundle bundle = new Bundle();
//            bundle.putParcelable("mealObj", mCurrent);
//            bundle.putParcelable("cookObj", mCurrent.getCook());
//            Log.e(TAG_NAME, "cookObj" + " : " + (bundle.get("cookObj") != null ? bundle.get("cookObj") : "NULL"));
//
//            intent.putExtra("bundle", bundle);
//
//            mainActivity.startActivity(intent);
        });

        // Setting the items UI elements to the Movies values
        Picasso.get().load(mCurrent.getImageURL()).into(holder.mMovieImage);
    }

    @Override
    public int getItemCount() {
        return movies.length;
    }
}
