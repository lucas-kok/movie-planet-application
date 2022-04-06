package com.pekict.movieplanet.logic;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pekict.movieplanet.domain.movie.Movie;

import java.lang.reflect.Type;
import java.util.List;

public class Converters {
    private static final String TAG_NAME = Converters.class.getSimpleName();

    @TypeConverter
    public static List<Integer> fromIntegerStringToList(String value) {
        Type listType = new TypeToken<List<Integer>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromListToIntegerString(List<Integer> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public String fromMovieList(List<Movie> movies) {
        if (movies == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Movie>>() {}.getType();
        return gson.toJson(movies, type);
    }

    @TypeConverter
    public List<Movie> toMovieList(String input) {
        if (input == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Movie>>() {}.getType();
        return gson.fromJson(input, type);
    }
}
