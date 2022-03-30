package com.pekict.movieplanet.storage.movie;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.pekict.movieplanet.domain.movie.Movie;

@Database(entities = Movie.class, version = 1)
public abstract class MovieDatabase extends RoomDatabase {
    private static MovieDatabase instance;

    public static synchronized MovieDatabase getInstance(Context context) {
        if (instance == null) {
            String DATABASE_NAME = "movie_database";
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    MovieDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }

    public abstract MovieDAO getMovieDAO();
}
