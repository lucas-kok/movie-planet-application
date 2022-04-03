package com.pekict.movieplanet.storage.movie;

import android.app.Application;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

    private static MovieDAO mMovieDAO;
    private static int mMoviePagesInDatabase;

    public MovieRepository(Application application) {
        mMovies = new MutableLiveData<>();
        mSearchedMovies = new MutableLiveData<>();

        mMovieDAO = MovieDatabase.getInstance(application).getMovieDAO();
        mMoviePagesInDatabase = 1;
    }

    // Get instance of Singleton MovieRepository
    public static MovieRepository getInstance(Application application) {
        if (instance == null) {
            instance = new MovieRepository(application);
        }
        return instance;
    }

    // Function that will start fetching the Movies based on if the user has internet
    public void fetchMovies(boolean hasInternet, int popularMoviePages) {
        if (hasInternet) {
            new FetchPopularMoviesAPIAsyncTask(popularMoviePages).execute();
            Log.d(TAG_NAME, "Retrieving Movies from API");
        } else {
            new GetPopularMealsAsyncTask(mMovieDAO).execute();
            Log.d(TAG_NAME, "Retrieving Movies from Database");
        }
    }

    // Function that saves the Movies to the database
    public static void savePopularMoviesToDatabase() {
        new UpdatePopularMealsAsyncTask(mMovieDAO).execute(mMovies.getValue());
        Log.d(TAG_NAME, "Movies saved in Database");
    }

    public LiveData<Movie[]> getMovies() {
        return mMovies;
    }

    public void setMovies(Movie[] movies) {
        mMovies.setValue(movies);
    }

    public void searchMovies(String query) {
        new SearchMoviesAPIAsyncTask(query).execute();
    }

    public void sortMovies(String query) {
        new SortMoviesAPIAsyncTask(query).execute();
    }

    public LiveData<Movie[]> getSearchedMovies() {
        return mSearchedMovies;
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
    private static class GetPopularMealsAsyncTask extends AsyncTask<Void, Void, Movie[]> {
        private final MovieDAO mMovieDAO;

        private GetPopularMealsAsyncTask(MovieDAO movieDAO) {
            mMovieDAO = movieDAO;
        }

        @Override
        protected Movie[] doInBackground(Void... voids) {
            return mMovieDAO.getAllPopularMovies();
        }

        protected void onPostExecute(Movie[] result) {
            mMovies.setValue(result);
        }
    }

    // AsyncTask Class that updates all Movies from the SQLite DataBase
    private static class UpdatePopularMealsAsyncTask extends AsyncTask<Movie[], Void, Void> {
        private final MovieDAO mMovieDAO;

        private UpdatePopularMealsAsyncTask(MovieDAO movieDAO) {
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
    private static class FetchPopularMoviesAPIAsyncTask extends AsyncTask<String, Void, MovieFetchResponse> {
        private final int mPopularMoviePages;

        public FetchPopularMoviesAPIAsyncTask(int popularMoviePages) {
            mPopularMoviePages = popularMoviePages;
        }

        @Override
        protected MovieFetchResponse doInBackground(String... strings) {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.themoviedb.org/3/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                APIService service = retrofit.create(APIService.class);

                Call<MovieFetchResponse> call = service.getMovies("movie/popular?api_key=c7cc756ca295eabae15bda786602c697&page=" + mPopularMoviePages);
                Response<MovieFetchResponse> response = call.execute();

                Log.d(TAG_NAME, "Executed call, response.code = " + response.code());
                Log.d(TAG_NAME, response.toString());

                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    Log.e(TAG_NAME, "Error while loading meals");
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
                Movie[] newMovies = mergeMovieArrays(mMovies.getValue(), result.getResults());

                mMovies.setValue(newMovies);

                // Only saving to the Database when it is in bound of the stated page(s) of Movies
                if (mPopularMoviePages > mMoviePagesInDatabase) return;

                savePopularMoviesToDatabase();
                Log.d(TAG_NAME, "onPostExecute found : " + result.getResults().length + " Movies");
            } else {
                Log.e(TAG_NAME, "No Movies found!");
            }
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
    private static class SortMoviesAPIAsyncTask extends AsyncTask<String, Void, MovieFetchResponse> {
        private final String mQuery;

        public SortMoviesAPIAsyncTask(String query) {
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

                Call<MovieFetchResponse> call = service.getMovies("discover/movie?api_key=c7cc756ca295eabae15bda786602c697&language=en-US&sort_by=" + queryUri + "&page=1");
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
                mMovies.setValue(result.getResults());
            } else {
                Log.e(TAG_NAME, "No Movies found for Query: " + mQuery + "!");
            }
        }
    }
}
