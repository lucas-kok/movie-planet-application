package com.pekict.movieplanet.storage.movie;

import android.app.Application;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.pekict.movieplanet.domain.movie.Movie;
import com.pekict.movieplanet.domain.movie.MovieFetchResponse;
import com.pekict.movieplanet.storage.APIService;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieRepository {
    private static final String TAG_NAME = MovieRepository.class.getSimpleName();
    private static volatile MovieRepository instance;

    private static MutableLiveData<Movie[]> mMovies;
    private static MutableLiveData<Movie[]> mSearchedMovies;
    private static MutableLiveData<Movie> mSharedMovie;

    private static MovieDAO mMovieDAO;
    private static int mMoviePagesInDatabase;

    public MovieRepository(Application application) {
        mMovies = new MutableLiveData<>();
        mSearchedMovies = new MutableLiveData<>();
        mSharedMovie = new MutableLiveData<>();

        mMovieDAO = MovieDatabase.getInstance(application).getMovieDAO();
        mMoviePagesInDatabase = 4;
    }

    // Get instance of Singleton MovieRepository
    public static MovieRepository getInstance(Application application) {
        if (instance == null) {
            instance = new MovieRepository(application);
        }
        return instance;
    }

    // Function that will start fetching the Movies based on if the user has internet
    public void fetchMovies(boolean hasInternet, String query, int moviePage) {
        if (hasInternet) {
            new FetchMoviesAPIAsyncTask(query, moviePage).execute();
            Log.d(TAG_NAME, "Retrieving Movies from API");
        } else {
            new GetMoviesAsyncTask(mMovieDAO, query).execute();
            Log.d(TAG_NAME, "Retrieving Movies from Database");
        }
    }

    public void fetchMovieById(int movieId) {
        new FetchMovieByIdAPIAsyncTask(movieId).execute();
    }

    // Function that saves the Movies to the database
    public static void saveMoviesToDatabase() {
        new UpdateMoviesAsyncTask(mMovieDAO).execute(mMovies.getValue());
        Log.d(TAG_NAME, "Movies saved in Database");
    }

    public LiveData<Movie[]> getMovies() {
        return mMovies;
    }

    public LiveData<Movie[]> getSearchedMovies() {
        return mSearchedMovies;
    }

    public LiveData<Movie> getSharedMovie() {
        return mSharedMovie;
    }

    public void setMovies(Movie[] movies) {
        mMovies.setValue(movies);
    }

    public void searchMovies(String query) {
        new SearchMoviesAPIAsyncTask(query).execute();
    }

    public void clearMovies() {
        mMovies.setValue(null);
    }

    // Function that will return and merge two arrays into one
    private static Movie[] mergeMovieArrays(Movie[] arrOne, Movie[] arrTwo) {
        if (arrOne == null) return arrTwo;

        Movie[] newArr = new Movie[arrOne.length + arrTwo.length];
        System.arraycopy(arrOne, 0, newArr, 0, arrOne.length);

        System.arraycopy(arrTwo, 0, newArr, arrOne.length, arrTwo.length);

        return newArr;
    }

    // AsyncTask Class that will get all Movies from the SQLite DataBase
    private static class GetMoviesAsyncTask extends AsyncTask<Void, Void, Movie[]> {
        private final MovieDAO mMovieDAO;
        private final String mQuery;

        private GetMoviesAsyncTask(MovieDAO movieDAO, String query) {
            mMovieDAO = movieDAO;
            mQuery = query;
        }

        @Override
        protected Movie[] doInBackground(Void... voids) {
            String[] queryPieces = mQuery.split("\\.");

            return mMovieDAO.getAllPopularMovies(queryPieces[0], queryPieces[1]);
        }

        protected void onPostExecute(Movie[] result) {
            mMovies.setValue(result);
        }
    }

    // AsyncTask Class that updates all Movies from the SQLite DataBase
    private static class UpdateMoviesAsyncTask extends AsyncTask<Movie[], Void, Void> {
        private final MovieDAO mMovieDAO;

        private UpdateMoviesAsyncTask(MovieDAO movieDAO) {
            mMovieDAO = movieDAO;
        }

        @Override
        protected Void doInBackground(Movie[]... movies) {
            mMovieDAO.deleteAllPopularMovies();
            mMovieDAO.savePopularMovies(movies[0]);
            return null;
        }
    }

    // AsyncTask Class that will fetch Movies from the API
    private static class SearchMoviesAPIAsyncTask extends AsyncTask<String, Void, MovieFetchResponse> {
        private final String mQuery;

        public SearchMoviesAPIAsyncTask(String query) {
            mQuery = query;
        }

        @Override
        protected MovieFetchResponse doInBackground(String... strings) {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.themoviedb.org/3/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                APIService service = retrofit.create(APIService.class);

                Uri queryUri = Uri.parse(mQuery);

                Call<MovieFetchResponse> call = service.getMovies("search/movie?api_key=c7cc756ca295eabae15bda786602c697&language=en-US&page=1&include_adult=false&query=" + queryUri);
                Response<MovieFetchResponse> response = call.execute();

                Log.d(TAG_NAME, "Executed call, response.code = " + response.code());
                Log.d(TAG_NAME, response.toString());

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    Log.e(TAG_NAME, "Error while searching Movies");
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG_NAME, "Exception: " + e);
                return null;
            }

        }

        @Override
        protected void onPostExecute(MovieFetchResponse result) {
            if (result != null && result.getResults() != null) {
                mSearchedMovies.setValue(result.getResults());
            } else {
                Log.e(TAG_NAME, "No Movies found for Query: " + mQuery + "!");
            }
        }
    }

    // AsyncTask Class that will fetch Movies from the API based on the given sorting-query
    private static class FetchMoviesAPIAsyncTask extends AsyncTask<String, Void, MovieFetchResponse> {
        private final String mQuery;
        private final int mMoviePage;

        public FetchMoviesAPIAsyncTask(String query, int moviePage) {
            mMoviePage = moviePage;
            mQuery = query;
        }

        @Override
        protected MovieFetchResponse doInBackground(String... strings) {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.themoviedb.org/3/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                APIService service = retrofit.create(APIService.class);

                Uri queryUri = Uri.parse(mQuery);

                Call<MovieFetchResponse> call = service.getMovies("discover/movie?api_key=c7cc756ca295eabae15bda786602c697&language=en-US&sort_by=" + queryUri + "&page=" + mMoviePage);
                Response<MovieFetchResponse> response = call.execute();

                Log.d(TAG_NAME, "Executed call, response.code = " + response.code());
                Log.d(TAG_NAME, response.toString());

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    Log.e(TAG_NAME, "Error while sorting Movies");
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG_NAME, "Exception: " + e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieFetchResponse result) {
            if (result != null && result.getResults() != null) {
                Movie[] newMovies = result.getResults();

                if (mMovies.getValue() == null) {
                    mMovies.setValue(newMovies);
                } else {
                    mMovies.setValue(mergeMovieArrays(mMovies.getValue(), newMovies));
                }

                if (mMoviePage > mMoviePagesInDatabase) { return; }

                saveMoviesToDatabase();
            } else {
                Log.e(TAG_NAME, "No Movies found for Query: " + mQuery + "!");
            }
        }
    }

    // AsyncTask Class that will fetch Movies from the API based on the given Movie Id
    private static class FetchMovieByIdAPIAsyncTask extends AsyncTask<String, Void, Movie> {
        private final int mMovieId;

        public FetchMovieByIdAPIAsyncTask(int id) {
            mMovieId = id;
        }

        @Override
        protected Movie doInBackground(String... strings) {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.themoviedb.org/3/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                APIService service = retrofit.create(APIService.class);

                Call<Movie> call = service.getMovieById("movie/" + mMovieId + "?api_key=c7cc756ca295eabae15bda786602c697&language=en-US&page=1");
                Response<Movie> response = call.execute();

                Log.d(TAG_NAME, "Executed call, response.code = " + response.code());
                Log.d(TAG_NAME, response.toString());

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    Log.e(TAG_NAME, "Error while sorting Movies");
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG_NAME, "Exception: " + e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie result) {
            if (result != null) {
                mSharedMovie.setValue(result);
            } else {
                Log.e(TAG_NAME, "No Movies found for id: " + mMovieId + "!");
            }
        }
    }
}
