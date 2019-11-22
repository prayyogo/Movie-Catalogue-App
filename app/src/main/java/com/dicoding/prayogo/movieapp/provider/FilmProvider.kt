package com.dicoding.prayogo.movieapp.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.dicoding.prayogo.movieapp.database.DatabaseContract.AUTHORITY
import com.dicoding.prayogo.movieapp.database.DatabaseContract.FilmColumns.Companion.CONTENT_URI_MOVIE
import com.dicoding.prayogo.movieapp.database.DatabaseContract.FilmColumns.Companion.CONTENT_URI_TV_SHOW
import com.dicoding.prayogo.movieapp.database.DatabaseContract.TABLE_MOVIE
import com.dicoding.prayogo.movieapp.database.DatabaseContract.TABLE_TV_SHOW
import com.dicoding.prayogo.movieapp.database.FilmHelper

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class FilmProvider : ContentProvider() {

    private var filmHelper: FilmHelper? = null
    private var tvshowHelper: FilmHelper? = null
    private val MOVIE = 1
    private val MOVIE_ID = 2
    private val TV_SHOW = 3
    private val TV_SHOW_ID = 4
    private val sURIMatcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        sURIMatcher.addURI(AUTHORITY, TABLE_MOVIE, MOVIE)
        sURIMatcher.addURI(AUTHORITY, TABLE_MOVIE + "/#", MOVIE_ID)
        sURIMatcher.addURI(AUTHORITY, TABLE_TV_SHOW, TV_SHOW)
        sURIMatcher.addURI(AUTHORITY, TABLE_TV_SHOW + "/#", TV_SHOW_ID)
    }

    override fun onCreate(): Boolean {
        filmHelper = FilmHelper(context,true)
        tvshowHelper = FilmHelper(context,false)
        return true
    }
    override fun insert(uri: Uri, contentValues: ContentValues): Uri? {
        val uriType = sURIMatcher.match(uri)
        val id: Long?

        id = when (uriType) {
            MOVIE -> {
                filmHelper?.open()
                filmHelper?.insertProvider(contentValues)
            }
            TV_SHOW-> {
                tvshowHelper?.open()
                tvshowHelper?.insertProvider(contentValues)
            }
            else -> 0
        }

       return when (uriType) {
            MOVIE -> {
                context.contentResolver.notifyChange(uri, null)
                Uri.parse("$CONTENT_URI_MOVIE/$id")
            }
            TV_SHOW-> {
                context.contentResolver.notifyChange(uri, null)
                Uri.parse("$CONTENT_URI_TV_SHOW/$id")
            }
           else -> null
       }
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val uriType = sURIMatcher.match(uri)
        val cursor:Cursor?
        when (uriType) {
            MOVIE_ID -> {
                filmHelper?.open()
                cursor= filmHelper?.queryByIdProvider(uri.lastPathSegment)
            }
            MOVIE -> {
                filmHelper?.open()
                cursor= filmHelper?.queryProvider()
            }
            TV_SHOW_ID -> {
                tvshowHelper?.open()
                cursor= tvshowHelper?.queryByIdProvider(uri.lastPathSegment)
            }
            TV_SHOW -> {
                tvshowHelper?.open()
                cursor= tvshowHelper?.queryProvider()
            }
            else -> cursor=null
        }
        return cursor
    }

    override fun update(uri: Uri, contentValues: ContentValues, selection: String?, selectionArgs: Array<String>?): Int {
        val uriType = sURIMatcher.match(uri)
        val rowsUpdated: Int?
        rowsUpdated = when (uriType) {
            MOVIE_ID ->{
                filmHelper?.open()
                filmHelper?.updateProvider(uri.lastPathSegment,contentValues)
            }
            TV_SHOW_ID->{
                tvshowHelper?.open()
                tvshowHelper?.updateProvider(uri.lastPathSegment,contentValues)
            }
            else -> 0
        }
        when (uriType) {
            MOVIE_ID ->{
                context.contentResolver.notifyChange(CONTENT_URI_MOVIE, null)
            }
            TV_SHOW_ID->{
                context.contentResolver.notifyChange(CONTENT_URI_TV_SHOW, null)
            }
        }
        return rowsUpdated!!
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val uriType = sURIMatcher.match(uri)
        val rowsDeleted: Int?
        rowsDeleted = when (uriType) {
            MOVIE_ID ->{
                filmHelper?.open()
                filmHelper?.deleteProvider(uri.lastPathSegment)
            }
            TV_SHOW_ID->{
                tvshowHelper?.open()
                tvshowHelper?.deleteProvider(uri.lastPathSegment)
            }
            else -> 0
        }
        when (uriType) {
            MOVIE_ID ->{
                context.contentResolver.notifyChange(CONTENT_URI_MOVIE,null)
            }
            TV_SHOW_ID->{
                context.contentResolver.notifyChange(CONTENT_URI_TV_SHOW, null)
            }
        }
        return rowsDeleted!!
    }

    override fun getType(uri: Uri): String? {
        return null
    }
}