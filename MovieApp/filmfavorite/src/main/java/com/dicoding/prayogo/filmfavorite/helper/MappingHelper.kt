package com.dicoding.prayogo.filmfavorite.helper

import android.database.Cursor
import android.provider.BaseColumns
import com.dicoding.prayogo.filmfavorite.database.DatabaseContract.FilmColumns.Companion.DESCRIPTION
import com.dicoding.prayogo.filmfavorite.database.DatabaseContract.FilmColumns.Companion.GENRE
import com.dicoding.prayogo.filmfavorite.database.DatabaseContract.FilmColumns.Companion.NAME
import com.dicoding.prayogo.filmfavorite.database.DatabaseContract.FilmColumns.Companion.PHOTO
import com.dicoding.prayogo.filmfavorite.database.DatabaseContract.FilmColumns.Companion.POPULARITY
import com.dicoding.prayogo.filmfavorite.database.DatabaseContract.FilmColumns.Companion.RATING
import com.dicoding.prayogo.filmfavorite.database.DatabaseContract.FilmColumns.Companion.RELEASEDATE
import com.dicoding.prayogo.filmfavorite.model.Film

class MappingHelper {
    companion object {
        fun mapCursorToArrayList(filmCursor: Cursor): ArrayList<Film> {
            val filmList = ArrayList<Film>()
            while (filmCursor.moveToNext()) {
                val id = filmCursor.getInt(filmCursor.getColumnIndexOrThrow(BaseColumns._ID))
                val photo = filmCursor.getString(filmCursor.getColumnIndexOrThrow(PHOTO))
                val name = filmCursor.getString(filmCursor.getColumnIndexOrThrow(NAME))
                val description = filmCursor.getString(filmCursor.getColumnIndexOrThrow(DESCRIPTION))
                val date = filmCursor.getString(filmCursor.getColumnIndexOrThrow(RELEASEDATE))
                val rating = filmCursor.getDouble(filmCursor.getColumnIndexOrThrow(RATING))
                val genre = filmCursor.getString(filmCursor.getColumnIndexOrThrow(GENRE))
                val popularity = filmCursor.getDouble(filmCursor.getColumnIndexOrThrow(POPULARITY))
                filmList.add(Film(id, photo, name, description, date, rating, genre, popularity))
            }
            return filmList
        }
    }
}