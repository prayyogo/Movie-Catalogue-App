package com.dicoding.prayogo.movieapp


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.*
import com.dicoding.prayogo.movieapp.adapter.AdapterMovieDb
import com.dicoding.prayogo.movieapp.api.ApiMovieDb
import com.dicoding.prayogo.movieapp.database.FilmHelper
import com.dicoding.prayogo.movieapp.model.Film
import com.dicoding.prayogo.movieapp.model.Genre
import com.dicoding.prayogo.movieapp.model.Genres
import com.dicoding.prayogo.movieapp.model.MovieDb
import com.dicoding.prayogo.movieapp.model.Result
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_movies.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.properties.Delegates

class MoviesFragment : Fragment() {
    companion object{
        var dataMovieFavorite=ArrayList<Film>()
    }
    private var adapterMovieDb by Delegates.notNull<AdapterMovieDb>()
    private var isLoading by Delegates.notNull<Boolean>()
    private var page by Delegates.notNull<Int>()
    private var totalPage by Delegates.notNull<Int>()
    private var listGenreMovieDb by Delegates.notNull<ArrayList<Genre>>()
    private var resultTheMovieDb by Delegates.notNull<ArrayList<Result>>()
    private var movieDb by Delegates.notNull<MovieDb>()
    private var searchView by Delegates.notNull<SearchView>()


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        page = 1
        totalPage = 0
        if(savedInstanceState!=null){
            page=savedInstanceState.getInt("Page")
            totalPage=savedInstanceState.getInt("TotalPage")
            resultTheMovieDb=savedInstanceState.getParcelableArrayList("Result")
            listGenreMovieDb=savedInstanceState.getParcelableArrayList("Genre")
            hideLoading()
            adapterMovieDb = AdapterMovieDb(
                this.activity!!,
                resultTheMovieDb,
                listGenreMovieDb,
                dataMovieFavorite
            )
            if(page==1){
                rv_movie.layoutManager = LinearLayoutManager(context)
                rv_movie.adapter = adapterMovieDb
            }else{
                rv_movie.layoutManager = LinearLayoutManager(context)
                rv_movie.adapter = adapterMovieDb
                adapterMovieDb.refreshAdapter(resultTheMovieDb)
            }
        }else{
            doLoadDatabase()
            doLoadData()
        }
        initListener()
        //retry button
        btn_retry_movie.setOnClickListener {
            btn_retry_movie.visibility=View.GONE
            doLoadDatabase()
            doLoadData()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movies, container, false)
    }

    @Override
    override fun onSaveInstanceState(outState:Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("Result",resultTheMovieDb)
        outState.putParcelableArrayList("Genre",listGenreMovieDb)
        outState.putInt("TotalPage",totalPage)
        outState.putInt("Page",page)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.main_menu, menu)
        searchView = menu.findItem(R.id.action_search_menu).actionView as SearchView
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.queryHint=resources.getString( R.string.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return if(query.trim().isEmpty()){
                    doLoadDatabase()
                    doLoadData()
                    false
                } else{
                    MainActivity.SEARCH_FILM=query
                    doSearchData()
                    true
                }
            }
            override fun onQueryTextChange(query: String): Boolean {
                 if(query.trim().isEmpty()){
                    doLoadDatabase()
                     doLoadData()
                }
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (when(item.itemId) {
            R.id.action_search_menu -> {
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        })
    }

    private  fun  doLoadDatabase(){
        dataMovieFavorite.clear()
        val movieHelper= FilmHelper(this.context!!,true)
        movieHelper.open()
        if (movieHelper.query().isNotEmpty()){
            dataMovieFavorite.addAll(movieHelper.query())
        }
        movieHelper.close()
    }
    @SuppressLint("CheckResult")
    private fun doLoadData() {
        showLoading(true)
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        val apiMovieDb = retrofit.create(ApiMovieDb::class.java)
        apiMovieDb.getGenres()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { genreMovieDB: Genres ->
                    listGenreMovieDb = genreMovieDB.genres as ArrayList
                },
                { t: Throwable ->
                    t.printStackTrace()
                    btn_retry_movie.visibility=View.VISIBLE
                },
                {
                    hideLoading()
                }
            )
        apiMovieDb.getNowPlayingMovie(page = page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { theMovieDb: MovieDb ->
                    resultTheMovieDb = theMovieDb.results as ArrayList
                    movieDb=theMovieDb
                    if (page == 1) {
                        adapterMovieDb = AdapterMovieDb(
                            this.activity!!,
                            resultTheMovieDb,
                            listGenreMovieDb,
                            dataMovieFavorite
                        )
                        rv_movie.layoutManager = LinearLayoutManager(activity)
                        rv_movie.adapter = adapterMovieDb
                    } else {
                        adapterMovieDb.refreshAdapter(resultTheMovieDb)
                    }
                    totalPage = movieDb.totalPages
                },
                { t: Throwable ->
                    t.printStackTrace()
                    btn_retry_movie.visibility=View.VISIBLE
                },
                {
                    hideLoading()
                }
            )
    }

    @SuppressLint("CheckResult")
    private fun doSearchData() {
        showLoading(true)
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        val apiMovieDb = retrofit.create(ApiMovieDb::class.java)
        apiMovieDb.getGenres()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { genreMovieDB: Genres ->
                    listGenreMovieDb = genreMovieDB.genres as ArrayList
                },
                { t: Throwable ->
                    t.printStackTrace()
                    btn_retry_movie.visibility=View.VISIBLE
                },
                {
                    hideLoading()
                }
            )
        apiMovieDb.getSearchMovie(page=page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { theMovieDb: MovieDb ->
                    resultTheMovieDb = theMovieDb.results as ArrayList
                    movieDb=theMovieDb
                    adapterMovieDb = AdapterMovieDb(
                        this.activity!!,
                        resultTheMovieDb,
                        listGenreMovieDb,
                        dataMovieFavorite
                    )
                    rv_movie.layoutManager = LinearLayoutManager(activity)
                    rv_movie.adapter = adapterMovieDb
                    //adapterMovieDb.refreshAdapter(resultTheMovieDb)
                    totalPage = movieDb.totalPages
                },
                { t: Throwable ->
                    t.printStackTrace()
                    btn_retry_movie.visibility=View.VISIBLE
                },
                {
                    hideLoading()
                }
            )
    }
    private fun initListener() {
        rv_movie.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val countItem = linearLayoutManager.itemCount
                val lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition()
                val isLastPosition = countItem.minus(1) == lastVisiblePosition
                if (!isLoading && isLastPosition && page < totalPage) {
                    showLoading(true)
                    page = page.plus(1)
                    doLoadData()
                }
            }
        })
    }

    private fun showLoading(isRefresh: Boolean) {
        isLoading = true
        btn_retry_movie.visibility=View.GONE
        progress_bar_movie.visibility = View.VISIBLE
        rv_movie.visibility.let {
            if (isRefresh) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun hideLoading() {
        isLoading = false
        progress_bar_movie.visibility = View.GONE
        rv_movie.visibility = View.VISIBLE
    }
}
