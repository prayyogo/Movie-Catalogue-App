package com.dicoding.prayogo.filmfavorite

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import com.dicoding.prayogo.filmfavorite.adapter.PageFilmAdapter

class MainActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewpager_main.adapter = PageFilmAdapter(supportFragmentManager,this)

        supportActionBar?.elevation= 0F
        tabs_main.setupWithViewPager(viewpager_main)
        tabs_main.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        supportActionBar?.title = resources.getString(R.string.movies_favorite)
                    }
                    1 -> {
                        supportActionBar?.title = resources.getString(R.string.tv_shows_favorite)
                    }
                }
            }
        })

        supportActionBar?.title = resources.getString(R.string.movies_favorite)
    }

}
