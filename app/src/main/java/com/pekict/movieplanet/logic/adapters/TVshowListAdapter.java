package com.pekict.movieplanet.logic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pekict.movieplanet.R;
import com.pekict.movieplanet.domain.tvshow.TVshow;
import com.pekict.movieplanet.presentation.MainActivity;
import com.squareup.picasso.Picasso;

public class TVshowListAdapter extends RecyclerView.Adapter<TVshowListAdapter.TVshowViewHolder> {
    private static final String TAG_NAME = TVshowListAdapter.class.getSimpleName();

    private final TVshow[] tvShows;
    private final LayoutInflater mInflater;

    public TVshowListAdapter(Context context, TVshow[] tvShows) {
        mInflater = LayoutInflater.from(context);
        this.tvShows = tvShows;
    }

    @NonNull
    @Override
    public TVshowListAdapter.TVshowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.movie_item, parent, false);
        return new TVshowListAdapter.TVshowViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(TVshowListAdapter.TVshowViewHolder holder, int position) {
        TVshow mCurrent = tvShows[position];

        // Listener to open the MovieViewActivity passing the clicked Movie with it
        holder.mTVshowImage.setOnClickListener(view -> {
            // Todo: Create detail activity
//            Bundle bundle = new Bundle();
//            bundle.putParcelable("movieObj", mCurrent);
//
//            Intent intent = new Intent(mainActivity, MovieViewActivity.class);
//            intent.putExtra("bundle", bundle);
//
//            mainActivity.startActivity(intent);
        });

        // Declaring the needed Movie information
        String backdropPath = mCurrent.getSmallImageURL();
        String firstReleaseDate = mCurrent.getFirst_air_date();
        String name = mCurrent.getName();
        String genres = mCurrent.getGenresAsString();

        // Setting the items UI elements to the Movies values

        // Displaying a placeholder Image when the Movie doesn't contain a valid image Url
        if (backdropPath.equals("https://image.tmdb.org/t/p/w500null")) {
            holder.mTVshowImage.setImageResource(R.drawable.placeholder);
        } else {
            Picasso.get().load(mCurrent.getSmallImageURL()).into(holder.mTVshowImage);
        }
        // Removing the releaseYearText when the Movie doesn't contain a release date, otherwise displaying the year
        if (firstReleaseDate == null) {
            holder.mTVshowFirstReleaseDateText.setVisibility(View.GONE);
        } else {
            holder.mTVshowFirstReleaseDateText.setText(firstReleaseDate);
        }


        holder.mTVshowNameText.setText(name);
        holder.mTVshowGenreText.setText(genres);
    }

    @Override
    public int getItemCount() {
        return tvShows.length;
    }

    static class TVshowViewHolder extends RecyclerView.ViewHolder {
        final TVshowListAdapter mAdapter;
        private final ImageView mTVshowImage;
        private final TextView mTVshowFirstReleaseDateText;
        private final TextView mTVshowNameText;
        private final TextView mTVshowGenreText;

        public TVshowViewHolder(View itemView, TVshowListAdapter mAdapter) {
            super(itemView);

            mTVshowImage = itemView.findViewById(R.id.iv_rv_movie_image);
            mTVshowFirstReleaseDateText = itemView.findViewById(R.id.tv_rv_release_year);
            mTVshowNameText = itemView.findViewById(R.id.tv_rv_movie_title);
            mTVshowGenreText = itemView.findViewById(R.id.tv_rv_movie_genres);

            this.mAdapter = mAdapter;
        }
    }
}

