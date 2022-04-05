package com.pekict.movieplanet.logic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pekict.movieplanet.R;
import com.pekict.movieplanet.domain.movie.Movie;
import com.pekict.movieplanet.domain.movie.MovieList;
import com.pekict.movieplanet.presentation.ListsActivity;
import com.pekict.movieplanet.presentation.MainActivity;
import com.pekict.movieplanet.presentation.MovieViewActivity;
import com.pekict.movieplanet.presentation.SpaceItemDecoration;
import com.pekict.movieplanet.presentation.viewmodels.MovieListViewModel;

public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.MovieViewHolder> {
    private static final String TAG_NAME = ListsAdapter.class.getSimpleName();

    private final MovieList[] mMovieLists;
    private final LayoutInflater mInflater;
    private final Movie mViewingMovie;
    private final PopupWindow mPopupWindow;
    private final boolean mIsFromMovieViewActivity;

    private final MovieListViewModel mMovieListViewModel;


    public ListsAdapter(Context context, MovieList[] movieLists, Movie viewingMovie, PopupWindow popupWindow, boolean isFromMovieViewActivity) {
        mInflater = LayoutInflater.from(context);
        mMovieLists = movieLists;
        mViewingMovie = viewingMovie;
        mPopupWindow = popupWindow;
        mIsFromMovieViewActivity = isFromMovieViewActivity;

        mMovieListViewModel = ViewModelProviders.of(ListsActivity.getInstance() != null ? ListsActivity.getInstance() : MovieViewActivity.getInstance()).get(MovieListViewModel.class);
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.list_item, parent, false);
        return new MovieViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        MovieList mCurrent = mMovieLists[position];

        holder.mMovieListTitleText.setText(mCurrent.getTitle());
        holder.mMovieListDeleteButton.setOnClickListener(view -> holder.mMovieListViewModel.deleteList(mCurrent));

        // Displaying the Movies to the RecyclerView using the MovieListAdapter
        if (!mIsFromMovieViewActivity) {
            Context context = ListsActivity.getContext() != null ? ListsActivity.getContext() : MovieViewActivity.getContext();
            MovieListAdapter mAdapter = new MovieListAdapter(context, mCurrent.getMoviesAsArray(), MainActivity.getInstance());
            holder.mMoviesRecyclerView.setAdapter(mAdapter);

            return;
        }

        holder.mMovieListCard.setOnClickListener(event -> {
            Context context = ListsActivity.getContext() != null ? ListsActivity.getContext() : MovieViewActivity.getContext();
            if (!mCurrent.addMovie(mViewingMovie)) {
                Toast.makeText(context, R.string.label_tv_error_already_in_list, Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(context, R.string.label_tv_info_movie_added_to_list, Toast.LENGTH_SHORT).show();
            }
            mMovieListViewModel.updateList(mCurrent);

            if (mPopupWindow == null) {
                return;
            }

            mPopupWindow.dismiss();
        });

        holder.mMovieListDeleteButton.setVisibility(View.INVISIBLE);
        holder.mMoviesRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mMovieLists.length;
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        private final MovieListViewModel mMovieListViewModel;
        final ListsAdapter mAdapter;

        private final CardView mMovieListCard;
        private final TextView mMovieListTitleText;
        private final ImageButton mMovieListDeleteButton;
        private final RecyclerView mMoviesRecyclerView;

        public MovieViewHolder(View itemView, ListsAdapter mAdapter) {
            super(itemView);

            // Could be called from the ListsActivity or MovieViewActivity
            mMovieListViewModel = ViewModelProviders.of(ListsActivity.getInstance() != null ? ListsActivity.getInstance() : MovieViewActivity.getInstance()).get(MovieListViewModel.class);
            this.mAdapter = mAdapter;

            mMovieListCard = itemView.findViewById(R.id.cv_movie_list);
            mMovieListTitleText = itemView.findViewById(R.id.tv_rv_movie_list_name);
            mMovieListDeleteButton = itemView.findViewById(R.id.btn_movie_list_delete);
            mMoviesRecyclerView = itemView.findViewById(R.id.rv_movie_list_movies);

            int recyclerViewVerticalSpacing = 100;

            Context context = ListsActivity.getContext() != null ? ListsActivity.getContext() : MovieViewActivity.getContext();
            mMoviesRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            mMoviesRecyclerView.addItemDecoration(new SpaceItemDecoration(recyclerViewVerticalSpacing, true));
        }
    }
}
