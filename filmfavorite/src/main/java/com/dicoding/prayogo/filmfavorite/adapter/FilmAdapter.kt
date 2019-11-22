package com.dicoding.prayogo.filmfavorite.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.dicoding.prayogo.filmfavorite.BuildConfig
import com.dicoding.prayogo.filmfavorite.DetailFilmActivity
import com.dicoding.prayogo.filmfavorite.R
import com.dicoding.prayogo.filmfavorite.model.Film
import kotlinx.android.synthetic.main.item_film_favorite.view.*
import java.util.ArrayList


class FilmAdapter(private val context: Context, private var databaseMovie: ArrayList<Film>): RecyclerView.Adapter<FilmAdapter.FilmViewHolder>() {
    private var film=Film()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder =
        FilmViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_film_favorite, parent, false)
        )

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val resultItem = databaseMovie[position]
        Glide
            .with(context)
            .load(BuildConfig.BASE_URL_IMAGE + resultItem.photo)
            .into(holder.itemView.img_photo)
        holder
            .itemView
            .tv_name
            ?.text = resultItem.name
        holder
            .itemView
            .tv_description
            ?.text = resultItem.description

        // show detail film
        holder.itemView.setOnClickListener {
            film = Film(0,
                resultItem.photo,
                holder.itemView.tv_name.text.toString(),
                resultItem.description,
                resultItem.releaseDate,
                resultItem.rating,
                resultItem.genre,
                resultItem.popularity
            )
            showDetailFilm(film)
        }

    }

    override fun getItemCount(): Int = databaseMovie.size

    private fun showDetailFilm(film: Film) {
        val dataFilm= Film(0,film.photo,film.name,film.description,film.releaseDate,film.rating,film.genre,film.popularity)
        val moveObjectIntent= Intent(context, DetailFilmActivity::class.java)
        moveObjectIntent.putExtra(DetailFilmActivity.EXTRA_FILM,dataFilm)
        context.startActivity(moveObjectIntent)
    }
    inner class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}