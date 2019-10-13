package com.tiagohs.cinema_history.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.tiagohs.cinema_history.models.main_topics.MilMoviesMainTopic
import com.tiagohs.cinema_history.models.tmdb.Movie
import com.tiagohs.cinema_history.ui.fragments.MoviePresentationItemFragment


class MoviesPagerAdapter(
    val fm: FragmentManager,
    val mainTopic: MilMoviesMainTopic,
    val listOfMovies: List<Movie>
): FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    val listOfFragments: HashMap<Int, MoviePresentationItemFragment> = HashMap()

    private var currentFragment: MoviePresentationItemFragment? = null
    private var previousFragment: MoviePresentationItemFragment? = null

    override fun getItem(position: Int): Fragment {
        val movie = listOfMovies[position]
        val fragment: MoviePresentationItemFragment

        if (position == 0 && listOfFragments[position] == null) {
            fragment =  MoviePresentationItemFragment.newInstance(movie, mainTopic, position,true)
            currentFragment = fragment
        } else {
            fragment = MoviePresentationItemFragment.newInstance(movie, mainTopic, position)
        }

        listOfFragments[position] = fragment

        return fragment
    }

    fun onPageSelected(position: Int) {
        previousFragment = currentFragment
        currentFragment = listOfFragments[position]

        currentFragment?.scaleUpImage()
        previousFragment?.scaleDownImage()
    }


    override fun getCount(): Int = listOfMovies.size

    override fun getPageWidth(position: Int): Float {
        return 0.8f
    }
}