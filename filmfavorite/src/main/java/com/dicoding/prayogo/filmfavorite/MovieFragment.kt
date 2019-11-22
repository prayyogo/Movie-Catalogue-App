package com.dicoding.prayogo.filmfavorite

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dicoding.prayogo.filmfavorite.adapter.FilmAdapter
import com.dicoding.prayogo.filmfavorite.database.DatabaseContract.FilmColumns.Companion.CONTENT_URI_MOVIE
import com.dicoding.prayogo.filmfavorite.helper.MappingHelper
import com.dicoding.prayogo.filmfavorite.model.Film
import kotlinx.android.synthetic.main.fragment_movie.*
import kotlin.properties.Delegates

class MovieFragment : Fragment() {
    private var isLoading by Delegates.notNull<Boolean>()
    private var adapterMovie by Delegates.notNull<FilmAdapter>()
    private var dataMovie by Delegates.notNull<ArrayList<Film>>()
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(savedInstanceState!=null){
            hideLoading()
            dataMovie = savedInstanceState.getParcelableArrayList("Movie")
            if(dataMovie.size!=0){
               setupAdapter()
            }else{
                tv_no_data_movie.visibility=View.VISIBLE
            }
        }else {
            loadDatabase()
        }
        initListener()
    }

    @Override
    override fun onSaveInstanceState(outState:Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("Movie",dataMovie)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie, container, false)
    }


    private fun initListener() {
        rv_movie_favorite.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
            }
        })
    }

    private fun loadDatabase(){
        showLoading(true)
        tv_no_data_movie.visibility=View.GONE
        Thread(Runnable {
            // performing some dummy time taking operation
            var i=0
            while(i<Int.MAX_VALUE){
                i++
            }
            // try to touch View of UI thread
            activity?.runOnUiThread {
                doLoadDatabase()
            }
        }).start()
    }
    private fun doLoadDatabase(){
        val cursor = context?.contentResolver?.query(CONTENT_URI_MOVIE, null, null, null, null)
        if (cursor != null) {
            if (cursor.count != 0) {
                dataMovie=MappingHelper.mapCursorToArrayList(cursor)
                setupAdapter()
            }else{
                tv_no_data_movie.visibility=View.VISIBLE
            }
        }
        hideLoading()
    }
    private fun setupAdapter(){
        adapterMovie = FilmAdapter(this.activity!!,
            dataMovie
        )
        rv_movie_favorite.layoutManager = LinearLayoutManager(activity)
        rv_movie_favorite.adapter= adapterMovie
        tv_no_data_movie.visibility=View.GONE
    }
    private fun showLoading(isRefresh: Boolean) {
        isLoading = true
        progress_bar_movie_favorite.visibility = View.VISIBLE
        rv_movie_favorite.visibility.let {
            if (isRefresh) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun hideLoading() {
        isLoading = false
        progress_bar_movie_favorite.visibility = View.GONE
        rv_movie_favorite.visibility = View.VISIBLE
    }
}
